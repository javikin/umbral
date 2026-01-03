package com.umbral.domain.nfc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.nfc.Tag
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Main entry point for NFC operations.
 * Injected via Hilt as Singleton.
 */
interface NfcManager {

    /**
     * Current NFC hardware state.
     */
    val nfcState: StateFlow<NfcState>

    /**
     * Events when tags are detected.
     */
    val tagEvents: SharedFlow<TagEvent>

    /**
     * Check if NFC is available and enabled.
     */
    fun isNfcAvailable(): Boolean

    /**
     * Check if NFC is enabled.
     */
    fun isNfcEnabled(): Boolean

    /**
     * Open system NFC settings.
     */
    fun openNfcSettings(context: Context)

    /**
     * Enable foreground tag detection.
     * Call in Activity's onResume().
     */
    fun enableForegroundDispatch(activity: Activity)

    /**
     * Disable foreground tag detection.
     * Call in Activity's onPause().
     */
    fun disableForegroundDispatch(activity: Activity)

    /**
     * Process an Intent containing an NFC tag.
     * Call when Activity receives Intent with ACTION_TAG_DISCOVERED.
     */
    suspend fun processTagIntent(intent: Intent): NfcResult<TagEvent>

    /**
     * Write Umbral payload to a tag.
     * Tag must be present and connected.
     */
    suspend fun writeTag(tag: Tag, tagId: String): NfcResult<Unit>

    /**
     * Read tag UID from Android Tag object.
     */
    fun getTagUid(tag: Tag): String

    /**
     * Get tag type from Android Tag object.
     */
    fun getTagType(tag: Tag): TagType

    /**
     * Refresh NFC state (call after returning from settings).
     */
    fun refreshState()
}
