package com.umbral.domain.blocking

import kotlinx.coroutines.flow.Flow

/**
 * Repository for blocking profile operations.
 */
interface ProfileRepository {

    /**
     * Get all profiles.
     */
    fun getAllProfiles(): Flow<List<BlockingProfile>>

    /**
     * Get the currently active profile.
     */
    fun getActiveProfile(): Flow<BlockingProfile?>

    /**
     * Get a profile by its ID.
     */
    suspend fun getProfileById(id: String): BlockingProfile?

    /**
     * Insert or update a profile.
     */
    suspend fun saveProfile(profile: BlockingProfile): Result<Unit>

    /**
     * Delete a profile.
     */
    suspend fun deleteProfile(profileId: String): Result<Unit>

    /**
     * Activate a profile (deactivates all others).
     */
    suspend fun activateProfile(profileId: String): Result<Unit>

    /**
     * Deactivate all profiles.
     */
    suspend fun deactivateAllProfiles(): Result<Unit>
}
