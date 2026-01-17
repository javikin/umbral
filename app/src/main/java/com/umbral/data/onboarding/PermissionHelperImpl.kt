package com.umbral.data.onboarding

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.nfc.NfcAdapter
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.umbral.domain.model.NfcStatus
import com.umbral.domain.model.PermissionStates
import com.umbral.domain.model.PermissionStatus
import com.umbral.domain.model.RequiredPermission
import com.umbral.domain.onboarding.PermissionHelper
import com.umbral.notifications.util.NotificationPermissionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionHelperImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationPermissionManager: NotificationPermissionManager
) : PermissionHelper {

    override fun checkAllPermissions(): PermissionStates {
        return PermissionStates(
            usageStats = checkPermission(RequiredPermission.USAGE_STATS),
            overlay = checkPermission(RequiredPermission.OVERLAY),
            notifications = checkPermission(RequiredPermission.NOTIFICATIONS),
            notificationListener = checkPermission(RequiredPermission.NOTIFICATION_LISTENER),
            nfc = checkNfcStatus()
        )
    }

    override fun checkPermission(permission: RequiredPermission): PermissionStatus {
        return when (permission) {
            RequiredPermission.USAGE_STATS -> checkUsageStatsPermission()
            RequiredPermission.OVERLAY -> checkOverlayPermission()
            RequiredPermission.NOTIFICATIONS,
            RequiredPermission.POST_NOTIFICATIONS -> checkNotificationPermission()
            RequiredPermission.NOTIFICATION_LISTENER -> checkNotificationListenerPermission()
        }
    }

    private fun checkUsageStatsPermission(): PermissionStatus {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),
                    context.packageName
                )
            }
            if (mode == AppOpsManager.MODE_ALLOWED) {
                PermissionStatus.GRANTED
            } else {
                PermissionStatus.DENIED
            }
        } catch (e: Exception) {
            PermissionStatus.DENIED
        }
    }

    private fun checkOverlayPermission(): PermissionStatus {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(context)) {
                PermissionStatus.GRANTED
            } else {
                PermissionStatus.DENIED
            }
        } else {
            PermissionStatus.GRANTED
        }
    }

    private fun checkNotificationPermission(): PermissionStatus {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val result = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (result == PackageManager.PERMISSION_GRANTED) {
                PermissionStatus.GRANTED
            } else {
                PermissionStatus.DENIED
            }
        } else {
            // Pre Android 13, notifications don't need explicit permission
            PermissionStatus.GRANTED
        }
    }

    private fun checkNotificationListenerPermission(): PermissionStatus {
        return if (notificationPermissionManager.isNotificationAccessEnabled()) {
            PermissionStatus.GRANTED
        } else {
            PermissionStatus.DENIED
        }
    }

    override fun openPermissionSettings(permission: RequiredPermission) {
        val intent = when (permission) {
            RequiredPermission.USAGE_STATS -> {
                Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            }
            RequiredPermission.OVERLAY -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                } else {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                }
            }
            RequiredPermission.NOTIFICATIONS,
            RequiredPermission.POST_NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    }
                } else {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                }
            }
            RequiredPermission.NOTIFICATION_LISTENER -> {
                Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun checkNfcStatus(): NfcStatus {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
        return when {
            nfcAdapter == null -> NfcStatus.NOT_AVAILABLE
            !nfcAdapter.isEnabled -> NfcStatus.DISABLED
            else -> NfcStatus.ENABLED
        }
    }

    override fun openNfcSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent(Settings.Panel.ACTION_NFC)
        } else {
            Intent(Settings.ACTION_NFC_SETTINGS)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    override fun hasMinimumPermissions(): Boolean {
        val states = checkAllPermissions()
        return states.usageStats == PermissionStatus.GRANTED &&
                states.overlay == PermissionStatus.GRANTED
    }

    override fun hasAllRecommendedPermissions(): Boolean {
        val states = checkAllPermissions()
        return hasMinimumPermissions() &&
                states.notifications == PermissionStatus.GRANTED &&
                states.nfc == NfcStatus.ENABLED
    }
}
