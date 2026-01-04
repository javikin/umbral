package com.umbral.data.qr

import com.umbral.data.security.EncryptionManager
import com.umbral.domain.blocking.ProfileRepository
import com.umbral.domain.qr.QrAction
import com.umbral.domain.qr.QrPayload
import com.umbral.domain.qr.QrScanResult
import com.umbral.domain.qr.QrValidator
import javax.inject.Inject

class QrValidatorImpl @Inject constructor(
    private val encryptionManager: EncryptionManager,
    private val profileRepository: ProfileRepository
) : QrValidator {

    private val urlPattern = Regex("^${QrPayload.SCHEME}://v(\\d+)/(.+)\\?sig=(.+)$")

    override suspend fun validate(rawContent: String): QrScanResult {
        if (!isUmbralQr(rawContent)) {
            return QrScanResult.NotUmbralQr
        }

        val matchResult = urlPattern.matchEntire(rawContent)
            ?: return QrScanResult.InvalidFormat(rawContent)

        val version = matchResult.groupValues[1].toIntOrNull()
            ?: return QrScanResult.InvalidFormat(rawContent)

        if (version > QrPayload.CURRENT_VERSION) {
            return QrScanResult.InvalidFormat("Version no soportada: $version")
        }

        val encryptedPayload = matchResult.groupValues[2]
        val signature = matchResult.groupValues[3]

        // Verify HMAC signature
        val expectedSignature = encryptionManager.hmac(encryptedPayload)
        if (signature != expectedSignature) {
            val payload = decryptPayload(encryptedPayload)
                ?: return QrScanResult.InvalidFormat(rawContent)
            return QrScanResult.InvalidSignature(payload)
        }

        val payload = decryptPayload(encryptedPayload)
            ?: return QrScanResult.InvalidFormat(rawContent)

        // Check expiration
        if (isExpired(payload)) {
            return QrScanResult.ExpiredQr(
                payload = payload,
                expiredAt = payload.timestamp + (QrPayload.MAX_AGE_HOURS * 3600 * 1000L)
            )
        }

        // Verify profile exists
        val profile = profileRepository.getProfileById(payload.profileId)
            ?: return QrScanResult.ProfileNotFound(payload.profileId)

        return QrScanResult.Success(payload, profile)
    }

    override fun isUmbralQr(rawContent: String): Boolean {
        return rawContent.startsWith("${QrPayload.SCHEME}://")
    }

    override fun decryptPayload(encryptedPayload: String): QrPayload? {
        return try {
            val decrypted = encryptionManager.decrypt(encryptedPayload)
            parseJsonPayload(decrypted)
        } catch (e: Exception) {
            null
        }
    }

    override fun verifySignature(payload: QrPayload): Boolean {
        return payload.signature.isNotEmpty()
    }

    override fun isExpired(payload: QrPayload): Boolean {
        val maxAge = QrPayload.MAX_AGE_HOURS * 3600 * 1000L
        return System.currentTimeMillis() - payload.timestamp > maxAge
    }

    private fun parseJsonPayload(json: String): QrPayload? {
        return try {
            // Simple JSON parsing without kotlinx.serialization
            val versionMatch = Regex("\"version\":(\\d+)").find(json)
            val profileIdMatch = Regex("\"profileId\":\"([^\"]+)\"").find(json)
            val actionMatch = Regex("\"action\":\"([^\"]+)\"").find(json)
            val timestampMatch = Regex("\"timestamp\":(\\d+)").find(json)

            if (versionMatch == null || profileIdMatch == null ||
                actionMatch == null || timestampMatch == null
            ) {
                return null
            }

            QrPayload(
                version = versionMatch.groupValues[1].toInt(),
                profileId = profileIdMatch.groupValues[1],
                action = QrAction.valueOf(actionMatch.groupValues[1]),
                timestamp = timestampMatch.groupValues[1].toLong(),
                signature = ""
            )
        } catch (e: Exception) {
            null
        }
    }
}
