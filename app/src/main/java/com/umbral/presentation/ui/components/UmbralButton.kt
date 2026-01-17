package com.umbral.presentation.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Button variants for UmbralButton
 */
enum class ButtonVariant {
    Primary,    // Filled with primary color
    Secondary,  // Filled with secondary color
    Outline,    // Outlined with primary color
    Ghost       // Text only, no background
}

/**
 * Button sizes for UmbralButton
 */
enum class ButtonSize {
    Small,   // height: 36.dp, text: labelMedium
    Medium,  // height: 48.dp, text: labelLarge (default)
    Large    // height: 56.dp, text: titleSmall
}

/**
 * Umbral Design System Button
 *
 * A customizable button component with support for different variants,
 * sizes, loading states, and press animations.
 *
 * @param text Button label text
 * @param onClick Click callback
 * @param modifier Modifier for customization
 * @param enabled Whether the button is enabled
 * @param variant Visual style variant
 * @param size Button size (Small, Medium, Large)
 * @param loading Show loading indicator instead of text
 * @param leadingIcon Optional icon before text
 * @param fullWidth Whether button takes full width
 */
@Composable
fun UmbralButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    variant: ButtonVariant = ButtonVariant.Primary,
    size: ButtonSize = ButtonSize.Medium,
    loading: Boolean = false,
    leadingIcon: ImageVector? = null,
    fullWidth: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Press animation - scale to 0.98 with spring
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled && !loading) 0.98f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 500f
        ),
        label = "buttonScale"
    )

    // Size-based dimensions
    val buttonHeight = when (size) {
        ButtonSize.Small -> 36.dp
        ButtonSize.Medium -> 48.dp
        ButtonSize.Large -> 56.dp
    }

    val horizontalPadding = when (size) {
        ButtonSize.Small -> 16.dp
        ButtonSize.Medium -> 24.dp
        ButtonSize.Large -> 32.dp
    }

    val textStyle = when (size) {
        ButtonSize.Small -> MaterialTheme.typography.labelMedium
        ButtonSize.Medium -> MaterialTheme.typography.labelLarge
        ButtonSize.Large -> MaterialTheme.typography.titleSmall
    }

    val buttonModifier = modifier
        .scale(scale)
        .height(buttonHeight)
        .then(if (fullWidth) Modifier.fillMaxWidth() else Modifier)

    when (variant) {
        ButtonVariant.Primary -> {
            Button(
                onClick = { if (!loading) onClick() },
                modifier = buttonModifier,
                enabled = enabled,
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary, // Sage teal
                    contentColor = Color(0xFF151515) // Dark text for contrast
                ),
                interactionSource = interactionSource,
                contentPadding = PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = UmbralSpacing.md
                )
            ) {
                ButtonContent(
                    text = text,
                    loading = loading,
                    leadingIcon = leadingIcon,
                    contentColor = Color(0xFF151515),
                    textStyle = textStyle
                )
            }
        }

        ButtonVariant.Secondary -> {
            Button(
                onClick = { if (!loading) onClick() },
                modifier = buttonModifier,
                enabled = enabled,
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                interactionSource = interactionSource,
                contentPadding = PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = UmbralSpacing.md
                )
            ) {
                ButtonContent(
                    text = text,
                    loading = loading,
                    leadingIcon = leadingIcon,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                    textStyle = textStyle
                )
            }
        }

        ButtonVariant.Outline -> {
            OutlinedButton(
                onClick = { if (!loading) onClick() },
                modifier = buttonModifier,
                enabled = enabled,
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(
                    width = 1.5.dp,
                    color = if (enabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                ),
                interactionSource = interactionSource,
                contentPadding = PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = UmbralSpacing.md
                )
            ) {
                ButtonContent(
                    text = text,
                    loading = loading,
                    leadingIcon = leadingIcon,
                    contentColor = MaterialTheme.colorScheme.primary,
                    textStyle = textStyle
                )
            }
        }

        ButtonVariant.Ghost -> {
            TextButton(
                onClick = { if (!loading) onClick() },
                modifier = buttonModifier,
                enabled = enabled,
                shape = MaterialTheme.shapes.small,
                interactionSource = interactionSource,
                contentPadding = PaddingValues(
                    horizontal = horizontalPadding,
                    vertical = UmbralSpacing.md
                )
            ) {
                ButtonContent(
                    text = text,
                    loading = loading,
                    leadingIcon = leadingIcon,
                    contentColor = MaterialTheme.colorScheme.primary,
                    textStyle = textStyle
                )
            }
        }
    }
}

@Composable
private fun ButtonContent(
    text: String,
    loading: Boolean,
    leadingIcon: ImageVector?,
    contentColor: Color,
    textStyle: androidx.compose.ui.text.TextStyle
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = contentColor
            )
        } else {
            leadingIcon?.let { icon ->
                AnimatedIcon(
                    icon = icon,
                    modifier = Modifier.size(UmbralSpacing.iconSizeMedium),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(UmbralSpacing.iconTextSpacing))
            }
            Text(
                text = text,
                style = textStyle,
                color = contentColor
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Primary Button", showBackground = true)
@Composable
private fun UmbralButtonPrimaryPreview() {
    UmbralTheme {
        UmbralButton(
            text = "Guardar",
            onClick = {},
            variant = ButtonVariant.Primary
        )
    }
}

@Preview(name = "Secondary Button", showBackground = true)
@Composable
private fun UmbralButtonSecondaryPreview() {
    UmbralTheme {
        UmbralButton(
            text = "Configurar",
            onClick = {},
            variant = ButtonVariant.Secondary
        )
    }
}

@Preview(name = "Outline Button", showBackground = true)
@Composable
private fun UmbralButtonOutlinePreview() {
    UmbralTheme {
        UmbralButton(
            text = "Cancelar",
            onClick = {},
            variant = ButtonVariant.Outline
        )
    }
}

@Preview(name = "Ghost Button", showBackground = true)
@Composable
private fun UmbralButtonGhostPreview() {
    UmbralTheme {
        UmbralButton(
            text = "Omitir",
            onClick = {},
            variant = ButtonVariant.Ghost
        )
    }
}

@Preview(name = "Loading Button", showBackground = true)
@Composable
private fun UmbralButtonLoadingPreview() {
    UmbralTheme {
        UmbralButton(
            text = "Guardando",
            onClick = {},
            loading = true
        )
    }
}

@Preview(name = "Disabled Button", showBackground = true)
@Composable
private fun UmbralButtonDisabledPreview() {
    UmbralTheme {
        UmbralButton(
            text = "Continuar",
            onClick = {},
            enabled = false
        )
    }
}

@Preview(name = "Full Width Button", showBackground = true)
@Composable
private fun UmbralButtonFullWidthPreview() {
    UmbralTheme {
        UmbralButton(
            text = "Continuar",
            onClick = {},
            fullWidth = true
        )
    }
}

@Preview(name = "Dark Theme Button", showBackground = true)
@Composable
private fun UmbralButtonDarkPreview() {
    UmbralTheme(darkTheme = true) {
        UmbralButton(
            text = "Guardar",
            onClick = {},
            variant = ButtonVariant.Primary
        )
    }
}

@Preview(name = "Button Sizes", showBackground = true)
@Composable
private fun UmbralButtonSizesPreview() {
    UmbralTheme {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UmbralButton(
                text = "Small Button",
                onClick = {},
                size = ButtonSize.Small,
                fullWidth = true
            )
            UmbralButton(
                text = "Medium Button",
                onClick = {},
                size = ButtonSize.Medium,
                fullWidth = true
            )
            UmbralButton(
                text = "Large Button",
                onClick = {},
                size = ButtonSize.Large,
                fullWidth = true
            )
        }
    }
}
