package com.umbral.expedition.domain.usecase

import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.expedition.domain.model.AchievementDefinitions
import javax.inject.Inject

/**
 * Use case for checking and unlocking achievements based on game events.
 * Called from various parts of the app when relevant actions occur.
 */
class CheckAchievementsUseCase @Inject constructor(
    private val repository: ExpeditionRepository
) {

    /**
     * Check blocking-related achievements after a session ends
     *
     * @param sessionMinutes Duration of the session in minutes
     * @param totalMinutes Total blocking minutes accumulated
     * @param currentStreak Current consecutive days streak
     * @param totalSessions Total number of sessions completed
     * @return List of newly unlocked achievements
     */
    suspend fun checkBlockingAchievements(
        sessionMinutes: Int,
        totalMinutes: Int,
        currentStreak: Int,
        totalSessions: Int
    ): List<UnlockedAchievement> {
        val unlocked = mutableListOf<UnlockedAchievement>()

        // First session special case
        if (totalSessions == 1) {
            checkAndUnlock("first_step")?.let { unlocked.add(it) }
        }

        // Session duration achievements
        if (sessionMinutes >= 60) {
            checkAndUnlock("golden_hour")?.let { unlocked.add(it) }
        }
        if (sessionMinutes >= 120) {
            checkAndUnlock("marathoner")?.let { unlocked.add(it) }
        }

        // Total minutes achievements
        updateProgress("thousand_min", totalMinutes)
        updateProgress("iron_will", totalMinutes)
        updateProgress("legend", totalMinutes)

        // Check if any total minutes achievements just unlocked
        checkAndUnlock("thousand_min")?.let { unlocked.add(it) }
        checkAndUnlock("iron_will")?.let { unlocked.add(it) }
        checkAndUnlock("legend")?.let { unlocked.add(it) }

        // Streak achievements
        updateProgress("consistent_7", currentStreak)
        updateProgress("dedicated_14", currentStreak)
        updateProgress("master_30", currentStreak)

        checkAndUnlock("consistent_7")?.let { unlocked.add(it) }
        checkAndUnlock("dedicated_14")?.let { unlocked.add(it) }
        checkAndUnlock("master_30")?.let { unlocked.add(it) }

        // Session count achievements
        updateProgress("centurion", totalSessions)
        checkAndUnlock("centurion")?.let { unlocked.add(it) }

        return unlocked
    }

    /**
     * Check exploration achievements when discovering locations
     *
     * @param totalLocationsDiscovered Total unique locations discovered
     * @param loreStoriesRead Number of lore stories read
     * @param biomeCompletionPercent Completion percentage of current biome
     * @param allCompanionsCaptured Whether all base companions are captured
     * @param secretsFound Number of secrets found
     * @param biomeCompletedInDays Days taken to complete biome (null if not complete)
     */
    suspend fun checkExplorationAchievements(
        totalLocationsDiscovered: Int,
        loreStoriesRead: Int,
        biomeCompletionPercent: Int,
        allCompanionsCaptured: Boolean,
        secretsFound: Int,
        biomeCompletedInDays: Int?
    ): List<UnlockedAchievement> {
        val unlocked = mutableListOf<UnlockedAchievement>()

        // Location discovery achievements
        if (totalLocationsDiscovered >= 1) {
            checkAndUnlock("novice_explorer")?.let { unlocked.add(it) }
        }
        updateProgress("cartographer", totalLocationsDiscovered)
        updateProgress("adventurer", totalLocationsDiscovered)
        updateProgress("master_explorer", totalLocationsDiscovered)

        checkAndUnlock("cartographer")?.let { unlocked.add(it) }
        checkAndUnlock("adventurer")?.let { unlocked.add(it) }
        checkAndUnlock("master_explorer")?.let { unlocked.add(it) }

        // Lore reading achievements
        updateProgress("lore_reader_5", loreStoriesRead)
        updateProgress("historian", loreStoriesRead)

        checkAndUnlock("lore_reader_5")?.let { unlocked.add(it) }
        checkAndUnlock("historian")?.let { unlocked.add(it) }

        // Biome completion
        updateProgress("biome_complete", biomeCompletionPercent)
        checkAndUnlock("biome_complete")?.let { unlocked.add(it) }

        // Collector achievement
        if (allCompanionsCaptured) {
            checkAndUnlock("collector")?.let { unlocked.add(it) }
        }

        // Secrets
        updateProgress("no_stone", secretsFound)
        checkAndUnlock("no_stone")?.let { unlocked.add(it) }

        // Speedrunner
        if (biomeCompletedInDays != null && biomeCompletedInDays < 14) {
            checkAndUnlock("speedrunner")?.let { unlocked.add(it) }
        }

        return unlocked
    }

    /**
     * Check companion achievements when capturing or evolving
     *
     * @param totalCompanionsCaptured Total unique companions captured
     * @param totalEvolutions Total evolutions performed
     * @param maxEnergyInOneCompanion Highest energy invested in a single companion
     * @param hasMaxEvolution Whether any companion reached max evolution
     * @param allCompanionsMaxEvolution Whether all companions are at max evolution
     */
    suspend fun checkCompanionAchievements(
        totalCompanionsCaptured: Int,
        totalEvolutions: Int,
        maxEnergyInOneCompanion: Int,
        hasMaxEvolution: Boolean,
        allCompanionsMaxEvolution: Boolean
    ): List<UnlockedAchievement> {
        val unlocked = mutableListOf<UnlockedAchievement>()

        // Capture achievements
        if (totalCompanionsCaptured >= 1) {
            checkAndUnlock("first_friend")?.let { unlocked.add(it) }
        }
        updateProgress("duo", totalCompanionsCaptured)
        updateProgress("team", totalCompanionsCaptured)
        updateProgress("all_together", totalCompanionsCaptured)

        checkAndUnlock("duo")?.let { unlocked.add(it) }
        checkAndUnlock("team")?.let { unlocked.add(it) }
        checkAndUnlock("all_together")?.let { unlocked.add(it) }

        // Evolution achievements
        if (totalEvolutions >= 1) {
            checkAndUnlock("first_evolution")?.let { unlocked.add(it) }
        }
        updateProgress("evolutionist", totalEvolutions)
        updateProgress("master_breeder", totalEvolutions)

        checkAndUnlock("evolutionist")?.let { unlocked.add(it) }
        checkAndUnlock("master_breeder")?.let { unlocked.add(it) }

        // Energy investment
        updateProgress("best_friend", maxEnergyInOneCompanion)
        checkAndUnlock("best_friend")?.let { unlocked.add(it) }

        // Max evolution
        if (hasMaxEvolution) {
            checkAndUnlock("eternal_bond")?.let { unlocked.add(it) }
        }

        // All companions max evolution
        if (allCompanionsMaxEvolution) {
            checkAndUnlock("full_sanctuary")?.let { unlocked.add(it) }
        }

        return unlocked
    }

    /**
     * Helper: Check if achievement can be unlocked and unlock it
     */
    private suspend fun checkAndUnlock(id: String): UnlockedAchievement? {
        val achievement = repository.getAchievement(id) ?: return null

        // Already unlocked?
        if (achievement.unlockedAt != null) return null

        // Check if progress meets target
        if (achievement.progress >= achievement.target) {
            val starsEarned = repository.unlockAchievement(id)

            // Only return if stars were actually awarded (achievement just unlocked)
            if (starsEarned > 0) {
                val definition = AchievementDefinitions.getById(id)

                return UnlockedAchievement(
                    id = id,
                    title = definition?.title ?: "",
                    description = definition?.description ?: "",
                    starsEarned = starsEarned
                )
            }
        }

        return null
    }

    /**
     * Helper: Update achievement progress
     */
    private suspend fun updateProgress(id: String, progress: Int) {
        repository.updateAchievementProgress(id, progress)
    }
}

/**
 * Data class representing a newly unlocked achievement
 */
data class UnlockedAchievement(
    val id: String,
    val title: String,
    val description: String,
    val starsEarned: Int
)
