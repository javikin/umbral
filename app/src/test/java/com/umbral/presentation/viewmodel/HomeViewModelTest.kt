package com.umbral.presentation.viewmodel

import app.cash.turbine.test
import com.umbral.data.local.dao.BlockingProfileDao
import com.umbral.data.local.entity.BlockingProfileEntity
import com.umbral.data.local.preferences.UmbralPreferences
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var preferences: UmbralPreferences
    private lateinit var profileDao: BlockingProfileDao
    private lateinit var viewModel: HomeViewModel

    private val testProfile = BlockingProfileEntity(
        id = "test-id",
        name = "Test Profile",
        iconName = "shield",
        colorHex = "#6650A4",
        isActive = true,
        isStrictMode = false,
        blockedApps = emptyList(),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        preferences = mockk(relaxed = true)
        profileDao = mockk(relaxed = true)

        every { preferences.blockingEnabled } returns flowOf(false)
        every { preferences.currentStreak } returns flowOf(5)
        every { preferences.onboardingCompleted } returns flowOf(true)
        every { profileDao.getActiveProfile() } returns flowOf(testProfile)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        viewModel = HomeViewModel(preferences, profileDao)

        viewModel.uiState.test {
            val initialState = awaitItem()
            assertEquals(true, initialState.isLoading)
        }
    }

    @Test
    fun `state updates after loading`() = runTest {
        viewModel = HomeViewModel(preferences, profileDao)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.isBlockingEnabled)
            assertEquals(5, state.currentStreak)
            assertEquals(testProfile, state.activeProfile)
        }
    }

    @Test
    fun `toggleBlocking updates preferences`() = runTest {
        coEvery { preferences.setBlockingEnabled(true) } returns Unit
        viewModel = HomeViewModel(preferences, profileDao)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleBlocking()
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { preferences.setBlockingEnabled(true) }
    }

    @Test
    fun `selectProfile activates profile and updates preferences`() = runTest {
        coEvery { profileDao.deactivateAllProfiles() } returns Unit
        coEvery { profileDao.activateProfile("new-id") } returns Unit
        coEvery { preferences.setActiveProfileId("new-id") } returns Unit

        viewModel = HomeViewModel(preferences, profileDao)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.selectProfile("new-id")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { profileDao.deactivateAllProfiles() }
        coVerify { profileDao.activateProfile("new-id") }
        coVerify { preferences.setActiveProfileId("new-id") }
    }
}
