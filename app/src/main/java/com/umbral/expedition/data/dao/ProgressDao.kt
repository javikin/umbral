package com.umbral.expedition.data.dao

import androidx.room.*
import com.umbral.expedition.data.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for player progress operations.
 *
 * This DAO manages the singleton progress entity (id=1)
 * and provides atomic update operations for progress tracking.
 */
@Dao
interface ProgressDao {
    @Query("SELECT * FROM player_progress WHERE id = 1 LIMIT 1")
    fun getProgress(): Flow<ProgressEntity?>

    @Query("SELECT * FROM player_progress WHERE id = 1 LIMIT 1")
    suspend fun getProgressOnce(): ProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(progress: ProgressEntity)

    @Update
    suspend fun update(progress: ProgressEntity)

    @Query("UPDATE player_progress SET current_xp = current_xp + :xp WHERE id = 1")
    suspend fun addXp(xp: Int)

    @Query("UPDATE player_progress SET total_energy = total_energy + :energy WHERE id = 1")
    suspend fun addEnergy(energy: Int)

    @Query("UPDATE player_progress SET stars = stars + :stars WHERE id = 1")
    suspend fun addStars(stars: Int)

    @Query("UPDATE player_progress SET current_streak = :streak, longest_streak = CASE WHEN :streak > longest_streak THEN :streak ELSE longest_streak END WHERE id = 1")
    suspend fun updateStreak(streak: Int)

    @Query("UPDATE player_progress SET total_blocking_minutes = total_blocking_minutes + :minutes WHERE id = 1")
    suspend fun addBlockingMinutes(minutes: Int)

    @Query("UPDATE player_progress SET level = :level WHERE id = 1")
    suspend fun updateLevel(level: Int)
}
