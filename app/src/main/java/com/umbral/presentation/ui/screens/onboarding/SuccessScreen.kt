package com.umbral.presentation.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.components.ButtonVariant
import com.umbral.presentation.ui.components.UmbralButton
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import kotlinx.coroutines.delay

@Composable
fun SuccessScreen(
    profileName: String,
    appsCount: Int,
    onStartBlocking: () -> Unit,
    onLater: () -> Unit
) {
    val context = LocalContext.current

    // Animation states
    var showIcon by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showSummary by remember { mutableStateOf(false) }
    var showButtons by remember { mutableStateOf(false) }

    // Staggered animation
    LaunchedEffect(Unit) {
        delay(200)
        showIcon = true
        delay(400)
        showTitle = true
        delay(200)
        showSummary = true
        delay(300)
        showButtons = true
    }

    // Prevent going back after completing onboarding
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

            // Animated checkmark icon
            AnimatedVisibility(
                visible = showIcon,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { -100 }
                        )
            ) {
                AnimatedSuccessIcon(
                    modifier = Modifier.size(120.dp)
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.xl))

            // Title
            AnimatedVisibility(
                visible = showTitle,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { 50 }
                        )
            ) {
                Text(
                    text = "¡Todo listo!",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.md))

            // Summary
            AnimatedVisibility(
                visible = showSummary,
                enter = fadeIn(animationSpec = tween(400))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tu perfil \"$profileName\" está configurado",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(UmbralSpacing.xs))

                    Text(
                        text = "$appsCount apps serán bloqueadas cuando te enfoques",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Buttons
            AnimatedVisibility(
                visible = showButtons,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            initialOffsetY = { 100 }
                        )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UmbralButton(
                        text = "Activar bloqueo ahora",
                        onClick = onStartBlocking,
                        fullWidth = true,
                        variant = ButtonVariant.Primary,
                        leadingIcon = Icons.Outlined.PlayArrow
                    )

                    Spacer(modifier = Modifier.height(UmbralSpacing.sm))

                    TextButton(onClick = onLater) {
                        Text(
                            text = "Más tarde",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.lg))
        }
    }
}

@Composable
private fun AnimatedSuccessIcon(
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0f) }
    val checkProgress = remember { Animatable(0f) }
    val primaryColor = MaterialTheme.colorScheme.primary
    val containerColor = MaterialTheme.colorScheme.primaryContainer

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        checkProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(400, easing = LinearEasing)
        )
    }

    Box(
        modifier = modifier
            .scale(scale.value)
            .background(
                containerColor,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Checkmark path animation
        Canvas(modifier = Modifier.size(56.dp)) {
            val path = Path().apply {
                moveTo(size.width * 0.2f, size.height * 0.5f)
                lineTo(size.width * 0.4f, size.height * 0.7f)
                lineTo(size.width * 0.8f, size.height * 0.3f)
            }

            val pathMeasure = PathMeasure()
            pathMeasure.setPath(path, false)

            val animatedPath = Path()
            pathMeasure.getSegment(
                0f,
                pathMeasure.length * checkProgress.value,
                animatedPath,
                true
            )

            drawPath(
                path = animatedPath,
                color = primaryColor,
                style = Stroke(
                    width = 6.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Success Screen - Light", showBackground = true)
@Composable
private fun SuccessScreenPreview() {
    UmbralTheme {
        SuccessScreen(
            profileName = "Trabajo",
            appsCount = 12,
            onStartBlocking = {},
            onLater = {}
        )
    }
}

@Preview(name = "Success Screen - Dark", showBackground = true)
@Composable
private fun SuccessScreenDarkPreview() {
    UmbralTheme(darkTheme = true) {
        SuccessScreen(
            profileName = "Estudio",
            appsCount = 8,
            onStartBlocking = {},
            onLater = {}
        )
    }
}

@Preview(name = "Success Icon Animation", showBackground = true)
@Composable
private fun AnimatedSuccessIconPreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            AnimatedSuccessIcon(
                modifier = Modifier.size(120.dp)
            )
        }
    }
}
