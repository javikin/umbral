package com.umbral.notifications.domain.model

import java.time.Instant

/**
 * Domain model for a blocked notification.
 * Represents a notification that was intercepted during a blocking session.
 *
 * This model is separate from the database entity to maintain clean architecture.
 * Uses Instant for type-safe timestamp handling in the domain layer.
 *
 * @property id Unique identifier
 * @property sessionId ID of the blocking session
 * @property packageName Package name of the app that sent the notification
 * @property appName Human-readable name of the app
 * @property title Notification title (nullable)
 * @property text Notification text content (nullable)
 * @property timestamp When the notification was blocked
 * @property iconUri URI to the notification icon (nullable)
 * @property isRead Whether the user has viewed this notification
 */
data class BlockedNotification(
    val id: Long = 0,
    val sessionId: String,
    val packageName: String,
    val appName: String,
    val title: String?,
    val text: String?,
    val timestamp: Instant,
    val iconUri: String?,
    val isRead: Boolean = false
)
