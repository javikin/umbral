package com.umbral.notifications.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility class for managing Notification Listener Service permissions.
 *
 * Provides methods to:
 * - Check if notification access is enabled
 * - Open system settings to enable notification access
 * - Get the service component name for permission checks
 */
@Singleton
class NotificationPermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        private const val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
    }

    /**
     * Check if Umbral has notification listener permission enabled.
     *
     * @return true if notification access is granted, false otherwise
     */
    fun isNotificationAccessEnabled(): Boolean {
        val enabledListeners = Settings.Secure.getString(
            context.contentResolver,
            ENABLED_NOTIFICATION_LISTENERS
        )

        if (enabledListeners.isNullOrEmpty()) {
            return false
        }

        val packageName = context.packageName
        val flat = ComponentName(packageName, getServiceComponentName()).flattenToString()

        return enabledListeners.split(":").any { listener ->
            val componentName = ComponentName.unflattenFromString(listener)
            componentName != null && (componentName.packageName == packageName ||
                TextUtils.equals(listener, flat))
        }
    }

    /**
     * Open Android Settings screen for Notification Access.
     * User can manually enable/disable notification listener services here.
     *
     * @param context Context to start the activity from
     */
    fun openNotificationAccessSettings(context: Context) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    /**
     * Get the fully qualified component name of the notification service.
     *
     * @return String representing the service class name
     */
    private fun getServiceComponentName(): String {
        return "com.umbral.notifications.service.UmbralNotificationService"
    }

    /**
     * Get the ComponentName object for the notification service.
     *
     * @return ComponentName for the service
     */
    fun getServiceComponent(): ComponentName {
        return ComponentName(context, getServiceComponentName())
    }

    /**
     * Check if notification listener settings action is available.
     * Should always be true for API 21+.
     *
     * @return true if settings can be opened
     */
    fun canOpenSettings(): Boolean {
        return true // Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS available since API 22
    }
}
