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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileRepositoryTest {

    private lateinit var profileDao: BlockingProfileDao
    private lateinit var repository: ProfileRepositoryImpl

    private val testEntity = BlockingProfileEntity(
        id = "test-id",
        name = "Test Profile",
        iconName = "shield",
        colorHex = "#6650A4",
        isActive = true,
        isStrictMode = false,
        blockedApps = listOf("com.example.app1", "com.example.app2"),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    private val testProfile = BlockingProfile(
        id = "test-id",
        name = "Test Profile",
        iconName = "shield",
        colorHex = "#6650A4",
        isActive = true,
        isStrictMode = false,
        blockedApps = listOf("com.example.app1", "com.example.app2"),
        createdAt = testEntity.createdAt,
        updatedAt = testEntity.updatedAt
    )

    @Before
    fun setup() {
        profileDao = mockk(relaxed = true)
        repository = ProfileRepositoryImpl(profileDao)
    }

    // getAllProfiles Tests
    @Test
    fun `getAllProfiles returns empty list when no profiles exist`() = runTest {
        // Given
        every { profileDao.getAllProfiles() } returns flowOf(emptyList())

        // When
        repository.getAllProfiles().test {
            // Then
            val profiles = awaitItem()
            assertEquals(0, profiles.size)
            awaitComplete()
        }
    }

    @Test
    fun `getAllProfiles returns list of profiles`() = runTest {
        // Given
        val entities = listOf(testEntity, testEntity.copy(id = "test-id-2", name = "Profile 2"))
        every { profileDao.getAllProfiles() } returns flowOf(entities)

        // When
        repository.getAllProfiles().test {
            // Then
            val profiles = awaitItem()
            assertEquals(2, profiles.size)
            assertEquals("Test Profile", profiles[0].name)
            assertEquals("Profile 2", profiles[1].name)
            awaitComplete()
        }
    }

    // getActiveProfile Tests
    @Test
    fun `getActiveProfile returns null when no active profile`() = runTest {
        // Given
        every { profileDao.getActiveProfile() } returns flowOf(null)

        // When
        repository.getActiveProfile().test {
            // Then
            val profile = awaitItem()
            assertNull(profile)
            awaitComplete()
        }
    }

    @Test
    fun `getActiveProfile returns active profile`() = runTest {
        // Given
        every { profileDao.getActiveProfile() } returns flowOf(testEntity)

        // When
        repository.getActiveProfile().test {
            // Then
            val profile = awaitItem()
            assertNotNull(profile)
            assertEquals("Test Profile", profile?.name)
            assertEquals(true, profile?.isActive)
            awaitComplete()
        }
    }

    // getProfileById Tests
    @Test
    fun `getProfileById returns null when profile not found`() = runTest {
        // Given
        coEvery { profileDao.getProfileById("nonexistent") } returns null

        // When
        val result = repository.getProfileById("nonexistent")

        // Then
        assertNull(result)
    }

    @Test
    fun `getProfileById returns profile when found`() = runTest {
        // Given
        coEvery { profileDao.getProfileById("test-id") } returns testEntity

        // When
        val result = repository.getProfileById("test-id")

        // Then
        assertNotNull(result)
        assertEquals("test-id", result?.id)
        assertEquals("Test Profile", result?.name)
    }

    // saveProfile Tests
    @Test
    fun `saveProfile successfully saves profile`() = runTest {
        // Given
        coEvery { profileDao.insertProfile(any()) } just Runs

        // When
        val result = repository.saveProfile(testProfile)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileDao.insertProfile(any()) }
    }

    @Test
    fun `saveProfile returns failure when dao throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { profileDao.insertProfile(any()) } throws exception

        // When
        val result = repository.saveProfile(testProfile)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // deleteProfile Tests
    @Test
    fun `deleteProfile successfully deletes existing profile`() = runTest {
        // Given
        coEvery { profileDao.getProfileById("test-id") } returns testEntity
        coEvery { profileDao.deleteProfile(any()) } just Runs

        // When
        val result = repository.deleteProfile("test-id")

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileDao.deleteProfile(testEntity) }
    }

    @Test
    fun `deleteProfile returns failure when profile not found`() = runTest {
        // Given
        coEvery { profileDao.getProfileById("nonexistent") } returns null

        // When
        val result = repository.deleteProfile("nonexistent")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Profile not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `deleteProfile returns failure when dao throws exception`() = runTest {
        // Given
        coEvery { profileDao.getProfileById("test-id") } returns testEntity
        val exception = RuntimeException("Database error")
        coEvery { profileDao.deleteProfile(any()) } throws exception

        // When
        val result = repository.deleteProfile("test-id")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // activateProfile Tests
    @Test
    fun `activateProfile deactivates all profiles then activates specified profile`() = runTest {
        // Given
        coEvery { profileDao.deactivateAllProfiles() } just Runs
        coEvery { profileDao.activateProfile("test-id") } just Runs

        // When
        val result = repository.activateProfile("test-id")

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileDao.deactivateAllProfiles() }
        coVerify(exactly = 1) { profileDao.activateProfile("test-id") }
    }

    @Test
    fun `activateProfile returns failure when dao throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { profileDao.deactivateAllProfiles() } throws exception

        // When
        val result = repository.activateProfile("test-id")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // deactivateAllProfiles Tests
    @Test
    fun `deactivateAllProfiles successfully deactivates all profiles`() = runTest {
        // Given
        coEvery { profileDao.deactivateAllProfiles() } just Runs

        // When
        val result = repository.deactivateAllProfiles()

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileDao.deactivateAllProfiles() }
    }

    @Test
    fun `deactivateAllProfiles returns failure when dao throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { profileDao.deactivateAllProfiles() } throws exception

        // When
        val result = repository.deactivateAllProfiles()

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // Domain-Entity Mapping Tests
    @Test
    fun `entity to domain mapping preserves all fields`() = runTest {
        // Given
        every { profileDao.getAllProfiles() } returns flowOf(listOf(testEntity))

        // When
        repository.getAllProfiles().test {
            val profiles = awaitItem()
            val profile = profiles.first()

            // Then
            assertEquals(testEntity.id, profile.id)
            assertEquals(testEntity.name, profile.name)
            assertEquals(testEntity.iconName, profile.iconName)
            assertEquals(testEntity.colorHex, profile.colorHex)
            assertEquals(testEntity.isActive, profile.isActive)
            assertEquals(testEntity.isStrictMode, profile.isStrictMode)
            assertEquals(testEntity.blockedApps, profile.blockedApps)
            assertEquals(testEntity.createdAt, profile.createdAt)
            assertEquals(testEntity.updatedAt, profile.updatedAt)
            awaitComplete()
        }
    }
}
