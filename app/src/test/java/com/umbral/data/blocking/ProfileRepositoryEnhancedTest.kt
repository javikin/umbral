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
 * Enhanced tests for ProfileRepository covering edge cases and Flow behavior.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileRepositoryEnhancedTest {

    private lateinit var profileDao: BlockingProfileDao
    private lateinit var repository: ProfileRepositoryImpl

    private val testTime = LocalDateTime.of(2024, 1, 15, 12, 0)

    private val testEntity = BlockingProfileEntity(
        id = "test-id",
        name = "Test Profile",
        iconName = "shield",
        colorHex = "#6650A4",
        isActive = true,
        isStrictMode = false,
        blockedApps = listOf("com.example.app1", "com.example.app2"),
        createdAt = testTime,
        updatedAt = testTime
    )

    @Before
    fun setup() {
        profileDao = mockk(relaxed = true)
        repository = ProfileRepositoryImpl(profileDao)
    }

    // Flow Emission Tests
    @Test
    fun `getAllProfiles emits updates when profiles change`() = runTest {
        // Given
        val initialProfiles = listOf(testEntity)
        val updatedProfiles = listOf(testEntity, testEntity.copy(id = "new-id"))
        every { profileDao.getAllProfiles() } returns flowOf(initialProfiles, updatedProfiles)

        // When
        repository.getAllProfiles().test {
            // Then - First emission
            val first = awaitItem()
            assertEquals(1, first.size)

            // Second emission
            val second = awaitItem()
            assertEquals(2, second.size)

            awaitComplete()
        }
    }

    @Test
    fun `getActiveProfile emits null then profile when activated`() = runTest {
        // Given
        every { profileDao.getActiveProfile() } returns flowOf(null, testEntity)

        // When
        repository.getActiveProfile().test {
            // Then - First emission (no active)
            assertEquals(null, awaitItem())

            // Second emission (profile activated)
            val profile = awaitItem()
            assertNotNull(profile)
            assertEquals("test-id", profile?.id)

            awaitComplete()
        }
    }

    // Blocked Apps Tests
    @Test
    fun `getAllProfiles preserves blocked apps list`() = runTest {
        // Given
        val blockedApps = listOf(
            "com.facebook.katana",
            "com.twitter.android",
            "com.instagram.android",
            "com.tiktok.android"
        )
        val entity = testEntity.copy(blockedApps = blockedApps)
        every { profileDao.getAllProfiles() } returns flowOf(listOf(entity))

        // When
        repository.getAllProfiles().test {
            val profiles = awaitItem()
            val profile = profiles.first()

            // Then
            assertEquals(4, profile.blockedApps.size)
            assertTrue(profile.blockedApps.contains("com.facebook.katana"))
            assertTrue(profile.blockedApps.contains("com.instagram.android"))
            awaitComplete()
        }
    }

    @Test
    fun `saveProfile handles empty blocked apps list`() = runTest {
        // Given
        val profile = BlockingProfile(
            id = "test-id",
            name = "Empty Profile",
            blockedApps = emptyList(),
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
                    assertTrue(it.blockedApps.isEmpty())
                }
            )
        }
    }

    @Test
    fun `saveProfile handles large blocked apps list`() = runTest {
        // Given
        val largeList = List(100) { "com.example.app$it" }
        val profile = BlockingProfile(
            id = "test-id",
            name = "Large Profile",
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
                    assertEquals(100, it.blockedApps.size)
                }
            )
        }
    }

    // Activation Tests
    @Test
    fun `activateProfile deactivates others in correct order`() = runTest {
        // Given
        coEvery { profileDao.deactivateAllProfiles() } just Runs
        coEvery { profileDao.activateProfile("test-id") } just Runs

        // When
        repository.activateProfile("test-id")

        // Then
        coVerify(ordering = io.mockk.Ordering.ORDERED) {
            profileDao.deactivateAllProfiles()
            profileDao.activateProfile("test-id")
        }
    }

    @Test
    fun `activateProfile fails if deactivation fails`() = runTest {
        // Given
        val exception = RuntimeException("Deactivation failed")
        coEvery { profileDao.deactivateAllProfiles() } throws exception

        // When
        val result = repository.activateProfile("test-id")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        // activateProfile should not be called
        coVerify(exactly = 0) { profileDao.activateProfile(any()) }
    }

    @Test
    fun `activateProfile fails if activation fails after deactivation`() = runTest {
        // Given
        coEvery { profileDao.deactivateAllProfiles() } just Runs
        val exception = RuntimeException("Activation failed")
        coEvery { profileDao.activateProfile("test-id") } throws exception

        // When
        val result = repository.activateProfile("test-id")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // Multiple Profile Tests
    @Test
    fun `getAllProfiles handles multiple profiles with different states`() = runTest {
        // Given
        val profiles = listOf(
            testEntity.copy(id = "1", isActive = true, isStrictMode = false),
            testEntity.copy(id = "2", isActive = false, isStrictMode = true),
            testEntity.copy(id = "3", isActive = false, isStrictMode = false)
        )
        every { profileDao.getAllProfiles() } returns flowOf(profiles)

        // When
        repository.getAllProfiles().test {
            val result = awaitItem()

            // Then
            assertEquals(3, result.size)
            assertEquals(1, result.count { it.isActive })
            assertEquals(1, result.count { it.isStrictMode })
            awaitComplete()
        }
    }

    // Update Profile Tests
    @Test
    fun `saveProfile updates existing profile`() = runTest {
        // Given
        val updatedProfile = BlockingProfile(
            id = "test-id",
            name = "Updated Name",
            iconName = "lock",
            colorHex = "#FF0000",
            isActive = false,
            isStrictMode = true,
            blockedApps = listOf("com.new.app"),
            createdAt = testTime,
            updatedAt = testTime.plusDays(1)
        )
        coEvery { profileDao.insertProfile(any()) } just Runs

        // When
        val result = repository.saveProfile(updatedProfile)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            profileDao.insertProfile(
                withArg {
                    assertEquals("Updated Name", it.name)
                    assertEquals("lock", it.iconName)
                    assertEquals("#FF0000", it.colorHex)
                    assertTrue(it.isStrictMode)
                    assertFalse(it.isActive)
                }
            )
        }
    }

    // Delete Multiple Profiles Tests
    @Test
    fun `deleteProfile can delete multiple profiles sequentially`() = runTest {
        // Given
        val profile1 = testEntity.copy(id = "1")
        val profile2 = testEntity.copy(id = "2")
        coEvery { profileDao.getProfileById("1") } returns profile1
        coEvery { profileDao.getProfileById("2") } returns profile2
        coEvery { profileDao.deleteProfile(any()) } just Runs

        // When
        val result1 = repository.deleteProfile("1")
        val result2 = repository.deleteProfile("2")

        // Then
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        coVerify(exactly = 1) { profileDao.deleteProfile(profile1) }
        coVerify(exactly = 1) { profileDao.deleteProfile(profile2) }
    }

    // Edge Case: Empty String IDs
    @Test
    fun `getProfileById handles empty string ID`() = runTest {
        // Given
        coEvery { profileDao.getProfileById("") } returns null

        // When
        val result = repository.getProfileById("")

        // Then
        assertEquals(null, result)
    }

    // Strict Mode Tests
    @Test
    fun `saveProfile preserves strict mode flag`() = runTest {
        // Given
        val strictProfile = testEntity.copy(isStrictMode = true)
        val profile = BlockingProfile(
            id = strictProfile.id,
            name = strictProfile.name,
            iconName = strictProfile.iconName,
            colorHex = strictProfile.colorHex,
            isActive = strictProfile.isActive,
            isStrictMode = strictProfile.isStrictMode,
            blockedApps = strictProfile.blockedApps,
            createdAt = strictProfile.createdAt,
            updatedAt = strictProfile.updatedAt
        )
        coEvery { profileDao.insertProfile(any()) } just Runs

        // When
        repository.saveProfile(profile)

        // Then
        coVerify {
            profileDao.insertProfile(
                withArg {
                    assertTrue(it.isStrictMode)
                }
            )
        }
    }

    // Icon and Color Tests
    @Test
    fun `saveProfile preserves custom icon and color`() = runTest {
        // Given
        val customProfile = BlockingProfile(
            id = "test",
            name = "Custom",
            iconName = "custom_icon",
            colorHex = "#123456",
            createdAt = testTime,
            updatedAt = testTime
        )
        coEvery { profileDao.insertProfile(any()) } just Runs

        // When
        repository.saveProfile(customProfile)

        // Then
        coVerify {
            profileDao.insertProfile(
                withArg {
                    assertEquals("custom_icon", it.iconName)
                    assertEquals("#123456", it.colorHex)
                }
            )
        }
    }

    // Timestamp Tests
    @Test
    fun `saveProfile preserves timestamps`() = runTest {
        // Given
        val createdTime = LocalDateTime.of(2024, 1, 1, 10, 0)
        val updatedTime = LocalDateTime.of(2024, 1, 15, 14, 30)
        val profile = BlockingProfile(
            id = "test",
            name = "Test",
            createdAt = createdTime,
            updatedAt = updatedTime
        )
        coEvery { profileDao.insertProfile(any()) } just Runs

        // When
        repository.saveProfile(profile)

        // Then
        coVerify {
            profileDao.insertProfile(
                withArg {
                    assertEquals(createdTime, it.createdAt)
                    assertEquals(updatedTime, it.updatedAt)
                }
            )
        }
    }

    // Concurrent Operations Tests
    @Test
    fun `repository handles concurrent profile reads`() = runTest {
        // Given
        every { profileDao.getAllProfiles() } returns flowOf(listOf(testEntity))
        every { profileDao.getActiveProfile() } returns flowOf(testEntity)
        coEvery { profileDao.getProfileById("test-id") } returns testEntity

        // When - Simulate concurrent access
        val profile1 = repository.getProfileById("test-id")

        // Then - All should succeed
        assertNotNull(profile1)

        repository.getAllProfiles().test {
            assertEquals(1, awaitItem().size)
            awaitComplete()
        }

        repository.getActiveProfile().test {
            assertNotNull(awaitItem())
            awaitComplete()
        }
    }
}
