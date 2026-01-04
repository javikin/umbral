package com.umbral.presentation.viewmodel

import app.cash.turbine.test
import com.umbral.domain.permission.PermissionManager
import com.umbral.domain.permission.PermissionState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var permissionManager: PermissionManager
    private lateinit var viewModel: SettingsViewModel

    private val permissionStateFlow = MutableStateFlow(
        PermissionState(
            usageStats = false,
            overlay = false,
            notification = false,
            camera = false
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        permissionManager = mockk(relaxed = true)

        every { permissionManager.permissionState } returns permissionStateFlow
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Initial State Tests
    @Test
    fun `initial state is loading`() = runTest {
        // When
        viewModel = SettingsViewModel(permissionManager)

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.isLoading)
            assertEquals("1.0.0", state.appVersion)
        }
    }

    @Test
    fun `state updates after permissions load`() = runTest {
        // Given
        permissionStateFlow.value = PermissionState(
            usageStats = true,
            overlay = true,
            notification = false,
            camera = false
        )

        // When
        viewModel = SettingsViewModel(permissionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.permissionState.usageStats)
            assertTrue(state.permissionState.overlay)
            assertFalse(state.permissionState.notification)
            assertFalse(state.permissionState.camera)
        }
    }

    // Permission State Updates Tests
    @Test
    fun `permission state flow updates reflect in UI state`() = runTest {
        // Given
        viewModel = SettingsViewModel(permissionManager)

        // When
        permissionStateFlow.value = PermissionState(
            usageStats = true,
            overlay = true,
            notification = true,
            camera = true
        )
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.permissionState.usageStats)
            assertTrue(state.permissionState.overlay)
            assertTrue(state.permissionState.notification)
            assertTrue(state.permissionState.camera)
            assertTrue(state.permissionState.allGranted)
        }
    }

    @Test
    fun `allRequired is true when usage stats and overlay granted`() = runTest {
        // Given
        permissionStateFlow.value = PermissionState(
            usageStats = true,
            overlay = true,
            notification = false,
            camera = false
        )

        // When
        viewModel = SettingsViewModel(permissionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.permissionState.allRequired)
        }
    }

    @Test
    fun `allRequired is false when usage stats missing`() = runTest {
        // Given
        permissionStateFlow.value = PermissionState(
            usageStats = false,
            overlay = true,
            notification = true,
            camera = true
        )

        // When
        viewModel = SettingsViewModel(permissionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.permissionState.allRequired)
        }
    }

    @Test
    fun `allRequired is false when overlay missing`() = runTest {
        // Given
        permissionStateFlow.value = PermissionState(
            usageStats = true,
            overlay = false,
            notification = true,
            camera = true
        )

        // When
        viewModel = SettingsViewModel(permissionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.permissionState.allRequired)
        }
    }

    @Test
    fun `allGranted is true only when all permissions granted`() = runTest {
        // Given
        permissionStateFlow.value = PermissionState(
            usageStats = true,
            overlay = true,
            notification = true,
            camera = true
        )

        // When
        viewModel = SettingsViewModel(permissionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.permissionState.allGranted)
        }
    }

    @Test
    fun `allGranted is false when any permission missing`() = runTest {
        // Given
        permissionStateFlow.value = PermissionState(
            usageStats = true,
            overlay = true,
            notification = true,
            camera = false
        )

        // When
        viewModel = SettingsViewModel(permissionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.permissionState.allGranted)
        }
    }

    // requestUsageStatsPermission Tests
    @Test
    fun `requestUsageStatsPermission calls permission manager`() = runTest {
        // Given
        viewModel = SettingsViewModel(permissionManager)

        // When
        viewModel.requestUsageStatsPermission()

        // Then
        verify(exactly = 1) { permissionManager.requestUsageStatsPermission() }
    }

    // requestOverlayPermission Tests
    @Test
    fun `requestOverlayPermission calls permission manager`() = runTest {
        // Given
        viewModel = SettingsViewModel(permissionManager)

        // When
        viewModel.requestOverlayPermission()

        // Then
        verify(exactly = 1) { permissionManager.requestOverlayPermission() }
    }

    // refreshPermissions Tests
    @Test
    fun `refreshPermissions calls permission manager`() = runTest {
        // Given
        coEvery { permissionManager.refreshPermissions() } returns Unit
        viewModel = SettingsViewModel(permissionManager)

        // When
        viewModel.refreshPermissions()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 1) { permissionManager.refreshPermissions() }
    }

    @Test
    fun `refreshPermissions updates state with new permissions`() = runTest {
        // Given
        viewModel = SettingsViewModel(permissionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        coEvery { permissionManager.refreshPermissions() } answers {
            permissionStateFlow.value = PermissionState(
                usageStats = true,
                overlay = true,
                notification = true,
                camera = true
            )
        }

        // When
        viewModel.refreshPermissions()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state.permissionState.usageStats)
            assertTrue(state.permissionState.overlay)
            assertTrue(state.permissionState.notification)
            assertTrue(state.permissionState.camera)
        }
    }

    // App Version Tests
    @Test
    fun `app version is always set to 1_0_0`() = runTest {
        // Given
        viewModel = SettingsViewModel(permissionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("1.0.0", state.appVersion)
        }
    }

    // Loading State Tests
    @Test
    fun `isLoading becomes false after permissions are loaded`() = runTest {
        // Given
        permissionStateFlow.value = PermissionState(
            usageStats = false,
            overlay = false,
            notification = false,
            camera = false
        )

        // When
        viewModel = SettingsViewModel(permissionManager)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.uiState.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
        }
    }

    // Integration Tests
    @Test
    fun `permission state changes are reflected immediately`() = runTest {
        // Given
        viewModel = SettingsViewModel(permissionManager)

        // When - Update permissions multiple times
        permissionStateFlow.value = PermissionState(
            usageStats = true,
            overlay = false,
            notification = false,
            camera = false
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state1 = awaitItem()
            assertTrue(state1.permissionState.usageStats)
            assertFalse(state1.permissionState.overlay)
        }

        permissionStateFlow.value = PermissionState(
            usageStats = true,
            overlay = true,
            notification = false,
            camera = false
        )
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state2 = awaitItem()
            assertTrue(state2.permissionState.usageStats)
            assertTrue(state2.permissionState.overlay)
        }
    }

    @Test
    fun `multiple permission requests can be made`() = runTest {
        // Given
        viewModel = SettingsViewModel(permissionManager)

        // When
        viewModel.requestUsageStatsPermission()
        viewModel.requestOverlayPermission()
        viewModel.requestUsageStatsPermission()

        // Then
        verify(exactly = 2) { permissionManager.requestUsageStatsPermission() }
        verify(exactly = 1) { permissionManager.requestOverlayPermission() }
    }

    @Test
    fun `refresh can be called multiple times`() = runTest {
        // Given
        coEvery { permissionManager.refreshPermissions() } returns Unit
        viewModel = SettingsViewModel(permissionManager)

        // When
        viewModel.refreshPermissions()
        viewModel.refreshPermissions()
        viewModel.refreshPermissions()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        coVerify(exactly = 3) { permissionManager.refreshPermissions() }
    }
}
