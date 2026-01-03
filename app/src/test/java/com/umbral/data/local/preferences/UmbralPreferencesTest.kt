package com.umbral.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class UmbralPreferencesTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder.builder().assureDeletion().build()

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var preferences: UmbralPreferences

    @Before
    fun setup() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test_preferences.preferences_pb") }
        )
        preferences = UmbralPreferences(dataStore)
    }

    @Test
    fun `onboarding completed defaults to false`() = runTest {
        val result = preferences.onboardingCompleted.first()
        assertFalse(result)
    }

    @Test
    fun `setOnboardingCompleted updates value`() = runTest {
        preferences.setOnboardingCompleted(true)
        val result = preferences.onboardingCompleted.first()
        assertTrue(result)
    }

    @Test
    fun `activeProfileId defaults to null`() = runTest {
        val result = preferences.activeProfileId.first()
        assertNull(result)
    }

    @Test
    fun `setActiveProfileId updates value`() = runTest {
        preferences.setActiveProfileId("profile-123")
        val result = preferences.activeProfileId.first()
        assertEquals("profile-123", result)
    }

    @Test
    fun `setActiveProfileId with null removes value`() = runTest {
        preferences.setActiveProfileId("profile-123")
        preferences.setActiveProfileId(null)
        val result = preferences.activeProfileId.first()
        assertNull(result)
    }

    @Test
    fun `blockingEnabled defaults to false`() = runTest {
        val result = preferences.blockingEnabled.first()
        assertFalse(result)
    }

    @Test
    fun `timerDurationSeconds defaults to 30`() = runTest {
        val result = preferences.timerDurationSeconds.first()
        assertEquals(30, result)
    }

    @Test
    fun `setTimerDurationSeconds updates value`() = runTest {
        preferences.setTimerDurationSeconds(60)
        val result = preferences.timerDurationSeconds.first()
        assertEquals(60, result)
    }

    @Test
    fun `darkMode defaults to system`() = runTest {
        val result = preferences.darkMode.first()
        assertEquals("system", result)
    }

    @Test
    fun `hapticFeedback defaults to true`() = runTest {
        val result = preferences.hapticFeedback.first()
        assertTrue(result)
    }

    @Test
    fun `currentStreak defaults to 0`() = runTest {
        val result = preferences.currentStreak.first()
        assertEquals(0, result)
    }

    @Test
    fun `clearAll removes all preferences`() = runTest {
        preferences.setOnboardingCompleted(true)
        preferences.setActiveProfileId("test")
        preferences.setBlockingEnabled(true)

        preferences.clearAll()

        assertFalse(preferences.onboardingCompleted.first())
        assertNull(preferences.activeProfileId.first())
        assertFalse(preferences.blockingEnabled.first())
    }
}
