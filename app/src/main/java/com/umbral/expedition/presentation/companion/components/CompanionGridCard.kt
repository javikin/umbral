package com.umbral.expedition.presentation.companion.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.umbral.R
import com.umbral.expedition.domain.model.CompanionType
import com.umbral.expedition.domain.model.Element
import com.umbral.expedition.presentation.companion.CompanionState

/**
 * Card component for displaying a companion in a grid layout.
 *
 * Shows different visual states:
 * - Locked: Grayed out with lock icon
 * - Available: Orange glow, capture button
 * - Captured: Full color, evolution progress
 */
@Composable
fun CompanionGridCard(
    state: CompanionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val companionType = state.companionType

    Card(
        modifier = modifier
            .aspectRatio(0.75f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (state is CompanionState.Available) 8.dp else 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = when (state) {
                        is CompanionState.Locked -> Brush.verticalGradient(
                            colors = listOf(
                                Color.Gray.copy(alpha = 0.3f),
                                Color.DarkGray.copy(alpha = 0.5f)
                            )
                        )
                        is CompanionState.Available -> {
                            // Animated glow effect
                            val infiniteTransition = rememberInfiniteTransition(label = "glow")
                            val alpha by infiniteTransition.animateFloat(
                                initialValue = 0.3f,
                                targetValue = 0.7f,
                                animationSpec = infiniteRepeatable(
                                    animation = tween(1000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                ),
                                label = "glowAlpha"
                            )
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFF9800).copy(alpha = alpha),
                                    Color(0xFFFFB74D).copy(alpha = alpha * 0.5f)
                                )
                            )
                        }
                        is CompanionState.Captured -> {
                            val elementColor = parseElementColor(companionType.element)
                            Brush.verticalGradient(
                                colors = listOf(
                                    elementColor.copy(alpha = 0.2f),
                                    elementColor.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                )
                .then(
                    if (state is CompanionState.Available) {
                        Modifier.border(
                            width = 2.dp,
                            color = Color(0xFFFF9800),
                            shape = RoundedCornerShape(16.dp)
                        )
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Section: Icon or Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            when (state) {
                                is CompanionState.Locked -> Color.Gray
                                is CompanionState.Available -> Color(0xFFFF9800)
                                is CompanionState.Captured -> parseElementColor(companionType.element)
                            }
                        )
                        .alpha(
                            when (state) {
                                is CompanionState.Locked -> 0.4f
                                else -> 1.0f
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when (state) {
                        is CompanionState.Locked -> {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = stringResource(R.string.companion_locked),
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        is CompanionState.Available -> {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = stringResource(R.string.companion_available),
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        is CompanionState.Captured -> {
                            // Evolution state indicator
                            val companion = state.companion
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(companion.evolutionState) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Middle Section: Name and Element
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = companionType.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = when (state) {
                            is CompanionState.Locked -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            else -> MaterialTheme.colorScheme.onSurface
                        }
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = companionType.element.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = when (state) {
                            is CompanionState.Locked -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        }
                    )
                }

                // Bottom Section: Status/Progress
                when (state) {
                    is CompanionState.Locked -> {
                        Text(
                            text = stringResource(R.string.companion_locked),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }
                    is CompanionState.Available -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = Color(0xFFFF9800),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.companion_capture_button),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    is CompanionState.Captured -> {
                        val companion = state.companion
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (!companion.isMaxEvolution) {
                                LinearProgressIndicator(
                                    progress = { companion.evolutionProgress / 100f },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = parseElementColor(companionType.element),
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${companion.evolutionProgress}%",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.companion_evolution_max),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF4CAF50),
                                        modifier = Modifier.padding(start = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Active indicator badge
            if (state is CompanionState.Captured && state.companion.isActive) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = stringResource(R.string.companion_active),
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Parse element color from hex string
 */
private fun parseElementColor(element: Element): Color {
    return try {
        Color(android.graphics.Color.parseColor(element.color))
    } catch (e: Exception) {
        Color.Gray
    }
}
