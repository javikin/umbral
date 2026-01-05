package com.umbral.presentation.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AppBlocking
import androidx.compose.material.icons.outlined.CenterFocusStrong
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.components.ButtonVariant
import com.umbral.presentation.ui.components.UmbralButton
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import kotlinx.coroutines.delay

/**
 * How It Works Screen - Explains the 3-step process
 *
 * Sequential animated steps showing:
 * 1. Select apps to block
 * 2. Configure NFC tag
 * 3. Focus without distractions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowItWorksScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    // Animation states for each step
    var showStep1 by remember { mutableStateOf(false) }
    var showStep2 by remember { mutableStateOf(false) }
    var showStep3 by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Staggered animation
    LaunchedEffect(Unit) {
        delay(200)
        showStep1 = true
        delay(300)
        showStep2 = true
        delay(300)
        showStep3 = true
        delay(400)
        showButton = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = UmbralSpacing.screenHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(UmbralSpacing.lg))

            // Header
            Text(
                text = "¿Cómo funciona?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(UmbralSpacing.sm))

            Text(
                text = "Solo 3 pasos para recuperar tu enfoque",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(UmbralSpacing.xxl))

            // Steps
            AnimatedVisibility(
                visible = showStep1,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { 50 }
                        )
            ) {
                StepItem(
                    stepNumber = 1,
                    icon = Icons.Outlined.AppBlocking,
                    title = "Elige las apps",
                    description = "Selecciona qué apps quieres bloquear cuando te enfoques"
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.lg))

            AnimatedVisibility(
                visible = showStep2,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { 50 }
                        )
            ) {
                StepItem(
                    stepNumber = 2,
                    icon = Icons.Outlined.Nfc,
                    title = "Configura tu tag",
                    description = "Programa un tag NFC o genera un código QR"
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.lg))

            AnimatedVisibility(
                visible = showStep3,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { 50 }
                        )
            ) {
                StepItem(
                    stepNumber = 3,
                    icon = Icons.Outlined.CenterFocusStrong,
                    title = "¡Listo!",
                    description = "Toca tu tag y enfócate sin distracciones"
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue button
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
                    text = "Continuar",
                    onClick = onContinue,
                    fullWidth = true,
                    variant = ButtonVariant.Primary
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.xl))
        }
    }
}

@Composable
private fun StepItem(
    stepNumber: Int,
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.large
            )
            .padding(UmbralSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Step number with icon
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(UmbralSpacing.md))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Step number badge
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$stepNumber",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(UmbralSpacing.sm))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "How It Works - Light", showBackground = true)
@Composable
private fun HowItWorksScreenPreview() {
    UmbralTheme {
        HowItWorksScreen(
            onContinue = {},
            onBack = {}
        )
    }
}

@Preview(name = "How It Works - Dark", showBackground = true)
@Composable
private fun HowItWorksScreenDarkPreview() {
    UmbralTheme(darkTheme = true) {
        HowItWorksScreen(
            onContinue = {},
            onBack = {}
        )
    }
}
