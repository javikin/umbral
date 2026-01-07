package com.umbral.expedition.domain.model

import com.umbral.expedition.domain.usecase.UnlockedAchievement

/**
 * Data class representing rewards earned from completing a blocking session.
 * Emitted as an event from BlockingManager when a session ends.
 */
data class SessionReward(
    val energyResult: EnergyGainResult,
    val unlockedAchievements: List<UnlockedAchievement>
) {
    /**
     * Check if any achievements were unlocked
     */
    val hasAchievements: Boolean
        get() = unlockedAchievements.isNotEmpty()

    /**
     * Check if player leveled up
     */
    val leveledUp: Boolean
        get() = energyResult.newLevel != null

    /**
     * Total stars earned from achievements
     */
    val totalStarsEarned: Int
        get() = unlockedAchievements.sumOf { it.starsEarned }
}
