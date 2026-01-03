package com.umbral.data.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * Foreground service for monitoring app usage and blocking apps.
 * Placeholder - full implementation in Week 5-6.
 */
@AndroidEntryPoint
class BlockingService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Timber.d("BlockingService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("BlockingService started")
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("BlockingService destroyed")
    }
}
