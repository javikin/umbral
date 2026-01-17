package com.umbral.presentation.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.DarkBackgroundBase
import com.umbral.presentation.ui.theme.LightBackgroundBase
import com.umbral.presentation.ui.theme.UmbralTheme

// =============================================================================
// UMBRAL TOP BAR (Design System 2.0)
// =============================================================================

/**
 * Umbral Design System 2.0 - Top App Bar Component
 *
 * Flat, seamless top bar with no elevation or bottom border.
 * Integrates seamlessly with screen content.
 *
 * Visual Specs:
 * - Background: backgroundBase (matches screen background)
 * - Height: 64.dp
 * - Title Style: titleLarge (20sp, Medium)
 * - Bottom Border: None
 * - Elevation: 0
 *
 * @param title The title text to display
 * @param modifier Modifier for customization
 * @param navigationIcon Optional leading navigation icon (typically back button)
 * @param actions Optional trailing actions (typically icon buttons)
 */
@Composable
fun UmbralTopBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = { }
) {
    val isDarkTheme = isSystemInDarkTheme()
    val backgroundColor = if (isDarkTheme) DarkBackgroundBase else LightBackgroundBase

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(backgroundColor),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Navigation icon (leading)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                if (navigationIcon != null) {
                    Box(modifier = Modifier.padding(end = 4.dp)) {
                        navigationIcon()
                    }
                }

                // Title
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // Actions (trailing)
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
    }
}

// =============================================================================
// UMBRAL TAB ROW (Design System 2.0)
// =============================================================================

/**
 * Umbral Design System 2.0 - Tab Row Component
 *
 * Tab row with pill-shaped sliding indicator.
 * Uses subtle colors and smooth spring animations.
 *
 * Visual Specs:
 * - Indicator: Pill shape with accentPrimary 15% opacity background
 * - Text unselected: textSecondary
 * - Text selected: textPrimary
 * - Animation: Spring animation for smooth indicator sliding
 *
 * @param tabs List of tab labels
 * @param selectedIndex Currently selected tab index
 * @param onTabSelected Callback when a tab is selected
 * @param modifier Modifier for customization
 */
@Composable
fun UmbralTabRow(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val selectedColor = MaterialTheme.colorScheme.onBackground
    val unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
    val indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)

    // Track tab widths and positions for indicator animation
    var tabWidths by remember { mutableIntStateOf(0) }
    var tabStartPosition by remember { mutableIntStateOf(0) }

    val density = LocalDensity.current

    // Animate indicator position
    val indicatorOffset by animateDpAsState(
        targetValue = with(density) { tabStartPosition.toDp() },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tabIndicatorOffset"
    )

    val indicatorWidth by animateDpAsState(
        targetValue = with(density) { tabWidths.toDp() },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "tabIndicatorWidth"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        // Sliding pill indicator
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(indicatorWidth)
                .height(40.dp)
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(indicatorColor)
                .align(Alignment.CenterStart)
        )

        // Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedIndex

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(index) }
                        )
                        .onGloballyPositioned { coordinates ->
                            if (isSelected) {
                                tabWidths = coordinates.size.width
                                tabStartPosition = coordinates.positionInParent().x.toInt()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSelected) selectedColor else unselectedColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "UmbralTopBar - With Navigation", showBackground = true)
@Composable
private fun UmbralTopBarWithNavigationPreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            UmbralTopBar(
                title = "Perfiles",
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Más opciones"
                        )
                    }
                }
            )
        }
    }
}

@Preview(name = "UmbralTopBar - Title Only", showBackground = true)
@Composable
private fun UmbralTopBarTitleOnlyPreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            UmbralTopBar(title = "Estadísticas")
        }
    }
}

@Preview(name = "UmbralTopBar - Dark Theme", showBackground = true)
@Composable
private fun UmbralTopBarDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBackgroundBase)
        ) {
            UmbralTopBar(
                title = "Configuración",
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    }
}

@Preview(name = "UmbralTabRow - 3 Tabs", showBackground = true)
@Composable
private fun UmbralTabRow3TabsPreview() {
    UmbralTheme {
        var selectedTab by remember { mutableIntStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            UmbralTabRow(
                tabs = listOf("Hoy", "Semana", "Mes"),
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}

@Preview(name = "UmbralTabRow - 2 Tabs", showBackground = true)
@Composable
private fun UmbralTabRow2TabsPreview() {
    UmbralTheme {
        var selectedTab by remember { mutableIntStateOf(1) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            UmbralTabRow(
                tabs = listOf("Bloqueadas", "Permitidas"),
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}

@Preview(name = "UmbralTabRow - Dark Theme", showBackground = true)
@Composable
private fun UmbralTabRowDarkPreview() {
    UmbralTheme(darkTheme = true) {
        var selectedTab by remember { mutableIntStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBackgroundBase)
                .padding(16.dp)
        ) {
            UmbralTabRow(
                tabs = listOf("Activos", "Inactivos", "Archivados"),
                selectedIndex = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }
    }
}

@Preview(name = "Combined - TopBar + TabRow", showBackground = true)
@Composable
private fun CombinedTopBarTabRowPreview() {
    UmbralTheme {
        var selectedTab by remember { mutableIntStateOf(0) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            androidx.compose.foundation.layout.Column {
                UmbralTopBar(
                    title = "Estadísticas",
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                )

                UmbralTabRow(
                    tabs = listOf("Día", "Semana", "Mes"),
                    selectedIndex = selectedTab,
                    onTabSelected = { selectedTab = it },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}
