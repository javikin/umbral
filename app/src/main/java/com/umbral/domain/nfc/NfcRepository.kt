package com.umbral.domain.nfc

import kotlinx.coroutines.flow.Flow

/**
 * Repository for NFC tag persistence operations.
 */
interface NfcRepository {

    /**
     * Get all registered tags.
     */
    fun getAllTags(): Flow<List<NfcTag>>

    /**
     * Get tags for a specific profile.
     */
    fun getTagsForProfile(profileId: String): Flow<List<NfcTag>>

    /**
     * Get a tag by its ID.
     */
    suspend fun getTagById(id: String): NfcTag?

    /**
     * Get a tag by its physical UID.
     */
    suspend fun getTagByUid(uid: String): NfcTag?

    /**
     * Register a new tag.
     */
    suspend fun insertTag(tag: NfcTag): Result<Unit>

    /**
     * Update an existing tag.
     */
    suspend fun updateTag(tag: NfcTag): Result<Unit>

    /**
     * Delete a tag.
     */
    suspend fun deleteTag(tagId: String): Result<Unit>

    /**
     * Update tag's last used timestamp.
     */
    suspend fun updateLastUsed(uid: String)

    /**
     * Link a tag to a profile.
     */
    suspend fun linkTagToProfile(tagId: String, profileId: String): Result<Unit>

    /**
     * Unlink a tag from its profile.
     */
    suspend fun unlinkTagFromProfile(tagId: String): Result<Unit>

    /**
     * Get tag count.
     */
    suspend fun getTagCount(): Int

    /**
     * Get tag count for a profile.
     */
    suspend fun getTagCountForProfile(profileId: String): Int
}
