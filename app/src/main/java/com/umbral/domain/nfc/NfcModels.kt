package com.umbral.domain.nfc

import android.nfc.Tag
import java.time.Instant
import java.util.UUID

/**
 * Represents an NFC tag registered in Umbral.
 *
 * @property id Unique ID of the tag in the app
 * @property uid Physical identifier of the tag (7-10 bytes hex)
 * @property name User-given name (e.g., "Front Door")
 * @property location Optional location (e.g., "Home", "Office")
 * @property profileId Associated profile ID (null = uses last active)
 * @property createdAt Registration date
 * @property lastUsedAt Last time the tag was used
 * @property useCount Times the tag has been used
 */
data class NfcTag(
    val id: String = UUID.randomUUID().toString(),
    val uid: String,
    val name: String,
    val location: String? = null,
    val profileId: String? = null,
    val createdAt: Instant = Instant.now(),
    val lastUsedAt: Instant? = null,
    val useCount: Int = 0
)

/**
 * Supported NFC tag types.
 */
enum class TagType(
    val techName: String,
    val minMemory: Int,
    val maxMemory: Int,
    val supported: Boolean
) {
    NTAG213("NTAG213", 144, 144, true),
    NTAG215("NTAG215", 504, 504, true),
    NTAG216("NTAG216", 888, 888, true),
    MIFARE_ULTRALIGHT("MIFARE Ultralight", 64, 192, true),
    MIFARE_CLASSIC("MIFARE Classic", 1024, 4096, false),
    UNKNOWN("Unknown", 0, 0, false);

    companion object {
        fun fromTechList(techList: Array<String>): TagType {
            return when {
                techList.any { it.contains("MifareClassic") } -> MIFARE_CLASSIC
                techList.any { it.contains("MifareUltralight") } -> MIFARE_ULTRALIGHT
                techList.any { it.contains("NfcA") } -> NTAG213 // Default, can be refined
                else -> UNKNOWN
            }
        }
    }
}

/**
 * Payload written to/read from NFC tags.
 * Format: URI record with scheme "umbral://"
 *
 * Example: umbral://tag/v1/abc123def456?c=checksum
 */
data class NdefPayload(
    val version: Int = CURRENT_VERSION,
    val tagId: String,
    val checksum: String
) {
    companion object {
        const val SCHEME = "umbral"
        const val HOST = "tag"
        const val CURRENT_VERSION = 1

        fun toUri(payload: NdefPayload): String {
            return "$SCHEME://$HOST/v${payload.version}/${payload.tagId}?c=${payload.checksum}"
        }

        fun fromUri(uri: String): NdefPayload? {
            return try {
                val regex = Regex("$SCHEME://$HOST/v(\\d+)/([^?]+)\\?c=(.+)")
                val match = regex.find(uri) ?: return null
                val (version, tagId, checksum) = match.destructured
                NdefPayload(
                    version = version.toInt(),
                    tagId = tagId,
                    checksum = checksum
                )
            } catch (e: Exception) {
                null
            }
        }

        fun create(tagId: String): NdefPayload {
            val checksum = calculateChecksum(tagId)
            return NdefPayload(
                version = CURRENT_VERSION,
                tagId = tagId,
                checksum = checksum
            )
        }

        private fun calculateChecksum(data: String): String {
            var crc = 0xFFFFFFFF.toInt()
            for (byte in data.toByteArray()) {
                crc = crc xor byte.toInt()
                for (i in 0 until 8) {
                    crc = if (crc and 1 != 0) {
                        (crc ushr 1) xor 0xEDB88320.toInt()
                    } else {
                        crc ushr 1
                    }
                }
            }
            return (crc xor 0xFFFFFFFF.toInt()).toUInt().toString(16).padStart(8, '0')
        }
    }

    fun isValid(): Boolean {
        val expectedChecksum = calculateChecksum(tagId)
        return checksum == expectedChecksum
    }
}

/**
 * NFC hardware state.
 */
sealed class NfcState {
    data object NotAvailable : NfcState()
    data object Disabled : NfcState()
    data object Enabled : NfcState()
    data object Scanning : NfcState()
}

/**
 * Event when a tag is detected.
 */
sealed class TagEvent {
    data class KnownTag(val tag: NfcTag) : TagEvent()
    data class UnknownTag(val uid: String, val type: TagType, val androidTag: Tag) : TagEvent()
    data class InvalidTag(val error: NfcError) : TagEvent()
}

/**
 * Result of NFC operations.
 */
sealed class NfcResult<out T> {
    data class Success<T>(val data: T) : NfcResult<T>()
    data class Error(val error: NfcError) : NfcResult<Nothing>()
}

/**
 * NFC error types.
 */
enum class NfcError {
    // Hardware
    NFC_NOT_AVAILABLE,
    NFC_DISABLED,

    // Tag
    TAG_NOT_SUPPORTED,
    TAG_READ_ONLY,
    TAG_TOO_SMALL,
    TAG_LOST,
    TAG_IO_ERROR,

    // Data
    INVALID_NDEF,
    INVALID_PAYLOAD,
    CHECKSUM_MISMATCH,

    // Write
    WRITE_FAILED,
    TAG_ALREADY_REGISTERED,

    // General
    UNKNOWN_ERROR
}
