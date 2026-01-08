package com.umbral.notifications.domain.usecase

import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.notifications.domain.repository.NotificationRepository
import timber.log.Timber
import javax.inject.Inject

/**
 * UseCase to grant energy bonus and update achievements based on blocked notifications.
 *
 * Called when a blocking session ends to:
 * 1. Grant +1 energy per 5 notifications blocked in the session
 * 2. Update notification achievement progress (shield_mind, fortress, immune)
 *
 * @property notificationRepository Repository for notification data
 * @property expeditionRepository Repository for gamification data (energy, achievements)
 */
class GrantNotificationRewardsUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val expeditionRepository: ExpeditionRepository
) {

    /**
     * Result of granting notification rewards.
     *
     * @property energyGranted Amount of energy granted for this session
     * @property achievementsUnlocked List of achievement IDs that were unlocked
     * @property totalBlockedNotifications Total notifications blocked (for achievement tracking)
     */
    data class RewardResult(
        val energyGranted: Int,
        val achievementsUnlocked: List<String>,
        val totalBlockedNotifications: Int
    )

    /**
     * Grant rewards for notifications blocked during a session.
     *
     * Energy bonus: +1 energy per 5 notifications blocked in the session.
     * Also updates progress for notification achievements.
     *
     * @param sessionId The ID of the session that ended
     * @return Result containing energy granted and any achievements unlocked
     */
    suspend operator fun invoke(sessionId: String): RewardResult {
        val sessionNotificationCount = notificationRepository.getCountForSession(sessionId)
        val totalNotificationCount = notificationRepository.getTotalBlockedCount()

        Timber.d("GrantNotificationRewards: session=$sessionId, sessionCount=$sessionNotificationCount, totalCount=$totalNotificationCount")

        // Calculate energy bonus: +1 energy per 5 notifications blocked
        val energyBonus = sessionNotificationCount / NOTIFICATIONS_PER_ENERGY
        if (energyBonus > 0) {
            expeditionRepository.addEnergy(energyBonus)
            Timber.d("Granted $energyBonus energy for $sessionNotificationCount blocked notifications")
        }

        // Update achievement progress and track unlocks
        val unlockedAchievements = mutableListOf<String>()

        // Check each notification achievement
        NOTIFICATION_ACHIEVEMENTS.forEach { (achievementId, target) ->
            val achievement = expeditionRepository.getAchievement(achievementId)
            if (achievement != null && achievement.unlockedAt == null) {
                // Update progress
                expeditionRepository.updateAchievementProgress(achievementId, totalNotificationCount)

                // Check if just unlocked (progress reached target)
                val updatedAchievement = expeditionRepository.getAchievement(achievementId)
                if (updatedAchievement?.unlockedAt != null) {
                    unlockedAchievements.add(achievementId)
                    Timber.d("Achievement unlocked: $achievementId")
                }
            }
        }

        return RewardResult(
            energyGranted = energyBonus,
            achievementsUnlocked = unlockedAchievements,
            totalBlockedNotifications = totalNotificationCount
        )
    }

    companion object {
        /** Number of notifications needed to earn 1 energy */
        const val NOTIFICATIONS_PER_ENERGY = 5

        /** Map of notification achievement IDs to their targets */
        val NOTIFICATION_ACHIEVEMENTS = mapOf(
            "shield_mind" to 100,   // Escudo Mental: 100 notifications
            "fortress" to 500,      // Fortaleza: 500 notifications
            "immune" to 1000        // Inmune: 1000 notifications
        )
    }
}
