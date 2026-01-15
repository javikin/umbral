package com.umbral.notifications.service

import android.content.ComponentName
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.umbral.domain.blocking.BlockingManager
import com.umbral.notifications.domain.NotificationWhitelistChecker
import com.umbral.notifications.domain.model.BlockedNotification
import com.umbral.notifications.domain.usecase.SaveBlockedNotificationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject

/**
 * NotificationListenerService that intercepts all system notifications.
 *
 * This service allows Umbral to:
 * - Detect when blocked apps show notifications during active blocking sessions
 * - Suppress notifications from blocked apps
 * - Store blocked notifications for later viewing
 * - Track notification patterns for statistics
 *
 * Requires BIND_NOTIFICATION_LISTENER_SERVICE permission which must be
 * granted by user through Settings > Notification Access.
 */
@AndroidEntryPoint
class UmbralNotificationService : NotificationListenerService() {

    @Inject
    lateinit var whitelistChecker: NotificationWhitelistChecker

    @Inject
    lateinit var blockingManager: BlockingManager

    @Inject
    lateinit var saveNotificationUseCase: SaveBlockedNotificationUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Cached blocking state to avoid repeated queries
    private var currentSessionId: String? = null
    private var blockedApps: Set<String> = emptySet()

    companion object {
        private const val TAG = "UmbralNotificationService"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("UmbralNotificationService created")
        observeBlockingState()
    }

    /**
     * Observe blocking state changes to track active sessions and blocked apps.
     */
    private fun observeBlockingState() {
        serviceScope.launch {
            blockingManager.blockingState.collect { state ->
                if (state.isActive && state.sessionId != null) {
                    currentSessionId = state.sessionId
                    blockedApps = state.blockedApps
                    Timber.d("Blocking active - Session: ${state.sessionId}, Blocked apps: ${blockedApps.size}")
                } else {
                    currentSessionId = null
                    blockedApps = emptySet()
                    Timber.d("Blocking inactive - Notifications will not be intercepted")
                }
            }
        }
    }

    /**
     * Called when a new notification is posted.
     *
     * @param sbn The StatusBarNotification containing notification details
     */
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        // Only process if blocking is active
        val sessionId = currentSessionId
        if (sessionId == null) {
            // Not blocking - allow all notifications
            return
        }

        val packageName = sbn.packageName

        // Check if this app is in the blocked list
        if (packageName !in blockedApps) {
            // App is not blocked - allow notification
            return
        }

        // Check whitelist first (high priority notifications like calls, alarms)
        serviceScope.launch {
            val isWhitelisted = whitelistChecker.shouldAllowNotification(sbn)

            if (isWhitelisted) {
                val reason = whitelistChecker.getAllowReason(sbn)
                Timber.d("""
                    ✅ Notification ALLOWED (Whitelisted):
                    - Package: $packageName
                    - ID: ${sbn.id}
                    - Reason: $reason
                """.trimIndent())
                return@launch
            }

            // Block and store the notification
            Timber.d("""
                🚫 Notification BLOCKED:
                - Package: $packageName
                - ID: ${sbn.id}
                - Session: $sessionId
            """.trimIndent())

            storeAndCancelNotification(sbn, sessionId)
        }
    }

    /**
     * Store a blocked notification and cancel it from the notification shade.
     */
    private suspend fun storeAndCancelNotification(
        sbn: StatusBarNotification,
        sessionId: String
    ) {
        try {
            val notification = sbn.notification
            val packageName = sbn.packageName

            // Get app name
            val appName = try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                packageManager.getApplicationLabel(appInfo).toString()
            } catch (e: PackageManager.NameNotFoundException) {
                packageName
            }

            // Extract notification content
            val title = notification.extras.getString(android.app.Notification.EXTRA_TITLE)
            val text = notification.extras.getString(android.app.Notification.EXTRA_TEXT)

            // Create blocked notification record
            val blockedNotification = BlockedNotification(
                sessionId = sessionId,
                packageName = packageName,
                appName = appName,
                title = title,
                text = text,
                timestamp = Instant.now(),
                iconUri = null // Can extract icon later if needed
            )

            // Save to database
            saveNotificationUseCase(blockedNotification)
            Timber.d("Blocked notification saved: $appName - $title")

            // Cancel the notification to hide it from user
            cancelNotification(sbn.key)
            Timber.d("Notification cancelled: ${sbn.key}")

        } catch (e: Exception) {
            Timber.e(e, "Error storing/cancelling blocked notification")
        }
    }

    /**
     * Called when a notification is removed (dismissed or timed out).
     *
     * @param sbn The StatusBarNotification that was removed
     */
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Currently not needed - notifications are already stored when blocked
    }

    /**
     * Called when notification listener is connected to the system.
     * This indicates the service has proper permissions.
     */
    override fun onListenerConnected() {
        super.onListenerConnected()
        Timber.i("NotificationListener connected - Service is active")
    }

    /**
     * Called when notification listener is disconnected.
     * This can happen if user revokes permission or service crashes.
     *
     * We attempt to rebind the service to maintain functionality.
     */
    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Timber.w("NotificationListener disconnected - Attempting rebind")

        // Request rebind to maintain service connectivity
        try {
            requestRebind(ComponentName(this, UmbralNotificationService::class.java))
        } catch (e: Exception) {
            Timber.e(e, "Failed to request rebind")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Timber.d("UmbralNotificationService destroyed")
    }
}
