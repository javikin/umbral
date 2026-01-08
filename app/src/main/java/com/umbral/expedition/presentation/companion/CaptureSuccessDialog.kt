package com.umbral.expedition.presentation.companion

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.umbral.expedition.domain.model.Companion
import com.umbral.expedition.presentation.animation.CompanionAnimation
import com.umbral.expedition.presentation.animation.CompanionAnimationState

/**
 * Dialog shown when a companion is successfully captured.
 *
 * Features:
 * - Celebration animation with Lottie
 * - Shows companion name, element, and passive bonus
 * - Single dismiss button
 *
 * @param companion The newly captured companion
 * @param elementColor The color theme based on companion's element
 * @param onDismiss Called when user dismisses the dialog
 */
@Composable
fun CaptureSuccessDialog(
    companion: Companion,
    elementColor: Color,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
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
                // Title
                Text(
                    text = "¡Captura Exitosa!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = elementColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Celebration animation
                CompanionAnimation(
                    companionType = companion.type,
                    animationState = CompanionAnimationState.HAPPY,
                    backgroundColor = elementColor,
                    height = 200.dp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Companion name
                Text(
                    text = companion.displayName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Element badge
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .then(
                                Modifier.padding(0.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.foundation.Canvas(
                            modifier = Modifier.size(16.dp)
                        ) {
                            drawCircle(color = elementColor)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = companion.element.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Passive bonus info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = elementColor.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Bonificación Pasiva",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = elementColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = companion.passiveBonus.getDescription(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Dismiss button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = elementColor
                    )
                ) {
                    Text(
                        text = "¡Genial!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
