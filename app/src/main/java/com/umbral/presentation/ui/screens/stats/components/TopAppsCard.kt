package com.umbral.presentation.ui.screens.stats.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umbral.R
import com.umbral.data.local.dao.AppAttemptCount
import com.umbral.presentation.ui.components.AppIcon
import com.umbral.presentation.ui.components.UmbralCard
import com.umbral.presentation.ui.components.UmbralElevation
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * TopAppsCard - Shows the top blocked apps
 *
 * Displays:
 * - Rank badge (1, 2, 3...)
 * - App icon using AppIcon component
 * - App display name (converts package name)
 * - Attempt count
 *
 * @param apps List of apps with attempt counts
 * @param modifier Modifier for customization
 */
@Composable
fun TopAppsCard(
    apps: List<AppAttemptCount>,
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
        Spacer(modifier = Modifier.height(UmbralSpacing.md))

        if (apps.isEmpty()) {
            // Empty state
            Text(
                text = "Aún no hay datos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        } else {
            // Top apps list
            Column(
                verticalArrangement = Arrangement.spacedBy(UmbralSpacing.sm)
            ) {
                apps.take(5).forEachIndexed { index, app ->
                    TopAppItem(
                        rank = index + 1,
                        packageName = app.packageName,
                        attemptCount = app.count
                    )
                }
            }
        }
    }
}

/**
 * TopAppItem - Individual app item in the list
 *
 * @param rank Position in the ranking (1-based)
 * @param packageName Package name of the app
 * @param attemptCount Number of blocked attempts
 */
@Composable
private fun TopAppItem(
    rank: Int,
    packageName: String,
    attemptCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank badge
        RankBadge(rank = rank)

        Spacer(modifier = Modifier.width(UmbralSpacing.md))

        // App icon
        AppIcon(
            packageName = packageName,
            size = 40.dp
        )

        Spacer(modifier = Modifier.width(UmbralSpacing.md))

        // App name
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = getAppDisplayName(packageName),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = "$attemptCount ${if (attemptCount == 1) "intento" else "intentos"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Attempt count
        Text(
            text = "$attemptCount",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * RankBadge - Shows the rank position
 *
 * @param rank Position number (1, 2, 3...)
 */
@Composable
private fun RankBadge(
    rank: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(28.dp)
            .background(
                color = when (rank) {
                    1 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    2 -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                    3 -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.2f)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$rank",
            style = MaterialTheme.typography.labelMedium,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = when (rank) {
                1 -> MaterialTheme.colorScheme.primary
                2 -> MaterialTheme.colorScheme.secondary
                3 -> MaterialTheme.colorScheme.tertiary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

/**
 * Get a user-friendly display name from package name
 *
 * Converts common package names to readable names.
 * Falls back to package name if not recognized.
 *
 * @param packageName Android package name
 * @return Display name
 */
private fun getAppDisplayName(packageName: String): String {
    return when {
        // Social Media
        packageName.contains("instagram") -> "Instagram"
        packageName.contains("facebook") -> "Facebook"
        packageName.contains("tiktok") -> "TikTok"
        packageName.contains("twitter") || packageName.contains("x.com") -> "X (Twitter)"
        packageName.contains("snapchat") -> "Snapchat"
        packageName.contains("whatsapp") -> "WhatsApp"
        packageName.contains("telegram") -> "Telegram"
        packageName.contains("discord") -> "Discord"
        packageName.contains("reddit") -> "Reddit"
        packageName.contains("linkedin") -> "LinkedIn"
        packageName.contains("pinterest") -> "Pinterest"

        // Entertainment
        packageName.contains("youtube") -> "YouTube"
        packageName.contains("netflix") -> "Netflix"
        packageName.contains("spotify") -> "Spotify"
        packageName.contains("twitch") -> "Twitch"
        packageName.contains("hulu") -> "Hulu"
        packageName.contains("disney") -> "Disney+"
        packageName.contains("primevideo") -> "Prime Video"

        // Gaming
        packageName.contains("genshin") -> "Genshin Impact"
        packageName.contains("clash") -> "Clash of Clans"
        packageName.contains("candy.crush") -> "Candy Crush"
        packageName.contains("pubg") -> "PUBG"
        packageName.contains("pokemon") -> "Pokémon GO"

        // Shopping
        packageName.contains("amazon") -> "Amazon"
        packageName.contains("ebay") -> "eBay"
        packageName.contains("mercadolibre") -> "Mercado Libre"
        packageName.contains("aliexpress") -> "AliExpress"
        packageName.contains("shein") -> "Shein"

        // Others
        packageName.contains("chrome") -> "Chrome"
        packageName.contains("firefox") -> "Firefox"
        packageName.contains("gmail") -> "Gmail"
        packageName.contains("maps") -> "Google Maps"

        // Extract last segment if available
        packageName.contains(".") -> {
            packageName.split(".").lastOrNull()?.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            } ?: packageName
        }

        // Fallback
        else -> packageName
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Top Apps Card - With Data", showBackground = true)
@Composable
private fun TopAppsCardPreview() {
    UmbralTheme {
        TopAppsCard(
            apps = listOf(
                AppAttemptCount("com.instagram.android", 28),
                AppAttemptCount("com.zhiliaoapp.musically", 19), // TikTok
                AppAttemptCount("com.facebook.katana", 15),
                AppAttemptCount("com.twitter.android", 12),
                AppAttemptCount("com.snapchat.android", 8)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Top Apps Card - Empty", showBackground = true)
@Composable
private fun TopAppsCardEmptyPreview() {
    UmbralTheme {
        TopAppsCard(
            apps = emptyList(),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Top Apps Card - Few Apps", showBackground = true)
@Composable
private fun TopAppsCardFewPreview() {
    UmbralTheme {
        TopAppsCard(
            apps = listOf(
                AppAttemptCount("com.spotify.music", 42),
                AppAttemptCount("com.google.android.youtube", 31)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Top Apps Card - Dark", showBackground = true)
@Composable
private fun TopAppsCardDarkPreview() {
    UmbralTheme(darkTheme = true) {
        TopAppsCard(
            apps = listOf(
                AppAttemptCount("com.netflix.mediaclient", 35),
                AppAttemptCount("com.discord", 22),
                AppAttemptCount("com.reddit.frontpage", 18)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
