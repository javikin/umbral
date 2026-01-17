package com.umbral.presentation.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Umbral Design System TextField
 *
 * A customizable text input component with floating label, icons, and error states.
 *
 * Visual Specs:
 * - Background: backgroundSurface
 * - Border (default): 1px borderDefault
 * - Border (focused): 2px accentPrimary
 * - Border (error): 2px error
 * - Corner Radius: 12.dp
 * - Height: 56.dp
 * - Padding: 16.dp horizontal
 *
 * Animations:
 * - Label float: tween 150ms easeOut
 * - Border color: tween 200ms
 *
 * @param value Current text value
 * @param onValueChange Callback when value changes
 * @param modifier Modifier for customization
 * @param label Optional floating label text
 * @param placeholder Optional placeholder text (shown when empty)
 * @param leadingIcon Optional icon before text
 * @param trailingIcon Optional icon after text
 * @param error Optional error message (shows below field)
 * @param enabled Whether the field is enabled
 * @param singleLine Whether to restrict to single line
 * @param keyboardOptions Keyboard configuration
 */
@Composable
fun UmbralTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    error: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val hasError = error != null
    val hasValue = value.isNotEmpty()
    val shouldFloatLabel = isFocused || hasValue

    // Animated border color based on state
    val borderColor by animateColorAsState(
        targetValue = when {
            hasError -> MaterialTheme.colorScheme.error
            isFocused -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(durationMillis = 200),
        label = "borderColor"
    )

    // Animated border width based on state
    val borderWidth by animateDpAsState(
        targetValue = if (isFocused || hasError) 2.dp else 1.dp,
        animationSpec = tween(durationMillis = 200),
        label = "borderWidth"
    )

    // Animated label position
    val labelOffset by animateDpAsState(
        targetValue = if (shouldFloatLabel) 0.dp else 16.dp,
        animationSpec = tween(durationMillis = 150),
        label = "labelOffset"
    )

    val labelAlpha by animateFloatAsState(
        targetValue = if (shouldFloatLabel) 1f else 0.6f,
        animationSpec = tween(durationMillis = 150),
        label = "labelAlpha"
    )

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .border(
                    width = borderWidth,
                    color = borderColor,
                    shape = MaterialTheme.shapes.medium // 12.dp radius
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = UmbralSpacing.md),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Leading icon
                leadingIcon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(UmbralSpacing.iconSizeMedium),
                        tint = if (hasError) {
                            MaterialTheme.colorScheme.error
                        } else if (isFocused) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Spacer(modifier = Modifier.width(UmbralSpacing.iconTextSpacing))
                }

                // Text field content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = if (shouldFloatLabel && label != null) 12.dp else 0.dp)
                ) {
                    // Floating label
                    label?.let {
                        Text(
                            text = it,
                            style = if (shouldFloatLabel) {
                                MaterialTheme.typography.labelSmall
                            } else {
                                MaterialTheme.typography.bodyLarge
                            },
                            color = when {
                                hasError -> MaterialTheme.colorScheme.error
                                isFocused -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier
                                .align(if (shouldFloatLabel) Alignment.TopStart else Alignment.CenterStart)
                                .padding(top = labelOffset)
                                .alpha(labelAlpha)
                        )
                    }

                    // Actual text input
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(if (label != null && shouldFloatLabel) Alignment.BottomStart else Alignment.CenterStart)
                            .padding(
                                top = if (label != null && shouldFloatLabel) 4.dp else 0.dp,
                                bottom = if (label != null && shouldFloatLabel) 4.dp else 0.dp
                            ),
                        enabled = enabled,
                        singleLine = singleLine,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = if (enabled) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            }
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        keyboardOptions = keyboardOptions,
                        interactionSource = interactionSource,
                        decorationBox = { innerTextField ->
                            Box {
                                // Placeholder (only show when no label floating and no value)
                                if (value.isEmpty() && !shouldFloatLabel && placeholder != null) {
                                    Text(
                                        text = placeholder,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                }

                // Trailing icon
                trailingIcon?.let { icon ->
                    Spacer(modifier = Modifier.width(UmbralSpacing.iconTextSpacing))
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(UmbralSpacing.iconSizeMedium),
                        tint = if (hasError) {
                            MaterialTheme.colorScheme.error
                        } else if (isFocused) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }

        // Error message
        AnimatedVisibility(
            visible = hasError,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = UmbralSpacing.md,
                        top = UmbralSpacing.xs,
                        end = UmbralSpacing.md
                    ),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.size(UmbralSpacing.iconSizeSmall),
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(UmbralSpacing.xs))
                Text(
                    text = error ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Default TextField", showBackground = true)
@Composable
private fun UmbralTextFieldDefaultPreview() {
    UmbralTheme {
        UmbralTextField(
            value = "",
            onValueChange = {},
            label = "Nombre del perfil",
            placeholder = "Ingresa un nombre",
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralSpacing.md)
        )
    }
}

@Preview(name = "Focused TextField", showBackground = true)
@Composable
private fun UmbralTextFieldFocusedPreview() {
    UmbralTheme {
        UmbralTextField(
            value = "Casa",
            onValueChange = {},
            label = "Nombre del perfil",
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralSpacing.md)
        )
    }
}

@Preview(name = "With Leading Icon", showBackground = true)
@Composable
private fun UmbralTextFieldLeadingIconPreview() {
    UmbralTheme {
        UmbralTextField(
            value = "Buscar apps",
            onValueChange = {},
            placeholder = "Buscar",
            leadingIcon = Icons.Default.Search,
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralSpacing.md)
        )
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
private fun UmbralTextFieldErrorPreview() {
    UmbralTheme {
        UmbralTextField(
            value = "",
            onValueChange = {},
            label = "Nombre del perfil",
            error = "El nombre no puede estar vacío",
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralSpacing.md)
        )
    }
}

@Preview(name = "Disabled State", showBackground = true)
@Composable
private fun UmbralTextFieldDisabledPreview() {
    UmbralTheme {
        UmbralTextField(
            value = "Campo bloqueado",
            onValueChange = {},
            label = "Email",
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralSpacing.md)
        )
    }
}

@Preview(name = "Dark Theme", showBackground = true)
@Composable
private fun UmbralTextFieldDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(UmbralSpacing.md),
            verticalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
        ) {
            UmbralTextField(
                value = "",
                onValueChange = {},
                label = "Nombre del perfil",
                placeholder = "Ingresa un nombre",
                modifier = Modifier.fillMaxWidth()
            )
            UmbralTextField(
                value = "Casa",
                onValueChange = {},
                label = "Nombre del perfil",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.fillMaxWidth()
            )
            UmbralTextField(
                value = "",
                onValueChange = {},
                label = "Email",
                error = "Campo requerido",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(name = "All States", showBackground = true)
@Composable
private fun UmbralTextFieldAllStatesPreview() {
    UmbralTheme {
        Column(
            modifier = Modifier.padding(UmbralSpacing.md),
            verticalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
        ) {
            Text(
                text = "Vacío",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            UmbralTextField(
                value = "",
                onValueChange = {},
                label = "Nombre del perfil",
                placeholder = "Ingresa un nombre",
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Con valor",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            UmbralTextField(
                value = "Casa",
                onValueChange = {},
                label = "Nombre del perfil",
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Con icono",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            UmbralTextField(
                value = "",
                onValueChange = {},
                placeholder = "Buscar apps",
                leadingIcon = Icons.Default.Search,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Error",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            UmbralTextField(
                value = "",
                onValueChange = {},
                label = "Email",
                error = "El email es requerido",
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "Deshabilitado",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            UmbralTextField(
                value = "Campo bloqueado",
                onValueChange = {},
                label = "Email",
                enabled = false,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
