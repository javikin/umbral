package com.umbral.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "nfc_tags",
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
data class NfcTagEntity(
    @PrimaryKey
    val id: String,
    val profileId: String,
    val tagUid: String,
    val tagType: String = "NTAG213",
    val label: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val lastUsedAt: LocalDateTime? = null
)
