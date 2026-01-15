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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.umbral.notifications.domain.model.NotificationSummary
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.minutes

/**
 * Dialog shown automatically when a blocking session ends.
 * Displays a summary of blocked notifications, top apps, and bonus energy earned.
 *
 * @param summary Notification summary for the completed session
 * @param bonusEnergy Bonus energy earned (+1 per 5 notifications)
 * @param onViewAll Callback when "Ver todas" button is clicked
 * @param onDismiss Callback when dialog is dismissed
 * @param modifier Optional modifier for the dialog
 */
@Composable
fun SessionSummaryDialog(
    summary: NotificationSummary,
    bonusEnergy: Int,
    onViewAll: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation states for smooth entrance
    var showHeader by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var showApps by remember { mutableStateOf(false) }
    var showEnergy by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showHeader = true
        delay(200)
        showStats = true
        delay(150)
        showApps = true
        delay(150)
        showEnergy = true
        delay(200)
        showButtons = true
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
                // Header with shield icon
                AnimatedVisibility(
                    visible = showHeader,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn()
                ) {
                    HeaderSection()
                }

                Spacer(modifier = Modifier.height(UmbralSpacing.lg))

                // Total notifications blocked
                AnimatedVisibility(
                    visible = showStats,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ) + fadeIn()
                ) {
                    Text(
                        text = "Evitaste ${summary.totalCount} distracciones",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(UmbralSpacing.lg))

                // Top 5 apps breakdown
                AnimatedVisibility(
                    visible = showApps,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy
                        )
                    ) + fadeIn()
                ) {
                    AppsBreakdownSection(
                        apps = summary.byApp.take(5)
                    )
                }

                // Energy bonus chip
                if (bonusEnergy > 0) {
                    Spacer(modifier = Modifier.height(UmbralSpacing.lg))
                    AnimatedVisibility(
                        visible = showEnergy,
                        enter = scaleIn(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        ) + fadeIn()
                    ) {
                        EnergyBonusChip(energy = bonusEnergy)
                    }
                }

                Spacer(modifier = Modifier.height(UmbralSpacing.xl))

                // Action buttons
                AnimatedVisibility(
                    visible = showButtons,
                    enter = fadeIn()
                ) {
                    ActionButtons(
                        onViewAll = onViewAll,
                        onDismiss = onDismiss
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Shield icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = null,
                modifier = Modifier.size(UmbralSpacing.iconSizeLarge),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.md))

        // Title
        Text(
            text = "¡Sesión completada!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AppsBreakdownSection(
    apps: List<NotificationSummary.AppCount>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(UmbralSpacing.md)
        ) {
            apps.forEach { app ->
                AppNotificationRow(app = app)
                if (app != apps.last()) {
                    Spacer(modifier = Modifier.height(UmbralSpacing.sm))
                }
            }
        }
    }
}

@Composable
private fun AppNotificationRow(
    app: NotificationSummary.AppCount
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = app.appName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = app.count.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun EnergyBonusChip(energy: Int) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = UmbralSpacing.md,
                vertical = UmbralSpacing.sm
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(UmbralSpacing.sm))
            Text(
                text = "+$energy energía bonus",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun ActionButtons(
    onViewAll: () -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(UmbralSpacing.buttonSpacing)
    ) {
        OutlinedButton(
            onClick = onViewAll,
            modifier = Modifier.weight(1f)
        ) {
            Text("Ver todas")
        }
        Button(
            onClick = onDismiss,
            modifier = Modifier.weight(1f)
        ) {
            Text("Descartar")
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(showBackground = true)
@Composable
private fun SessionSummaryDialogPreview() {
    UmbralTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SessionSummaryDialog(
                summary = NotificationSummary(
                    sessionId = "session_123",
                    totalCount = 23,
                    byApp = listOf(
                        NotificationSummary.AppCount("com.instagram", "Instagram", 8),
                        NotificationSummary.AppCount("com.twitter", "Twitter", 6),
                        NotificationSummary.AppCount("com.whatsapp", "WhatsApp", 5),
                        NotificationSummary.AppCount("com.youtube", "YouTube", 3),
                        NotificationSummary.AppCount("com.gmail", "Gmail", 1)
                    ),
                    sessionDuration = 60.minutes
                ),
                bonusEnergy = 4,
                onViewAll = {},
                onDismiss = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionSummaryDialogLargePreview() {
    UmbralTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SessionSummaryDialog(
                summary = NotificationSummary(
                    sessionId = "session_456",
                    totalCount = 87,
                    byApp = listOf(
                        NotificationSummary.AppCount("com.instagram", "Instagram", 25),
                        NotificationSummary.AppCount("com.twitter", "Twitter", 22),
                        NotificationSummary.AppCount("com.whatsapp", "WhatsApp", 18),
                        NotificationSummary.AppCount("com.youtube", "YouTube", 14),
                        NotificationSummary.AppCount("com.gmail", "Gmail", 8)
                    ),
                    sessionDuration = 120.minutes
                ),
                bonusEnergy = 17,
                onViewAll = {},
                onDismiss = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SessionSummaryDialogSmallPreview() {
    UmbralTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            SessionSummaryDialog(
                summary = NotificationSummary(
                    sessionId = "session_789",
                    totalCount = 3,
                    byApp = listOf(
                        NotificationSummary.AppCount("com.gmail", "Gmail", 2),
                        NotificationSummary.AppCount("com.slack", "Slack", 1)
                    ),
                    sessionDuration = 30.minutes
                ),
                bonusEnergy = 0, // No bonus for < 5 notifications
                onViewAll = {},
                onDismiss = {}
            )
        }
    }
}
