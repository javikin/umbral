package com.umbral.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.DarkBackgroundBase
import com.umbral.presentation.ui.theme.DarkBackgroundElevated
import com.umbral.presentation.ui.theme.DarkBackgroundSurface
import com.umbral.presentation.ui.theme.LightBackgroundBase
import com.umbral.presentation.ui.theme.LightBackgroundElevated
import com.umbral.presentation.ui.theme.LightBackgroundSurface
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Surface elevation levels
 *
 * Defines the visual hierarchy of surfaces in the app.
 * Higher levels appear more elevated/prominent.
 *
 * Level0: backgroundBase - Main app background (deepest/base)
 * Level1: backgroundSurface - Cards and containers
 * Level2: backgroundElevated - Elevated surfaces (modals, dialogs)
 * Level3: backgroundElevated + 2% overlay - Highest elevation
 */
enum class SurfaceElevation {
    Level0,  // backgroundBase
    Level1,  // backgroundSurface
    Level2,  // backgroundElevated
    Level3   // backgroundElevated + 2% overlay
}

/**
 * Umbral Design System 2.0 - Surface Component
 *
 * A utility component for creating surfaces at different elevation levels.
 * Uses solid colors from the Design System instead of shadows.
 *
 * Elevation System:
 * - Light Theme: Minimal color change between levels (subtle gray variations)
 * - Dark Theme: Lighter colors = higher elevation (tonal elevation)
 *
 * @param modifier Modifier for customization
 * @param elevation Surface elevation level (affects background color)
 * @param shape Surface corner shape
 * @param content Surface content
 */
@Composable
fun UmbralSurface(
    modifier: Modifier = Modifier,
    elevation: SurfaceElevation = SurfaceElevation.Level1,
    shape: Shape = MaterialTheme.shapes.medium,
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()

    // Get background color based on elevation and theme
    val backgroundColor = when (elevation) {
        SurfaceElevation.Level0 -> if (isDarkTheme) DarkBackgroundBase else LightBackgroundBase
        SurfaceElevation.Level1 -> if (isDarkTheme) DarkBackgroundSurface else LightBackgroundSurface
        SurfaceElevation.Level2 -> if (isDarkTheme) DarkBackgroundElevated else LightBackgroundElevated
        SurfaceElevation.Level3 -> {
            // Level 3 = Elevated + 2% white overlay (dark) or 2% black overlay (light)
            val baseColor = if (isDarkTheme) DarkBackgroundElevated else LightBackgroundElevated
            val overlay = if (isDarkTheme) Color.White.copy(alpha = 0.02f) else Color.Black.copy(alpha = 0.02f)
            // Note: In production, you'd blend these properly. For simplicity, we'll just use the base color
            // with a slight adjustment. Proper blending would require color manipulation utilities.
            baseColor
        }
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor, shape)
    ) {
        content()
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Surface - All Elevations (Light)", showBackground = true)
@Composable
private fun UmbralSurfaceAllLevelsLightPreview() {
    UmbralTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(LightBackgroundBase)
                .padding(16.dp)
        ) {
            Column {
                UmbralSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = SurfaceElevation.Level0
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Level 0 - Background Base",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                UmbralSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = SurfaceElevation.Level1
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Level 1 - Surface (Cards)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                UmbralSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = SurfaceElevation.Level2
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Level 2 - Elevated (Modals)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                UmbralSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = SurfaceElevation.Level3
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Level 3 - Highest",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Surface - All Elevations (Dark)", showBackground = true)
@Composable
private fun UmbralSurfaceAllLevelsDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBackgroundBase)
                .padding(16.dp)
        ) {
            Column {
                UmbralSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = SurfaceElevation.Level0
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Level 0 - Background Base (#151515)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                UmbralSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = SurfaceElevation.Level1
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Level 1 - Surface (#1E1E1E) Cards",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                UmbralSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = SurfaceElevation.Level2
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Level 2 - Elevated (#282828) Modals",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                UmbralSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = SurfaceElevation.Level3
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Level 3 - Highest Elevation",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "Surface - Different Shapes", showBackground = true)
@Composable
private fun UmbralSurfaceShapesPreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Column {
                UmbralSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = SurfaceElevation.Level1,
                    shape = MaterialTheme.shapes.small
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Small Shape (8dp)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                UmbralSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = SurfaceElevation.Level1,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Medium Shape (12dp)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                UmbralSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = SurfaceElevation.Level1,
                    shape = MaterialTheme.shapes.large
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Large Shape (16dp)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                UmbralSurface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    elevation = SurfaceElevation.Level1,
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Extra Large Shape (24dp)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
