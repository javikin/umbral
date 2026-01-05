package com.umbral.integration

import android.content.Context
import app.cash.turbine.test
import com.umbral.data.blocking.BlockingManagerImpl
import com.umbral.data.blocking.ProfileRepositoryImpl
import com.umbral.data.local.dao.BlockingProfileDao
import com.umbral.data.local.entity.BlockingProfileEntity
import com.umbral.domain.blocking.BlockingManager
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.domain.blocking.ForegroundAppMonitor
import com.umbral.domain.blocking.ProfileRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * Integration tests for the complete blocking flow.
 * Tests the interaction between BlockingManager, ProfileRepository, and DAO layer.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class BlockingFlowIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var context: Context
    private lateinit var profileDao: BlockingProfileDao
    private lateinit var profileRepository: ProfileRepository
    private lateinit var foregroundAppMonitor: ForegroundAppMonitor
    private lateinit var blockingManager: BlockingManager

    private val testProfile1 = BlockingProfile(
        id = "profile-1",
        name = "Work Mode",
        iconName = "work",
        colorHex = "#6650A4",
        isActive = false,
        isStrictMode = false,
        blockedApps = listOf(
            "com.facebook.katana",
            "com.instagram.android",
            "com.twitter.android"
        ),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    private val testProfile2 = BlockingProfile(
        id = "profile-2",
        name = "Focus Mode",
        iconName = "focus",
        colorHex = "#FF5722",
        isActive = false,
        isStrictMode = true,
        blockedApps = listOf(
            "com.youtube.android",
            "com.netflix.mediaclient",
            "com.spotify.music"
        ),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = mockk(relaxed = true)
        profileDao = mockk(relaxed = true)
        foregroundAppMonitor = mockk(relaxed = true)

        every { context.packageName } returns "com.umbral"

        // Initialize repository with DAO
        profileRepository = ProfileRepositoryImpl(profileDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ==================== COMPLETE BLOCKING FLOW TESTS ====================

    @Test
    fun `complete blocking flow from profile activation`() = runTest {
        // Given: Profile exists in database
        val profileEntity = testProfile1.toEntity()
        val activeProfile = testProfile1.copy(isActive = true)
        val activeEntity = activeProfile.toEntity()

        val profileFlow = MutableStateFlow<BlockingProfileEntity?>(null)
        every { profileDao.getActiveProfile() } returns profileFlow
        coEvery { profileDao.deactivateAllProfiles() } answers {
            profileFlow.value = null
        }
        coEvery { profileDao.activateProfile("profile-1") } answers {
            profileFlow.value = activeEntity
        }
        coEvery { profileDao.getProfileById("profile-1") } returns profileEntity

        // Initialize manager
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When: Start blocking
        val startResult = blockingManager.startBlocking("profile-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Blocking is activated
        assertTrue(startResult.isSuccess)
        assertTrue(blockingManager.isBlocking)

        blockingManager.blockingState.test {
            val state = awaitItem()
            assertTrue(state.isActive)
            assertEquals("profile-1", state.activeProfileId)
            assertEquals("Work Mode", state.activeProfileName)
            assertEquals(3, state.blockedApps.size)
            assertTrue(state.blockedApps.contains("com.facebook.katana"))
            assertTrue(state.blockedApps.contains("com.instagram.android"))
            assertTrue(state.blockedApps.contains("com.twitter.android"))
            assertFalse(state.isStrictMode)
        }

        // Verify apps are blocked
        assertTrue(blockingManager.isAppBlocked("com.facebook.katana"))
        assertTrue(blockingManager.isAppBlocked("com.instagram.android"))
        assertFalse(blockingManager.isAppBlocked("com.google.android.apps.maps"))
    }

    @Test
    fun `blocking survives state restoration`() = runTest {
        // Given: Active profile exists
        val activeProfile = testProfile1.copy(isActive = true)
        val activeEntity = activeProfile.toEntity()
        every { profileDao.getActiveProfile() } returns flowOf(activeEntity)

        // When: Manager is initialized (simulating app restart)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Blocking state is restored
        assertTrue(blockingManager.isBlocking)
        blockingManager.blockingState.test {
            val state = awaitItem()
            assertTrue(state.isActive)
            assertEquals("profile-1", state.activeProfileId)
            assertEquals("Work Mode", state.activeProfileName)
            assertEquals(testProfile1.blockedApps.toSet(), state.blockedApps)
        }

        // Apps are still blocked after restoration
        assertTrue(blockingManager.isAppBlocked("com.facebook.katana"))
    }

    @Test
    fun `complete flow - activate then deactivate blocking`() = runTest {
        // Given: Setup with inactive profile
        val profileEntity = testProfile1.toEntity()
        val activeProfile = testProfile1.copy(isActive = true)
        val activeEntity = activeProfile.toEntity()

        val profileFlow = MutableStateFlow<BlockingProfileEntity?>(null)
        every { profileDao.getActiveProfile() } returns profileFlow
        coEvery { profileDao.activateProfile("profile-1") } answers {
            profileFlow.value = activeEntity
        }
        coEvery { profileDao.deactivateAllProfiles() } answers {
            profileFlow.value = null
        }
        coEvery { profileDao.getProfileById("profile-1") } returns profileEntity

        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When: Activate blocking
        val startResult = blockingManager.startBlocking("profile-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Apps are blocked
        assertTrue(startResult.isSuccess)
        assertTrue(blockingManager.isBlocking)
        assertTrue(blockingManager.isAppBlocked("com.facebook.katana"))

        // When: Deactivate blocking
        val stopResult = blockingManager.stopBlocking(requireNfc = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Apps are no longer blocked
        assertTrue(stopResult.isSuccess)
        assertFalse(blockingManager.isBlocking)
        assertFalse(blockingManager.isAppBlocked("com.facebook.katana"))

        blockingManager.blockingState.test {
            val state = awaitItem()
            assertFalse(state.isActive)
            assertNull(state.activeProfileId)
            assertTrue(state.blockedApps.isEmpty())
        }
    }

    @Test
    fun `complete flow - toggle blocking on and off`() = runTest {
        // Given: Setup
        val profileEntity = testProfile1.toEntity()
        val activeProfile = testProfile1.copy(isActive = true)
        val activeEntity = activeProfile.toEntity()

        val profileFlow = MutableStateFlow<BlockingProfileEntity?>(null)
        every { profileDao.getActiveProfile() } returns profileFlow
        coEvery { profileDao.activateProfile("profile-1") } answers {
            profileFlow.value = activeEntity
        }
        coEvery { profileDao.deactivateAllProfiles() } answers {
            profileFlow.value = null
        }
        coEvery { profileDao.getProfileById("profile-1") } returns profileEntity

        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When: Toggle on (from inactive)
        val toggleOnResult = blockingManager.toggleBlocking("profile-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Blocking is active
        assertTrue(toggleOnResult.isSuccess)
        assertTrue(blockingManager.isBlocking)

        // When: Toggle off (same profile)
        val toggleOffResult = blockingManager.toggleBlocking("profile-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Blocking is inactive
        assertTrue(toggleOffResult.isSuccess)
        assertFalse(blockingManager.isBlocking)
    }

    @Test
    fun `complete flow - switch between profiles`() = runTest {
        // Given: Two profiles
        val profile1Entity = testProfile1.toEntity()
        val profile2Entity = testProfile2.toEntity()
        val activeProfile1 = testProfile1.copy(isActive = true)
        val activeProfile2 = testProfile2.copy(isActive = true)

        val profileFlow = MutableStateFlow<BlockingProfileEntity?>(null)
        every { profileDao.getActiveProfile() } returns profileFlow

        coEvery { profileDao.activateProfile("profile-1") } answers {
            profileFlow.value = activeProfile1.toEntity()
        }
        coEvery { profileDao.activateProfile("profile-2") } answers {
            profileFlow.value = activeProfile2.toEntity()
        }
        coEvery { profileDao.deactivateAllProfiles() } answers {
            profileFlow.value = null
        }
        coEvery { profileDao.getProfileById("profile-1") } returns profile1Entity
        coEvery { profileDao.getProfileById("profile-2") } returns profile2Entity

        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When: Activate profile 1
        blockingManager.startBlocking("profile-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Profile 1 apps are blocked
        assertTrue(blockingManager.isAppBlocked("com.facebook.katana"))
        assertFalse(blockingManager.isAppBlocked("com.youtube.android"))

        // When: Switch to profile 2
        blockingManager.startBlocking("profile-2")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Profile 2 apps are blocked, profile 1 apps are not
        assertFalse(blockingManager.isAppBlocked("com.facebook.katana"))
        assertTrue(blockingManager.isAppBlocked("com.youtube.android"))
        assertTrue(blockingManager.isAppBlocked("com.netflix.mediaclient"))

        blockingManager.blockingState.test {
            val state = awaitItem()
            assertTrue(state.isActive)
            assertEquals("profile-2", state.activeProfileId)
            assertTrue(state.isStrictMode)
        }
    }

    @Test
    fun `strict mode prevents deactivation without NFC`() = runTest {
        // Given: Strict profile is active
        val strictProfile = testProfile2.copy(isActive = true)
        every { profileDao.getActiveProfile() } returns flowOf(strictProfile.toEntity())

        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When: Try to stop blocking with requireNfc = true
        val result = blockingManager.stopBlocking(requireNfc = true)

        // Then: Operation fails
        assertTrue(result.isFailure)
        assertTrue(blockingManager.isBlocking)
        assertEquals(
            "Strict mode requires NFC tag to disable blocking",
            result.exceptionOrNull()?.message
        )
    }

    @Test
    fun `multiple rapid blocking state changes are handled correctly`() = runTest {
        // Given: Setup
        val profileFlow = MutableStateFlow<BlockingProfileEntity?>(null)
        every { profileDao.getActiveProfile() } returns profileFlow

        coEvery { profileDao.activateProfile(any()) } answers {
            val profileId = firstArg<String>()
            val profile = if (profileId == "profile-1") testProfile1 else testProfile2
            profileFlow.value = profile.copy(isActive = true).toEntity()
        }
        coEvery { profileDao.deactivateAllProfiles() } answers {
            profileFlow.value = null
        }

        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When: Rapid state changes
        blockingManager.startBlocking("profile-1")
        testDispatcher.scheduler.advanceUntilIdle()

        blockingManager.stopBlocking(requireNfc = false)
        testDispatcher.scheduler.advanceUntilIdle()

        blockingManager.startBlocking("profile-2")
        testDispatcher.scheduler.advanceUntilIdle()

        blockingManager.startBlocking("profile-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Final state is correct (profile-1 active)
        blockingManager.blockingState.test {
            val state = awaitItem()
            assertTrue(state.isActive)
            assertEquals("profile-1", state.activeProfileId)
        }
    }

    @Test
    fun `blocking flow with empty blocked apps list`() = runTest {
        // Given: Profile with no blocked apps
        val emptyProfile = testProfile1.copy(
            id = "empty-profile",
            blockedApps = emptyList()
        )
        val activeEmptyProfile = emptyProfile.copy(isActive = true)

        val profileFlow = MutableStateFlow<BlockingProfileEntity?>(null)
        every { profileDao.getActiveProfile() } returns profileFlow
        coEvery { profileDao.activateProfile("empty-profile") } answers {
            profileFlow.value = activeEmptyProfile.toEntity()
        }

        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When: Activate empty profile
        val result = blockingManager.startBlocking("empty-profile")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Blocking is active but no apps are blocked
        assertTrue(result.isSuccess)
        assertTrue(blockingManager.isBlocking)
        assertFalse(blockingManager.isAppBlocked("com.facebook.katana"))
        assertFalse(blockingManager.isAppBlocked("com.anything.app"))

        blockingManager.blockingState.test {
            val state = awaitItem()
            assertTrue(state.isActive)
            assertTrue(state.blockedApps.isEmpty())
        }
    }

    @Test
    fun `essential system apps are never blocked`() = runTest {
        // Given: Profile with system apps in blocked list
        val profileWithSystemApps = testProfile1.copy(
            blockedApps = testProfile1.blockedApps + listOf(
                "com.android.systemui",
                "com.android.settings",
                "com.android.phone",
                "com.google.android.dialer",
                "com.samsung.android.dialer"
            )
        )
        val activeProfile = profileWithSystemApps.copy(isActive = true)
        every { profileDao.getActiveProfile() } returns flowOf(activeProfile.toEntity())

        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: System apps are not blocked
        assertFalse(blockingManager.isAppBlocked("com.android.systemui"))
        assertFalse(blockingManager.isAppBlocked("com.android.settings"))
        assertFalse(blockingManager.isAppBlocked("com.android.phone"))
        assertFalse(blockingManager.isAppBlocked("com.google.android.dialer"))
        assertFalse(blockingManager.isAppBlocked("com.samsung.android.dialer"))

        // But regular apps are still blocked
        assertTrue(blockingManager.isAppBlocked("com.facebook.katana"))
    }

    @Test
    fun `umbral app itself is never blocked`() = runTest {
        // Given: Profile with Umbral in blocked list
        val profileWithUmbral = testProfile1.copy(
            blockedApps = testProfile1.blockedApps + listOf("com.umbral")
        )
        val activeProfile = profileWithUmbral.copy(isActive = true)
        every { profileDao.getActiveProfile() } returns flowOf(activeProfile.toEntity())
        every { context.packageName } returns "com.umbral"

        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then: Umbral is not blocked
        assertFalse(blockingManager.isAppBlocked("com.umbral"))
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
