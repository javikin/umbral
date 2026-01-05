package com.umbral.data.stats

import app.cash.turbine.test
import com.umbral.data.local.dao.AppBlockCount
import com.umbral.data.local.dao.StatsDao
import com.umbral.data.local.entity.BlockedAttemptEntity
import com.umbral.data.local.entity.BlockingSessionEntity
import com.umbral.domain.stats.BlockedAttempt
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

@OptIn(ExperimentalCoroutinesApi::class)
class StatsRepositoryTest {

    private lateinit var statsDao: StatsDao
    private lateinit var repository: StatsRepositoryImpl

    private val testTime = LocalDateTime.of(2024, 1, 15, 12, 0)

    private val testAttemptEntity = BlockedAttemptEntity(
        id = 1L,
        packageName = "com.example.app",
        appName = "Test App",
        profileId = "profile-1",
        timestamp = testTime,
        wasUnlocked = false,
        unlockMethod = null
    )

    private val testAttempt = BlockedAttempt(
        id = 1L,
        packageName = "com.example.app",
        appName = "Test App",
        profileId = "profile-1",
        timestamp = testTime,
        wasUnlocked = false,
        unlockMethod = null
    )

    private val testSessionEntity = BlockingSessionEntity(
        id = 1L,
        profileId = "profile-1",
        startedAt = testTime,
        endedAt = null,
        blockedAttempts = 0,
        unlockMethod = null
    )

    @Before
    fun setup() {
        statsDao = mockk(relaxed = true)
        repository = StatsRepositoryImpl(statsDao)
    }

    // recordBlockedAttempt Tests
    @Test
    fun `recordBlockedAttempt successfully saves attempt`() = runTest {
        // Given
        coEvery { statsDao.insertBlockedAttempt(any()) } just Runs

        // When
        val result = repository.recordBlockedAttempt(testAttempt)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { statsDao.insertBlockedAttempt(any()) }
    }

    @Test
    fun `recordBlockedAttempt returns failure when dao throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { statsDao.insertBlockedAttempt(any()) } throws exception

        // When
        val result = repository.recordBlockedAttempt(testAttempt)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // getRecentAttempts Tests
    @Test
    fun `getRecentAttempts returns empty list when no attempts exist`() = runTest {
        // Given
        every { statsDao.getRecentAttempts(50) } returns flowOf(emptyList())

        // When
        repository.getRecentAttempts(50).test {
            // Then
            val attempts = awaitItem()
            assertEquals(0, attempts.size)
            awaitComplete()
        }
    }

    @Test
    fun `getRecentAttempts returns list of attempts`() = runTest {
        // Given
        val entities = listOf(
            testAttemptEntity,
            testAttemptEntity.copy(id = 2L, packageName = "com.example.app2")
        )
        every { statsDao.getRecentAttempts(50) } returns flowOf(entities)

        // When
        repository.getRecentAttempts(50).test {
            // Then
            val attempts = awaitItem()
            assertEquals(2, attempts.size)
            assertEquals("com.example.app", attempts[0].packageName)
            assertEquals("com.example.app2", attempts[1].packageName)
            awaitComplete()
        }
    }

    @Test
    fun `getRecentAttempts respects limit parameter`() = runTest {
        // Given
        val entities = List(100) { testAttemptEntity.copy(id = it.toLong()) }
        every { statsDao.getRecentAttempts(10) } returns flowOf(entities.take(10))

        // When
        repository.getRecentAttempts(10).test {
            // Then
            val attempts = awaitItem()
            assertEquals(10, attempts.size)
            awaitComplete()
        }
    }

    // getAttemptCountSince Tests
    @Test
    fun `getAttemptCountSince returns count from dao`() = runTest {
        // Given
        val since = testTime.minusDays(7)
        val timestamp = since.toEpochSecond(ZoneOffset.UTC)
        coEvery { statsDao.getAttemptCountSince(timestamp) } returns 42

        // When
        val count = repository.getAttemptCountSince(since)

        // Then
        assertEquals(42, count)
        coVerify(exactly = 1) { statsDao.getAttemptCountSince(timestamp) }
    }

    @Test
    fun `getAttemptCountSince returns 0 when dao throws exception`() = runTest {
        // Given
        val since = testTime.minusDays(7)
        coEvery { statsDao.getAttemptCountSince(any()) } throws RuntimeException()

        // When
        val count = repository.getAttemptCountSince(since)

        // Then
        assertEquals(0, count)
    }

    // getTopBlockedApps Tests
    @Test
    fun `getTopBlockedApps returns map of apps and counts`() = runTest {
        // Given
        val since = testTime.minusDays(7)
        val timestamp = since.toEpochSecond(ZoneOffset.UTC)
        val results = listOf(
            AppBlockCount("com.example.app1", 10),
            AppBlockCount("com.example.app2", 5),
            AppBlockCount("com.example.app3", 3)
        )
        coEvery { statsDao.getTopBlockedApps(timestamp, 5) } returns results

        // When
        val topApps = repository.getTopBlockedApps(since, 5)

        // Then
        assertEquals(3, topApps.size)
        assertEquals(10, topApps["com.example.app1"])
        assertEquals(5, topApps["com.example.app2"])
        assertEquals(3, topApps["com.example.app3"])
    }

    @Test
    fun `getTopBlockedApps returns empty map when dao throws exception`() = runTest {
        // Given
        val since = testTime.minusDays(7)
        coEvery { statsDao.getTopBlockedApps(any(), any()) } throws RuntimeException()

        // When
        val topApps = repository.getTopBlockedApps(since, 5)

        // Then
        assertTrue(topApps.isEmpty())
    }

    // startSession Tests
    @Test
    fun `startSession successfully creates session and returns ID`() = runTest {
        // Given
        val sessionId = 123L
        coEvery { statsDao.insertSession(any()) } returns sessionId

        // When
        val result = repository.startSession("profile-1")

        // Then
        assertTrue(result.isSuccess)
        assertEquals(sessionId, result.getOrNull())
        coVerify(exactly = 1) { statsDao.insertSession(any()) }
    }

    @Test
    fun `startSession returns failure when dao throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { statsDao.insertSession(any()) } throws exception

        // When
        val result = repository.startSession("profile-1")

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // endSession Tests
    @Test
    fun `endSession successfully updates session`() = runTest {
        // Given
        val sessionId = 123L
        val endTime = testTime.plusHours(2)
        val attempts = 5
        coEvery { statsDao.endSession(any(), any(), any(), any()) } just Runs

        // When
        val result = repository.endSession(sessionId, endTime, attempts, "nfc")

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            statsDao.endSession(
                sessionId,
                endTime.toEpochSecond(ZoneOffset.UTC),
                attempts,
                "nfc"
            )
        }
    }

    @Test
    fun `endSession returns failure when dao throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { statsDao.endSession(any(), any(), any(), any()) } throws exception

        // When
        val result = repository.endSession(1L, testTime, 0, null)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // getActiveSession Tests
    @Test
    fun `getActiveSession returns null when no active session`() = runTest {
        // Given
        coEvery { statsDao.getActiveSession() } returns null

        // When
        val session = repository.getActiveSession()

        // Then
        assertNull(session)
    }

    @Test
    fun `getActiveSession returns active session`() = runTest {
        // Given
        coEvery { statsDao.getActiveSession() } returns testSessionEntity

        // When
        val session = repository.getActiveSession()

        // Then
        assertNotNull(session)
        assertEquals("profile-1", session?.profileId)
        assertTrue(session?.isActive == true)
        assertNull(session?.endedAt)
    }

    @Test
    fun `getActiveSession returns null when dao throws exception`() = runTest {
        // Given
        coEvery { statsDao.getActiveSession() } throws RuntimeException()

        // When
        val session = repository.getActiveSession()

        // Then
        assertNull(session)
    }

    // getTotalBlockedSince Tests
    @Test
    fun `getTotalBlockedSince returns total from dao`() = runTest {
        // Given
        val since = testTime.minusDays(30)
        val timestamp = since.toEpochSecond(ZoneOffset.UTC)
        coEvery { statsDao.getTotalBlockedSince(timestamp) } returns 100

        // When
        val total = repository.getTotalBlockedSince(since)

        // Then
        assertEquals(100, total)
    }

    @Test
    fun `getTotalBlockedSince returns 0 when dao returns null`() = runTest {
        // Given
        val since = testTime.minusDays(30)
        coEvery { statsDao.getTotalBlockedSince(any()) } returns null

        // When
        val total = repository.getTotalBlockedSince(since)

        // Then
        assertEquals(0, total)
    }

    @Test
    fun `getTotalBlockedSince returns 0 when dao throws exception`() = runTest {
        // Given
        val since = testTime.minusDays(30)
        coEvery { statsDao.getTotalBlockedSince(any()) } throws RuntimeException()

        // When
        val total = repository.getTotalBlockedSince(since)

        // Then
        assertEquals(0, total)
    }

    // deleteOldAttempts Tests
    @Test
    fun `deleteOldAttempts successfully deletes old records`() = runTest {
        // Given
        val before = testTime.minusDays(90)
        coEvery { statsDao.deleteOldAttempts(any()) } just Runs

        // When
        val result = repository.deleteOldAttempts(before)

        // Then
        assertTrue(result.isSuccess)
        coVerify(exactly = 1) {
            statsDao.deleteOldAttempts(before.toEpochSecond(ZoneOffset.UTC))
        }
    }

    @Test
    fun `deleteOldAttempts returns failure when dao throws exception`() = runTest {
        // Given
        val exception = RuntimeException("Database error")
        coEvery { statsDao.deleteOldAttempts(any()) } throws exception

        // When
        val result = repository.deleteOldAttempts(testTime)

        // Then
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // Domain Model Tests
    @Test
    fun `BlockingSession calculates duration correctly`() = runTest {
        // Given
        coEvery { statsDao.getActiveSession() } returns testSessionEntity.copy(
            endedAt = testTime.plusHours(2)
        )

        // When
        val session = repository.getActiveSession()

        // Then
        assertNotNull(session)
        assertEquals(120L, session?.durationMinutes)
        assertFalse(session?.isActive == true)
    }

    @Test
    fun `BlockingSession identifies active session correctly`() = runTest {
        // Given
        coEvery { statsDao.getActiveSession() } returns testSessionEntity

        // When
        val session = repository.getActiveSession()

        // Then
        assertNotNull(session)
        assertTrue(session?.isActive == true)
        assertNull(session?.durationMinutes)
    }

    // Entity to Domain Mapping Tests
    @Test
    fun `entity to domain mapping preserves all attempt fields`() = runTest {
        // Given
        every { statsDao.getRecentAttempts(1) } returns flowOf(listOf(testAttemptEntity))

        // When
        repository.getRecentAttempts(1).test {
            val attempts = awaitItem()
            val attempt = attempts.first()

            // Then
            assertEquals(testAttemptEntity.id, attempt.id)
            assertEquals(testAttemptEntity.packageName, attempt.packageName)
            assertEquals(testAttemptEntity.appName, attempt.appName)
            assertEquals(testAttemptEntity.profileId, attempt.profileId)
            assertEquals(testAttemptEntity.timestamp, attempt.timestamp)
            assertEquals(testAttemptEntity.wasUnlocked, attempt.wasUnlocked)
            assertEquals(testAttemptEntity.unlockMethod, attempt.unlockMethod)
            awaitComplete()
        }
    }

    @Test
    fun `entity to domain mapping preserves all session fields`() = runTest {
        // Given
        coEvery { statsDao.getActiveSession() } returns testSessionEntity

        // When
        val session = repository.getActiveSession()

        // Then
        assertNotNull(session)
        assertEquals(testSessionEntity.id, session?.id)
        assertEquals(testSessionEntity.profileId, session?.profileId)
        assertEquals(testSessionEntity.startedAt, session?.startedAt)
        assertEquals(testSessionEntity.endedAt, session?.endedAt)
        assertEquals(testSessionEntity.blockedAttempts, session?.blockedAttempts)
        assertEquals(testSessionEntity.unlockMethod, session?.unlockMethod)
    }
}
