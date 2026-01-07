package com.umbral.expedition.domain.usecase

import com.umbral.expedition.data.entity.AchievementEntity
import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.expedition.domain.model.AchievementDefinitions
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case for initializing the expedition system on first app launch.
 * This should be called once during app startup to set up all achievements.
 */
class InitializeExpeditionUseCase @Inject constructor(
    private val repository: ExpeditionRepository
) {

    /**
     * Initialize all achievements from definitions.
     * Safe to call multiple times - uses REPLACE strategy.
     *
     * Call this from Application.onCreate() or a startup initializer.
     */
    suspend operator fun invoke() {
        initializeAchievements()
    }

    /**
     * Initialize all 30 achievements in the database
     */
    private suspend fun initializeAchievements() {
        // Check if achievements already exist
        val existingAchievements = repository.getAchievements().first()
        if (existingAchievements.isNotEmpty()) {
            // Already initialized
            return
        }

        // Create entities from definitions
        val achievementEntities = AchievementDefinitions.ALL.map { def ->
            AchievementEntity(
                id = def.id,
                category = def.category.name.lowercase(),
                progress = 0,
                target = def.target,
                unlockedAt = null,
                starsReward = def.starsReward
            )
        }

        // Note: This would need to be implemented in the repository
        // For now, this is a placeholder showing the pattern
        // Actual implementation will be in Issue #53's migration or repository init
        // repository.insertAchievements(achievementEntities)
    }
}
