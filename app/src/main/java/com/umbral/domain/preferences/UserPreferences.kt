package com.umbral.domain.preferences

/**
 * Domain model for user preferences.
 */
data class UserPreferences(
    val onboardingCompleted: Boolean = false,
    val activeProfileId: String? = null,
    val blockingEnabled: Boolean = false,
    val timerDurationSeconds: Int = 30,
    val strictModeDefault: Boolean = false,
    val darkMode: DarkMode = DarkMode.SYSTEM,
    val hapticFeedback: Boolean = true,
    val currentStreak: Int = 0,
    val lastActiveDate: String? = null
)

enum class DarkMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromString(value: String): DarkMode {
            return when (value.lowercase()) {
                "light" -> LIGHT
                "dark" -> DARK
                else -> SYSTEM
            }
        }
    }

    override fun toString(): String {
        return name.lowercase()
    }
}
