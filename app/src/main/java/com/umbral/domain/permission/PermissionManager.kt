package com.umbral.domain.permission

import kotlinx.coroutines.flow.Flow

/**
 * Manages special permissions required by Umbral.
 */
interface PermissionManager {

    /**
     * Check if usage stats permission is granted.
     * Required for detecting which app is currently in foreground.
     */
    fun hasUsageStatsPermission(): Boolean

    /**
     * Check if overlay permission is granted.
     * Required for showing blocking overlay on top of other apps.
     */
    fun hasOverlayPermission(): Boolean

    /**
     * Check if notification permission is granted.
     * Required for showing persistent notification (Android 13+).
     */
    fun hasNotificationPermission(): Boolean

    /**
     * Check if camera permission is granted.
     * Required for QR code scanning.
     */
    fun hasCameraPermission(): Boolean

    /**
     * Check if all required permissions are granted.
     */
    fun hasAllRequiredPermissions(): Boolean

    /**
     * Flow of permission states for reactive UI updates.
     */
    val permissionState: Flow<PermissionState>

    /**
     * Request usage stats permission by opening system settings.
     */
    fun requestUsageStatsPermission()

    /**
     * Request overlay permission by opening system settings.
     */
    fun requestOverlayPermission()

    /**
     * Refresh permission states (call after returning from settings).
     */
    suspend fun refreshPermissions()
}

data class PermissionState(
    val usageStats: Boolean = false,
    val overlay: Boolean = false,
    val notification: Boolean = false,
    val camera: Boolean = false
) {
    val allRequired: Boolean
        get() = usageStats && overlay

    val allGranted: Boolean
        get() = usageStats && overlay && notification && camera
}
