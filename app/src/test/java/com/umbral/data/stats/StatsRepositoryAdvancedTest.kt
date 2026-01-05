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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Advanced test scenarios for StatsRepository.
 * Covers time-based queries, aggregations, and session lifecycle management.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class StatsRepositoryAdvancedTest {

    private lateinit var statsDao: StatsDao
    private lateinit var repository: StatsRepositoryImpl

    private val testTime = LocalDateTime.of(2024, 1, 15, 12, 0)

    @Before
    fun setup() {
        statsDao = mockk(relaxed = true)
        repository = StatsRepositoryImpl(statsDao)
    }

    // Blocked Attempt Edge Cases
    @Test
    fun `recordBlockedAttempt handles attempt with very long package name`() = runTest {
        // Given
        val longPackageName = "com." + "a".repeat(500) + ".app"
        val attempt = BlockedAttempt(
            packageName = longPackageName,
            appName = "Test App",
            profileId = "profile-1",
            timestamp = testTime
        )
        coEvery { statsDao.insertBlockedAttempt(any()) } just Runs

        // When
        val result = repository.recordBlockedAttempt(attempt)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            statsDao.insertBlockedAttempt(
                withArg {
                    assertTrue(it.packageName.length > 500)
                }
            )
        }
    }

    @Test
    fun `recordBlockedAttempt handles attempt with special characters in app name`() = runTest {
        // Given
        val specialNames = listOf(
            "App with emoji 📱",
            "Aplicación con ñ",
            "アプリ (Japanese)",
            "App\nwith\nnewlines",
            "App's \"special\" chars"
        )

        coEvery { statsDao.insertBlockedAttempt(any()) } just Runs

        // When/Then
        specialNames.forEach { appName ->
            val attempt = BlockedAttempt(
                packageName = "com.test.app",
                appName = appName,
                profileId = "profile-1",
                timestamp = testTime
            )
            val result = repository.recordBlockedAttempt(attempt)
            assertTrue("Failed for app name: $appName", result.isSuccess)
        }
    }

    @Test
    fun `recordBlockedAttempt with unlocked attempt tracks unlock method`() = runTest {
        // Given
        val unlockedAttempt = BlockedAttempt(
            packageName = "com.facebook.katana",
            appName = "Facebook",
            profileId = "profile-1",
            timestamp = testTime,
            wasUnlocked = true,
            unlockMethod = "nfc"
        )
        coEvery { statsDao.insertBlockedAttempt(any()) } just Runs

        // When
        val result = repository.recordBlockedAttempt(unlockedAttempt)

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            statsDao.insertBlockedAttempt(
                withArg {
                    assertTrue(it.wasUnlocked)
                    assertEquals("nfc", it.unlockMethod)
                }
            )
        }
    }

    // Time-based Query Tests
    @Test
    fun `getAttemptCountSince handles various time ranges`() = runTest {
        // Given - Various time ranges
        val now = testTime
        val oneHourAgo = now.minusHours(1)
        val oneDayAgo = now.minusDays(1)
        val oneWeekAgo = now.minusWeeks(1)
        val oneMonthAgo = now.minusMonths(1)

        coEvery { statsDao.getAttemptCountSince(oneHourAgo.toEpochSecond(ZoneOffset.UTC)) } returns 5
        coEvery { statsDao.getAttemptCountSince(oneDayAgo.toEpochSecond(ZoneOffset.UTC)) } returns 25
        coEvery { statsDao.getAttemptCountSince(oneWeekAgo.toEpochSecond(ZoneOffset.UTC)) } returns 100
        coEvery { statsDao.getAttemptCountSince(oneMonthAgo.toEpochSecond(ZoneOffset.UTC)) } returns 500

        // When/Then
        assertEquals(5, repository.getAttemptCountSince(oneHourAgo))
        assertEquals(25, repository.getAttemptCountSince(oneDayAgo))
        assertEquals(100, repository.getAttemptCountSince(oneWeekAgo))
        assertEquals(500, repository.getAttemptCountSince(oneMonthAgo))
    }

    @Test
    fun `getAttemptCountSince handles future timestamps gracefully`() = runTest {
        // Given - Future timestamp (shouldn't happen but let's test)
        val futureTime = testTime.plusYears(1)
        coEvery { statsDao.getAttemptCountSince(any()) } returns 0

        // When
        val count = repository.getAttemptCountSince(futureTime)

        // Then
        assertEquals(0, count)
    }

    @Test
    fun `getAttemptCountSince handles very old timestamps`() = runTest {
        // Given - Very old timestamp (beginning of time)
        val ancientTime = LocalDateTime.of(1970, 1, 1, 0, 0)
        coEvery { statsDao.getAttemptCountSince(any()) } returns 10000

        // When
        val count = repository.getAttemptCountSince(ancientTime)

        // Then
        assertEquals(10000, count)
    }

    // Top Blocked Apps Tests
    @Test
    fun `getTopBlockedApps returns correct ranking`() = runTest {
        // Given - Apps ranked by frequency
        val results = listOf(
            AppBlockCount("com.instagram.android", 100),
            AppBlockCount("com.facebook.katana", 75),
            AppBlockCount("com.twitter.android", 50),
            AppBlockCount("com.tiktok.android", 25),
            AppBlockCount("com.reddit.android", 10)
        )
        coEvery { statsDao.getTopBlockedApps(any(), 5) } returns results

        // When
        val topApps = repository.getTopBlockedApps(testTime.minusDays(7), 5)

        // Then
        assertEquals(5, topApps.size)
        assertEquals(100, topApps["com.instagram.android"])
        assertEquals(75, topApps["com.facebook.katana"])
        assertEquals(10, topApps["com.reddit.android"])
    }

    @Test
    fun `getTopBlockedApps handles different limit values`() = runTest {
        // Given
        val allApps = List(20) { AppBlockCount("com.app$it", 100 - it) }

        coEvery { statsDao.getTopBlockedApps(any(), 5) } returns allApps.take(5)
        coEvery { statsDao.getTopBlockedApps(any(), 10) } returns allApps.take(10)
        coEvery { statsDao.getTopBlockedApps(any(), 1) } returns allApps.take(1)

        // When/Then
        assertEquals(5, repository.getTopBlockedApps(testTime, 5).size)
        assertEquals(10, repository.getTopBlockedApps(testTime, 10).size)
        assertEquals(1, repository.getTopBlockedApps(testTime, 1).size)
    }

    @Test
    fun `getTopBlockedApps handles ties in count`() = runTest {
        // Given - Multiple apps with same count
        val results = listOf(
            AppBlockCount("com.app1", 50),
            AppBlockCount("com.app2", 50),
            AppBlockCount("com.app3", 50)
        )
        coEvery { statsDao.getTopBlockedApps(any(), 5) } returns results

        // When
        val topApps = repository.getTopBlockedApps(testTime, 5)

        // Then - All should be included
        assertEquals(3, topApps.size)
        assertEquals(50, topApps["com.app1"])
        assertEquals(50, topApps["com.app2"])
        assertEquals(50, topApps["com.app3"])
    }

    @Test
    fun `getTopBlockedApps returns empty map when no data`() = runTest {
        // Given
        coEvery { statsDao.getTopBlockedApps(any(), any()) } returns emptyList()

        // When
        val topApps = repository.getTopBlockedApps(testTime, 5)

        // Then
        assertTrue(topApps.isEmpty())
    }

    // Session Lifecycle Tests
    @Test
    fun `session lifecycle - start, record attempts, end`() = runTest {
        // Given
        val sessionId = 123L
        coEvery { statsDao.insertSession(any()) } returns sessionId
        coEvery { statsDao.endSession(any(), any(), any(), any()) } just Runs

        // When - Complete session lifecycle
        val startResult = repository.startSession("profile-1")
        val endResult = repository.endSession(
            sessionId,
            testTime.plusHours(2),
            attempts = 15,
            unlockMethod = "nfc"
        )

        // Then
        assertTrue(startResult.isSuccess)
        assertEquals(sessionId, startResult.getOrNull())
        assertTrue(endResult.isSuccess)

        coVerify(exactly = 1) { statsDao.insertSession(any()) }
        coVerify(exactly = 1) {
            statsDao.endSession(
                sessionId,
                testTime.plusHours(2).toEpochSecond(ZoneOffset.UTC),
                15,
                "nfc"
            )
        }
    }

    @Test
    fun `startSession handles multiple concurrent profiles`() = runTest {
        // Given
        coEvery { statsDao.insertSession(any()) } returnsMany listOf(1L, 2L, 3L)

        // When - Start sessions for different profiles
        val session1 = repository.startSession("profile-1")
        val session2 = repository.startSession("profile-2")
        val session3 = repository.startSession("profile-3")

        // Then
        assertEquals(1L, session1.getOrNull())
        assertEquals(2L, session2.getOrNull())
        assertEquals(3L, session3.getOrNull())
    }

    @Test
    fun `endSession with zero attempts`() = runTest {
        // Given
        coEvery { statsDao.endSession(any(), any(), any(), any()) } just Runs

        // When - Session ended with no blocked attempts
        val result = repository.endSession(
            sessionId = 1L,
            endTime = testTime.plusHours(1),
            attempts = 0,
            unlockMethod = null
        )

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            statsDao.endSession(1L, any(), 0, null)
        }
    }

    @Test
    fun `endSession with very high attempt count`() = runTest {
        // Given
        val highAttemptCount = 9999
        coEvery { statsDao.endSession(any(), any(), any(), any()) } just Runs

        // When
        val result = repository.endSession(
            sessionId = 1L,
            endTime = testTime.plusHours(8),
            attempts = highAttemptCount,
            unlockMethod = "timer"
        )

        // Then
        assertTrue(result.isSuccess)
        coVerify {
            statsDao.endSession(1L, any(), highAttemptCount, "timer")
        }
    }

    @Test
    fun `endSession with various unlock methods`() = runTest {
        // Given
        val unlockMethods = listOf("nfc", "qr", "timer", "manual", null)
        coEvery { statsDao.endSession(any(), any(), any(), any()) } just Runs

        // When/Then
        unlockMethods.forEachIndexed { index, method ->
            val result = repository.endSession(
                sessionId = index.toLong(),
                endTime = testTime,
                attempts = 5,
                unlockMethod = method
            )
            assertTrue("Failed for method: $method", result.isSuccess)
        }
    }

    // Recent Attempts Flow Tests
    @Test
    fun `getRecentAttempts emits updates when new attempts arrive`() = runTest {
        // Given
        val initialAttempts = listOf(
            createTestAttemptEntity(1L, "com.app1")
        )
        val updatedAttempts = listOf(
            createTestAttemptEntity(1L, "com.app1"),
            createTestAttemptEntity(2L, "com.app2")
        )

        every { statsDao.getRecentAttempts(50) } returns flowOf(initialAttempts, updatedAttempts)

        // When/Then
        repository.getRecentAttempts(50).test {
            assertEquals(1, awaitItem().size)
            assertEquals(2, awaitItem().size)
            awaitComplete()
        }
    }

    @Test
    fun `getRecentAttempts handles rapid updates`() = runTest {
        // Given - Rapid fire updates
        val updates = (1..5).map { count ->
            List(count) { createTestAttemptEntity(it.toLong(), "com.app$it") }
        }

        every { statsDao.getRecentAttempts(50) } returns flowOf(*updates.toTypedArray())

        // When/Then
        repository.getRecentAttempts(50).test {
            assertEquals(1, awaitItem().size)
            assertEquals(2, awaitItem().size)
            assertEquals(3, awaitItem().size)
            assertEquals(4, awaitItem().size)
            assertEquals(5, awaitItem().size)
            awaitComplete()
        }
    }

    // Cleanup Tests
    @Test
    fun `deleteOldAttempts handles various time ranges`() = runTest {
        // Given
        val timeRanges = listOf(
            testTime.minusDays(30),
            testTime.minusDays(90),
            testTime.minusYears(1)
        )
        coEvery { statsDao.deleteOldAttempts(any()) } just Runs

        // When/Then
        timeRanges.forEach { before ->
            val result = repository.deleteOldAttempts(before)
            assertTrue(result.isSuccess)
        }

        coVerify(exactly = 3) { statsDao.deleteOldAttempts(any()) }
    }

    @Test
    fun `deleteOldAttempts with future timestamp`() = runTest {
        // Given - Future timestamp (would delete everything)
        val future = testTime.plusYears(10)
        coEvery { statsDao.deleteOldAttempts(any()) } just Runs

        // When
        val result = repository.deleteOldAttempts(future)

        // Then - Should succeed (DAO handles the logic)
        assertTrue(result.isSuccess)
        coVerify {
            statsDao.deleteOldAttempts(future.toEpochSecond(ZoneOffset.UTC))
        }
    }

    // getTotalBlockedSince Tests
    @Test
    fun `getTotalBlockedSince handles various time periods`() = runTest {
        // Given
        coEvery { statsDao.getTotalBlockedSince(any()) } returnsMany listOf(10, 50, 200, 1000)

        // When
        val lastHour = repository.getTotalBlockedSince(testTime.minusHours(1))
        val lastDay = repository.getTotalBlockedSince(testTime.minusDays(1))
        val lastWeek = repository.getTotalBlockedSince(testTime.minusWeeks(1))
        val lastMonth = repository.getTotalBlockedSince(testTime.minusMonths(1))

        // Then - Expect increasing counts for longer periods
        assertEquals(10, lastHour)
        assertEquals(50, lastDay)
        assertEquals(200, lastWeek)
        assertEquals(1000, lastMonth)
    }

    @Test
    fun `getTotalBlockedSince returns 0 for null DAO result`() = runTest {
        // Given
        coEvery { statsDao.getTotalBlockedSince(any()) } returns null

        // When
        val total = repository.getTotalBlockedSince(testTime)

        // Then
        assertEquals(0, total)
    }

    // Active Session Tests
    @Test
    fun `getActiveSession returns null when no session active`() = runTest {
        // Given
        coEvery { statsDao.getActiveSession() } returns null

        // When
        val session = repository.getActiveSession()

        // Then
        assertNull(session)
    }

    @Test
    fun `getActiveSession returns session with correct properties`() = runTest {
        // Given
        val sessionEntity = BlockingSessionEntity(
            id = 100L,
            profileId = "profile-awesome",
            startedAt = testTime,
            endedAt = null,
            blockedAttempts = 42,
            unlockMethod = null
        )
        coEvery { statsDao.getActiveSession() } returns sessionEntity

        // When
        val session = repository.getActiveSession()

        // Then
        assertNotNull(session)
        assertEquals(100L, session?.id)
        assertEquals("profile-awesome", session?.profileId)
        assertEquals(testTime, session?.startedAt)
        assertNull(session?.endedAt)
        assertEquals(42, session?.blockedAttempts)
        assertTrue(session?.isActive == true)
    }

    // Error Recovery Tests
    @Test
    fun `repository recovers from transient DAO errors`() = runTest {
        // Given - First call fails, second succeeds
        coEvery { statsDao.getAttemptCountSince(any()) } throws RuntimeException("Temporary error") andThen 42

        // When
        val firstAttempt = repository.getAttemptCountSince(testTime)
        val secondAttempt = repository.getAttemptCountSince(testTime)

        // Then - First returns 0 (error fallback), second returns actual value
        assertEquals(0, firstAttempt)
        assertEquals(42, secondAttempt)
    }

    // Helper Functions
    private fun createTestAttemptEntity(
        id: Long,
        packageName: String,
        wasUnlocked: Boolean = false
    ): BlockedAttemptEntity {
        return BlockedAttemptEntity(
            id = id,
            packageName = packageName,
            appName = "App $id",
            profileId = "profile-1",
            timestamp = testTime,
            wasUnlocked = wasUnlocked,
            unlockMethod = if (wasUnlocked) "nfc" else null
        )
    }
}
