package com.umbral.expedition.domain.model

/**
 * Object containing all game formulas and constants for the expedition system.
 * Centralizes game balance calculations.
 */
object ExpeditionFormulas {

    // ========== Energy Generation ==========

    /**
     * Base energy earned per minute of blocking
     */
    const val BASE_ENERGY_PER_MINUTE = 10

    /**
     * Calculate streak multiplier based on consecutive days
     *
     * Multipliers:
     * - Days 1-2: 1.0x (no bonus)
     * - Days 3-6: 1.2x (+20%)
     * - Days 7-13: 1.5x (+50%)
     * - Days 14-29: 2.0x (+100%)
     * - Days 30+: 2.5x (+150%)
     */
    fun getStreakMultiplier(streak: Int): Float = when {
        streak < 3 -> 1.0f
        streak < 7 -> 1.2f
        streak < 14 -> 1.5f
        streak < 30 -> 2.0f
        else -> 2.5f
    }

    /**
     * Calculate total energy gained from blocking session
     *
     * @param minutes Duration of blocking session
     * @param streakMultiplier Current streak multiplier (from getStreakMultiplier)
     * @return Total energy earned (rounded down)
     */
    fun calculateEnergy(minutes: Int, streakMultiplier: Float): Int {
        val baseEnergy = minutes * BASE_ENERGY_PER_MINUTE
        return (baseEnergy * streakMultiplier).toInt()
    }

    // ========== XP and Leveling ==========

    /**
     * XP required to reach a given level (total from level 1)
     *
     * Formula: level^2 * 100
     * - Level 2: 400 XP
     * - Level 5: 2500 XP
     * - Level 10: 10000 XP
     * - Level 20: 40000 XP
     */
    fun xpForLevel(level: Int): Int {
        return level * level * 100
    }

    /**
     * XP needed to advance from current level to next level
     */
    fun xpForNextLevel(currentLevel: Int): Int {
        return xpForLevel(currentLevel + 1) - xpForLevel(currentLevel)
    }

    /**
     * Calculate current level progress percentage (0-100)
     */
    fun levelProgress(currentXp: Int, currentLevel: Int): Int {
        val xpBase = xpForLevel(currentLevel)
        val xpNext = xpForLevel(currentLevel + 1)
        val xpIntoLevel = currentXp - xpBase
        val xpNeeded = xpNext - xpBase

        return if (xpNeeded > 0) {
            ((xpIntoLevel.toFloat() / xpNeeded) * 100).toInt().coerceIn(0, 100)
        } else {
            0
        }
    }

    // ========== Location Discovery ==========

    /**
     * Energy cost to reveal a location in the map
     *
     * Formula: (index + 1) * 50
     * - Location 1: 50 energy
     * - Location 5: 250 energy
     * - Location 15: 750 energy
     */
    fun locationRevealCost(index: Int): Int {
        return (index + 1) * 50
    }

    // ========== Companion Evolution ==========

    /**
     * Energy thresholds for companion evolution states
     */
    const val EVOLUTION_STATE_1_MAX = 499
    const val EVOLUTION_STATE_2_MIN = 500
    const val EVOLUTION_STATE_2_MAX = 1499
    const val EVOLUTION_STATE_3_MIN = 1500

    /**
     * Calculate total energy cost to reach next evolution state
     *
     * @param currentState Current evolution state (1, 2, or 3)
     * @return Energy needed, or 0 if already at max evolution
     */
    fun evolutionCost(currentState: Int): Int = when (currentState) {
        1 -> EVOLUTION_STATE_2_MIN
        2 -> EVOLUTION_STATE_3_MIN
        else -> 0 // Already at max evolution
    }

    /**
     * Determine evolution state based on energy invested
     */
    fun getEvolutionState(energyInvested: Int): Int = when {
        energyInvested < EVOLUTION_STATE_2_MIN -> 1
        energyInvested < EVOLUTION_STATE_3_MIN -> 2
        else -> 3
    }

    /**
     * Calculate evolution progress percentage for current state (0-100)
     */
    fun evolutionProgress(energyInvested: Int, currentState: Int): Int {
        return when (currentState) {
            1 -> {
                val progress = (energyInvested.toFloat() / EVOLUTION_STATE_2_MIN * 100).toInt()
                progress.coerceIn(0, 100)
            }
            2 -> {
                val energyIntoState = energyInvested - EVOLUTION_STATE_2_MIN
                val energyNeeded = EVOLUTION_STATE_3_MIN - EVOLUTION_STATE_2_MIN
                val progress = (energyIntoState.toFloat() / energyNeeded * 100).toInt()
                progress.coerceIn(0, 100)
            }
            else -> 100 // Max evolution
        }
    }
}
