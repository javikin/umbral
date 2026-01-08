package com.umbral.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.umbral.presentation.ui.components.UmbralScaffold
import com.umbral.presentation.ui.screens.apps.AppSelectorScreen
import com.umbral.presentation.ui.screens.home.HomeScreen
import com.umbral.presentation.ui.screens.nfc.NfcScanScreen
import com.umbral.presentation.ui.screens.nfc.TagsScreen
import com.umbral.presentation.ui.screens.onboarding.OnboardingNavHost
import com.umbral.presentation.ui.screens.profiles.ProfileDetailScreen
import com.umbral.presentation.ui.screens.profiles.ProfilesScreen
import com.umbral.presentation.ui.screens.qr.QrScanScreen
import com.umbral.presentation.ui.screens.settings.SettingsScreen
import com.umbral.presentation.ui.screens.stats.StatsScreen
import com.umbral.expedition.presentation.map.ExpeditionMapScreen
import com.umbral.expedition.presentation.companion.CompanionListScreen
import com.umbral.expedition.presentation.companion.CompanionDetailScreen
import com.umbral.expedition.presentation.achievements.AchievementsScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun UmbralNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.HOME
) {
    UmbralScaffold(
        navController = navController,
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Main destinations (with bottom nav)
            composable(NavRoutes.HOME) {
                HomeScreen(
                    onNavigateToNfcScan = { navController.navigate(NavRoutes.NFC_SCAN) },
                    onNavigateToQrScan = { navController.navigate(NavRoutes.QR_SCAN) },
                    onNavigateToStats = { navController.navigate(NavRoutes.STATS) },
                    onNavigateToCreateProfile = { navController.navigate(NavRoutes.profileDetail("new")) },
                    onNavigateToExpedition = { navController.navigate(NavRoutes.EXPEDITION_MAP) }
                )
            }

            composable(NavRoutes.PROFILES) {
                ProfilesScreen(
                    onNavigateToProfileDetail = { profileId ->
                        navController.navigate(NavRoutes.profileDetail(profileId))
                    }
                )
            }

            composable(NavRoutes.STATS) {
                StatsScreen()
            }

            composable(NavRoutes.SETTINGS) {
                SettingsScreen(
                    onNavigateToNfcTags = { navController.navigate(NavRoutes.NFC_TAGS) }
                )
            }

            // Secondary destinations (without bottom nav)
            composable(NavRoutes.NFC_SCAN) {
                NfcScanScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.NFC_TAGS) {
                TagsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToScan = { navController.navigate(NavRoutes.NFC_SCAN) }
                )
            }

            composable(NavRoutes.QR_SCAN) {
                QrScanScreen(
                    onDismiss = { navController.popBackStack() }
                )
            }

            // Expedition / Gamification destinations
            composable(NavRoutes.EXPEDITION_MAP) {
                ExpeditionMapScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.COMPANION_LIST) {
                CompanionListScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onCompanionClick = { companionId ->
                        navController.navigate(NavRoutes.companionDetail(companionId))
                    }
                )
            }

            composable(
                route = NavRoutes.COMPANION_DETAIL,
                arguments = listOf(
                    navArgument("companionId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val companionId = backStackEntry.arguments?.getString("companionId") ?: ""
                CompanionDetailScreen(
                    companionId = companionId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.ACHIEVEMENTS) {
                AchievementsScreen(
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = NavRoutes.PROFILE_DETAIL,
                arguments = listOf(
                    navArgument("profileId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                // Observe selected apps from AppSelectorScreen
                val selectedApps by backStackEntry.savedStateHandle
                    .getStateFlow<List<String>?>("selectedApps", null)
                    .collectAsStateWithLifecycle()

                ProfileDetailScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToAppSelector = { blockedApps ->
                        val encodedApps = URLEncoder.encode(
                            blockedApps.joinToString(","),
                            StandardCharsets.UTF_8.toString()
                        )
                        val profileId = backStackEntry.arguments?.getString("profileId") ?: "new"
                        navController.navigate(NavRoutes.appSelector(profileId, encodedApps))
                    },
                    selectedApps = selectedApps
                )
            }

            composable(
                route = NavRoutes.APP_SELECTOR,
                arguments = listOf(
                    navArgument("profileId") { type = NavType.StringType },
                    navArgument("blockedApps") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) {
                AppSelectorScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onConfirmSelection = { selectedApps ->
                        // Save selected apps to previousBackStackEntry for ProfileDetailScreen
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selectedApps",
                            selectedApps
                        )
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

object NavRoutes {
    const val HOME = "home"
    const val PROFILES = "profiles"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val NFC_SCAN = "nfc_scan"
    const val NFC_TAGS = "nfc_tags"
    const val QR_SCAN = "qr_scan"
    const val PROFILE_DETAIL = "profile/{profileId}"
    const val APP_SELECTOR = "app_selector/{profileId}?blockedApps={blockedApps}"
    const val ONBOARDING = "onboarding"

    // Expedition / Gamification
    const val EXPEDITION_MAP = "expedition/map"
    const val COMPANION_LIST = "expedition/companions"
    const val COMPANION_DETAIL = "expedition/companion/{companionId}"
    const val ACHIEVEMENTS = "expedition/achievements"

    fun profileDetail(profileId: String) = "profile/$profileId"
    fun appSelector(profileId: String, blockedApps: String = "") =
        "app_selector/$profileId?blockedApps=$blockedApps"
    fun companionDetail(companionId: String) = "expedition/companion/$companionId"
}
