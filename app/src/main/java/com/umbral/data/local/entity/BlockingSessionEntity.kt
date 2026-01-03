package com.umbral.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "blocking_sessions")
data class BlockingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: String,
    val startedAt: LocalDateTime = LocalDateTime.now(),
    val endedAt: LocalDateTime? = null,
    val blockedAttempts: Int = 0,
    val unlockMethod: String? = null
)
