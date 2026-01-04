package com.umbral.data.blocking

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import android.os.Process
import com.umbral.domain.blocking.ForegroundAppMonitor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForegroundAppMonitorImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : ForegroundAppMonitor {

    private val usageStatsManager: UsageStatsManager by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    private val appOpsManager: AppOpsManager by lazy {
        context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    }

    companion object {
        private const val POLL_INTERVAL_MS = 500L
        private const val USAGE_QUERY_INTERVAL_MS = 1000L
    }

    override val foregroundApp: Flow<String?> = flow {
        var lastEmittedApp: String? = null

        while (true) {
            val currentApp = getCurrentForegroundApp()
            if (currentApp != lastEmittedApp) {
                lastEmittedApp = currentApp
                emit(currentApp)
            }
            delay(POLL_INTERVAL_MS)
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getCurrentForegroundApp(): String? {
        return withContext(Dispatchers.IO) {
            if (!hasUsageStatsPermission()) {
                Timber.w("Usage stats permission not granted")
                return@withContext null
            }

            try {
                getForegroundAppFromUsageEvents()
            } catch (e: Exception) {
                Timber.e(e, "Error getting foreground app")
                null
            }
        }
    }

    private fun getForegroundAppFromUsageEvents(): String? {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - USAGE_QUERY_INTERVAL_MS

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        val event = UsageEvents.Event()

        var lastForegroundApp: String? = null
        var lastForegroundTime = 0L

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            // Look for ACTIVITY_RESUMED or MOVE_TO_FOREGROUND events
            @Suppress("DEPRECATION")
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {

                if (event.timeStamp > lastForegroundTime) {
                    lastForegroundTime = event.timeStamp
                    lastForegroundApp = event.packageName
                }
            }
        }

        // Filter out our own package and system UI
        return when (lastForegroundApp) {
            context.packageName -> null
            "com.android.systemui" -> null
            else -> lastForegroundApp
        }
    }

    override fun hasUsageStatsPermission(): Boolean {
        return try {
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOpsManager.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            Timber.e(e, "Error checking usage stats permission")
            false
        }
    }
}
