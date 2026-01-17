package com.umbral.presentation.ui.components.display

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.DarkAccentPrimary
import com.umbral.presentation.ui.theme.LightAccentPrimary
import com.umbral.presentation.ui.theme.UmbralMotion
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Umbral Design System 2.0 - Tag Component
 *
 * A compact tag component for labels, categories, and removable items.
 * Tags are read-only displays (no selection state) with optional remove functionality.
 *
 * ## Usage
 * ```kotlin
 * // Simple label tag
 * UmbralTag(text = "Trabajo")
 *
 * // Tag with icon
 * UmbralTag(
 *     text = "Importante",
 *     icon = Icons.Default.Star
 * )
 *
 * // Removable tag
 * UmbralTag(
 *     text = "Instagram",
 *     onRemove = { /* handle removal */ }
 * )
 * ```
 *
 * ## Visual Specs
 * - Height: 28.dp
 * - Background: accentPrimary at 10% opacity (15% when pressed)
 * - Text Color: accentPrimary
 * - Corner Radius: 6.dp
 * - Horizontal Padding: 8.dp
 * - Icon Size: 16.dp
 * - Remove Button: 16.dp icon with 48.dp touch target
 *
 * @param text The tag label text
 * @param modifier Modifier for customization
 * @param icon Optional leading icon (tinted to match text)
 * @param onRemove Optional remove callback - shows X button when provided
 */
@Composable
fun UmbralTag(
    text: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    onRemove: (() -> Unit)? = null
) {
    // Track if tag is being removed for animation
    var isVisible by remember { mutableStateOf(true) }

    // Press state for visual feedback
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Scale animation for press feedback (subtle)
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(
            durationMillis = UmbralMotion.quick,
            easing = UmbralMotion.easeOut
        ),
        label = "tagPressScale"
    )

    // Background opacity based on press state
    val backgroundAlpha = if (isPressed) 0.15f else 0.10f

    // Get accent color based on theme
    val accentColor = MaterialTheme.colorScheme.primary

    AnimatedVisibility(
        visible = isVisible,
        exit = fadeOut(
            animationSpec = tween(
                durationMillis = UmbralMotion.fast,
                easing = UmbralMotion.easeIn
            )
        ) + scaleOut(
            animationSpec = tween(
                durationMillis = UmbralMotion.fast,
                easing = UmbralMotion.easeIn
            ),
            targetScale = 0.8f
        )
    ) {
        Row(
            modifier = modifier
                .scale(scale)
                .height(28.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(accentColor.copy(alpha = backgroundAlpha))
                .then(
                    // Only make clickable if it's removable (for accessibility)
                    if (onRemove != null) {
                        Modifier.clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Button,
                            onClick = {
                                isVisible = false
                                // Delay callback to allow animation to complete
                                onRemove?.invoke()
                            }
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Leading icon (if provided)
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = accentColor
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            // Tag text
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor
            )

            // Remove button (if onRemove is provided)
            onRemove?.let {
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(
                    onClick = {
                        isVisible = false
                        // Delay callback to allow animation to complete
                        onRemove.invoke()
                    },
                    modifier = Modifier.size(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(12.dp),
                        tint = accentColor
                    )
                }
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Tag - Simple", showBackground = true)
@Composable
private fun UmbralTagSimplePreview() {
    UmbralTheme {
        UmbralTag(
            text = "Trabajo",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Tag - With Icon", showBackground = true)
@Composable
private fun UmbralTagWithIconPreview() {
    UmbralTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UmbralTag(
                text = "Instagram",
                icon = Icons.Filled.Favorite
            )
            UmbralTag(
                text = "Facebook",
                icon = Icons.Filled.Star
            )
        }
    }
}

@Preview(name = "Tag - Removable", showBackground = true)
@Composable
private fun UmbralTagRemovablePreview() {
    UmbralTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UmbralTag(
                text = "Instagram",
                onRemove = { /* handle removal */ }
            )
            UmbralTag(
                text = "TikTok",
                icon = Icons.Filled.Star,
                onRemove = { /* handle removal */ }
            )
        }
    }
}

@Preview(name = "Tag - Multiple Tags", showBackground = true)
@Composable
private fun UmbralTagGroupPreview() {
    UmbralTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UmbralTag(text = "Redes Sociales")
            UmbralTag(text = "Entretenimiento")
            UmbralTag(text = "Juegos")
        }
    }
}

@Preview(name = "Tag - Dark Theme", showBackground = true, backgroundColor = 0xFF151515)
@Composable
private fun UmbralTagDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UmbralTag(text = "Trabajo")
            UmbralTag(
                text = "Personal",
                icon = Icons.Filled.Person
            )
            UmbralTag(
                text = "Instagram",
                onRemove = { /* handle removal */ }
            )
        }
    }
}

@Preview(name = "Tag - Light Theme", showBackground = true, backgroundColor = 0xFFF8F8F8)
@Composable
private fun UmbralTagLightPreview() {
    UmbralTheme(darkTheme = false) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UmbralTag(text = "Trabajo")
            UmbralTag(
                text = "Personal",
                icon = Icons.Filled.Person
            )
            UmbralTag(
                text = "Instagram",
                onRemove = { /* handle removal */ }
            )
        }
    }
}
