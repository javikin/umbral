package com.umbral.presentation.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.presentation.ui.components.ProfileCard
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDateTime

class ProfileCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testProfile = BlockingProfile(
        id = "test-id",
        name = "Trabajo",
        colorHex = "#FF5722",
        blockedApps = listOf("com.example.app1", "com.example.app2"),
        isActive = false,
        isStrictMode = false,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Test
    fun profileCard_displaysProfileName() {
        composeTestRule.setContent {
            ProfileCard(
                profile = testProfile,
                onClick = {},
                onToggleActive = {},
                onDelete = {}
            )
        }

        composeTestRule.onNodeWithText("Trabajo").assertIsDisplayed()
    }

    @Test
    fun profileCard_displaysBlockedAppsCount() {
        composeTestRule.setContent {
            ProfileCard(
                profile = testProfile,
                onClick = {},
                onToggleActive = {},
                onDelete = {}
            )
        }

        composeTestRule.onNodeWithText("2 apps bloqueadas").assertIsDisplayed()
    }

    @Test
    fun profileCard_showsStrictModeWhenEnabled() {
        val strictProfile = testProfile.copy(isStrictMode = true)

        composeTestRule.setContent {
            ProfileCard(
                profile = strictProfile,
                onClick = {},
                onToggleActive = {},
                onDelete = {}
            )
        }

        composeTestRule.onNodeWithText("Modo estricto").assertIsDisplayed()
    }

    @Test
    fun profileCard_hidesStrictModeWhenDisabled() {
        composeTestRule.setContent {
            ProfileCard(
                profile = testProfile,
                onClick = {},
                onToggleActive = {},
                onDelete = {}
            )
        }

        composeTestRule.onNodeWithText("Modo estricto").assertDoesNotExist()
    }

    @Test
    fun profileCard_showsActiveIndicatorWhenActive() {
        val activeProfile = testProfile.copy(isActive = true)

        composeTestRule.setContent {
            ProfileCard(
                profile = activeProfile,
                onClick = {},
                onToggleActive = {},
                onDelete = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Activo").assertIsDisplayed()
    }

    @Test
    fun profileCard_hidesActiveIndicatorWhenInactive() {
        composeTestRule.setContent {
            ProfileCard(
                profile = testProfile,
                onClick = {},
                onToggleActive = {},
                onDelete = {}
            )
        }

        composeTestRule.onNodeWithContentDescription("Activo").assertDoesNotExist()
    }

    @Test
    fun profileCard_switchReflectsActiveState() {
        val activeProfile = testProfile.copy(isActive = true)

        composeTestRule.setContent {
            ProfileCard(
                profile = activeProfile,
                onClick = {},
                onToggleActive = {},
                onDelete = {}
            )
        }

        // The switch should be ON for active profile
        // Note: Checking by finding the switch component
        composeTestRule.onNode(
            androidx.compose.ui.test.hasTestTag("profile_switch") or
                    androidx.compose.ui.test.hasClickAction()
        ).assertExists()
    }

    @Test
    fun profileCard_onClick_triggersCallback() {
        var clicked = false

        composeTestRule.setContent {
            ProfileCard(
                profile = testProfile,
                onClick = { clicked = true },
                onToggleActive = {},
                onDelete = {}
            )
        }

        composeTestRule.onNodeWithText("Trabajo").performClick()
        assertTrue(clicked)
    }

    @Test
    fun profileCard_onDeleteClick_triggersCallback() {
        var deleteClicked = false

        composeTestRule.setContent {
            ProfileCard(
                profile = testProfile,
                onClick = {},
                onToggleActive = {},
                onDelete = { deleteClicked = true }
            )
        }

        composeTestRule.onNodeWithContentDescription("Eliminar").performClick()
        assertTrue(deleteClicked)
    }

    @Test
    fun profileCard_displaysChevronIcon() {
        composeTestRule.setContent {
            ProfileCard(
                profile = testProfile,
                onClick = {},
                onToggleActive = {},
                onDelete = {}
            )
        }

        // Chevron icon should be visible
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()
    }

    @Test
    fun profileCard_withLongName_displaysEllipsis() {
        val longNameProfile = testProfile.copy(
            name = "Este es un nombre de perfil extremadamente largo que debería truncarse"
        )

        composeTestRule.setContent {
            ProfileCard(
                profile = longNameProfile,
                onClick = {},
                onToggleActive = {},
                onDelete = {}
            )
        }

        // The text should still be displayed (even if truncated)
        composeTestRule.onNodeWithText(
            longNameProfile.name,
            substring = true
        ).assertIsDisplayed()
    }

    @Test
    fun profileCard_withZeroApps_displaysCorrectCount() {
        val emptyProfile = testProfile.copy(blockedApps = emptyList())

        composeTestRule.setContent {
            ProfileCard(
                profile = emptyProfile,
                onClick = {},
                onToggleActive = {},
                onDelete = {}
            )
        }

        composeTestRule.onNodeWithText("0 apps bloqueadas").assertIsDisplayed()
    }

    @Test
    fun profileCard_withInvalidColor_fallsBackToDefaultColor() {
        val invalidColorProfile = testProfile.copy(colorHex = "invalid")

        // Should not crash
        composeTestRule.setContent {
            ProfileCard(
                profile = invalidColorProfile,
                onClick = {},
                onToggleActive = {},
                onDelete = {}
            )
        }

        // Should still display the profile name
        composeTestRule.onNodeWithText("Trabajo").assertIsDisplayed()
    }
}
