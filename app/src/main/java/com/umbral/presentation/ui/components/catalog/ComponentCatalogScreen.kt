package com.umbral.presentation.ui.components.catalog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.components.*
import com.umbral.presentation.ui.components.display.BadgeVariant
import com.umbral.presentation.ui.components.display.UmbralBadge
import com.umbral.presentation.ui.components.display.UmbralDotBadge
import com.umbral.presentation.ui.components.feedback.SnackbarAction
import com.umbral.presentation.ui.components.feedback.SnackbarVariant
import com.umbral.presentation.ui.components.feedback.UmbralSnackbar
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Component Catalog Screen (Debug Only)
 *
 * Showcases all design system components with theme toggle for quick testing.
 * Organized by category with sticky headers.
 *
 * Categories:
 * - Buttons
 * - Cards
 * - Inputs
 * - Navigation
 * - Feedback
 * - Data Display
 * - Skeletons
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ComponentCatalogScreen(
    onBack: () -> Unit
) {
    var isDarkTheme by remember { mutableStateOf(false) }

    UmbralTheme(darkTheme = isDarkTheme) {
        Scaffold(
            topBar = {
                UmbralTopBar(
                    title = "Component Catalog",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    },
                    actions = {
                        // Theme toggle
                        UmbralIconButton(
                            icon = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            onClick = { isDarkTheme = !isDarkTheme },
                            contentDescription = "Toggle theme",
                            variant = IconButtonVariant.Ghost
                        )
                    }
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // =============================================================================
                // BUTTONS SECTION
                // =============================================================================
                stickyHeader {
                    SectionHeader(title = "Buttons")
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = UmbralSpacing.screenHorizontal),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // UmbralButton - All variants
                        ComponentDemo(label = "Primary Button") {
                            UmbralButton(
                                text = "Guardar",
                                onClick = {},
                                variant = ButtonVariant.Primary
                            )
                        }

                        ComponentDemo(label = "Secondary Button") {
                            UmbralButton(
                                text = "Configurar",
                                onClick = {},
                                variant = ButtonVariant.Secondary
                            )
                        }

                        ComponentDemo(label = "Outline Button") {
                            UmbralButton(
                                text = "Cancelar",
                                onClick = {},
                                variant = ButtonVariant.Outline
                            )
                        }

                        ComponentDemo(label = "Ghost Button") {
                            UmbralButton(
                                text = "Omitir",
                                onClick = {},
                                variant = ButtonVariant.Ghost
                            )
                        }

                        // Button sizes
                        ComponentDemo(label = "Button Sizes") {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                UmbralButton(
                                    text = "Small",
                                    onClick = {},
                                    size = ButtonSize.Small,
                                    fullWidth = true
                                )
                                UmbralButton(
                                    text = "Medium",
                                    onClick = {},
                                    size = ButtonSize.Medium,
                                    fullWidth = true
                                )
                                UmbralButton(
                                    text = "Large",
                                    onClick = {},
                                    size = ButtonSize.Large,
                                    fullWidth = true
                                )
                            }
                        }

                        ComponentDemo(label = "Button States") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                UmbralButton(
                                    text = "Enabled",
                                    onClick = {},
                                    modifier = Modifier.weight(1f)
                                )
                                UmbralButton(
                                    text = "Disabled",
                                    onClick = {},
                                    enabled = false,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        ComponentDemo(label = "Loading Button") {
                            UmbralButton(
                                text = "Guardando",
                                onClick = {},
                                loading = true
                            )
                        }

                        // Icon buttons
                        ComponentDemo(label = "Icon Buttons - Ghost") {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                UmbralIconButton(
                                    icon = Icons.Default.Menu,
                                    onClick = {},
                                    contentDescription = "Menu",
                                    size = IconButtonSize.Small,
                                    variant = IconButtonVariant.Ghost
                                )
                                UmbralIconButton(
                                    icon = Icons.Default.Menu,
                                    onClick = {},
                                    contentDescription = "Menu",
                                    size = IconButtonSize.Medium,
                                    variant = IconButtonVariant.Ghost
                                )
                                UmbralIconButton(
                                    icon = Icons.Default.Menu,
                                    onClick = {},
                                    contentDescription = "Menu",
                                    size = IconButtonSize.Large,
                                    variant = IconButtonVariant.Ghost
                                )
                            }
                        }

                        ComponentDemo(label = "Icon Buttons - Filled") {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                UmbralIconButton(
                                    icon = Icons.Default.Favorite,
                                    onClick = {},
                                    contentDescription = "Favorito",
                                    size = IconButtonSize.Small,
                                    variant = IconButtonVariant.Filled
                                )
                                UmbralIconButton(
                                    icon = Icons.Default.Favorite,
                                    onClick = {},
                                    contentDescription = "Favorito",
                                    size = IconButtonSize.Medium,
                                    variant = IconButtonVariant.Filled
                                )
                                UmbralIconButton(
                                    icon = Icons.Default.Favorite,
                                    onClick = {},
                                    contentDescription = "Favorito",
                                    size = IconButtonSize.Large,
                                    variant = IconButtonVariant.Filled
                                )
                            }
                        }

                        ComponentDemo(label = "Icon Buttons - Tonal") {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                UmbralIconButton(
                                    icon = Icons.Default.Settings,
                                    onClick = {},
                                    contentDescription = "Configuración",
                                    size = IconButtonSize.Small,
                                    variant = IconButtonVariant.Tonal
                                )
                                UmbralIconButton(
                                    icon = Icons.Default.Settings,
                                    onClick = {},
                                    contentDescription = "Configuración",
                                    size = IconButtonSize.Medium,
                                    variant = IconButtonVariant.Tonal
                                )
                                UmbralIconButton(
                                    icon = Icons.Default.Settings,
                                    onClick = {},
                                    contentDescription = "Configuración",
                                    size = IconButtonSize.Large,
                                    variant = IconButtonVariant.Tonal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // =============================================================================
                // CARDS SECTION
                // =============================================================================
                stickyHeader {
                    SectionHeader(title = "Cards & Surfaces")
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = UmbralSpacing.screenHorizontal),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ComponentDemo(label = "Default Card") {
                            UmbralCard(variant = CardVariant.Default) {
                                Text("Default Card", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "Standard border, flat design",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        ComponentDemo(label = "Elevated Card") {
                            UmbralCard(variant = CardVariant.Elevated) {
                                Text("Elevated Card", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "Uses elevated background color",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        ComponentDemo(label = "Outlined Card") {
                            UmbralCard(variant = CardVariant.Outlined) {
                                Text("Outlined Card", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "More visible border (1.5dp)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        ComponentDemo(label = "Interactive Card") {
                            UmbralCard(variant = CardVariant.Interactive, onClick = {}) {
                                Text("Interactive Card", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "Tap to see press state",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        ComponentDemo(label = "Dividers") {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("Full", style = MaterialTheme.typography.labelSmall)
                                UmbralDivider(variant = DividerVariant.Full)

                                Text("Inset", style = MaterialTheme.typography.labelSmall)
                                UmbralDivider(variant = DividerVariant.Inset)

                                Text("Middle", style = MaterialTheme.typography.labelSmall)
                                UmbralDivider(variant = DividerVariant.Middle)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // =============================================================================
                // INPUTS SECTION
                // =============================================================================
                stickyHeader {
                    SectionHeader(title = "Inputs")
                }

                item {
                    var textValue by remember { mutableStateOf("") }
                    var searchValue by remember { mutableStateOf("") }
                    var switchChecked by remember { mutableStateOf(false) }
                    var checkboxChecked by remember { mutableStateOf(false) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = UmbralSpacing.screenHorizontal),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ComponentDemo(label = "TextField - Empty") {
                            UmbralTextField(
                                value = textValue,
                                onValueChange = { textValue = it },
                                label = "Nombre del perfil",
                                placeholder = "Ingresa un nombre"
                            )
                        }

                        ComponentDemo(label = "TextField - With Value") {
                            UmbralTextField(
                                value = "Casa",
                                onValueChange = {},
                                label = "Nombre del perfil"
                            )
                        }

                        ComponentDemo(label = "TextField - Error") {
                            UmbralTextField(
                                value = "",
                                onValueChange = {},
                                label = "Email",
                                error = "El campo no puede estar vacío"
                            )
                        }

                        ComponentDemo(label = "TextField - Disabled") {
                            UmbralTextField(
                                value = "Campo bloqueado",
                                onValueChange = {},
                                label = "Email",
                                enabled = false
                            )
                        }

                        ComponentDemo(label = "Search Field") {
                            UmbralSearchField(
                                value = searchValue,
                                onValueChange = { searchValue = it },
                                placeholder = "Buscar aplicaciones..."
                            )
                        }

                        ComponentDemo(label = "Switch") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    UmbralSwitch(
                                        checked = false,
                                        onCheckedChange = {}
                                    )
                                    Text("Off", style = MaterialTheme.typography.labelSmall)
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    UmbralSwitch(
                                        checked = true,
                                        onCheckedChange = {}
                                    )
                                    Text("On", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        ComponentDemo(label = "Switch with Label") {
                            UmbralSwitch(
                                checked = switchChecked,
                                onCheckedChange = { switchChecked = it },
                                label = "Activar notificaciones"
                            )
                        }

                        ComponentDemo(label = "Checkbox") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                UmbralCheckbox(
                                    checked = false,
                                    onCheckedChange = {}
                                )
                                UmbralCheckbox(
                                    checked = true,
                                    onCheckedChange = {}
                                )
                            }
                        }

                        ComponentDemo(label = "Checkbox with Label") {
                            UmbralCheckbox(
                                checked = checkboxChecked,
                                onCheckedChange = { checkboxChecked = it },
                                label = "Recordar mi preferencia"
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // =============================================================================
                // NAVIGATION SECTION
                // =============================================================================
                stickyHeader {
                    SectionHeader(title = "Navigation")
                }

                item {
                    var selectedBottomItem by remember { mutableIntStateOf(0) }
                    var selectedTab by remember { mutableIntStateOf(0) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = UmbralSpacing.screenHorizontal),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ComponentDemo(label = "Top Bar") {
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
                                            contentDescription = "Más"
                                        )
                                    }
                                }
                            )
                        }

                        ComponentDemo(label = "Bottom Bar") {
                            UmbralBottomBar(
                                items = listOf(
                                    BottomBarItem(
                                        icon = Icons.Outlined.Home,
                                        selectedIcon = Icons.Filled.Home,
                                        label = "Inicio"
                                    ),
                                    BottomBarItem(
                                        icon = Icons.Default.BarChart,
                                        label = "Stats",
                                        badge = 3
                                    ),
                                    BottomBarItem(
                                        icon = Icons.Default.Settings,
                                        label = "Config"
                                    )
                                ),
                                selectedIndex = selectedBottomItem,
                                onItemSelected = { selectedBottomItem = it }
                            )
                        }

                        ComponentDemo(label = "Tab Row") {
                            UmbralTabRow(
                                tabs = listOf("Hoy", "Semana", "Mes"),
                                selectedIndex = selectedTab,
                                onTabSelected = { selectedTab = it }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // =============================================================================
                // FEEDBACK SECTION
                // =============================================================================
                stickyHeader {
                    SectionHeader(title = "Feedback")
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = UmbralSpacing.screenHorizontal),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ComponentDemo(label = "Snackbar - Default") {
                            UmbralSnackbar(
                                message = "Cambios guardados correctamente",
                                variant = SnackbarVariant.Default
                            )
                        }

                        ComponentDemo(label = "Snackbar - Success") {
                            UmbralSnackbar(
                                message = "Perfil creado exitosamente",
                                variant = SnackbarVariant.Success
                            )
                        }

                        ComponentDemo(label = "Snackbar - Error") {
                            UmbralSnackbar(
                                message = "Error al guardar los cambios",
                                variant = SnackbarVariant.Error
                            )
                        }

                        ComponentDemo(label = "Snackbar - Warning") {
                            UmbralSnackbar(
                                message = "La batería está baja",
                                variant = SnackbarVariant.Warning
                            )
                        }

                        ComponentDemo(label = "Snackbar with Action") {
                            UmbralSnackbar(
                                message = "Perfil eliminado",
                                variant = SnackbarVariant.Default,
                                action = SnackbarAction(
                                    label = "DESHACER",
                                    onClick = {}
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // =============================================================================
                // DATA DISPLAY SECTION
                // =============================================================================
                stickyHeader {
                    SectionHeader(title = "Data Display")
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = UmbralSpacing.screenHorizontal),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ComponentDemo(label = "Badge Variants") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                UmbralBadge(content = "5", variant = BadgeVariant.Default)
                                UmbralBadge(content = "3", variant = BadgeVariant.Success)
                                UmbralBadge(content = "2", variant = BadgeVariant.Warning)
                                UmbralBadge(content = "1", variant = BadgeVariant.Error)
                                UmbralBadge(content = "4", variant = BadgeVariant.Neutral)
                            }
                        }

                        ComponentDemo(label = "Badge - Number Overflow") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                UmbralBadge(content = "1")
                                UmbralBadge(content = "12")
                                UmbralBadge(content = "150")
                            }
                        }

                        ComponentDemo(label = "Badge - Text") {
                            UmbralBadge(content = "Nuevo")
                        }

                        ComponentDemo(label = "Dot Badge") {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                UmbralDotBadge(variant = BadgeVariant.Default)
                                UmbralDotBadge(variant = BadgeVariant.Success)
                                UmbralDotBadge(variant = BadgeVariant.Warning)
                                UmbralDotBadge(variant = BadgeVariant.Error)
                                UmbralDotBadge(variant = BadgeVariant.Neutral)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // =============================================================================
                // SKELETONS SECTION
                // =============================================================================
                stickyHeader {
                    SectionHeader(title = "Skeletons")
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = UmbralSpacing.screenHorizontal),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ComponentDemo(label = "Shimmer Box") {
                            ShimmerBox(
                                modifier = Modifier.fillMaxWidth(),
                                height = 20.dp
                            )
                        }

                        ComponentDemo(label = "Shimmer Circle") {
                            ShimmerCircle(size = 60.dp)
                        }

                        ComponentDemo(label = "Shimmer List Item") {
                            ShimmerListItem()
                        }

                        ComponentDemo(label = "Shimmer Card") {
                            ShimmerCard()
                        }

                        ComponentDemo(label = "Shimmer Stats") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                ShimmerStatsItem()
                                ShimmerStatsItem()
                                ShimmerStatsItem()
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

/**
 * Section header with sticky positioning
 */
@Composable
private fun SectionHeader(title: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(
                horizontal = UmbralSpacing.screenHorizontal,
                vertical = 12.dp
            )
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Component demo wrapper with label
 */
@Composable
private fun ComponentDemo(
    label: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}
