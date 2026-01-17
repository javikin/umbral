package com.umbral.presentation.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.umbral.domain.model.NfcStatus
import com.umbral.domain.model.PermissionStatus
import com.umbral.domain.model.RequiredPermission
import com.umbral.presentation.ui.components.ButtonVariant
import com.umbral.presentation.ui.components.UmbralButton
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import com.umbral.presentation.viewmodel.OnboardingViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onContinue: () -> Unit,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = androidx.compose.ui.platform.LocalContext.current

    // Animation states
    var showHeader by remember { mutableStateOf(false) }
    var showPermission1 by remember { mutableStateOf(false) }
    var showPermission2 by remember { mutableStateOf(false) }
    var showPermission3 by remember { mutableStateOf(false) }
    var showPermission4 by remember { mutableStateOf(false) }
    var showNfc by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Staggered animation
    LaunchedEffect(Unit) {
        delay(100)
        showHeader = true
        delay(150)
        showPermission1 = true
        delay(100)
        showPermission2 = true
        delay(100)
        showPermission3 = true
        delay(100)
        showPermission4 = true
        delay(100)
        showNfc = true
        delay(200)
        showButton = true
    }

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
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = UmbralSpacing.screenHorizontal)
        ) {
            // Header with animation
            AnimatedVisibility(
                visible = showHeader,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { -50 }
                        )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Permisos necesarios",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(UmbralSpacing.sm))

                    Text(
                        text = "Para funcionar correctamente, Umbral necesita algunos permisos del sistema",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.xl))

            // Permission cards
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
            ) {
                // Usage Stats (required)
                item {
                    AnimatedVisibility(
                        visible = showPermission1,
                        enter = fadeIn(animationSpec = tween(400)) +
                                slideInVertically(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    initialOffsetY = { 50 }
                                )
                    ) {
                        PermissionCard(
                            icon = Icons.Outlined.BarChart,
                            title = "Acceso a uso de apps",
                            description = "Necesario para detectar cuándo abres una app bloqueada",
                            status = uiState.permissionStates.usageStats,
                            isRequired = true,
                            onRequestPermission = {
                                viewModel.openPermissionSettings(RequiredPermission.USAGE_STATS)
                            }
                        )
                    }
                }

                // Overlay (required)
                item {
                    AnimatedVisibility(
                        visible = showPermission2,
                        enter = fadeIn(animationSpec = tween(400)) +
                                slideInVertically(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    initialOffsetY = { 50 }
                                )
                    ) {
                        PermissionCard(
                            icon = Icons.Outlined.Layers,
                            title = "Mostrar sobre otras apps",
                            description = "Necesario para mostrar la pantalla de bloqueo",
                            status = uiState.permissionStates.overlay,
                            isRequired = true,
                            onRequestPermission = {
                                viewModel.openPermissionSettings(RequiredPermission.OVERLAY)
                            }
                        )
                    }
                }

                // Notifications (recommended)
                item {
                    AnimatedVisibility(
                        visible = showPermission3,
                        enter = fadeIn(animationSpec = tween(400)) +
                                slideInVertically(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    initialOffsetY = { 50 }
                                )
                    ) {
                        PermissionCard(
                            icon = Icons.Outlined.Notifications,
                            title = "Notificaciones",
                            description = "Recomendado para mostrarte el estado del bloqueo",
                            status = uiState.permissionStates.notifications,
                            isRequired = false,
                            onRequestPermission = {
                                viewModel.openPermissionSettings(RequiredPermission.NOTIFICATIONS)
                            }
                        )
                    }
                }

                // Notification Listener (recommended)
                item {
                    AnimatedVisibility(
                        visible = showPermission4,
                        enter = fadeIn(animationSpec = tween(400)) +
                                slideInVertically(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    initialOffsetY = { 50 }
                                )
                    ) {
                        PermissionCard(
                            icon = Icons.Outlined.NotificationsActive,
                            title = "Acceso a notificaciones",
                            description = "Recomendado para bloquear notificaciones durante sesiones de enfoque",
                            status = uiState.permissionStates.notificationListener,
                            isRequired = false,
                            onRequestPermission = {
                                viewModel.openPermissionSettings(RequiredPermission.NOTIFICATION_LISTENER)
                            }
                        )
                    }
                }

                // NFC status
                item {
                    AnimatedVisibility(
                        visible = showNfc,
                        enter = fadeIn(animationSpec = tween(400)) +
                                slideInVertically(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    initialOffsetY = { 50 }
                                )
                    ) {
                        NfcStatusCard(
                            status = uiState.permissionStates.nfc,
                            onOpenSettings = { viewModel.openNfcSettings() }
                        )
                    }
                }
            }

            // Continue button
            val canContinue = uiState.permissionStates.usageStats == PermissionStatus.GRANTED &&
                    uiState.permissionStates.overlay == PermissionStatus.GRANTED

            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            initialOffsetY = { 100 }
                        )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!canContinue) {
                        Text(
                            text = "Los permisos marcados con * son obligatorios",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = UmbralSpacing.sm)
                        )
                    }

                    UmbralButton(
                        text = "Continuar",
                        onClick = onContinue,
                        enabled = canContinue,
                        fullWidth = true,
                        variant = ButtonVariant.Primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.xl))
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
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isGranted = status == PermissionStatus.GRANTED

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = when {
                    isGranted -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    status == PermissionStatus.DENIED || status == PermissionStatus.PERMANENTLY_DENIED ->
                        MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                },
                shape = MaterialTheme.shapes.large
            )
            .padding(UmbralSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isGranted) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isGranted) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(UmbralSpacing.md))

        // Text content
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(UmbralSpacing.xs)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isRequired) {
                    Text(
                        text = "*",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Status / Action button
        when (status) {
            PermissionStatus.GRANTED -> {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Concedido",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }
            else -> {
                TextButton(onClick = onRequestPermission) {
                    Text(
                        text = "Permitir",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun NfcStatusCard(
    status: NfcStatus,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (icon, title, description, actionText) = when (status) {
        NfcStatus.ENABLED -> listOf(
            Icons.Outlined.Nfc,
            "NFC habilitado",
            "Podrás usar tags NFC para activar el bloqueo",
            null
        )
        NfcStatus.DISABLED -> listOf(
            Icons.Outlined.Nfc,
            "NFC deshabilitado",
            "Habilita NFC para usar tags físicos. También puedes usar códigos QR.",
            "Habilitar"
        )
        NfcStatus.NOT_AVAILABLE -> listOf(
            Icons.Outlined.Nfc,
            "NFC no disponible",
            "Tu dispositivo no tiene NFC. Podrás usar códigos QR como alternativa.",
            null
        )
    }

    val isEnabled = status == NfcStatus.ENABLED

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isEnabled)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.large
            )
            .padding(UmbralSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isEnabled) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon as ImageVector,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (isEnabled) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(UmbralSpacing.md))

        // Text content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title as String,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = description as String,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Status / Action button
        if (actionText != null) {
            TextButton(onClick = onOpenSettings) {
                Text(
                    text = actionText as String,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else if (isEnabled) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Permissions Screen - Light", showBackground = true)
@Composable
private fun PermissionsScreenPreview() {
    UmbralTheme {
        // Preview requires mocked ViewModel, showing structure only
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(UmbralSpacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
        ) {
            Text(
                text = "Permisos necesarios",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )

            PermissionCard(
                icon = Icons.Outlined.BarChart,
                title = "Acceso a uso de apps",
                description = "Necesario para detectar cuándo abres una app bloqueada",
                status = PermissionStatus.GRANTED,
                isRequired = true,
                onRequestPermission = {}
            )

            PermissionCard(
                icon = Icons.Outlined.Layers,
                title = "Mostrar sobre otras apps",
                description = "Necesario para mostrar la pantalla de bloqueo",
                status = PermissionStatus.NOT_REQUESTED,
                isRequired = true,
                onRequestPermission = {}
            )

            PermissionCard(
                icon = Icons.Outlined.Notifications,
                title = "Notificaciones",
                description = "Recomendado para mostrarte el estado del bloqueo",
                status = PermissionStatus.DENIED,
                isRequired = false,
                onRequestPermission = {}
            )
        }
    }
}

@Preview(name = "Permissions Screen - Dark", showBackground = true)
@Composable
private fun PermissionsScreenDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(UmbralSpacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
        ) {
            Text(
                text = "Permisos necesarios",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            PermissionCard(
                icon = Icons.Outlined.BarChart,
                title = "Acceso a uso de apps",
                description = "Necesario para detectar cuándo abres una app bloqueada",
                status = PermissionStatus.GRANTED,
                isRequired = true,
                onRequestPermission = {}
            )

            NfcStatusCard(
                status = NfcStatus.ENABLED,
                onOpenSettings = {}
            )
        }
    }
}
