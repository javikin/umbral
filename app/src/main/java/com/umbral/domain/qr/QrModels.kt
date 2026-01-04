package com.umbral.domain.qr

import android.graphics.Bitmap
import android.net.Uri
import com.umbral.domain.blocking.BlockingProfile

/**
 * Payload encriptado contenido en el QR code
 * Formato: umbral://v1/{encrypted_data}?sig={signature}
 */
data class QrPayload(
    val version: Int,
    val profileId: String,
    val action: QrAction,
    val timestamp: Long,
    val signature: String = ""
) {
    companion object {
        const val CURRENT_VERSION = 1
        const val SCHEME = "umbral"
        const val MAX_AGE_HOURS = 24 * 365  // QR valido por 1 año
    }
}

enum class QrAction {
    ACTIVATE,    // Activar perfil
    DEACTIVATE,  // Desactivar bloqueo
    TOGGLE       // Toggle segun estado actual
}

/**
 * Resultado del escaneo de QR
 */
sealed class QrScanResult {
    data class Success(
        val payload: QrPayload,
        val profile: BlockingProfile
    ) : QrScanResult()

    data class InvalidFormat(
        val rawContent: String
    ) : QrScanResult()

    data class ExpiredQr(
        val payload: QrPayload,
        val expiredAt: Long
    ) : QrScanResult()

    data class ProfileNotFound(
        val profileId: String
    ) : QrScanResult()

    data class InvalidSignature(
        val payload: QrPayload
    ) : QrScanResult()

    object NotUmbralQr : QrScanResult()
}

/**
 * Configuracion de generacion de QR
 */
data class QrGenerationConfig(
    val size: Int = 512,
    val errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.M,
    val margin: Int = 2,
    val foregroundColor: Int = android.graphics.Color.BLACK,
    val backgroundColor: Int = android.graphics.Color.WHITE,
    val logoOverlay: Boolean = false
)

enum class ErrorCorrectionLevel {
    L,  // ~7% recovery
    M,  // ~15% recovery
    Q,  // ~25% recovery
    H   // ~30% recovery
}

/**
 * QR code generado y almacenado
 */
data class GeneratedQr(
    val id: String,
    val profileId: String,
    val action: QrAction,
    val payload: String,
    val createdAt: Long,
    val lastScannedAt: Long?,
    val scanCount: Int,
    val isActive: Boolean
)

/**
 * Estado del scanner de QR
 */
sealed class QrScannerState {
    object Idle : QrScannerState()
    object Initializing : QrScannerState()
    object Scanning : QrScannerState()
    object Processing : QrScannerState()
    data class Error(val message: String) : QrScannerState()
}

/**
 * Formato de imagen para exportar
 */
enum class ImageFormat {
    PNG,
    JPEG,
    WEBP;

    val extension: String
        get() = when (this) {
            PNG -> "png"
            JPEG -> "jpg"
            WEBP -> "webp"
        }

    fun toCompressFormat(): Bitmap.CompressFormat = when (this) {
        PNG -> Bitmap.CompressFormat.PNG
        JPEG -> Bitmap.CompressFormat.JPEG
        WEBP -> Bitmap.CompressFormat.WEBP
    }
}
