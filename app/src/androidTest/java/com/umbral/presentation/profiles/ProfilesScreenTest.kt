package com.umbral.presentation.profiles

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.presentation.ui.screens.profiles.ProfilesScreen
import com.umbral.presentation.viewmodel.ProfilesUiState
import com.umbral.presentation.viewmodel.ProfilesViewModel
import io.mockk.*
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

/**
 * UI Tests for ProfilesScreen using Jetpack Compose Testing.
 *
 * NOTE: These tests use MockK to avoid Hilt dependency injection complexity.
 * They focus on UI behavior and user interactions.
 *
 * TODO: To run on device/emulator, ensure:
 * - Device API 26+ (Android 8.0+)
 * - USB Debugging enabled
 * - Run: ./gradlew connectedAndroidTest
 */
class ProfilesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: ProfilesViewModel
    private lateinit var uiStateFlow: MutableStateFlow<ProfilesUiState>
    private var navigateToDetailCalled = false
    private var navigatedProfileId: String? = null

    @Before
    fun setup() {
        // Reset navigation state
        navigateToDetailCalled = false
        navigatedProfileId = null

        // Create mock ViewModel
        mockViewModel = mockk(relaxed = true)
        uiStateFlow = MutableStateFlow(ProfilesUiState())

        every { mockViewModel.uiState } returns uiStateFlow
    }

    // ============================================
    // LOADING STATE TESTS
    // ============================================

    @Test
    fun profilesScreen_whenLoading_showsLoadingIndicator() {
        // Given: Loading state
        uiStateFlow.value = ProfilesUiState(isLoading = true, profiles = emptyList())

        // When: Screen is displayed
        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // Then: Loading indicator is shown
        composeTestRule.onNodeWithTag("loading_indicator", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun profilesScreen_whenLoading_hidesOtherContent() {
        // Given: Loading state
        uiStateFlow.value = ProfilesUiState(isLoading = true, profiles = emptyList())

        // When: Screen is displayed
        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // Then: Profile list and empty state are not shown
        composeTestRule.onNodeWithText("No hay perfiles creados")
            .assertDoesNotExist()
    }

    // ============================================
    // EMPTY STATE TESTS
    // ============================================

    @Test
    fun profilesList_whenEmpty_showsEmptyState() {
        // Given: No profiles
        uiStateFlow.value = ProfilesUiState(isLoading = false, profiles = emptyList())

        // When: Screen is displayed
        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // Then: Empty state is shown
        composeTestRule.onNodeWithText("No hay perfiles creados")
            .assertIsDisplayed()
    }

    @Test
    fun emptyState_showsCreateProfileButton() {
        // Given: No profiles
        uiStateFlow.value = ProfilesUiState(isLoading = false, profiles = emptyList())

        // When: Screen is displayed
        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // Then: Create button is shown
        composeTestRule.onNodeWithText("Crear perfil")
            .assertIsDisplayed()
    }

    @Test
    fun emptyState_createButtonClickCallsViewModel() {
        // Given: No profiles
        uiStateFlow.value = ProfilesUiState(isLoading = false, profiles = emptyList())

        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // When: Create button is clicked
        composeTestRule.onNodeWithText("Crear perfil")
            .performClick()

        // Then: ViewModel method is called
        verify { mockViewModel.createDefaultProfile() }
    }

    // ============================================
    // PROFILE LIST TESTS
    // ============================================

    @Test
    fun profilesList_showsAllProfiles() {
        // Given: Multiple profiles
        val profiles = listOf(
            createTestProfile(id = "1", name = "Perfil Trabajo"),
            createTestProfile(id = "2", name = "Perfil Casa"),
            createTestProfile(id = "3", name = "Perfil Estudio")
        )
        uiStateFlow.value = ProfilesUiState(isLoading = false, profiles = profiles)

        // When: Screen is displayed
        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // Then: All profiles are shown
        composeTestRule.onNodeWithText("Perfil Trabajo").assertIsDisplayed()
        composeTestRule.onNodeWithText("Perfil Casa").assertIsDisplayed()
        composeTestRule.onNodeWithText("Perfil Estudio").assertIsDisplayed()
    }

    @Test
    fun profileCard_showsProfileDetails() {
        // Given: Profile with 5 blocked apps
        val profile = createTestProfile(
            name = "Mi Perfil",
            blockedApps = listOf("com.app1", "com.app2", "com.app3", "com.app4", "com.app5")
        )
        uiStateFlow.value = ProfilesUiState(isLoading = false, profiles = listOf(profile))

        // When: Screen is displayed
        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // Then: Profile details are shown
        composeTestRule.onNodeWithText("Mi Perfil").assertIsDisplayed()
        composeTestRule.onNodeWithText("5 apps bloqueadas").assertIsDisplayed()
    }

    @Test
    fun profileCard_whenActive_showsActiveIndicator() {
        // Given: Active profile
        val profile = createTestProfile(name = "Perfil Activo", isActive = true)
        uiStateFlow.value = ProfilesUiState(isLoading = false, profiles = listOf(profile))

        // When: Screen is displayed
        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // Then: Active indicator is shown
        composeTestRule.onNodeWithContentDescription("Activo")
            .assertIsDisplayed()
    }

    @Test
    fun profileCard_whenInactive_hidesActiveIndicator() {
        // Given: Inactive profile
        val profile = createTestProfile(name = "Perfil Inactivo", isActive = false)
        uiStateFlow.value = ProfilesUiState(isLoading = false, profiles = listOf(profile))

        // When: Screen is displayed
        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // Then: Active indicator is not shown
        composeTestRule.onNodeWithContentDescription("Activo")
            .assertDoesNotExist()
    }

    @Test
    fun profileCard_whenStrictMode_showsStrictModeLabel() {
        // Given: Profile with strict mode enabled
        val profile = createTestProfile(name = "Perfil Estricto", isStrictMode = true)
        uiStateFlow.value = ProfilesUiState(isLoading = false, profiles = listOf(profile))

        // When: Screen is displayed
        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // Then: Strict mode label is shown
        composeTestRule.onNodeWithText("Modo estricto")
            .assertIsDisplayed()
    }

    // ============================================
    // NAVIGATION TESTS
    // ============================================

    @Test
    fun fabButton_navigatesToCreateProfile() {
        // Given: Screen with profiles
        val profile = createTestProfile()
        uiStateFlow.value = ProfilesUiState(isLoading = false, profiles = listOf(profile))

        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { profileId ->
                    navigateToDetailCalled = true
                    navigatedProfileId = profileId
                }
            )
        }

        // When: FAB is clicked
        composeTestRule.onNodeWithContentDescription("Crear perfil")
            .performClick()

        // Then: Navigation is called with "new"
        assert(navigateToDetailCalled)
        assert(navigatedProfileId == "new")
    }

    @Test
    fun profileCard_clickNavigatesToProfileDetail() {
        // Given: Screen with a profile
        val profile = createTestProfile(id = "test-profile-123")
        uiStateFlow.value = ProfilesUiState(isLoading = false, profiles = listOf(profile))

        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { profileId ->
                    navigateToDetailCalled = true
                    navigatedProfileId = profileId
                }
            )
        }

        // When: Profile card is clicked
        composeTestRule.onNodeWithText("Test Profile")
            .performClick()

        // Then: Navigation is called with profile ID
        assert(navigateToDetailCalled)
        assert(navigatedProfileId == "test-profile-123")
    }

    // ============================================
    // TOGGLE PROFILE TESTS
    // ============================================

    @Test
    fun profileToggle_whenInactive_callsActivateProfile() {
        // Given: Inactive profile
        val profile = createTestProfile(id = "profile-1", isActive = false)
        uiStateFlow.value = ProfilesUiState(isLoading = false, profiles = listOf(profile))

        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // When: Toggle is clicked
        composeTestRule.onNode(hasContentDescription("Toggle profile status"))
            .performClick()

        // Then: Activate is called
        verify { mockViewModel.activateProfile("profile-1") }
    }

    @Test
    fun profileToggle_whenActive_callsDeactivateProfile() {
        // Given: Active profile
        val profile = createTestProfile(id = "profile-1", isActive = true)
        uiStateFlow.value = ProfilesUiState(isLoading = false, profiles = listOf(profile))

        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // When: Toggle is clicked
        composeTestRule.onNode(hasContentDescription("Toggle profile status"))
            .performClick()

        // Then: Deactivate is called
        verify { mockViewModel.deactivateProfile("profile-1") }
    }

    // ============================================
    // DELETE PROFILE TESTS
    // ============================================

    @Test
    fun deleteButton_clickShowsDeleteDialog() {
        // Given: Profile in the list
        val profile = createTestProfile(name = "Perfil a Eliminar")
        uiStateFlow.value = ProfilesUiState(isLoading = false, profiles = listOf(profile))

        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // When: Delete button is clicked
        composeTestRule.onNodeWithContentDescription("Eliminar")
            .performClick()

        // Then: ViewModel method is called to show dialog
        verify { mockViewModel.showDeleteDialog(profile) }
    }

    @Test
    fun deleteDialog_showsProfileName() {
        // Given: Delete dialog is shown
        val profile = createTestProfile(name = "Perfil Importante")
        uiStateFlow.value = ProfilesUiState(
            isLoading = false,
            profiles = listOf(profile),
            selectedProfile = profile,
            showDeleteDialog = true
        )

        // When: Screen is displayed
        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // Then: Dialog shows profile name
        composeTestRule.onNodeWithText("Eliminar perfil")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("¿Estás seguro de que quieres eliminar \"Perfil Importante\"? Esta acción no se puede deshacer.")
            .assertIsDisplayed()
    }

    @Test
    fun deleteDialog_confirmButtonCallsDeleteProfile() {
        // Given: Delete dialog is shown
        val profile = createTestProfile(name = "Perfil a Borrar")
        uiStateFlow.value = ProfilesUiState(
            isLoading = false,
            profiles = listOf(profile),
            selectedProfile = profile,
            showDeleteDialog = true
        )

        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // When: Confirm button is clicked
        composeTestRule.onNodeWithText("Eliminar")
            .performClick()

        // Then: Delete is called
        verify { mockViewModel.deleteProfile() }
    }

    @Test
    fun deleteDialog_cancelButtonHidesDialog() {
        // Given: Delete dialog is shown
        val profile = createTestProfile(name = "Perfil Cancelado")
        uiStateFlow.value = ProfilesUiState(
            isLoading = false,
            profiles = listOf(profile),
            selectedProfile = profile,
            showDeleteDialog = true
        )

        composeTestRule.setContent {
            ProfilesScreen(
                viewModel = mockViewModel,
                onNavigateToProfileDetail = { }
            )
        }

        // When: Cancel button is clicked
        composeTestRule.onNodeWithText("Cancelar")
            .performClick()

        // Then: Hide dialog is called
        verify { mockViewModel.hideDeleteDialog() }
    }

    // ============================================
    // HELPER FUNCTIONS
    // ============================================

    private fun createTestProfile(
        id: String = "test-profile-id",
        name: String = "Test Profile",
        isActive: Boolean = false,
        isStrictMode: Boolean = false,
        blockedApps: List<String> = emptyList()
    ) = BlockingProfile(
        id = id,
        name = name,
        iconName = "shield",
        colorHex = "#6650A4",
        isActive = isActive,
        isStrictMode = isStrictMode,
        blockedApps = blockedApps,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}
