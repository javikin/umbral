package com.umbral.expedition.domain.usecase

import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.expedition.domain.mapper.CompanionMapper
import com.umbral.expedition.domain.mapper.ProgressMapper
import com.umbral.expedition.domain.model.InvestEnergyResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for investing player energy into a companion for evolution progress.
 *
 * This use case:
 * 1. Checks if companion exists
 * 2. Checks if companion is not already max evolution
 * 3. Checks if player has enough energy
 * 4. Spends player energy
 * 5. Adds energy to companion
 * 6. Returns result
 */
class InvestEnergyUseCase @Inject constructor(
    private val repository: ExpeditionRepository
) {

    /**
     * Invest energy into a companion
     *
     * @param companionId ID of companion to invest in
     * @param energyAmount Amount of energy to invest
     * @return InvestEnergyResult with success or failure details
     */
    suspend operator fun invoke(companionId: String, energyAmount: Int): InvestEnergyResult {
        require(energyAmount > 0) { "Energy amount must be positive" }

        // Get companion
        val companionEntity = repository.getCompanionById(companionId)
            ?: return InvestEnergyResult.CompanionNotFound(companionId)

        val companion = CompanionMapper.toDomain(companionEntity)

        // Check if already at max evolution
        if (companion.isMaxEvolution) {
            return InvestEnergyResult.AlreadyMaxEvolution(companionId)
        }

        // Get current progress
        val progressEntity = repository.getProgress().first()
            ?: throw IllegalStateException("Progress not initialized")

        val progress = ProgressMapper.toDomain(progressEntity)

        // Check if player has enough energy
        if (progress.totalEnergy < energyAmount) {
            return InvestEnergyResult.InsufficientEnergy(
                companionId = companionId,
                requested = energyAmount,
                available = progress.totalEnergy,
                shortage = energyAmount - progress.totalEnergy
            )
        }

        // Spend player energy
        repository.spendEnergy(energyAmount)

        // Add energy to companion
        val newTotalEnergy = companion.energyInvested + energyAmount
        repository.investEnergyInCompanion(companionId, newTotalEnergy)

        // Get updated companion
        val updatedEntity = repository.getCompanionById(companionId)
            ?: throw IllegalStateException("Companion disappeared after energy investment")

        val updatedCompanion = CompanionMapper.toDomain(updatedEntity)

        return InvestEnergyResult.Success(
            companion = updatedCompanion,
            energyInvested = energyAmount,
            totalEnergyInvested = newTotalEnergy,
            canNowEvolve = updatedCompanion.canEvolve
        )
    }
}
