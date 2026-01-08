package com.umbral.expedition.domain.usecase

import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.expedition.domain.mapper.CompanionMapper
import com.umbral.expedition.domain.model.EvolutionResult
import com.umbral.expedition.domain.model.ExpeditionFormulas
import javax.inject.Inject

/**
 * Use case for evolving a companion to the next evolution state.
 *
 * This use case:
 * 1. Checks if companion exists
 * 2. Checks if companion has enough energy invested
 * 3. Checks if not already at max evolution
 * 4. Performs evolution
 * 5. Returns result
 */
class EvolveCompanionUseCase @Inject constructor(
    private val repository: ExpeditionRepository
) {

    /**
     * Attempt to evolve a companion
     *
     * @param companionId ID of companion to evolve
     * @return EvolutionResult with success or failure details
     */
    suspend operator fun invoke(companionId: String): EvolutionResult {
        // Get companion
        val companionEntity = repository.getCompanionById(companionId)
            ?: return EvolutionResult.CompanionNotFound(companionId)

        val companion = CompanionMapper.toDomain(companionEntity)

        // Check if already at max evolution
        if (companion.isMaxEvolution) {
            return EvolutionResult.AlreadyMaxEvolution(companionId)
        }

        // Check if has enough energy for evolution
        val requiredEnergy = ExpeditionFormulas.evolutionCost(companion.evolutionState)
        if (companion.energyInvested < requiredEnergy) {
            return EvolutionResult.InsufficientEnergy(
                companionId = companionId,
                required = requiredEnergy,
                available = companion.energyInvested,
                shortage = requiredEnergy - companion.energyInvested
            )
        }

        // Perform evolution
        val newEvolutionState = companion.evolutionState + 1
        repository.evolveCompanion(companionId)

        // Get updated companion
        val updatedEntity = repository.getCompanionById(companionId)
            ?: throw IllegalStateException("Companion disappeared after evolution")

        val updatedCompanion = CompanionMapper.toDomain(updatedEntity)

        return EvolutionResult.Success(
            companion = updatedCompanion,
            newEvolutionState = newEvolutionState
        )
    }
}
