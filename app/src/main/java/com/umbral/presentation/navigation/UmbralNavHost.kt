package com.umbral.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umbral.presentation.ui.screens.home.HomeScreen
import com.umbral.presentation.ui.screens.profiles.ProfilesScreen
import com.umbral.presentation.ui.screens.settings.SettingsScreen
import com.umbral.presentation.ui.screens.stats.StatsScreen

@Composable
fun UmbralNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.HOME
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NavRoutes.HOME) {
            HomeScreen(
                onNavigateToProfiles = { navController.navigate(NavRoutes.PROFILES) },
                onNavigateToStats = { navController.navigate(NavRoutes.STATS) },
                onNavigateToSettings = { navController.navigate(NavRoutes.SETTINGS) }
            )
        }

        composable(NavRoutes.PROFILES) {
            ProfilesScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.STATS) {
            StatsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

object NavRoutes {
    const val HOME = "home"
    const val PROFILES = "profiles"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val NFC_SCAN = "nfc_scan"
    const val QR_SCAN = "qr_scan"
    const val PROFILE_DETAIL = "profile/{profileId}"
    const val ONBOARDING = "onboarding"

    fun profileDetail(profileId: String) = "profile/$profileId"
}
