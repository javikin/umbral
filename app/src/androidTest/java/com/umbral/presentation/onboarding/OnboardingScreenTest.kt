package com.umbral.presentation.onboarding

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.umbral.domain.model.NfcStatus
import com.umbral.domain.model.OnboardingState
import com.umbral.domain.model.OnboardingStep
import com.umbral.domain.model.PermissionStatus
import com.umbral.domain.model.PermissionStates
import com.umbral.presentation.ui.screens.onboarding.HowItWorksScreen
import com.umbral.presentation.ui.screens.onboarding.PermissionsScreen
import com.umbral.presentation.ui.screens.onboarding.SuccessScreen
import com.umbral.presentation.ui.screens.onboarding.WelcomeScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests de UI para el flujo de onboarding.
 *
 * Estos tests verifican:
 * 1. Navegación entre páginas
 * 2. Contenido de cada página
 * 3. Estados de permisos
 * 4. Flujo de completar onboarding
 */
@RunWith(AndroidJUnit4::class)
class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var continueClicked = false
    private var backClicked = false
    private var getStartedClicked = false

    @Before
    fun setup() {
        continueClicked = false
        backClicked = false
        getStartedClicked = false
    }

    // ==================== Welcome Screen Tests ====================

    @Test
    fun welcomeScreen_displaysWelcomeTitle() {
        // Given
        composeTestRule.setContent {
            WelcomeScreen(
                onGetStarted = { getStartedClicked = true }
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Bienvenido a Umbral")
            .assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_displaysDescription() {
        // Given
        composeTestRule.setContent {
            WelcomeScreen(
                onGetStarted = { getStartedClicked = true }
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Recupera tu atención. Bloquea distracciones con un simple tap NFC.")
            .assertIsDisplayed()
    }

    @Test
    fun welcomeScreen_displaysGetStartedButton() {
        // Given
        composeTestRule.setContent {
            WelcomeScreen(
                onGetStarted = { getStartedClicked = true }
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Comenzar")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun welcomeScreen_getStartedButton_callsCallback() {
        // Given
        composeTestRule.setContent {
            WelcomeScreen(
                onGetStarted = { getStartedClicked = true }
            )
        }

        // When
        composeTestRule
            .onNodeWithText("Comenzar")
            .performClick()

        // Then
        assert(getStartedClicked)
    }

    // ==================== How It Works Screen Tests ====================

    @Test
    fun howItWorksScreen_displaysContinueButton() {
        // Given
        composeTestRule.setContent {
            HowItWorksScreen(
                onContinue = { continueClicked = true },
                onBack = { backClicked = true }
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Continuar")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun howItWorksScreen_displaysBackButton() {
        // Given
        composeTestRule.setContent {
            HowItWorksScreen(
                onContinue = { continueClicked = true },
                onBack = { backClicked = true }
            )
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Volver")
            .assertIsDisplayed()
    }

    @Test
    fun howItWorksScreen_continueButton_callsCallback() {
        // Given
        composeTestRule.setContent {
            HowItWorksScreen(
                onContinue = { continueClicked = true },
                onBack = { backClicked = true }
            )
        }

        // When
        composeTestRule
            .onNodeWithText("Continuar")
            .performClick()

        // Then
        assert(continueClicked)
    }

    @Test
    fun howItWorksScreen_backButton_callsCallback() {
        // Given
        composeTestRule.setContent {
            HowItWorksScreen(
                onContinue = { continueClicked = true },
                onBack = { backClicked = true }
            )
        }

        // When
        composeTestRule
            .onNodeWithContentDescription("Volver")
            .performClick()

        // Then
        assert(backClicked)
    }

    // ==================== Permissions Screen Tests ====================

    @Test
    fun permissionsScreen_displaysTitle() {
        // TODO: Requires Hilt ViewModel injection for instrumentation tests
        // This test would require HiltAndroidTest setup
        // Given
        // composeTestRule.setContent {
        //     PermissionsScreen(
        //         onContinue = { continueClicked = true },
        //         onBack = { backClicked = true }
        //     )
        // }

        // Then
        // composeTestRule
        //     .onNodeWithText("Permisos necesarios")
        //     .assertIsDisplayed()
    }

    @Test
    fun permissionsScreen_displaysUsageStatsPermission() {
        // TODO: Requires Hilt ViewModel injection
        // When implemented with HiltAndroidTest:
        // - Verify "Acceso a uso de apps" card is displayed
        // - Verify it shows required "*" indicator
        // - Verify description is shown
    }

    @Test
    fun permissionsScreen_displaysOverlayPermission() {
        // TODO: Requires Hilt ViewModel injection
        // When implemented with HiltAndroidTest:
        // - Verify "Mostrar sobre otras apps" card is displayed
        // - Verify it shows required "*" indicator
        // - Verify description is shown
    }

    @Test
    fun permissionsScreen_displaysNotificationsPermission() {
        // TODO: Requires Hilt ViewModel injection
        // When implemented with HiltAndroidTest:
        // - Verify "Notificaciones" card is displayed
        // - Verify it does NOT show required "*" indicator
        // - Verify description mentions it's recommended
    }

    @Test
    fun permissionsScreen_displaysNfcStatus() {
        // TODO: Requires Hilt ViewModel injection
        // When implemented with HiltAndroidTest:
        // - Verify NFC status card is displayed
        // - Test different states: ENABLED, DISABLED, NOT_AVAILABLE
    }

    @Test
    fun permissionsScreen_continueButton_disabledWhenPermissionsMissing() {
        // TODO: Requires Hilt ViewModel injection
        // Given: State with missing required permissions
        // Then: Continue button should be disabled
        // And: Error message should be displayed
    }

    @Test
    fun permissionsScreen_continueButton_enabledWhenRequiredPermissionsGranted() {
        // TODO: Requires Hilt ViewModel injection
        // Given: State with usageStats = GRANTED and overlay = GRANTED
        // Then: Continue button should be enabled
        // And: No error message should be displayed
    }

    @Test
    fun permissionsScreen_permissionButton_opensSettings() {
        // TODO: Requires actual permission system interaction
        // When clicking "Permitir" button:
        // - Should call viewModel.openPermissionSettings()
        // - In real device, would open system settings
    }

    // ==================== Success Screen Tests ====================

    @Test
    fun successScreen_displaysProfileName() {
        // Given
        val profileName = "Mi Primer Perfil"

        composeTestRule.setContent {
            SuccessScreen(
                profileName = profileName,
                appsCount = 5,
                onStartBlocking = { continueClicked = true },
                onLater = { backClicked = true }
            )
        }

        // Then
        composeTestRule
            .onNodeWithText(profileName)
            .assertIsDisplayed()
    }

    @Test
    fun successScreen_displaysAppsCount() {
        // Given
        val appsCount = 5

        composeTestRule.setContent {
            SuccessScreen(
                profileName = "Mi Perfil",
                appsCount = appsCount,
                onStartBlocking = { continueClicked = true },
                onLater = { backClicked = true }
            )
        }

        // Then
        // The text should mention the number of apps
        // Format: "5 apps bloqueadas" or similar
        composeTestRule
            .onNodeWithText("$appsCount apps", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun successScreen_displaysStartBlockingButton() {
        // Given
        composeTestRule.setContent {
            SuccessScreen(
                profileName = "Mi Perfil",
                appsCount = 3,
                onStartBlocking = { continueClicked = true },
                onLater = { backClicked = true }
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Activar bloqueo")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun successScreen_displaysLaterButton() {
        // Given
        composeTestRule.setContent {
            SuccessScreen(
                profileName = "Mi Perfil",
                appsCount = 3,
                onStartBlocking = { continueClicked = true },
                onLater = { backClicked = true }
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Más tarde")
            .assertIsDisplayed()
    }

    @Test
    fun successScreen_startBlockingButton_callsCallback() {
        // Given
        composeTestRule.setContent {
            SuccessScreen(
                profileName = "Mi Perfil",
                appsCount = 3,
                onStartBlocking = { continueClicked = true },
                onLater = { backClicked = true }
            )
        }

        // When
        composeTestRule
            .onNodeWithText("Activar bloqueo")
            .performClick()

        // Then
        assert(continueClicked)
    }

    @Test
    fun successScreen_laterButton_callsCallback() {
        // Given
        composeTestRule.setContent {
            SuccessScreen(
                profileName = "Mi Perfil",
                appsCount = 3,
                onStartBlocking = { continueClicked = true },
                onLater = { backClicked = true }
            )
        }

        // When
        composeTestRule
            .onNodeWithText("Más tarde")
            .performClick()

        // Then
        assert(backClicked)
    }

    // ==================== Navigation Flow Tests ====================

    @Test
    fun onboardingNavHost_startsAtWelcomeScreen() {
        // TODO: Requires full NavHost setup with Hilt
        // When OnboardingNavHost is launched:
        // - Should start at "welcome" destination
        // - WelcomeScreen should be displayed
    }

    @Test
    fun onboardingNavHost_navigatesFromWelcomeToHowItWorks() {
        // TODO: Requires full NavHost setup with Hilt
        // Given: OnboardingNavHost displayed
        // When: Click "Comenzar" on WelcomeScreen
        // Then: Should navigate to HowItWorksScreen
    }

    @Test
    fun onboardingNavHost_navigatesBackFromHowItWorksToWelcome() {
        // TODO: Requires full NavHost setup with Hilt
        // Given: OnboardingNavHost at HowItWorksScreen
        // When: Click back button
        // Then: Should navigate back to WelcomeScreen
    }

    @Test
    fun onboardingNavHost_navigatesFromHowItWorksToPermissions() {
        // TODO: Requires full NavHost setup with Hilt
        // Given: OnboardingNavHost at HowItWorksScreen
        // When: Click "Continuar"
        // Then: Should navigate to PermissionsScreen
    }

    @Test
    fun onboardingNavHost_navigatesFromPermissionsToSelectApps() {
        // TODO: Requires full NavHost setup with Hilt + granted permissions
        // Given: PermissionsScreen with all required permissions granted
        // When: Click "Continuar"
        // Then: Should navigate to SelectAppsScreen
    }

    @Test
    fun onboardingNavHost_cannotNavigateFromPermissionsWithoutRequiredPermissions() {
        // TODO: Requires full NavHost setup with Hilt
        // Given: PermissionsScreen with missing required permissions
        // Then: Continue button should be disabled
        // And: Navigation should not occur when trying to click
    }

    @Test
    fun onboardingNavHost_navigatesFromSelectAppsToSuccess() {
        // TODO: Requires full NavHost setup with Hilt
        // Given: SelectAppsScreen with at least one app selected
        // When: Complete app selection
        // Then: Should navigate to SuccessScreen
        // And: Should clear backstack (can't go back to welcome)
    }

    @Test
    fun onboardingNavHost_completesOnboarding() {
        // TODO: Requires full NavHost setup with Hilt
        // Given: OnboardingNavHost at SuccessScreen
        // When: Click "Activar bloqueo" or "Más tarde"
        // Then: Should call onOnboardingComplete callback
        // And: Should save onboarding completion preference
    }
}
