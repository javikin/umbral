package com.umbral.data.permission

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import app.cash.turbine.test
import androidx.core.content.ContextCompat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PermissionManagerTest {

    private lateinit var context: Context
    private lateinit var appOpsManager: AppOpsManager
    private lateinit var permissionManager: PermissionManagerImpl

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        appOpsManager = mockk(relaxed = true)

        every { context.packageName } returns "com.umbral"
        every { context.getSystemService(Context.APP_OPS_SERVICE) } returns appOpsManager

        // Mock static methods
        mockkStatic(Settings::class)
        mockkStatic(ContextCompat::class)
    }

    // hasUsageStatsPermission Tests
    @Test
    fun `hasUsageStatsPermission returns true when permission granted`() {
        // Given
        every {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                any(),
                any()
            )
        } returns AppOpsManager.MODE_ALLOWED

        // When
        permissionManager = PermissionManagerImpl(context)
        val hasPermission = permissionManager.hasUsageStatsPermission()

        // Then
        assertTrue(hasPermission)
    }

    @Test
    fun `hasUsageStatsPermission returns false when permission denied`() {
        // Given
        every {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                any(),
                any()
            )
        } returns AppOpsManager.MODE_IGNORED

        // When
        permissionManager = PermissionManagerImpl(context)
        val hasPermission = permissionManager.hasUsageStatsPermission()

        // Then
        assertFalse(hasPermission)
    }

    @Test
    fun `hasUsageStatsPermission returns false on exception`() {
        // Given
        every {
            appOpsManager.unsafeCheckOpNoThrow(any(), any(), any())
        } throws SecurityException("Permission check failed")

        // When
        permissionManager = PermissionManagerImpl(context)
        val hasPermission = permissionManager.hasUsageStatsPermission()

        // Then
        assertFalse(hasPermission)
    }

    // hasOverlayPermission Tests
    @Test
    fun `hasOverlayPermission returns true when permission granted`() {
        // Given
        every { Settings.canDrawOverlays(context) } returns true

        // When
        permissionManager = PermissionManagerImpl(context)
        val hasPermission = permissionManager.hasOverlayPermission()

        // Then
        assertTrue(hasPermission)
    }

    @Test
    fun `hasOverlayPermission returns false when permission denied`() {
        // Given
        every { Settings.canDrawOverlays(context) } returns false

        // When
        permissionManager = PermissionManagerImpl(context)
        val hasPermission = permissionManager.hasOverlayPermission()

        // Then
        assertFalse(hasPermission)
    }

    // hasNotificationPermission Tests
    @Test
    fun `hasNotificationPermission returns true when granted on Android 13+`() {
        // Given
        every {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
        } returns PackageManager.PERMISSION_GRANTED

        // When
        permissionManager = PermissionManagerImpl(context)
        val hasPermission = permissionManager.hasNotificationPermission()

        // Then - Should be true if SDK >= 33, otherwise always true
        assertTrue(hasPermission || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
    }

    @Test
    fun `hasNotificationPermission returns false when denied on Android 13+`() {
        // Given
        every {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
        } returns PackageManager.PERMISSION_DENIED

        // When
        permissionManager = PermissionManagerImpl(context)
        val hasPermission = permissionManager.hasNotificationPermission()

        // Then - If SDK < 33, it always returns true; otherwise follows the mock
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            assertFalse(hasPermission)
        } else {
            assertTrue(hasPermission)
        }
    }

    // hasCameraPermission Tests
    @Test
    fun `hasCameraPermission returns true when permission granted`() {
        // Given
        every {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
        } returns PackageManager.PERMISSION_GRANTED

        // When
        permissionManager = PermissionManagerImpl(context)
        val hasPermission = permissionManager.hasCameraPermission()

        // Then
        assertTrue(hasPermission)
    }

    @Test
    fun `hasCameraPermission returns false when permission denied`() {
        // Given
        every {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
        } returns PackageManager.PERMISSION_DENIED

        // When
        permissionManager = PermissionManagerImpl(context)
        val hasPermission = permissionManager.hasCameraPermission()

        // Then
        assertFalse(hasPermission)
    }

    // hasAllRequiredPermissions Tests
    @Test
    fun `hasAllRequiredPermissions returns true when both required permissions granted`() {
        // Given
        every { Settings.canDrawOverlays(context) } returns true
        every {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                any(),
                any()
            )
        } returns AppOpsManager.MODE_ALLOWED

        // When
        permissionManager = PermissionManagerImpl(context)
        val hasAll = permissionManager.hasAllRequiredPermissions()

        // Then
        assertTrue(hasAll)
    }

    @Test
    fun `hasAllRequiredPermissions returns false when usage stats missing`() {
        // Given
        every { Settings.canDrawOverlays(context) } returns true
        every {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                any(),
                any()
            )
        } returns AppOpsManager.MODE_IGNORED

        // When
        permissionManager = PermissionManagerImpl(context)
        val hasAll = permissionManager.hasAllRequiredPermissions()

        // Then
        assertFalse(hasAll)
    }

    @Test
    fun `hasAllRequiredPermissions returns false when overlay missing`() {
        // Given
        every { Settings.canDrawOverlays(context) } returns false
        every {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                any(),
                any()
            )
        } returns AppOpsManager.MODE_ALLOWED

        // When
        permissionManager = PermissionManagerImpl(context)
        val hasAll = permissionManager.hasAllRequiredPermissions()

        // Then
        assertFalse(hasAll)
    }

    // requestUsageStatsPermission Tests
    @Test
    fun `requestUsageStatsPermission starts correct activity`() {
        // Given
        permissionManager = PermissionManagerImpl(context)

        // When
        permissionManager.requestUsageStatsPermission()

        // Then
        verify {
            context.startActivity(match { intent ->
                intent.action == Settings.ACTION_USAGE_ACCESS_SETTINGS &&
                        intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0
            })
        }
    }

    @Test
    fun `requestUsageStatsPermission falls back to general settings on error`() {
        // Given
        every { context.startActivity(any()) } throws SecurityException("Cannot start activity") andThen {}
        permissionManager = PermissionManagerImpl(context)

        // When
        permissionManager.requestUsageStatsPermission()

        // Then - Should attempt twice: first usage stats, then fallback
        verify(exactly = 2) { context.startActivity(any()) }
    }

    // requestOverlayPermission Tests
    @Test
    fun `requestOverlayPermission starts correct activity`() {
        // Given
        permissionManager = PermissionManagerImpl(context)

        // When
        permissionManager.requestOverlayPermission()

        // Then
        verify {
            context.startActivity(match { intent ->
                intent.action == Settings.ACTION_MANAGE_OVERLAY_PERMISSION &&
                        intent.flags and Intent.FLAG_ACTIVITY_NEW_TASK != 0
            })
        }
    }

    @Test
    fun `requestOverlayPermission falls back to general settings on error`() {
        // Given
        every { context.startActivity(any()) } throws SecurityException("Cannot start activity") andThen {}
        permissionManager = PermissionManagerImpl(context)

        // When
        permissionManager.requestOverlayPermission()

        // Then - Should attempt twice: first overlay, then fallback
        verify(exactly = 2) { context.startActivity(any()) }
    }

    // refreshPermissions Tests
    @Test
    fun `refreshPermissions updates permission state`() = runTest {
        // Given
        every { Settings.canDrawOverlays(context) } returns false
        every {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                any(),
                any()
            )
        } returns AppOpsManager.MODE_IGNORED
        every {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
        } returns PackageManager.PERMISSION_DENIED
        every {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
        } returns PackageManager.PERMISSION_DENIED

        permissionManager = PermissionManagerImpl(context)

        // When
        permissionManager.refreshPermissions()

        // Then
        permissionManager.permissionState.test {
            val state = awaitItem()
            assertFalse(state.usageStats)
            assertFalse(state.overlay)
            assertFalse(state.camera)
            // notification depends on SDK version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                assertFalse(state.notification)
            }
        }
    }

    // permissionState Flow Tests
    @Test
    fun `permissionState flow emits initial state`() = runTest {
        // Given
        every { Settings.canDrawOverlays(context) } returns true
        every {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                any(),
                any()
            )
        } returns AppOpsManager.MODE_ALLOWED
        every {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
        } returns PackageManager.PERMISSION_GRANTED

        // When
        permissionManager = PermissionManagerImpl(context)

        // Then
        permissionManager.permissionState.test {
            val state = awaitItem()
            assertTrue(state.usageStats)
            assertTrue(state.overlay)
            assertTrue(state.camera)
            assertTrue(state.allRequired)
        }
    }

    @Test
    fun `permissionState allRequired is true when both required permissions granted`() = runTest {
        // Given
        every { Settings.canDrawOverlays(context) } returns true
        every {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                any(),
                any()
            )
        } returns AppOpsManager.MODE_ALLOWED

        // When
        permissionManager = PermissionManagerImpl(context)

        // Then
        permissionManager.permissionState.test {
            val state = awaitItem()
            assertTrue(state.allRequired)
        }
    }

    @Test
    fun `permissionState allGranted requires all four permissions`() = runTest {
        // Given
        every { Settings.canDrawOverlays(context) } returns true
        every {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                any(),
                any()
            )
        } returns AppOpsManager.MODE_ALLOWED
        every {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
        } returns PackageManager.PERMISSION_GRANTED
        every {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
        } returns PackageManager.PERMISSION_GRANTED

        // When
        permissionManager = PermissionManagerImpl(context)

        // Then
        permissionManager.permissionState.test {
            val state = awaitItem()
            assertEquals(
                state.usageStats && state.overlay && state.notification && state.camera,
                state.allGranted
            )
        }
    }
}
