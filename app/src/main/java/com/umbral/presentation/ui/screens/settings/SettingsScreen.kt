package com.umbral.presentation.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.umbral.R
import com.umbral.domain.permission.PermissionState
import com.umbral.presentation.ui.components.UmbralCard
import com.umbral.presentation.ui.components.UmbralElevation
import com.umbral.presentation.ui.components.UmbralToggle
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import com.umbral.presentation.viewmodel.SettingsUiState
import com.umbral.presentation.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay

// =============================================================================
// SETTINGS SCREEN
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateToNfcTags: () -> Unit = {},
    onNavigateToQrCodes: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    // Animation state for staggered entrance
    var showContent by remember { mutableStateOf(false) }

    // Refresh permissions when returning from settings
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshPermissions()
        }
    }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            delay(100)
            showContent = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = UmbralSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(UmbralSpacing.sm)
            ) {
                item { Spacer(modifier = Modifier.height(UmbralSpacing.md)) }

                // NFC TAGS Section
                item {
                    AnimatedSettingsSection(
                        visible = showContent,
                        index = 0,
                        title = "TAGS NFC"
                    ) {
                        SettingsNavigationRow(
                            icon = Icons.Outlined.Nfc,
                            title = "Gestionar tags",
                            subtitle = "Ver, editar y eliminar tags NFC registrados",
                            onClick = onNavigateToNfcTags
                        )
                    }
                }

                // QR CODES Section
                item {
                    AnimatedSettingsSection(
                        visible = showContent,
                        index = 1,
                        title = "CÓDIGOS QR"
                    ) {
                        SettingsNavigationRow(
                            icon = Icons.Outlined.QrCode2,
                            title = "Gestionar códigos QR",
                            subtitle = "Alternativa a NFC para desbloquear apps",
                            onClick = onNavigateToQrCodes
                        )
                    }
                }

                // PERMISOS REQUERIDOS Section
                item {
                    AnimatedSettingsSection(
                        visible = showContent,
                        index = 2,
                        title = stringResource(R.string.required_permissions)
                    ) {
                        PermissionRow(
                            icon = Icons.Default.Timeline,
                            title = stringResource(R.string.usage_stats_permission),
                            description = stringResource(R.string.usage_stats_desc),
                            isGranted = uiState.permissionState.usageStats,
                            onRequestClick = viewModel::requestUsageStatsPermission
                        )

                        SettingsDivider()

                        PermissionRow(
                            icon = Icons.Default.Visibility,
                            title = stringResource(R.string.overlay_permission),
                            description = stringResource(R.string.overlay_desc),
                            isGranted = uiState.permissionState.overlay,
                            onRequestClick = viewModel::requestOverlayPermission
                        )
                    }
                }

                // PERMISOS OPCIONALES Section
                item {
                    AnimatedSettingsSection(
                        visible = showContent,
                        index = 3,
                        title = stringResource(R.string.optional_permissions)
                    ) {
                        PermissionRow(
                            icon = Icons.Default.Notifications,
                            title = stringResource(R.string.notification_permission),
                            description = stringResource(R.string.notification_desc),
                            isGranted = uiState.permissionState.notification,
                            onRequestClick = null
                        )

                        SettingsDivider()

                        PermissionRow(
                            icon = Icons.Default.Camera,
                            title = stringResource(R.string.camera_permission),
                            description = stringResource(R.string.camera_desc),
                            isGranted = uiState.permissionState.camera,
                            onRequestClick = null
                        )
                    }
                }

                // INFORMACIÓN Section
                item {
                    AnimatedSettingsSection(
                        visible = showContent,
                        index = 4,
                        title = stringResource(R.string.about)
                    ) {
                        SettingsInfoRow(
                            icon = Icons.Outlined.Info,
                            title = stringResource(R.string.version),
                            value = uiState.appVersion
                        )

                        SettingsDivider()

                        SettingsNavigationRow(
                            icon = Icons.Outlined.Code,
                            title = stringResource(R.string.open_source),
                            subtitle = stringResource(R.string.open_source_desc),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/javikin/umbral"))
                                context.startActivity(intent)
                            }
                        )

                        SettingsDivider()

                        SettingsInfoRow(
                            icon = Icons.Outlined.Security,
                            title = stringResource(R.string.privacy),
                            subtitle = stringResource(R.string.privacy_desc)
                        )
                    }
                }

                // Version footer
                item {
                    AnimatedVisibility(
                        visible = showContent,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                delayMillis = 5 * 50
                            )
                        )
                    ) {
                        Text(
                            text = "Umbral v${uiState.appVersion}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = UmbralSpacing.lg),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(UmbralSpacing.lg)) }
            }
        }
    }
}

// =============================================================================
// ANIMATED SETTINGS SECTION
// =============================================================================

@Composable
private fun AnimatedSettingsSection(
    visible: Boolean,
    index: Int,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = 300,
                delayMillis = index * 50
            )
        ) + slideInVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            initialOffsetY = { 50 }
        )
    ) {
        SettingsSection(
            title = title,
            content = content
        )
    }
}

// =============================================================================
// SETTINGS SECTION
// =============================================================================

@Composable
private fun SettingsSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier.padding(vertical = UmbralSpacing.xs)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(
                vertical = UmbralSpacing.sm
            )
        )

        UmbralCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = UmbralElevation.Subtle
        ) {
            Column {
                content()
            }
        }
    }
}

// =============================================================================
// SETTINGS ROWS
// =============================================================================

@Composable
private fun SettingsToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    subtitle: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(UmbralSpacing.cardPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(UmbralSpacing.md))
        }

        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            subtitle?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.width(UmbralSpacing.sm))

        UmbralToggle(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun SettingsNavigationRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    subtitle: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(UmbralSpacing.cardPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(UmbralSpacing.md))
        }

        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            subtitle?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsInfoRow(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    subtitle: String? = null,
    value: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(UmbralSpacing.cardPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(UmbralSpacing.md))
        }

        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            subtitle?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        value?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PermissionRow(
    icon: ImageVector,
    title: String,
    description: String,
    isGranted: Boolean,
    onRequestClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val successColor = Color(0xFF4CAF50)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(UmbralSpacing.cardPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (isGranted) {
                        successColor.copy(alpha = 0.15f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isGranted) successColor else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(UmbralSpacing.md))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(UmbralSpacing.sm))

        if (isGranted) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = stringResource(R.string.granted),
                tint = successColor,
                modifier = Modifier.size(24.dp)
            )
        } else if (onRequestClick != null) {
            OutlinedButton(
                onClick = onRequestClick
            ) {
                Text(
                    text = stringResource(R.string.grant_permission),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = UmbralSpacing.md),
        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Settings Screen - Light", showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    UmbralTheme {
        SettingsScreenContent(
            uiState = SettingsUiState(
                isLoading = false,
                permissionState = PermissionState(
                    usageStats = true,
                    overlay = true,
                    notification = true,
                    camera = false
                ),
                appVersion = "1.0.0"
            )
        )
    }
}

@Preview(name = "Settings Screen - Dark", showBackground = true)
@Composable
private fun SettingsScreenDarkPreview() {
    UmbralTheme(darkTheme = true) {
        SettingsScreenContent(
            uiState = SettingsUiState(
                isLoading = false,
                permissionState = PermissionState(
                    usageStats = true,
                    overlay = false,
                    notification = true,
                    camera = true
                ),
                appVersion = "1.0.0"
            )
        )
    }
}

@Preview(name = "Settings Screen - Loading", showBackground = true)
@Composable
private fun SettingsScreenLoadingPreview() {
    UmbralTheme {
        SettingsScreenContent(
            uiState = SettingsUiState(isLoading = true)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    uiState: SettingsUiState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ajustes",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = UmbralSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(UmbralSpacing.sm)
            ) {
                item { Spacer(modifier = Modifier.height(UmbralSpacing.sm)) }

                // NFC TAGS Section
                item {
                    SettingsSection(title = "TAGS NFC") {
                        SettingsNavigationRow(
                            icon = Icons.Outlined.Nfc,
                            title = "Gestionar tags",
                            subtitle = "Ver, editar y eliminar tags NFC registrados",
                            onClick = {}
                        )
                    }
                }

                // PERMISOS REQUERIDOS Section
                item {
                    SettingsSection(title = "Permisos requeridos") {
                        PermissionRow(
                            icon = Icons.Default.Timeline,
                            title = "Acceso a uso de apps",
                            description = "Necesario para detectar que app está en primer plano",
                            isGranted = uiState.permissionState.usageStats,
                            onRequestClick = {}
                        )

                        SettingsDivider()

                        PermissionRow(
                            icon = Icons.Default.Visibility,
                            title = "Mostrar sobre otras apps",
                            description = "Necesario para mostrar pantalla de bloqueo",
                            isGranted = uiState.permissionState.overlay,
                            onRequestClick = {}
                        )
                    }
                }

                // PERMISOS OPCIONALES Section
                item {
                    SettingsSection(title = "Permisos opcionales") {
                        PermissionRow(
                            icon = Icons.Default.Notifications,
                            title = "Notificaciones",
                            description = "Para mostrar el estado de bloqueo",
                            isGranted = uiState.permissionState.notification,
                            onRequestClick = null
                        )

                        SettingsDivider()

                        PermissionRow(
                            icon = Icons.Default.Camera,
                            title = "Cámara",
                            description = "Necesario para escanear códigos QR",
                            isGranted = uiState.permissionState.camera,
                            onRequestClick = null
                        )
                    }
                }

                // INFORMACIÓN Section
                item {
                    SettingsSection(title = "Acerca de") {
                        SettingsInfoRow(
                            icon = Icons.Outlined.Info,
                            title = "Versión",
                            value = uiState.appVersion
                        )

                        SettingsDivider()

                        SettingsNavigationRow(
                            icon = Icons.Outlined.Code,
                            title = "Código abierto",
                            subtitle = "Umbral es 100% open source",
                            onClick = {}
                        )

                        SettingsDivider()

                        SettingsInfoRow(
                            icon = Icons.Outlined.Security,
                            title = "Privacidad",
                            subtitle = "Tus datos nunca salen de tu dispositivo"
                        )
                    }
                }

                // Version footer
                item {
                    Text(
                        text = "Umbral v${uiState.appVersion}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = UmbralSpacing.lg),
                        textAlign = TextAlign.Center
                    )
                }

                item { Spacer(modifier = Modifier.height(UmbralSpacing.lg)) }
            }
        }
    }
}
