package com.umbral.expedition.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.expedition.domain.model.ActiveCompanionInfo
import com.umbral.expedition.domain.model.CompanionType
import com.umbral.expedition.domain.model.ExpeditionHomeState
import com.umbral.presentation.ui.components.UmbralCard
import com.umbral.presentation.ui.components.UmbralElevation
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Card displayed on HomeScreen showing expedition progress summary.
 * Shows level, XP progress, energy, streak, and active companion.
 */
@Composable
fun ExpeditionProgressCard(
    state: ExpeditionHomeState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!state.isInitialized || state.isLoading) {
        // Show placeholder when not initialized
        ExpeditionNotInitializedCard(
            onClick = onClick,
            modifier = modifier
        )
        return
    }

    val animatedProgress by animateFloatAsState(
        targetValue = state.levelProgress / 100f,
        label = "xpProgress"
    )

    UmbralCard(
        modifier = modifier.fillMaxWidth(),
        elevation = UmbralElevation.Medium,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header row with title and arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Gradient icon background
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Expedicion",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Nivel ${state.level}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Ver mas",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.md))

            // XP Progress bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "XP",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${state.currentXp} / ${state.xpForNextLevel}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.md))

            // Stats row: Energy and Streak
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Default.Bolt,
                    value = state.totalEnergy.toString(),
                    label = "Energia"
                )
                StatItem(
                    icon = Icons.Default.LocalFireDepartment,
                    value = state.currentStreak.toString(),
                    label = "Racha",
                    badge = if (state.streakMultiplier != "1.0x") state.streakMultiplier else null
                )
            }

            // Active companion (if any)
            state.activeCompanion?.let { companion ->
                Spacer(modifier = Modifier.height(UmbralSpacing.md))
                ActiveCompanionRow(companion = companion)
            }
        }
    }
}

@Composable
private fun StatItem(
    icon: ImageVector,
    value: String,
    label: String,
    badge: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActiveCompanionRow(
    companion: ActiveCompanionInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Companion avatar placeholder
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            // Using first letter of companion type as placeholder
            Text(
                text = companion.type.displayName.first().toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = companion.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = companion.passiveBonusDescription,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExpeditionNotInitializedCard(
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
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Expedicion",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Comienza tu aventura y gana recompensas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Comenzar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(showBackground = true)
@Composable
private fun ExpeditionProgressCardPreview() {
    UmbralTheme {
        ExpeditionProgressCard(
            state = ExpeditionHomeState(
                isLoading = false,
                isInitialized = true,
                level = 5,
                currentXp = 350,
                xpForNextLevel = 500,
                levelProgress = 70,
                totalEnergy = 1250,
                currentStreak = 7,
                streakMultiplier = "1.3x",
                activeCompanion = ActiveCompanionInfo(
                    id = "1",
                    displayName = "Espiritu de Hoja II",
                    type = CompanionType.LEAF_SPRITE,
                    evolutionState = 2,
                    passiveBonusDescription = "+5% energia de bloqueo"
                )
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpeditionProgressCardNoCompanionPreview() {
    UmbralTheme {
        ExpeditionProgressCard(
            state = ExpeditionHomeState(
                isLoading = false,
                isInitialized = true,
                level = 2,
                currentXp = 80,
                xpForNextLevel = 150,
                levelProgress = 53,
                totalEnergy = 320,
                currentStreak = 3,
                streakMultiplier = "1.0x",
                activeCompanion = null
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpeditionNotInitializedPreview() {
    UmbralTheme {
        ExpeditionProgressCard(
            state = ExpeditionHomeState.NotInitialized,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ExpeditionProgressCardDarkPreview() {
    UmbralTheme(darkTheme = true) {
        ExpeditionProgressCard(
            state = ExpeditionHomeState(
                isLoading = false,
                isInitialized = true,
                level = 10,
                currentXp = 900,
                xpForNextLevel = 1000,
                levelProgress = 90,
                totalEnergy = 5000,
                currentStreak = 14,
                streakMultiplier = "1.5x",
                activeCompanion = ActiveCompanionInfo(
                    id = "2",
                    displayName = "Zorro de Brasa III",
                    type = CompanionType.EMBER_FOX,
                    evolutionState = 3,
                    passiveBonusDescription = "+5% experiencia"
                )
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
