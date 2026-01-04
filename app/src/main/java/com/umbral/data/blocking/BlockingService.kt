package com.umbral.data.blocking

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.umbral.R
import com.umbral.domain.blocking.BlockingManager
import com.umbral.domain.blocking.ForegroundAppMonitor
import com.umbral.presentation.ui.blocking.BlockingActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BlockingService : Service() {

    @Inject
    lateinit var blockingManager: BlockingManager

    @Inject
    lateinit var foregroundAppMonitor: ForegroundAppMonitor

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var monitoringJob: Job? = null

    companion object {
        const val ACTION_START = "com.umbral.action.START_BLOCKING"
        const val ACTION_STOP = "com.umbral.action.STOP_BLOCKING"

        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "umbral_blocking_channel"
        private const val MONITOR_INTERVAL_MS = 500L
    }

    override fun onCreate() {
        super.onCreate()
        Timber.d("BlockingService created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("BlockingService onStartCommand: ${intent?.action}")

        when (intent?.action) {
            ACTION_START -> {
                startForeground(NOTIFICATION_ID, createNotification())
                startMonitoring()
            }
            ACTION_STOP -> {
                stopMonitoring()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            else -> {
                // Service restarted by system, check if we should be blocking
                if (blockingManager.isBlocking) {
                    startForeground(NOTIFICATION_ID, createNotification())
                    startMonitoring()
                } else {
                    stopSelf()
                }
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        stopMonitoring()
        serviceScope.cancel()
        Timber.d("BlockingService destroyed")
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.blocking_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.blocking_notification_channel_description)
            setShowBadge(false)
        }

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val state = blockingManager.blockingState.value

        val contentIntent = packageManager.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(
                this,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.blocking_active))
            .setContentText(
                state.activeProfileName?.let {
                    getString(R.string.blocking_notification_text, it)
                } ?: getString(R.string.blocking_active)
            )
            .setSmallIcon(R.drawable.ic_shield)
            .setOngoing(true)
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun startMonitoring() {
        if (monitoringJob?.isActive == true) {
            Timber.d("Monitoring already active")
            return
        }

        Timber.d("Starting app monitoring")
        monitoringJob = serviceScope.launch {
            var lastBlockedApp: String? = null

            while (isActive && blockingManager.isBlocking) {
                try {
                    val currentApp = foregroundAppMonitor.getCurrentForegroundApp()

                    if (currentApp != null && blockingManager.isAppBlocked(currentApp)) {
                        // Only show blocking screen if it's a different app or first time
                        if (currentApp != lastBlockedApp) {
                            Timber.d("Blocked app detected: $currentApp")
                            showBlockingScreen(currentApp)
                            lastBlockedApp = currentApp
                        }
                    } else {
                        lastBlockedApp = null
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error during app monitoring")
                }

                delay(MONITOR_INTERVAL_MS)
            }
        }
    }

    private fun stopMonitoring() {
        Timber.d("Stopping app monitoring")
        monitoringJob?.cancel()
        monitoringJob = null
    }

    private fun showBlockingScreen(blockedPackage: String) {
        val intent = Intent(this, BlockingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(BlockingActivity.EXTRA_BLOCKED_PACKAGE, blockedPackage)
        }
        startActivity(intent)
    }
}
