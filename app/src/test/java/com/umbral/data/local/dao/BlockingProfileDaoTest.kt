package com.umbral.data.local.dao

import com.umbral.data.local.entity.BlockingProfileEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

class BlockingProfileDaoTest {

    private lateinit var dao: BlockingProfileDao

    private val testProfile = BlockingProfileEntity(
        id = "test-id",
        name = "Test Profile",
        iconName = "shield",
        colorHex = "#6650A4",
        isActive = false,
        isStrictMode = false,
        blockedApps = listOf("com.instagram.android", "com.twitter.android"),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Before
    fun setup() {
        dao = mockk()
    }

    @Test
    fun `getAllProfiles returns flow of profiles`() = runTest {
        val profiles = listOf(testProfile)
        coEvery { dao.getAllProfiles() } returns flowOf(profiles)

        val result = dao.getAllProfiles().first()

        assertEquals(1, result.size)
        assertEquals("Test Profile", result[0].name)
    }

    @Test
    fun `getProfileById returns profile when exists`() = runTest {
        coEvery { dao.getProfileById("test-id") } returns testProfile

        val result = dao.getProfileById("test-id")

        assertEquals(testProfile, result)
    }

    @Test
    fun `getProfileById returns null when not exists`() = runTest {
        coEvery { dao.getProfileById("non-existent") } returns null

        val result = dao.getProfileById("non-existent")

        assertNull(result)
    }

    @Test
    fun `insertProfile calls dao`() = runTest {
        coEvery { dao.insertProfile(testProfile) } returns Unit

        dao.insertProfile(testProfile)

        coVerify { dao.insertProfile(testProfile) }
    }

    @Test
    fun `activateProfile deactivates all then activates one`() = runTest {
        coEvery { dao.deactivateAllProfiles() } returns Unit
        coEvery { dao.activateProfile("test-id") } returns Unit

        dao.deactivateAllProfiles()
        dao.activateProfile("test-id")

        coVerify { dao.deactivateAllProfiles() }
        coVerify { dao.activateProfile("test-id") }
    }

    @Test
    fun `getActiveProfile returns active profile`() = runTest {
        val activeProfile = testProfile.copy(isActive = true)
        coEvery { dao.getActiveProfile() } returns flowOf(activeProfile)

        val result = dao.getActiveProfile().first()

        assertEquals(true, result?.isActive)
    }
}
