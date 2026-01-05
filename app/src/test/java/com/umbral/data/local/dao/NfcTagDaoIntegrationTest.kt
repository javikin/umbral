package com.umbral.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.umbral.data.local.database.UmbralDatabase
import com.umbral.data.local.entity.NfcTagEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NfcTagDaoIntegrationTest {

    private lateinit var database: UmbralDatabase
    private lateinit var dao: NfcTagDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            UmbralDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        dao = database.nfcTagDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTag_andRetrieveByUid_returnsCorrectTag() = runTest {
        // Given
        val tag = createTestTag(
            id = "tag-1",
            uid = "04:AB:CD:EF:12:34:56",
            name = "Home Door",
            location = "Front Door"
        )

        // When
        dao.insertTag(tag)
        val retrieved = dao.getTagByUid("04:AB:CD:EF:12:34:56")

        // Then
        assertNotNull(retrieved)
        assertEquals("tag-1", retrieved?.id)
        assertEquals("04:AB:CD:EF:12:34:56", retrieved?.uid)
        assertEquals("Home Door", retrieved?.name)
        assertEquals("Front Door", retrieved?.location)
    }

    @Test
    fun insertTag_andRetrieveById_returnsCorrectTag() = runTest {
        // Given
        val tag = createTestTag(id = "tag-1", uid = "UID-001", name = "Test Tag")

        // When
        dao.insertTag(tag)
        val retrieved = dao.getTagById("tag-1")

        // Then
        assertNotNull(retrieved)
        assertEquals("tag-1", retrieved?.id)
        assertEquals("Test Tag", retrieved?.name)
    }

    @Test
    fun getTagByUid_withNonExistentUid_returnsNull() = runTest {
        // When
        val retrieved = dao.getTagByUid("non-existent-uid")

        // Then
        assertNull(retrieved)
    }

    @Test
    fun getAllTags_returnsAllInsertedTags() = runTest {
        // Given
        val tag1 = createTestTag(id = "1", uid = "UID-001", name = "Tag 1")
        val tag2 = createTestTag(id = "2", uid = "UID-002", name = "Tag 2")
        val tag3 = createTestTag(id = "3", uid = "UID-003", name = "Tag 3")

        // When
        dao.insertTag(tag1)
        dao.insertTag(tag2)
        dao.insertTag(tag3)
        val tags = dao.getAllTags().first()

        // Then
        assertEquals(3, tags.size)
        assertTrue(tags.any { it.name == "Tag 1" })
        assertTrue(tags.any { it.name == "Tag 2" })
        assertTrue(tags.any { it.name == "Tag 3" })
    }

    @Test
    fun getAllTags_orderedByCreatedAtDesc() = runTest {
        // Given
        val now = System.currentTimeMillis()
        val tag1 = createTestTag(id = "1", uid = "UID-001", name = "Old", createdAt = now - 2000)
        val tag2 = createTestTag(id = "2", uid = "UID-002", name = "Recent", createdAt = now)
        val tag3 = createTestTag(id = "3", uid = "UID-003", name = "Medium", createdAt = now - 1000)

        // When
        dao.insertTag(tag1)
        dao.insertTag(tag2)
        dao.insertTag(tag3)
        val tags = dao.getAllTags().first()

        // Then
        assertEquals(3, tags.size)
        assertEquals("Recent", tags[0].name)
        assertEquals("Medium", tags[1].name)
        assertEquals("Old", tags[2].name)
    }

    @Test
    fun getTagsForProfile_returnsOnlyTagsForSpecificProfile() = runTest {
        // Given
        val tag1 = createTestTag(id = "1", uid = "UID-001", name = "Profile1 Tag1", profileId = "profile-1")
        val tag2 = createTestTag(id = "2", uid = "UID-002", name = "Profile1 Tag2", profileId = "profile-1")
        val tag3 = createTestTag(id = "3", uid = "UID-003", name = "Profile2 Tag", profileId = "profile-2")
        val tag4 = createTestTag(id = "4", uid = "UID-004", name = "No Profile Tag", profileId = null)

        // When
        dao.insertTag(tag1)
        dao.insertTag(tag2)
        dao.insertTag(tag3)
        dao.insertTag(tag4)
        val tagsForProfile1 = dao.getTagsForProfile("profile-1").first()

        // Then
        assertEquals(2, tagsForProfile1.size)
        assertTrue(tagsForProfile1.all { it.profileId == "profile-1" })
        assertTrue(tagsForProfile1.any { it.name == "Profile1 Tag1" })
        assertTrue(tagsForProfile1.any { it.name == "Profile1 Tag2" })
    }

    @Test
    fun updateTag_updatesExistingTag() = runTest {
        // Given
        val tag = createTestTag(id = "tag-1", uid = "UID-001", name = "Original Name")
        dao.insertTag(tag)

        // When
        val updated = tag.copy(name = "Updated Name", location = "New Location")
        dao.updateTag(updated)
        val retrieved = dao.getTagById("tag-1")

        // Then
        assertNotNull(retrieved)
        assertEquals("Updated Name", retrieved?.name)
        assertEquals("New Location", retrieved?.location)
    }

    @Test
    fun deleteTag_removesTagFromDatabase() = runTest {
        // Given
        val tag = createTestTag(id = "tag-1", uid = "UID-001", name = "To Delete")
        dao.insertTag(tag)
        assertNotNull(dao.getTagById("tag-1"))

        // When
        dao.deleteTag(tag)
        val retrieved = dao.getTagById("tag-1")

        // Then
        assertNull(retrieved)
    }

    @Test
    fun deleteTagById_removesTagFromDatabase() = runTest {
        // Given
        val tag = createTestTag(id = "tag-1", uid = "UID-001", name = "To Delete")
        dao.insertTag(tag)
        assertNotNull(dao.getTagById("tag-1"))

        // When
        dao.deleteTagById("tag-1")
        val retrieved = dao.getTagById("tag-1")

        // Then
        assertNull(retrieved)
    }

    @Test
    fun updateLastUsed_incrementsUseCountAndUpdatesTimestamp() = runTest {
        // Given
        val tag = createTestTag(
            id = "tag-1",
            uid = "UID-001",
            name = "Test Tag",
            useCount = 5,
            lastUsedAt = 1000L
        )
        dao.insertTag(tag)

        // When
        val newTimestamp = System.currentTimeMillis()
        dao.updateLastUsed("UID-001", newTimestamp)

        // Then
        val retrieved = dao.getTagByUid("UID-001")
        assertNotNull(retrieved)
        assertEquals(6, retrieved?.useCount)
        assertEquals(newTimestamp, retrieved?.lastUsedAt)
    }

    @Test
    fun updateLastUsed_multipleUpdates_incrementsCorrectly() = runTest {
        // Given
        val tag = createTestTag(id = "tag-1", uid = "UID-001", name = "Test Tag", useCount = 0)
        dao.insertTag(tag)

        // When
        dao.updateLastUsed("UID-001", System.currentTimeMillis())
        dao.updateLastUsed("UID-001", System.currentTimeMillis())
        dao.updateLastUsed("UID-001", System.currentTimeMillis())

        // Then
        val retrieved = dao.getTagByUid("UID-001")
        assertEquals(3, retrieved?.useCount)
    }

    @Test
    fun getTagCount_returnsCorrectCount() = runTest {
        // Given
        dao.insertTag(createTestTag(id = "1", uid = "UID-001", name = "Tag 1"))
        dao.insertTag(createTestTag(id = "2", uid = "UID-002", name = "Tag 2"))
        dao.insertTag(createTestTag(id = "3", uid = "UID-003", name = "Tag 3"))

        // When
        val count = dao.getTagCount()

        // Then
        assertEquals(3, count)
    }

    @Test
    fun getTagCount_emptyDatabase_returnsZero() = runTest {
        // When
        val count = dao.getTagCount()

        // Then
        assertEquals(0, count)
    }

    @Test
    fun getTagCountForProfile_returnsCorrectCount() = runTest {
        // Given
        dao.insertTag(createTestTag(id = "1", uid = "UID-001", profileId = "profile-1"))
        dao.insertTag(createTestTag(id = "2", uid = "UID-002", profileId = "profile-1"))
        dao.insertTag(createTestTag(id = "3", uid = "UID-003", profileId = "profile-2"))

        // When
        val countProfile1 = dao.getTagCountForProfile("profile-1")
        val countProfile2 = dao.getTagCountForProfile("profile-2")

        // Then
        assertEquals(2, countProfile1)
        assertEquals(1, countProfile2)
    }

    @Test
    fun insertTag_withDuplicateUid_replacesExisting() = runTest {
        // Given - UID has unique index
        val tag1 = createTestTag(id = "tag-1", uid = "UID-001", name = "Original")
        dao.insertTag(tag1)

        // When - Insert tag with same UID but different ID (REPLACE strategy)
        val tag2 = createTestTag(id = "tag-2", uid = "UID-001", name = "Replacement")
        dao.insertTag(tag2)

        // Then
        val allTags = dao.getAllTags().first()
        assertEquals(1, allTags.size) // Should only have 1 tag due to unique UID constraint
        assertEquals("Replacement", allTags[0].name)
    }

    @Test
    fun insertTag_withNullProfileId_insertsSuccessfully() = runTest {
        // Given
        val tag = createTestTag(id = "tag-1", uid = "UID-001", name = "Unassigned Tag", profileId = null)

        // When
        dao.insertTag(tag)
        val retrieved = dao.getTagById("tag-1")

        // Then
        assertNotNull(retrieved)
        assertNull(retrieved?.profileId)
    }

    @Test
    fun observeAllTags_emitsUpdates() = runTest {
        // Given - Initial state
        val tag1 = createTestTag(id = "1", uid = "UID-001", name = "Tag 1")
        dao.insertTag(tag1)

        // When - First observation
        val initial = dao.getAllTags().first()
        assertEquals(1, initial.size)

        // When - Add another tag
        val tag2 = createTestTag(id = "2", uid = "UID-002", name = "Tag 2")
        dao.insertTag(tag2)

        // Then - Should emit updated list
        val updated = dao.getAllTags().first()
        assertEquals(2, updated.size)
    }

    @Test
    fun observeTagsForProfile_emitsUpdates() = runTest {
        // Given - Initial state
        val tag1 = createTestTag(id = "1", uid = "UID-001", name = "Tag 1", profileId = "profile-1")
        dao.insertTag(tag1)

        // When - First observation
        val initial = dao.getTagsForProfile("profile-1").first()
        assertEquals(1, initial.size)

        // When - Add another tag for same profile
        val tag2 = createTestTag(id = "2", uid = "UID-002", name = "Tag 2", profileId = "profile-1")
        dao.insertTag(tag2)

        // Then - Should emit updated list
        val updated = dao.getTagsForProfile("profile-1").first()
        assertEquals(2, updated.size)
    }

    // Helper function to create test tags
    private fun createTestTag(
        id: String,
        uid: String,
        name: String = "Test Tag",
        location: String? = null,
        profileId: String? = null,
        createdAt: Long = System.currentTimeMillis(),
        lastUsedAt: Long? = null,
        useCount: Int = 0
    ): NfcTagEntity {
        return NfcTagEntity(
            id = id,
            uid = uid,
            name = name,
            location = location,
            profileId = profileId,
            createdAt = createdAt,
            lastUsedAt = lastUsedAt,
            useCount = useCount
        )
    }
}
