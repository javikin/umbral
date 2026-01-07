package com.umbral.expedition.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.umbral.expedition.domain.model.EnergyGainResult
import com.umbral.expedition.domain.model.SessionReward
import com.umbral.expedition.domain.usecase.UnlockedAchievement
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import kotlinx.coroutines.delay

/**
 * Dialog shown when a blocking session completes, displaying earned rewards.
 * Shows energy gained, XP earned, level ups, and unlocked achievements.
 */
@Composable
fun SessionRewardDialog(
    reward: SessionReward,
    onDismiss: () -> Unit
) {
    // Animation states
    var showHeader by remember { mutableStateOf(false) }
    var showEnergy by remember { mutableStateOf(false) }
    var showXp by remember { mutableStateOf(false) }
    var showLevelUp by remember { mutableStateOf(false) }
    var showAchievements by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showHeader = true
        delay(200)
        showEnergy = true
        delay(150)
        showXp = true
        if (reward.leveledUp) {
            delay(150)
            showLevelUp = true
        }
        if (reward.hasAchievements) {
            delay(200)
            showAchievements = true
        }
        delay(300)
        showButton = true
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(UmbralSpacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with celebration
                AnimatedVisibility(
                    visible = showHeader,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn()
                ) {
                    CelebrationHeader()
                }

                Spacer(modifier = Modifier.height(UmbralSpacing.lg))

                // Energy gained
                AnimatedVisibility(
                    visible = showEnergy,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ) + fadeIn()
                ) {
                    RewardRow(
                        icon = Icons.Default.Bolt,
                        value = "+${reward.energyResult.totalEnergy}",
                        label = "Energia",
                        badge = if (reward.energyResult.multiplier > 1f) {
                            "${String.format("%.1f", reward.energyResult.multiplier)}x"
                        } else null,
                        badgeDescription = if (reward.energyResult.multiplier > 1f) {
                            "Bonus por racha"
                        } else null
                    )
                }

                Spacer(modifier = Modifier.height(UmbralSpacing.md))

                // XP gained
                AnimatedVisibility(
                    visible = showXp,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ) + fadeIn()
                ) {
                    RewardRow(
                        icon = Icons.Default.Star,
                        value = "+${reward.energyResult.xpGained}",
                        label = "XP"
                    )
                }

                // Level up notification
                if (reward.leveledUp) {
                    Spacer(modifier = Modifier.height(UmbralSpacing.md))
                    AnimatedVisibility(
                        visible = showLevelUp,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        ) + fadeIn()
                    ) {
                        LevelUpBanner(newLevel = reward.energyResult.newLevel!!)
                    }
                }

                // Achievements unlocked
                if (reward.hasAchievements) {
                    Spacer(modifier = Modifier.height(UmbralSpacing.lg))
                    AnimatedVisibility(
                        visible = showAchievements,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        ) + fadeIn()
                    ) {
                        AchievementsSection(achievements = reward.unlockedAchievements)
                    }
                }

                Spacer(modifier = Modifier.height(UmbralSpacing.xl))

                // Dismiss button
                AnimatedVisibility(
                    visible = showButton,
                    enter = fadeIn()
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Genial!")
                    }
                }
            }
        }
    }
}

@Composable
private fun CelebrationHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Celebration icon with gradient background
        Box(
            modifier = Modifier
                .size(80.dp)
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
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.md))

        Text(
            text = "Sesion completada!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Has ganado estas recompensas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun RewardRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    badge: String? = null,
    badgeDescription: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(UmbralSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(UmbralSpacing.md))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.tertiary,
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = if (badgeDescription != null) "$label ($badgeDescription)" else label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LevelUpBanner(newLevel: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.tertiary
                    )
                )
            )
            .padding(UmbralSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowUpward,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Subiste a nivel $newLevel!",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
private fun AchievementsSection(
    achievements: List<UnlockedAchievement>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Logros desbloqueados",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(UmbralSpacing.sm))

        achievements.forEach { achievement ->
            AchievementItem(achievement = achievement)
            Spacer(modifier = Modifier.height(UmbralSpacing.xs))
        }
    }
}

@Composable
private fun AchievementItem(
    achievement: UnlockedAchievement
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
            .padding(UmbralSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
        }
        // Stars earned
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "+${achievement.starsEarned}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(showBackground = true)
@Composable
private fun SessionRewardDialogPreview() {
    UmbralTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SessionRewardDialog(
                reward = SessionReward(
                    energyResult = EnergyGainResult(
                        baseEnergy = 30,
                        multiplier = 1.3f,
                        totalEnergy = 39,
                        xpGained = 4,
                        newLevel = null,
                        newStreak = 7
                    ),
                    unlockedAchievements = emptyList()
                ),
                onDismiss = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionRewardDialogWithLevelUpPreview() {
    UmbralTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SessionRewardDialog(
                reward = SessionReward(
                    energyResult = EnergyGainResult(
                        baseEnergy = 60,
                        multiplier = 1.5f,
                        totalEnergy = 90,
                        xpGained = 9,
                        newLevel = 5,
                        newStreak = 14
                    ),
                    unlockedAchievements = listOf(
                        UnlockedAchievement(
                            id = "golden_hour",
                            title = "Hora Dorada",
                            description = "Completa una sesion de 60 minutos",
                            starsEarned = 2
                        )
                    )
                ),
                onDismiss = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionRewardDialogFullPreview() {
    UmbralTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SessionRewardDialog(
                reward = SessionReward(
                    energyResult = EnergyGainResult(
                        baseEnergy = 120,
                        multiplier = 2.0f,
                        totalEnergy = 240,
                        xpGained = 24,
                        newLevel = 10,
                        newStreak = 30
                    ),
                    unlockedAchievements = listOf(
                        UnlockedAchievement(
                            id = "marathoner",
                            title = "Maratonista",
                            description = "Completa una sesion de 2 horas",
                            starsEarned = 3
                        ),
                        UnlockedAchievement(
                            id = "master_30",
                            title = "Maestro",
                            description = "Mantiene racha de 30 dias",
                            starsEarned = 5
                        )
                    )
                ),
                onDismiss = {}
            )
        }
    }
}
