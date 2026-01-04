package com.umbral.presentation.ui.screens.qr

import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.umbral.R
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.domain.qr.QrScannerState
import com.umbral.presentation.ui.components.PermissionRequestScreen
import com.umbral.presentation.viewmodel.QrScanViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun QrScanScreen(
    onDismiss: () -> Unit,
    onSuccess: (BlockingProfile) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: QrScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    // Navigate back on successful scan
    LaunchedEffect(uiState.scannedProfile) {
        uiState.scannedProfile?.let { profile ->
            onSuccess(profile)
            onDismiss()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.scan_qr)) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (cameraPermissionState.status.isGranted) {
                        IconButton(onClick = viewModel::toggleFlashlight) {
                            Icon(
                                imageVector = if (uiState.isFlashlightOn)
                                    Icons.Default.FlashOn
                                else
                                    Icons.Default.FlashOff,
                                contentDescription = if (uiState.isFlashlightOn)
                                    "Apagar linterna"
                                else
                                    "Encender linterna"
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                !cameraPermissionState.status.isGranted -> {
                    CameraPermissionRequest(
                        onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
                    )
                }

                else -> {
                    // Camera preview
                    AndroidView(
                        factory = { ctx ->
                            PreviewView(ctx).also { previewView ->
                                viewModel.startScanning(lifecycleOwner, previewView)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scanner overlay with viewfinder
                    QrScannerOverlay(
                        state = uiState.scannerState,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Gallery scan button
                    FloatingActionButton(
                        onClick = viewModel::scanFromGallery,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = "Escanear desde galeria"
                        )
                    }
                }
            }

            // Error snackbar
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    action = {
                        IconButton(onClick = viewModel::clearError) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopScanning()
        }
    }
}

@Composable
private fun CameraPermissionRequest(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    PermissionRequestScreen(
        title = stringResource(R.string.camera_permission),
        description = stringResource(R.string.camera_desc),
        onRequestPermission = onRequestPermission,
        modifier = modifier
    )
}

@Composable
private fun QrScannerOverlay(
    state: QrScannerState,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val scanAreaSize = size.minDimension * 0.7f
        val left = (size.width - scanAreaSize) / 2
        val top = (size.height - scanAreaSize) / 2

        // Semi-transparent background
        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            size = size
        )

        // Transparent scan area (cutout)
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(scanAreaSize, scanAreaSize),
            cornerRadius = CornerRadius(16.dp.toPx()),
            blendMode = BlendMode.Clear
        )

        // Border around scan area
        val borderColor = when (state) {
            QrScannerState.Scanning -> Color.White
            QrScannerState.Processing -> Color.Yellow
            is QrScannerState.Error -> Color.Red
            else -> Color.White.copy(alpha = 0.5f)
        }

        drawRoundRect(
            color = borderColor,
            topLeft = Offset(left, top),
            size = Size(scanAreaSize, scanAreaSize),
            cornerRadius = CornerRadius(16.dp.toPx()),
            style = Stroke(width = 3.dp.toPx())
        )

        // Corner markers
        val cornerLength = 40.dp.toPx()
        val cornerWidth = 4.dp.toPx()

        // Top-left corner
        drawLine(
            color = borderColor,
            start = Offset(left, top + cornerLength),
            end = Offset(left, top),
            strokeWidth = cornerWidth
        )
        drawLine(
            color = borderColor,
            start = Offset(left, top),
            end = Offset(left + cornerLength, top),
            strokeWidth = cornerWidth
        )

        // Top-right corner
        drawLine(
            color = borderColor,
            start = Offset(left + scanAreaSize - cornerLength, top),
            end = Offset(left + scanAreaSize, top),
            strokeWidth = cornerWidth
        )
        drawLine(
            color = borderColor,
            start = Offset(left + scanAreaSize, top),
            end = Offset(left + scanAreaSize, top + cornerLength),
            strokeWidth = cornerWidth
        )

        // Bottom-left corner
        drawLine(
            color = borderColor,
            start = Offset(left, top + scanAreaSize - cornerLength),
            end = Offset(left, top + scanAreaSize),
            strokeWidth = cornerWidth
        )
        drawLine(
            color = borderColor,
            start = Offset(left, top + scanAreaSize),
            end = Offset(left + cornerLength, top + scanAreaSize),
            strokeWidth = cornerWidth
        )

        // Bottom-right corner
        drawLine(
            color = borderColor,
            start = Offset(left + scanAreaSize - cornerLength, top + scanAreaSize),
            end = Offset(left + scanAreaSize, top + scanAreaSize),
            strokeWidth = cornerWidth
        )
        drawLine(
            color = borderColor,
            start = Offset(left + scanAreaSize, top + scanAreaSize - cornerLength),
            end = Offset(left + scanAreaSize, top + scanAreaSize),
            strokeWidth = cornerWidth
        )
    }
}
