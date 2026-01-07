package com.umbral.presentation.ui.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.umbral.R
import com.umbral.expedition.domain.model.ExpeditionHomeState
import com.umbral.expedition.domain.model.SessionReward
import com.umbral.expedition.presentation.components.ExpeditionProgressCard
import com.umbral.expedition.presentation.components.SessionRewardDialog
import com.umbral.presentation.ui.components.LoadingIndicator
import com.umbral.presentation.ui.components.StatsGraph
import com.umbral.presentation.ui.components.StreakDisplay
import com.umbral.presentation.ui.components.UmbralCard
import com.umbral.presentation.ui.components.UmbralElevation
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import com.umbral.presentation.viewmodel.HomeUiState
import com.umbral.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate

@Composable
fun HomeScreen(
    onNavigateToNfcScan: () -> Unit = {},
    onNavigateToQrScan: () -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    onNavigateToCreateProfile: () -> Unit = {},
    onNavigateToExpedition: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        onToggleBlocking = viewModel::toggleBlocking,
        onNfcScanClick = onNavigateToNfcScan,
        onQrScanClick = onNavigateToQrScan,
        onStatsClick = onNavigateToStats,
        onCreateProfileClick = onNavigateToCreateProfile,
        onExpeditionClick = onNavigateToExpedition,
        onDismissRewardDialog = viewModel::dismissRewardDialog,
        modifier = modifier
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onToggleBlocking: () -> Unit,
    onNfcScanClick: () -> Unit,
    onQrScanClick: () -> Unit,
    onStatsClick: () -> Unit,
    onCreateProfileClick: () -> Unit,
    onExpeditionClick: () -> Unit,
    onDismissRewardDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states
    var showStatusCard by remember { mutableStateOf(false) }
    var showFirstProfileCard by remember { mutableStateOf(false) }
    var showExpeditionCard by remember { mutableStateOf(false) }
    var showStreakCard by remember { mutableStateOf(false) }
    var showQuickActions by remember { mutableStateOf(false) }
    var showStatsPreview by remember { mutableStateOf(false) }

    // Staggered animation
    LaunchedEffect(Unit) {
        delay(100)
        showStatusCard = true
        delay(150)
        if (!uiState.hasProfiles) {
            showFirstProfileCard = true
            delay(150)
        }
        showExpeditionCard = true
        delay(150)
        showStreakCard = true
        delay(150)
        showQuickActions = true
        delay(200)
        showStatsPreview = true
    }

    // Show reward dialog when available
    uiState.showRewardDialog?.let { reward ->
        SessionRewardDialog(
            reward = reward,
            onDismiss = onDismissRewardDialog
        )
    }

    if (uiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LoadingIndicator()
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = UmbralSpacing.screenHorizontal)
            .padding(vertical = UmbralSpacing.screenVertical),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(UmbralSpacing.lg))

        // Main blocking status card with gradient
        AnimatedVisibility(
            visible = showStatusCard,
            enter = fadeIn(animationSpec = tween(400)) +
                    slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialOffsetY = { -50 }
                    )
        ) {
            StatusCard(
                isActive = uiState.isBlockingEnabled,
                profileName = uiState.activeProfile?.name,
                blockedAppsCount = uiState.activeProfile?.blockedApps?.size ?: 0,
                onToggle = onToggleBlocking
            )
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.lg))

        // First profile prompt card (only when no profiles exist)
        AnimatedVisibility(
            visible = showFirstProfileCard && !uiState.hasProfiles,
            enter = fadeIn(animationSpec = tween(400)) +
                    slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialOffsetY = { 50 }
                    )
        ) {
            FirstProfilePromptCard(
                onCreateProfile = onCreateProfileClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (!uiState.hasProfiles) {
            Spacer(modifier = Modifier.height(UmbralSpacing.lg))
        }

        // Streak card with mini calendar
        AnimatedVisibility(
            visible = showStreakCard,
            enter = fadeIn(animationSpec = tween(400)) +
                    slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialOffsetY = { 50 }
                    )
        ) {
            StreakCard(
                currentStreak = uiState.currentStreak,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.lg))

        // Quick actions for NFC/QR
        AnimatedVisibility(
            visible = showQuickActions,
            enter = fadeIn(animationSpec = tween(400)) +
                    slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        initialOffsetY = { 50 }
                    )
        ) {
            QuickActionsRow(
                onNfcClick = onNfcScanClick,
                onQrClick = onQrScanClick
            )
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.lg))

        // Stats preview card
        AnimatedVisibility(
            visible = showStatsPreview,
            enter = fadeIn(animationSpec = tween(400)) +
                    slideInVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMedium
                        ),
                        initialOffsetY = { 100 }
                    )
        ) {
            StatsPreviewCard(
                weeklyData = uiState.weeklyStats,
                hasData = uiState.hasStatsData,
                onClick = onStatsClick
            )
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.xl))
    }
}

// =============================================================================
// STATUS CARD
// =============================================================================

@Composable
private fun StatusCard(
    isActive: Boolean,
    profileName: String?,
    blockedAppsCount: Int,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1f else 0.98f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "statusScale"
    )

    val gradientColors = if (isActive) {
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary
        )
    } else {
        listOf(
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.surfaceVariant
        )
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(brush = Brush.linearGradient(gradientColors))
            .clickable(onClick = onToggle)
            .padding(UmbralSpacing.xl)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val contentColor = if (isActive) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            // Animated lock icon
            AnimatedLockIcon(
                isLocked = isActive,
                tint = contentColor
            )

            Spacer(modifier = Modifier.height(UmbralSpacing.md))

            Text(
                text = if (isActive)
                    stringResource(R.string.blocking_active)
                else
                    stringResource(R.string.blocking_inactive),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )

            if (profileName != null && isActive) {
                Spacer(modifier = Modifier.height(UmbralSpacing.xs))
                Text(
                    text = "Perfil: $profileName",
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(UmbralSpacing.xs))
                Text(
                    text = "$blockedAppsCount apps bloqueadas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.md))

            Text(
                text = if (isActive) "Toca para desactivar" else "Toca para activar",
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun AnimatedLockIcon(
    isLocked: Boolean,
    tint: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }

    LaunchedEffect(isLocked) {
        // Bounce and rotate animation
        scale.animateTo(
            targetValue = 0.8f,
            animationSpec = tween(100)
        )
        rotation.animateTo(
            targetValue = if (isLocked) 0f else 15f,
            animationSpec = tween(150, easing = LinearEasing)
        )
        scale.animateTo(
            targetValue = 1.1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
        )
    }

    Box(
        modifier = modifier
            .size(88.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen,
            contentDescription = null,
            modifier = Modifier
                .size(44.dp)
                .scale(scale.value)
                .rotate(rotation.value),
            tint = tint
        )
    }
}

// =============================================================================
// STREAK CARD
// =============================================================================

@Composable
private fun StreakCard(
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier,
        elevation = UmbralElevation.Subtle
    ) {
        StreakDisplay(
            currentStreak = currentStreak,
            showMiniCalendar = true,
            completedDays = (0 until minOf(currentStreak, 7)).map {
                LocalDate.now().minusDays(it.toLong())
            }.toSet(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// =============================================================================
// QUICK ACTIONS
// =============================================================================

@Composable
private fun QuickActionsRow(
    onNfcClick: () -> Unit,
    onQrClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
    ) {
        QuickActionCard(
            icon = Icons.Outlined.Nfc,
            label = "Escanear NFC",
            onClick = onNfcClick,
            modifier = Modifier.weight(1f)
        )
        QuickActionCard(
            icon = Icons.Outlined.QrCodeScanner,
            label = "Escanear QR",
            onClick = onQrClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier,
        elevation = UmbralElevation.Subtle,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.sm))

            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// =============================================================================
// STATS PREVIEW
// =============================================================================

@Composable
private fun StatsPreviewCard(
    weeklyData: List<Float>,
    hasData: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier.fillMaxWidth(),
        elevation = UmbralElevation.Subtle,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Estadísticas",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver más",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.md))

        if (hasData) {
            // Show graph with real data
            StatsGraph(
                data = weeklyData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                labels = listOf("L", "M", "X", "J", "V", "S", "D"),
                showLabels = true
            )

            Spacer(modifier = Modifier.height(UmbralSpacing.sm))

            Text(
                text = "Últimos 7 días",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // Show empty state
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Sin datos todavía",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(UmbralSpacing.xs))
                Text(
                    text = "Activa un perfil para comenzar a trackear",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// =============================================================================
// FIRST PROFILE PROMPT CARD
// =============================================================================

@Composable
private fun FirstProfilePromptCard(
    onCreateProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier,
        elevation = UmbralElevation.Medium,
        onClick = onCreateProfile
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.md))

            Text(
                text = "Crea tu primer perfil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(UmbralSpacing.sm))

            Text(
                text = "Define qué apps bloquear y cómo desbloquearlas con NFC o timer",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(UmbralSpacing.lg))

            Button(
                onClick = onCreateProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear perfil")
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Home Screen - Active", showBackground = true)
@Composable
private fun HomeScreenActivePreview() {
    UmbralTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                isLoading = false,
                isBlockingEnabled = true,
                activeProfile = null,
                currentStreak = 12,
                hasProfiles = true
            ),
            onToggleBlocking = {},
            onNfcScanClick = {},
            onQrScanClick = {},
            onStatsClick = {},
            onCreateProfileClick = {}
        )
    }
}

@Preview(name = "Home Screen - Inactive", showBackground = true)
@Composable
private fun HomeScreenInactivePreview() {
    UmbralTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                isLoading = false,
                isBlockingEnabled = false,
                activeProfile = null,
                currentStreak = 5,
                hasProfiles = true
            ),
            onToggleBlocking = {},
            onNfcScanClick = {},
            onQrScanClick = {},
            onStatsClick = {},
            onCreateProfileClick = {}
        )
    }
}

@Preview(name = "Home Screen - Dark Theme", showBackground = true)
@Composable
private fun HomeScreenDarkPreview() {
    UmbralTheme(darkTheme = true) {
        HomeScreenContent(
            uiState = HomeUiState(
                isLoading = false,
                isBlockingEnabled = true,
                activeProfile = null,
                currentStreak = 7,
                hasProfiles = true
            ),
            onToggleBlocking = {},
            onNfcScanClick = {},
            onQrScanClick = {},
            onStatsClick = {},
            onCreateProfileClick = {}
        )
    }
}

@Preview(name = "Home Screen - Loading", showBackground = true)
@Composable
private fun HomeScreenLoadingPreview() {
    UmbralTheme {
        HomeScreenContent(
            uiState = HomeUiState(isLoading = true),
            onToggleBlocking = {},
            onNfcScanClick = {},
            onQrScanClick = {},
            onStatsClick = {},
            onCreateProfileClick = {}
        )
    }
}

@Preview(name = "Status Card - Active", showBackground = true)
@Composable
private fun StatusCardActivePreview() {
    UmbralTheme {
        StatusCard(
            isActive = true,
            profileName = "Productividad",
            blockedAppsCount = 15,
            onToggle = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Status Card - Inactive", showBackground = true)
@Composable
private fun StatusCardInactivePreview() {
    UmbralTheme {
        StatusCard(
            isActive = false,
            profileName = null,
            blockedAppsCount = 0,
            onToggle = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Quick Actions", showBackground = true)
@Composable
private fun QuickActionsPreview() {
    UmbralTheme {
        QuickActionsRow(
            onNfcClick = {},
            onQrClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "First Profile Prompt Card", showBackground = true)
@Composable
private fun FirstProfilePromptCardPreview() {
    UmbralTheme {
        FirstProfilePromptCard(
            onCreateProfile = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Home Screen - No Profiles", showBackground = true)
@Composable
private fun HomeScreenNoProfilesPreview() {
    UmbralTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                isLoading = false,
                isBlockingEnabled = false,
                activeProfile = null,
                currentStreak = 0,
                hasProfiles = false
            ),
            onToggleBlocking = {},
            onNfcScanClick = {},
            onQrScanClick = {},
            onStatsClick = {},
            onCreateProfileClick = {}
        )
    }
}
