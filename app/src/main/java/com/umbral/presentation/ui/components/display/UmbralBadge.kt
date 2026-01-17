package com.umbral.presentation.ui.components.display

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.*

/**
 * Umbral Design System 2.0 - Badge Component
 *
 * Compact indicators for notification counts and status labels.
 *
 * ## Usage
 * ```kotlin
 * // Notification count badge
 * UmbralBadge(content = "5", variant = BadgeVariant.Default)
 *
 * // Status badge
 * UmbralBadge(content = "Nuevo", variant = BadgeVariant.Success)
 *
 * // Overflow badge
 * UmbralBadge(content = "99+", variant = BadgeVariant.Error)
 *
 * // Dot indicator (no text)
 * UmbralDotBadge(variant = BadgeVariant.Warning)
 * ```
 *
 * ## Variants
 * - Default: Accent color for general notifications
 * - Success: Positive status indicators
 * - Warning: Attention-needed indicators
 * - Error: Critical notifications
 * - Neutral: Subtle status labels
 *
 * ## Accessibility
 * - High contrast text/background ratios
 * - Minimum touch target size met via parent container
 * - Use semantic variants for meaning
 *
 * ## Performance
 * - Animated appearance with springBouncy
 * - Content changes animate smoothly
 * - Numbers > 99 formatted as "99+"
 */

/**
 * Badge visual variant defining color scheme
 */
enum class BadgeVariant {
    /** Accent color (sage teal) - general notifications */
    Default,

    /** Success color (green) - positive status */
    Success,

    /** Warning color (orange) - needs attention */
    Warning,

    /** Error color (red) - critical notifications */
    Error,

    /** Neutral gray - subtle status labels */
    Neutral
}

/**
 * Primary badge component for counts and status labels
 *
 * @param content Text content to display (auto-formats "99+" for large numbers)
 * @param modifier Modifier for positioning and layout
 * @param variant Visual style variant (Default, Success, Warning, Error, Neutral)
 */
@Composable
fun UmbralBadge(
    content: String,
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default
) {
    val colors = getBadgeColors(variant)

    // Format content (convert numbers > 99 to "99+")
    val formattedContent = remember(content) {
        content.toIntOrNull()?.let { count ->
            if (count > 99) "99+" else count.toString()
        } ?: content
    }

    // Animate appearance
    AnimatedVisibility(
        visible = true,
        enter = scaleIn(
            animationSpec = UmbralMotion.springBouncy(),
            initialScale = 0.8f
        ) + fadeIn(
            animationSpec = UmbralMotion.springBouncy()
        ),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = modifier
                .defaultMinSize(minWidth = 20.dp)
                .height(20.dp)
                .clip(CircleShape)
                .background(colors.background),
            contentAlignment = Alignment.Center
        ) {
            // Animate content changes
            AnimatedContent(
                targetState = formattedContent,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(UmbralMotion.quick)) +
                            scaleIn(
                                initialScale = 0.8f,
                                animationSpec = UmbralMotion.springBouncy()
                            )).togetherWith(
                        fadeOut(animationSpec = tween(UmbralMotion.quick))
                    )
                },
                label = "badge_content"
            ) { text ->
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.text,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

/**
 * Dot badge variant without text - minimal status indicator
 *
 * @param modifier Modifier for positioning and layout
 * @param variant Visual style variant (Default, Success, Warning, Error, Neutral)
 */
@Composable
fun UmbralDotBadge(
    modifier: Modifier = Modifier,
    variant: BadgeVariant = BadgeVariant.Default
) {
    val colors = getBadgeColors(variant)

    // Animate appearance with bounce
    AnimatedVisibility(
        visible = true,
        enter = scaleIn(
            animationSpec = UmbralMotion.springBouncy(),
            initialScale = 0.6f
        ) + fadeIn(
            animationSpec = UmbralMotion.springBouncy()
        ),
        exit = scaleOut() + fadeOut()
    ) {
        Box(
            modifier = modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(colors.background)
        )
    }
}

/**
 * Badge color scheme for background and text
 */
private data class BadgeColors(
    val background: Color,
    val text: Color
)

/**
 * Get colors for badge variant based on current theme
 */
@Composable
private fun getBadgeColors(variant: BadgeVariant): BadgeColors {
    val isDark = MaterialTheme.colorScheme.background == DarkBackgroundBase

    return when (variant) {
        BadgeVariant.Default -> BadgeColors(
            background = if (isDark) DarkAccentPrimary else LightAccentPrimary,
            text = Color(0xFF151515) // Dark text on accent background
        )

        BadgeVariant.Success -> BadgeColors(
            background = if (isDark) DarkSuccess else LightSuccess,
            text = Color(0xFF151515) // Dark text on success background
        )

        BadgeVariant.Warning -> BadgeColors(
            background = if (isDark) DarkWarning else LightWarning,
            text = Color(0xFF151515) // Dark text on warning background
        )

        BadgeVariant.Error -> BadgeColors(
            background = if (isDark) DarkError else LightError,
            text = Color(0xFFFFFFFF) // White text on error background
        )

        BadgeVariant.Neutral -> BadgeColors(
            background = if (isDark) DarkBackgroundSurface else LightBackgroundSurface,
            text = if (isDark) DarkTextSecondary else LightTextSecondary
        )
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Badge Variants - Light")
@Composable
private fun PreviewBadgeVariantsLight() {
    UmbralTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Variants de Badge", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "5", variant = BadgeVariant.Default)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Default", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "3", variant = BadgeVariant.Success)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Success", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "2", variant = BadgeVariant.Warning)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Warning", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "1", variant = BadgeVariant.Error)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Error", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "4", variant = BadgeVariant.Neutral)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Neutral", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Preview(name = "Badge Variants - Dark")
@Composable
private fun PreviewBadgeVariantsDark() {
    UmbralTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Variants de Badge", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "5", variant = BadgeVariant.Default)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Default", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "3", variant = BadgeVariant.Success)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Success", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "2", variant = BadgeVariant.Warning)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Warning", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "1", variant = BadgeVariant.Error)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Error", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "4", variant = BadgeVariant.Neutral)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Neutral", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Preview(name = "Badge Content Types - Light")
@Composable
private fun PreviewBadgeContentLight() {
    UmbralTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Tipos de Contenido", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "1")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Single", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "12")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Double", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "150")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Overflow", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralBadge(content = "Nuevo")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Text", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Preview(name = "Dot Badge Variants - Light")
@Composable
private fun PreviewDotBadgeLight() {
    UmbralTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Dot Badge Variants", style = MaterialTheme.typography.titleMedium)

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralDotBadge(variant = BadgeVariant.Default)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Default", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralDotBadge(variant = BadgeVariant.Success)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Success", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralDotBadge(variant = BadgeVariant.Warning)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Warning", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralDotBadge(variant = BadgeVariant.Error)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Error", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralDotBadge(variant = BadgeVariant.Neutral)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Neutral", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Preview(name = "Dot Badge Variants - Dark")
@Composable
private fun PreviewDotBadgeDark() {
    UmbralTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Dot Badge Variants", style = MaterialTheme.typography.titleMedium)

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralDotBadge(variant = BadgeVariant.Default)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Default", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralDotBadge(variant = BadgeVariant.Success)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Success", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralDotBadge(variant = BadgeVariant.Warning)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Warning", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralDotBadge(variant = BadgeVariant.Error)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Error", style = MaterialTheme.typography.labelSmall)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    UmbralDotBadge(variant = BadgeVariant.Neutral)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Neutral", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Preview(name = "Badge in Context - Light")
@Composable
private fun PreviewBadgeInContextLight() {
    UmbralTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Badge en Contexto", style = MaterialTheme.typography.titleMedium)

            // Notification icon with badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box {
                    // Simulated icon
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                    )
                    UmbralBadge(
                        content = "5",
                        modifier = Modifier.align(Alignment.TopEnd)
                    )
                }
                Text("Notificaciones", style = MaterialTheme.typography.bodyMedium)
            }

            // Status label with dot badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UmbralDotBadge(variant = BadgeVariant.Success)
                Text("Perfil Activo", style = MaterialTheme.typography.bodyMedium)
            }

            // Tag with badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Trabajo", style = MaterialTheme.typography.bodyMedium)
                UmbralBadge(content = "12", variant = BadgeVariant.Warning)
            }
        }
    }
}
