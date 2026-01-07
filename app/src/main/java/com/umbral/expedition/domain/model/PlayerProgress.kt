package com.umbral.expedition.domain.model

/**
 * Domain model for player progress in the expedition system.
 * This is the clean domain representation without Room dependencies.
 */
data class PlayerProgress(
    val id: Int = 1,
    val level: Int,
    val currentXp: Int,
    val totalEnergy: Int,
    val stars: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalBlockingMinutes: Int
) {
    /**
     * XP needed to reach next level
     */
    val xpForNextLevel: Int
        get() = ExpeditionFormulas.xpForNextLevel(level)

    /**
     * Current progress towards next level (0-100)
     */
    val levelProgress: Int
        get() = ExpeditionFormulas.levelProgress(currentXp, level)

    /**
     * Current streak multiplier for energy generation
     */
    val streakMultiplier: Float
        get() = ExpeditionFormulas.getStreakMultiplier(currentStreak)

    /**
     * Display text for streak multiplier (e.g., "1.5x")
     */
    val streakMultiplierText: String
        get() {
            val multiplier = streakMultiplier
            return if (multiplier == 1.0f) {
                "1.0x"
            } else {
                String.format("%.1fx", multiplier)
            }
        }

    /**
     * Total blocking hours (formatted)
     */
    val totalBlockingHours: Double
        get() = totalBlockingMinutes / 60.0

    /**
     * Check if player has enough energy for a purchase
     */
    fun hasEnoughEnergy(cost: Int): Boolean = totalEnergy >= cost

    /**
     * Check if player has enough stars for a purchase
     */
    fun hasEnoughStars(cost: Int): Boolean = stars >= cost

    /**
     * Calculate new progress after gaining energy and XP
     */
    fun withEnergyAndXp(energyGained: Int, xpGained: Int): PlayerProgress {
        val newXp = currentXp + xpGained
        val newLevel = calculateLevel(newXp)

        return copy(
            level = newLevel,
            currentXp = newXp,
            totalEnergy = totalEnergy + energyGained
        )
    }

    /**
     * Calculate new progress after spending energy
     */
    fun withEnergySpent(cost: Int): PlayerProgress {
        require(cost <= totalEnergy) { "Insufficient energy" }
        return copy(totalEnergy = totalEnergy - cost)
    }

    /**
     * Calculate new progress after spending stars
     */
    fun withStarsSpent(cost: Int): PlayerProgress {
        require(cost <= stars) { "Insufficient stars" }
        return copy(stars = stars - cost)
    }

    /**
     * Calculate new progress after earning stars
     */
    fun withStarsEarned(amount: Int): PlayerProgress {
        return copy(stars = stars + amount)
    }

    /**
     * Calculate new progress after updating streak
     */
    fun withStreak(newStreak: Int): PlayerProgress {
        val newLongestStreak = maxOf(longestStreak, newStreak)
        return copy(
            currentStreak = newStreak,
            longestStreak = newLongestStreak
        )
    }

    /**
     * Calculate new progress after adding blocking minutes
     */
    fun withBlockingMinutes(minutes: Int): PlayerProgress {
        return copy(totalBlockingMinutes = totalBlockingMinutes + minutes)
    }

    private fun calculateLevel(xp: Int): Int {
        var level = 1
        while (ExpeditionFormulas.xpForLevel(level + 1) <= xp) {
            level++
        }
        return level
    }
}
