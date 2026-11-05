package com.example.safegrid.data

import android.content.Context
import android.net.Uri
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File

data class CloudSession(
    val uid: String,
    val email: String,
    val name: String,
    val provider: String
)

sealed class CloudAuthResult {
    data class Success(val session: CloudSession) : CloudAuthResult()
    data class Failure(val code: String) : CloudAuthResult()
}

class CloudStore(private val context: Context) {

    fun isReady(): Boolean {
        return try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (error: Exception) {
            false
        }
    }

    fun currentSession(): CloudSession? {
        if (!isReady()) return null
        val user = FirebaseAuth.getInstance().currentUser ?: return null
        val provider = user.providerData.lastOrNull()?.providerId ?: "password"
        return CloudSession(
            uid = user.uid,
            email = user.email.orEmpty(),
            name = user.displayName.orEmpty(),
            provider = provider
        )
    }

    suspend fun register(email: String, passwordHash: String, name: String): CloudAuthResult {
        if (!isReady()) return CloudAuthResult.Failure("NOT_CONFIGURED")
        return try {
            val result = FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, passwordHash)
                .await()
            val user = result.user ?: return CloudAuthResult.Failure("ERROR_GENERIC")
            user.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            ).await()
            CloudAuthResult.Success(
                CloudSession(user.uid, email, name, "password")
            )
        } catch (error: Exception) {
            CloudAuthResult.Failure(errorCode(error))
        }
    }

    suspend fun signIn(email: String, passwordHash: String): CloudAuthResult {
        if (!isReady()) return CloudAuthResult.Failure("NOT_CONFIGURED")
        return try {
            val result = FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, passwordHash)
                .await()
            val user = result.user ?: return CloudAuthResult.Failure("ERROR_GENERIC")
            CloudAuthResult.Success(
                CloudSession(
                    uid = user.uid,
                    email = user.email.orEmpty(),
                    name = user.displayName.orEmpty(),
                    provider = "password"
                )
            )
        } catch (error: Exception) {
            CloudAuthResult.Failure(errorCode(error))
        }
    }

    suspend fun signInWithGoogle(idToken: String): CloudAuthResult {
        if (!isReady()) return CloudAuthResult.Failure("NOT_CONFIGURED")
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = FirebaseAuth.getInstance().signInWithCredential(credential).await()
            val user = result.user ?: return CloudAuthResult.Failure("ERROR_GENERIC")
            CloudAuthResult.Success(
                CloudSession(
                    uid = user.uid,
                    email = user.email.orEmpty(),
                    name = user.displayName.orEmpty(),
                    provider = "google.com"
                )
            )
        } catch (error: Exception) {
            CloudAuthResult.Failure(errorCode(error))
        }
    }

    fun signOut() {
        if (!isReady()) return
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun saveProfile(
        uid: String,
        name: String,
        email: String,
        language: String,
        radiusKm: Int,
        area: String,
        points: Int
    ) {
        if (!isReady()) return
        val data = mapOf(
            "name" to name,
            "email" to email,
            "language" to language,
            "radiusKm" to radiusKm,
            "area" to area,
            "points" to points,
            "updatedAt" to System.currentTimeMillis()
        )
        FirebaseFirestore.getInstance().collection("users").document(uid).set(data).await()
    }

    suspend fun saveIncident(uid: String, incident: IncidentEntity): String? {
        if (!isReady()) return null
        val photoUrl = uploadPhoto(uid, incident.id, incident.photoLocal) ?: incident.photoCloudUrl
        val data = mapOf(
            "userId" to uid,
            "category" to incident.category,
            "description" to incident.description,
            "area" to incident.area,
            "reporter" to incident.reporter,
            "createdAt" to incident.createdAt,
            "photoUrl" to photoUrl
        )
        FirebaseFirestore.getInstance().collection("incidents").document(incident.id).set(data).await()
        return photoUrl
    }

    suspend fun fetchIncidents(): List<IncidentEntity> {
        if (!isReady()) return emptyList()
        val snap = FirebaseFirestore.getInstance().collection("incidents")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .await()
        return snap.documents.mapNotNull { doc ->
            val created = doc.getLong("createdAt") ?: return@mapNotNull null
            IncidentEntity(
                id = doc.id,
                category = doc.getString("category") ?: "Crime",
                description = doc.getString("description") ?: "",
                area = doc.getString("area") ?: "",
                reporter = doc.getString("reporter") ?: "",
                createdAt = created,
                photoLocal = null,
                photoCloudUrl = doc.getString("photoUrl"),
                pendingSync = false
            )
        }
    }

    suspend fun saveSos(uid: String, name: String, event: SosEntity) {
        if (!isReady()) return
        val data = mapOf(
            "userId" to uid,
            "name" to name,
            "area" to event.area,
            "location" to event.location,
            "createdAt" to event.createdAt,
            "active" to true
        )
        FirebaseFirestore.getInstance().collection("alerts").document(event.id).set(data).await()
    }

    fun subscribeToAlerts() {
        if (!isReady()) return
        FirebaseMessaging.getInstance().subscribeToTopic("safegrid_alerts")
    }

    private suspend fun uploadPhoto(uid: String, id: String, localPath: String?): String? {
        if (localPath.isNullOrBlank()) return null
        val file = File(localPath)
        if (!file.exists()) return null
        val ref = FirebaseStorage.getInstance().reference.child("incidents/$uid/$id.jpg")
        file.inputStream().use { stream ->
            ref.putStream(stream).await()
        }
        return ref.downloadUrl.await().toString()
    }

    private fun errorCode(error: Exception): String {
        val auth = error as? FirebaseAuthException
        return auth?.errorCode ?: "ERROR_GENERIC"
    }

    companion object {
        fun webClientId(context: Context): String? {
            val id = context.resources.getIdentifier(
                "default_web_client_id",
                "string",
                context.packageName
            )
            if (id == 0) return null
            return context.getString(id)
        }

        fun copyPhoto(context: Context, uri: Uri, id: String): String? {
            return try {
                val directory = File(context.filesDir, "photos").apply { mkdirs() }
                val destination = File(directory, "$id.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    destination.outputStream().use { output -> input.copyTo(output) }
                } ?: return null
                destination.absolutePath
            } catch (error: Exception) {
                null
            }
        }
    }
}
