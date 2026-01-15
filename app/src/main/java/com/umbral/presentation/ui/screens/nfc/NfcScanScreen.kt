package com.umbral.presentation.ui.screens.nfc

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.SignalWifiOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.umbral.R
import com.umbral.domain.nfc.NfcState
import com.umbral.presentation.viewmodel.NfcScanViewModel
import com.umbral.presentation.viewmodel.ScanState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NfcScanScreen(
    onNavigateBack: () -> Unit,
    onNavigateToTags: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NfcScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.scan_nfc)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                windowInsets = WindowInsets(0.dp)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (uiState.nfcState) {
                is NfcState.NotAvailable -> {
                    NfcNotAvailableContent()
                }
                is NfcState.Disabled -> {
                    NfcDisabledContent(
                        onEnableClick = { viewModel.openNfcSettings(context) }
                    )
                }
                else -> {
                    NfcScanContent(
                        scanState = uiState.scanState,
                        showRegisterForm = uiState.showRegisterForm,
                        tagName = uiState.tagName,
                        tagLocation = uiState.tagLocation,
                        onTagNameChange = viewModel::updateTagName,
                        onTagLocationChange = viewModel::updateTagLocation,
                        onShowRegisterForm = viewModel::showRegisterForm,
                        onHideRegisterForm = viewModel::hideRegisterForm,
                        onStartWaitingForTag = viewModel::startWaitingForTag,
                        onCancelWaitingForTag = viewModel::cancelWaitingForTag,
                        onResetClick = viewModel::resetState,
                        onNavigateToTags = onNavigateToTags
                    )
                }
            }
        }

        // Register tag dialog
        if (uiState.showRegisterDialog) {
            RegisterTagDialog(
                tagName = uiState.tagName,
                tagLocation = uiState.tagLocation,
                onTagNameChange = viewModel::updateTagName,
                onTagLocationChange = viewModel::updateTagLocation,
                onConfirm = viewModel::registerTag,
                onDismiss = viewModel::dismissDialog
            )
        }
    }
}

@Composable
private fun NfcNotAvailableContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.SignalWifiOff,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.nfc_not_supported),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tu dispositivo no tiene NFC",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NfcDisabledContent(
    onEnableClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.SignalWifiOff,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.nfc_disabled),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Activa NFC para escanear tags",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onEnableClick) {
            Text("Activar NFC")
        }
    }
}

@Composable
private fun NfcScanContent(
    scanState: ScanState,
    showRegisterForm: Boolean,
    tagName: String,
    tagLocation: String,
    onTagNameChange: (String) -> Unit,
    onTagLocationChange: (String) -> Unit,
    onShowRegisterForm: () -> Unit,
    onHideRegisterForm: () -> Unit,
    onStartWaitingForTag: () -> Unit,
    onCancelWaitingForTag: () -> Unit,
    onResetClick: () -> Unit,
    onNavigateToTags: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            showRegisterForm && scanState != ScanState.WaitingForTag -> {
                RegisterTagForm(
                    tagName = tagName,
                    tagLocation = tagLocation,
                    onTagNameChange = onTagNameChange,
                    onTagLocationChange = onTagLocationChange,
                    onSave = onStartWaitingForTag,
                    onCancel = onHideRegisterForm
                )
            }
            scanState == ScanState.WaitingForTag -> {
                WaitingForTagAnimation()
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Acerca el tag NFC",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mantén el tag cerca hasta que se registre",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(onClick = onCancelWaitingForTag) {
                    Text("Cancelar")
                }
            }
            scanState is ScanState.Idle || scanState is ScanState.Scanning -> {
                ScanningAnimation()
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.nfc_ready),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Escanea un tag registrado o registra uno nuevo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onShowRegisterForm) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Registrar nuevo tag")
                }
            }
            scanState is ScanState.Writing -> {
                WritingAnimation()
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Escribiendo tag...",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No retires el tag",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
            scanState is ScanState.Success -> {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.nfc_success),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bloqueo activado/desactivado",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedButton(onClick = onResetClick) {
                    Text("Escanear otro")
                }
            }
            scanState is ScanState.TagRegistered -> {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Tag registrado",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"${(scanState as ScanState.TagRegistered).tag.name}\" está listo para usar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onNavigateToTags) {
                    Text("Ver mis tags")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onResetClick) {
                    Text("Registrar otro")
                }
            }
            scanState is ScanState.Error -> {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    modifier = Modifier.size(120.dp),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(R.string.nfc_error),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = getErrorMessage((scanState as ScanState.Error).error),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onResetClick) {
                    Text(stringResource(R.string.retry))
                }
            }
        }
    }
}

@Composable
private fun ScanningAnimation(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nfc_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulse ring
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(scale)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = alpha * 0.3f),
                    shape = CircleShape
                )
        )

        // Middle ring
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        )

        // Inner circle with icon
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Nfc,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun WritingAnimation(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nfc_write")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Rotating ring
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f),
                    shape = CircleShape
                )
        )

        // Inner circle with icon
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
        }

        // Progress indicator
        androidx.compose.material3.CircularProgressIndicator(
            modifier = Modifier.size(180.dp),
            color = MaterialTheme.colorScheme.tertiary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
private fun RegisterTagForm(
    tagName: String,
    tagLocation: String,
    onTagNameChange: (String) -> Unit,
    onTagLocationChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Nfc,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Registrar nuevo tag",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Completa los datos y luego escanea el tag",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = tagName,
            onValueChange = onTagNameChange,
            label = { Text("Nombre del tag") },
            placeholder = { Text("Ej: Mi tag personal") },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Nfc, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = tagLocation,
            onValueChange = onTagLocationChange,
            label = { Text("Ubicación (opcional)") },
            placeholder = { Text("Ej: Puerta principal") },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.LocationOn, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar y escanear tag")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onCancel) {
            Text("Cancelar")
        }
    }
}

@Composable
private fun WaitingForTagAnimation(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waiting_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier.size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulse ring
        Box(
            modifier = Modifier
                .size(180.dp)
                .scale(scale)
                .background(
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = alpha * 0.3f),
                    shape = CircleShape
                )
        )

        // Middle ring
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f),
                    shape = CircleShape
                )
        )

        // Inner circle with icon
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Nfc,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun RegisterTagDialog(
    tagName: String,
    tagLocation: String,
    onTagNameChange: (String) -> Unit,
    onTagLocationChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Nuevo tag detectado")
        },
        text = {
            Column {
                Text(
                    text = "Este tag no está registrado. ¿Quieres agregarlo a Umbral?",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = tagName,
                    onValueChange = onTagNameChange,
                    label = { Text("Nombre del tag") },
                    placeholder = { Text("Ej: Mi tag personal") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Nfc, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = tagLocation,
                    onValueChange = onTagLocationChange,
                    label = { Text("Ubicación (opcional)") },
                    placeholder = { Text("Ej: Puerta principal") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Registrar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun getErrorMessage(error: com.umbral.domain.nfc.NfcError): String {
    return when (error) {
        com.umbral.domain.nfc.NfcError.NFC_NOT_AVAILABLE -> "NFC no disponible"
        com.umbral.domain.nfc.NfcError.NFC_DISABLED -> "NFC desactivado"
        com.umbral.domain.nfc.NfcError.TAG_NOT_SUPPORTED -> "Tag no soportado"
        com.umbral.domain.nfc.NfcError.TAG_READ_ONLY -> "Tag de solo lectura"
        com.umbral.domain.nfc.NfcError.TAG_TOO_SMALL -> "Tag muy pequeño"
        com.umbral.domain.nfc.NfcError.TAG_LOST -> "Tag perdido - acércalo de nuevo"
        com.umbral.domain.nfc.NfcError.TAG_IO_ERROR -> "Error de lectura"
        com.umbral.domain.nfc.NfcError.INVALID_NDEF -> "Formato inválido"
        com.umbral.domain.nfc.NfcError.INVALID_PAYLOAD -> "Datos inválidos"
        com.umbral.domain.nfc.NfcError.CHECKSUM_MISMATCH -> "Error de verificación"
        com.umbral.domain.nfc.NfcError.WRITE_FAILED -> "Error al escribir"
        com.umbral.domain.nfc.NfcError.TAG_ALREADY_REGISTERED -> "Tag ya registrado"
        com.umbral.domain.nfc.NfcError.UNKNOWN_ERROR -> "Error desconocido"
    }
}
