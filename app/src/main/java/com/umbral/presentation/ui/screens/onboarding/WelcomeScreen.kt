package com.umbral.presentation.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.components.ButtonVariant
import com.umbral.presentation.ui.components.UmbralButton
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import kotlinx.coroutines.delay

/**
 * Welcome Screen - First impression of Umbral
 *
 * Clean, minimal design with animated entrance.
 * Focus on the core value proposition: "Tu espacio de enfoque"
 */
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    val context = LocalContext.current

    // Animation states
    var showLogo by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Staggered entrance animation
    LaunchedEffect(Unit) {
        delay(200)
        showLogo = true
        delay(300)
        showTitle = true
        delay(200)
        showSubtitle = true
        delay(300)
        showButton = true
    }

    // Handle physical back button - exit app instead of going back
    BackHandler {
        (context as? android.app.Activity)?.finish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = UmbralSpacing.screenHorizontal)
                .padding(vertical = UmbralSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Hero Logo with animation
            AnimatedVisibility(
                visible = showLogo,
                enter = fadeIn(animationSpec = tween(600)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { -100 }
                        )
            ) {
                UmbralHeroLogo()
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.xl))

            // Title
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(animationSpec = tween(500)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { 50 }
                        )
            ) {
                Text(
                    text = "Umbral",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.sm))

            // Subtitle
            AnimatedVisibility(
                visible = showSubtitle,
                enter = fadeIn(animationSpec = tween(500))
            ) {
                Text(
                    text = "Tu espacio de enfoque",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Description
            AnimatedVisibility(
                visible = showSubtitle,
                enter = fadeIn(animationSpec = tween(500))
            ) {
                Text(
                    text = "Bloquea distracciones con un simple tap NFC.\nRecupera tu atención, un momento a la vez.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = UmbralSpacing.md)
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.xxl))

            // CTA Button
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            initialOffsetY = { 100 }
                        )
            ) {
                UmbralButton(
                    text = "Comenzar",
                    onClick = onGetStarted,
                    fullWidth = true,
                    variant = ButtonVariant.Primary
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.lg))
        }
    }
}

@Composable
private fun UmbralHeroLogo(
    modifier: Modifier = Modifier
) {
    var isAnimating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isAnimating = true
    }

    val scale by animateFloatAsState(
        targetValue = if (isAnimating) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    Box(
        modifier = modifier
            .size(140.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        // Inner circle
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Shield,
                contentDescription = "Umbral Logo",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Welcome Screen - Light", showBackground = true)
@Composable
private fun WelcomeScreenPreview() {
    UmbralTheme {
        WelcomeScreen(onGetStarted = {})
    }
}

@Preview(name = "Welcome Screen - Dark", showBackground = true)
@Composable
private fun WelcomeScreenDarkPreview() {
    UmbralTheme(darkTheme = true) {
        WelcomeScreen(onGetStarted = {})
    }
}
