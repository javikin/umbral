package com.umbral.presentation.ui.screens.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.umbral.R
import com.umbral.data.local.dao.AppAttemptCount
import com.umbral.data.local.dao.DailyStats
import com.umbral.notifications.domain.model.AppNotificationStats
import com.umbral.notifications.domain.model.NotificationStats
import com.umbral.presentation.ui.components.UmbralCard
import com.umbral.presentation.ui.components.UmbralElevation
import com.umbral.presentation.ui.screens.stats.components.TopAppsCard
import com.umbral.presentation.ui.screens.stats.components.WeeklyChartCard
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import kotlin.math.abs

// =============================================================================
// STATS SCREEN
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.stats),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                // Loading state
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            uiState.error != null -> {
                // Error state
                ErrorState(
                    error = uiState.error!!,
                    onRetry = { viewModel.loadStats() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            uiState.todayBlockedMinutes == 0 && uiState.weeklyBlockedMinutes == 0 -> {
                // Empty state
                EmptyStatsState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                // Stats content
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = UmbralSpacing.screenHorizontal),
                    verticalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
                ) {
                    item { Spacer(modifier = Modifier.height(UmbralSpacing.sm)) }

                    // Today Stats Card
                    item {
                        TodayStatsCard(
                            blockedMinutes = uiState.todayBlockedMinutes,
                            attempts = uiState.todayAttempts
                        )
                    }

                    // Weekly Stats Card
                    item {
                        WeeklyStatsCard(
                            totalMinutes = uiState.weeklyBlockedMinutes,
                            percentageChange = uiState.percentageChange
                        )
                    }

                    // Weekly Chart Card
                    item {
                        WeeklyChartCard(dailyStats = uiState.dailyStats)
                    }

                    // Total Attempts Card
                    item {
                        AttemptsCard(
                            todayAttempts = uiState.todayAttempts
                        )
                    }

                    // Top Apps Card
                    item {
                        TopAppsCard(apps = uiState.topApps)
                    }

                    // Notification Stats Card
                    uiState.notificationStats?.let { notifStats ->
                        if (notifStats.totalBlocked > 0) {
                            item {
                                NotificationStatsCard(stats = notifStats)
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(UmbralSpacing.lg)) }
                }
            }
        }
    }
}

// =============================================================================
// TODAY STATS CARD
// =============================================================================

@Composable
private fun TodayStatsCard(
    blockedMinutes: Int,
    attempts: Int,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier.fillMaxWidth(),
        elevation = UmbralElevation.Subtle
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(UmbralSpacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.today),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(UmbralSpacing.xs))
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = formatDuration(blockedMinutes),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(UmbralSpacing.sm))
                    Text(
                        text = stringResource(R.string.stats_blocked_time),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

// =============================================================================
// WEEKLY STATS CARD
// =============================================================================

@Composable
private fun WeeklyStatsCard(
    totalMinutes: Int,
    percentageChange: Int,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier.fillMaxWidth(),
        elevation = UmbralElevation.Subtle
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.this_week),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(UmbralSpacing.sm))
                Text(
                    text = formatDuration(totalMinutes),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(UmbralSpacing.xs))
                Text(
                    text = stringResource(R.string.stats_blocked_time),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Percentage change chip
            if (percentageChange != 0) {
                PercentageChangeChip(percentageChange = percentageChange)
            }
        }
    }
}

// =============================================================================
// PERCENTAGE CHANGE CHIP
// =============================================================================

@Composable
private fun PercentageChangeChip(
    percentageChange: Int,
    modifier: Modifier = Modifier
) {
    val isPositive = percentageChange > 0
    val chipColor = if (isPositive) {
        Color(0xFF4CAF50) // Green - more blocking is good
    } else {
        Color(0xFFF44336) // Red - less blocking might be concerning
    }

    Row(
        modifier = modifier
            .background(
                color = chipColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = UmbralSpacing.sm, vertical = UmbralSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isPositive) {
                Icons.AutoMirrored.Filled.TrendingUp
            } else {
                Icons.AutoMirrored.Filled.TrendingDown
            },
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = chipColor
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${if (isPositive) "+" else ""}${percentageChange}%",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = chipColor
        )
    }
}

// WeeklyChartCard is imported from components package

// =============================================================================
// ATTEMPTS CARD
// =============================================================================

@Composable
private fun AttemptsCard(
    todayAttempts: Int,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier.fillMaxWidth(),
        elevation = UmbralElevation.Subtle
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Block,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.width(UmbralSpacing.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.stats_attempts_blocked),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(UmbralSpacing.xs))
                Text(
                    text = stringResource(R.string.today),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$todayAttempts",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

// TopAppsCard is imported from components package

// =============================================================================
// NOTIFICATION STATS CARD
// =============================================================================

@Composable
private fun NotificationStatsCard(
    stats: NotificationStats,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier.fillMaxWidth(),
        elevation = UmbralElevation.Subtle
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsOff,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.width(UmbralSpacing.md))
                Text(
                    text = stringResource(R.string.stats_notifications_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.md))

            // Stats rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Total blocked
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.stats_notifications_total),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(UmbralSpacing.xs))
                    Text(
                        text = "${stats.totalBlocked}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }

                // Last 7 days
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = stringResource(R.string.stats_notifications_last_7_days),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(UmbralSpacing.xs))
                    Text(
                        text = "${stats.last7Days}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Top apps section (if available)
            if (stats.topApps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(UmbralSpacing.md))

                Text(
                    text = stringResource(R.string.stats_notifications_top_apps),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(UmbralSpacing.sm))

                // Show top 3 apps
                stats.topApps.take(3).forEach { app ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = UmbralSpacing.xs),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = app.appName.ifEmpty { app.packageName.substringAfterLast('.') },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "${app.count}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// EMPTY STATE
// =============================================================================

@Composable
private fun EmptyStatsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(UmbralSpacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.BarChart,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(UmbralSpacing.md))
        Text(
            text = stringResource(R.string.stats_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(UmbralSpacing.xs))
        Text(
            text = "Empieza a bloquear apps para ver tus estadísticas",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

// =============================================================================
// ERROR STATE
// =============================================================================

@Composable
private fun ErrorState(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(UmbralSpacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.error),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(UmbralSpacing.sm))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// =============================================================================
// HELPER FUNCTIONS
// =============================================================================

/**
 * Format duration in minutes to "Xh Ym" format.
 * Examples: "2h 30m", "45m", "0m"
 */
private fun formatDuration(minutes: Int): String {
    if (minutes == 0) return "0m"

    val hours = minutes / 60
    val mins = minutes % 60

    return when {
        hours > 0 && mins > 0 -> "${hours}h ${mins}m"
        hours > 0 -> "${hours}h"
        else -> "${mins}m"
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Stats Screen - Loading", showBackground = true)
@Composable
private fun StatsScreenLoadingPreview() {
    UmbralTheme {
        StatsScreenContent(
            uiState = StatsUiState(isLoading = true)
        )
    }
}

@Preview(name = "Stats Screen - Empty", showBackground = true)
@Composable
private fun StatsScreenEmptyPreview() {
    UmbralTheme {
        StatsScreenContent(
            uiState = StatsUiState(
                isLoading = false,
                todayBlockedMinutes = 0,
                weeklyBlockedMinutes = 0
            )
        )
    }
}

@Preview(name = "Stats Screen - With Data", showBackground = true)
@Composable
private fun StatsScreenWithDataPreview() {
    UmbralTheme {
        StatsScreenContent(
            uiState = StatsUiState(
                isLoading = false,
                todayBlockedMinutes = 145,
                todayAttempts = 12,
                weeklyBlockedMinutes = 720,
                previousWeekMinutes = 480,
                percentageChange = 50,
                dailyStats = listOf(
                    DailyStats("2026-01-01", 90),
                    DailyStats("2026-01-02", 120),
                    DailyStats("2026-01-03", 100)
                ),
                topApps = listOf(
                    AppAttemptCount("com.instagram", 28),
                    AppAttemptCount("com.tiktok", 19)
                )
            )
        )
    }
}

@Preview(name = "Stats Screen - Dark", showBackground = true)
@Composable
private fun StatsScreenDarkPreview() {
    UmbralTheme(darkTheme = true) {
        StatsScreenContent(
            uiState = StatsUiState(
                isLoading = false,
                todayBlockedMinutes = 230,
                todayAttempts = 18,
                weeklyBlockedMinutes = 1200,
                previousWeekMinutes = 1400,
                percentageChange = -14
            )
        )
    }
}

@Preview(name = "Today Stats Card", showBackground = true)
@Composable
private fun TodayStatsCardPreview() {
    UmbralTheme {
        TodayStatsCard(
            blockedMinutes = 145,
            attempts = 12,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Weekly Stats Card", showBackground = true)
@Composable
private fun WeeklyStatsCardPreview() {
    UmbralTheme {
        WeeklyStatsCard(
            totalMinutes = 720,
            percentageChange = 50,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Notification Stats Card", showBackground = true)
@Composable
private fun NotificationStatsCardPreview() {
    UmbralTheme {
        NotificationStatsCard(
            stats = NotificationStats(
                totalBlocked = 247,
                last7Days = 42,
                topApps = listOf(
                    AppNotificationStats(
                        packageName = "com.whatsapp",
                        appName = "WhatsApp",
                        count = 89
                    ),
                    AppNotificationStats(
                        packageName = "com.instagram.android",
                        appName = "Instagram",
                        count = 67
                    ),
                    AppNotificationStats(
                        packageName = "com.twitter.android",
                        appName = "Twitter",
                        count = 45
                    )
                )
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

// =============================================================================
// PREVIEW HELPER
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatsScreenContent(
    uiState: StatsUiState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Estadísticas",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            uiState.todayBlockedMinutes == 0 && uiState.weeklyBlockedMinutes == 0 -> {
                EmptyStatsState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = UmbralSpacing.screenHorizontal),
                    verticalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
                ) {
                    item { Spacer(modifier = Modifier.height(UmbralSpacing.sm)) }
                    item { TodayStatsCard(uiState.todayBlockedMinutes, uiState.todayAttempts) }
                    item { WeeklyStatsCard(uiState.weeklyBlockedMinutes, uiState.percentageChange) }
                    item { WeeklyChartCard(uiState.dailyStats) }
                    item { AttemptsCard(uiState.todayAttempts) }
                    item { TopAppsCard(apps = uiState.topApps) }
                    uiState.notificationStats?.let { notifStats ->
                        if (notifStats.totalBlocked > 0) {
                            item { NotificationStatsCard(stats = notifStats) }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(UmbralSpacing.lg)) }
                }
            }
        }
    }
}
