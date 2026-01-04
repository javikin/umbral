package com.umbral.data.qr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Environment
import android.util.Base64
import androidx.core.content.FileProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.umbral.data.security.EncryptionManager
import com.umbral.domain.qr.ErrorCorrectionLevel
import com.umbral.domain.qr.ImageFormat
import com.umbral.domain.qr.QrAction
import com.umbral.domain.qr.QrGenerationConfig
import com.umbral.domain.qr.QrGenerator
import com.umbral.domain.qr.QrPayload
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class QrGeneratorImpl @Inject constructor(
    private val context: Context,
    private val encryptionManager: EncryptionManager
) : QrGenerator {

    override suspend fun generateQrCode(
        profileId: String,
        action: QrAction,
        config: QrGenerationConfig
    ): Result<Bitmap> = withContext(Dispatchers.Default) {
        try {
            val payload = generatePayload(profileId, action)

            val hints = mutableMapOf<EncodeHintType, Any>().apply {
                put(EncodeHintType.ERROR_CORRECTION, config.errorCorrectionLevel.toZxing())
                put(EncodeHintType.MARGIN, config.margin)
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
            }

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(
                payload,
                BarcodeFormat.QR_CODE,
                config.size,
                config.size,
                hints
            )

            val bitmap = Bitmap.createBitmap(
                config.size,
                config.size,
                Bitmap.Config.ARGB_8888
            )

            for (x in 0 until config.size) {
                for (y in 0 until config.size) {
                    bitmap.setPixel(
                        x, y,
                        if (bitMatrix[x, y]) config.foregroundColor
                        else config.backgroundColor
                    )
                }
            }

            Result.success(bitmap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun generatePayload(profileId: String, action: QrAction): String {
        val payload = QrPayload(
            version = QrPayload.CURRENT_VERSION,
            profileId = profileId,
            action = action,
            timestamp = System.currentTimeMillis(),
            signature = ""
        )

        // Serialize to JSON
        val jsonPayload = buildString {
            append("{")
            append("\"version\":${payload.version},")
            append("\"profileId\":\"${payload.profileId}\",")
            append("\"action\":\"${payload.action.name}\",")
            append("\"timestamp\":${payload.timestamp}")
            append("}")
        }

        // Encrypt
        val encrypted = encryptionManager.encrypt(jsonPayload)

        // Generate HMAC signature
        val signature = encryptionManager.hmac(encrypted)

        return "${QrPayload.SCHEME}://v${payload.version}/${encrypted}?sig=${signature}"
    }

    override suspend fun exportToFile(
        qrBitmap: Bitmap,
        filename: String,
        format: ImageFormat
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(picturesDir, "$filename.${format.extension}")

            FileOutputStream(file).use { out ->
                qrBitmap.compress(format.toCompressFormat(), 100, out)
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            Result.success(uri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun toBase64(qrBitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun ErrorCorrectionLevel.toZxing(): com.google.zxing.qrcode.decoder.ErrorCorrectionLevel {
        return when (this) {
            ErrorCorrectionLevel.L -> com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.L
            ErrorCorrectionLevel.M -> com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.M
            ErrorCorrectionLevel.Q -> com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.Q
            ErrorCorrectionLevel.H -> com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.H
        }
    }
}
