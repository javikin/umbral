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

    @Query("SELECT * FROM nfc_tags ORDER BY created_at DESC")
    fun getAllTags(): Flow<List<NfcTagEntity>>

    @Query("SELECT * FROM nfc_tags WHERE profile_id = :profileId ORDER BY created_at DESC")
    fun getTagsForProfile(profileId: String): Flow<List<NfcTagEntity>>

    @Query("SELECT * FROM nfc_tags WHERE uid = :uid LIMIT 1")
    suspend fun getTagByUid(uid: String): NfcTagEntity?

    @Query("SELECT * FROM nfc_tags WHERE id = :id LIMIT 1")
    suspend fun getTagById(id: String): NfcTagEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: NfcTagEntity)

    @Update
    suspend fun updateTag(tag: NfcTagEntity)

    @Delete
    suspend fun deleteTag(tag: NfcTagEntity)

    @Query("DELETE FROM nfc_tags WHERE id = :id")
    suspend fun deleteTagById(id: String)

    @Query("UPDATE nfc_tags SET last_used_at = :timestamp, use_count = use_count + 1 WHERE uid = :uid")
    suspend fun updateLastUsed(uid: String, timestamp: Long)

    @Query("SELECT COUNT(*) FROM nfc_tags")
    suspend fun getTagCount(): Int

    @Query("SELECT COUNT(*) FROM nfc_tags WHERE profile_id = :profileId")
    suspend fun getTagCountForProfile(profileId: String): Int
}
