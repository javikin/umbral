package com.umbral.presentation.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.outlined.BarChart
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Data class for bottom bar items
 *
 * @param icon Icon to display (unselected state)
 * @param selectedIcon Icon to display when selected (defaults to icon)
 * @param label Accessibility label for the item
 * @param badge Optional notification count badge
 */
data class BottomBarItem(
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
    val label: String,
    val badge: Int? = null
)

/**
 * Umbral Design System Bottom Bar
 *
 * Minimal tab bar with icon-only navigation and animated indicator line.
 * Follows Design System 2.0 specifications with sage teal accent.
 *
 * Visual specs:
 * - Background: backgroundBase
 * - Top border: 1px borderDefault
 * - Height: 64dp
 * - Icon size: 28dp
 * - Indicator: 3px line below active icon
 * - Animation: spring(dampingRatio=0.8, stiffness=300)
 *
 * @param items List of bottom bar items to display
 * @param selectedIndex Currently selected item index
 * @param onItemSelected Callback when item is selected
 * @param modifier Modifier for customization
 */
@Composable
fun UmbralBottomBar(
    items: List<BottomBarItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Top border (1px)
            Divider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )

            // Items row with indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Items
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = UmbralSpacing.screenHorizontal),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items.forEachIndexed { index, item ->
                        BottomBarItemContent(
                            item = item,
                            selected = index == selectedIndex,
                            onClick = { onItemSelected(index) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Animated indicator line
                if (items.isNotEmpty()) {
                    val itemWidth = 1f / items.size
                    val indicatorOffset by animateDpAsState(
                        targetValue = (selectedIndex * itemWidth * 100).dp,
                        animationSpec = spring(
                            dampingRatio = 0.8f,
                            stiffness = Spring.StiffnessMedium
                        ),
                        label = "bottomBarIndicator"
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = indicatorOffset)
                            .width((itemWidth * 100).dp)
                            .height(3.dp)
                            .padding(horizontal = (itemWidth * 100 / 3).dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                    )
                }
            }
        }
    }
}

/**
 * Individual bottom bar item with icon and optional badge
 */
@Composable
private fun BottomBarItemContent(
    item: BottomBarItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, radius = 28.dp),
                onClick = onClick
            )
            .padding(vertical = UmbralSpacing.sm),
        contentAlignment = Alignment.Center
    ) {
        BadgedBox(
            badge = {
                if (item.badge != null && item.badge > 0) {
                    Badge(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ) {
                        Text(
                            text = if (item.badge > 99) "99+" else item.badge.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = if (selected) item.selectedIcon else item.icon,
                contentDescription = item.label,
                modifier = Modifier.size(28.dp),
                tint = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Bottom Bar - Light", showBackground = true)
@Composable
private fun UmbralBottomBarLightPreview() {
    UmbralTheme(darkTheme = false) {
        UmbralBottomBar(
            items = listOf(
                BottomBarItem(
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    label = "Inicio"
                ),
                BottomBarItem(
                    icon = Icons.Outlined.BarChart,
                    selectedIcon = Icons.Filled.BarChart,
                    label = "Estadísticas",
                    badge = 3
                ),
                BottomBarItem(
                    icon = Icons.Outlined.Settings,
                    selectedIcon = Icons.Filled.Settings,
                    label = "Configuración"
                )
            ),
            selectedIndex = 0,
            onItemSelected = {}
        )
    }
}

@Preview(name = "Bottom Bar - Dark", showBackground = true)
@Composable
private fun UmbralBottomBarDarkPreview() {
    UmbralTheme(darkTheme = true) {
        UmbralBottomBar(
            items = listOf(
                BottomBarItem(
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    label = "Inicio"
                ),
                BottomBarItem(
                    icon = Icons.Outlined.BarChart,
                    selectedIcon = Icons.Filled.BarChart,
                    label = "Estadísticas"
                ),
                BottomBarItem(
                    icon = Icons.Outlined.Settings,
                    selectedIcon = Icons.Filled.Settings,
                    label = "Configuración"
                )
            ),
            selectedIndex = 1,
            onItemSelected = {}
        )
    }
}

@Preview(name = "Bottom Bar with Badges", showBackground = true)
@Composable
private fun UmbralBottomBarBadgesPreview() {
    UmbralTheme(darkTheme = false) {
        UmbralBottomBar(
            items = listOf(
                BottomBarItem(
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    label = "Inicio",
                    badge = 5
                ),
                BottomBarItem(
                    icon = Icons.Outlined.BarChart,
                    selectedIcon = Icons.Filled.BarChart,
                    label = "Estadísticas",
                    badge = 150
                ),
                BottomBarItem(
                    icon = Icons.Outlined.Settings,
                    selectedIcon = Icons.Filled.Settings,
                    label = "Configuración"
                )
            ),
            selectedIndex = 2,
            onItemSelected = {}
        )
    }
}

@Preview(name = "Bottom Bar - Two Items", showBackground = true)
@Composable
private fun UmbralBottomBarTwoItemsPreview() {
    UmbralTheme(darkTheme = false) {
        UmbralBottomBar(
            items = listOf(
                BottomBarItem(
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    label = "Inicio"
                ),
                BottomBarItem(
                    icon = Icons.Outlined.Settings,
                    selectedIcon = Icons.Filled.Settings,
                    label = "Configuración"
                )
            ),
            selectedIndex = 0,
            onItemSelected = {}
        )
    }
}

@Preview(name = "Bottom Bar - Four Items", showBackground = true)
@Composable
private fun UmbralBottomBarFourItemsPreview() {
    UmbralTheme(darkTheme = false) {
        UmbralBottomBar(
            items = listOf(
                BottomBarItem(
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    label = "Inicio"
                ),
                BottomBarItem(
                    icon = Icons.Outlined.BarChart,
                    selectedIcon = Icons.Filled.BarChart,
                    label = "Estadísticas"
                ),
                BottomBarItem(
                    icon = Icons.Outlined.Home,
                    selectedIcon = Icons.Filled.Home,
                    label = "Perfil"
                ),
                BottomBarItem(
                    icon = Icons.Outlined.Settings,
                    selectedIcon = Icons.Filled.Settings,
                    label = "Configuración"
                )
            ),
            selectedIndex = 2,
            onItemSelected = {}
        )
    }
}
