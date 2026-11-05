package com.example.safegrid.data

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.safegrid.core.IncidentMerger
import com.example.safegrid.core.MergeIncident
import com.example.safegrid.core.ScheduleBuilder
import com.example.safegrid.sync.SyncWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

data class PowerSnapshot(
    val stage: Int?,
    val raw: String,
    val detail: String,
    val fromCache: Boolean,
    val syncedAt: Long,
    val slots: List<ScheduleEntity>,
    val stageChanged: Boolean
)

class SafeGridRepository(private val context: Context) {
    private val database = SafeGridDatabase.get(context)
    private val preferences = UserPreferences(context)
    private val eskom = EskomRestClient()
    private val cloud = CloudStore(context)

    val settings = preferences.settings
    fun observeIncidents(): Flow<List<IncidentEntity>> = database.incidents().observe()

    fun cloudReady(): Boolean = cloud.isReady()

    fun currentSession(): CloudSession? = cloud.currentSession()

    suspend fun register(email: String, passwordHash: String, name: String): CloudAuthResult {
        val result = cloud.register(email, passwordHash, name)
        if (result is CloudAuthResult.Success) {
            rememberUser(result.session)
        }
        return result
    }

    suspend fun signIn(email: String, passwordHash: String): CloudAuthResult {
        val result = cloud.signIn(email, passwordHash)
        if (result is CloudAuthResult.Success) {
            val session = result.session
            val named = if (session.name.isBlank()) {
                session.copy(name = preferences.settings.first().name)
            } else {
                session
            }
            rememberUser(named)
            return CloudAuthResult.Success(named)
        }
        return result
    }

    suspend fun signInWithGoogle(idToken: String): CloudAuthResult {
        val result = cloud.signInWithGoogle(idToken)
        if (result is CloudAuthResult.Success) {
            rememberUser(result.session)
        }
        return result
    }

    suspend fun signOut() {
        cloud.signOut()
        preferences.update { it.copy(name = "", email = "") }
    }

    suspend fun updateSettings(transform: (StoredSettings) -> StoredSettings) {
        preferences.update(transform)
        val settings = preferences.settings.first()
        val session = cloud.currentSession() ?: return
        try {
            cloud.saveProfile(
                uid = session.uid,
                name = settings.name.ifBlank { session.name },
                email = settings.email.ifBlank { session.email },
                language = settings.lang,
                radiusKm = settings.radiusKm,
                area = settings.area,
                points = settings.points
            )
            preferences.update { it.copy(lastCloud = "users/${session.uid}") }
        } catch (error: Exception) {
            preferences.update { it.copy(lastCloud = "queued profile") }
        }
    }

    suspend fun refreshLoadshedding(): PowerSnapshot {
        val settings = preferences.settings.first()
        val result = eskom.fetchStatus()
        val now = System.currentTimeMillis()
        if (result is EskomResult.Success) {
            val slots = ScheduleBuilder.build(result.stage).mapIndexed { index, slot ->
                ScheduleEntity(
                    id = index.toString(),
                    dayKey = slot.dayKey,
                    time = slot.time,
                    stage = result.stage,
                    area = settings.area,
                    syncedAt = now
                )
            }
            database.schedule().clear()
            database.schedule().insertAll(slots)
            val changed = settings.stageKnown && settings.lastStage != result.stage
            preferences.update {
                it.copy(
                    lastStage = result.stage,
                    stageKnown = true,
                    lastRaw = result.raw,
                    lastSync = now
                )
            }
            return PowerSnapshot(
                stage = result.stage,
                raw = result.raw,
                detail = "",
                fromCache = false,
                syncedAt = now,
                slots = slots,
                stageChanged = changed
            )
        }
        val cached = database.schedule().all()
        return PowerSnapshot(
            stage = settings.lastStage.takeIf { settings.stageKnown },
            raw = settings.lastRaw,
            detail = (result as EskomResult.Failure).detail,
            fromCache = true,
            syncedAt = settings.lastSync,
            slots = cached,
            stageChanged = false
        )
    }

    suspend fun cachedPower(): PowerSnapshot {
        val settings = preferences.settings.first()
        return PowerSnapshot(
            stage = settings.lastStage.takeIf { settings.stageKnown },
            raw = settings.lastRaw,
            detail = "",
            fromCache = true,
            syncedAt = settings.lastSync,
            slots = database.schedule().all(),
            stageChanged = false
        )
    }

    suspend fun submitIncident(
        category: String,
        description: String,
        photoLocal: String?
    ): IncidentEntity {
        val settings = preferences.settings.first()
        val session = cloud.currentSession()
        val incident = IncidentEntity(
            id = UUID.randomUUID().toString(),
            category = category,
            description = description.trim(),
            area = currentLocationLabel(settings.area),
            reporter = settings.name.ifBlank { session?.name ?: "SafeGrid user" },
            createdAt = System.currentTimeMillis(),
            photoLocal = photoLocal,
            photoCloudUrl = null,
            pendingSync = true
        )
        database.incidents().upsert(incident)
        preferences.update { it.copy(points = it.points + 10) }
        enqueueSync()
        return incident
    }

    suspend fun submitSos(): SosEntity {
        val settings = preferences.settings.first()
        val event = SosEntity(
            id = UUID.randomUUID().toString(),
            area = settings.area,
            location = currentLocationLabel(settings.area),
            createdAt = System.currentTimeMillis(),
            pendingSync = true
        )
        database.sos().upsert(event)
        preferences.update { it.copy(points = it.points + 5) }
        enqueueSync()
        return event
    }

    suspend fun syncPending(): String {
        if (!cloud.isReady()) return "device-only"
        val session = cloud.currentSession() ?: return "signed-out"
        val settings = preferences.settings.first()
        var wrote = false
        for (incident in database.incidents().pending()) {
            val photo = cloud.saveIncident(session.uid, incident)
            database.incidents().markSynced(incident.id, photo)
            wrote = true
        }
        for (event in database.sos().pending()) {
            cloud.saveSos(session.uid, settings.name, event)
            database.sos().markSynced(event.id)
            wrote = true
        }
        val remote = cloud.fetchIncidents()
        val local = database.incidents().pending()
        val merged = IncidentMerger.merge(
            local = local.map { it.toMerge() },
            remote = remote.map { it.toMerge() }
        )
        val remoteById = remote.associateBy { it.id }
        val localById = local.associateBy { it.id }
        for (item in merged) {
            val entity = when {
                item.pendingSync -> localById[item.id]
                else -> remoteById[item.id]
            } ?: continue
            val existing = database.incidents().get(entity.id)
            if (existing?.pendingSync == true) continue
            database.incidents().upsert(entity.copy(pendingSync = false))
        }
        try {
            cloud.saveProfile(
                uid = session.uid,
                name = settings.name,
                email = settings.email.ifBlank { session.email },
                language = settings.lang,
                radiusKm = settings.radiusKm,
                area = settings.area,
                points = settings.points
            )
        } catch (error: Exception) {
            // Profile sync can retry on the next connection.
        }
        if (settings.notifications) {
            cloud.subscribeToAlerts()
        }
        val message = if (wrote) "incidents + alerts" else "firestore read"
        preferences.update { it.copy(lastCloud = message) }
        return message
    }

    fun enqueueSync() {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "safegrid-sync",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    private suspend fun rememberUser(session: CloudSession) {
        preferences.update {
            it.copy(
                name = session.name.ifBlank { it.name },
                email = session.email.ifBlank { it.email }
            )
        }
        val settings = preferences.settings.first()
        try {
            cloud.saveProfile(
                uid = session.uid,
                name = settings.name.ifBlank { session.name },
                email = settings.email.ifBlank { session.email },
                language = settings.lang,
                radiusKm = settings.radiusKm,
                area = settings.area,
                points = settings.points
            )
            preferences.update { it.copy(lastCloud = "users/${session.uid}") }
        } catch (error: Exception) {
            preferences.update { it.copy(lastCloud = "profile queued") }
        }
    }

    @SuppressLint("MissingPermission")
    private fun currentLocationLabel(fallback: String): String {
        val fine = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!fine && !coarse) return fallback
        return try {
            val manager = context.getSystemService(LocationManager::class.java) ?: return fallback
            val location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                ?: manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location == null) {
                fallback
            } else {
                "%.5f, %.5f".format(java.util.Locale.US, location.latitude, location.longitude)
            }
        } catch (error: SecurityException) {
            fallback
        }
    }

    private fun IncidentEntity.toMerge(): MergeIncident {
        return MergeIncident(id, createdAt, pendingSync, description)
    }

    companion object {
        @Volatile
        private var instance: SafeGridRepository? = null

        fun get(context: Context): SafeGridRepository {
            return instance ?: synchronized(this) {
                instance ?: SafeGridRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
