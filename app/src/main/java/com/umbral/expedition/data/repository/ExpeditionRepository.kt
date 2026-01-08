package com.umbral.expedition.data.repository

import com.umbral.expedition.data.entity.AchievementEntity
import com.umbral.expedition.data.entity.CompanionEntity
import com.umbral.expedition.data.entity.LocationEntity
import com.umbral.expedition.data.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for expedition/gamification data operations.
 *
 * This repository manages all data access for the expedition system:
 * - Player progress (level, XP, energy, streaks)
 * - Companions (capture, evolution, activation)
 * - Locations (discovery, lore)
 * - Achievements (progress tracking, unlocking)
 */
interface ExpeditionRepository {

    // ========== Progress ==========

    /**
     * Get player progress as Flow for reactive updates
     */
    fun getProgress(): Flow<ProgressEntity?>

    /**
     * Get player progress once (suspend)
     */
    suspend fun getProgressOnce(): ProgressEntity?

    /**
     * Add energy to player's total (earned from blocking sessions)
     */
    suspend fun addEnergy(amount: Int)

    /**
     * Spend energy from player's total
     */
    suspend fun spendEnergy(amount: Int)

    /**
     * Add XP to player's current XP
     */
    suspend fun addXp(amount: Int)

    /**
     * Update player level
     */
    suspend fun updateLevel(level: Int)

    /**
     * Update current streak and potentially longest streak
     */
    suspend fun updateStreak(streak: Int)

    /**
     * Add blocking minutes to total
     */
    suspend fun addBlockingMinutes(minutes: Int)

    // ========== Companions ==========

    /**
     * Get all captured companions as Flow
     */
    fun getAllCompanions(): Flow<List<CompanionEntity>>

    /**
     * Get currently active companion as Flow
     */
    fun getActiveCompanion(): Flow<CompanionEntity?>

    /**
     * Get companion by ID
     */
    suspend fun getCompanionById(id: String): CompanionEntity?

    /**
     * Get companion by type
     */
    suspend fun getCompanionByType(type: String): CompanionEntity?

    /**
     * Capture a new companion of given type
     */
    suspend fun captureCompanion(type: String)

    /**
     * Evolve companion to next evolution state
     */
    suspend fun evolveCompanion(id: String)

    /**
     * Invest energy into companion for evolution progress
     */
    suspend fun investEnergyInCompanion(id: String, totalEnergy: Int)

    /**
     * Set a companion as active (deactivates all others)
     */
    suspend fun setActiveCompanion(id: String)

    // ========== Locations ==========

    /**
     * Get all discovered locations as Flow
     */
    fun getDiscoveredLocations(): Flow<List<LocationEntity>>

    /**
     * Get location by ID
     */
    suspend fun getLocationById(id: String): LocationEntity?

    /**
     * Discover a new location
     */
    suspend fun discoverLocation(locationId: String, biomeId: String, energyCost: Int)

    /**
     * Get count of discovered locations
     */
    suspend fun getDiscoveryCount(): Int

    // ========== Achievements ==========

    /**
     * Get all achievements as Flow
     */
    fun getAchievements(): Flow<List<AchievementEntity>>

    /**
     * Get single achievement by ID (for use cases)
     */
    suspend fun getAchievement(id: String): AchievementEntity?

    /**
     * Get unlocked achievements as Flow
     */
    fun getUnlockedAchievements(): Flow<List<AchievementEntity>>

    /**
     * Update achievement progress
     */
    suspend fun updateAchievementProgress(id: String, progress: Int)

    /**
     * Unlock achievement and award stars
     * @return Stars earned from unlocking the achievement
     */
    suspend fun unlockAchievement(id: String): Int
}
