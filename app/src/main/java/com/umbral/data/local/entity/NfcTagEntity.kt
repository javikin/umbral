package com.umbral.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "nfc_tags",
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["profile_id"])
    ]
)
data class NfcTagEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "uid")
    val uid: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "location")
    val location: String? = null,

    @ColumnInfo(name = "profile_id")
    val profileId: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long, // epoch millis

    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: Long? = null,

    @ColumnInfo(name = "use_count")
    val useCount: Int = 0
)
