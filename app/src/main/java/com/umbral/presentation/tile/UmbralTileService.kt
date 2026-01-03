package com.umbral.presentation.tile

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import timber.log.Timber

/**
 * Quick Settings tile for toggling blocking on/off.
 * Placeholder - full implementation in Week 11.
 */
class UmbralTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        Timber.d("Tile clicked")
        // Toggle blocking state
        updateTile()
    }

    private fun updateTile() {
        qsTile?.apply {
            state = Tile.STATE_INACTIVE
            label = "Umbral"
            updateTile()
        }
    }
}
