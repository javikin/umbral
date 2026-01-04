package com.umbral.domain.qr

import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Interface para generar codigos QR
 */
interface QrGenerator {
    /**
     * Genera un QR code para un perfil
     */
    suspend fun generateQrCode(
        profileId: String,
        action: QrAction = QrAction.TOGGLE,
        config: QrGenerationConfig = QrGenerationConfig()
    ): Result<Bitmap>

    /**
     * Genera el payload encriptado
     */
    fun generatePayload(
        profileId: String,
        action: QrAction
    ): String

    /**
     * Exporta el QR como archivo de imagen
     */
    suspend fun exportToFile(
        qrBitmap: Bitmap,
        filename: String,
        format: ImageFormat = ImageFormat.PNG
    ): Result<Uri>

    /**
     * Obtiene el QR como base64 para compartir
     */
    fun toBase64(qrBitmap: Bitmap): String
}

/**
 * Interface para escanear codigos QR
 */
interface QrScanner {
    /**
     * Estado actual del scanner
     */
    val scannerState: StateFlow<QrScannerState>

    /**
     * Ultimo resultado de escaneo
     */
    val lastResult: SharedFlow<QrScanResult>

    /**
     * Inicia el escaneo de QR
     */
    fun startScanning(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    )

    /**
     * Detiene el escaneo
     */
    fun stopScanning()

    /**
     * Procesa una imagen estatica (desde galeria)
     */
    suspend fun scanFromImage(imageUri: Uri): QrScanResult

    /**
     * Activa/desactiva la linterna
     */
    fun toggleFlashlight()

    /**
     * Estado actual de la linterna
     */
    val isFlashlightOn: StateFlow<Boolean>
}

/**
 * Interface para validar codigos QR
 */
interface QrValidator {
    /**
     * Valida y parsea el contenido de un QR escaneado
     */
    suspend fun validate(rawContent: String): QrScanResult

    /**
     * Verifica si el contenido es un QR de Umbral
     */
    fun isUmbralQr(rawContent: String): Boolean

    /**
     * Desencripta el payload
     */
    fun decryptPayload(encryptedPayload: String): QrPayload?

    /**
     * Verifica la firma HMAC del payload
     */
    fun verifySignature(payload: QrPayload): Boolean

    /**
     * Verifica si el QR no ha expirado
     */
    fun isExpired(payload: QrPayload): Boolean
}
