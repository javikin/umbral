package com.umbral.expedition.domain.model

/**
 * State model for expedition data displayed on the HomeScreen.
 * Contains summary information for the expedition progress card.
 */
data class ExpeditionHomeState(
    val isLoading: Boolean = true,
    val isInitialized: Boolean = false,
    val level: Int = 1,
    val currentXp: Int = 0,
    val xpForNextLevel: Int = 100,
    val levelProgress: Int = 0,
    val totalEnergy: Int = 0,
    val currentStreak: Int = 0,
    val streakMultiplier: String = "1.0x",
    val activeCompanion: ActiveCompanionInfo? = null
) {
    companion object {
        val Loading = ExpeditionHomeState(isLoading = true)
        val NotInitialized = ExpeditionHomeState(isLoading = false, isInitialized = false)
    }
}

/**
 * Minimal companion info for display on HomeScreen
 */
data class ActiveCompanionInfo(
    val id: String,
    val displayName: String,
    val type: CompanionType,
    val evolutionState: Int,
    val passiveBonusDescription: String
)
