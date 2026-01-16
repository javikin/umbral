package com.umbral.notifications.service

import android.app.Notification
import android.content.ComponentName
import android.content.pm.PackageManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.umbral.domain.blocking.BlockingManager
import com.umbral.notifications.data.preferences.NotificationPreferences
import com.umbral.notifications.domain.model.BlockedNotification
import com.umbral.notifications.domain.model.SystemWhitelist
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
 *
 * IMPORTANT: As per AOSP documentation, all callbacks (onNotificationPosted,
 * onListenerConnected, etc.) run on the main thread since Android N.
 * cancelNotification() must be called immediately on the main thread for
 * reliable operation.
 */
@AndroidEntryPoint
class UmbralNotificationService : NotificationListenerService() {

    @Inject
    lateinit var blockingManager: BlockingManager

    @Inject
    lateinit var saveNotificationUseCase: SaveBlockedNotificationUseCase

    @Inject
    lateinit var notificationPreferences: NotificationPreferences

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Connection state - MUST check before calling cancelNotification
    @Volatile
    private var isServiceConnected = false

    // Cached blocking state to avoid repeated queries
    @Volatile
    private var currentSessionId: String? = null
    @Volatile
    private var blockedApps: Set<String> = emptySet()
    @Volatile
    private var blockNotifications: Boolean = true

    // Cached user whitelist for synchronous checks
    @Volatile
    private var userWhitelist: Set<String> = emptySet()

    companion object {
        private const val TAG = "UmbralNotificationService"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("UmbralNotificationService created")
        observeBlockingState()
        observeUserWhitelist()
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
                    blockNotifications = state.blockNotifications
                    Timber.d("Blocking active - Session: ${state.sessionId}, Blocked apps: ${blockedApps.size}, Block notifications: $blockNotifications")
                } else {
                    currentSessionId = null
                    blockedApps = emptySet()
                    blockNotifications = true // Reset to default
                    Timber.d("Blocking inactive - Notifications will not be intercepted")
                }
            }
        }
    }

    /**
     * Observe user whitelist changes to cache for synchronous checks.
     */
    private fun observeUserWhitelist() {
        serviceScope.launch {
            notificationPreferences.userWhitelist.collect { whitelist ->
                userWhitelist = whitelist
                Timber.d("User whitelist updated: ${whitelist.size} apps")
            }
        }
    }

    /**
     * Called when a new notification is posted.
     *
     * CRITICAL: This method runs on the main thread (since Android N).
     * We must cancel notifications IMMEDIATELY and synchronously here,
     * then save to database asynchronously AFTER cancellation.
     *
     * @param sbn The StatusBarNotification containing notification details
     */
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return

        // Guard: Service must be connected
        if (!isServiceConnected) {
            Timber.w("Service not connected, ignoring notification from ${sbn.packageName}")
            return
        }

        // Only process if blocking is active
        val sessionId = currentSessionId
        if (sessionId == null) {
            // Not blocking - allow all notifications
            return
        }

        // Check if notification blocking is enabled for this profile
        if (!blockNotifications) {
            // Profile has notification blocking disabled - allow all notifications
            return
        }

        val packageName = sbn.packageName

        // Check if this app is in the blocked list
        if (packageName !in blockedApps) {
            // App is not blocked - allow notification
            return
        }

        // Check whitelist SYNCHRONOUSLY (no suspend, no coroutine)
        val whitelistResult = shouldAllowNotificationSync(sbn)
        if (whitelistResult.allowed) {
            Timber.d("""
                ✅ Notification ALLOWED (Whitelisted):
                - Package: $packageName
                - ID: ${sbn.id}
                - Reason: ${whitelistResult.reason}
            """.trimIndent())
            return
        }

        // ========================================
        // CRITICAL: Cancel IMMEDIATELY on main thread
        // ========================================
        Timber.d("""
            🚫 Notification BLOCKED:
            - Package: $packageName
            - ID: ${sbn.id}
            - Key: ${sbn.key}
            - Session: $sessionId
        """.trimIndent())

        try {
            cancelNotification(sbn.key)
            Timber.d("✅ Notification cancelled immediately: ${sbn.key}")
        } catch (e: Exception) {
            Timber.e(e, "❌ Failed to cancel notification: ${sbn.key}")
        }

        // ========================================
        // Save to database AFTER cancellation (async)
        // ========================================
        serviceScope.launch {
            saveBlockedNotification(sbn, sessionId)
        }
    }

    /**
     * Synchronous whitelist check - NO suspend, NO coroutines.
     * Uses cached values for fast, synchronous decision making.
     *
     * Priority order:
     * 1. System whitelist (calls, SMS, alarms, system apps)
     * 2. Notification category (CALL, ALARM, MESSAGE)
     * 3. User's custom whitelist (cached)
     * 4. Battery low notifications
     * 5. Critical system alerts
     */
    private fun shouldAllowNotificationSync(sbn: StatusBarNotification): WhitelistResult {
        val packageName = sbn.packageName
        val notification = sbn.notification

        // 1. Check system whitelist (highest priority)
        if (SystemWhitelist.isAlwaysAllowed(packageName)) {
            return WhitelistResult(true, "App del sistema crítica")
        }

        // 2. Check notification category
        val category = notification.category
        if (SystemWhitelist.isCategoryAllowed(category)) {
            return WhitelistResult(true, "Categoría crítica: $category")
        }

        // 3. Check user's custom whitelist (from cache)
        if (packageName in userWhitelist) {
            return WhitelistResult(true, "En lista blanca personal")
        }

        // 4. Check for low battery notification
        if (isBatteryLowNotification(sbn)) {
            return WhitelistResult(true, "Alerta de batería baja")
        }

        // 5. Check for critical system alerts
        if (isCriticalSystemAlert(sbn)) {
            return WhitelistResult(true, "Alerta crítica del sistema")
        }

        return WhitelistResult(false, null)
    }

    /**
     * Result of whitelist check.
     */
    private data class WhitelistResult(
        val allowed: Boolean,
        val reason: String?
    )

    /**
     * Checks if a notification is a low battery warning.
     */
    private fun isBatteryLowNotification(sbn: StatusBarNotification): Boolean {
        val packageName = sbn.packageName
        val notification = sbn.notification

        // Check if it's from the system
        if (packageName != "android" && packageName != "com.android.systemui") {
            return false
        }

        // Check notification extras for battery-related content
        val extras = notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE, "")
        val text = extras.getString(Notification.EXTRA_TEXT, "")

        // Common battery low keywords (multilingual support)
        val batteryKeywords = listOf(
            "battery", "batería", "bateria",
            "power", "energía", "energia",
            "low", "bajo", "baja",
            "charging", "cargando"
        )

        return batteryKeywords.any { keyword ->
            title.contains(keyword, ignoreCase = true) ||
            text.contains(keyword, ignoreCase = true)
        }
    }

    /**
     * Checks if a notification is a critical system alert.
     */
    private fun isCriticalSystemAlert(sbn: StatusBarNotification): Boolean {
        val packageName = sbn.packageName
        val notification = sbn.notification

        // Only system packages can have critical alerts
        if (packageName != "android" && packageName != "com.android.systemui") {
            return false
        }

        // Check for CATEGORY_SYSTEM
        if (notification.category == Notification.CATEGORY_SYSTEM) {
            return true
        }

        // Check for high priority or importance
        @Suppress("DEPRECATION")
        if (notification.priority >= Notification.PRIORITY_HIGH) {
            return true
        }

        // Check for heads-up display (fullScreenIntent indicates critical notification)
        if (notification.fullScreenIntent != null) {
            return true
        }

        return false
    }

    /**
     * Save blocked notification to database.
     * Called asynchronously AFTER the notification has been cancelled.
     */
    private suspend fun saveBlockedNotification(
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
            val title = notification.extras.getString(Notification.EXTRA_TITLE)
            val text = notification.extras.getString(Notification.EXTRA_TEXT)

            // Create blocked notification record
            val blockedNotification = BlockedNotification(
                sessionId = sessionId,
                packageName = packageName,
                appName = appName,
                title = title,
                text = text,
                timestamp = Instant.now(),
                iconUri = null
            )

            // Save to database
            saveNotificationUseCase(blockedNotification)
            Timber.d("📝 Blocked notification saved: $appName - $title")

        } catch (e: Exception) {
            Timber.e(e, "Error saving blocked notification to database")
        }
    }

    /**
     * Called when a notification is removed (dismissed or timed out).
     */
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Currently not needed - notifications are already stored when blocked
    }

    /**
     * Called when notification listener is connected to the system.
     * This indicates the service has proper permissions.
     *
     * CRITICAL: We MUST set isServiceConnected = true here before
     * attempting any cancelNotification() calls.
     */
    override fun onListenerConnected() {
        super.onListenerConnected()
        isServiceConnected = true
        Timber.i("🔌 NotificationListener CONNECTED - Service is active")
    }

    /**
     * Called when notification listener is disconnected.
     * This can happen if user revokes permission or service crashes.
     *
     * We attempt to rebind the service to maintain functionality.
     */
    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        isServiceConnected = false
        Timber.w("🔌 NotificationListener DISCONNECTED - Attempting rebind")

        // Request rebind to maintain service connectivity
        try {
            requestRebind(ComponentName(this, UmbralNotificationService::class.java))
        } catch (e: Exception) {
            Timber.e(e, "Failed to request rebind")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceConnected = false
        serviceScope.cancel()
        Timber.d("UmbralNotificationService destroyed")
    }
}
