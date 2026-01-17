package com.umbral.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.DarkBackgroundBase
import com.umbral.presentation.ui.theme.DarkBorderDefault
import com.umbral.presentation.ui.theme.LightBorderDefault
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Divider variant styles
 *
 * Full: Full width divider (edge to edge)
 * Inset: Divider with 16dp horizontal padding on both sides
 * Middle: Divider with 72dp horizontal padding (for lists with icons/avatars)
 */
enum class DividerVariant {
    Full,
    Inset,
    Middle
}

/**
 * Umbral Design System 2.0 - Divider Component
 *
 * A horizontal or vertical line separator with theme-aware styling.
 * Uses border colors from Design System 2.0.
 *
 * Specs:
 * - Height: 1dp (horizontal) or width: 1dp (vertical)
 * - Color: borderDefault (6% white dark, 4% black light)
 * - Variants: Full, Inset (16dp padding), Middle (72dp padding)
 *
 * @param modifier Modifier for customization
 * @param variant Divider style variant (Full, Inset, Middle)
 * @param color Optional custom color (defaults to borderDefault)
 * @param thickness Divider thickness (default 1dp)
 * @param vertical Whether divider is vertical (default false)
 */
@Composable
fun UmbralDivider(
    modifier: Modifier = Modifier,
    variant: DividerVariant = DividerVariant.Full,
    color: Color? = null,
    thickness: androidx.compose.ui.unit.Dp = 1.dp,
    vertical: Boolean = false
) {
    val isDarkTheme = isSystemInDarkTheme()

    // Use custom color or theme's border color
    val dividerColor = color ?: if (isDarkTheme) DarkBorderDefault else LightBorderDefault

    // Calculate padding based on variant
    val horizontalPadding = when (variant) {
        DividerVariant.Full -> 0.dp
        DividerVariant.Inset -> 16.dp
        DividerVariant.Middle -> 72.dp
    }

    if (vertical) {
        // Vertical divider
        Box(
            modifier = modifier
                .fillMaxHeight()
                .padding(horizontal = horizontalPadding)
                .width(thickness)
                .background(dividerColor)
        )
    } else {
        // Horizontal divider
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .height(thickness)
                .background(dividerColor)
        )
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Divider - All Variants (Light)", showBackground = true)
@Composable
private fun UmbralDividerVariantsLightPreview() {
    UmbralTheme(darkTheme = false) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Full Width Divider",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            UmbralDivider(variant = DividerVariant.Full)

            Text(
                text = "Inset Divider (16dp padding)",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            UmbralDivider(variant = DividerVariant.Inset)

            Text(
                text = "Middle Divider (72dp padding)",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            UmbralDivider(variant = DividerVariant.Middle)
        }
    }
}

@Preview(name = "Divider - All Variants (Dark)", showBackground = true)
@Composable
private fun UmbralDividerVariantsDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBackgroundBase)
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Full Width Divider",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            UmbralDivider(variant = DividerVariant.Full)

            Text(
                text = "Inset Divider (16dp padding)",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            UmbralDivider(variant = DividerVariant.Inset)

            Text(
                text = "Middle Divider (72dp padding)",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            UmbralDivider(variant = DividerVariant.Middle)
        }
    }
}

@Preview(name = "Divider - List Items Example", showBackground = true)
@Composable
private fun UmbralDividerListPreview() {
    UmbralTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // List item 1
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column {
                    Text(
                        text = "John Doe",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Software Engineer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Middle divider aligns with text (skips icon space)
            UmbralDivider(variant = DividerVariant.Middle)

            // List item 2
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column {
                    Text(
                        text = "Jane Smith",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Product Designer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            UmbralDivider(variant = DividerVariant.Middle)

            // List item 3
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column {
                    Text(
                        text = "Bob Johnson",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "UX Researcher",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(name = "Divider - Vertical", showBackground = true)
@Composable
private fun UmbralDividerVerticalPreview() {
    UmbralTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Left",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )

            UmbralDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                variant = DividerVariant.Full,
                vertical = true
            )

            Text(
                text = "Center",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )

            UmbralDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                variant = DividerVariant.Full,
                vertical = true
            )

            Text(
                text = "Right",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(name = "Divider - Custom Colors", showBackground = true)
@Composable
private fun UmbralDividerCustomColorsPreview() {
    UmbralTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Text(
                text = "Default Color",
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            UmbralDivider()

            Text(
                text = "Primary Color",
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            UmbralDivider(color = MaterialTheme.colorScheme.primary)

            Text(
                text = "Error Color",
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            UmbralDivider(color = MaterialTheme.colorScheme.error)

            Text(
                text = "Thick Divider (3dp)",
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            UmbralDivider(thickness = 3.dp, color = MaterialTheme.colorScheme.primary)
        }
    }
}
