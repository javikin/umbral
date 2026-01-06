package com.umbral.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Unified entity for tracking all blocking-related events.
 * Replaces multiple separate tracking tables with a single event log.
 */
@Entity(
    tableName = "blocking_events",
    indices = [
        Index("timestamp"),
        Index("eventType"),
        Index("profileId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = BlockingProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class BlockingEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val eventType: EventType,
    val profileId: String? = null,
    val packageName: String? = null,
    val durationMinutes: Int? = null
)

/**
 * Types of blocking events that can be tracked.
 */
enum class EventType {
    /** Blocking session started */
    BLOCK_STARTED,

    /** Blocking session ended (includes duration) */
    BLOCK_ENDED,

    /** User attempted to open a blocked app */
    APP_ATTEMPT
}
