package com.example.base

import android.os.Looper
import androidx.test.core.app.ApplicationProvider
import com.example.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import java.net.HttpURLConnection
import java.net.URL

@RunWith(RobolectricTestRunner::class)
abstract class FirestoreEmulatorTestBase {
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var projectId: String
    lateinit var databaseId: String

    protected fun runEmulatorTest(block: suspend CoroutineScope.() -> Unit) {
        val shadowMainLooper = Shadows.shadowOf(Looper.getMainLooper())
        val scope = CoroutineScope(Dispatchers.IO)
        val deferred = scope.async {
            withTimeout(30_000L) {
                block()
            }
        }
        while (!deferred.isCompleted) {
            shadowMainLooper.idle()
            Thread.sleep(10)
        }
        shadowMainLooper.idle()
        runBlocking { deferred.await() }
    }

    @Before
    open fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()

        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context)
        }

        projectId = FirebaseApp.getInstance().options.projectId
            ?: throw IllegalStateException("Project ID not found in google-services.json")

        databaseId = context.getString(R.string.firestore_database_id)

        db = FirebaseFirestore.getInstance(databaseId)
        auth = FirebaseAuth.getInstance()

        try {
            db.useEmulator("127.0.0.1", 8085)
            auth.useEmulator("127.0.0.1", 9099)

            val settings = FirebaseFirestoreSettings.Builder()
                .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
                .build()
            db.firestoreSettings = settings
        } catch (e: IllegalStateException) {
            // Already configured in this process
        }

        clearEmulatorData()
        auth.signOut()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @After
    open fun tearDown() = runEmulatorTest {
        auth.signOut()
        db.terminate().await()
        db.clearPersistence().await()
        FirebaseApp.clearInstancesForTest()
    }

    protected suspend fun signInAs(uid: String) {
        try {
            auth.createUserWithEmailAndPassword("$uid@example.com", "password123").await()
        } catch (e: Exception) {
            auth.signInWithEmailAndPassword("$uid@example.com", "password123").await()
        }
    }

    private fun clearEmulatorData() {
        val firestoreUrl = URL("http://127.0.0.1:8085/emulator/v1/projects/$projectId/databases/$databaseId/documents")
        (firestoreUrl.openConnection() as HttpURLConnection).apply {
            requestMethod = "DELETE"
            responseCode
            disconnect()
        }

        val authUrl = URL("http://127.0.0.1:9099/emulator/v1/projects/$projectId/accounts")
        (authUrl.openConnection() as HttpURLConnection).apply {
            requestMethod = "DELETE"
            responseCode
            disconnect()
        }
    }
}
