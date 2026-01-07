package com.umbral.expedition.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.umbral.expedition.data.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for achievement persistence operations
 */
@Dao
interface AchievementDao {

    /**
     * Get all achievements as Flow for real-time updates
     */
    @Query("SELECT * FROM achievements ORDER BY category ASC, id ASC")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    /**
     * Get achievements by category
     */
    @Query("SELECT * FROM achievements WHERE category = :category ORDER BY id ASC")
    fun getAchievementsByCategory(category: String): Flow<List<AchievementEntity>>

    /**
     * Get single achievement by ID
     */
    @Query("SELECT * FROM achievements WHERE id = :id LIMIT 1")
    suspend fun getAchievementById(id: String): AchievementEntity?

    /**
     * Get unlocked achievements count
     */
    @Query("SELECT COUNT(*) FROM achievements WHERE unlocked_at IS NOT NULL")
    suspend fun getUnlockedCount(): Int

    /**
     * Get total stars earned from unlocked achievements
     */
    @Query("SELECT COALESCE(SUM(stars_reward), 0) FROM achievements WHERE unlocked_at IS NOT NULL")
    suspend fun getTotalStarsEarned(): Int

    /**
     * Insert or replace achievement
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    /**
     * Insert multiple achievements (for initial setup)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<AchievementEntity>)

    /**
     * Update achievement
     */
    @Update
    suspend fun updateAchievement(achievement: AchievementEntity)

    /**
     * Update achievement progress
     */
    @Query("UPDATE achievements SET progress = :progress WHERE id = :id")
    suspend fun updateProgress(id: String, progress: Int)

    /**
     * Unlock achievement (set unlocked_at timestamp)
     */
    @Query("UPDATE achievements SET unlocked_at = :timestamp WHERE id = :id")
    suspend fun unlockAchievement(id: String, timestamp: Long)

    /**
     * Check if achievement is unlocked
     */
    @Query("SELECT unlocked_at IS NOT NULL FROM achievements WHERE id = :id")
    suspend fun isUnlocked(id: String): Boolean

    /**
     * Get all unlocked achievements
     */
    @Query("SELECT * FROM achievements WHERE unlocked_at IS NOT NULL ORDER BY unlocked_at DESC")
    fun getUnlockedAchievements(): Flow<List<AchievementEntity>>

    /**
     * Delete all achievements (for testing/reset)
     */
    @Query("DELETE FROM achievements")
    suspend fun deleteAll()
}
