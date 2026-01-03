package com.umbral.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "blocked_attempts")
data class BlockedAttemptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val profileId: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val wasUnlocked: Boolean = false,
    val unlockMethod: String? = null  // "nfc", "qr", "timer"
)
