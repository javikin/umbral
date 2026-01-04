package com.umbral.presentation.viewmodel

import android.content.Context
import android.nfc.Tag
import app.cash.turbine.test
import com.umbral.data.local.preferences.UmbralPreferences
import com.umbral.domain.nfc.NfcError
import com.umbral.domain.nfc.NfcManager
import com.umbral.domain.nfc.NfcRepository
import com.umbral.domain.nfc.NfcResult
import com.umbral.domain.nfc.NfcState
import com.umbral.domain.nfc.NfcTag
import com.umbral.domain.nfc.TagEvent
import com.umbral.domain.nfc.TagType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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

@OptIn(ExperimentalCoroutinesApi::class)
class NfcScanViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var nfcManager: NfcManager
    private lateinit var nfcRepository: NfcRepository
    private lateinit var preferences: UmbralPreferences
    private lateinit var viewModel: NfcScanViewModel

    private val nfcStateFlow = MutableStateFlow<NfcState>(NfcState.Enabled)
    private val tagEventsFlow = MutableSharedFlow<TagEvent>()

    private val testTag = NfcTag(
        id = "tag-id",
        uid = "04:A1:B2:C3:D4:E5:F6",
        name = "Home Tag",
        location = "Front Door",
        profileId = "profile-id",
        createdAt = Instant.now()
    )

    private val androidTag: Tag = mockk(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        nfcManager = mockk(relaxed = true)
        nfcRepository = mockk(relaxed = true)
        preferences = mockk(relaxed = true)

        every { nfcManager.nfcState } returns nfcStateFlow
        every { nfcManager.tagEvents } returns tagEventsFlow
        every { preferences.blockingEnabled } returns flowOf(false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Initial State Tests
    @Test
    fun `initial state has correct default values`() = runTest {
        // When
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(NfcState.Enabled, state.nfcState)
            assertTrue(state.scanState is ScanState.Idle)
            assertNull(state.lastEvent)
            assertEquals("", state.tagName)
            assertEquals("", state.tagLocation)
            assertFalse(state.showRegisterDialog)
            assertFalse(state.isWriting)
            assertNull(state.lastScannedTag)
        }
    }

    @Test
    fun `nfc state updates from manager`() = runTest {
        // Given
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            // Skip initial state
            awaitItem()

            // When
            nfcStateFlow.value = NfcState.Disabled
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertEquals(NfcState.Disabled, state.nfcState)
        }
    }

    // Tag Event Handling Tests
    @Test
    fun `known tag event updates state and toggles blocking`() = runTest {
        // Given
        coEvery { nfcRepository.updateLastUsed(testTag.uid) } returns Unit
        coEvery { preferences.setBlockingEnabled(any()) } returns Unit
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            // Skip initial state
            awaitItem()

            // When
            tagEventsFlow.emit(TagEvent.KnownTag(testTag))
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertTrue(state.scanState is ScanState.Success)
            assertEquals(testTag, state.lastScannedTag)
            assertFalse(state.showRegisterDialog)
        }
        coVerify { nfcRepository.updateLastUsed(testTag.uid) }
        coVerify { preferences.setBlockingEnabled(true) }
    }

    @Test
    fun `unknown tag event shows register dialog`() = runTest {
        // Given
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            // Skip initial state
            awaitItem()

            // When
            tagEventsFlow.emit(TagEvent.UnknownTag("04:A1:B2:C3", TagType.NTAG213, androidTag))
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertTrue(state.scanState is ScanState.Idle)
            assertTrue(state.showRegisterDialog)
            assertEquals("", state.tagName)
            assertEquals("", state.tagLocation)
        }
    }

    @Test
    fun `invalid tag event shows error state`() = runTest {
        // Given
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            // Skip initial state
            awaitItem()

            // When
            tagEventsFlow.emit(TagEvent.InvalidTag(NfcError.TAG_NOT_SUPPORTED))
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertTrue(state.scanState is ScanState.Error)
            val errorState = state.scanState as ScanState.Error
            assertEquals(NfcError.TAG_NOT_SUPPORTED, errorState.error)
        }
    }

    // Start/Stop Scanning Tests
    @Test
    fun `startScanning sets scanning state`() = runTest {
        // Given
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            // Skip initial state
            awaitItem()

            // When
            viewModel.startScanning()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertTrue(state.scanState is ScanState.Scanning)
        }
    }

    @Test
    fun `stopScanning sets idle state`() = runTest {
        // Given
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            // Skip initial state
            awaitItem()

            viewModel.startScanning()
            testDispatcher.scheduler.advanceUntilIdle()
            // Skip scanning state
            awaitItem()

            // When
            viewModel.stopScanning()
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertTrue(state.scanState is ScanState.Idle)
        }
    }

    // Tag Registration Tests
    @Test
    fun `updateTagName updates tag name in state`() = runTest {
        // Given
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            // Skip initial state
            awaitItem()

            // When
            viewModel.updateTagName("Office Tag")
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertEquals("Office Tag", state.tagName)
        }
    }

    @Test
    fun `updateTagLocation updates tag location in state`() = runTest {
        // Given
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            // Skip initial state
            awaitItem()

            // When
            viewModel.updateTagLocation("Meeting Room")
            testDispatcher.scheduler.advanceUntilIdle()

            // Then
            val state = awaitItem()
            assertEquals("Meeting Room", state.tagLocation)
        }
    }

    @Test
    fun `registerTag successfully registers new tag`() = runTest {
        // Given
        coEvery { nfcRepository.insertTag(any()) } returns Result.success(Unit)
        coEvery { nfcManager.writeTag(androidTag, any()) } returns NfcResult.Success(Unit)
        coEvery { preferences.setBlockingEnabled(any()) } returns Unit
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        // Setup: emit unknown tag and set name/location
        tagEventsFlow.emit(TagEvent.UnknownTag("04:A1:B2:C3", TagType.NTAG213, androidTag))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateTagName("New Tag")
        viewModel.updateTagLocation("Home")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.registerTag()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.scanState is ScanState.TagRegistered)
            assertFalse(state.isWriting)
            assertFalse(state.showRegisterDialog)
            assertEquals("", state.tagName)
            assertEquals("", state.tagLocation)
        }
        coVerify { nfcRepository.insertTag(any()) }
        coVerify { nfcManager.writeTag(androidTag, any()) }
    }

    @Test
    fun `registerTag uses default name when blank`() = runTest {
        // Given
        coEvery { nfcRepository.insertTag(any()) } returns Result.success(Unit)
        coEvery { nfcManager.writeTag(androidTag, any()) } returns NfcResult.Success(Unit)
        coEvery { preferences.setBlockingEnabled(any()) } returns Unit
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        // Setup: emit unknown tag
        tagEventsFlow.emit(TagEvent.UnknownTag("04:A1:B2:C3", TagType.NTAG213, androidTag))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.registerTag()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify {
            nfcRepository.insertTag(match { tag ->
                tag.name == "Tag NFC"
            })
        }
    }

    @Test
    fun `registerTag fails when insert fails`() = runTest {
        // Given
        coEvery { nfcRepository.insertTag(any()) } returns Result.failure(RuntimeException("DB error"))
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        // Setup: emit unknown tag
        tagEventsFlow.emit(TagEvent.UnknownTag("04:A1:B2:C3", TagType.NTAG213, androidTag))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.registerTag()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.scanState is ScanState.Error)
            assertFalse(state.isWriting)
        }
        coVerify(exactly = 0) { nfcManager.writeTag(any(), any()) }
    }

    @Test
    fun `registerTag deletes tag when write fails`() = runTest {
        // Given
        coEvery { nfcRepository.insertTag(any()) } returns Result.success(Unit)
        coEvery { nfcManager.writeTag(androidTag, any()) } returns NfcResult.Error(NfcError.WRITE_FAILED)
        coEvery { nfcRepository.deleteTag(any()) } returns Result.success(Unit)
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        // Setup: emit unknown tag
        tagEventsFlow.emit(TagEvent.UnknownTag("04:A1:B2:C3", TagType.NTAG213, androidTag))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.registerTag()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.scanState is ScanState.Error)
        }
        coVerify { nfcRepository.deleteTag(any()) }
    }

    @Test
    fun `registerTag does nothing when no unknown tag event`() = runTest {
        // Given
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        // When - no unknown tag event emitted
        viewModel.registerTag()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 0) { nfcRepository.insertTag(any()) }
        coVerify(exactly = 0) { nfcManager.writeTag(any(), any()) }
    }

    // Dialog Tests
    @Test
    fun `dismissDialog hides dialog and clears name`() = runTest {
        // Given
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        // Setup: emit unknown tag and set name
        tagEventsFlow.emit(TagEvent.UnknownTag("04:A1:B2:C3", TagType.NTAG213, androidTag))
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.updateTagName("Test Tag")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.dismissDialog()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.showRegisterDialog)
            assertEquals("", state.tagName)
        }
    }

    // Reset State Tests
    @Test
    fun `resetState clears scan state and last event`() = runTest {
        // Given
        coEvery { nfcRepository.updateLastUsed(any()) } returns Unit
        coEvery { preferences.setBlockingEnabled(any()) } returns Unit
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        // Setup: emit known tag
        tagEventsFlow.emit(TagEvent.KnownTag(testTag))
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.resetState()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.scanState is ScanState.Idle)
            assertNull(state.lastEvent)
        }
    }

    // NFC Settings Tests
    @Test
    fun `openNfcSettings calls manager`() = runTest {
        // Given
        val context: Context = mockk(relaxed = true)
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)

        // When
        viewModel.openNfcSettings(context)

        // Then
        verify { nfcManager.openNfcSettings(context) }
    }

    // NFC Availability Tests
    @Test
    fun `isNfcAvailable delegates to manager`() = runTest {
        // Given
        every { nfcManager.isNfcAvailable() } returns true
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)

        // When
        val isAvailable = viewModel.isNfcAvailable()

        // Then
        assertTrue(isAvailable)
        verify { nfcManager.isNfcAvailable() }
    }

    @Test
    fun `isNfcEnabled delegates to manager`() = runTest {
        // Given
        every { nfcManager.isNfcEnabled() } returns false
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)

        // When
        val isEnabled = viewModel.isNfcEnabled()

        // Then
        assertFalse(isEnabled)
        verify { nfcManager.isNfcEnabled() }
    }

    // Blocking Toggle Tests
    @Test
    fun `known tag toggles blocking from false to true`() = runTest {
        // Given
        every { preferences.blockingEnabled } returns flowOf(false)
        coEvery { nfcRepository.updateLastUsed(any()) } returns Unit
        coEvery { preferences.setBlockingEnabled(any()) } returns Unit
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        tagEventsFlow.emit(TagEvent.KnownTag(testTag))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { preferences.setBlockingEnabled(true) }
    }

    @Test
    fun `known tag toggles blocking from true to false`() = runTest {
        // Given
        every { preferences.blockingEnabled } returns flowOf(true)
        coEvery { nfcRepository.updateLastUsed(any()) } returns Unit
        coEvery { preferences.setBlockingEnabled(any()) } returns Unit
        viewModel = NfcScanViewModel(nfcManager, nfcRepository, preferences)
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        tagEventsFlow.emit(TagEvent.KnownTag(testTag))
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify { preferences.setBlockingEnabled(false) }
    }
}
