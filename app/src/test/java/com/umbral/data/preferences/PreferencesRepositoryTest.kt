package com.umbral.data.preferences

import app.cash.turbine.test
import com.umbral.data.local.preferences.UmbralPreferences
import com.umbral.domain.preferences.DarkMode
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PreferencesRepositoryTest {

    private lateinit var umbralPreferences: UmbralPreferences
    private lateinit var repository: PreferencesRepositoryImpl

    @Before
    fun setup() {
        umbralPreferences = mockk(relaxed = true)
        repository = PreferencesRepositoryImpl(umbralPreferences)
    }

    // getPreferences Tests
    @Test
    fun `getPreferences combines all preference flows`() = runTest {
        // Given
        every { umbralPreferences.onboardingCompleted } returns flowOf(true)
        every { umbralPreferences.activeProfileId } returns flowOf("profile-1")
        every { umbralPreferences.blockingEnabled } returns flowOf(true)
        every { umbralPreferences.timerDurationSeconds } returns flowOf(60)
        every { umbralPreferences.strictModeDefault } returns flowOf(true)
        every { umbralPreferences.darkMode } returns flowOf("dark")
        every { umbralPreferences.hapticFeedback } returns flowOf(false)
        every { umbralPreferences.currentStreak } returns flowOf(5)
        every { umbralPreferences.lastActiveDate } returns flowOf("2024-01-15")

        // When
        repository.getPreferences().test {
            val prefs = awaitItem()

            // Then
            assertTrue(prefs.onboardingCompleted)
            assertEquals("profile-1", prefs.activeProfileId)
            assertTrue(prefs.blockingEnabled)
            assertEquals(60, prefs.timerDurationSeconds)
            assertTrue(prefs.strictModeDefault)
            assertEquals(DarkMode.DARK, prefs.darkMode)
            assertFalse(prefs.hapticFeedback)
            assertEquals(5, prefs.currentStreak)
            assertEquals("2024-01-15", prefs.lastActiveDate)
            awaitComplete()
        }
    }

    @Test
    fun `getPreferences returns default values when not set`() = runTest {
        // Given
        every { umbralPreferences.onboardingCompleted } returns flowOf(false)
        every { umbralPreferences.activeProfileId } returns flowOf(null)
        every { umbralPreferences.blockingEnabled } returns flowOf(false)
        every { umbralPreferences.timerDurationSeconds } returns flowOf(30)
        every { umbralPreferences.strictModeDefault } returns flowOf(false)
        every { umbralPreferences.darkMode } returns flowOf("system")
        every { umbralPreferences.hapticFeedback } returns flowOf(true)
        every { umbralPreferences.currentStreak } returns flowOf(0)
        every { umbralPreferences.lastActiveDate } returns flowOf(null)

        // When
        repository.getPreferences().test {
            val prefs = awaitItem()

            // Then
            assertFalse(prefs.onboardingCompleted)
            assertNull(prefs.activeProfileId)
            assertFalse(prefs.blockingEnabled)
            assertEquals(30, prefs.timerDurationSeconds)
            assertFalse(prefs.strictModeDefault)
            assertEquals(DarkMode.SYSTEM, prefs.darkMode)
            assertTrue(prefs.hapticFeedback)
            assertEquals(0, prefs.currentStreak)
            assertNull(prefs.lastActiveDate)
            awaitComplete()
        }
    }

    // Onboarding Tests
    @Test
    fun `isOnboardingCompleted returns flow from preferences`() = runTest {
        // Given
        every { umbralPreferences.onboardingCompleted } returns flowOf(true)

        // When
        repository.isOnboardingCompleted().test {
            // Then
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `setOnboardingCompleted updates preferences`() = runTest {
        // Given
        coEvery { umbralPreferences.setOnboardingCompleted(true) } just Runs

        // When
        repository.setOnboardingCompleted(true)

        // Then
        coVerify(exactly = 1) { umbralPreferences.setOnboardingCompleted(true) }
    }

    @Test
    fun `setOnboardingCompleted propagates exception`() = runTest {
        // Given
        val exception = RuntimeException("DataStore error")
        coEvery { umbralPreferences.setOnboardingCompleted(any()) } throws exception

        // When/Then
        try {
            repository.setOnboardingCompleted(true)
            throw AssertionError("Expected exception to be thrown")
        } catch (e: Exception) {
            assertEquals(exception, e)
        }
    }

    // Active Profile Tests
    @Test
    fun `getActiveProfileId returns flow from preferences`() = runTest {
        // Given
        every { umbralPreferences.activeProfileId } returns flowOf("profile-1")

        // When
        repository.getActiveProfileId().test {
            // Then
            assertEquals("profile-1", awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `setActiveProfileId updates preferences with non-null value`() = runTest {
        // Given
        coEvery { umbralPreferences.setActiveProfileId("profile-1") } just Runs

        // When
        repository.setActiveProfileId("profile-1")

        // Then
        coVerify(exactly = 1) { umbralPreferences.setActiveProfileId("profile-1") }
    }

    @Test
    fun `setActiveProfileId updates preferences with null value`() = runTest {
        // Given
        coEvery { umbralPreferences.setActiveProfileId(null) } just Runs

        // When
        repository.setActiveProfileId(null)

        // Then
        coVerify(exactly = 1) { umbralPreferences.setActiveProfileId(null) }
    }

    // Blocking Enabled Tests
    @Test
    fun `isBlockingEnabled returns flow from preferences`() = runTest {
        // Given
        every { umbralPreferences.blockingEnabled } returns flowOf(true)

        // When
        repository.isBlockingEnabled().test {
            // Then
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `setBlockingEnabled updates preferences`() = runTest {
        // Given
        coEvery { umbralPreferences.setBlockingEnabled(true) } just Runs

        // When
        repository.setBlockingEnabled(true)

        // Then
        coVerify(exactly = 1) { umbralPreferences.setBlockingEnabled(true) }
    }

    // Timer Duration Tests
    @Test
    fun `getTimerDurationSeconds returns flow from preferences`() = runTest {
        // Given
        every { umbralPreferences.timerDurationSeconds } returns flowOf(60)

        // When
        repository.getTimerDurationSeconds().test {
            // Then
            assertEquals(60, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `setTimerDurationSeconds updates preferences with valid value`() = runTest {
        // Given
        coEvery { umbralPreferences.setTimerDurationSeconds(60) } just Runs

        // When
        repository.setTimerDurationSeconds(60)

        // Then
        coVerify(exactly = 1) { umbralPreferences.setTimerDurationSeconds(60) }
    }

    @Test
    fun `setTimerDurationSeconds throws exception for non-positive value`() = runTest {
        // When/Then
        try {
            repository.setTimerDurationSeconds(0)
            throw AssertionError("Expected exception to be thrown")
        } catch (e: IllegalArgumentException) {
            assertEquals("Timer duration must be positive", e.message)
        }

        try {
            repository.setTimerDurationSeconds(-10)
            throw AssertionError("Expected exception to be thrown")
        } catch (e: IllegalArgumentException) {
            assertEquals("Timer duration must be positive", e.message)
        }
    }

    // Strict Mode Tests
    @Test
    fun `isStrictModeDefault returns flow from preferences`() = runTest {
        // Given
        every { umbralPreferences.strictModeDefault } returns flowOf(true)

        // When
        repository.isStrictModeDefault().test {
            // Then
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `setStrictModeDefault updates preferences`() = runTest {
        // Given
        coEvery { umbralPreferences.setStrictModeDefault(true) } just Runs

        // When
        repository.setStrictModeDefault(true)

        // Then
        coVerify(exactly = 1) { umbralPreferences.setStrictModeDefault(true) }
    }

    // Dark Mode Tests
    @Test
    fun `getDarkMode returns system mode from preferences`() = runTest {
        // Given
        every { umbralPreferences.darkMode } returns flowOf("system")

        // When
        repository.getDarkMode().test {
            // Then
            assertEquals(DarkMode.SYSTEM, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getDarkMode returns light mode from preferences`() = runTest {
        // Given
        every { umbralPreferences.darkMode } returns flowOf("light")

        // When
        repository.getDarkMode().test {
            // Then
            assertEquals(DarkMode.LIGHT, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `getDarkMode returns dark mode from preferences`() = runTest {
        // Given
        every { umbralPreferences.darkMode } returns flowOf("dark")

        // When
        repository.getDarkMode().test {
            // Then
            assertEquals(DarkMode.DARK, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `setDarkMode updates preferences with system mode`() = runTest {
        // Given
        coEvery { umbralPreferences.setDarkMode("system") } just Runs

        // When
        repository.setDarkMode(DarkMode.SYSTEM)

        // Then
        coVerify(exactly = 1) { umbralPreferences.setDarkMode("system") }
    }

    @Test
    fun `setDarkMode updates preferences with light mode`() = runTest {
        // Given
        coEvery { umbralPreferences.setDarkMode("light") } just Runs

        // When
        repository.setDarkMode(DarkMode.LIGHT)

        // Then
        coVerify(exactly = 1) { umbralPreferences.setDarkMode("light") }
    }

    @Test
    fun `setDarkMode updates preferences with dark mode`() = runTest {
        // Given
        coEvery { umbralPreferences.setDarkMode("dark") } just Runs

        // When
        repository.setDarkMode(DarkMode.DARK)

        // Then
        coVerify(exactly = 1) { umbralPreferences.setDarkMode("dark") }
    }

    // Haptic Feedback Tests
    @Test
    fun `isHapticFeedbackEnabled returns flow from preferences`() = runTest {
        // Given
        every { umbralPreferences.hapticFeedback } returns flowOf(false)

        // When
        repository.isHapticFeedbackEnabled().test {
            // Then
            assertFalse(awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `setHapticFeedback updates preferences`() = runTest {
        // Given
        coEvery { umbralPreferences.setHapticFeedback(false) } just Runs

        // When
        repository.setHapticFeedback(false)

        // Then
        coVerify(exactly = 1) { umbralPreferences.setHapticFeedback(false) }
    }

    // Streak Tests
    @Test
    fun `getCurrentStreak returns flow from preferences`() = runTest {
        // Given
        every { umbralPreferences.currentStreak } returns flowOf(10)

        // When
        repository.getCurrentStreak().test {
            // Then
            assertEquals(10, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `setCurrentStreak updates preferences with valid value`() = runTest {
        // Given
        coEvery { umbralPreferences.setCurrentStreak(10) } just Runs

        // When
        repository.setCurrentStreak(10)

        // Then
        coVerify(exactly = 1) { umbralPreferences.setCurrentStreak(10) }
    }

    @Test
    fun `setCurrentStreak throws exception for negative value`() = runTest {
        // When/Then
        try {
            repository.setCurrentStreak(-1)
            throw AssertionError("Expected exception to be thrown")
        } catch (e: IllegalArgumentException) {
            assertEquals("Streak must be non-negative", e.message)
        }
    }

    @Test
    fun `setCurrentStreak allows zero value`() = runTest {
        // Given
        coEvery { umbralPreferences.setCurrentStreak(0) } just Runs

        // When
        repository.setCurrentStreak(0)

        // Then
        coVerify(exactly = 1) { umbralPreferences.setCurrentStreak(0) }
    }

    // Last Active Date Tests
    @Test
    fun `getLastActiveDate returns flow from preferences`() = runTest {
        // Given
        every { umbralPreferences.lastActiveDate } returns flowOf("2024-01-15")

        // When
        repository.getLastActiveDate().test {
            // Then
            assertEquals("2024-01-15", awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `setLastActiveDate updates preferences`() = runTest {
        // Given
        coEvery { umbralPreferences.setLastActiveDate("2024-01-15") } just Runs

        // When
        repository.setLastActiveDate("2024-01-15")

        // Then
        coVerify(exactly = 1) { umbralPreferences.setLastActiveDate("2024-01-15") }
    }

    // Clear All Tests
    @Test
    fun `clearAll clears all preferences`() = runTest {
        // Given
        coEvery { umbralPreferences.clearAll() } just Runs

        // When
        repository.clearAll()

        // Then
        coVerify(exactly = 1) { umbralPreferences.clearAll() }
    }

    @Test
    fun `clearAll propagates exception`() = runTest {
        // Given
        val exception = RuntimeException("DataStore error")
        coEvery { umbralPreferences.clearAll() } throws exception

        // When/Then
        try {
            repository.clearAll()
            throw AssertionError("Expected exception to be thrown")
        } catch (e: Exception) {
            assertEquals(exception, e)
        }
    }

    // DarkMode Enum Tests
    @Test
    fun `DarkMode fromString parses system correctly`() {
        assertEquals(DarkMode.SYSTEM, DarkMode.fromString("system"))
        assertEquals(DarkMode.SYSTEM, DarkMode.fromString("SYSTEM"))
        assertEquals(DarkMode.SYSTEM, DarkMode.fromString("System"))
    }

    @Test
    fun `DarkMode fromString parses light correctly`() {
        assertEquals(DarkMode.LIGHT, DarkMode.fromString("light"))
        assertEquals(DarkMode.LIGHT, DarkMode.fromString("LIGHT"))
        assertEquals(DarkMode.LIGHT, DarkMode.fromString("Light"))
    }

    @Test
    fun `DarkMode fromString parses dark correctly`() {
        assertEquals(DarkMode.DARK, DarkMode.fromString("dark"))
        assertEquals(DarkMode.DARK, DarkMode.fromString("DARK"))
        assertEquals(DarkMode.DARK, DarkMode.fromString("Dark"))
    }

    @Test
    fun `DarkMode fromString defaults to system for unknown values`() {
        assertEquals(DarkMode.SYSTEM, DarkMode.fromString("unknown"))
        assertEquals(DarkMode.SYSTEM, DarkMode.fromString(""))
        assertEquals(DarkMode.SYSTEM, DarkMode.fromString("invalid"))
    }

    @Test
    fun `DarkMode toString returns lowercase name`() {
        assertEquals("system", DarkMode.SYSTEM.toString())
        assertEquals("light", DarkMode.LIGHT.toString())
        assertEquals("dark", DarkMode.DARK.toString())
    }
}
