package com.umbral.expedition.domain.usecase

import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.expedition.domain.mapper.CompanionMapper
import com.umbral.expedition.domain.mapper.ProgressMapper
import com.umbral.expedition.domain.model.CaptureResult
import com.umbral.expedition.domain.model.CompanionType
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for capturing a new companion.
 *
 * This use case:
 * 1. Checks if companion type already captured
 * 2. Checks if capture requirements are met
 * 3. Captures companion
 * 4. Returns result
 */
class CaptureCompanionUseCase @Inject constructor(
    private val repository: ExpeditionRepository
) {

    /**
     * Attempt to capture a companion
     *
     * @param companionType Type of companion to capture
     * @return CaptureResult with success or failure details
     */
    suspend operator fun invoke(companionType: CompanionType): CaptureResult {
        // Check if already captured
        val existingCompanions = repository.getAllCompanions().first()
        if (existingCompanions.any { it.type == companionType.id }) {
            return CaptureResult.AlreadyCaptured(companionType)
        }

        // Get current progress for requirement checking
        val progressEntity = repository.getProgress().first()
            ?: throw IllegalStateException("Progress not initialized")

        val progress = ProgressMapper.toDomain(progressEntity)

        // Get discovered locations
        val discoveredLocations = repository.getDiscoveredLocations().first()
        val discoveredLocationIds = discoveredLocations.map { it.id }

        // Get unlocked achievements
        val unlockedAchievements = repository.getUnlockedAchievements().first()
        val unlockedAchievementIds = unlockedAchievements.map { it.id }

        // Check if requirements are met
        val requirement = companionType.captureRequirement
        val requirementMet = requirement.isMet(
            progress = progress,
            discoveredLocationIds = discoveredLocationIds,
            unlockedAchievementIds = unlockedAchievementIds
        )

        if (!requirementMet) {
            return CaptureResult.RequirementNotMet(
                companionType = companionType,
                requirement = requirement,
                requirementDescription = requirement.getDescription()
            )
        }

        // Capture the companion
        repository.captureCompanion(companionType.id)

        // Get the newly captured companion
        val companionEntity = repository.getCompanionByType(companionType.id)
            ?: throw IllegalStateException("Companion not found after capture")

        val companion = CompanionMapper.toDomain(companionEntity)

        return CaptureResult.Success(companion)
    }
}
