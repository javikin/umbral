package com.umbral.presentation.ui.preview

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.components.AnimatedCheckbox
import com.umbral.presentation.ui.components.ButtonVariant
import com.umbral.presentation.ui.components.LabeledCheckbox
import com.umbral.presentation.ui.components.ShimmerCard
import com.umbral.presentation.ui.components.ShimmerListItem
import com.umbral.presentation.ui.components.UmbralButton
import com.umbral.presentation.ui.components.UmbralCard
import com.umbral.presentation.ui.components.CardVariant
import com.umbral.presentation.ui.components.UmbralElevation
import com.umbral.presentation.ui.components.UmbralLabeledToggle
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Comprehensive preview file for visual testing all UI components
 * in both light and dark modes.
 *
 * Use these previews to verify:
 * - Color consistency
 * - Typography hierarchy
 * - Spacing standards
 * - Component states
 * - Dark mode appearance
 */

// =============================================================================
// COMPONENT SHOWCASE
// =============================================================================

@Preview(
    name = "Light - Components",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun ComponentShowcaseLightPreview() {
    UmbralTheme(darkTheme = false) {
        ComponentShowcase()
    }
}

@Preview(
    name = "Dark - Components",
    showBackground = true,
    widthDp = 360,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ComponentShowcaseDarkPreview() {
    UmbralTheme(darkTheme = true) {
        ComponentShowcase()
    }
}

@Composable
private fun ComponentShowcase() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(UmbralSpacing.screenHorizontal)
        ) {
            // Section: Typography
            SectionHeader("Typography")
            Text(
                text = "Display Large",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "Headline Medium",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Title Large",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Body Large - Main content text",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Body Medium - Secondary content",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Label Small - Captions",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Buttons
            SectionHeader("Buttons")
            UmbralButton(
                text = "Primary Button",
                onClick = {},
                variant = ButtonVariant.Primary,
                fullWidth = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            UmbralButton(
                text = "Secondary Button",
                onClick = {},
                variant = ButtonVariant.Secondary,
                fullWidth = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            UmbralButton(
                text = "Outline Button",
                onClick = {},
                variant = ButtonVariant.Outline,
                fullWidth = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            UmbralButton(
                text = "Ghost Button",
                onClick = {},
                variant = ButtonVariant.Ghost,
                fullWidth = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            UmbralButton(
                text = "Disabled Button",
                onClick = {},
                enabled = false,
                fullWidth = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            UmbralButton(
                text = "Loading...",
                onClick = {},
                loading = true,
                fullWidth = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Cards
            SectionHeader("Cards")
            UmbralCard(
                elevation = UmbralElevation.Subtle
            ) {
                Text(
                    text = "Subtle Elevation Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "This card has minimal shadow",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            UmbralCard(
                elevation = UmbralElevation.Medium
            ) {
                Text(
                    text = "Medium Elevation Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "This card has moderate shadow",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            UmbralCard(
                elevation = UmbralElevation.High,
                onClick = {}
            ) {
                Text(
                    text = "Clickable Card",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Tap to see press animation",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Form Elements
            SectionHeader("Form Elements")
            UmbralLabeledToggle(
                label = "Toggle enabled",
                checked = true,
                onCheckedChange = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            UmbralLabeledToggle(
                label = "Toggle disabled",
                checked = false,
                onCheckedChange = {},
                enabled = false
            )
            Spacer(modifier = Modifier.height(8.dp))
            AnimatedCheckbox(
                checked = true,
                onCheckedChange = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
            LabeledCheckbox(
                label = "Checkbox with label",
                description = "Optional description text",
                checked = false,
                onCheckedChange = {}
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Loading States
            SectionHeader("Loading States")
            ShimmerListItem()
            Spacer(modifier = Modifier.height(8.dp))
            ShimmerCard(contentLines = 2)

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Colors
            SectionHeader("Theme Colors")
            ColorSwatch("Primary", MaterialTheme.colorScheme.primary)
            ColorSwatch("Secondary", MaterialTheme.colorScheme.secondary)
            ColorSwatch("Tertiary", MaterialTheme.colorScheme.tertiary)
            ColorSwatch("Error", MaterialTheme.colorScheme.error)
            ColorSwatch("Surface", MaterialTheme.colorScheme.surface)
            ColorSwatch("Background", MaterialTheme.colorScheme.background)

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 12.dp)
    )
}

@Composable
private fun ColorSwatch(name: String, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(color)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            color = if (color == MaterialTheme.colorScheme.surface ||
                color == MaterialTheme.colorScheme.background
            ) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    }
}

// =============================================================================
// SPACING VERIFICATION
// =============================================================================

@Preview(
    name = "Spacing Standards",
    showBackground = true,
    widthDp = 360
)
@Composable
private fun SpacingVerificationPreview() {
    UmbralTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(UmbralSpacing.screenHorizontal)
            ) {
                Text(
                    text = "Spacing Verification",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(UmbralSpacing.lg))

                // Show spacing values
                SpacingRow("xs", UmbralSpacing.xs)
                SpacingRow("sm", UmbralSpacing.sm)
                SpacingRow("md", UmbralSpacing.md)
                SpacingRow("lg", UmbralSpacing.lg)
                SpacingRow("xl", UmbralSpacing.xl)
                SpacingRow("xxl", UmbralSpacing.xxl)

                Spacer(modifier = Modifier.height(UmbralSpacing.lg))

                // Card spacing demo
                Text(
                    text = "Card Padding: ${UmbralSpacing.cardPadding}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Screen Horizontal: ${UmbralSpacing.screenHorizontal}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SpacingRow(name: String, value: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(value)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Text(
            text = "$name: $value",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}

// =============================================================================
// ACCESSIBILITY PREVIEW
// =============================================================================

@Preview(
    name = "Accessibility - Large Text",
    showBackground = true,
    fontScale = 1.5f,
    widthDp = 360
)
@Composable
private fun LargeTextPreview() {
    UmbralTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(UmbralSpacing.screenHorizontal)
            ) {
                Text(
                    text = "Large Text Mode",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "This preview shows how the UI looks with 1.5x font scaling. " +
                            "All text should remain readable and not overflow.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                UmbralButton(
                    text = "Button Text",
                    onClick = {},
                    fullWidth = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                UmbralCard(variant = CardVariant.Default) {
                    Text(
                        text = "Card Title",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Card content should wrap properly",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

// =============================================================================
// SCREEN SIZE PREVIEWS
// =============================================================================

@Preview(
    name = "Small Screen (320dp)",
    showBackground = true,
    widthDp = 320,
    heightDp = 568
)
@Composable
private fun SmallScreenPreview() {
    UmbralTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(UmbralSpacing.screenHorizontal)
            ) {
                Text(
                    text = "Small Screen",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                UmbralButton(
                    text = "Full Width Button",
                    onClick = {},
                    fullWidth = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                UmbralCard(variant = CardVariant.Default) {
                    Text("Content adapts to small screens")
                }
            }
        }
    }
}

@Preview(
    name = "Large Screen (600dp)",
    showBackground = true,
    widthDp = 600,
    heightDp = 900
)
@Composable
private fun LargeScreenPreview() {
    UmbralTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(UmbralSpacing.screenHorizontal)
            ) {
                Text(
                    text = "Large Screen / Tablet",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Layout should adapt for larger screens",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
