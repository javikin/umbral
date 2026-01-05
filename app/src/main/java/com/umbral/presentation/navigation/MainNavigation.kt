package com.umbral.presentation.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umbral.data.local.preferences.UmbralPreferences
import com.umbral.presentation.ui.screens.onboarding.OnboardingNavHost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainNavigationViewModel @Inject constructor(
    val preferences: UmbralPreferences
) : ViewModel()

@Composable
fun MainNavigation(
    viewModel: MainNavigationViewModel = hiltViewModel()
) {
    val onboardingCompleted by viewModel.preferences.onboardingCompleted
        .collectAsStateWithLifecycle(initialValue = false)

    LaunchedEffect(onboardingCompleted) {
        Log.d("MainNavigation", "onboardingCompleted = $onboardingCompleted")
    }

    if (!onboardingCompleted) {
        // Show onboarding flow
        OnboardingNavHost(
            onOnboardingComplete = {
                // Preferences update will trigger recomposition
            }
        )
    } else {
        // Show main app
        UmbralNavHost()
    }
}
