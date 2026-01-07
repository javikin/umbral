package com.umbral.notifications.domain

import android.app.Notification
import android.service.notification.StatusBarNotification
import com.umbral.notifications.data.preferences.NotificationPreferences
import com.umbral.notifications.domain.model.SystemWhitelist
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Checks whether a notification should be allowed to show based on whitelist rules.
 *
 * Priority order:
 * 1. System whitelist (calls, SMS, alarms, system apps)
 * 2. Notification category (CALL, ALARM, MESSAGE)
 * 3. User's custom whitelist (authenticators, banking apps)
 * 4. Battery low notifications
 */
@Singleton
class NotificationWhitelistChecker @Inject constructor(
    private val notificationPreferences: NotificationPreferences
) {

    /**
     * Determines if a notification should be allowed to show.
     *
     * @param sbn The StatusBarNotification to check
     * @return true if the notification should be allowed, false if it should be blocked
     */
    suspend fun shouldAllowNotification(sbn: StatusBarNotification): Boolean {
        val packageName = sbn.packageName
        val notification = sbn.notification

        // 1. Check system whitelist (highest priority)
        if (SystemWhitelist.isAlwaysAllowed(packageName)) {
            return true
        }

        // 2. Check notification category
        val category = notification.category
        if (SystemWhitelist.isCategoryAllowed(category)) {
            return true
        }

        // 3. Check user's custom whitelist
        val userWhitelist = notificationPreferences.userWhitelist.first()
        if (packageName in userWhitelist) {
            return true
        }

        // 4. Check for low battery notification
        if (isBatteryLowNotification(sbn)) {
            return true
        }

        // 5. Check for critical system alerts
        if (isCriticalSystemAlert(sbn)) {
            return true
        }

        return false
    }

    /**
     * Checks if a notification is a low battery warning.
     *
     * Battery low notifications are critical as they warn users about device shutdown,
     * which could prevent them from disabling blocking if needed.
     */
    private fun isBatteryLowNotification(sbn: StatusBarNotification): Boolean {
        val packageName = sbn.packageName
        val notification = sbn.notification

        // Check if it's from the system
        if (packageName != "android" && packageName != "com.android.systemui") {
            return false
        }

        // Check for battery-related actions
        // Note: We can't easily inspect PendingIntent actions, so we skip this check
        // and rely on notification content analysis below

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
     *
     * Examples:
     * - Storage almost full
     * - SIM card removed
     * - Critical security updates
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
     * Gets a human-readable reason for why a notification was allowed.
     * Useful for debugging and user transparency.
     *
     * @param sbn The StatusBarNotification that was allowed
     * @return A string describing why it was allowed (in Spanish for UI display)
     */
    suspend fun getAllowReason(sbn: StatusBarNotification): String {
        val packageName = sbn.packageName

        return when {
            SystemWhitelist.isAlwaysAllowed(packageName) ->
                "App del sistema crítica"

            SystemWhitelist.isCategoryAllowed(sbn.notification.category) ->
                "Categoría crítica: ${sbn.notification.category}"

            packageName in notificationPreferences.userWhitelist.first() ->
                "En lista blanca personal"

            isBatteryLowNotification(sbn) ->
                "Alerta de batería baja"

            isCriticalSystemAlert(sbn) ->
                "Alerta crítica del sistema"

            else ->
                "Permitida (razón desconocida)"
        }
    }
}
