package com.umbral.presentation.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.umbral.domain.model.OnboardingStep
import com.umbral.presentation.viewmodel.OnboardingViewModel

@Composable
fun OnboardingNavHost(
    onOnboardingComplete: (String) -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "welcome",
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        composable("welcome") {
            WelcomeScreen(
                onGetStarted = {
                    viewModel.nextStep()
                    navController.navigate("how_it_works")
                }
            )
        }

        composable("how_it_works") {
            HowItWorksScreen(
                onContinue = {
                    viewModel.nextStep()
                    navController.navigate("how_to_unlock")
                },
                onBack = {
                    viewModel.previousStep()
                    navController.popBackStack()
                }
            )
        }

        composable("how_to_unlock") {
            HowToUnblockScreen(
                onContinue = {
                    viewModel.nextStep()
                    navController.navigate("permissions")
                },
                onBack = {
                    viewModel.previousStep()
                    navController.popBackStack()
                }
            )
        }

        composable("permissions") {
            PermissionsScreen(
                viewModel = viewModel,
                onContinue = {
                    viewModel.nextStep()
                    navController.navigate("select_apps") {
                        // Clear backstack - no going back after permissions
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("select_apps") {
            SelectAppsScreen(
                viewModel = viewModel,
                onContinue = {
                    // Complete onboarding and create profile
                    viewModel.completeOnboarding(
                        onSuccess = { profileId ->
                            viewModel.nextStep()
                            navController.navigate("success") {
                                // Clear backstack so user can't go back
                                popUpTo("select_apps") { inclusive = true }
                            }
                        },
                        onError = { error ->
                            // TODO: Show error message
                        }
                    )
                }
            )
        }

        composable("success") {
            SuccessScreen(
                profileName = state.profileName,
                appsCount = state.selectedApps.size,
                onStartBlocking = {
                    // TODO: Navigate to home and activate blocking
                    // For now, just complete
                    onOnboardingComplete("") // Empty string since we already created profile
                },
                onLater = {
                    onOnboardingComplete("") // Empty string since we already created profile
                }
            )
        }
    }
}
