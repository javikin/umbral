package com.umbral.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Umbral Design System Chip
 *
 * A selectable chip component with smooth animations for selection state changes.
 *
 * @param label Chip text
 * @param selected Whether the chip is selected
 * @param onClick Selection toggle callback
 * @param modifier Modifier for customization
 * @param leadingIcon Optional icon before text
 * @param enabled Whether the chip is enabled
 */
@Composable
fun UmbralChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = 0.6f,
            stiffness = 500f
        ),
        label = "chipScale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            selected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.surface
        },
        label = "chipBackground"
    )

    val contentColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            selected -> MaterialTheme.colorScheme.onPrimary
            else -> MaterialTheme.colorScheme.onSurface
        },
        label = "chipContent"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            !enabled -> MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
            selected -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline
        },
        label = "chipBorder"
    )

    Surface(
        modifier = modifier
            .scale(scale)
            .height(UmbralSpacing.chipHeight)
            .toggleable(
                value = selected,
                enabled = enabled,
                role = Role.Checkbox,
                interactionSource = interactionSource,
                indication = null,
                onValueChange = { onClick() }
            ),
        shape = MaterialTheme.shapes.small,
        color = backgroundColor,
        border = if (!selected) BorderStroke(1.dp, borderColor) else null
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = UmbralSpacing.md,
                vertical = UmbralSpacing.xs
            ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let { icon ->
                AnimatedIcon(
                    icon = icon,
                    modifier = Modifier.size(18.dp),
                    tint = contentColor,
                    animate = false
                )
                Spacer(modifier = Modifier.width(UmbralSpacing.xs))
            }

            if (selected && leadingIcon == null) {
                AnimatedIcon(
                    icon = Icons.Default.Check,
                    modifier = Modifier.size(16.dp),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(UmbralSpacing.xs))
            }

            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }
    }
}

/**
 * Filter chip group for multiple selections
 */
@Composable
fun UmbralChipGroup(
    items: List<String>,
    selectedItems: Set<String>,
    onSelectionChanged: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    singleSelection: Boolean = false
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(UmbralSpacing.chipSpacing)
    ) {
        items.forEach { item ->
            UmbralChip(
                label = item,
                selected = selectedItems.contains(item),
                onClick = {
                    val newSelection = if (singleSelection) {
                        setOf(item)
                    } else {
                        if (selectedItems.contains(item)) {
                            selectedItems - item
                        } else {
                            selectedItems + item
                        }
                    }
                    onSelectionChanged(newSelection)
                }
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Chip - Unselected", showBackground = true)
@Composable
private fun UmbralChipUnselectedPreview() {
    UmbralTheme {
        UmbralChip(
            label = "Trabajo",
            selected = false,
            onClick = {},
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(name = "Chip - Selected", showBackground = true)
@Composable
private fun UmbralChipSelectedPreview() {
    UmbralTheme {
        UmbralChip(
            label = "Trabajo",
            selected = true,
            onClick = {},
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(name = "Chip - With Icon", showBackground = true)
@Composable
private fun UmbralChipWithIconPreview() {
    UmbralTheme {
        UmbralChip(
            label = "Favorito",
            selected = true,
            onClick = {},
            leadingIcon = Icons.Default.Star,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(name = "Chip - Disabled", showBackground = true)
@Composable
private fun UmbralChipDisabledPreview() {
    UmbralTheme {
        UmbralChip(
            label = "Deshabilitado",
            selected = false,
            onClick = {},
            enabled = false,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(name = "Chip Group", showBackground = true)
@Composable
private fun UmbralChipGroupPreview() {
    UmbralTheme {
        UmbralChipGroup(
            items = listOf("Trabajo", "Personal", "Estudio"),
            selectedItems = setOf("Trabajo"),
            onSelectionChanged = {},
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(name = "Dark Theme Chip", showBackground = true)
@Composable
private fun UmbralChipDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UmbralChip(
                label = "Unselected",
                selected = false,
                onClick = {}
            )
            UmbralChip(
                label = "Selected",
                selected = true,
                onClick = {}
            )
        }
    }
}
