package com.umbral.data.nfc

import com.umbral.data.local.dao.NfcTagDao
import com.umbral.data.local.entity.NfcTagEntity
import com.umbral.domain.nfc.NfcRepository
import com.umbral.domain.nfc.NfcTag
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NfcRepositoryImpl @Inject constructor(
    private val nfcTagDao: NfcTagDao
) : NfcRepository {

    override fun getAllTags(): Flow<List<NfcTag>> {
        return nfcTagDao.getAllTags().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTagsForProfile(profileId: String): Flow<List<NfcTag>> {
        return nfcTagDao.getTagsForProfile(profileId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getTagById(id: String): NfcTag? {
        return nfcTagDao.getTagById(id)?.toDomain()
    }

    override suspend fun getTagByUid(uid: String): NfcTag? {
        return nfcTagDao.getTagByUid(uid)?.toDomain()
    }

    override suspend fun insertTag(tag: NfcTag): Result<Unit> {
        return try {
            nfcTagDao.insertTag(tag.toEntity())
            Timber.d("Tag inserted: ${tag.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error inserting tag")
            Result.failure(e)
        }
    }

    override suspend fun updateTag(tag: NfcTag): Result<Unit> {
        return try {
            nfcTagDao.updateTag(tag.toEntity())
            Timber.d("Tag updated: ${tag.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error updating tag")
            Result.failure(e)
        }
    }

    override suspend fun deleteTag(tagId: String): Result<Unit> {
        return try {
            nfcTagDao.deleteTagById(tagId)
            Timber.d("Tag deleted: $tagId")
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting tag")
            Result.failure(e)
        }
    }

    override suspend fun updateLastUsed(uid: String) {
        try {
            nfcTagDao.updateLastUsed(uid, System.currentTimeMillis())
            Timber.d("Tag last used updated: $uid")
        } catch (e: Exception) {
            Timber.e(e, "Error updating last used")
        }
    }

    override suspend fun linkTagToProfile(tagId: String, profileId: String): Result<Unit> {
        return try {
            val tag = nfcTagDao.getTagById(tagId)
            if (tag != null) {
                val updatedTag = tag.copy(profileId = profileId)
                nfcTagDao.updateTag(updatedTag)
                Timber.d("Tag $tagId linked to profile $profileId")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Tag not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error linking tag to profile")
            Result.failure(e)
        }
    }

    override suspend fun unlinkTagFromProfile(tagId: String): Result<Unit> {
        return try {
            val tag = nfcTagDao.getTagById(tagId)
            if (tag != null) {
                val updatedTag = tag.copy(profileId = null)
                nfcTagDao.updateTag(updatedTag)
                Timber.d("Tag $tagId unlinked from profile")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Tag not found"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error unlinking tag from profile")
            Result.failure(e)
        }
    }

    override suspend fun getTagCount(): Int {
        return nfcTagDao.getTagCount()
    }

    override suspend fun getTagCountForProfile(profileId: String): Int {
        return nfcTagDao.getTagCountForProfile(profileId)
    }
}

// Extension functions for mapping
private fun NfcTagEntity.toDomain(): NfcTag {
    return NfcTag(
        id = id,
        uid = uid,
        name = name,
        location = location,
        profileId = profileId,
        createdAt = Instant.ofEpochMilli(createdAt),
        lastUsedAt = lastUsedAt?.let { Instant.ofEpochMilli(it) },
        useCount = useCount
    )
}

private fun NfcTag.toEntity(): NfcTagEntity {
    return NfcTagEntity(
        id = id,
        uid = uid,
        name = name,
        location = location,
        profileId = profileId,
        createdAt = createdAt.toEpochMilli(),
        lastUsedAt = lastUsedAt?.toEpochMilli(),
        useCount = useCount
    )
}
