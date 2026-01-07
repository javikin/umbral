package com.umbral.expedition.domain.model

/**
 * Sealed class representing different capture requirements for companions.
 * Each companion type has specific conditions that must be met before capture.
 */
sealed class CaptureRequirement {

    /**
     * Companion available from the start (no requirements)
     */
    object AlwaysAvailable : CaptureRequirement()

    /**
     * Requires player to reach a minimum level
     */
    data class MinimumLevel(val level: Int) : CaptureRequirement()

    /**
     * Requires discovering a minimum number of locations
     */
    data class LocationsDiscovered(val count: Int) : CaptureRequirement()

    /**
     * Requires specific location to be discovered
     */
    data class SpecificLocation(val locationId: String) : CaptureRequirement()

    /**
     * Requires completing specific achievements
     */
    data class AchievementUnlocked(val achievementIds: List<String>) : CaptureRequirement()

    /**
     * Requires minimum current streak
     */
    data class MinimumStreak(val days: Int) : CaptureRequirement()

    /**
     * Requires total blocking time
     */
    data class TotalBlockingMinutes(val minutes: Int) : CaptureRequirement()

    /**
     * Check if requirement is met given current player progress
     *
     * @param progress Current player progress
     * @param discoveredLocationIds List of discovered location IDs
     * @param unlockedAchievementIds List of unlocked achievement IDs
     * @return true if requirement is satisfied
     */
    fun isMet(
        progress: PlayerProgress,
        discoveredLocationIds: List<String> = emptyList(),
        unlockedAchievementIds: List<String> = emptyList()
    ): Boolean = when (this) {
        is AlwaysAvailable -> true
        is MinimumLevel -> progress.level >= this.level
        is LocationsDiscovered -> discoveredLocationIds.size >= this.count
        is SpecificLocation -> discoveredLocationIds.contains(this.locationId)
        is AchievementUnlocked -> this.achievementIds.all { it in unlockedAchievementIds }
        is MinimumStreak -> progress.currentStreak >= this.days
        is TotalBlockingMinutes -> progress.totalBlockingMinutes >= this.minutes
    }

    /**
     * Get user-friendly description of requirement (in Spanish)
     */
    fun getDescription(): String = when (this) {
        is AlwaysAvailable -> "Disponible desde el inicio"
        is MinimumLevel -> "Alcanza nivel $level"
        is LocationsDiscovered -> "Descubre $count locaciones"
        is SpecificLocation -> "Descubre una locación especial"
        is AchievementUnlocked -> {
            if (achievementIds.size == 1) {
                "Desbloquea un logro específico"
            } else {
                "Desbloquea ${achievementIds.size} logros"
            }
        }
        is MinimumStreak -> "Mantén racha de $days días"
        is TotalBlockingMinutes -> {
            val hours = minutes / 60
            if (hours > 0) {
                "Acumula $hours horas bloqueadas"
            } else {
                "Acumula $minutes minutos bloqueados"
            }
        }
    }
}
