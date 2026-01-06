package com.umbral.presentation.tile

import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import com.umbral.R
import com.umbral.domain.blocking.BlockingManager
import com.umbral.domain.blocking.ProfileRepository
import com.umbral.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Quick Settings tile for toggling blocking on/off.
 * Allows quick access to blocking functionality from the notification shade.
 */
@AndroidEntryPoint
class UmbralTileService : TileService() {

    @Inject
    lateinit var blockingManager: BlockingManager

    @Inject
    lateinit var profileRepository: ProfileRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        serviceScope.launch {
            try {
                val activeProfile = profileRepository.getActiveProfile().first()

                if (activeProfile == null) {
                    // No active profile, open app to create one
                    Timber.d("No active profile, opening app")
                    openApp()
                    return@launch
                }

                val isBlocking = blockingManager.isBlocking

                if (isBlocking) {
                    // Attempting to unblock
                    if (activeProfile.isStrictMode) {
                        // Strict mode - cannot unblock from tile
                        Timber.d("Strict mode enabled, showing toast")
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.tile_strict_mode_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Normal mode - allow unblock
                        Timber.d("Stopping blocking from tile")
                        blockingManager.stopBlocking()
                        updateTileState()
                    }
                } else {
                    // Activate blocking with active profile
                    Timber.d("Starting blocking with profile: ${activeProfile.name}")
                    blockingManager.startBlocking(activeProfile.id)
                    updateTileState()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error toggling blocking from tile")
                Toast.makeText(
                    applicationContext,
                    getString(R.string.error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateTileState() {
        serviceScope.launch {
            try {
                val isBlocking = blockingManager.isBlocking
                val activeProfile = profileRepository.getActiveProfile().first()

                qsTile?.apply {
                    state = if (isBlocking) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE

                    label = if (isBlocking && activeProfile != null) {
                        activeProfile.name
                    } else {
                        getString(R.string.tile_label_off)
                    }

                    subtitle = if (isBlocking) {
                        getString(R.string.tile_subtitle_active)
                    } else {
                        getString(R.string.tile_subtitle_inactive)
                    }

                    icon = Icon.createWithResource(
                        applicationContext,
                        if (isBlocking) R.drawable.ic_shield_on else R.drawable.ic_shield_off
                    )

                    updateTile()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error updating tile state")
            }
        }
    }

    private fun openApp() {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivityAndCollapse(intent)
    }
}
