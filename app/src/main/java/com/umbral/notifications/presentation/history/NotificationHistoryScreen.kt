package com.umbral.notifications.presentation.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umbral.notifications.domain.model.BlockedNotification
import com.umbral.notifications.presentation.history.components.FilterChipsRow
import com.umbral.notifications.presentation.history.components.NotificationItem
import com.umbral.notifications.presentation.history.components.SessionHeader
import com.umbral.presentation.ui.theme.UmbralTheme
import java.time.Instant

/**
 * Notification History screen.
 *
 * Displays all blocked notifications grouped by session with filtering options.
 * Users can:
 * - Filter by app and time period
 * - Mark notifications as read
 * - Open the source app
 * - Delete notifications
 * - Swipe to delete
 *
 * @param onNavigateBack Callback to navigate back
 * @param viewModel ViewModel for managing state and business logic
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: NotificationHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notificaciones Pausadas") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter chips
            FilterChipsRow(
                selectedApp = state.selectedApp,
                selectedPeriod = state.selectedPeriod,
                availableApps = state.availableApps,
                onAppSelected = viewModel::setAppFilter,
                onPeriodSelected = viewModel::setPeriodFilter
            )

            // Content
            when {
                state.isLoading -> LoadingContent()
                state.notifications.isEmpty() -> EmptyContent()
                else -> NotificationList(
                    groupedNotifications = state.groupedNotifications,
                    onMarkAsRead = viewModel::markAsRead,
                    onOpenApp = viewModel::openApp,
                    onDelete = viewModel::deleteNotification
                )
            }
        }
    }
}

/**
 * Loading indicator.
 */
@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

/**
 * Empty state when no notifications are available.
 */
@Composable
private fun EmptyContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Inbox,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Sin notificaciones",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Las notificaciones pausadas durante sesiones de bloqueo aparecerán aquí",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * List of notifications grouped by session.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NotificationList(
    groupedNotifications: Map<String, List<BlockedNotification>>,
    onMarkAsRead: (Long) -> Unit,
    onOpenApp: (String) -> Unit,
    onDelete: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        groupedNotifications.forEach { (sessionId, notifications) ->
            // Session header (sticky)
            stickyHeader(key = "header_$sessionId") {
                SessionHeader(
                    sessionId = sessionId,
                    notificationCount = notifications.size
                )
            }

            // Notification items
            items(
                items = notifications,
                key = { notification -> notification.id }
            ) { notification ->
                NotificationItem(
                    notification = notification,
                    onMarkAsRead = { onMarkAsRead(notification.id) },
                    onOpenApp = { onOpenApp(notification.packageName) },
                    onDelete = { onDelete(notification.id) }
                )
            }

            // Spacer between sessions
            item(key = "spacer_$sessionId") {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Notification History - With Data", showBackground = true)
@Composable
private fun NotificationHistoryScreenPreview() {
    val sampleNotifications = mapOf(
        "session-1" to listOf(
            BlockedNotification(
                id = 1,
                sessionId = "session-1",
                packageName = "com.instagram.android",
                appName = "Instagram",
                title = "usuario123 te mencionó",
                text = "En una historia...",
                timestamp = Instant.now(),
                iconUri = null,
                isRead = false
            ),
            BlockedNotification(
                id = 2,
                sessionId = "session-1",
                packageName = "com.twitter.android",
                appName = "Twitter",
                title = "Nuevo tweet",
                text = "De @cuenta...",
                timestamp = Instant.now().minusSeconds(600),
                iconUri = null,
                isRead = false
            )
        ),
        "session-2" to listOf(
            BlockedNotification(
                id = 3,
                sessionId = "session-2",
                packageName = "com.whatsapp",
                appName = "WhatsApp",
                title = "Mensaje de María",
                text = "Hola, ¿cómo estás?",
                timestamp = Instant.now().minusSeconds(7200),
                iconUri = null,
                isRead = true
            )
        )
    )

    UmbralTheme {
        Surface {
            NotificationList(
                groupedNotifications = sampleNotifications,
                onMarkAsRead = {},
                onOpenApp = {},
                onDelete = {}
            )
        }
    }
}

@Preview(name = "Notification History - Empty", showBackground = true)
@Composable
private fun NotificationHistoryEmptyPreview() {
    UmbralTheme {
        Surface {
            EmptyContent()
        }
    }
}

@Preview(name = "Notification History - Loading", showBackground = true)
@Composable
private fun NotificationHistoryLoadingPreview() {
    UmbralTheme {
        Surface {
            LoadingContent()
        }
    }
}

@Preview(name = "Notification History - Dark Theme", showBackground = true)
@Composable
private fun NotificationHistoryDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            EmptyContent()
        }
    }
}
