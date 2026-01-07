package com.umbral.notifications.data.mapper

import com.umbral.notifications.data.local.BlockedNotificationEntity
import com.umbral.notifications.domain.model.BlockedNotification
import java.time.Instant

/**
 * Mapper to convert between Entity and Domain models.
 * Handles the conversion between database Long timestamps and domain Instant timestamps.
 */

/**
 * Convert BlockedNotificationEntity to domain model BlockedNotification.
 * Converts Long timestamp to Instant for type-safe date handling.
 */
fun BlockedNotificationEntity.toDomain(): BlockedNotification {
    return BlockedNotification(
        id = id,
        sessionId = sessionId,
        packageName = packageName,
        appName = appName,
        title = title,
        text = text,
        timestamp = Instant.ofEpochMilli(timestamp),
        iconUri = iconUri,
        isRead = isRead
    )
}

/**
 * Convert domain model BlockedNotification to BlockedNotificationEntity.
 * Converts Instant timestamp to Long for database storage.
 */
fun BlockedNotification.toEntity(): BlockedNotificationEntity {
    return BlockedNotificationEntity(
        id = id,
        sessionId = sessionId,
        packageName = packageName,
        appName = appName,
        title = title,
        text = text,
        timestamp = timestamp.toEpochMilli(),
        iconUri = iconUri,
        isRead = isRead
    )
}
