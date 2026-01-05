package com.umbral.domain.apps

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.ui.graphics.vector.ImageVector
import com.umbral.R

/**
 * Categories for installed apps with Spanish display names and icons.
 */
enum class AppCategory(
    @StringRes val displayName: Int,
    val icon: ImageVector
) {
    ALL(R.string.category_all, Icons.Default.Apps),
    SOCIAL(R.string.category_social, Icons.Default.People),
    COMMUNICATION(R.string.category_communication, Icons.AutoMirrored.Filled.Chat),
    GAMES(R.string.category_games, Icons.Default.SportsEsports),
    ENTERTAINMENT(R.string.category_entertainment, Icons.Default.Movie),
    PRODUCTIVITY(R.string.category_productivity, Icons.Default.Work),
    SHOPPING(R.string.category_shopping, Icons.Default.ShoppingCart),
    NEWS(R.string.category_news, Icons.Default.Newspaper),
    SYSTEM(R.string.category_system, Icons.Default.Settings),
    OTHER(R.string.category_other, Icons.Default.Apps);

    companion object {
        /**
         * Categorize an app based on its package name.
         */
        fun fromPackageName(packageName: String): AppCategory {
            val lower = packageName.lowercase()

            return when {
                // Social Networks
                lower.contains("facebook") ||
                lower.contains("instagram") ||
                lower.contains("twitter") ||
                lower.contains("tiktok") ||
                lower.contains("snapchat") ||
                lower.contains("linkedin") ||
                lower.contains("reddit") ||
                lower.contains("pinterest") ||
                lower.contains("tumblr") -> SOCIAL

                // Communication
                lower.contains("whatsapp") ||
                lower.contains("telegram") ||
                lower.contains("messenger") ||
                lower.contains("signal") ||
                lower.contains("discord") ||
                lower.contains("skype") ||
                lower.contains("viber") ||
                lower.contains("wechat") ||
                lower.contains("line") ||
                lower.contains("slack") ||
                lower.contains("teams") ||
                lower.contains("zoom") ||
                lower.contains("meet") -> COMMUNICATION

                // Games
                lower.contains("game") ||
                lower.contains("play.games") ||
                lower.contains("supercell") ||
                lower.contains("king.com") ||
                lower.contains("roblox") ||
                lower.contains("minecraft") ||
                lower.contains("pokemon") ||
                lower.contains("pubg") ||
                lower.contains("freefire") ||
                lower.contains("callofduty") ||
                lower.contains("chess") ||
                lower.contains("puzzle") ||
                lower.contains("arcade") -> GAMES

                // Entertainment
                lower.contains("youtube") ||
                lower.contains("netflix") ||
                lower.contains("spotify") ||
                lower.contains("twitch") ||
                lower.contains("hbo") ||
                lower.contains("disney") ||
                lower.contains("amazon.video") ||
                lower.contains("primevideo") ||
                lower.contains("music") ||
                lower.contains("video") ||
                lower.contains("media") ||
                lower.contains("movie") ||
                lower.contains("player") ||
                lower.contains("soundcloud") ||
                lower.contains("podcast") -> ENTERTAINMENT

                // Productivity
                lower.contains("office") ||
                lower.contains("docs") ||
                lower.contains("sheets") ||
                lower.contains("slides") ||
                lower.contains("drive") ||
                lower.contains("dropbox") ||
                lower.contains("notion") ||
                lower.contains("evernote") ||
                lower.contains("onenote") ||
                lower.contains("calendar") ||
                lower.contains("tasks") ||
                lower.contains("todo") ||
                lower.contains("notes") ||
                lower.contains("trello") ||
                lower.contains("asana") ||
                lower.contains("pdf") -> PRODUCTIVITY

                // Shopping
                lower.contains("amazon") && !lower.contains("video") ||
                lower.contains("ebay") ||
                lower.contains("aliexpress") ||
                lower.contains("mercadolibre") ||
                lower.contains("shop") ||
                lower.contains("store") ||
                lower.contains("market") ||
                lower.contains("walmart") ||
                lower.contains("target") ||
                lower.contains("bestbuy") -> SHOPPING

                // News
                lower.contains("news") ||
                lower.contains("noticias") ||
                lower.contains("flipboard") ||
                lower.contains("feedly") ||
                lower.contains("medium") -> NEWS

                // System
                lower.contains("android.") ||
                lower.contains("com.google.android.") &&
                    !lower.contains("youtube") &&
                    !lower.contains("music") ||
                lower.contains("samsung.android.") ||
                lower.contains("settings") ||
                lower.contains("launcher") -> SYSTEM

                else -> OTHER
            }
        }
    }
}
