package com.umbral.notifications.domain.usecase

import com.umbral.notifications.domain.model.BlockedNotification
import com.umbral.notifications.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * UseCase to save a blocked notification.
 *
 * Saves the notification and automatically trims old entries to maintain
 * storage limits (keeps only most recent 1000 notifications).
 */
class SaveBlockedNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    /**
     * Save a blocked notification and trim old entries.
     * @param notification The notification to save
     * @return The row ID of the inserted notification
     */
    suspend operator fun invoke(notification: BlockedNotification): Long {
        val id = repository.save(notification)

        // Trim to limit after save to maintain storage constraints
        repository.trimToLimit(maxCount = 1000)

        return id
    }
}
