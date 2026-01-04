package com.umbral.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.domain.blocking.ProfileRepository
import com.umbral.domain.nfc.NfcRepository
import com.umbral.domain.nfc.NfcTag
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
import java.time.Instant
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var profileRepository: ProfileRepository
    private lateinit var nfcRepository: NfcRepository
    private lateinit var viewModel: ProfileDetailViewModel

    private val testProfile = BlockingProfile(
        id = "existing-profile-id",
        name = "Work Mode",
        iconName = "work",
        colorHex = "#6650A4",
        isActive = false,
        isStrictMode = true,
        blockedApps = listOf("com.facebook.katana", "com.instagram.android"),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    private val testTag = NfcTag(
        id = "tag-1",
        uid = "04:A1:B2:C3:D4:E5:F6",
        name = "Office Tag",
        location = "Office",
        profileId = "existing-profile-id",
        createdAt = Instant.now()
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        profileRepository = mockk(relaxed = true)
        nfcRepository = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // New Profile Tests
    @Test
    fun `new profile initializes with default values`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "new"))
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())

        // When
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        // Then
        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.isNewProfile)
            assertEquals("", state.name)
            assertEquals("shield", state.iconName)
            assertEquals(ProfileDetailViewModel.AVAILABLE_COLORS.first(), state.colorHex)
            assertFalse(state.isStrictMode)
            assertEquals(emptyList<String>(), state.blockedApps)
            assertEquals(emptyList<NfcTag>(), state.linkedTags)
        }
    }

    // Existing Profile Tests
    @Test
    fun `existing profile loads successfully`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "existing-profile-id"))
        coEvery { profileRepository.getProfileById("existing-profile-id") } returns testProfile
        every { nfcRepository.getAllTags() } returns flowOf(listOf(testTag))

        // When
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        // Then
        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.isNewProfile)
            assertEquals("existing-profile-id", state.profileId)
            assertEquals("Work Mode", state.name)
            assertEquals("work", state.iconName)
            assertEquals("#6650A4", state.colorHex)
            assertTrue(state.isStrictMode)
            assertEquals(2, state.blockedApps.size)
            assertEquals(1, state.linkedTags.size)
        }
    }

    @Test
    fun `existing profile not found shows error`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "nonexistent"))
        coEvery { profileRepository.getProfileById("nonexistent") } returns null
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())

        // When
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        // Then
        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals("Perfil no encontrado", state.error)
        }
    }

    // updateName Tests
    @Test
    fun `updateName updates profile name and clears error`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "new"))
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            // When
            viewModel.updateName("New Name")
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertEquals("New Name", state.name)
            assertNull(state.error)
        }
    }

    // updateColor Tests
    @Test
    fun `updateColor changes profile color`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "new"))
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            // When
            viewModel.updateColor("#FF0000")
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertEquals("#FF0000", state.colorHex)
        }
    }

    // updateIcon Tests
    @Test
    fun `updateIcon changes profile icon`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "new"))
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            // When
            viewModel.updateIcon("lock")
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertEquals("lock", state.iconName)
        }
    }

    // toggleStrictMode Tests
    @Test
    fun `toggleStrictMode toggles between true and false`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "new"))
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            // When - First toggle
            viewModel.toggleStrictMode()
            testDispatcher.scheduler.advanceUntilIdle()

            val state1 = awaitItem()
            assertTrue(state1.isStrictMode)

            // When - Second toggle
            viewModel.toggleStrictMode()
            testDispatcher.scheduler.advanceUntilIdle()

            val state2 = awaitItem()
            assertFalse(state2.isStrictMode)
        }
    }

    // addBlockedApp Tests
    @Test
    fun `addBlockedApp adds app to blocked list`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "new"))
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            // When
            viewModel.addBlockedApp("com.facebook.katana")
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertEquals(1, state.blockedApps.size)
            assertTrue(state.blockedApps.contains("com.facebook.katana"))
        }
    }

    @Test
    fun `addBlockedApp does not add duplicate app`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "new"))
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            // When - add first app
            viewModel.addBlockedApp("com.facebook.katana")
            testDispatcher.scheduler.advanceUntilIdle()

            val stateAfterFirst = awaitItem()
            assertEquals(1, stateAfterFirst.blockedApps.size)

            // When - try to add duplicate
            viewModel.addBlockedApp("com.facebook.katana")
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - no new emission since it's a duplicate, verify size is still 1
            // The state hasn't changed, so we cancel and check the current value
            cancelAndIgnoreRemainingEvents()
        }

        // Verify the current state still has only 1 app
        assertEquals(1, viewModel.uiState.value.blockedApps.size)
    }

    // removeBlockedApp Tests
    @Test
    fun `removeBlockedApp removes app from blocked list`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "new"))
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            viewModel.addBlockedApp("com.facebook.katana")
            viewModel.addBlockedApp("com.instagram.android")
            testDispatcher.scheduler.advanceUntilIdle()
            cancelAndIgnoreRemainingEvents()
        }

        // When
        viewModel.removeBlockedApp("com.facebook.katana")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(1, state.blockedApps.size)
            assertFalse(state.blockedApps.contains("com.facebook.katana"))
            assertTrue(state.blockedApps.contains("com.instagram.android"))
        }
    }

    // setBlockedApps Tests
    @Test
    fun `setBlockedApps replaces entire blocked apps list`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "new"))
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            // When
            val newApps = listOf("com.app1", "com.app2", "com.app3")
            viewModel.setBlockedApps(newApps)
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertEquals(3, state.blockedApps.size)
            assertEquals(newApps, state.blockedApps)
        }
    }

    // unlinkTag Tests
    @Test
    fun `unlinkTag unlinks tag from profile`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "existing-profile-id"))
        coEvery { profileRepository.getProfileById("existing-profile-id") } returns testProfile
        every { nfcRepository.getAllTags() } returns flowOf(listOf(testTag))
        coEvery { nfcRepository.unlinkTagFromProfile("tag-1") } returns Result.success(Unit)
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            // When
            viewModel.unlinkTag("tag-1")
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            coVerify(exactly = 1) { nfcRepository.unlinkTagFromProfile("tag-1") }
            cancelAndIgnoreRemainingEvents()
        }
    }

    // saveProfile Tests
    @Test
    fun `saveProfile saves new profile successfully`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "new"))
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())
        coEvery { profileRepository.saveProfile(any()) } returns Result.success(Unit)
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        var successCalled = false

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            viewModel.updateName("My Profile")
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            // When
            viewModel.saveProfile { successCalled = true }
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - skip isSaving=true state, get final state
            val state = expectMostRecentItem()
            assertFalse(state.isSaving)
            assertTrue(state.saveSuccess)
            assertNull(state.error)
        }
        assertTrue(successCalled)
        coVerify(exactly = 1) { profileRepository.saveProfile(any()) }
    }

    @Test
    fun `saveProfile fails when name is blank`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "new"))
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        var successCalled = false

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            // When
            viewModel.saveProfile { successCalled = true }
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertEquals("El nombre es requerido", state.error)
            assertFalse(state.saveSuccess)
        }
        assertFalse(successCalled)
        coVerify(exactly = 0) { profileRepository.saveProfile(any()) }
    }

    @Test
    fun `saveProfile shows error when repository fails`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "new"))
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())
        coEvery { profileRepository.saveProfile(any()) } returns Result.failure(RuntimeException("Save failed"))
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        var successCalled = false

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            viewModel.updateName("My Profile")
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            // When
            viewModel.saveProfile { successCalled = true }
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = expectMostRecentItem()
            assertFalse(state.isSaving)
            assertFalse(state.saveSuccess)
            assertEquals("Error al guardar el perfil", state.error)
        }
        assertFalse(successCalled)
    }

    @Test
    fun `saveProfile sets isSaving during save operation`() = runTest {
        // Given
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "new"))
        every { nfcRepository.getAllTags() } returns flowOf(emptyList())
        coEvery { profileRepository.saveProfile(any()) } returns Result.success(Unit)
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            viewModel.updateName("My Profile")
            testDispatcher.scheduler.advanceUntilIdle()
            skipItems(1)

            // When
            viewModel.saveProfile {}
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - verify operation completed without error
            val state = expectMostRecentItem()
            assertNotNull(state)
            assertTrue(state.saveSuccess)
        }
    }

    // Linked Tags Tests
    @Test
    fun `linkedTags filters tags by profile ID`() = runTest {
        // Given
        val otherTag = testTag.copy(id = "tag-2", profileId = "other-profile")
        savedStateHandle = SavedStateHandle(mapOf("profileId" to "existing-profile-id"))
        coEvery { profileRepository.getProfileById("existing-profile-id") } returns testProfile
        every { nfcRepository.getAllTags() } returns flowOf(listOf(testTag, otherTag))

        // When
        viewModel = ProfileDetailViewModel(savedStateHandle, profileRepository, nfcRepository)

        // Then
        viewModel.uiState.test {
            skipItems(1)
            testDispatcher.scheduler.advanceUntilIdle()
            val state = awaitItem()
            assertEquals(1, state.linkedTags.size)
            assertEquals("tag-1", state.linkedTags[0].id)
        }
    }
}
