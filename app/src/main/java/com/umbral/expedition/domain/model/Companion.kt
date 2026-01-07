package com.umbral.expedition.domain.model

/**
 * Domain model for a companion creature.
 * This is the clean domain representation without Room dependencies.
 */
data class Companion(
    val id: String,
    val type: CompanionType,
    val name: String?,
    val evolutionState: Int,
    val energyInvested: Int,
    val capturedAt: Long,
    val isActive: Boolean
) {
    /**
     * Check if companion can evolve to next state
     */
    val canEvolve: Boolean
        get() = evolutionState < 3 && energyInvested >= ExpeditionFormulas.evolutionCost(evolutionState)

    /**
     * Get total energy needed for next evolution
     * Returns 0 if already at max evolution
     */
    val evolutionCost: Int
        get() = ExpeditionFormulas.evolutionCost(evolutionState)

    /**
     * Get evolution progress percentage (0-100)
     */
    val evolutionProgress: Int
        get() = ExpeditionFormulas.evolutionProgress(energyInvested, evolutionState)

    /**
     * Display name: Custom name if set, otherwise type name with evolution state
     */
    val displayName: String
        get() = name ?: "${type.displayName} ${evolutionStateToRoman(evolutionState)}"

    /**
     * Get passive bonus from this companion
     */
    val passiveBonus: PassiveBonus
        get() = type.passiveBonus

    /**
     * Get element of this companion
     */
    val element: Element
        get() = type.element

    /**
     * Check if companion is at max evolution
     */
    val isMaxEvolution: Boolean
        get() = evolutionState >= 3

    /**
     * Get remaining energy needed for next evolution
     */
    val energyUntilNextEvolution: Int
        get() = if (canEvolve) {
            0 // Already can evolve
        } else if (isMaxEvolution) {
            0 // Already max
        } else {
            evolutionCost - energyInvested
        }

    private fun evolutionStateToRoman(state: Int): String = when (state) {
        1 -> "I"
        2 -> "II"
        3 -> "III"
        else -> ""
    }
}
