package com.umbral.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.umbral.data.local.entity.NfcTagEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NfcTagDao {

    @Query("SELECT * FROM nfc_tags WHERE profileId = :profileId")
    fun getTagsForProfile(profileId: String): Flow<List<NfcTagEntity>>

    @Query("SELECT * FROM nfc_tags WHERE tagUid = :tagUid LIMIT 1")
    suspend fun getTagByUid(tagUid: String): NfcTagEntity?

    @Query("SELECT * FROM nfc_tags WHERE id = :id")
    suspend fun getTagById(id: String): NfcTagEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: NfcTagEntity)

    @Update
    suspend fun updateTag(tag: NfcTagEntity)

    @Delete
    suspend fun deleteTag(tag: NfcTagEntity)

    @Query("UPDATE nfc_tags SET lastUsedAt = :timestamp WHERE id = :tagId")
    suspend fun updateLastUsed(tagId: String, timestamp: Long)
}
