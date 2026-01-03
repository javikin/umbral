package com.umbral.data.local.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UmbralPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // Keys
    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val ACTIVE_PROFILE_ID = stringPreferencesKey("active_profile_id")
        val BLOCKING_ENABLED = booleanPreferencesKey("blocking_enabled")
        val TIMER_DURATION_SECONDS = intPreferencesKey("timer_duration_seconds")
        val STRICT_MODE_DEFAULT = booleanPreferencesKey("strict_mode_default")
        val DARK_MODE = stringPreferencesKey("dark_mode") // "system", "light", "dark"
        val HAPTIC_FEEDBACK = booleanPreferencesKey("haptic_feedback")
        val CURRENT_STREAK = intPreferencesKey("current_streak")
        val LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
    }

    // Onboarding
    val onboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    // Active Profile
    val activeProfileId: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.ACTIVE_PROFILE_ID]
    }

    suspend fun setActiveProfileId(profileId: String?) {
        dataStore.edit { prefs ->
            if (profileId != null) {
                prefs[Keys.ACTIVE_PROFILE_ID] = profileId
            } else {
                prefs.remove(Keys.ACTIVE_PROFILE_ID)
            }
        }
    }

    // Blocking State
    val blockingEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.BLOCKING_ENABLED] ?: false
    }

    suspend fun setBlockingEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.BLOCKING_ENABLED] = enabled
        }
    }

    // Timer Duration
    val timerDurationSeconds: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.TIMER_DURATION_SECONDS] ?: 30
    }

    suspend fun setTimerDurationSeconds(seconds: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.TIMER_DURATION_SECONDS] = seconds
        }
    }

    // Strict Mode Default
    val strictModeDefault: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.STRICT_MODE_DEFAULT] ?: false
    }

    suspend fun setStrictModeDefault(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.STRICT_MODE_DEFAULT] = enabled
        }
    }

    // Dark Mode
    val darkMode: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.DARK_MODE] ?: "system"
    }

    suspend fun setDarkMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = mode
        }
    }

    // Haptic Feedback
    val hapticFeedback: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.HAPTIC_FEEDBACK] ?: true
    }

    suspend fun setHapticFeedback(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[Keys.HAPTIC_FEEDBACK] = enabled
        }
    }

    // Streak
    val currentStreak: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.CURRENT_STREAK] ?: 0
    }

    suspend fun setCurrentStreak(streak: Int) {
        dataStore.edit { prefs ->
            prefs[Keys.CURRENT_STREAK] = streak
        }
    }

    val lastActiveDate: Flow<String?> = dataStore.data.map { prefs ->
        prefs[Keys.LAST_ACTIVE_DATE]
    }

    suspend fun setLastActiveDate(date: String) {
        dataStore.edit { prefs ->
            prefs[Keys.LAST_ACTIVE_DATE] = date
        }
    }

    // Clear all
    suspend fun clearAll() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
