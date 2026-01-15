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
    onNavigateToStats: () -> Unit = {},
    onNavigateToCreateProfile: () -> Unit = {},
    onNavigateToExpedition: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    HomeScreenContent(
        uiState = uiState,
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
    onStatsClick: () -> Unit,
    onCreateProfileClick: () -> Unit,
    onExpeditionClick: () -> Unit,
    onDismissRewardDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states (simplificado)
    var showContent by remember { mutableStateOf(false) }

    // Single animation trigger
    LaunchedEffect(Unit) {
        delay(100)
        showContent = true
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

        // Main blocking status card (compacto)
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(300))
        ) {
            StatusCard(
                isActive = uiState.isBlockingEnabled,
                profileName = uiState.activeProfile?.name,
                blockedAppsCount = uiState.activeProfile?.blockedApps?.size ?: 0
            )
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.lg))

        // First profile prompt card (only when no profiles exist)
        if (!uiState.hasProfiles) {
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(animationSpec = tween(300))
            ) {
                FirstProfilePromptCard(
                    onCreateProfile = onCreateProfileClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(UmbralSpacing.lg))
        }

        // Expedition progress card
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(300))
        ) {
            ExpeditionProgressCard(
                state = uiState.expeditionState,
                onClick = onExpeditionClick,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.lg))

        // Stats preview card
        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn(animationSpec = tween(300))
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
// STATUS CARD (Compacto)
// =============================================================================

@Composable
private fun StatusCard(
    isActive: Boolean,
    profileName: String?,
    blockedAppsCount: Int,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isActive)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300),
        label = "statusBg"
    )

    val iconColor = if (isActive)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.onSurfaceVariant

    UmbralCard(
        modifier = modifier.fillMaxWidth(),
        elevation = UmbralElevation.Medium
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Compact lock icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(backgroundColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isActive) Icons.Default.Lock else Icons.Default.LockOpen,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = iconColor
                )
            }

            Spacer(modifier = Modifier.width(UmbralSpacing.md))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isActive)
                        stringResource(R.string.blocking_active)
                    else
                        stringResource(R.string.blocking_inactive),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = if (profileName != null && isActive)
                        "$profileName • $blockedAppsCount apps"
                    else
                        "Acerca un tag para activar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status indicator dot
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(
                        if (isActive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.outline
                    )
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
// FIRST PROFILE PROMPT CARD (Compacto)
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(UmbralSpacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Crea tu primer perfil",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Define qué apps bloquear",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                hasProfiles = true,
                expeditionState = ExpeditionHomeState(
                    isLoading = false,
                    isInitialized = true,
                    level = 5,
                    currentXp = 350,
                    xpForNextLevel = 500,
                    levelProgress = 70,
                    totalEnergy = 1250,
                    currentStreak = 12,
                    streakMultiplier = "1.5x"
                )
            ),
            onStatsClick = {},
            onCreateProfileClick = {},
            onExpeditionClick = {},
            onDismissRewardDialog = {}
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
            onStatsClick = {},
            onCreateProfileClick = {},
            onExpeditionClick = {},
            onDismissRewardDialog = {}
        )
    }
}

@Preview(name = "Home Screen - Loading", showBackground = true)
@Composable
private fun HomeScreenLoadingPreview() {
    UmbralTheme {
        HomeScreenContent(
            uiState = HomeUiState(isLoading = true),
            onStatsClick = {},
            onCreateProfileClick = {},
            onExpeditionClick = {},
            onDismissRewardDialog = {}
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
            onStatsClick = {},
            onCreateProfileClick = {},
            onExpeditionClick = {},
            onDismissRewardDialog = {}
        )
    }
}
