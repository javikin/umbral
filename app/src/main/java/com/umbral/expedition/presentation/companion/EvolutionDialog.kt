package com.umbral.expedition.presentation.companion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.umbral.expedition.domain.model.Companion
import com.umbral.expedition.presentation.animation.CompanionAnimation
import com.umbral.expedition.presentation.animation.CompanionAnimationState
import kotlinx.coroutines.delay

/**
 * Dialog that shows evolution confirmation and animation.
 *
 * Flow:
 * 1. Confirmation view: Shows current state -> next state with confirm/cancel buttons
 * 2. Animation view: 3-second evolution animation
 * 3. Auto-dismisses after animation completes
 *
 * @param companion The companion to evolve
 * @param elementColor The color theme based on companion's element
 * @param onConfirm Called when user confirms evolution
 * @param onDismiss Called when user cancels or animation completes
 */
@Composable
fun EvolutionDialog(
    companion: Companion,
    elementColor: Color,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var isAnimating by remember { mutableStateOf(false) }
    var animationComplete by remember { mutableStateOf(false) }

    // Handle animation sequence
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            // Wait 3 seconds for animation
            delay(3000)
            animationComplete = true
            // Auto-dismiss after brief pause
            delay(500)
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = {
            if (!isAnimating) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !isAnimating,
            dismissOnClickOutside = !isAnimating
        )
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isAnimating) {
                    // Confirmation view
                    ConfirmationView(
                        companion = companion,
                        elementColor = elementColor,
                        onConfirm = {
                            onConfirm()
                            isAnimating = true
                        },
                        onCancel = onDismiss
                    )
                } else {
                    // Animation view
                    AnimationView(
                        companion = companion,
                        elementColor = elementColor,
                        animationComplete = animationComplete
                    )
                }
            }
        }
    }
}

/**
 * Confirmation view showing evolution transition
 */
@Composable
private fun ConfirmationView(
    companion: Companion,
    elementColor: Color,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Text(
        text = "¿Evolucionar?",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Current state -> Next state
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Current evolution state
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Estado Actual",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            EvolutionStateIndicator(
                state = companion.evolutionState,
                color = elementColor.copy(alpha = 0.5f)
            )
        }

        // Arrow
        Text(
            text = "→",
            style = MaterialTheme.typography.headlineLarge,
            color = elementColor
        )

        // Next evolution state
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nuevo Estado",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            EvolutionStateIndicator(
                state = companion.evolutionState + 1,
                color = elementColor
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "${companion.displayName} se volverá más poderoso",
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Buttons
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.weight(1f)
        ) {
            Text("Cancelar")
        }

        Button(
            onClick = onConfirm,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = elementColor
            )
        ) {
            Text("Evolucionar")
        }
    }
}

/**
 * Animation view showing evolution in progress
 */
@Composable
private fun AnimationView(
    companion: Companion,
    elementColor: Color,
    animationComplete: Boolean
) {
    Text(
        text = if (animationComplete) "¡Evolución Completa!" else "Evolucionando...",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        color = elementColor
    )

    Spacer(modifier = Modifier.height(24.dp))

    // Show evolution animation
    CompanionAnimation(
        companionType = companion.type,
        animationState = CompanionAnimationState.EVOLVING,
        backgroundColor = elementColor,
        height = 200.dp
    )

    Spacer(modifier = Modifier.height(16.dp))

    AnimatedVisibility(visible = animationComplete) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${companion.displayName}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            EvolutionStateIndicator(
                state = companion.evolutionState + 1,
                color = elementColor
            )
        }
    }
}

/**
 * Shows evolution state as stars
 */
@Composable
private fun EvolutionStateIndicator(
    state: Int,
    color: Color
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(state.coerceIn(1, 3)) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = color,
                modifier = Modifier.width(32.dp)
            )
        }
    }
}
