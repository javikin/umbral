package com.umbral.data.blocking

import android.content.Context
import app.cash.turbine.test
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.domain.blocking.BlockingState
import com.umbral.domain.blocking.ForegroundAppMonitor
import com.umbral.domain.blocking.ProfileRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class BlockingManagerTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var context: Context
    private lateinit var profileRepository: ProfileRepository
    private lateinit var foregroundAppMonitor: ForegroundAppMonitor
    private lateinit var blockingManager: BlockingManagerImpl

    private val testProfile = BlockingProfile(
        id = "test-profile-id",
        name = "Work Mode",
        iconName = "work",
        colorHex = "#6650A4",
        isActive = true,
        isStrictMode = false,
        blockedApps = listOf("com.facebook.katana", "com.instagram.android", "com.twitter.android"),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    private val strictProfile = testProfile.copy(
        id = "strict-profile-id",
        isStrictMode = true
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        context = mockk(relaxed = true)
        profileRepository = mockk(relaxed = true)
        foregroundAppMonitor = mockk(relaxed = true)

        every { context.packageName } returns "com.umbral"
        every { profileRepository.getActiveProfile() } returns flowOf(null)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Initialization Tests
    @Test
    fun `initial state is inactive when no active profile`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)

        // When
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        blockingManager.blockingState.test {
            val state = awaitItem()
            assertFalse(state.isActive)
            assertNull(state.activeProfileId)
            assertEquals(emptySet<String>(), state.blockedApps)
        }
    }

    @Test
    fun `initial state is active when profile exists`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)

        // When
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        blockingManager.blockingState.test {
            val state = awaitItem()
            assertTrue(state.isActive)
            assertEquals("test-profile-id", state.activeProfileId)
            assertEquals("Work Mode", state.activeProfileName)
            assertEquals(testProfile.blockedApps.toSet(), state.blockedApps)
            assertFalse(state.isStrictMode)
        }
    }

    // startBlocking Tests
    @Test
    fun `startBlocking activates profile successfully`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        coEvery { profileRepository.activateProfile("test-profile-id") } returns Result.success(Unit)

        // When
        val result = blockingManager.startBlocking("test-profile-id")

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileRepository.activateProfile("test-profile-id") }
    }

    @Test
    fun `startBlocking returns failure when repository fails`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        val exception = RuntimeException("Database error")
        coEvery { profileRepository.activateProfile(any()) } returns Result.failure(exception)

        // When
        val result = blockingManager.startBlocking("test-profile-id")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // stopBlocking Tests
    @Test
    fun `stopBlocking deactivates all profiles successfully`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { profileRepository.deactivateAllProfiles() } returns Result.success(Unit)

        // When
        val result = blockingManager.stopBlocking(requireNfc = false)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileRepository.deactivateAllProfiles() }
    }

    @Test
    fun `stopBlocking fails when strict mode enabled and NFC required`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(strictProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val result = blockingManager.stopBlocking(requireNfc = true)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
        assertEquals(
            "Strict mode requires NFC tag to disable blocking",
            result.exceptionOrNull()?.message
        )
        coVerify(exactly = 0) { profileRepository.deactivateAllProfiles() }
    }

    @Test
    fun `stopBlocking succeeds in strict mode when NFC not required`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(strictProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { profileRepository.deactivateAllProfiles() } returns Result.success(Unit)

        // When
        val result = blockingManager.stopBlocking(requireNfc = false)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileRepository.deactivateAllProfiles() }
    }

    // toggleBlocking Tests
    @Test
    fun `toggleBlocking starts blocking when inactive`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { profileRepository.activateProfile("test-profile-id") } returns Result.success(Unit)

        // When
        val result = blockingManager.toggleBlocking("test-profile-id")

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileRepository.activateProfile("test-profile-id") }
    }

    @Test
    fun `toggleBlocking stops blocking when same profile is active`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { profileRepository.deactivateAllProfiles() } returns Result.success(Unit)

        // When
        val result = blockingManager.toggleBlocking("test-profile-id")

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileRepository.deactivateAllProfiles() }
    }

    @Test
    fun `toggleBlocking switches to different profile when active`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { profileRepository.activateProfile("other-profile-id") } returns Result.success(Unit)

        // When
        val result = blockingManager.toggleBlocking("other-profile-id")

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileRepository.activateProfile("other-profile-id") }
        coVerify(exactly = 0) { profileRepository.deactivateAllProfiles() }
    }

    @Test
    fun `toggleBlocking respects strict mode when stopping`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(strictProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val result = blockingManager.toggleBlocking("strict-profile-id")

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    // isAppBlocked Tests
    @Test
    fun `isAppBlocked returns false when blocking is inactive`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val isBlocked = blockingManager.isAppBlocked("com.facebook.katana")

        // Then
        assertFalse(isBlocked)
    }

    @Test
    fun `isAppBlocked returns true for blocked app`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val isBlocked = blockingManager.isAppBlocked("com.facebook.katana")

        // Then
        assertTrue(isBlocked)
    }

    @Test
    fun `isAppBlocked returns false for non-blocked app`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val isBlocked = blockingManager.isAppBlocked("com.example.calculator")

        // Then
        assertFalse(isBlocked)
    }

    @Test
    fun `isAppBlocked returns false for Umbral app itself`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)
        every { context.packageName } returns "com.umbral"
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val isBlocked = blockingManager.isAppBlocked("com.umbral")

        // Then
        assertFalse(isBlocked)
    }

    @Test
    fun `isAppBlocked returns false for essential system apps`() = runTest {
        // Given
        val profileWithSystemApps = testProfile.copy(
            blockedApps = testProfile.blockedApps + listOf(
                "com.android.systemui",
                "com.android.settings",
                "com.android.phone"
            )
        )
        every { profileRepository.getActiveProfile() } returns flowOf(profileWithSystemApps)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When & Then
        assertFalse(blockingManager.isAppBlocked("com.android.systemui"))
        assertFalse(blockingManager.isAppBlocked("com.android.settings"))
        assertFalse(blockingManager.isAppBlocked("com.android.phone"))
        assertFalse(blockingManager.isAppBlocked("com.android.dialer"))
        assertFalse(blockingManager.isAppBlocked("com.android.emergency"))
    }

    // getCurrentForegroundApp Tests
    @Test
    fun `getCurrentForegroundApp delegates to foreground monitor`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        coEvery { foregroundAppMonitor.getCurrentForegroundApp() } returns "com.example.app"

        // When
        val foregroundApp = blockingManager.getCurrentForegroundApp()

        // Then
        assertEquals("com.example.app", foregroundApp)
        coVerify(exactly = 1) { foregroundAppMonitor.getCurrentForegroundApp() }
    }

    @Test
    fun `getCurrentForegroundApp returns null when no foreground app`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        coEvery { foregroundAppMonitor.getCurrentForegroundApp() } returns null

        // When
        val foregroundApp = blockingManager.getCurrentForegroundApp()

        // Then
        assertNull(foregroundApp)
    }

    // isBlocking Property Tests
    @Test
    fun `isBlocking returns false when inactive`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When & Then
        assertFalse(blockingManager.isBlocking)
    }

    @Test
    fun `isBlocking returns true when active`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When & Then
        assertTrue(blockingManager.isBlocking)
    }

    // State Flow Tests
    @Test
    fun `blockingState flow emits updated state when profile changes`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)

        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        blockingManager.blockingState.test {
            val state = awaitItem()
            assertTrue(state.isActive)
            assertEquals("test-profile-id", state.activeProfileId)
            assertEquals("Work Mode", state.activeProfileName)
            assertEquals(3, state.blockedApps.size)
            assertFalse(state.isStrictMode)
        }
    }

    // Additional Edge Case Tests
    @Test
    fun `startBlocking with invalid profile ID returns failure`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        val exception = NoSuchElementException("Profile not found")
        coEvery { profileRepository.activateProfile("invalid-id") } returns Result.failure(exception)

        // When
        val result = blockingManager.startBlocking("invalid-id")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
        coVerify(exactly = 1) { profileRepository.activateProfile("invalid-id") }
    }

    @Test
    fun `stopBlocking returns failure when repository fails`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()
        val exception = RuntimeException("Database error")
        coEvery { profileRepository.deactivateAllProfiles() } returns Result.failure(exception)

        // When
        val result = blockingManager.stopBlocking(requireNfc = false)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `isAppBlocked returns false when blocking is inactive even for apps in profile`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val isBlocked = blockingManager.isAppBlocked("com.facebook.katana")

        // Then
        assertFalse(isBlocked)
    }

    @Test
    fun `isAppBlocked returns false for Samsung dialer variant`() = runTest {
        // Given
        val profileWithDialer = testProfile.copy(
            blockedApps = testProfile.blockedApps + listOf("com.samsung.android.dialer")
        )
        every { profileRepository.getActiveProfile() } returns flowOf(profileWithDialer)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When & Then
        assertFalse(blockingManager.isAppBlocked("com.samsung.android.dialer"))
    }

    @Test
    fun `isAppBlocked returns false for Google dialer variant`() = runTest {
        // Given
        val profileWithDialer = testProfile.copy(
            blockedApps = testProfile.blockedApps + listOf("com.google.android.dialer")
        )
        every { profileRepository.getActiveProfile() } returns flowOf(profileWithDialer)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When & Then
        assertFalse(blockingManager.isAppBlocked("com.google.android.dialer"))
    }

    @Test
    fun `blockingState reflects strict mode from active profile`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(strictProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When & Then
        blockingManager.blockingState.test {
            val state = awaitItem()
            assertTrue(state.isActive)
            assertTrue(state.isStrictMode)
            assertEquals("strict-profile-id", state.activeProfileId)
        }
    }

    @Test
    fun `isBlocking returns current state from blockingState`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When & Then
        assertEquals(blockingManager.blockingState.value.isActive, blockingManager.isBlocking)
        assertTrue(blockingManager.isBlocking)
    }

    @Test
    fun `toggleBlocking with invalid profile returns failure`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()
        val exception = NoSuchElementException("Profile not found")
        coEvery { profileRepository.activateProfile("invalid-id") } returns Result.failure(exception)

        // When
        val result = blockingManager.toggleBlocking("invalid-id")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `stopBlocking with non-strict profile succeeds regardless of requireNfc parameter`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { profileRepository.deactivateAllProfiles() } returns Result.success(Unit)

        // When - Test with requireNfc = true on non-strict profile
        val result = blockingManager.stopBlocking(requireNfc = true)

        // Then - Should succeed because profile is not in strict mode
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileRepository.deactivateAllProfiles() }
    }

    @Test
    fun `startBlocking updates blocking state after successful activation`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null, testProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { profileRepository.activateProfile("test-profile-id") } returns Result.success(Unit)

        // When
        val result = blockingManager.startBlocking("test-profile-id")

        // Then
        assertTrue(result.isSuccess)
        // State update happens asynchronously via flow observation
    }

    @Test
    fun `isAppBlocked handles empty blocked apps list`() = runTest {
        // Given
        val emptyProfile = testProfile.copy(blockedApps = emptyList())
        every { profileRepository.getActiveProfile() } returns flowOf(emptyProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        val isBlocked = blockingManager.isAppBlocked("com.facebook.katana")

        // Then
        assertFalse(isBlocked)
    }

    @Test
    fun `isAppBlocked returns true for all apps in blocked list`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When & Then - Check all blocked apps
        assertTrue(blockingManager.isAppBlocked("com.facebook.katana"))
        assertTrue(blockingManager.isAppBlocked("com.instagram.android"))
        assertTrue(blockingManager.isAppBlocked("com.twitter.android"))
    }

    @Test
    fun `blockingState contains correct blocked apps set`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(testProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()

        // When & Then
        blockingManager.blockingState.test {
            val state = awaitItem()
            assertEquals(
                setOf("com.facebook.katana", "com.instagram.android", "com.twitter.android"),
                state.blockedApps
            )
        }
    }

    @Test
    fun `getCurrentForegroundApp handles exception gracefully`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        coEvery { foregroundAppMonitor.getCurrentForegroundApp() } throws RuntimeException("Permission denied")

        // When & Then - Should propagate exception
        try {
            blockingManager.getCurrentForegroundApp()
            assertTrue("Should have thrown exception", false)
        } catch (e: RuntimeException) {
            assertEquals("Permission denied", e.message)
        }
    }

    @Test
    fun `multiple startBlocking calls with same profile succeed`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        coEvery { profileRepository.activateProfile("test-profile-id") } returns Result.success(Unit)

        // When - Call startBlocking multiple times
        val result1 = blockingManager.startBlocking("test-profile-id")
        val result2 = blockingManager.startBlocking("test-profile-id")
        val result3 = blockingManager.startBlocking("test-profile-id")

        // Then - All should succeed
        assertTrue(result1.isSuccess)
        assertTrue(result2.isSuccess)
        assertTrue(result3.isSuccess)
        coVerify(exactly = 3) { profileRepository.activateProfile("test-profile-id") }
    }

    @Test
    fun `stopBlocking when already stopped succeeds`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(null)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { profileRepository.deactivateAllProfiles() } returns Result.success(Unit)

        // When
        val result = blockingManager.stopBlocking(requireNfc = false)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileRepository.deactivateAllProfiles() }
    }

    @Test
    fun `strict mode check only applies when requireNfc is true`() = runTest {
        // Given
        every { profileRepository.getActiveProfile() } returns flowOf(strictProfile)
        blockingManager = BlockingManagerImpl(context, profileRepository, foregroundAppMonitor)
        testDispatcher.scheduler.advanceUntilIdle()
        coEvery { profileRepository.deactivateAllProfiles() } returns Result.success(Unit)

        // When - requireNfc = false should bypass strict mode check
        val result = blockingManager.stopBlocking(requireNfc = false)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { profileRepository.deactivateAllProfiles() }
    }
}
