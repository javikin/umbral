package com.umbral.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umbral.presentation.ui.components.UmbralScaffold
import com.umbral.presentation.ui.screens.home.HomeScreen
import com.umbral.presentation.ui.screens.nfc.NfcScanScreen
import com.umbral.presentation.ui.screens.profiles.ProfilesScreen
import com.umbral.presentation.ui.screens.settings.SettingsScreen
import com.umbral.presentation.ui.screens.stats.StatsScreen

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
                    onNavigateToQrScan = { navController.navigate(NavRoutes.QR_SCAN) }
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
                SettingsScreen()
            }

            // Secondary destinations (without bottom nav)
            composable(NavRoutes.NFC_SCAN) {
                NfcScanScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(NavRoutes.QR_SCAN) {
                // TODO: QrScanScreen
            }

            composable(NavRoutes.PROFILE_DETAIL) { backStackEntry ->
                val profileId = backStackEntry.arguments?.getString("profileId")
                // TODO: ProfileDetailScreen(profileId)
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
    const val QR_SCAN = "qr_scan"
    const val PROFILE_DETAIL = "profile/{profileId}"
    const val ONBOARDING = "onboarding"

    fun profileDetail(profileId: String) = "profile/$profileId"
}
