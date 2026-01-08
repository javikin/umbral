package com.umbral.expedition.presentation.companion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.umbral.expedition.domain.model.Companion
import com.umbral.expedition.domain.model.ExpeditionFormulas

/**
 * Component showing evolution progress for a companion.
 *
 * Displays:
 * - Current evolution state (I, II, III)
 * - Energy invested vs required
 * - Progress bar
 * - Next evolution threshold
 */
@Composable
fun EvolutionProgress(
    companion: Companion,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Progreso de Evolución",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Evolution state indicators
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EvolutionStateIndicator(
                state = 1,
                currentState = companion.evolutionState,
                label = "I",
                accentColor = accentColor
            )

            EvolutionConnector(
                isActive = companion.evolutionState >= 2,
                accentColor = accentColor
            )

            EvolutionStateIndicator(
                state = 2,
                currentState = companion.evolutionState,
                label = "II",
                accentColor = accentColor
            )

            EvolutionConnector(
                isActive = companion.evolutionState >= 3,
                accentColor = accentColor
            )

            EvolutionStateIndicator(
                state = 3,
                currentState = companion.evolutionState,
                label = "III",
                accentColor = accentColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress info
        if (!companion.isMaxEvolution) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Energía Invertida",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )

                    Text(
                        text = "${companion.energyInvested} / ${companion.evolutionCost}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { companion.evolutionProgress / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = accentColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${companion.evolutionProgress}% completado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    if (companion.energyUntilNextEvolution > 0) {
                        Text(
                            text = "${companion.energyUntilNextEvolution} restante",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Evolución Máxima Alcanzada",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFD700)
                )
            }
        }

        // Energy thresholds info
        if (!companion.isMaxEvolution) {
            Spacer(modifier = Modifier.height(12.dp))

            Column {
                Text(
                    text = "Umbrales de Evolución",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "• Estado II: ${ExpeditionFormulas.EVOLUTION_STATE_2_MIN} energía",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (companion.evolutionState >= 2) {
                        Color(0xFF4CAF50)
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    }
                )
                Text(
                    text = "• Estado III: ${ExpeditionFormulas.EVOLUTION_STATE_3_MIN} energía",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (companion.evolutionState >= 3) {
                        Color(0xFF4CAF50)
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    }
                )
            }
        }
    }
}

/**
 * Individual evolution state indicator circle
 */
@Composable
private fun EvolutionStateIndicator(
    state: Int,
    currentState: Int,
    label: String,
    accentColor: Color
) {
    val isActive = currentState >= state
    val isCurrent = currentState == state

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (isActive) accentColor else Color.Gray.copy(alpha = 0.3f)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isActive) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isCurrent) {
                accentColor
            } else if (isActive) {
                Color(0xFF4CAF50)
            } else {
                Color.Gray
            },
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Connector line between evolution states
 */
@Composable
private fun EvolutionConnector(
    isActive: Boolean,
    accentColor: Color
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .width(40.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(
                if (isActive) accentColor else Color.Gray.copy(alpha = 0.3f)
            )
    )
}
