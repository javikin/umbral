package com.umbral.presentation.ui.components

import android.os.Build
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView

/**
 * Umbral Haptic Feedback System
 *
 * Provides consistent haptic feedback throughout the app.
 * Falls back gracefully on devices that don't support certain haptic types.
 */
object UmbralHaptics {

    /**
     * Perform a light click haptic feedback.
     * Use for button presses, toggles, and minor interactions.
     */
    @Composable
    fun rememberClickFeedback(): () -> Unit {
        val haptic = LocalHapticFeedback.current
        return remember(haptic) {
            { haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
        }
    }

    /**
     * Perform a confirmation haptic feedback.
     * Use for successful actions, confirmations, and completions.
     */
    @Composable
    fun rememberSuccessFeedback(): () -> Unit {
        val view = LocalView.current
        return remember(view) {
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                } else {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
            }
        }
    }

    /**
     * Perform a rejection/error haptic feedback.
     * Use for failed actions, errors, and blocked states.
     */
    @Composable
    fun rememberErrorFeedback(): () -> Unit {
        val view = LocalView.current
        return remember(view) {
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    view.performHapticFeedback(HapticFeedbackConstants.REJECT)
                } else {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                }
            }
        }
    }

    /**
     * Perform a heavy/long press haptic feedback.
     * Use for important actions, context menus, and destructive actions.
     */
    @Composable
    fun rememberLongPressFeedback(): () -> Unit {
        val haptic = LocalHapticFeedback.current
        return remember(haptic) {
            { haptic.performHapticFeedback(HapticFeedbackType.LongPress) }
        }
    }

    /**
     * Perform a selection change haptic feedback.
     * Use for picker wheels, sliders, and selections.
     */
    @Composable
    fun rememberSelectionFeedback(): () -> Unit {
        val view = LocalView.current
        return remember(view) {
            {
                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            }
        }
    }

    /**
     * Perform haptic feedback for a toggle change.
     * Slightly different feeling for on vs off.
     */
    @Composable
    fun rememberToggleFeedback(): (Boolean) -> Unit {
        val view = LocalView.current
        return remember(view) {
            { isOn: Boolean ->
                if (isOn) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                    } else {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    }
                } else {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                }
            }
        }
    }

    /**
     * Perform haptic feedback directly on a View.
     * Useful for non-composable contexts.
     */
    object ViewHaptics {
        fun click(view: View) {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }

        fun success(view: View) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            } else {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            }
        }

        fun error(view: View) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                view.performHapticFeedback(HapticFeedbackConstants.REJECT)
            } else {
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
            }
        }

        fun longPress(view: View) {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }

        fun selection(view: View) {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        }
    }
}

/**
 * Extension function to easily add haptic feedback to click modifiers
 */
@Composable
fun rememberHapticClick(onClick: () -> Unit): () -> Unit {
    val hapticFeedback = UmbralHaptics.rememberClickFeedback()
    return remember(onClick, hapticFeedback) {
        {
            hapticFeedback()
            onClick()
        }
    }
}
