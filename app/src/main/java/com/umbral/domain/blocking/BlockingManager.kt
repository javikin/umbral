package com.umbral.domain.blocking

import com.umbral.expedition.domain.model.SessionReward
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Event emitted when a blocking session starts.
 * Used to trigger the session started confirmation dialog.
 */
data class SessionStartedEvent(
    val sessionId: String,
    val profileId: String,
    val profileName: String,
    val blockedAppsCount: Int,
    val isStrictMode: Boolean,
    val blockNotifications: Boolean
)

/**
 * Event emitted when a blocking session ends.
 * Used to trigger the notification summary dialog.
 */
data class SessionEndedEvent(
    val sessionId: String,
    val profileId: String,
    val durationMinutes: Long
)

/**
 * Represents the current blocking state.
 */
data class BlockingState(
    val isActive: Boolean = false,
    val activeProfileId: String? = null,
    val activeProfileName: String? = null,
    val blockedApps: Set<String> = emptySet(),
    val isStrictMode: Boolean = false,
    val blockNotifications: Boolean = true,
    val sessionStartTime: Long? = null,
    val sessionId: String? = null  // String sessionId for notification tracking
)

/**
 * Manager interface for app blocking functionality.
 */
interface BlockingManager {

    /**
     * Current blocking state as a flow.
     */
    val blockingState: StateFlow<BlockingState>

    /**
     * Whether blocking is currently active.
     */
    val isBlocking: Boolean

    /**
     * One-time event flow for session rewards.
     * Emitted when a blocking session completes successfully.
     */
    val rewardEvent: SharedFlow<SessionReward>

    /**
     * One-time event flow for session starting.
     * Emitted when a blocking session starts, used to trigger session started dialog.
     */
    val sessionStartedEvent: SharedFlow<SessionStartedEvent>

    /**
     * One-time event flow for session ending.
     * Emitted when a blocking session ends, used to trigger notification summary dialog.
     */
    val sessionEndedEvent: SharedFlow<SessionEndedEvent>

    /**
     * Start blocking with the specified profile.
     * @param profileId The profile to activate
     * @return Result indicating success or failure
     */
    suspend fun startBlocking(profileId: String): Result<Unit>

    /**
     * Stop blocking.
     * @param requireNfc If true, requires NFC tag scan to stop (strict mode)
     * @return Result indicating success or failure
     */
    suspend fun stopBlocking(requireNfc: Boolean = false): Result<Unit>

    /**
     * Toggle blocking for a profile.
     * If blocking is active with this profile, stop it.
     * If blocking is inactive or with different profile, start with this profile.
     */
    suspend fun toggleBlocking(profileId: String): Result<Unit>

    /**
     * Check if a specific app is currently blocked.
     */
    fun isAppBlocked(packageName: String): Boolean

    /**
     * Get the current foreground app package name.
     */
    suspend fun getCurrentForegroundApp(): String?
}

/**
 * Monitor for detecting foreground app changes.
 */
interface ForegroundAppMonitor {

    /**
     * Flow of foreground app package names.
     */
    val foregroundApp: Flow<String?>

    /**
     * Get current foreground app.
     */
    suspend fun getCurrentForegroundApp(): String?

    /**
     * Check if usage stats permission is granted.
     */
    fun hasUsageStatsPermission(): Boolean
}
