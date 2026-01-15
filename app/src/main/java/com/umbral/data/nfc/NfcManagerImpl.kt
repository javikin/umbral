package com.umbral.data.nfc

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.MifareUltralight
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV
import android.os.Build
import android.provider.Settings
import com.umbral.data.local.dao.NfcTagDao
import com.umbral.domain.nfc.NdefPayload
import com.umbral.domain.nfc.NfcError
import com.umbral.domain.nfc.NfcManager
import com.umbral.domain.nfc.NfcResult
import com.umbral.domain.nfc.NfcState
import com.umbral.domain.nfc.NfcTag
import com.umbral.domain.nfc.TagEvent
import com.umbral.domain.nfc.TagType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NfcManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val nfcTagDao: NfcTagDao
) : NfcManager {

    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)

    private val _nfcState = MutableStateFlow(getCurrentState())
    override val nfcState: StateFlow<NfcState> = _nfcState.asStateFlow()

    private val _tagEvents = MutableSharedFlow<TagEvent>()
    override val tagEvents: SharedFlow<TagEvent> = _tagEvents.asSharedFlow()

    override fun isNfcAvailable(): Boolean = nfcAdapter != null

    override fun isNfcEnabled(): Boolean = nfcAdapter?.isEnabled == true

    override fun openNfcSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_NFC_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Timber.e(e, "Error opening NFC settings")
            // Fallback to wireless settings
            val fallbackIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallbackIntent)
        }
    }

    override fun enableForegroundDispatch(activity: Activity) {
        nfcAdapter?.let { adapter ->
            if (!adapter.isEnabled) {
                _nfcState.value = NfcState.Disabled
                return
            }

            val intent = Intent(activity, activity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

            val pendingIntent = PendingIntent.getActivity(
                activity,
                0,
                intent,
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val filters = arrayOf(
                IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
                    try {
                        addDataScheme(NdefPayload.SCHEME)
                    } catch (e: Exception) {
                        Timber.e(e, "Error adding data scheme")
                    }
                },
                IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
            )

            val techList = arrayOf(
                arrayOf(Ndef::class.java.name),
                arrayOf(NdefFormatable::class.java.name),
                arrayOf(NfcA::class.java.name),
                arrayOf(MifareUltralight::class.java.name),
                arrayOf(NfcV::class.java.name),
                arrayOf(NfcF::class.java.name),
                arrayOf(NfcB::class.java.name),
                arrayOf(IsoDep::class.java.name)
            )

            try {
                adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList)
                _nfcState.value = NfcState.Scanning
                Timber.d("NFC foreground dispatch enabled")
            } catch (e: Exception) {
                Timber.e(e, "Error enabling foreground dispatch")
            }
        } ?: run {
            _nfcState.value = NfcState.NotAvailable
        }
    }

    override fun disableForegroundDispatch(activity: Activity) {
        nfcAdapter?.let { adapter ->
            try {
                adapter.disableForegroundDispatch(activity)
                _nfcState.value = if (adapter.isEnabled) NfcState.Enabled else NfcState.Disabled
                Timber.d("NFC foreground dispatch disabled")
            } catch (e: Exception) {
                Timber.e(e, "Error disabling foreground dispatch")
            }
        }
    }

    override suspend fun processTagIntent(intent: Intent): NfcResult<TagEvent> {
        val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }

        if (tag == null) {
            Timber.w("No tag found in intent")
            return NfcResult.Error(NfcError.TAG_LOST)
        }

        val uid = getTagUid(tag)
        val tagType = getTagType(tag)

        Timber.d("Tag detected: UID=$uid, Type=$tagType")

        if (!tagType.supported) {
            val event = TagEvent.InvalidTag(NfcError.TAG_NOT_SUPPORTED)
            _tagEvents.emit(event)
            return NfcResult.Error(NfcError.TAG_NOT_SUPPORTED)
        }

        // Check if tag is already registered
        val existingTag = nfcTagDao.getTagByUid(uid)
        if (existingTag != null) {
            val domainTag = existingTag.toDomain()
            val event = TagEvent.KnownTag(domainTag)
            _tagEvents.emit(event)

            // Update last used
            nfcTagDao.updateLastUsed(uid, System.currentTimeMillis())

            return NfcResult.Success(event)
        }

        // Try to read NDEF message
        val ndefResult = readNdefMessage(tag)
        if (ndefResult is NfcResult.Success) {
            val payload = ndefResult.data
            if (payload != null && payload.isValid()) {
                // Check if this tag ID is registered
                val tagById = nfcTagDao.getTagById(payload.tagId)
                if (tagById != null) {
                    val domainTag = tagById.toDomain()
                    val event = TagEvent.KnownTag(domainTag)
                    _tagEvents.emit(event)
                    return NfcResult.Success(event)
                }
            }
        }

        // Unknown tag
        val event = TagEvent.UnknownTag(uid, tagType, tag)
        _tagEvents.emit(event)
        return NfcResult.Success(event)
    }

    override suspend fun writeTag(tag: Tag, tagId: String): NfcResult<Unit> {
        val payload = NdefPayload.create(tagId)
        val uri = NdefPayload.toUri(payload)

        val ndefRecord = NdefRecord.createUri(uri)
        val ndefMessage = NdefMessage(arrayOf(ndefRecord))

        return try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                writeToNdef(ndef, ndefMessage)
            } else {
                val formatable = NdefFormatable.get(tag)
                if (formatable != null) {
                    formatAndWrite(formatable, ndefMessage)
                } else {
                    NfcResult.Error(NfcError.TAG_NOT_SUPPORTED)
                }
            }
        } catch (e: IOException) {
            Timber.e(e, "IO error writing to tag")
            NfcResult.Error(NfcError.TAG_IO_ERROR)
        } catch (e: Exception) {
            Timber.e(e, "Error writing to tag")
            NfcResult.Error(NfcError.WRITE_FAILED)
        }
    }

    override fun getTagUid(tag: Tag): String {
        return tag.id.joinToString("") { "%02X".format(it) }
    }

    override fun getTagType(tag: Tag): TagType {
        return TagType.fromTechList(tag.techList)
    }

    override fun refreshState() {
        _nfcState.value = getCurrentState()
    }

    private fun getCurrentState(): NfcState {
        return when {
            nfcAdapter == null -> NfcState.NotAvailable
            !nfcAdapter.isEnabled -> NfcState.Disabled
            else -> NfcState.Enabled
        }
    }

    private fun readNdefMessage(tag: Tag): NfcResult<NdefPayload?> {
        val ndef = Ndef.get(tag) ?: return NfcResult.Success(null)

        return try {
            ndef.connect()
            val ndefMessage = ndef.ndefMessage
            ndef.close()

            if (ndefMessage == null) {
                return NfcResult.Success(null)
            }

            val records = ndefMessage.records
            for (record in records) {
                if (record.tnf == NdefRecord.TNF_WELL_KNOWN &&
                    record.type.contentEquals(NdefRecord.RTD_URI)
                ) {
                    val uri = parseUriRecord(record)
                    if (uri.startsWith(NdefPayload.SCHEME)) {
                        val payload = NdefPayload.fromUri(uri)
                        if (payload != null) {
                            return NfcResult.Success(payload)
                        }
                    }
                }
            }

            NfcResult.Success(null)
        } catch (e: IOException) {
            Timber.e(e, "IO error reading tag")
            NfcResult.Error(NfcError.TAG_IO_ERROR)
        } catch (e: Exception) {
            Timber.e(e, "Error reading tag")
            NfcResult.Error(NfcError.INVALID_NDEF)
        }
    }

    private fun parseUriRecord(record: NdefRecord): String {
        val payload = record.payload
        if (payload.isEmpty()) return ""

        val prefixCode = payload[0].toInt() and 0xFF
        val prefix = URI_PREFIXES.getOrElse(prefixCode) { "" }
        val suffix = String(payload, 1, payload.size - 1, Charset.forName("UTF-8"))

        return prefix + suffix
    }

    private fun writeToNdef(ndef: Ndef, message: NdefMessage): NfcResult<Unit> {
        return try {
            ndef.connect()

            if (!ndef.isWritable) {
                ndef.close()
                return NfcResult.Error(NfcError.TAG_READ_ONLY)
            }

            if (ndef.maxSize < message.toByteArray().size) {
                ndef.close()
                return NfcResult.Error(NfcError.TAG_TOO_SMALL)
            }

            ndef.writeNdefMessage(message)
            ndef.close()

            Timber.d("Successfully wrote NDEF message to tag")
            NfcResult.Success(Unit)
        } catch (e: IOException) {
            Timber.e(e, "IO error writing NDEF")
            NfcResult.Error(NfcError.TAG_IO_ERROR)
        }
    }

    private fun formatAndWrite(formatable: NdefFormatable, message: NdefMessage): NfcResult<Unit> {
        return try {
            formatable.connect()
            formatable.format(message)
            formatable.close()

            Timber.d("Successfully formatted and wrote to tag")
            NfcResult.Success(Unit)
        } catch (e: IOException) {
            Timber.e(e, "IO error formatting tag")
            NfcResult.Error(NfcError.TAG_IO_ERROR)
        }
    }

    companion object {
        private val URI_PREFIXES = arrayOf(
            "", // 0x00
            "http://www.", // 0x01
            "https://www.", // 0x02
            "http://", // 0x03
            "https://", // 0x04
            "tel:", // 0x05
            "mailto:", // 0x06
            "ftp://anonymous:anonymous@", // 0x07
            "ftp://ftp.", // 0x08
            "ftps://", // 0x09
            "sftp://", // 0x0A
            "smb://", // 0x0B
            "nfs://", // 0x0C
            "ftp://", // 0x0D
            "dav://", // 0x0E
            "news:", // 0x0F
            "telnet://", // 0x10
            "imap:", // 0x11
            "rtsp://", // 0x12
            "urn:", // 0x13
            "pop:", // 0x14
            "sip:", // 0x15
            "sips:", // 0x16
            "tftp:", // 0x17
            "btspp://", // 0x18
            "btl2cap://", // 0x19
            "btgoep://", // 0x1A
            "tcpobex://", // 0x1B
            "irdaobex://", // 0x1C
            "file://", // 0x1D
            "urn:epc:id:", // 0x1E
            "urn:epc:tag:", // 0x1F
            "urn:epc:pat:", // 0x20
            "urn:epc:raw:", // 0x21
            "urn:epc:", // 0x22
            "urn:nfc:" // 0x23
        )
    }
}

// Extension to convert Entity to Domain
private fun com.umbral.data.local.entity.NfcTagEntity.toDomain(): NfcTag {
    return NfcTag(
        id = id,
        uid = uid,
        name = name,
        location = location,
        profileId = profileId,
        createdAt = java.time.Instant.ofEpochMilli(createdAt),
        lastUsedAt = lastUsedAt?.let { java.time.Instant.ofEpochMilli(it) },
        useCount = useCount
    )
}
