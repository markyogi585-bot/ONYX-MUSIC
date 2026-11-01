package com.example.data.firebase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import com.example.base.FirestoreEmulatorTestBase

class OnyxFirebaseRepositoryRuleTest : FirestoreEmulatorTestBase() {

    private lateinit var repository: OnyxFirebaseRepository

    @Before
    override fun setUp() {
        super.setUp()
        repository = OnyxFirebaseRepository(db, auth)
    }

    @Test
    fun `unauthenticated user cannot create or get cloud_items`() = runEmulatorTest {
        try {
            repository.createCloudItem(
                userId = "unauthed_user",
                itemId = "item_1",
                songId = "song_1",
                title = "Dhurandhar - Title Track",
                artist = "Hanumankind",
                category = "favorite"
            )
            fail("Expected PERMISSION_DENIED for unauthenticated user")
        } catch (e: RuntimeException) {
            assertTrue("Expected PERMISSION_DENIED in error JSON: ${e.message}", e.message?.contains("PERMISSION_DENIED") == true)
        }
    }

    @Test
    fun `authenticated owner can create get update and delete own cloud_items`() = runEmulatorTest {
        signInAs("alice")
        val aliceUid = auth.currentUser!!.uid

        repository.createCloudItem(
            userId = aliceUid,
            itemId = "item_dhurandhar",
            songId = "song_dhurandhar",
            title = "Dhurandhar - Title Track",
            artist = "Hanumankind, Jasmine Sandlas",
            category = "favorite"
        )

        val fetched = repository.getCloudItem(aliceUid, "item_dhurandhar")
        assertNotNull(fetched)
        assertEquals("Dhurandhar - Title Track", fetched?.title)

        repository.updateCloudItemTitle(aliceUid, "item_dhurandhar", "Dhurandhar (Dolby Atmos)")
        val updated = repository.getCloudItem(aliceUid, "item_dhurandhar")
        assertEquals("Dhurandhar (Dolby Atmos)", updated?.title)

        repository.deleteCloudItem(aliceUid, "item_dhurandhar")
    }

    @Test
    fun `authenticated non-owner cannot read or delete another user cloud_item`() = runEmulatorTest {
        signInAs("alice")
        val aliceUid = auth.currentUser!!.uid

        repository.createCloudItem(
            userId = aliceUid,
            itemId = "item_secret",
            songId = "song_kesariya",
            title = "Kesariya",
            artist = "Arijit Singh",
            category = "favorite"
        )

        auth.signOut()
        signInAs("bob")

        try {
            repository.getCloudItem(aliceUid, "item_secret")
            fail("Expected PERMISSION_DENIED when bob reads alice's item")
        } catch (e: RuntimeException) {
            assertTrue("Expected PERMISSION_DENIED: ${e.message}", e.message?.contains("PERMISSION_DENIED") == true)
        }

        try {
            repository.deleteCloudItem(aliceUid, "item_secret")
            fail("Expected PERMISSION_DENIED when bob deletes alice's item")
        } catch (e: RuntimeException) {
            assertTrue("Expected PERMISSION_DENIED: ${e.message}", e.message?.contains("PERMISSION_DENIED") == true)
        }
    }
}
