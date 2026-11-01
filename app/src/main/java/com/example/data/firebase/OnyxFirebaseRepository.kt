package com.example.data.firebase

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.example.BuildConfig
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.Timestamp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType {
    CREATE, UPDATE, DELETE, LIST, GET, WRITE
}

data class CloudMusicItem(
    val id: String = "",
    val userId: String = "",
    val songId: String = "",
    val title: String = "",
    val artist: String = "",
    val category: String = "favorite",
    val createdAt: Timestamp = Timestamp.now()
)

private const val TAG = "AppCheckInit"
private const val DEBUG_TOKEN_EXTRA = "FIREBASE_APPCHECK_DEBUG_TOKEN"
private const val DEBUG_STORE_PREFIX = "com.google.firebase.appcheck.debug.store."
private const val DEBUG_SECRET_KEY = "com.google.firebase.appcheck.debug.DEBUG_SECRET"
private const val RECAPTCHA_SITE_KEY_RES = "recaptcha_site_key"

fun configureAppCheck(context: Context, intent: Intent?) {
    try {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        if (BuildConfig.DEBUG) {
            val debugToken = intent?.getStringExtra(DEBUG_TOKEN_EXTRA)
            if (!debugToken.isNullOrEmpty()) {
                val persistenceKey = FirebaseApp.getInstance().persistenceKey
                val prefs = context.getSharedPreferences(
                    "$DEBUG_STORE_PREFIX$persistenceKey",
                    Context.MODE_PRIVATE
                )
                prefs.edit()
                    .putString(DEBUG_SECRET_KEY, debugToken)
                    .commit()
                Log.d(TAG, "App Check debug secret configured successfully.")
            } else {
                Log.w(TAG, "No debug token found in Intent extra.")
            }

            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
            Log.d(TAG, "DebugAppCheckProviderFactory installed.")
        } else {
            val resId = context.resources.getIdentifier(RECAPTCHA_SITE_KEY_RES, "string", context.packageName)
            if (resId != 0) {
                val siteKey = context.getString(resId)
                if (siteKey.isNotBlank()) {
                    FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                        RecaptchaAppCheckProviderFactory.getInstance(siteKey)
                    )
                    Log.d(TAG, "RecaptchaAppCheckProviderFactory installed.")
                } else {
                    Log.e(TAG, "recaptcha_site_key resource string is blank.")
                }
            } else {
                Log.e(TAG, "recaptcha_site_key string resource not found in package.")
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Firebase App Check", e)
    }
}

fun handleFirestoreError(
    error: Exception,
    operationType: OperationType,
    path: String?,
    auth: FirebaseAuth = Firebase.auth
) {
    val currentUser = auth.currentUser
    val providerInfo = JSONArray().apply {
        currentUser?.providerData?.forEach { profile ->
            put(JSONObject().apply {
                put("providerId", profile.providerId)
                put("displayName", profile.displayName)
                put("email", profile.email)
                put("photoUrl", profile.photoUrl?.toString())
            })
        }
    }

    val authObj = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("isAnonymous", currentUser?.isAnonymous)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", providerInfo)
    }

    val errInfo = JSONObject().apply {
        put("error", error.message ?: error.toString())
        if (error is FirebaseFirestoreException) {
            put("code", error.code.name)
        }
        put("operationType", operationType.name.lowercase())
        put("path", path)
        put("authInfo", authObj)
    }.toString()

    Log.e("FirestoreError", errInfo, error)
    throw RuntimeException(errInfo, error)
}

class OnyxFirebaseRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    companion object {
        @Volatile
        private var instance: OnyxFirebaseRepository? = null

        fun getInstance(context: Context): OnyxFirebaseRepository {
            return instance ?: synchronized(this) {
                instance ?: run {
                    if (FirebaseApp.getApps(context).isEmpty()) {
                        FirebaseApp.initializeApp(context)
                    }
                    val databaseId = context.getString(R.string.firestore_database_id)
                    val firestore = FirebaseFirestore.getInstance(databaseId)
                    val firebaseAuth = Firebase.auth
                    OnyxFirebaseRepository(firestore, firebaseAuth).also { instance = it }
                }
            }
        }
    }

    val currentUser: FirebaseUser?
        get() = try { auth.currentUser } catch (_: Throwable) { null }

    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser)
        }
        try {
            auth.addAuthStateListener(listener)
        } catch (e: Throwable) {
            trySend(null)
        }
        awaitClose {
            try {
                auth.removeAuthStateListener(listener)
            } catch (_: Throwable) {}
        }
    }

    suspend fun signInWithGoogle(activityContext: Activity): Result<FirebaseUser> {
        return try {
            val credentialManager = CredentialManager.create(activityContext)
            val webClientId = activityContext.getString(R.string.default_web_client_id)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = try {
                credentialManager.getCredential(
                    request = request,
                    context = activityContext
                )
            } catch (e: NoCredentialException) {
                val signInWithGoogleOption = GetSignInWithGoogleOption.Builder(webClientId)
                    .build()
                val fallbackRequest = GetCredentialRequest.Builder()
                    .addCredentialOption(signInWithGoogleOption)
                    .build()
                credentialManager.getCredential(
                    request = fallbackRequest,
                    context = activityContext
                )
            }

            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                val user = authResult.user
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(IllegalStateException("Google Sign-In succeeded but Firebase user was null"))
                }
            } else {
                Result.failure(IllegalStateException("Unexpected credential type: ${credential.type}"))
            }
        } catch (e: GetCredentialCancellationException) {
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("OnyxFirebaseAuth", "Google Sign-In failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun signOut(context: Context) {
        try {
            auth.signOut()
            val credentialManager = CredentialManager.create(context)
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (e: Throwable) {
            Log.w("OnyxFirebaseAuth", "Sign out warning: ${e.message}")
        }
    }

    fun observeCloudItems(userId: String): Flow<List<CloudMusicItem>> = callbackFlow {
        val path = "users/$userId/cloud_items"
        var registration: ListenerRegistration? = null
        try {
            registration = db.collection("users")
                .document(userId)
                .collection("cloud_items")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w("OnyxFirestore", "Snapshot listener warning on $path: ${error.message}")
                        return@addSnapshotListener
                    }
                    val items = snapshot?.documents?.mapNotNull { doc ->
                        val uid = doc.getString("userId") ?: return@mapNotNull null
                        val songId = doc.getString("songId") ?: return@mapNotNull null
                        val title = doc.getString("title") ?: return@mapNotNull null
                        val artist = doc.getString("artist") ?: return@mapNotNull null
                        val category = doc.getString("category") ?: "favorite"
                        val createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now()
                        CloudMusicItem(
                            id = doc.id,
                            userId = uid,
                            songId = songId,
                            title = title,
                            artist = artist,
                            category = category,
                            createdAt = createdAt
                        )
                    }.orEmpty()
                    trySend(items)
                }
        } catch (e: Throwable) {
            Log.w("OnyxFirestore", "Could not attach listener on $path: ${e.message}")
        }
        awaitClose { registration?.remove() }
    }

    suspend fun createCloudItem(
        userId: String,
        itemId: String,
        songId: String,
        title: String,
        artist: String,
        category: String = "favorite"
    ) {
        val path = "users/$userId/cloud_items/$itemId"
        val payload = mapOf(
            "userId" to userId,
            "songId" to songId,
            "title" to title.take(200),
            "artist" to artist.take(200),
            "category" to if (category in listOf("favorite", "mood", "ai_playlist")) category else "favorite",
            "createdAt" to Timestamp.now()
        )
        try {
            db.collection("users")
                .document(userId)
                .collection("cloud_items")
                .document(itemId)
                .set(payload)
                .await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path, auth)
        }
    }

    suspend fun updateCloudItemTitle(
        userId: String,
        itemId: String,
        newTitle: String
    ) {
        val path = "users/$userId/cloud_items/$itemId"
        val payload = mapOf("title" to newTitle.take(200))
        try {
            db.collection("users")
                .document(userId)
                .collection("cloud_items")
                .document(itemId)
                .set(payload, SetOptions.merge())
                .await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path, auth)
        }
    }

    suspend fun getCloudItem(userId: String, itemId: String): CloudMusicItem? {
        val path = "users/$userId/cloud_items/$itemId"
        return try {
            val doc = db.collection("users")
                .document(userId)
                .collection("cloud_items")
                .document(itemId)
                .get()
                .await()
            if (!doc.exists()) return null
            CloudMusicItem(
                id = doc.id,
                userId = doc.getString("userId") ?: userId,
                songId = doc.getString("songId") ?: "",
                title = doc.getString("title") ?: "",
                artist = doc.getString("artist") ?: "",
                category = doc.getString("category") ?: "favorite",
                createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now()
            )
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, path, auth)
            null
        }
    }

    suspend fun deleteCloudItem(userId: String, itemId: String) {
        val path = "users/$userId/cloud_items/$itemId"
        try {
            db.collection("users")
                .document(userId)
                .collection("cloud_items")
                .document(itemId)
                .delete()
                .await()
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, path, auth)
        }
    }
}
