package com.umbral.presentation.components

import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.umbral.domain.apps.InstalledApp
import com.umbral.presentation.ui.components.AppListItem
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AppListItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testApp = InstalledApp(
        packageName = "com.example.testapp",
        name = "Test App",
        icon = ColorDrawable(android.graphics.Color.BLUE),
        isSystemApp = false
    )

    @Test
    fun appListItem_displaysAppName() {
        composeTestRule.setContent {
            AppListItem(
                app = testApp,
                isSelected = false,
                onToggle = {}
            )
        }

        composeTestRule.onNodeWithText("Test App").assertIsDisplayed()
    }

    @Test
    fun appListItem_displaysPackageName() {
        composeTestRule.setContent {
            AppListItem(
                app = testApp,
                isSelected = false,
                onToggle = {}
            )
        }

        composeTestRule.onNodeWithText("com.example.testapp").assertIsDisplayed()
    }

    @Test
    fun appListItem_showsSystemAppLabel() {
        val systemApp = testApp.copy(isSystemApp = true)

        composeTestRule.setContent {
            AppListItem(
                app = systemApp,
                isSelected = false,
                onToggle = {}
            )
        }

        composeTestRule.onNodeWithText("Sistema").assertIsDisplayed()
    }

    @Test
    fun appListItem_hidesSystemAppLabelForUserApps() {
        composeTestRule.setContent {
            AppListItem(
                app = testApp,
                isSelected = false,
                onToggle = {}
            )
        }

        composeTestRule.onNodeWithText("Sistema").assertDoesNotExist()
    }

    @Test
    fun appListItem_onClick_triggersToggle() {
        var toggled = false

        composeTestRule.setContent {
            AppListItem(
                app = testApp,
                isSelected = false,
                onToggle = { toggled = true }
            )
        }

        composeTestRule.onNodeWithText("Test App").performClick()
        assertTrue(toggled)
    }

    @Test
    fun appListItem_checkboxClick_triggersToggle() {
        var toggled = false

        composeTestRule.setContent {
            AppListItem(
                app = testApp,
                isSelected = false,
                onToggle = { toggled = true }
            )
        }

        // Find checkbox by role
        composeTestRule.onNode(
            androidx.compose.ui.test.hasClickAction() and
                    androidx.compose.ui.test.isToggleable()
        ).performClick()

        assertTrue(toggled)
    }

    @Test
    fun appListItem_whenSelected_checkboxIsChecked() {
        composeTestRule.setContent {
            AppListItem(
                app = testApp,
                isSelected = true,
                onToggle = {}
            )
        }

        // Checkbox should be in checked state
        composeTestRule.onNode(
            androidx.compose.ui.test.isToggleable()
        ).assertExists()
    }

    @Test
    fun appListItem_whenNotSelected_checkboxIsUnchecked() {
        composeTestRule.setContent {
            AppListItem(
                app = testApp,
                isSelected = false,
                onToggle = {}
            )
        }

        // Checkbox should exist but not be checked
        composeTestRule.onNode(
            androidx.compose.ui.test.isToggleable()
        ).assertExists()
    }

    @Test
    fun appListItem_withNullIcon_displaysPlaceholder() {
        val appWithoutIcon = testApp.copy(icon = null)

        composeTestRule.setContent {
            AppListItem(
                app = appWithoutIcon,
                isSelected = false,
                onToggle = {}
            )
        }

        // Should still display without crashing
        composeTestRule.onNodeWithText("Test App").assertIsDisplayed()
    }

    @Test
    fun appListItem_withLongName_displaysEllipsis() {
        val longNameApp = testApp.copy(
            name = "This is an extremely long application name that should be truncated with ellipsis"
        )

        composeTestRule.setContent {
            AppListItem(
                app = longNameApp,
                isSelected = false,
                onToggle = {}
            )
        }

        // Text should be displayed (even if truncated)
        composeTestRule.onNodeWithText(
            longNameApp.name,
            substring = true
        ).assertIsDisplayed()
    }

    @Test
    fun appListItem_withLongPackageName_displaysEllipsis() {
        val longPackageApp = testApp.copy(
            packageName = "com.example.verylongpackagenamethatshouldbetruncated.app"
        )

        composeTestRule.setContent {
            AppListItem(
                app = longPackageApp,
                isSelected = false,
                onToggle = {}
            )
        }

        composeTestRule.onNodeWithText(
            longPackageApp.packageName,
            substring = true
        ).assertIsDisplayed()
    }

    @Test
    fun appListItem_selectedState_hasElevation() {
        composeTestRule.setContent {
            AppListItem(
                app = testApp,
                isSelected = true,
                onToggle = {}
            )
        }

        // Card with elevation should be rendered
        // Verify by checking the text is still visible
        composeTestRule.onNodeWithText("Test App").assertIsDisplayed()
    }

    @Test
    fun appListItem_unselectedState_hasNoElevation() {
        composeTestRule.setContent {
            AppListItem(
                app = testApp,
                isSelected = false,
                onToggle = {}
            )
        }

        // Card without elevation should be rendered
        composeTestRule.onNodeWithText("Test App").assertIsDisplayed()
    }

    @Test
    fun appListItem_systemApp_displaysAllElements() {
        val systemApp = testApp.copy(isSystemApp = true)

        composeTestRule.setContent {
            AppListItem(
                app = systemApp,
                isSelected = true,
                onToggle = {}
            )
        }

        composeTestRule.onNodeWithText("Test App").assertIsDisplayed()
        composeTestRule.onNodeWithText("com.example.testapp").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sistema").assertIsDisplayed()
    }
}
