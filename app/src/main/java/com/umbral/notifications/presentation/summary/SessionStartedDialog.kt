package com.umbral.notifications.presentation.summary

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.umbral.domain.blocking.SessionStartedEvent
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import kotlinx.coroutines.delay

/**
 * Dialog shown when a blocking session starts.
 * Displays profile characteristics and important information about the session,
 * especially highlighting NFC requirement for strict mode.
 *
 * @param event Session started event with profile details
 * @param onDismiss Callback when dialog is dismissed
 * @param modifier Optional modifier for the dialog
 */
@Composable
fun SessionStartedDialog(
    event: SessionStartedEvent,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states for smooth entrance
    var showHeader by remember { mutableStateOf(false) }
    var showFeatures by remember { mutableStateOf(false) }
    var showStrictMode by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showHeader = true
        delay(200)
        showFeatures = true
        delay(150)
        showStrictMode = true
        delay(200)
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
            modifier = modifier.fillMaxWidth(),
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
                // Header with play icon
                AnimatedVisibility(
                    visible = showHeader,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn()
                ) {
                    HeaderSection(profileName = event.profileName)
                }

                Spacer(modifier = Modifier.height(UmbralSpacing.lg))

                // Session features
                AnimatedVisibility(
                    visible = showFeatures,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ) + fadeIn()
                ) {
                    FeaturesSection(
                        blockedAppsCount = event.blockedAppsCount,
                        blockNotifications = event.blockNotifications
                    )
                }

                // Strict mode warning (highlighted)
                if (event.isStrictMode) {
                    Spacer(modifier = Modifier.height(UmbralSpacing.lg))
                    AnimatedVisibility(
                        visible = showStrictMode,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        ) + fadeIn()
                    ) {
                        StrictModeWarning()
                    }
                }

                Spacer(modifier = Modifier.height(UmbralSpacing.xl))

                // OK button
                AnimatedVisibility(
                    visible = showButton,
                    enter = fadeIn()
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Entendido")
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(profileName: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Play icon in circle
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(UmbralSpacing.iconSizeLarge),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.md))

        // Title
        Text(
            text = "¡Sesión iniciada!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(UmbralSpacing.xs))

        // Profile name
        Text(
            text = "Perfil: $profileName",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FeaturesSection(
    blockedAppsCount: Int,
    blockNotifications: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(UmbralSpacing.md),
            verticalArrangement = Arrangement.spacedBy(UmbralSpacing.sm)
        ) {
            // Blocked apps count
            FeatureRow(
                icon = Icons.Default.Apps,
                text = "$blockedAppsCount apps bloqueadas",
                isEnabled = true
            )

            // Notifications status
            FeatureRow(
                icon = if (blockNotifications) Icons.Default.NotificationsOff else Icons.Default.Notifications,
                text = if (blockNotifications) "Notificaciones bloqueadas" else "Notificaciones permitidas",
                isEnabled = blockNotifications
            )
        }
    }
}

@Composable
private fun FeatureRow(
    icon: ImageVector,
    text: String,
    isEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = if (isEnabled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        Spacer(modifier = Modifier.width(UmbralSpacing.sm))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun StrictModeWarning() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(UmbralSpacing.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // NFC icon in circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Nfc,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.width(UmbralSpacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Modo estricto activo",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Necesitarás el tag NFC para desactivar el bloqueo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(showBackground = true)
@Composable
private fun SessionStartedDialogPreview() {
    UmbralTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SessionStartedDialog(
                event = SessionStartedEvent(
                    sessionId = "session_123",
                    profileId = "profile_1",
                    profileName = "Trabajo",
                    blockedAppsCount = 5,
                    isStrictMode = false,
                    blockNotifications = true
                ),
                onDismiss = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionStartedDialogStrictModePreview() {
    UmbralTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SessionStartedDialog(
                event = SessionStartedEvent(
                    sessionId = "session_456",
                    profileId = "profile_2",
                    profileName = "Estudio intensivo",
                    blockedAppsCount = 12,
                    isStrictMode = true,
                    blockNotifications = true
                ),
                onDismiss = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionStartedDialogNotificationsAllowedPreview() {
    UmbralTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SessionStartedDialog(
                event = SessionStartedEvent(
                    sessionId = "session_789",
                    profileId = "profile_3",
                    profileName = "Relajado",
                    blockedAppsCount = 3,
                    isStrictMode = false,
                    blockNotifications = false
                ),
                onDismiss = {}
            )
        }
    }
}
