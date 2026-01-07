package com.umbral.notifications.presentation.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.notifications.domain.model.BlockedNotification
import com.umbral.notifications.presentation.history.formatTime
import com.umbral.presentation.ui.theme.UmbralTheme
import java.time.Instant

/**
 * Individual notification item card with swipe-to-delete support.
 *
 * Displays notification details and provides actions via context menu:
 * - Mark as read
 * - Open app
 * - Delete
 *
 * @param notification The blocked notification to display
 * @param onMarkAsRead Callback when marking as read
 * @param onOpenApp Callback when opening the source app
 * @param onDelete Callback when deleting notification
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationItem(
    notification: BlockedNotification,
    onMarkAsRead: () -> Unit,
    onOpenApp: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showMenu by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            // Swipe background (red with delete icon)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        enableDismissFromStartToEnd = false // Only allow right-to-left swipe
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (notification.isRead)
                    MaterialTheme.colorScheme.surfaceVariant
                else
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showMenu = true }
                    .padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // App icon placeholder (circle with first letter)
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = notification.appName.firstOrNull()?.uppercase() ?: "?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Content
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = notification.appName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = notification.timestamp.formatTime(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Notification title
                    notification.title?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (!notification.isRead) FontWeight.Medium else FontWeight.Normal,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Notification text
                    notification.text?.let {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            // Context menu
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                if (!notification.isRead) {
                    DropdownMenuItem(
                        text = { Text("Marcar como leída") },
                        onClick = {
                            onMarkAsRead()
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null
                            )
                        }
                    )
                }

                DropdownMenuItem(
                    text = { Text("Abrir ${notification.appName}") },
                    onClick = {
                        onOpenApp()
                        showMenu = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = null
                        )
                    }
                )

                DropdownMenuItem(
                    text = { Text("Eliminar") },
                    onClick = {
                        onDelete()
                        showMenu = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.error,
                        leadingIconColor = MaterialTheme.colorScheme.error
                    )
                )
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Notification Item - Unread", showBackground = true)
@Composable
private fun NotificationItemUnreadPreview() {
    UmbralTheme {
        NotificationItem(
            notification = BlockedNotification(
                id = 1,
                sessionId = "session-1",
                packageName = "com.instagram.android",
                appName = "Instagram",
                title = "usuario123 te mencionó en un comentario",
                text = "¡Mira este increíble post que compartí contigo!",
                timestamp = Instant.now(),
                iconUri = null,
                isRead = false
            ),
            onMarkAsRead = {},
            onOpenApp = {},
            onDelete = {}
        )
    }
}

@Preview(name = "Notification Item - Read", showBackground = true)
@Composable
private fun NotificationItemReadPreview() {
    UmbralTheme {
        NotificationItem(
            notification = BlockedNotification(
                id = 2,
                sessionId = "session-1",
                packageName = "com.twitter.android",
                appName = "Twitter",
                title = "Nuevo tweet de @cuenta",
                text = "Este es el contenido del tweet...",
                timestamp = Instant.now().minusSeconds(3600),
                iconUri = null,
                isRead = true
            ),
            onMarkAsRead = {},
            onOpenApp = {},
            onDelete = {}
        )
    }
}

@Preview(name = "Notification Item - No Text", showBackground = true)
@Composable
private fun NotificationItemNoTextPreview() {
    UmbralTheme {
        NotificationItem(
            notification = BlockedNotification(
                id = 3,
                sessionId = "session-1",
                packageName = "com.whatsapp",
                appName = "WhatsApp",
                title = "Mensaje de María",
                text = null,
                timestamp = Instant.now(),
                iconUri = null,
                isRead = false
            ),
            onMarkAsRead = {},
            onOpenApp = {},
            onDelete = {}
        )
    }
}

@Preview(name = "Notification Item - Dark", showBackground = true)
@Composable
private fun NotificationItemDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            NotificationItem(
                notification = BlockedNotification(
                    id = 4,
                    sessionId = "session-1",
                    packageName = "com.telegram",
                    appName = "Telegram",
                    title = "Canal de noticias",
                    text = "Nuevo mensaje en el canal...",
                    timestamp = Instant.now(),
                    iconUri = null,
                    isRead = false
                ),
                onMarkAsRead = {},
                onOpenApp = {},
                onDelete = {}
            )
        }
    }
}
