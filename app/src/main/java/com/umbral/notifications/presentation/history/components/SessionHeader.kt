package com.umbral.notifications.presentation.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Session header for grouping notifications by blocking session.
 *
 * Displays session metadata like time and notification count.
 * Used as sticky header in LazyColumn.
 *
 * @param sessionId The session ID (could be parsed for display)
 * @param notificationCount Number of notifications in this session
 */
@Composable
fun SessionHeader(
    sessionId: String,
    notificationCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Session info (could parse sessionId for better display)
            Text(
                text = "Sesión $sessionId",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Notification count badge
            Text(
                text = "$notificationCount notificaciones",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Session Header - Light", showBackground = true)
@Composable
private fun SessionHeaderPreview() {
    UmbralTheme {
        SessionHeader(
            sessionId = "2024-01-07-10:30",
            notificationCount = 5
        )
    }
}

@Preview(name = "Session Header - Many Notifications", showBackground = true)
@Composable
private fun SessionHeaderManyPreview() {
    UmbralTheme {
        SessionHeader(
            sessionId = "2024-01-06-15:45",
            notificationCount = 23
        )
    }
}

@Preview(name = "Session Header - Dark", showBackground = true)
@Composable
private fun SessionHeaderDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            SessionHeader(
                sessionId = "2024-01-05-08:15",
                notificationCount = 12
            )
        }
    }
}
