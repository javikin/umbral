package com.umbral.integration

import app.cash.turbine.test
import com.umbral.data.local.dao.NfcTagDao
import com.umbral.data.local.entity.NfcTagEntity
import com.umbral.data.nfc.NfcRepositoryImpl
import com.umbral.domain.nfc.NfcRepository
import com.umbral.domain.nfc.NfcTag
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant

/**
 * Integration tests for NFC repository and DAO layer.
 * Tests tag storage, retrieval, and profile linking flows.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class NfcRepositoryIntegrationTest {

    private lateinit var nfcTagDao: NfcTagDao
    private lateinit var nfcRepository: NfcRepository

    private val testProfileId = "test-profile-id"
    private val testTag1Uid = "04E1A2B3C4D5E6"
    private val testTag2Uid = "04F1G2H3I4J5K6"

    @Before
    fun setup() {
        nfcTagDao = mockk(relaxed = true)
        nfcRepository = NfcRepositoryImpl(nfcTagDao)
    }

    // ==================== TAG REGISTRATION FLOW ====================

    @Test
    fun `register new nfc tag and link to profile`() = runTest {
        // Given: New tag
        val newTag = NfcTag(
            id = "tag-1",
            uid = testTag1Uid,
            name = "Office Tag",
            location = "Desk",
            profileId = null,
            createdAt = Instant.now(),
            lastUsedAt = null,
            useCount = 0
        )

        coEvery { nfcTagDao.insertTag(any()) } just Runs

        // When: Insert tag
        val insertResult = nfcRepository.insertTag(newTag)

        // Then: Tag is inserted
        assertTrue(insertResult.isSuccess)
        coVerify(exactly = 1) { nfcTagDao.insertTag(any()) }

        // When: Link to profile (setup mock to return tag)
        coEvery { nfcTagDao.getTagById("tag-1") } returns newTag.toEntity()
        coEvery { nfcTagDao.updateTag(any()) } just Runs

        val linkResult = nfcRepository.linkTagToProfile("tag-1", testProfileId)

        // Then: Tag is linked
        assertTrue(linkResult.isSuccess)
        coVerify(exactly = 1) { nfcTagDao.updateTag(match { it.profileId == testProfileId }) }
    }

    @Test
    fun `get tag by uid returns correct tag`() = runTest {
        // Given: Tag in database
        val tagEntity = NfcTagEntity(
            id = "tag-1",
            uid = testTag1Uid,
            name = "Test Tag",
            location = "Test",
            profileId = testProfileId,
            createdAt = System.currentTimeMillis(),
            lastUsedAt = null,
            useCount = 5
        )

        coEvery { nfcTagDao.getTagByUid(testTag1Uid) } returns tagEntity

        // When: Get tag by UID
        val result = nfcRepository.getTagByUid(testTag1Uid)

        // Then: Tag is found
        assertNotNull(result)
        assertEquals("tag-1", result?.id)
        assertEquals(testTag1Uid, result?.uid)
        assertEquals(testProfileId, result?.profileId)
        assertEquals(5, result?.useCount)
    }

    @Test
    fun `get tags for profile returns all linked tags`() = runTest {
        // Given: Multiple tags for same profile
        val tag1 = NfcTagEntity(
            id = "tag-1",
            uid = testTag1Uid,
            name = "Office Tag",
            location = "Desk",
            profileId = testProfileId,
            createdAt = System.currentTimeMillis(),
            lastUsedAt = null,
            useCount = 3
        )

        val tag2 = NfcTagEntity(
            id = "tag-2",
            uid = testTag2Uid,
            name = "Home Tag",
            location = "Door",
            profileId = testProfileId,
            createdAt = System.currentTimeMillis(),
            lastUsedAt = null,
            useCount = 7
        )

        every { nfcTagDao.getTagsForProfile(testProfileId) } returns flowOf(listOf(tag1, tag2))

        // When: Get tags for profile
        nfcRepository.getTagsForProfile(testProfileId).test {
            val tags = awaitItem()

            // Then: All tags are returned
            assertEquals(2, tags.size)
            assertTrue(tags.all { it.profileId == testProfileId })
            assertTrue(tags.any { it.uid == testTag1Uid })
            assertTrue(tags.any { it.uid == testTag2Uid })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `unlink tag from profile`() = runTest {
        // Given: Tag linked to profile
        val linkedTag = NfcTag(
            id = "tag-1",
            uid = testTag1Uid,
            name = "Test Tag",
            location = "Test",
            profileId = testProfileId,
            createdAt = Instant.now(),
            lastUsedAt = null,
            useCount = 0
        )

        coEvery { nfcTagDao.getTagById("tag-1") } returns linkedTag.toEntity()
        coEvery { nfcTagDao.updateTag(any()) } just Runs

        // When: Unlink tag
        val result = nfcRepository.unlinkTagFromProfile("tag-1")

        // Then: Tag is unlinked
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            nfcTagDao.updateTag(match { it.profileId == null })
        }
    }

    @Test
    fun `update last used timestamp`() = runTest {
        // Given: Tag exists
        coEvery { nfcTagDao.updateLastUsed(testTag1Uid, any()) } just Runs

        // When: Update last used
        nfcRepository.updateLastUsed(testTag1Uid)

        // Then: DAO is called
        coVerify(exactly = 1) { nfcTagDao.updateLastUsed(testTag1Uid, any()) }
    }

    // ==================== TAG COUNT FLOWS ====================

    @Test
    fun `get tag count returns correct count`() = runTest {
        // Given: Tags in database
        coEvery { nfcTagDao.getTagCount() } returns 5

        // When: Get count
        val count = nfcRepository.getTagCount()

        // Then: Count is correct
        assertEquals(5, count)
    }

    @Test
    fun `get tag count for profile returns correct count`() = runTest {
        // Given: Tags for profile
        coEvery { nfcTagDao.getTagCountForProfile(testProfileId) } returns 3

        // When: Get count for profile
        val count = nfcRepository.getTagCountForProfile(testProfileId)

        // Then: Count is correct
        assertEquals(3, count)
    }

    // ==================== ERROR HANDLING ====================

    @Test
    fun `insert tag handles dao exception`() = runTest {
        // Given: DAO throws exception
        val newTag = NfcTag(
            id = "tag-1",
            uid = testTag1Uid,
            name = "Test",
            location = "Test",
            profileId = null,
            createdAt = Instant.now(),
            lastUsedAt = null,
            useCount = 0
        )

        coEvery { nfcTagDao.insertTag(any()) } throws RuntimeException("Database error")

        // When: Try to insert
        val result = nfcRepository.insertTag(newTag)

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertEquals("Database error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `link tag to non-existent tag returns failure`() = runTest {
        // Given: Tag doesn't exist
        coEvery { nfcTagDao.getTagById("non-existent") } returns null

        // When: Try to link
        val result = nfcRepository.linkTagToProfile("non-existent", testProfileId)

        // Then: Result is failure
        assertTrue(result.isFailure)
    }

    @Test
    fun `delete tag success`() = runTest {
        // Given: DAO deletes tag successfully
        coEvery { nfcTagDao.deleteTagById("tag-1") } just Runs

        // When: Delete tag
        val result = nfcRepository.deleteTag("tag-1")

        // Then: Tag is deleted
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { nfcTagDao.deleteTagById("tag-1") }
    }

    @Test
    fun `delete tag handles dao exception`() = runTest {
        // Given: DAO throws exception
        coEvery { nfcTagDao.deleteTagById("tag-1") } throws RuntimeException("Database error")

        // When: Try to delete
        val result = nfcRepository.deleteTag("tag-1")

        // Then: Result is failure
        assertTrue(result.isFailure)
        assertEquals("Database error", result.exceptionOrNull()?.message)
    }

    // ==================== Helper Functions ====================

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
}
