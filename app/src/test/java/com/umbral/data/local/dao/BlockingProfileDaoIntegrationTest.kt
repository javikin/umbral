package com.umbral.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.umbral.data.local.database.UmbralDatabase
import com.umbral.data.local.entity.BlockingProfileEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class BlockingProfileDaoIntegrationTest {

    private lateinit var database: UmbralDatabase
    private lateinit var dao: BlockingProfileDao

    @Before
    fun setup() {
        // Create in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UmbralDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.blockingProfileDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertProfile_andRetrieveById_returnsCorrectProfile() = runTest {
        // Given
        val profile = createTestProfile(id = "test-1", name = "Work Mode")

        // When
        dao.insertProfile(profile)
        val retrieved = dao.getProfileById("test-1")

        // Then
        assertNotNull(retrieved)
        assertEquals("test-1", retrieved?.id)
        assertEquals("Work Mode", retrieved?.name)
        assertEquals("shield", retrieved?.iconName)
        assertEquals("#6650A4", retrieved?.colorHex)
    }

    @Test
    fun insertProfile_andRetrieveById_withNonExistentId_returnsNull() = runTest {
        // When
        val retrieved = dao.getProfileById("non-existent")

        // Then
        assertNull(retrieved)
    }

    @Test
    fun getAllProfiles_returnsAllInsertedProfiles() = runTest {
        // Given
        val profile1 = createTestProfile(id = "1", name = "Work")
        val profile2 = createTestProfile(id = "2", name = "Study")
        val profile3 = createTestProfile(id = "3", name = "Sleep")

        // When
        dao.insertProfile(profile1)
        dao.insertProfile(profile2)
        dao.insertProfile(profile3)
        val profiles = dao.getAllProfiles().first()

        // Then
        assertEquals(3, profiles.size)
        assertTrue(profiles.any { it.name == "Work" })
        assertTrue(profiles.any { it.name == "Study" })
        assertTrue(profiles.any { it.name == "Sleep" })
    }

    @Test
    fun getAllProfiles_orderedByUpdatedAtDesc() = runTest {
        // Given
        val now = LocalDateTime.now()
        val profile1 = createTestProfile(id = "1", name = "Old", updatedAt = now.minusHours(2))
        val profile2 = createTestProfile(id = "2", name = "Recent", updatedAt = now)
        val profile3 = createTestProfile(id = "3", name = "Medium", updatedAt = now.minusHours(1))

        // When
        dao.insertProfile(profile1)
        dao.insertProfile(profile2)
        dao.insertProfile(profile3)
        val profiles = dao.getAllProfiles().first()

        // Then
        assertEquals(3, profiles.size)
        assertEquals("Recent", profiles[0].name)
        assertEquals("Medium", profiles[1].name)
        assertEquals("Old", profiles[2].name)
    }

    @Test
    fun updateProfile_updatesExistingProfile() = runTest {
        // Given
        val profile = createTestProfile(id = "test-1", name = "Original Name")
        dao.insertProfile(profile)

        // When
        val updated = profile.copy(name = "Updated Name", isStrictMode = true)
        dao.updateProfile(updated)
        val retrieved = dao.getProfileById("test-1")

        // Then
        assertNotNull(retrieved)
        assertEquals("Updated Name", retrieved?.name)
        assertTrue(retrieved?.isStrictMode == true)
    }

    @Test
    fun deleteProfile_removesProfileFromDatabase() = runTest {
        // Given
        val profile = createTestProfile(id = "test-1", name = "To Delete")
        dao.insertProfile(profile)
        assertNotNull(dao.getProfileById("test-1"))

        // When
        dao.deleteProfile(profile)
        val retrieved = dao.getProfileById("test-1")

        // Then
        assertNull(retrieved)
    }

    @Test
    fun getActiveProfile_returnsActiveProfile() = runTest {
        // Given
        val inactiveProfile = createTestProfile(id = "1", name = "Inactive", isActive = false)
        val activeProfile = createTestProfile(id = "2", name = "Active", isActive = true)
        dao.insertProfile(inactiveProfile)
        dao.insertProfile(activeProfile)

        // When
        val retrieved = dao.getActiveProfile().first()

        // Then
        assertNotNull(retrieved)
        assertEquals("Active", retrieved?.name)
        assertTrue(retrieved?.isActive == true)
    }

    @Test
    fun getActiveProfile_whenNoActiveProfile_returnsNull() = runTest {
        // Given
        val profile1 = createTestProfile(id = "1", name = "Inactive 1", isActive = false)
        val profile2 = createTestProfile(id = "2", name = "Inactive 2", isActive = false)
        dao.insertProfile(profile1)
        dao.insertProfile(profile2)

        // When
        val retrieved = dao.getActiveProfile().first()

        // Then
        assertNull(retrieved)
    }

    @Test
    fun activateProfile_deactivatesAllOthers_andActivatesTarget() = runTest {
        // Given
        val profile1 = createTestProfile(id = "1", name = "Profile 1", isActive = true)
        val profile2 = createTestProfile(id = "2", name = "Profile 2", isActive = false)
        val profile3 = createTestProfile(id = "3", name = "Profile 3", isActive = false)
        dao.insertProfile(profile1)
        dao.insertProfile(profile2)
        dao.insertProfile(profile3)

        // When
        dao.deactivateAllProfiles()
        dao.activateProfile("2")

        // Then
        val active = dao.getActiveProfile().first()
        assertNotNull(active)
        assertEquals("2", active?.id)
        assertEquals("Profile 2", active?.name)

        val profile1Updated = dao.getProfileById("1")
        assertFalse(profile1Updated?.isActive == true)
    }

    @Test
    fun deactivateAllProfiles_deactivatesAllProfiles() = runTest {
        // Given
        val profile1 = createTestProfile(id = "1", name = "Profile 1", isActive = true)
        val profile2 = createTestProfile(id = "2", name = "Profile 2", isActive = true)
        dao.insertProfile(profile1)
        dao.insertProfile(profile2)

        // When
        dao.deactivateAllProfiles()

        // Then
        val activeProfile = dao.getActiveProfile().first()
        assertNull(activeProfile)

        val allProfiles = dao.getAllProfiles().first()
        assertTrue(allProfiles.all { !it.isActive })
    }

    @Test
    fun insertProfile_withReplace_updatesExisting() = runTest {
        // Given
        val original = createTestProfile(id = "test-1", name = "Original")
        dao.insertProfile(original)

        // When - Insert with same ID (REPLACE strategy)
        val replacement = createTestProfile(id = "test-1", name = "Replaced")
        dao.insertProfile(replacement)

        // Then
        val allProfiles = dao.getAllProfiles().first()
        assertEquals(1, allProfiles.size) // Should only have 1 profile
        assertEquals("Replaced", allProfiles[0].name)
    }

    @Test
    fun insertProfile_withBlockedApps_persistsList() = runTest {
        // Given
        val blockedApps = listOf(
            "com.instagram.android",
            "com.twitter.android",
            "com.tiktok"
        )
        val profile = createTestProfile(
            id = "test-1",
            name = "Social Media Block",
            blockedApps = blockedApps
        )

        // When
        dao.insertProfile(profile)
        val retrieved = dao.getProfileById("test-1")

        // Then
        assertNotNull(retrieved)
        assertEquals(3, retrieved?.blockedApps?.size)
        assertTrue(retrieved?.blockedApps?.contains("com.instagram.android") == true)
        assertTrue(retrieved?.blockedApps?.contains("com.twitter.android") == true)
        assertTrue(retrieved?.blockedApps?.contains("com.tiktok") == true)
    }

    @Test
    fun observeAllProfiles_emitsUpdates() = runTest {
        // Given - Initial state
        val profile1 = createTestProfile(id = "1", name = "Profile 1")
        dao.insertProfile(profile1)

        // When - First observation
        val initial = dao.getAllProfiles().first()
        assertEquals(1, initial.size)

        // When - Add another profile
        val profile2 = createTestProfile(id = "2", name = "Profile 2")
        dao.insertProfile(profile2)

        // Then - Should emit updated list
        val updated = dao.getAllProfiles().first()
        assertEquals(2, updated.size)
    }

    // Helper function to create test profiles
    private fun createTestProfile(
        id: String,
        name: String,
        iconName: String = "shield",
        colorHex: String = "#6650A4",
        isActive: Boolean = false,
        isStrictMode: Boolean = false,
        blockedApps: List<String> = emptyList(),
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = LocalDateTime.now()
    ): BlockingProfileEntity {
        return BlockingProfileEntity(
            id = id,
            name = name,
            iconName = iconName,
            colorHex = colorHex,
            isActive = isActive,
            isStrictMode = isStrictMode,
            blockedApps = blockedApps,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
