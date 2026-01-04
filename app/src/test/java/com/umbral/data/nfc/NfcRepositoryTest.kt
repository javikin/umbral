package com.umbral.data.nfc

import app.cash.turbine.test
import com.umbral.data.local.dao.NfcTagDao
import com.umbral.data.local.entity.NfcTagEntity
import com.umbral.domain.nfc.NfcTag
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class NfcRepositoryTest {

    private lateinit var nfcTagDao: NfcTagDao
    private lateinit var repository: NfcRepositoryImpl

    private val testInstant = Instant.now()
    private val testEntity = NfcTagEntity(
        id = "tag-id",
        uid = "04:A1:B2:C3:D4:E5:F6",
        name = "Front Door Tag",
        location = "Home",
        profileId = "profile-id",
        createdAt = testInstant.toEpochMilli(),
        lastUsedAt = testInstant.toEpochMilli(),
        useCount = 5
    )

    private val testTag = NfcTag(
        id = "tag-id",
        uid = "04:A1:B2:C3:D4:E5:F6",
        name = "Front Door Tag",
        location = "Home",
        profileId = "profile-id",
        createdAt = testInstant,
        lastUsedAt = testInstant,
        useCount = 5
    )

    @Before
    fun setup() {
        nfcTagDao = mockk(relaxed = true)
        repository = NfcRepositoryImpl(nfcTagDao)
    }

    // getAllTags Tests
    @Test
    fun `getAllTags returns empty list when no tags exist`() = runTest {
        // Given
        every { nfcTagDao.getAllTags() } returns flowOf(emptyList())

        // When
        repository.getAllTags().test {
            // Then
            val tags = awaitItem()
            assertEquals(0, tags.size)
            awaitComplete()
        }
    }

    @Test
    fun `getAllTags returns list of tags`() = runTest {
        // Given
        val entities = listOf(
            testEntity,
            testEntity.copy(id = "tag-id-2", name = "Back Door Tag")
        )
        every { nfcTagDao.getAllTags() } returns flowOf(entities)

        // When
        repository.getAllTags().test {
            // Then
            val tags = awaitItem()
            assertEquals(2, tags.size)
            assertEquals("Front Door Tag", tags[0].name)
            assertEquals("Back Door Tag", tags[1].name)
            awaitComplete()
        }
    }

    // getTagsForProfile Tests
    @Test
    fun `getTagsForProfile returns only tags linked to profile`() = runTest {
        // Given
        val profileTags = listOf(testEntity)
        every { nfcTagDao.getTagsForProfile("profile-id") } returns flowOf(profileTags)

        // When
        repository.getTagsForProfile("profile-id").test {
            // Then
            val tags = awaitItem()
            assertEquals(1, tags.size)
            assertEquals("profile-id", tags[0].profileId)
            awaitComplete()
        }
    }

    @Test
    fun `getTagsForProfile returns empty list when no tags for profile`() = runTest {
        // Given
        every { nfcTagDao.getTagsForProfile("nonexistent-profile") } returns flowOf(emptyList())

        // When
        repository.getTagsForProfile("nonexistent-profile").test {
            // Then
            val tags = awaitItem()
            assertEquals(0, tags.size)
            awaitComplete()
        }
    }

    // getTagById Tests
    @Test
    fun `getTagById returns null when tag not found`() = runTest {
        // Given
        coEvery { nfcTagDao.getTagById("nonexistent") } returns null

        // When
        val result = repository.getTagById("nonexistent")

        // Then
        assertNull(result)
    }

    @Test
    fun `getTagById returns tag when found`() = runTest {
        // Given
        coEvery { nfcTagDao.getTagById("tag-id") } returns testEntity

        // When
        val result = repository.getTagById("tag-id")

        // Then
        assertNotNull(result)
        assertEquals("tag-id", result?.id)
        assertEquals("Front Door Tag", result?.name)
    }

    // getTagByUid Tests
    @Test
    fun `getTagByUid returns null when tag not found`() = runTest {
        // Given
        coEvery { nfcTagDao.getTagByUid("unknown-uid") } returns null

        // When
        val result = repository.getTagByUid("unknown-uid")

        // Then
        assertNull(result)
    }

    @Test
    fun `getTagByUid returns tag when found`() = runTest {
        // Given
        coEvery { nfcTagDao.getTagByUid("04:A1:B2:C3:D4:E5:F6") } returns testEntity

        // When
        val result = repository.getTagByUid("04:A1:B2:C3:D4:E5:F6")

        // Then
        assertNotNull(result)
        assertEquals("04:A1:B2:C3:D4:E5:F6", result?.uid)
        assertEquals("Front Door Tag", result?.name)
    }

    // insertTag Tests
    @Test
    fun `insertTag successfully inserts tag`() = runTest {
        // Given
        coEvery { nfcTagDao.insertTag(any()) } just Runs

        // When
        val result = repository.insertTag(testTag)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { nfcTagDao.insertTag(any()) }
    }

    @Test
    fun `insertTag returns failure when dao throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { nfcTagDao.insertTag(any()) } throws exception

        // When
        val result = repository.insertTag(testTag)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // updateTag Tests
    @Test
    fun `updateTag successfully updates tag`() = runTest {
        // Given
        coEvery { nfcTagDao.updateTag(any()) } just Runs

        // When
        val result = repository.updateTag(testTag)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { nfcTagDao.updateTag(any()) }
    }

    @Test
    fun `updateTag returns failure when dao throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { nfcTagDao.updateTag(any()) } throws exception

        // When
        val result = repository.updateTag(testTag)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // deleteTag Tests
    @Test
    fun `deleteTag successfully deletes tag`() = runTest {
        // Given
        coEvery { nfcTagDao.deleteTagById("tag-id") } just Runs

        // When
        val result = repository.deleteTag("tag-id")

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { nfcTagDao.deleteTagById("tag-id") }
    }

    @Test
    fun `deleteTag returns failure when dao throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { nfcTagDao.deleteTagById("tag-id") } throws exception

        // When
        val result = repository.deleteTag("tag-id")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // updateLastUsed Tests
    @Test
    fun `updateLastUsed updates timestamp successfully`() = runTest {
        // Given
        val timestampSlot = slot<Long>()
        coEvery { nfcTagDao.updateLastUsed("04:A1:B2:C3:D4:E5:F6", capture(timestampSlot)) } just Runs

        // When
        repository.updateLastUsed("04:A1:B2:C3:D4:E5:F6")

        // Then
        coVerify(exactly = 1) { nfcTagDao.updateLastUsed("04:A1:B2:C3:D4:E5:F6", any()) }
        assertTrue(timestampSlot.captured > 0)
    }

    @Test
    fun `updateLastUsed handles exception gracefully`() = runTest {
        // Given
        coEvery { nfcTagDao.updateLastUsed(any(), any()) } throws RuntimeException("Database error")

        // When - Should not throw
        repository.updateLastUsed("04:A1:B2:C3:D4:E5:F6")

        // Then - Method completes without exception
        coVerify(exactly = 1) { nfcTagDao.updateLastUsed(any(), any()) }
    }

    // linkTagToProfile Tests
    @Test
    fun `linkTagToProfile successfully links tag to profile`() = runTest {
        // Given
        val unlinkedTag = testEntity.copy(profileId = null)
        coEvery { nfcTagDao.getTagById("tag-id") } returns unlinkedTag
        coEvery { nfcTagDao.updateTag(any()) } just Runs

        // When
        val result = repository.linkTagToProfile("tag-id", "new-profile-id")

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            nfcTagDao.updateTag(
                match { it.id == "tag-id" && it.profileId == "new-profile-id" }
            )
        }
    }

    @Test
    fun `linkTagToProfile returns failure when tag not found`() = runTest {
        // Given
        coEvery { nfcTagDao.getTagById("nonexistent") } returns null

        // When
        val result = repository.linkTagToProfile("nonexistent", "profile-id")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Tag not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `linkTagToProfile returns failure when dao throws exception`() = runTest {
        // Given
        coEvery { nfcTagDao.getTagById("tag-id") } returns testEntity
        val exception = RuntimeException("Database error")
        coEvery { nfcTagDao.updateTag(any()) } throws exception

        // When
        val result = repository.linkTagToProfile("tag-id", "profile-id")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // unlinkTagFromProfile Tests
    @Test
    fun `unlinkTagFromProfile successfully unlinks tag from profile`() = runTest {
        // Given
        coEvery { nfcTagDao.getTagById("tag-id") } returns testEntity
        coEvery { nfcTagDao.updateTag(any()) } just Runs

        // When
        val result = repository.unlinkTagFromProfile("tag-id")

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            nfcTagDao.updateTag(
                match { it.id == "tag-id" && it.profileId == null }
            )
        }
    }

    @Test
    fun `unlinkTagFromProfile returns failure when tag not found`() = runTest {
        // Given
        coEvery { nfcTagDao.getTagById("nonexistent") } returns null

        // When
        val result = repository.unlinkTagFromProfile("nonexistent")

        // Then
        assertTrue(result.isFailure)
        assertEquals("Tag not found", result.exceptionOrNull()?.message)
    }

    // getTagCount Tests
    @Test
    fun `getTagCount returns correct count`() = runTest {
        // Given
        coEvery { nfcTagDao.getTagCount() } returns 10

        // When
        val count = repository.getTagCount()

        // Then
        assertEquals(10, count)
    }

    @Test
    fun `getTagCount returns zero when no tags`() = runTest {
        // Given
        coEvery { nfcTagDao.getTagCount() } returns 0

        // When
        val count = repository.getTagCount()

        // Then
        assertEquals(0, count)
    }

    // getTagCountForProfile Tests
    @Test
    fun `getTagCountForProfile returns correct count`() = runTest {
        // Given
        coEvery { nfcTagDao.getTagCountForProfile("profile-id") } returns 3

        // When
        val count = repository.getTagCountForProfile("profile-id")

        // Then
        assertEquals(3, count)
    }

    @Test
    fun `getTagCountForProfile returns zero when no tags for profile`() = runTest {
        // Given
        coEvery { nfcTagDao.getTagCountForProfile("profile-id") } returns 0

        // When
        val count = repository.getTagCountForProfile("profile-id")

        // Then
        assertEquals(0, count)
    }

    // Domain-Entity Mapping Tests
    @Test
    fun `entity to domain mapping preserves all fields`() = runTest {
        // Given
        every { nfcTagDao.getAllTags() } returns flowOf(listOf(testEntity))

        // When
        repository.getAllTags().test {
            val tags = awaitItem()
            val tag = tags.first()

            // Then
            assertEquals(testEntity.id, tag.id)
            assertEquals(testEntity.uid, tag.uid)
            assertEquals(testEntity.name, tag.name)
            assertEquals(testEntity.location, tag.location)
            assertEquals(testEntity.profileId, tag.profileId)
            assertEquals(testEntity.createdAt, tag.createdAt.toEpochMilli())
            assertEquals(testEntity.lastUsedAt, tag.lastUsedAt?.toEpochMilli())
            assertEquals(testEntity.useCount, tag.useCount)
            awaitComplete()
        }
    }

    @Test
    fun `entity to domain mapping handles null lastUsedAt`() = runTest {
        // Given
        val entityWithoutLastUsed = testEntity.copy(lastUsedAt = null)
        every { nfcTagDao.getAllTags() } returns flowOf(listOf(entityWithoutLastUsed))

        // When
        repository.getAllTags().test {
            val tags = awaitItem()
            val tag = tags.first()

            // Then
            assertNull(tag.lastUsedAt)
            awaitComplete()
        }
    }
}
