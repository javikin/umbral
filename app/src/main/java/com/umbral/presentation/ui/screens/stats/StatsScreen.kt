package com.umbral.presentation.ui.screens.stats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.umbral.R
import com.umbral.presentation.ui.components.StatsGraph
import com.umbral.presentation.ui.components.StreakDisplay
import com.umbral.presentation.ui.components.UmbralCard
import com.umbral.presentation.ui.components.UmbralElevation
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

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

    // Animation states for staggered entrance
    var showStreakCard by remember { mutableStateOf(false) }
    var showAttemptsCard by remember { mutableStateOf(false) }
    var showTimeSavedCard by remember { mutableStateOf(false) }
    var showTopAppsCard by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            delay(100)
            showStreakCard = true
            delay(150)
            showAttemptsCard = true
            delay(150)
            showTimeSavedCard = true
            delay(150)
            showTopAppsCard = true
        }
    }

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
        if (uiState.isLoading) {
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
        } else if (uiState.isEmpty) {
            // Empty state
            EmptyStatsState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            // Stats content
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = UmbralSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
            ) {
                item { Spacer(modifier = Modifier.height(UmbralSpacing.sm)) }

                // Streak Card
                item {
                    AnimatedVisibility(
                        visible = showStreakCard,
                        enter = fadeIn(animationSpec = tween(400)) +
                                slideInVertically(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    initialOffsetY = { 50 }
                                )
                    ) {
                        StreakStatsCard(streak = uiState.streak)
                    }
                }

                // Blocked Attempts Card
                item {
                    AnimatedVisibility(
                        visible = showAttemptsCard,
                        enter = fadeIn(animationSpec = tween(400)) +
                                slideInVertically(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    initialOffsetY = { 50 }
                                )
                    ) {
                        BlockedAttemptsCard(attempts = uiState.weeklyAttempts)
                    }
                }

                // Time Saved Card
                item {
                    AnimatedVisibility(
                        visible = showTimeSavedCard,
                        enter = fadeIn(animationSpec = tween(400)) +
                                slideInVertically(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    initialOffsetY = { 50 }
                                )
                    ) {
                        TimeSavedCard(timeSaved = uiState.timeSaved)
                    }
                }

                // Most Blocked Apps Card
                item {
                    AnimatedVisibility(
                        visible = showTopAppsCard,
                        enter = fadeIn(animationSpec = tween(400)) +
                                slideInVertically(
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    ),
                                    initialOffsetY = { 50 }
                                )
                    ) {
                        MostBlockedAppsCard(topApps = uiState.topApps)
                    }
                }

                item { Spacer(modifier = Modifier.height(UmbralSpacing.lg)) }
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
    }
}

// =============================================================================
// STREAK STATS CARD
// =============================================================================

@Composable
fun StreakStatsCard(
    streak: StreakStats,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier.fillMaxWidth(),
        elevation = UmbralElevation.Subtle
    ) {
        // Header with streak display
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fire emoji
            Text(
                text = "🔥",
                fontSize = 40.sp
            )

            Spacer(modifier = Modifier.width(UmbralSpacing.md))

            Column {
                Text(
                    text = "${streak.current}",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (streak.current == 1) "día de racha" else "días de racha",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.md))

        // Progress to next milestone
        val milestones = listOf(7, 14, 21, 30, 60, 90, 100, 365)
        val nextMilestone = milestones.firstOrNull { it > streak.current } ?: 365
        val progress = streak.current.toFloat() / nextMilestone

        Column {
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(UmbralSpacing.xs))

            Text(
                text = "${nextMilestone - streak.current} días para próximo logro",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.md))

        // Best streak
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(UmbralSpacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mejor racha",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${streak.best} días",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// =============================================================================
// BLOCKED ATTEMPTS CARD
// =============================================================================

@Composable
fun BlockedAttemptsCard(
    attempts: WeeklyAttempts,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier.fillMaxWidth(),
        elevation = UmbralElevation.Subtle
    ) {
        Text(
            text = stringResource(R.string.blocked_attempts),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(UmbralSpacing.md))

        // Graph
        val maxAttempts = attempts.daily.maxOrNull()?.toFloat() ?: 1f
        val normalizedData = attempts.daily.map { it.toFloat() / maxAttempts }

        StatsGraph(
            data = normalizedData,
            labels = listOf("L", "M", "X", "J", "V", "S", "D"),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            lineColor = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(UmbralSpacing.md))

        // Summary row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.this_week),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${attempts.thisWeek}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Change indicator
            val change = attempts.percentChange
            val isPositive = change < 0 // Less attempts is positive
            val changeColor = if (isPositive) {
                Color(0xFF4CAF50) // Green
            } else {
                Color(0xFFF44336) // Red
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = changeColor.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = UmbralSpacing.sm, vertical = UmbralSpacing.xs)
            ) {
                Icon(
                    imageVector = if (isPositive) {
                        Icons.AutoMirrored.Filled.TrendingDown
                    } else {
                        Icons.AutoMirrored.Filled.TrendingUp
                    },
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = changeColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${abs(change)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = changeColor
                )
            }
        }
    }
}

// =============================================================================
// TIME SAVED CARD
// =============================================================================

@Composable
fun TimeSavedCard(
    timeSaved: Duration,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier.fillMaxWidth(),
        elevation = UmbralElevation.Subtle
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Schedule,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(UmbralSpacing.md))
            Column {
                Text(
                    text = "Tiempo ahorrado",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "esta semana",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(UmbralSpacing.md))

        // Format duration
        val hours = timeSaved.inWholeHours
        val minutes = (timeSaved.inWholeMinutes % 60)

        Text(
            text = "~${hours}h ${minutes}min",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(UmbralSpacing.xs))

        Text(
            text = "(basado en 5 min/intento)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// =============================================================================
// MOST BLOCKED APPS CARD
// =============================================================================

@Composable
fun MostBlockedAppsCard(
    topApps: List<BlockedAppStats>,
    modifier: Modifier = Modifier
) {
    UmbralCard(
        modifier = modifier.fillMaxWidth(),
        elevation = UmbralElevation.Subtle
    ) {
        Text(
            text = stringResource(R.string.top_blocked_apps),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (topApps.isEmpty()) {
            Spacer(modifier = Modifier.height(UmbralSpacing.md))
            Text(
                text = "Aún no hay datos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            Spacer(modifier = Modifier.height(UmbralSpacing.md))

            val maxCount = topApps.maxOfOrNull { it.count } ?: 1

            topApps.forEachIndexed { index, app ->
                BlockedAppRow(
                    position = index + 1,
                    appName = app.appName,
                    count = app.count,
                    progress = app.count.toFloat() / maxCount
                )

                if (index < topApps.lastIndex) {
                    Spacer(modifier = Modifier.height(UmbralSpacing.sm))
                }
            }
        }
    }
}

@Composable
private fun BlockedAppRow(
    position: Int,
    appName: String,
    count: Int,
    progress: Float,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Position
        Text(
            text = "$position.",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(24.dp)
        )

        Spacer(modifier = Modifier.width(UmbralSpacing.sm))

        // App name and progress
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

// =============================================================================
// DATA MODELS
// =============================================================================

data class StatsUiState(
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false,
    val streak: StreakStats = StreakStats(),
    val weeklyAttempts: WeeklyAttempts = WeeklyAttempts(),
    val timeSaved: Duration = Duration.ZERO,
    val topApps: List<BlockedAppStats> = emptyList()
)

data class StreakStats(
    val current: Int = 0,
    val best: Int = 0,
    val milestones: List<Int> = emptyList()
)

data class WeeklyAttempts(
    val daily: List<Int> = List(7) { 0 },
    val thisWeek: Int = 0,
    val lastWeek: Int = 0,
    val percentChange: Int = 0
)

data class BlockedAppStats(
    val packageName: String = "",
    val appName: String = "",
    val count: Int = 0
)

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
            uiState = StatsUiState(isLoading = false, isEmpty = true)
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
                isEmpty = false,
                streak = StreakStats(current = 12, best = 15),
                weeklyAttempts = WeeklyAttempts(
                    daily = listOf(5, 8, 12, 6, 15, 10, 7),
                    thisWeek = 63,
                    lastWeek = 82,
                    percentChange = -23
                ),
                timeSaved = 5.hours + 15.minutes,
                topApps = listOf(
                    BlockedAppStats("com.instagram", "Instagram", 28),
                    BlockedAppStats("com.tiktok", "TikTok", 19),
                    BlockedAppStats("com.twitter", "X (Twitter)", 16)
                )
            )
        )
    }
}

@Preview(name = "Stats Screen - Dark Theme", showBackground = true)
@Composable
private fun StatsScreenDarkPreview() {
    UmbralTheme(darkTheme = true) {
        StatsScreenContent(
            uiState = StatsUiState(
                isLoading = false,
                isEmpty = false,
                streak = StreakStats(current = 30, best = 30),
                weeklyAttempts = WeeklyAttempts(
                    daily = listOf(10, 15, 8, 20, 12, 5, 3),
                    thisWeek = 73,
                    lastWeek = 55,
                    percentChange = 33
                ),
                timeSaved = 6.hours + 5.minutes,
                topApps = listOf(
                    BlockedAppStats("com.instagram", "Instagram", 35),
                    BlockedAppStats("com.youtube", "YouTube", 22),
                    BlockedAppStats("com.reddit", "Reddit", 16)
                )
            )
        )
    }
}

@Preview(name = "Streak Stats Card", showBackground = true)
@Composable
private fun StreakStatsCardPreview() {
    UmbralTheme {
        StreakStatsCard(
            streak = StreakStats(current = 12, best = 21),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Blocked Attempts Card", showBackground = true)
@Composable
private fun BlockedAttemptsCardPreview() {
    UmbralTheme {
        BlockedAttemptsCard(
            attempts = WeeklyAttempts(
                daily = listOf(5, 8, 12, 6, 15, 10, 7),
                thisWeek = 63,
                lastWeek = 82,
                percentChange = -23
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Time Saved Card", showBackground = true)
@Composable
private fun TimeSavedCardPreview() {
    UmbralTheme {
        TimeSavedCard(
            timeSaved = 4.hours + 30.minutes,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Most Blocked Apps Card", showBackground = true)
@Composable
private fun MostBlockedAppsCardPreview() {
    UmbralTheme {
        MostBlockedAppsCard(
            topApps = listOf(
                BlockedAppStats("com.instagram", "Instagram", 23),
                BlockedAppStats("com.tiktok", "TikTok", 15),
                BlockedAppStats("com.twitter", "X (Twitter)", 9)
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
        if (uiState.isLoading) {
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
        } else if (uiState.isEmpty) {
            EmptyStatsState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = UmbralSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
            ) {
                item { Spacer(modifier = Modifier.height(UmbralSpacing.sm)) }
                item { StreakStatsCard(streak = uiState.streak) }
                item { BlockedAttemptsCard(attempts = uiState.weeklyAttempts) }
                item { TimeSavedCard(timeSaved = uiState.timeSaved) }
                item { MostBlockedAppsCard(topApps = uiState.topApps) }
                item { Spacer(modifier = Modifier.height(UmbralSpacing.lg)) }
            }
        }
    }
}
