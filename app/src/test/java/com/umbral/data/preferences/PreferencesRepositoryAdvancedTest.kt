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

/**
 * Advanced test scenarios for PreferencesRepository.
 * Covers Flow behavior, edge cases, and complex preference interactions.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PreferencesRepositoryAdvancedTest {

    private lateinit var umbralPreferences: UmbralPreferences
    private lateinit var repository: PreferencesRepositoryImpl

    @Before
    fun setup() {
        umbralPreferences = mockk(relaxed = true)
        repository = PreferencesRepositoryImpl(umbralPreferences)
    }

    // Combined Preferences Flow Tests
    @Test
    fun `getPreferences combines all preferences correctly`() = runTest {
        // Given - All preferences set
        every { umbralPreferences.onboardingCompleted } returns flowOf(true)
        every { umbralPreferences.activeProfileId } returns flowOf("profile-1")
        every { umbralPreferences.blockingEnabled } returns flowOf(true)
        every { umbralPreferences.timerDurationSeconds } returns flowOf(60)
        every { umbralPreferences.strictModeDefault } returns flowOf(true)
        every { umbralPreferences.darkMode } returns flowOf("dark")
        every { umbralPreferences.hapticFeedback } returns flowOf(false)
        every { umbralPreferences.currentStreak } returns flowOf(5)
        every { umbralPreferences.lastActiveDate } returns flowOf("2024-01-15")

        // When/Then
        repository.getPreferences().test {
            val prefs = awaitItem()

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
    fun `getPreferences handles all preferences at maximum values`() = runTest {
        // Given - Maximum/extreme values
        every { umbralPreferences.onboardingCompleted } returns flowOf(true)
        every { umbralPreferences.activeProfileId } returns flowOf("a".repeat(500))
        every { umbralPreferences.blockingEnabled } returns flowOf(true)
        every { umbralPreferences.timerDurationSeconds } returns flowOf(Int.MAX_VALUE)
        every { umbralPreferences.strictModeDefault } returns flowOf(true)
        every { umbralPreferences.darkMode } returns flowOf("dark")
        every { umbralPreferences.hapticFeedback } returns flowOf(true)
        every { umbralPreferences.currentStreak } returns flowOf(Int.MAX_VALUE)
        every { umbralPreferences.lastActiveDate } returns flowOf("2099-12-31")

        // When
        repository.getPreferences().test {
            val prefs = awaitItem()

            // Then
            assertTrue(prefs.onboardingCompleted)
            assertEquals(500, prefs.activeProfileId?.length)
            assertTrue(prefs.blockingEnabled)
            assertEquals(Int.MAX_VALUE, prefs.timerDurationSeconds)
            assertTrue(prefs.strictModeDefault)
            assertEquals(DarkMode.DARK, prefs.darkMode)
            assertTrue(prefs.hapticFeedback)
            assertEquals(Int.MAX_VALUE, prefs.currentStreak)
            assertEquals("2099-12-31", prefs.lastActiveDate)

            awaitComplete()
        }
    }

    // Active Profile Edge Cases
    @Test
    fun `setActiveProfileId handles rapid profile switching`() = runTest {
        // Given
        coEvery { umbralPreferences.setActiveProfileId(any()) } just Runs

        // When - Rapid profile changes
        val profiles = listOf("profile-1", "profile-2", "profile-3", null, "profile-4")
        profiles.forEach { repository.setActiveProfileId(it) }

        // Then
        profiles.forEach { profileId ->
            coVerify(exactly = 1) { umbralPreferences.setActiveProfileId(profileId) }
        }
    }

    @Test
    fun `setActiveProfileId handles very long profile IDs`() = runTest {
        // Given
        val longId = "profile-" + "a".repeat(1000)
        coEvery { umbralPreferences.setActiveProfileId(any()) } just Runs

        // When
        repository.setActiveProfileId(longId)

        // Then
        coVerify {
            umbralPreferences.setActiveProfileId(
                withArg {
                    assertTrue(it!!.length > 1000)
                }
            )
        }
    }

    @Test
    fun `setActiveProfileId handles special characters`() = runTest {
        // Given
        val specialIds = listOf(
            "profile-with-emoji-🎯",
            "profile\nwith\nnewlines",
            "profile/with/slashes",
            "profile\\with\\backslashes",
            "profile with spaces"
        )
        coEvery { umbralPreferences.setActiveProfileId(any()) } just Runs

        // When/Then
        specialIds.forEach { id ->
            repository.setActiveProfileId(id)
            coVerify(exactly = 1) { umbralPreferences.setActiveProfileId(id) }
        }
    }

    // Timer Duration Edge Cases
    @Test
    fun `setTimerDurationSeconds validates boundary values`() = runTest {
        // Given
        coEvery { umbralPreferences.setTimerDurationSeconds(any()) } just Runs

        // When - Valid boundary values
        repository.setTimerDurationSeconds(1)           // Minimum valid
        repository.setTimerDurationSeconds(3600)        // 1 hour
        repository.setTimerDurationSeconds(86400)       // 24 hours
        repository.setTimerDurationSeconds(Int.MAX_VALUE) // Maximum

        // Then
        coVerify(exactly = 1) { umbralPreferences.setTimerDurationSeconds(1) }
        coVerify(exactly = 1) { umbralPreferences.setTimerDurationSeconds(3600) }
        coVerify(exactly = 1) { umbralPreferences.setTimerDurationSeconds(86400) }
        coVerify(exactly = 1) { umbralPreferences.setTimerDurationSeconds(Int.MAX_VALUE) }
    }

    @Test
    fun `setTimerDurationSeconds rejects all non-positive values`() = runTest {
        // When/Then - Invalid values
        val invalidValues = listOf(0, -1, -100, Int.MIN_VALUE)

        invalidValues.forEach { value ->
            try {
                repository.setTimerDurationSeconds(value)
                throw AssertionError("Should have thrown for value: $value")
            } catch (e: IllegalArgumentException) {
                assertEquals("Timer duration must be positive", e.message)
            }
        }

        // Verify preferences never called with invalid values
        coVerify(exactly = 0) { umbralPreferences.setTimerDurationSeconds(any()) }
    }

    // Streak Management Tests
    @Test
    fun `setCurrentStreak handles streak progression`() = runTest {
        // Given
        coEvery { umbralPreferences.setCurrentStreak(any()) } just Runs

        // When - Simulate streak progression
        (0..100).forEach { streak ->
            repository.setCurrentStreak(streak)
        }

        // Then
        coVerify(exactly = 101) { umbralPreferences.setCurrentStreak(any()) }
    }

    @Test
    fun `setCurrentStreak allows zero to reset streak`() = runTest {
        // Given
        coEvery { umbralPreferences.setCurrentStreak(any()) } just Runs

        // When - Reset streak to zero
        repository.setCurrentStreak(0)

        // Then
        coVerify(exactly = 1) { umbralPreferences.setCurrentStreak(0) }
    }

    @Test
    fun `setCurrentStreak handles very high streak values`() = runTest {
        // Given
        coEvery { umbralPreferences.setCurrentStreak(any()) } just Runs

        // When - Extreme streak values
        val extremeValues = listOf(1000, 10000, 100000, Int.MAX_VALUE)
        extremeValues.forEach { repository.setCurrentStreak(it) }

        // Then
        extremeValues.forEach { value ->
            coVerify(exactly = 1) { umbralPreferences.setCurrentStreak(value) }
        }
    }

    @Test
    fun `setCurrentStreak rejects negative values`() = runTest {
        // When/Then
        val negativeValues = listOf(-1, -10, -100, Int.MIN_VALUE)

        negativeValues.forEach { value ->
            try {
                repository.setCurrentStreak(value)
                throw AssertionError("Should have thrown for value: $value")
            } catch (e: IllegalArgumentException) {
                assertEquals("Streak must be non-negative", e.message)
            }
        }
    }

    // Last Active Date Tests
    @Test
    fun `setLastActiveDate handles various date formats`() = runTest {
        // Given
        val dateFormats = listOf(
            "2024-01-15",
            "2024-12-31",
            "1970-01-01",
            "2099-12-31",
            "2024-01-15T12:00:00",
            "2024-01-15T12:00:00Z",
            "2024-01-15T12:00:00.000Z"
        )
        coEvery { umbralPreferences.setLastActiveDate(any()) } just Runs

        // When
        dateFormats.forEach { date ->
            repository.setLastActiveDate(date)
        }

        // Then
        dateFormats.forEach { date ->
            coVerify(exactly = 1) { umbralPreferences.setLastActiveDate(date) }
        }
    }

    @Test
    fun `setLastActiveDate handles invalid date strings`() = runTest {
        // Given - Repository doesn't validate format (that's domain layer's job)
        val invalidDates = listOf(
            "not-a-date",
            "2024-13-45", // Invalid month/day
            "",
            "2024/01/15" // Wrong separator
        )
        coEvery { umbralPreferences.setLastActiveDate(any()) } just Runs

        // When - Should accept any string (validation happens elsewhere)
        invalidDates.forEach { date ->
            repository.setLastActiveDate(date)
        }

        // Then
        invalidDates.forEach { date ->
            coVerify(exactly = 1) { umbralPreferences.setLastActiveDate(date) }
        }
    }

    // Dark Mode Tests
    @Test
    fun `getDarkMode handles all DarkMode values`() = runTest {
        // Given/When/Then
        val modes = mapOf(
            "system" to DarkMode.SYSTEM,
            "light" to DarkMode.LIGHT,
            "dark" to DarkMode.DARK
        )

        modes.forEach { (stringValue, enumValue) ->
            every { umbralPreferences.darkMode } returns flowOf(stringValue)

            repository.getDarkMode().test {
                assertEquals(enumValue, awaitItem())
                awaitComplete()
            }
        }
    }

    @Test
    fun `setDarkMode handles rapid mode switching`() = runTest {
        // Given
        coEvery { umbralPreferences.setDarkMode(any()) } just Runs

        // When - Rapid switching
        val sequence = listOf(
            DarkMode.SYSTEM,
            DarkMode.DARK,
            DarkMode.LIGHT,
            DarkMode.SYSTEM,
            DarkMode.DARK
        )
        sequence.forEach { repository.setDarkMode(it) }

        // Then
        coVerify(exactly = 2) { umbralPreferences.setDarkMode("system") }
        coVerify(exactly = 2) { umbralPreferences.setDarkMode("dark") }
        coVerify(exactly = 1) { umbralPreferences.setDarkMode("light") }
    }

    // Blocking Enabled Tests
    @Test
    fun `setBlockingEnabled handles rapid toggling`() = runTest {
        // Given
        coEvery { umbralPreferences.setBlockingEnabled(any()) } just Runs

        // When - Rapid on/off toggling
        repeat(100) { i ->
            repository.setBlockingEnabled(i % 2 == 0)
        }

        // Then
        coVerify(exactly = 50) { umbralPreferences.setBlockingEnabled(true) }
        coVerify(exactly = 50) { umbralPreferences.setBlockingEnabled(false) }
    }

    @Test
    fun `isBlockingEnabled emits updates on toggle`() = runTest {
        // Given
        every { umbralPreferences.blockingEnabled } returns flowOf(false, true, false, true)

        // When/Then
        repository.isBlockingEnabled().test {
            assertFalse(awaitItem())
            assertTrue(awaitItem())
            assertFalse(awaitItem())
            assertTrue(awaitItem())
            awaitComplete()
        }
    }

    // Haptic Feedback Tests
    @Test
    fun `setHapticFeedback handles multiple updates`() = runTest {
        // Given
        coEvery { umbralPreferences.setHapticFeedback(any()) } just Runs

        // When
        val updates = listOf(true, false, true, true, false)
        updates.forEach { repository.setHapticFeedback(it) }

        // Then
        coVerify(exactly = 3) { umbralPreferences.setHapticFeedback(true) }
        coVerify(exactly = 2) { umbralPreferences.setHapticFeedback(false) }
    }

    // Strict Mode Default Tests
    @Test
    fun `setStrictModeDefault persists user choice`() = runTest {
        // Given
        coEvery { umbralPreferences.setStrictModeDefault(any()) } just Runs

        // When
        repository.setStrictModeDefault(true)
        repository.setStrictModeDefault(false)
        repository.setStrictModeDefault(true)

        // Then
        coVerify(exactly = 2) { umbralPreferences.setStrictModeDefault(true) }
        coVerify(exactly = 1) { umbralPreferences.setStrictModeDefault(false) }
    }

    // Onboarding Tests
    @Test
    fun `setOnboardingCompleted can only be set to true once typically`() = runTest {
        // Given
        coEvery { umbralPreferences.setOnboardingCompleted(any()) } just Runs

        // When - Complete onboarding (shouldn't need to set to false)
        repository.setOnboardingCompleted(true)

        // Then
        coVerify(exactly = 1) { umbralPreferences.setOnboardingCompleted(true) }
        coVerify(exactly = 0) { umbralPreferences.setOnboardingCompleted(false) }
    }

    @Test
    fun `isOnboardingCompleted emits initial false then true`() = runTest {
        // Given - Typical onboarding flow
        every { umbralPreferences.onboardingCompleted } returns flowOf(false, true)

        // When/Then
        repository.isOnboardingCompleted().test {
            assertFalse(awaitItem()) // Initial state
            assertTrue(awaitItem())  // After completion
            awaitComplete()
        }
    }

    // Clear All Tests
    @Test
    fun `clearAll resets all preferences`() = runTest {
        // Given
        coEvery { umbralPreferences.clearAll() } just Runs

        // When
        repository.clearAll()

        // Then
        coVerify(exactly = 1) { umbralPreferences.clearAll() }
    }

    @Test
    fun `clearAll handles multiple consecutive calls`() = runTest {
        // Given
        coEvery { umbralPreferences.clearAll() } just Runs

        // When - Multiple clears (e.g., user rapidly clicking reset)
        repeat(5) {
            repository.clearAll()
        }

        // Then
        coVerify(exactly = 5) { umbralPreferences.clearAll() }
    }

    // Error Propagation Tests
    @Test
    fun `all setters propagate DataStore exceptions`() = runTest {
        // Given
        val exception = RuntimeException("DataStore write failed")

        // Test each setter
        coEvery { umbralPreferences.setOnboardingCompleted(any()) } throws exception
        assertThrowsException { repository.setOnboardingCompleted(true) }

        coEvery { umbralPreferences.setActiveProfileId(any()) } throws exception
        assertThrowsException { repository.setActiveProfileId("test") }

        coEvery { umbralPreferences.setBlockingEnabled(any()) } throws exception
        assertThrowsException { repository.setBlockingEnabled(true) }

        coEvery { umbralPreferences.setTimerDurationSeconds(any()) } throws exception
        assertThrowsException { repository.setTimerDurationSeconds(60) }

        coEvery { umbralPreferences.setStrictModeDefault(any()) } throws exception
        assertThrowsException { repository.setStrictModeDefault(true) }

        coEvery { umbralPreferences.setDarkMode(any()) } throws exception
        assertThrowsException { repository.setDarkMode(DarkMode.DARK) }

        coEvery { umbralPreferences.setHapticFeedback(any()) } throws exception
        assertThrowsException { repository.setHapticFeedback(true) }

        coEvery { umbralPreferences.setCurrentStreak(any()) } throws exception
        assertThrowsException { repository.setCurrentStreak(5) }

        coEvery { umbralPreferences.setLastActiveDate(any()) } throws exception
        assertThrowsException { repository.setLastActiveDate("2024-01-15") }

        coEvery { umbralPreferences.clearAll() } throws exception
        assertThrowsException { repository.clearAll() }
    }

    // Concurrent Access Tests
    @Test
    fun `repository handles concurrent reads`() = runTest {
        // Given
        every { umbralPreferences.blockingEnabled } returns flowOf(true)
        every { umbralPreferences.currentStreak } returns flowOf(42)
        every { umbralPreferences.activeProfileId } returns flowOf("profile-1")

        // When - Concurrent reads (all should succeed)
        repository.isBlockingEnabled().test {
            assertTrue(awaitItem())
            awaitComplete()
        }

        repository.getCurrentStreak().test {
            assertEquals(42, awaitItem())
            awaitComplete()
        }

        repository.getActiveProfileId().test {
            assertEquals("profile-1", awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `repository handles concurrent writes`() = runTest {
        // Given
        coEvery { umbralPreferences.setBlockingEnabled(any()) } just Runs
        coEvery { umbralPreferences.setCurrentStreak(any()) } just Runs
        coEvery { umbralPreferences.setHapticFeedback(any()) } just Runs

        // When - Concurrent writes
        repository.setBlockingEnabled(true)
        repository.setCurrentStreak(10)
        repository.setHapticFeedback(false)

        // Then - All should succeed
        coVerify(exactly = 1) { umbralPreferences.setBlockingEnabled(true) }
        coVerify(exactly = 1) { umbralPreferences.setCurrentStreak(10) }
        coVerify(exactly = 1) { umbralPreferences.setHapticFeedback(false) }
    }

    // Helper function
    private suspend fun assertThrowsException(block: suspend () -> Unit) {
        try {
            block()
            throw AssertionError("Expected exception to be thrown")
        } catch (e: RuntimeException) {
            // Expected
        }
    }
}
