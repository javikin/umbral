package com.umbral.data.blocking

import com.umbral.data.local.dao.BlockingProfileDao
import com.umbral.data.local.entity.BlockingProfileEntity
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.domain.blocking.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val profileDao: BlockingProfileDao
) : ProfileRepository {

    override fun getAllProfiles(): Flow<List<BlockingProfile>> {
        return profileDao.getAllProfiles().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveProfile(): Flow<BlockingProfile?> {
        return profileDao.getActiveProfile().map { it?.toDomain() }
    }

    override suspend fun getProfileById(id: String): BlockingProfile? {
        return profileDao.getProfileById(id)?.toDomain()
    }

    override suspend fun saveProfile(profile: BlockingProfile): Result<Unit> {
        return try {
            val entity = profile.toEntity()
            profileDao.insertProfile(entity)
            Timber.d("Profile saved: ${profile.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error saving profile")
            Result.failure(e)
        }
    }

    override suspend fun deleteProfile(profileId: String): Result<Unit> {
        return try {
            val profile = profileDao.getProfileById(profileId)
            if (profile != null) {
                profileDao.deleteProfile(profile)
                Timber.d("Profile deleted: $profileId")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Profile not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error deleting profile")
            Result.failure(e)
        }
    }

    override suspend fun activateProfile(profileId: String): Result<Unit> {
        return try {
            profileDao.deactivateAllProfiles()
            profileDao.activateProfile(profileId)
            Timber.d("Profile activated: $profileId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error activating profile")
            Result.failure(e)
        }
    }

    override suspend fun deactivateAllProfiles(): Result<Unit> {
        return try {
            profileDao.deactivateAllProfiles()
            Timber.d("All profiles deactivated")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deactivating profiles")
            Result.failure(e)
        }
    }
}

// Extension functions for mapping
private fun BlockingProfileEntity.toDomain(): BlockingProfile {
    return BlockingProfile(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex,
        isActive = isActive,
        isStrictMode = isStrictMode,
        blockNotifications = blockNotifications,
        blockedApps = blockedApps,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun BlockingProfile.toEntity(): BlockingProfileEntity {
    return BlockingProfileEntity(
        id = id,
        name = name,
        iconName = iconName,
        colorHex = colorHex,
        isActive = isActive,
        isStrictMode = isStrictMode,
        blockNotifications = blockNotifications,
        blockedApps = blockedApps,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
