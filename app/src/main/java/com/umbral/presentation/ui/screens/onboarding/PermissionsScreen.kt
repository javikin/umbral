package com.umbral.presentation.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.umbral.domain.model.NfcStatus
import com.umbral.domain.model.PermissionStatus
import com.umbral.domain.model.RequiredPermission
import com.umbral.presentation.ui.theme.UmbralDimens
import com.umbral.presentation.viewmodel.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onContinue: () -> Unit
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = androidx.compose.ui.platform.LocalContext.current

    // Prevent back navigation - exit app instead
    BackHandler {
        (context as? android.app.Activity)?.finish()
    }

    // Refresh permissions when returning to this screen
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshPermissions()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Permisos necesarios") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = UmbralDimens.screenPaddingHorizontal)
        ) {
            // Explicación
            Text(
                text = "Umbral necesita algunos permisos para funcionar correctamente. Estos permisos son necesarios para detectar qué apps están abiertas y mostrar la pantalla de bloqueo.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = UmbralDimens.spaceLg)
            )

            // Lista de permisos
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceMd)
            ) {
                // Usage Stats (obligatorio)
                item {
                    PermissionCard(
                        icon = Icons.Default.BarChart,
                        title = "Acceso a uso de apps",
                        description = "Necesario para detectar cuándo abres una app bloqueada",
                        status = uiState.permissionStates.usageStats,
                        isRequired = true,
                        onRequestPermission = {
                            viewModel.openPermissionSettings(RequiredPermission.USAGE_STATS)
                        }
                    )
                }

                // Overlay (obligatorio)
                item {
                    PermissionCard(
                        icon = Icons.Default.Layers,
                        title = "Mostrar sobre otras apps",
                        description = "Necesario para mostrar la pantalla de bloqueo",
                        status = uiState.permissionStates.overlay,
                        isRequired = true,
                        onRequestPermission = {
                            viewModel.openPermissionSettings(RequiredPermission.OVERLAY)
                        }
                    )
                }

                // Notifications (recomendado)
                item {
                    PermissionCard(
                        icon = Icons.Default.Notifications,
                        title = "Notificaciones",
                        description = "Recomendado para mostrarte el estado del bloqueo",
                        status = uiState.permissionStates.notifications,
                        isRequired = false,
                        onRequestPermission = {
                            viewModel.openPermissionSettings(RequiredPermission.NOTIFICATIONS)
                        }
                    )
                }

                // NFC status
                item {
                    NfcStatusCard(
                        status = uiState.permissionStates.nfc,
                        onOpenSettings = { viewModel.openNfcSettings() }
                    )
                }
            }

            // Botón continuar
            val canContinue = uiState.permissionStates.usageStats == PermissionStatus.GRANTED &&
                    uiState.permissionStates.overlay == PermissionStatus.GRANTED

            Button(
                onClick = onContinue,
                enabled = canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = UmbralDimens.spaceLg)
            ) {
                Text("Continuar")
            }

            if (!canContinue) {
                Text(
                    text = "Los permisos marcados con * son obligatorios",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = UmbralDimens.spaceMd)
                )
            }
        }
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    status: PermissionStatus,
    isRequired: Boolean,
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                PermissionStatus.GRANTED -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                PermissionStatus.DENIED,
                PermissionStatus.PERMANENTLY_DENIED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralDimens.spaceLg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceLg)
        ) {
            // Icono
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = when (status) {
                    PermissionStatus.GRANTED -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(32.dp)
            )

            // Texto
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceXs)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall
                    )
                    if (isRequired) {
                        Text(
                            text = "*",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Estado / Botón
            when (status) {
                PermissionStatus.GRANTED -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Concedido",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                else -> {
                    TextButton(onClick = onRequestPermission) {
                        Text("Permitir")
                    }
                }
            }
        }
    }
}

@Composable
private fun NfcStatusCard(
    status: NfcStatus,
    onOpenSettings: () -> Unit
) {
    val (icon, title, description, actionText) = when (status) {
        NfcStatus.ENABLED -> listOf(
            Icons.Default.Nfc,
            "NFC habilitado",
            "Podrás usar tags NFC para activar el bloqueo",
            null
        )
        NfcStatus.DISABLED -> listOf(
            Icons.Default.Nfc,
            "NFC deshabilitado",
            "Habilita NFC para usar tags físicos. También puedes usar códigos QR.",
            "Habilitar"
        )
        NfcStatus.NOT_AVAILABLE -> listOf(
            Icons.Default.Nfc,
            "NFC no disponible",
            "Tu dispositivo no tiene NFC. Podrás usar códigos QR como alternativa.",
            null
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralDimens.spaceLg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceLg)
        ) {
            Icon(
                imageVector = icon as ImageVector,
                contentDescription = null,
                tint = if (status == NfcStatus.ENABLED)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title as String,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = description as String,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (actionText != null) {
                TextButton(onClick = onOpenSettings) {
                    Text(actionText as String)
                }
            } else if (status == NfcStatus.ENABLED) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
