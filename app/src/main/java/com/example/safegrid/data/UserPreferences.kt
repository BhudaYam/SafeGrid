package com.example.safegrid.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.safeGridDataStore by preferencesDataStore(name = "safegrid_settings")

data class StoredSettings(
    val lang: String = "en",
    val notifications: Boolean = true,
    val radiusKm: Int = 5,
    val area: String = "Johannesburg",
    val name: String = "",
    val email: String = "",
    val points: Int = 0,
    val lastStage: Int = -1,
    val stageKnown: Boolean = false,
    val lastRaw: String = "",
    val lastSync: Long = 0L,
    val lastCloud: String = ""
)

class UserPreferences(private val context: Context) {
    private val langKey = stringPreferencesKey("lang")
    private val notifKey = booleanPreferencesKey("notif")
    private val radiusKey = intPreferencesKey("radius")
    private val areaKey = stringPreferencesKey("area")
    private val nameKey = stringPreferencesKey("name")
    private val emailKey = stringPreferencesKey("email")
    private val pointsKey = intPreferencesKey("points")
    private val lastStageKey = intPreferencesKey("last_stage")
    private val stageKnownKey = booleanPreferencesKey("stage_known")
    private val lastRawKey = stringPreferencesKey("last_raw")
    private val lastSyncKey = longPreferencesKey("last_sync")
    private val lastCloudKey = stringPreferencesKey("last_cloud")

    val settings: Flow<StoredSettings> = context.safeGridDataStore.data.map { prefs ->
        prefs.toSettings()
    }

    suspend fun update(transform: (StoredSettings) -> StoredSettings) {
        context.safeGridDataStore.edit { prefs ->
            val updated = transform(prefs.toSettings())
            prefs[langKey] = updated.lang
            prefs[notifKey] = updated.notifications
            prefs[radiusKey] = updated.radiusKm
            prefs[areaKey] = updated.area
            prefs[nameKey] = updated.name
            prefs[emailKey] = updated.email
            prefs[pointsKey] = updated.points
            prefs[lastStageKey] = updated.lastStage
            prefs[stageKnownKey] = updated.stageKnown
            prefs[lastRawKey] = updated.lastRaw
            prefs[lastSyncKey] = updated.lastSync
            prefs[lastCloudKey] = updated.lastCloud
        }
    }

    private fun Preferences.toSettings(): StoredSettings {
        return StoredSettings(
            lang = this[langKey] ?: "en",
            notifications = this[notifKey] ?: true,
            radiusKm = this[radiusKey] ?: 5,
            area = this[areaKey] ?: "Johannesburg",
            name = this[nameKey] ?: "",
            email = this[emailKey] ?: "",
            points = this[pointsKey] ?: 0,
            lastStage = this[lastStageKey] ?: -1,
            stageKnown = this[stageKnownKey] ?: false,
            lastRaw = this[lastRawKey] ?: "",
            lastSync = this[lastSyncKey] ?: 0L,
            lastCloud = this[lastCloudKey] ?: ""
        )
    }
}
