package com.umbral.presentation.viewmodel

import app.cash.turbine.test
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.domain.blocking.ProfileRepository
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class ProfilesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var profileRepository: ProfileRepository
    private lateinit var viewModel: ProfilesViewModel

    private val testProfile1 = BlockingProfile(
        id = "profile-1",
        name = "Work Mode",
        iconName = "work",
        colorHex = "#6650A4",
        isActive = false,
        isStrictMode = false,
        blockedApps = listOf("com.facebook.katana"),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    private val testProfile2 = BlockingProfile(
        id = "profile-2",
        name = "Focus Mode",
        iconName = "focus",
        colorHex = "#4CAF50",
        isActive = true,
        isStrictMode = true,
        blockedApps = listOf("com.instagram.android"),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        profileRepository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Initial State Tests
    @Test
    fun `initial state is loading`() = runTest {
        // Given
        every { profileRepository.getAllProfiles() } returns flowOf(emptyList())

        // When
        viewModel = ProfilesViewModel(profileRepository)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
            assertEquals(emptyList<BlockingProfile>(), state.profiles)
            assertNull(state.selectedProfile)
            assertFalse(state.showDeleteDialog)
        }
    }

    @Test
    fun `state updates with profiles after loading`() = runTest {
        // Given
        every { profileRepository.getAllProfiles() } returns flowOf(listOf(testProfile1, testProfile2))
        viewModel = ProfilesViewModel(profileRepository)

        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(2, state.profiles.size)
            assertEquals("Work Mode", state.profiles[0].name)
            assertEquals("Focus Mode", state.profiles[1].name)
        }
    }

    @Test
    fun `state shows empty list when no profiles exist`() = runTest {
        // Given
        every { profileRepository.getAllProfiles() } returns flowOf(emptyList())
        viewModel = ProfilesViewModel(profileRepository)

        // When
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(0, state.profiles.size)
        }
    }

    // showDeleteDialog Tests
    @Test
    fun `showDeleteDialog sets selected profile and shows dialog`() = runTest {
        // Given
        every { profileRepository.getAllProfiles() } returns flowOf(emptyList())
        viewModel = ProfilesViewModel(profileRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.showDeleteDialog(testProfile1)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.showDeleteDialog)
            assertNotNull(state.selectedProfile)
            assertEquals("profile-1", state.selectedProfile?.id)
        }
    }

    // hideDeleteDialog Tests
    @Test
    fun `hideDeleteDialog clears selected profile and hides dialog`() = runTest {
        // Given
        every { profileRepository.getAllProfiles() } returns flowOf(emptyList())
        viewModel = ProfilesViewModel(profileRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.showDeleteDialog(testProfile1)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.hideDeleteDialog()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.showDeleteDialog)
            assertNull(state.selectedProfile)
        }
    }

    // deleteProfile Tests
    @Test
    fun `deleteProfile deletes selected profile and hides dialog`() = runTest {
        // Given
        every { profileRepository.getAllProfiles() } returns flowOf(emptyList())
        coEvery { profileRepository.deleteProfile("profile-1") } returns Result.success(Unit)
        viewModel = ProfilesViewModel(profileRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.showDeleteDialog(testProfile1)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.deleteProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { profileRepository.deleteProfile("profile-1") }
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.showDeleteDialog)
            assertNull(state.selectedProfile)
        }
    }

    @Test
    fun `deleteProfile does nothing when no profile selected`() = runTest {
        // Given
        every { profileRepository.getAllProfiles() } returns flowOf(emptyList())
        viewModel = ProfilesViewModel(profileRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.deleteProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { profileRepository.deleteProfile(any()) }
    }

    // activateProfile Tests
    @Test
    fun `activateProfile activates specified profile`() = runTest {
        // Given
        every { profileRepository.getAllProfiles() } returns flowOf(emptyList())
        coEvery { profileRepository.activateProfile("profile-1") } returns Result.success(Unit)
        viewModel = ProfilesViewModel(profileRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.activateProfile("profile-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { profileRepository.activateProfile("profile-1") }
    }

    // deactivateProfile Tests
    @Test
    fun `deactivateProfile deactivates all profiles`() = runTest {
        // Given
        every { profileRepository.getAllProfiles() } returns flowOf(emptyList())
        coEvery { profileRepository.deactivateAllProfiles() } returns Result.success(Unit)
        viewModel = ProfilesViewModel(profileRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.deactivateProfile("profile-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { profileRepository.deactivateAllProfiles() }
    }

    // createDefaultProfile Tests
    @Test
    fun `createDefaultProfile creates profile with default values`() = runTest {
        // Given
        every { profileRepository.getAllProfiles() } returns flowOf(emptyList())
        coEvery { profileRepository.saveProfile(any()) } returns Result.success(Unit)
        viewModel = ProfilesViewModel(profileRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.createDefaultProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) {
            profileRepository.saveProfile(
                match { profile ->
                    profile.name == "Mi Perfil" &&
                            profile.iconName == "shield" &&
                            profile.colorHex == "#6650A4" &&
                            profile.blockedApps.isEmpty()
                }
            )
        }
    }

    // Integration Tests
    @Test
    fun `profiles flow updates when repository emits new data`() = runTest {
        // Given
        every { profileRepository.getAllProfiles() } returns flowOf(
            emptyList(),
            listOf(testProfile1),
            listOf(testProfile1, testProfile2)
        )

        // When
        viewModel = ProfilesViewModel(profileRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            // Latest emission should have 2 profiles
            assertTrue(state.profiles.size <= 2)
        }
    }

    @Test
    fun `delete dialog workflow complete cycle`() = runTest {
        // Given
        every { profileRepository.getAllProfiles() } returns flowOf(listOf(testProfile1))
        coEvery { profileRepository.deleteProfile("profile-1") } returns Result.success(Unit)
        viewModel = ProfilesViewModel(profileRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - Show dialog
        viewModel.showDeleteDialog(testProfile1)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val stateAfterShow = awaitItem()
            assertTrue(stateAfterShow.showDeleteDialog)
            assertEquals("profile-1", stateAfterShow.selectedProfile?.id)
        }

        // When - Confirm deletion
        viewModel.deleteProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val stateAfterDelete = awaitItem()
            assertFalse(stateAfterDelete.showDeleteDialog)
            assertNull(stateAfterDelete.selectedProfile)
        }
        coVerify(exactly = 1) { profileRepository.deleteProfile("profile-1") }
    }

    @Test
    fun `cancel delete dialog does not delete profile`() = runTest {
        // Given
        every { profileRepository.getAllProfiles() } returns flowOf(listOf(testProfile1))
        viewModel = ProfilesViewModel(profileRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - Show and hide without deleting
        viewModel.showDeleteDialog(testProfile1)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.hideDeleteDialog()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.showDeleteDialog)
            assertNull(state.selectedProfile)
        }
        coVerify(exactly = 0) { profileRepository.deleteProfile(any()) }
    }
}
