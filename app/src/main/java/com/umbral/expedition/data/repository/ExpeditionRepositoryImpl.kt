package com.umbral.expedition.data.repository

import com.umbral.expedition.data.dao.AchievementDao
import com.umbral.expedition.data.dao.CompanionDao
import com.umbral.expedition.data.dao.LocationDao
import com.umbral.expedition.data.dao.ProgressDao
import com.umbral.expedition.data.entity.AchievementEntity
import com.umbral.expedition.data.entity.CompanionEntity
import com.umbral.expedition.data.entity.LocationEntity
import com.umbral.expedition.data.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ExpeditionRepository.
 *
 * Coordinates data access across multiple DAOs and provides
 * business logic for expedition system operations.
 */
@Singleton
class ExpeditionRepositoryImpl @Inject constructor(
    private val companionDao: CompanionDao,
    private val locationDao: LocationDao,
    private val progressDao: ProgressDao,
    private val achievementDao: AchievementDao
) : ExpeditionRepository {

    // ========== Progress ==========

    override fun getProgress(): Flow<ProgressEntity?> {
        return progressDao.getProgress()
    }

    override suspend fun addEnergy(amount: Int) {
        // Initialize progress if it doesn't exist
        ensureProgressExists()
        progressDao.addEnergy(amount)
    }

    override suspend fun addXp(amount: Int) {
        ensureProgressExists()
        progressDao.addXp(amount)
    }

    override suspend fun updateStreak(streak: Int) {
        ensureProgressExists()
        progressDao.updateStreak(streak)
    }

    /**
     * Ensure progress entity exists (create if first time)
     */
    private suspend fun ensureProgressExists() {
        val existing = progressDao.getProgressOnce()
        if (existing == null) {
            progressDao.insert(ProgressEntity())
        }
    }

    // ========== Companions ==========

    override fun getAllCompanions(): Flow<List<CompanionEntity>> {
        return companionDao.getAllCompanions()
    }

    override fun getActiveCompanion(): Flow<CompanionEntity?> {
        return companionDao.getActiveCompanion()
    }

    override suspend fun captureCompanion(type: String) {
        // Check if companion of this type already exists
        val existing = companionDao.getByType(type)
        if (existing != null) {
            return // Already captured this type
        }

        // Create new companion
        val companion = CompanionEntity(
            id = UUID.randomUUID().toString(),
            type = type,
            name = null,
            evolutionState = 1,
            energyInvested = 0,
            capturedAt = System.currentTimeMillis(),
            isActive = false
        )

        companionDao.insert(companion)
    }

    override suspend fun evolveCompanion(id: String) {
        val companion = companionDao.getByType(id)
        if (companion == null || companion.evolutionState >= 3) {
            return // Companion not found or already at max evolution
        }

        val newState = companion.evolutionState + 1
        val requiredEnergy = when (newState) {
            2 -> 500
            3 -> 1500
            else -> 0
        }

        companionDao.evolve(id, newState, requiredEnergy)
    }

    override suspend fun setActiveCompanion(id: String) {
        // Deactivate all companions first
        companionDao.deactivateAll()
        // Activate the selected one
        companionDao.setActive(id)
    }

    // ========== Locations ==========

    override fun getDiscoveredLocations(): Flow<List<LocationEntity>> {
        return locationDao.getDiscoveredLocations()
    }

    override suspend fun discoverLocation(locationId: String, biomeId: String, energyCost: Int) {
        // Check if already discovered
        val existing = locationDao.getById(locationId)
        if (existing != null) {
            return // Already discovered
        }

        val location = LocationEntity(
            id = locationId,
            biomeId = biomeId,
            discoveredAt = System.currentTimeMillis(),
            energySpent = energyCost,
            loreRead = false
        )

        locationDao.insert(location)
    }

    override suspend fun getDiscoveryCount(): Int {
        return locationDao.getDiscoveryCount()
    }

    // ========== Achievements ==========

    override fun getAchievements(): Flow<List<AchievementEntity>> {
        return achievementDao.getAllAchievements()
    }

    override suspend fun getAchievement(id: String): AchievementEntity? {
        return achievementDao.getAchievementById(id)
    }

    override fun getUnlockedAchievements(): Flow<List<AchievementEntity>> {
        return achievementDao.getUnlockedAchievements()
    }

    override suspend fun updateAchievementProgress(id: String, progress: Int) {
        val achievement = achievementDao.getAchievementById(id) ?: return

        // Don't update if already unlocked
        if (achievement.unlockedAt != null) return

        // Update progress
        val newProgress = maxOf(achievement.progress, progress)
        achievementDao.updateProgress(id, newProgress)

        // Check if should unlock
        if (newProgress >= achievement.target) {
            unlockAchievement(id)
        }
    }

    override suspend fun unlockAchievement(id: String): Int {
        val achievement = achievementDao.getAchievementById(id) ?: return 0

        // Already unlocked?
        if (achievement.unlockedAt != null) return 0

        // Unlock it
        val timestamp = System.currentTimeMillis()
        achievementDao.unlockAchievement(id, timestamp)

        // Award stars to player progress
        progressDao.addStars(achievement.starsReward)

        return achievement.starsReward
    }
}
