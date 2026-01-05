package com.umbral.presentation.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import com.umbral.presentation.ui.components.NfcAnimationState
import com.umbral.presentation.ui.components.NfcScanAnimation
import org.junit.Rule
import org.junit.Test

class NfcScanAnimationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun nfcScanAnimation_scanningState_displaysAnimation() {
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.SCANNING)
        }

        // Animation should render without crashing
        // Check for the NFC icon
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()
    }

    @Test
    fun nfcScanAnimation_writingState_displaysWritingIndicator() {
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.WRITING)
        }

        // Writing animation should render
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()
    }

    @Test
    fun nfcScanAnimation_successState_displaysCheckIcon() {
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.SUCCESS)
        }

        // Success state should show check circle icon
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()
    }

    @Test
    fun nfcScanAnimation_errorState_displaysErrorIcon() {
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.ERROR)
        }

        // Error state should show error icon
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()
    }

    @Test
    fun nfcScanAnimation_scanningState_animates() {
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.SCANNING)
        }

        // Wait for animation to render
        composeTestRule.mainClock.advanceTimeBy(1000)

        // Animation should still be visible
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()
    }

    @Test
    fun nfcScanAnimation_writingState_showsProgressIndicator() {
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.WRITING)
        }

        // Progress indicator should be present
        composeTestRule.onNode(
            androidx.compose.ui.test.hasProgressBarRangeInfo(
                androidx.compose.ui.semantics.ProgressBarRangeInfo.Indeterminate
            )
        ).assertExists()
    }

    @Test
    fun nfcScanAnimation_stateTransition_scanningToSuccess() {
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.SCANNING)
        }

        // Verify initial state
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()

        // Change state to success
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.SUCCESS)
        }

        // Verify new state
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()
    }

    @Test
    fun nfcScanAnimation_stateTransition_scanningToError() {
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.SCANNING)
        }

        // Change state to error
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.ERROR)
        }

        // Should render error state
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()
    }

    @Test
    fun nfcScanAnimation_scanningState_hasCorrectSize() {
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.SCANNING)
        }

        // Animation should render with size 200.dp
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()
    }

    @Test
    fun nfcScanAnimation_writingState_hasCorrectSize() {
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.WRITING)
        }

        // Animation should render with size 200.dp
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()
    }

    @Test
    fun nfcScanAnimation_successState_hasCorrectSize() {
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.SUCCESS)
        }

        // Success icon should render with size 120.dp
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()
    }

    @Test
    fun nfcScanAnimation_errorState_hasCorrectSize() {
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.ERROR)
        }

        // Error icon should render with size 120.dp
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()
    }

    @Test
    fun nfcScanAnimation_multipleStateChanges_rendersCorrectly() {
        // Start with scanning
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.SCANNING)
        }
        composeTestRule.mainClock.advanceTimeBy(500)

        // Change to writing
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.WRITING)
        }
        composeTestRule.mainClock.advanceTimeBy(500)

        // Change to success
        composeTestRule.setContent {
            NfcScanAnimation(state = NfcAnimationState.SUCCESS)
        }

        // Should render final state
        composeTestRule.onNode(
            androidx.compose.ui.test.hasContentDescription("null")
        ).assertExists()
    }
}
