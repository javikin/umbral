package com.umbral.integration

import app.cash.turbine.test
import com.umbral.data.blocking.ProfileRepositoryImpl
import com.umbral.data.local.dao.BlockingProfileDao
import com.umbral.data.local.entity.BlockingProfileEntity
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.domain.blocking.ProfileRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * Integration tests for profile management flows.
 * Tests the interaction between ProfileRepository and DAO layer.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileFlowIntegrationTest {

    private lateinit var profileDao: BlockingProfileDao
    private lateinit var profileRepository: ProfileRepository

    private val testProfile = BlockingProfile(
        id = "test-profile",
        name = "Work Mode",
        iconName = "work",
        colorHex = "#6650A4",
        isActive = false,
        isStrictMode = false,
        blockedApps = listOf("com.facebook.katana", "com.instagram.android"),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Before
    fun setup() {
        profileDao = mockk(relaxed = true)
        profileRepository = ProfileRepositoryImpl(profileDao)
    }

    // ==================== CREATE PROFILE WITH APPS FLOW ====================

    @Test
    fun `create profile with apps flow`() = runTest {
        // Given: New profile with blocked apps
        val newProfile = BlockingProfile(
            id = "new-profile",
            name = "Focus Time",
            iconName = "focus",
            colorHex = "#FF5722",
            isActive = false,
            isStrictMode = false,
            blockedApps = listOf(
                "com.twitter.android",
                "com.youtube.android",
                "com.netflix.mediaclient"
            ),
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        coEvery { profileDao.insertProfile(any()) } just Runs

        // When: Save profile
        val result = profileRepository.saveProfile(newProfile)

        // Then: Profile is created successfully
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            profileDao.insertProfile(match { entity ->
                entity.id == "new-profile" &&
                entity.name == "Focus Time" &&
                entity.blockedApps.size == 3 &&
                entity.blockedApps.contains("com.twitter.android")
            })
        }
    }

    @Test
    fun `create profile then add apps`() = runTest {
        // Given: Profile with no apps initially
        val initialProfile = testProfile.copy(
            id = "new-profile",
            blockedApps = emptyList()
        )

        coEvery { profileDao.insertProfile(any()) } just Runs
        coEvery { profileDao.getProfileById("new-profile") } returns initialProfile.toEntity()

        // When: Create profile
        val createResult = profileRepository.saveProfile(initialProfile)
        assertTrue(createResult.isSuccess)

        // Then: Retrieve and verify empty apps
        val retrieved = profileRepository.getProfileById("new-profile")
        assertNotNull(retrieved)
        assertTrue(retrieved!!.blockedApps.isEmpty())

        // When: Update with apps
        val updatedProfile = initialProfile.copy(
            blockedApps = listOf("com.facebook.katana", "com.instagram.android"),
            updatedAt = LocalDateTime.now()
        )
        val updateResult = profileRepository.saveProfile(updatedProfile)

        // Then: Apps are added
        assertTrue(updateResult.isSuccess)
        coVerify(exactly = 2) { profileDao.insertProfile(any()) }
    }

    @Test
    fun `create multiple profiles with different apps`() = runTest {
        // Given: Multiple profiles
        val profile1 = testProfile.copy(
            id = "profile-1",
            name = "Work",
            blockedApps = listOf("com.facebook.katana", "com.twitter.android")
        )
        val profile2 = testProfile.copy(
            id = "profile-2",
            name = "Study",
            blockedApps = listOf("com.youtube.android", "com.netflix.mediaclient")
        )

        coEvery { profileDao.insertProfile(any()) } just Runs
        every { profileDao.getAllProfiles() } returns flowOf(
            listOf(profile1.toEntity(), profile2.toEntity())
        )

        // When: Save both profiles
        val result1 = profileRepository.saveProfile(profile1)
        val result2 = profileRepository.saveProfile(profile2)

        // Then: Both profiles are saved
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)

        // Then: Both profiles exist
        profileRepository.getAllProfiles().test {
            val profiles = awaitItem()
            assertEquals(2, profiles.size)
            assertTrue(profiles.any { it.id == "profile-1" })
            assertTrue(profiles.any { it.id == "profile-2" })

            val workProfile = profiles.find { it.id == "profile-1" }
            assertEquals(2, workProfile?.blockedApps?.size)
            assertTrue(workProfile?.blockedApps?.contains("com.facebook.katana") == true)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `verify blocked apps after profile creation`() = runTest {
        // Given: Profile with specific apps
        val blockedApps = listOf(
            "com.facebook.katana",
            "com.instagram.android",
            "com.twitter.android",
            "com.youtube.android"
        )
        val profile = testProfile.copy(blockedApps = blockedApps)

        coEvery { profileDao.insertProfile(any()) } just Runs
        coEvery { profileDao.getProfileById(testProfile.id) } returns profile.toEntity()

        // When: Create and retrieve profile
        val saveResult = profileRepository.saveProfile(profile)
        val retrieved = profileRepository.getProfileById(testProfile.id)

        // Then: All apps are present and in correct order
        assertTrue(saveResult.isSuccess)
        assertNotNull(retrieved)
        assertEquals(4, retrieved!!.blockedApps.size)
        assertEquals(blockedApps, retrieved.blockedApps)
    }

    // ==================== UPDATE PROFILE UPDATES BLOCKING STATE ====================

    @Test
    fun `update profile updates blocking state`() = runTest {
        // Given: Active profile
        val activeProfile = testProfile.copy(
            isActive = true,
            blockedApps = listOf("com.facebook.katana")
        )

        coEvery { profileDao.getProfileById(testProfile.id) } returns activeProfile.toEntity()
        every { profileDao.getActiveProfile() } returns flowOf(activeProfile.toEntity())

        // When: Update profile with new apps
        val updatedProfile = activeProfile.copy(
            blockedApps = listOf("com.facebook.katana", "com.instagram.android", "com.twitter.android"),
            updatedAt = LocalDateTime.now()
        )

        coEvery { profileDao.insertProfile(any()) } just Runs
        every { profileDao.getActiveProfile() } returns flowOf(updatedProfile.toEntity())

        val updateResult = profileRepository.saveProfile(updatedProfile)

        // Then: Profile is updated
        assertTrue(updateResult.isSuccess)

        // Verify active profile reflects changes
        profileRepository.getActiveProfile().test {
            val profile = awaitItem()
            assertNotNull(profile)
            assertEquals(3, profile!!.blockedApps.size)
            assertTrue(profile.blockedApps.contains("com.instagram.android"))
            assertTrue(profile.blockedApps.contains("com.twitter.android"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `update profile name and icon`() = runTest {
        // Given: Existing profile
        coEvery { profileDao.getProfileById(testProfile.id) } returns testProfile.toEntity()

        // When: Update metadata
        val updated = testProfile.copy(
            name = "Updated Work Mode",
            iconName = "briefcase",
            colorHex = "#FF0000",
            updatedAt = LocalDateTime.now()
        )

        coEvery { profileDao.insertProfile(any()) } just Runs
        coEvery { profileDao.getProfileById(testProfile.id) } returns updated.toEntity()

        val result = profileRepository.saveProfile(updated)
        val retrieved = profileRepository.getProfileById(testProfile.id)

        // Then: Metadata is updated
        assertTrue(result.isSuccess)
        assertEquals("Updated Work Mode", retrieved?.name)
        assertEquals("briefcase", retrieved?.iconName)
        assertEquals("#FF0000", retrieved?.colorHex)
    }

    @Test
    fun `toggle strict mode on existing profile`() = runTest {
        // Given: Non-strict profile
        val profile = testProfile.copy(isStrictMode = false)
        coEvery { profileDao.getProfileById(testProfile.id) } returns profile.toEntity()

        // When: Enable strict mode
        val strictProfile = profile.copy(
            isStrictMode = true,
            updatedAt = LocalDateTime.now()
        )

        coEvery { profileDao.insertProfile(any()) } just Runs
        coEvery { profileDao.getProfileById(testProfile.id) } returns strictProfile.toEntity()

        val result = profileRepository.saveProfile(strictProfile)
        val retrieved = profileRepository.getProfileById(testProfile.id)

        // Then: Strict mode is enabled
        assertTrue(result.isSuccess)
        assertTrue(retrieved?.isStrictMode == true)
    }

    @Test
    fun `add apps to existing profile`() = runTest {
        // Given: Profile with some apps
        val profile = testProfile.copy(
            blockedApps = listOf("com.facebook.katana")
        )
        coEvery { profileDao.getProfileById(testProfile.id) } returns profile.toEntity()

        // When: Add more apps
        val updatedProfile = profile.copy(
            blockedApps = profile.blockedApps + listOf("com.instagram.android", "com.twitter.android"),
            updatedAt = LocalDateTime.now()
        )

        coEvery { profileDao.insertProfile(any()) } just Runs
        coEvery { profileDao.getProfileById(testProfile.id) } returns updatedProfile.toEntity()

        val result = profileRepository.saveProfile(updatedProfile)
        val retrieved = profileRepository.getProfileById(testProfile.id)

        // Then: Apps are added
        assertTrue(result.isSuccess)
        assertEquals(3, retrieved?.blockedApps?.size)
        assertTrue(retrieved?.blockedApps?.containsAll(
            listOf("com.facebook.katana", "com.instagram.android", "com.twitter.android")
        ) == true)
    }

    @Test
    fun `remove apps from existing profile`() = runTest {
        // Given: Profile with multiple apps
        val profile = testProfile.copy(
            blockedApps = listOf("com.facebook.katana", "com.instagram.android", "com.twitter.android")
        )
        coEvery { profileDao.getProfileById(testProfile.id) } returns profile.toEntity()

        // When: Remove some apps
        val updatedProfile = profile.copy(
            blockedApps = listOf("com.facebook.katana"),
            updatedAt = LocalDateTime.now()
        )

        coEvery { profileDao.insertProfile(any()) } just Runs
        coEvery { profileDao.getProfileById(testProfile.id) } returns updatedProfile.toEntity()

        val result = profileRepository.saveProfile(updatedProfile)
        val retrieved = profileRepository.getProfileById(testProfile.id)

        // Then: Apps are removed
        assertTrue(result.isSuccess)
        assertEquals(1, retrieved?.blockedApps?.size)
        assertTrue(retrieved?.blockedApps?.contains("com.facebook.katana") == true)
        assertFalse(retrieved?.blockedApps?.contains("com.instagram.android") == true)
    }

    // ==================== PROFILE ACTIVATION FLOW ====================

    @Test
    fun `activate profile deactivates others`() = runTest {
        // Given: Multiple profiles
        coEvery { profileDao.deactivateAllProfiles() } just Runs
        coEvery { profileDao.activateProfile("profile-1") } just Runs

        // When: Activate one profile
        val result = profileRepository.activateProfile("profile-1")

        // Then: All others are deactivated first
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileDao.deactivateAllProfiles() }
        coVerify(exactly = 1) { profileDao.activateProfile("profile-1") }
    }

    @Test
    fun `deactivate all profiles`() = runTest {
        // Given: Some active profile
        coEvery { profileDao.deactivateAllProfiles() } just Runs

        // When: Deactivate all
        val result = profileRepository.deactivateAllProfiles()

        // Then: Operation succeeds
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileDao.deactivateAllProfiles() }
    }

    @Test
    fun `get active profile returns correct profile`() = runTest {
        // Given: Active profile
        val activeProfile = testProfile.copy(isActive = true)
        every { profileDao.getActiveProfile() } returns flowOf(activeProfile.toEntity())

        // When: Get active profile
        profileRepository.getActiveProfile().test {
            val profile = awaitItem()

            // Then: Correct profile is returned
            assertNotNull(profile)
            assertEquals(testProfile.id, profile?.id)
            assertTrue(profile?.isActive == true)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `get active profile returns null when none active`() = runTest {
        // Given: No active profile
        every { profileDao.getActiveProfile() } returns flowOf(null)

        // When: Get active profile
        profileRepository.getActiveProfile().test {
            val profile = awaitItem()

            // Then: Null is returned
            assertNull(profile)

            cancelAndIgnoreRemainingEvents()
        }
    }

    // ==================== DELETE PROFILE FLOW ====================

    @Test
    fun `delete existing profile`() = runTest {
        // Given: Existing profile
        val profile = testProfile.toEntity()
        coEvery { profileDao.getProfileById(testProfile.id) } returns profile
        coEvery { profileDao.deleteProfile(profile) } just Runs

        // When: Delete profile
        val result = profileRepository.deleteProfile(testProfile.id)

        // Then: Profile is deleted
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileDao.deleteProfile(profile) }
    }

    @Test
    fun `delete non-existing profile returns failure`() = runTest {
        // Given: Profile doesn't exist
        coEvery { profileDao.getProfileById("non-existent") } returns null

        // When: Try to delete
        val result = profileRepository.deleteProfile("non-existent")

        // Then: Operation fails
        assertTrue(result.isFailure)
        assertEquals("Profile not found", result.exceptionOrNull()?.message)
    }

    // ==================== ERROR HANDLING ====================

    @Test
    fun `save profile handles dao exception`() = runTest {
        // Given: DAO throws exception
        coEvery { profileDao.insertProfile(any()) } throws RuntimeException("Database error")

        // When: Try to save
        val result = profileRepository.saveProfile(testProfile)

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertEquals("Database error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `activate profile handles dao exception`() = runTest {
        // Given: DAO throws exception
        coEvery { profileDao.deactivateAllProfiles() } throws RuntimeException("Database error")

        // When: Try to activate
        val result = profileRepository.activateProfile("profile-1")

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertEquals("Database error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `deactivate profiles handles dao exception`() = runTest {
        // Given: DAO throws exception
        coEvery { profileDao.deactivateAllProfiles() } throws RuntimeException("Database error")

        // When: Try to deactivate
        val result = profileRepository.deactivateAllProfiles()

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertEquals("Database error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `delete profile handles dao exception`() = runTest {
        // Given: Profile exists but delete throws exception
        val profile = testProfile.toEntity()
        coEvery { profileDao.getProfileById(testProfile.id) } returns profile
        coEvery { profileDao.deleteProfile(any()) } throws RuntimeException("Database error")

        // When: Try to delete
        val result = profileRepository.deleteProfile(testProfile.id)

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertEquals("Database error", result.exceptionOrNull()?.message)
    }

    // ==================== Helper Functions ====================

    private fun BlockingProfile.toEntity(): BlockingProfileEntity {
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
