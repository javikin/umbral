package com.umbral.data.preferences

import com.umbral.data.local.preferences.UmbralPreferences
import com.umbral.domain.preferences.DarkMode
import com.umbral.domain.preferences.PreferencesRepository
import com.umbral.domain.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val umbralPreferences: UmbralPreferences
) : PreferencesRepository {

    override fun getPreferences(): Flow<UserPreferences> {
        return combine(
            umbralPreferences.onboardingCompleted,
            umbralPreferences.activeProfileId,
            umbralPreferences.blockingEnabled,
            umbralPreferences.timerDurationSeconds,
            umbralPreferences.strictModeDefault,
            umbralPreferences.darkMode,
            umbralPreferences.hapticFeedback,
            umbralPreferences.currentStreak,
            umbralPreferences.lastActiveDate
        ) { values ->
            UserPreferences(
                onboardingCompleted = values[0] as Boolean,
                activeProfileId = values[1] as String?,
                blockingEnabled = values[2] as Boolean,
                timerDurationSeconds = values[3] as Int,
                strictModeDefault = values[4] as Boolean,
                darkMode = DarkMode.fromString(values[5] as String),
                hapticFeedback = values[6] as Boolean,
                currentStreak = values[7] as Int,
                lastActiveDate = values[8] as String?
            )
        }
    }

    override fun isOnboardingCompleted(): Flow<Boolean> {
        return umbralPreferences.onboardingCompleted
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        try {
            umbralPreferences.setOnboardingCompleted(completed)
            Timber.d("Onboarding completed set to: $completed")
        } catch (e: Exception) {
            Timber.e(e, "Error setting onboarding completed")
            throw e
        }
    }

    override fun getActiveProfileId(): Flow<String?> {
        return umbralPreferences.activeProfileId
    }

    override suspend fun setActiveProfileId(profileId: String?) {
        try {
            umbralPreferences.setActiveProfileId(profileId)
            Timber.d("Active profile ID set to: $profileId")
        } catch (e: Exception) {
            Timber.e(e, "Error setting active profile ID")
            throw e
        }
    }

    override fun isBlockingEnabled(): Flow<Boolean> {
        return umbralPreferences.blockingEnabled
    }

    override suspend fun setBlockingEnabled(enabled: Boolean) {
        try {
            umbralPreferences.setBlockingEnabled(enabled)
            Timber.d("Blocking enabled set to: $enabled")
        } catch (e: Exception) {
            Timber.e(e, "Error setting blocking enabled")
            throw e
        }
    }

    override fun getTimerDurationSeconds(): Flow<Int> {
        return umbralPreferences.timerDurationSeconds
    }

    override suspend fun setTimerDurationSeconds(seconds: Int) {
        try {
            require(seconds > 0) { "Timer duration must be positive" }
            umbralPreferences.setTimerDurationSeconds(seconds)
            Timber.d("Timer duration set to: $seconds seconds")
        } catch (e: Exception) {
            Timber.e(e, "Error setting timer duration")
            throw e
        }
    }

    override fun isStrictModeDefault(): Flow<Boolean> {
        return umbralPreferences.strictModeDefault
    }

    override suspend fun setStrictModeDefault(enabled: Boolean) {
        try {
            umbralPreferences.setStrictModeDefault(enabled)
            Timber.d("Strict mode default set to: $enabled")
        } catch (e: Exception) {
            Timber.e(e, "Error setting strict mode default")
            throw e
        }
    }

    override fun getDarkMode(): Flow<DarkMode> {
        return umbralPreferences.darkMode.map { DarkMode.fromString(it) }
    }

    override suspend fun setDarkMode(mode: DarkMode) {
        try {
            umbralPreferences.setDarkMode(mode.toString())
            Timber.d("Dark mode set to: $mode")
        } catch (e: Exception) {
            Timber.e(e, "Error setting dark mode")
            throw e
        }
    }

    override fun isHapticFeedbackEnabled(): Flow<Boolean> {
        return umbralPreferences.hapticFeedback
    }

    override suspend fun setHapticFeedback(enabled: Boolean) {
        try {
            umbralPreferences.setHapticFeedback(enabled)
            Timber.d("Haptic feedback set to: $enabled")
        } catch (e: Exception) {
            Timber.e(e, "Error setting haptic feedback")
            throw e
        }
    }

    override fun getCurrentStreak(): Flow<Int> {
        return umbralPreferences.currentStreak
    }

    override suspend fun setCurrentStreak(streak: Int) {
        try {
            require(streak >= 0) { "Streak must be non-negative" }
            umbralPreferences.setCurrentStreak(streak)
            Timber.d("Current streak set to: $streak")
        } catch (e: Exception) {
            Timber.e(e, "Error setting current streak")
            throw e
        }
    }

    override fun getLastActiveDate(): Flow<String?> {
        return umbralPreferences.lastActiveDate
    }

    override suspend fun setLastActiveDate(date: String) {
        try {
            umbralPreferences.setLastActiveDate(date)
            Timber.d("Last active date set to: $date")
        } catch (e: Exception) {
            Timber.e(e, "Error setting last active date")
            throw e
        }
    }

    override suspend fun clearAll() {
        try {
            umbralPreferences.clearAll()
            Timber.d("All preferences cleared")
        } catch (e: Exception) {
            Timber.e(e, "Error clearing preferences")
            throw e
        }
    }
}
