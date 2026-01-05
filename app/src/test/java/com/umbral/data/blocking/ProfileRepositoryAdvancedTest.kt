package com.umbral.data.blocking

import app.cash.turbine.test
import com.umbral.data.local.dao.BlockingProfileDao
import com.umbral.data.local.entity.BlockingProfileEntity
import com.umbral.domain.blocking.BlockingProfile
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * Advanced test scenarios for ProfileRepository.
 * Covers race conditions, concurrent operations, and complex state transitions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileRepositoryAdvancedTest {

    private lateinit var profileDao: BlockingProfileDao
    private lateinit var repository: ProfileRepositoryImpl

    private val testTime = LocalDateTime.of(2024, 1, 15, 12, 0)

    @Before
    fun setup() {
        profileDao = mockk(relaxed = true)
        repository = ProfileRepositoryImpl(profileDao)
    }

    // Concurrent Profile Activation Tests
    @Test
    fun `activating profile A then profile B leaves only B active`() = runTest {
        // Given
        coEvery { profileDao.deactivateAllProfiles() } just Runs
        coEvery { profileDao.activateProfile(any()) } just Runs

        // When - Simulate rapid profile switching
        repository.activateProfile("profile-a")
        repository.activateProfile("profile-b")

        // Then - deactivateAll should be called twice (once per activation)
        coVerify(exactly = 2) { profileDao.deactivateAllProfiles() }
        coVerify(exactly = 1) { profileDao.activateProfile("profile-a") }
        coVerify(exactly = 1) { profileDao.activateProfile("profile-b") }
    }

    @Test
    fun `saveProfile updates timestamp correctly`() = runTest {
        // Given
        val originalTime = testTime
        val updatedTime = testTime.plusHours(2)
        val profile = BlockingProfile(
            id = "test",
            name = "Original",
            createdAt = originalTime,
            updatedAt = originalTime
        )
        val updatedProfile = profile.copy(
            name = "Updated",
            updatedAt = updatedTime
        )
        coEvery { profileDao.insertProfile(any()) } just Runs

        // When
        repository.saveProfile(profile)
        repository.saveProfile(updatedProfile)

        // Then - Verify updatedAt changed but createdAt stayed the same
        coVerify(exactly = 2) {
            profileDao.insertProfile(
                withArg {
                    assertEquals(originalTime, it.createdAt)
                }
            )
        }
        coVerify(exactly = 1) {
            profileDao.insertProfile(
                withArg {
                    assertEquals(updatedTime, it.updatedAt)
                }
            )
        }
    }

    // Complex Blocked Apps Scenarios
    @Test
    fun `saveProfile handles duplicate package names in blocked apps`() = runTest {
        // Given - List with duplicates (shouldn't happen but let's test)
        val appsWithDuplicates = listOf(
            "com.facebook.katana",
            "com.instagram.android",
            "com.facebook.katana", // duplicate
            "com.twitter.android"
        )
        val profile = BlockingProfile(
            id = "test",
            name = "Test",
            blockedApps = appsWithDuplicates,
            createdAt = testTime,
            updatedAt = testTime
        )
        coEvery { profileDao.insertProfile(any()) } just Runs

        // When
        val result = repository.saveProfile(profile)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            profileDao.insertProfile(
                withArg {
                    // Verify all apps (including duplicates) are saved as-is
                    assertEquals(4, it.blockedApps.size)
                    assertTrue(it.blockedApps.contains("com.facebook.katana"))
                }
            )
        }
    }

    @Test
    fun `saveProfile handles special characters in app names`() = runTest {
        // Given
        val appsWithSpecialChars = listOf(
            "com.app.with-dash",
            "com.app.with_underscore",
            "com.app.with.many.dots",
            "com.app123.numbers",
            "com.ąćęłńóśźż.unicode" // Unicode characters
        )
        val profile = BlockingProfile(
            id = "test",
            name = "Special",
            blockedApps = appsWithSpecialChars,
            createdAt = testTime,
            updatedAt = testTime
        )
        coEvery { profileDao.insertProfile(any()) } just Runs

        // When
        val result = repository.saveProfile(profile)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            profileDao.insertProfile(
                withArg {
                    assertEquals(5, it.blockedApps.size)
                    assertTrue(it.blockedApps.contains("com.ąćęłńóśźż.unicode"))
                }
            )
        }
    }

    // Profile Name Edge Cases
    @Test
    fun `saveProfile handles very long profile names`() = runTest {
        // Given
        val longName = "A".repeat(500) // 500 character name
        val profile = BlockingProfile(
            id = "test",
            name = longName,
            createdAt = testTime,
            updatedAt = testTime
        )
        coEvery { profileDao.insertProfile(any()) } just Runs

        // When
        val result = repository.saveProfile(profile)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            profileDao.insertProfile(
                withArg {
                    assertEquals(500, it.name.length)
                }
            )
        }
    }

    @Test
    fun `saveProfile handles empty profile name`() = runTest {
        // Given
        val profile = BlockingProfile(
            id = "test",
            name = "",
            createdAt = testTime,
            updatedAt = testTime
        )
        coEvery { profileDao.insertProfile(any()) } just Runs

        // When
        val result = repository.saveProfile(profile)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            profileDao.insertProfile(
                withArg {
                    assertEquals("", it.name)
                }
            )
        }
    }

    @Test
    fun `saveProfile handles names with special characters`() = runTest {
        // Given
        val specialNames = listOf(
            "Profile with emoji 🎯",
            "Nombre con ñ y tildes áéíóú",
            "名前 (Japanese)",
            "Profile\nwith\nnewlines",
            "Profile\twith\ttabs"
        )

        coEvery { profileDao.insertProfile(any()) } just Runs

        // When/Then
        specialNames.forEach { name ->
            val profile = BlockingProfile(
                id = "test-$name",
                name = name,
                createdAt = testTime,
                updatedAt = testTime
            )
            val result = repository.saveProfile(profile)
            assertTrue("Failed for name: $name", result.isSuccess)
        }
    }

    // Color Hex Edge Cases
    @Test
    fun `saveProfile handles various color hex formats`() = runTest {
        // Given
        val colorFormats = listOf(
            "#FFFFFF",    // Standard 6-char
            "#FFF",       // Short 3-char
            "#00000000",  // 8-char with alpha
            "FFFFFF",     // Without hash
            "#ffffff",    // Lowercase
            "#AbCdEf"     // Mixed case
        )

        coEvery { profileDao.insertProfile(any()) } just Runs

        // When/Then
        colorFormats.forEach { color ->
            val profile = BlockingProfile(
                id = "test-$color",
                name = "Test",
                colorHex = color,
                createdAt = testTime,
                updatedAt = testTime
            )
            val result = repository.saveProfile(profile)
            assertTrue("Failed for color: $color", result.isSuccess)
        }
    }

    // Flow Behavior Tests
    @Test
    fun `getAllProfiles emits multiple updates correctly`() = runTest {
        // Given
        val update1 = listOf(createTestEntity("1"))
        val update2 = listOf(createTestEntity("1"), createTestEntity("2"))
        val update3 = listOf(createTestEntity("1"), createTestEntity("2"), createTestEntity("3"))

        every { profileDao.getAllProfiles() } returns flowOf(update1, update2, update3)

        // When/Then
        repository.getAllProfiles().test {
            assertEquals(1, awaitItem().size)
            assertEquals(2, awaitItem().size)
            assertEquals(3, awaitItem().size)
            awaitComplete()
        }
    }

    @Test
    fun `getActiveProfile handles rapid activation changes`() = runTest {
        // Given - Simulate rapid profile switching
        val profile1 = createTestEntity("1", isActive = true)
        val profile2 = createTestEntity("2", isActive = true)
        val noActive = null

        every { profileDao.getActiveProfile() } returns flowOf(profile1, noActive, profile2)

        // When/Then
        repository.getActiveProfile().test {
            val first = awaitItem()
            assertEquals("1", first?.id)

            val second = awaitItem()
            assertEquals(null, second)

            val third = awaitItem()
            assertEquals("2", third?.id)

            awaitComplete()
        }
    }

    // Delete Profile Edge Cases
    @Test
    fun `deleteProfile handles deletion of active profile`() = runTest {
        // Given
        val activeProfile = createTestEntity("active", isActive = true)
        coEvery { profileDao.getProfileById("active") } returns activeProfile
        coEvery { profileDao.deleteProfile(any()) } just Runs

        // When
        val result = repository.deleteProfile("active")

        // Then - Should succeed even if profile is active
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileDao.deleteProfile(activeProfile) }
    }

    @Test
    fun `deleteProfile with very long profile ID`() = runTest {
        // Given
        val longId = "a".repeat(1000)
        coEvery { profileDao.getProfileById(longId) } returns null

        // When
        val result = repository.deleteProfile(longId)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Profile not found", result.exceptionOrNull()?.message)
    }

    // Multiple Profiles with Same Name
    @Test
    fun `getAllProfiles handles profiles with identical names`() = runTest {
        // Given - Multiple profiles can have the same name (different IDs)
        val profiles = listOf(
            createTestEntity("1", name = "Work"),
            createTestEntity("2", name = "Work"),
            createTestEntity("3", name = "Work")
        )
        every { profileDao.getAllProfiles() } returns flowOf(profiles)

        // When
        repository.getAllProfiles().test {
            val result = awaitItem()

            // Then
            assertEquals(3, result.size)
            assertTrue(result.all { it.name == "Work" })
            assertEquals(setOf("1", "2", "3"), result.map { it.id }.toSet())
            awaitComplete()
        }
    }

    // Stress Tests
    @Test
    fun `saveProfile handles extremely large blocked apps list`() = runTest {
        // Given - 1000 apps
        val largeList = List(1000) { "com.example.app$it" }
        val profile = BlockingProfile(
            id = "stress-test",
            name = "Stress",
            blockedApps = largeList,
            createdAt = testTime,
            updatedAt = testTime
        )
        coEvery { profileDao.insertProfile(any()) } just Runs

        // When
        val result = repository.saveProfile(profile)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            profileDao.insertProfile(
                withArg {
                    assertEquals(1000, it.blockedApps.size)
                }
            )
        }
    }

    @Test
    fun `getAllProfiles handles many profiles efficiently`() = runTest {
        // Given - 100 profiles
        val manyProfiles = List(100) { createTestEntity("$it") }
        every { profileDao.getAllProfiles() } returns flowOf(manyProfiles)

        // When
        repository.getAllProfiles().test {
            val result = awaitItem()

            // Then
            assertEquals(100, result.size)
            awaitComplete()
        }
    }

    // Icon Name Validation
    @Test
    fun `saveProfile handles various icon name formats`() = runTest {
        // Given
        val iconNames = listOf(
            "shield",
            "lock_outline",
            "ic_profile_work",
            "🎯", // Emoji as icon name
            "icon-with-dashes",
            ""    // Empty icon name
        )

        coEvery { profileDao.insertProfile(any()) } just Runs

        // When/Then
        iconNames.forEach { iconName ->
            val profile = BlockingProfile(
                id = "test-$iconName",
                name = "Test",
                iconName = iconName,
                createdAt = testTime,
                updatedAt = testTime
            )
            val result = repository.saveProfile(profile)
            assertTrue("Failed for icon: $iconName", result.isSuccess)
        }
    }

    // Strict Mode Combinations
    @Test
    fun `getAllProfiles handles all combinations of isActive and isStrictMode`() = runTest {
        // Given - All 4 combinations
        val profiles = listOf(
            createTestEntity("1", isActive = true, isStrictMode = true),
            createTestEntity("2", isActive = true, isStrictMode = false),
            createTestEntity("3", isActive = false, isStrictMode = true),
            createTestEntity("4", isActive = false, isStrictMode = false)
        )
        every { profileDao.getAllProfiles() } returns flowOf(profiles)

        // When
        repository.getAllProfiles().test {
            val result = awaitItem()

            // Then
            assertEquals(4, result.size)
            assertEquals(1, result.count { it.isActive && it.isStrictMode })
            assertEquals(1, result.count { it.isActive && !it.isStrictMode })
            assertEquals(1, result.count { !it.isActive && it.isStrictMode })
            assertEquals(1, result.count { !it.isActive && !it.isStrictMode })
            awaitComplete()
        }
    }

    // Database Transaction Edge Cases
    @Test
    fun `activateProfile handles partial failure gracefully`() = runTest {
        // Given - deactivate succeeds, but activate fails
        coEvery { profileDao.deactivateAllProfiles() } just Runs
        val activationError = RuntimeException("Constraint violation")
        coEvery { profileDao.activateProfile("test") } throws activationError

        // When
        val result = repository.activateProfile("test")

        // Then - Should return failure with original exception
        assertTrue(result.isFailure)
        assertEquals(activationError, result.exceptionOrNull())
        // Verify deactivate was still called
        coVerify(exactly = 1) { profileDao.deactivateAllProfiles() }
    }

    @Test
    fun `deactivateAllProfiles succeeds with no profiles`() = runTest {
        // Given - No profiles exist
        coEvery { profileDao.deactivateAllProfiles() } just Runs

        // When
        val result = repository.deactivateAllProfiles()

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileDao.deactivateAllProfiles() }
    }

    // Helper function to create test entities
    private fun createTestEntity(
        id: String,
        name: String = "Profile $id",
        isActive: Boolean = false,
        isStrictMode: Boolean = false
    ): BlockingProfileEntity {
        return BlockingProfileEntity(
            id = id,
            name = name,
            iconName = "shield",
            colorHex = "#6650A4",
            isActive = isActive,
            isStrictMode = isStrictMode,
            blockedApps = listOf("com.example.app1"),
            createdAt = testTime,
            updatedAt = testTime
        )
    }
}
