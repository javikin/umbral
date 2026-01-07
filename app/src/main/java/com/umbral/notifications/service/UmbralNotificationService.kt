package com.umbral.notifications.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.umbral.notifications.domain.NotificationWhitelistChecker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * NotificationListenerService that intercepts all system notifications.
 *
 * This service allows Umbral to:
 * - Detect when blocked apps show notifications
 * - Optionally suppress notifications from blocked apps
 * - Track notification patterns for statistics
 *
 * Requires BIND_NOTIFICATION_LISTENER_SERVICE permission which must be
 * granted by user through Settings > Notification Access.
 */
@AndroidEntryPoint
class UmbralNotificationService : NotificationListenerService() {

    @Inject
    lateinit var whitelistChecker: NotificationWhitelistChecker

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {
        private const val TAG = "UmbralNotificationService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "UmbralNotificationService created")
    }

    /**
     * Called when a new notification is posted.
     *
     * @param sbn The StatusBarNotification containing notification details
     */
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        val packageName = sbn.packageName
        val notification = sbn.notification

        // Extract notification content
        val title = notification.extras.getString(android.app.Notification.EXTRA_TITLE)
        val text = notification.extras.getString(android.app.Notification.EXTRA_TEXT)
        val id = sbn.id
        val postTime = sbn.postTime

        // Check whitelist in a coroutine
        serviceScope.launch {
            val isWhitelisted = whitelistChecker.shouldAllowNotification(sbn)

            if (isWhitelisted) {
                val reason = whitelistChecker.getAllowReason(sbn)
                Log.d(TAG, """
                    ✅ Notification ALLOWED (Whitelisted):
                    - Package: $packageName
                    - ID: $id
                    - Title: $title
                    - Reason: $reason
                """.trimIndent())
            } else {
                Log.d(TAG, """
                    ⚠️ Notification SUBJECT TO BLOCKING:
                    - Package: $packageName
                    - ID: $id
                    - Title: $title
                    - Text: $text
                    - PostTime: $postTime
                """.trimIndent())
            }

            // TODO (Issue #65): Store notification event in database
            // TODO (Issue #66): Optionally cancel notification if app is blocked
        }
    }

    /**
     * Called when a notification is removed (dismissed or timed out).
     *
     * @param sbn The StatusBarNotification that was removed
     */
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn ?: return

        val packageName = sbn.packageName
        val id = sbn.id

        Log.d(TAG, "Notification Removed - Package: $packageName, ID: $id")

        // TODO (Issue #65): Update notification status in database
    }

    /**
     * Called when notification listener is connected to the system.
     * This indicates the service has proper permissions.
     */
    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i(TAG, "NotificationListener connected - Service is active")
    }

    /**
     * Called when notification listener is disconnected.
     * This can happen if user revokes permission or service crashes.
     */
    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.w(TAG, "NotificationListener disconnected - Permission may be revoked")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(TAG, "UmbralNotificationService destroyed")
    }
}
