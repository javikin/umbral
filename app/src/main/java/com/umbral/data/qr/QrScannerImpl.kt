package com.umbral.data.qr

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.umbral.domain.qr.QrScanResult
import com.umbral.domain.qr.QrScanner
import com.umbral.domain.qr.QrScannerState
import com.umbral.domain.qr.QrValidator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QrScannerImpl @Inject constructor(
    private val context: Context,
    private val qrValidator: QrValidator
) : QrScanner {

    private val _scannerState = MutableStateFlow<QrScannerState>(QrScannerState.Idle)
    override val scannerState: StateFlow<QrScannerState> = _scannerState.asStateFlow()

    private val _lastResult = MutableSharedFlow<QrScanResult>()
    override val lastResult: SharedFlow<QrScanResult> = _lastResult.asSharedFlow()

    private val _isFlashlightOn = MutableStateFlow(false)
    override val isFlashlightOn: StateFlow<Boolean> = _isFlashlightOn.asStateFlow()

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var imageAnalysis: ImageAnalysis? = null

    private val barcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    private var isProcessing = false

    override fun startScanning(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        _scannerState.value = QrScannerState.Initializing

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases(lifecycleOwner, previewView)
                _scannerState.value = QrScannerState.Scanning
            } catch (e: Exception) {
                _scannerState.value = QrScannerState.Error(
                    e.message ?: "Error al iniciar camara"
                )
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCameraUseCases(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context)
                ) { imageProxy ->
                    processImage(imageProxy)
                }
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider?.unbindAll()
        camera = cameraProvider?.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalysis
        )
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImage(imageProxy: ImageProxy) {
        if (isProcessing) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { rawValue ->
                            if (qrValidator.isUmbralQr(rawValue)) {
                                processScannedContent(rawValue)
                            }
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun processScannedContent(rawValue: String) {
        if (isProcessing) return

        isProcessing = true
        _scannerState.value = QrScannerState.Processing

        CoroutineScope(Dispatchers.Default).launch {
            val result = qrValidator.validate(rawValue)
            _lastResult.emit(result)
            _scannerState.value = QrScannerState.Scanning
            isProcessing = false
        }
    }

    override fun stopScanning() {
        cameraProvider?.unbindAll()
        _scannerState.value = QrScannerState.Idle
        _isFlashlightOn.value = false
        isProcessing = false
    }

    override suspend fun scanFromImage(imageUri: Uri): QrScanResult {
        return try {
            val inputImage = InputImage.fromFilePath(context, imageUri)
            val barcodes = barcodeScanner.process(inputImage).await()

            val qrBarcode = barcodes.firstOrNull { it.format == Barcode.FORMAT_QR_CODE }

            if (qrBarcode?.rawValue != null) {
                qrValidator.validate(qrBarcode.rawValue!!)
            } else {
                QrScanResult.NotUmbralQr
            }
        } catch (e: Exception) {
            QrScanResult.InvalidFormat(e.message ?: "")
        }
    }

    override fun toggleFlashlight() {
        val currentState = _isFlashlightOn.value
        camera?.cameraControl?.enableTorch(!currentState)
        _isFlashlightOn.value = !currentState
    }
}
