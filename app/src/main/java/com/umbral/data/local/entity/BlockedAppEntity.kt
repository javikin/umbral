package com.umbral.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "blocked_apps",
    foreignKeys = [
        ForeignKey(
            entity = BlockingProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("profileId")]
)
data class BlockedAppEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val profileId: String,
    val packageName: String,
    val appName: String,
    val isWhitelisted: Boolean = false
)
