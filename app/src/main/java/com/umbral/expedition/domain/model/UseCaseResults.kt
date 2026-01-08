package com.umbral.expedition.domain.model

/**
 * Result types for use cases.
 * Sealed classes provide type-safe results with success/failure variants.
 */

// ========== Energy Gain ==========

/**
 * Result from gaining energy after a blocking session
 */
data class EnergyGainResult(
    val baseEnergy: Int,
    val multiplier: Float,
    val totalEnergy: Int,
    val xpGained: Int,
    val newLevel: Int?,
    val newStreak: Int
)

// ========== Location Discovery ==========

/**
 * Result from attempting to discover a location
 */
sealed class DiscoveryResult {
    data class Success(
        val location: Location,
        val energyRemaining: Int
    ) : DiscoveryResult()

    data class InsufficientEnergy(
        val required: Int,
        val available: Int,
        val shortage: Int
    ) : DiscoveryResult()

    data class AlreadyDiscovered(
        val locationId: String
    ) : DiscoveryResult()
}

// ========== Companion Evolution ==========

/**
 * Result from attempting to evolve a companion
 */
sealed class EvolutionResult {
    data class Success(
        val companion: Companion,
        val newEvolutionState: Int
    ) : EvolutionResult()

    data class InsufficientEnergy(
        val companionId: String,
        val required: Int,
        val available: Int,
        val shortage: Int
    ) : EvolutionResult()

    data class AlreadyMaxEvolution(
        val companionId: String
    ) : EvolutionResult()

    data class CompanionNotFound(
        val companionId: String
    ) : EvolutionResult()
}

// ========== Companion Capture ==========

/**
 * Result from attempting to capture a companion
 */
sealed class CaptureResult {
    data class Success(
        val companion: Companion
    ) : CaptureResult()

    data class RequirementNotMet(
        val companionType: CompanionType,
        val requirement: CaptureRequirement,
        val requirementDescription: String
    ) : CaptureResult()

    data class AlreadyCaptured(
        val companionType: CompanionType
    ) : CaptureResult()
}

// ========== Energy Investment ==========

/**
 * Result from investing energy into a companion
 */
sealed class InvestEnergyResult {
    data class Success(
        val companion: Companion,
        val energyInvested: Int,
        val totalEnergyInvested: Int,
        val canNowEvolve: Boolean
    ) : InvestEnergyResult()

    data class InsufficientEnergy(
        val companionId: String,
        val requested: Int,
        val available: Int,
        val shortage: Int
    ) : InvestEnergyResult()

    data class CompanionNotFound(
        val companionId: String
    ) : InvestEnergyResult()

    data class AlreadyMaxEvolution(
        val companionId: String
    ) : InvestEnergyResult()
}
