package com.umbral.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.umbral.data.local.entity.BlockingProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockingProfileDao {

    @Query("SELECT * FROM blocking_profiles ORDER BY updatedAt DESC")
    fun getAllProfiles(): Flow<List<BlockingProfileEntity>>

    @Query("SELECT * FROM blocking_profiles WHERE id = :id")
    suspend fun getProfileById(id: String): BlockingProfileEntity?

    @Query("SELECT * FROM blocking_profiles WHERE isActive = 1 LIMIT 1")
    fun getActiveProfile(): Flow<BlockingProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: BlockingProfileEntity)

    @Update
    suspend fun updateProfile(profile: BlockingProfileEntity)

    @Delete
    suspend fun deleteProfile(profile: BlockingProfileEntity)

    @Query("UPDATE blocking_profiles SET isActive = 0")
    suspend fun deactivateAllProfiles()

    @Query("UPDATE blocking_profiles SET isActive = 1 WHERE id = :profileId")
    suspend fun activateProfile(profileId: String)
}
