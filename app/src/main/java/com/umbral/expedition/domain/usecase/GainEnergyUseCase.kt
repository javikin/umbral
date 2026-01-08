package com.umbral.expedition.domain.usecase

import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.expedition.domain.mapper.ProgressMapper
import com.umbral.expedition.domain.model.EnergyGainResult
import com.umbral.expedition.domain.model.ExpeditionFormulas
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for calculating and awarding energy after a blocking session.
 *
 * This use case:
 * 1. Calculates base energy from blocking minutes
 * 2. Applies streak multiplier
 * 3. Awards energy and XP
 * 4. Checks for level up
 * 5. Returns detailed result
 */
class GainEnergyUseCase @Inject constructor(
    private val repository: ExpeditionRepository
) {

    /**
     * Calculate and award energy from a blocking session
     *
     * @param blockingMinutes Duration of blocking session in minutes
     * @return EnergyGainResult with detailed breakdown
     */
    suspend operator fun invoke(blockingMinutes: Int): EnergyGainResult {
        // Get current progress
        val progressEntity = repository.getProgress().first()
            ?: throw IllegalStateException("Progress not initialized")

        val progress = ProgressMapper.toDomain(progressEntity)

        // Calculate energy with streak multiplier
        val streakMultiplier = progress.streakMultiplier
        val baseEnergy = blockingMinutes * ExpeditionFormulas.BASE_ENERGY_PER_MINUTE
        val totalEnergy = ExpeditionFormulas.calculateEnergy(blockingMinutes, streakMultiplier)

        // Calculate XP gained (10% of energy earned)
        val xpGained = (totalEnergy * 0.1).toInt()

        // Get level before adding XP
        val oldLevel = progress.level
        val newXp = progress.currentXp + xpGained

        // Calculate new level
        var newLevel = oldLevel
        while (ExpeditionFormulas.xpForLevel(newLevel + 1) <= newXp) {
            newLevel++
        }

        // Award energy, XP, and update level
        repository.addEnergy(totalEnergy)
        repository.addXp(xpGained)

        if (newLevel > oldLevel) {
            repository.updateLevel(newLevel)
        }

        // Add blocking minutes to total
        repository.addBlockingMinutes(blockingMinutes)

        return EnergyGainResult(
            baseEnergy = baseEnergy,
            multiplier = streakMultiplier,
            totalEnergy = totalEnergy,
            xpGained = xpGained,
            newLevel = if (newLevel > oldLevel) newLevel else null,
            newStreak = progress.currentStreak
        )
    }
}
