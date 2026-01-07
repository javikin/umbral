package com.umbral.notifications.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for storing blocked notifications.
 * Stores notifications that were intercepted during a blocking session.
 *
 * @property id Auto-generated primary key
 * @property sessionId ID of the blocking session this notification belongs to
 * @property packageName Package name of the app that sent the notification
 * @property appName Human-readable name of the app
 * @property title Notification title (nullable)
 * @property text Notification text content (nullable)
 * @property timestamp Unix timestamp in milliseconds when notification was blocked
 * @property iconUri URI to the notification icon (nullable)
 * @property isRead Whether the user has viewed this notification
 */
@Entity(
    tableName = "blocked_notifications",
    indices = [
        Index(value = ["session_id"]),
        Index(value = ["package_name"]),
        Index(value = ["timestamp"])
    ]
)
data class BlockedNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "session_id")
    val sessionId: String,

    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "app_name")
    val appName: String,

    @ColumnInfo(name = "title")
    val title: String?,

    @ColumnInfo(name = "text")
    val text: String?,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "icon_uri")
    val iconUri: String?,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false
)
