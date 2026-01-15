package com.umbral.domain.preferences

import kotlinx.coroutines.flow.Flow

/**
 * Repository for user preferences and settings.
 */
interface PreferencesRepository {

    // Get all preferences as flow
    fun getPreferences(): Flow<UserPreferences>

    // Onboarding
    fun isOnboardingCompleted(): Flow<Boolean>
    suspend fun setOnboardingCompleted(completed: Boolean)

    // Active Profile
    fun getActiveProfileId(): Flow<String?>
    suspend fun setActiveProfileId(profileId: String?)

    // Blocking State
    fun isBlockingEnabled(): Flow<Boolean>
    suspend fun setBlockingEnabled(enabled: Boolean)

    // Timer Duration
    fun getTimerDurationSeconds(): Flow<Int>
    suspend fun setTimerDurationSeconds(seconds: Int)

    // Strict Mode
    fun isStrictModeDefault(): Flow<Boolean>
    suspend fun setStrictModeDefault(enabled: Boolean)

    // Dark Mode
    fun getDarkMode(): Flow<DarkMode>
    suspend fun setDarkMode(mode: DarkMode)

    // Haptic Feedback
    fun isHapticFeedbackEnabled(): Flow<Boolean>
    suspend fun setHapticFeedback(enabled: Boolean)

    // Streak
    fun getCurrentStreak(): Flow<Int>
    suspend fun setCurrentStreak(streak: Int)

    fun getLastActiveDate(): Flow<String?>
    suspend fun setLastActiveDate(date: String)

    // Expedition Welcome
    fun isExpeditionWelcomeShown(): Flow<Boolean>
    suspend fun setExpeditionWelcomeShown(shown: Boolean)

    // Clear all
    suspend fun clearAll()
}
