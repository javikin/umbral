package com.umbral.presentation.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.umbral.domain.apps.AppCategory
import com.umbral.domain.apps.InstalledApp
import com.umbral.presentation.ui.components.ButtonVariant
import com.umbral.presentation.ui.components.LoadingIndicator
import com.umbral.presentation.ui.components.UmbralButton
import com.umbral.presentation.ui.components.UmbralChip
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import com.umbral.presentation.viewmodel.AppPreset
import com.umbral.presentation.viewmodel.OnboardingViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAppsScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onContinue: () -> Unit,
    onBack: () -> Unit = {}
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<AppCategory?>(null) }
    val context = LocalContext.current

    // Animation states
    var showHeader by remember { mutableStateOf(false) }
    var showSearch by remember { mutableStateOf(false) }
    var showCategories by remember { mutableStateOf(false) }
    var showList by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Staggered animation
    LaunchedEffect(Unit) {
        delay(100)
        showHeader = true
        delay(150)
        showSearch = true
        delay(100)
        showCategories = true
        delay(150)
        showList = true
        delay(200)
        showButton = true
    }

    // Prevent back navigation - exit app instead
    BackHandler {
        (context as? android.app.Activity)?.finish()
    }

    // Auto-select popular distracting apps on first load
    LaunchedEffect(installedApps) {
        if (installedApps.isNotEmpty() && uiState.selectedApps.isEmpty()) {
            viewModel.selectPreset(AppPreset.SOCIAL)
            viewModel.selectPreset(AppPreset.ENTERTAINMENT)
        }
    }

    // Filter and group apps
    val filteredApps by remember(installedApps, searchQuery, selectedCategory) {
        derivedStateOf {
            installedApps
                .filter { app ->
                    val matchesSearch = searchQuery.isBlank() ||
                            app.name.contains(searchQuery, ignoreCase = true)
                    val matchesCategory = selectedCategory == null ||
                            app.category == selectedCategory
                    matchesSearch && matchesCategory
                }
        }
    }

    // Group apps by category for display
    val groupedApps by remember(filteredApps) {
        derivedStateOf {
            filteredApps
                .groupBy { it.category }
                .toSortedMap(compareBy { it.ordinal })
        }
    }

    // Count apps per category
    val categoryCount by remember(installedApps) {
        derivedStateOf {
            installedApps.groupBy { it.category }.mapValues { it.value.size }
        }
    }

    // Distracting categories (for highlighting)
    val distractingCategories = setOf(
        AppCategory.SOCIAL,
        AppCategory.GAMES,
        AppCategory.ENTERTAINMENT,
        AppCategory.COMMUNICATION
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            AnimatedVisibility(
                visible = showHeader,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { -50 }
                        )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = UmbralSpacing.screenHorizontal)
                ) {
                    Text(
                        text = "Selecciona apps a bloquear",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(UmbralSpacing.xs))

                    Text(
                        text = "Hemos pre-seleccionado las apps más distractoras. Puedes ajustar la selección.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.md))

            // Search bar
            AnimatedVisibility(
                visible = showSearch,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { 30 }
                        )
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            "Buscar apps...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    Icons.Default.Clear,
                                    contentDescription = "Limpiar",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.large,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = UmbralSpacing.screenHorizontal)
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.sm))

            // Category filter chips (horizontal scroll)
            AnimatedVisibility(
                visible = showCategories,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { 30 }
                        )
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = UmbralSpacing.screenHorizontal),
                    horizontalArrangement = Arrangement.spacedBy(UmbralSpacing.xs)
                ) {
                    // "All" chip
                    item {
                        UmbralChip(
                            label = "Todas (${installedApps.size})",
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null }
                        )
                    }

                    // Category chips (prioritize distracting categories)
                    val sortedCategories = categoryCount.keys
                        .filter { it != AppCategory.ALL && it != AppCategory.SYSTEM }
                        .sortedWith(compareBy(
                            { it !in distractingCategories },
                            { it.ordinal }
                        ))

                    items(sortedCategories) { category ->
                        val count = categoryCount[category] ?: 0
                        if (count > 0) {
                            val isDistracting = category in distractingCategories
                            UmbralChip(
                                label = "${stringResource(category.displayName)} ($count)",
                                selected = selectedCategory == category,
                                onClick = {
                                    selectedCategory = if (selectedCategory == category) null else category
                                },
                                leadingIcon = category.icon
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.sm))

            // Selection summary and actions
            AnimatedVisibility(
                visible = showList,
                enter = fadeIn(animationSpec = tween(400))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = UmbralSpacing.screenHorizontal),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Selection count with visual indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(UmbralSpacing.xs)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${uiState.selectedApps.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "apps seleccionadas",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Quick actions
                    Row {
                        TextButton(
                            onClick = {
                                filteredApps.forEach { app ->
                                    if (!uiState.selectedApps.contains(app.packageName)) {
                                        viewModel.toggleAppSelection(app.packageName)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.SelectAll,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Todo", fontWeight = FontWeight.Medium)
                        }
                        TextButton(
                            onClick = {
                                filteredApps.forEach { app ->
                                    if (uiState.selectedApps.contains(app.packageName)) {
                                        viewModel.toggleAppSelection(app.packageName)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Limpiar", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.xs))

            // App list with category headers
            AnimatedVisibility(
                visible = showList,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { 50 }
                        )
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            LoadingIndicator(size = 48.dp)
                            Spacer(modifier = Modifier.height(UmbralSpacing.md))
                            Text(
                                text = "Cargando apps instaladas...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else if (filteredApps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) {
                                "No se encontraron apps con \"$searchQuery\""
                            } else {
                                "No hay apps en esta categoría"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(
                            horizontal = UmbralSpacing.screenHorizontal,
                            vertical = UmbralSpacing.xs
                        ),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        groupedApps.forEach { (category, apps) ->
                            // Category header
                            item(key = "header_${category.name}") {
                                CategoryHeader(
                                    category = category,
                                    count = apps.size,
                                    selectedCount = apps.count { uiState.selectedApps.contains(it.packageName) },
                                    isDistracting = category in distractingCategories
                                )
                            }

                            // Apps in category
                            items(
                                items = apps,
                                key = { it.packageName }
                            ) { app ->
                                AppSelectItem(
                                    app = app,
                                    isSelected = uiState.selectedApps.contains(app.packageName),
                                    onToggle = { viewModel.toggleAppSelection(app.packageName) }
                                )
                            }

                            // Spacer after category
                            item(key = "spacer_${category.name}") {
                                Spacer(modifier = Modifier.height(UmbralSpacing.md))
                            }
                        }
                    }
                }
            }

            // Continue button
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            initialOffsetY = { 100 }
                        )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = UmbralSpacing.screenHorizontal)
                        .padding(bottom = UmbralSpacing.lg)
                ) {
                    UmbralButton(
                        text = if (uiState.selectedApps.isEmpty()) {
                            "Selecciona al menos una app"
                        } else {
                            "Continuar con ${uiState.selectedApps.size} apps"
                        },
                        onClick = onContinue,
                        enabled = uiState.selectedApps.isNotEmpty(),
                        fullWidth = true,
                        variant = ButtonVariant.Primary
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(
    category: AppCategory,
    count: Int,
    selectedCount: Int,
    isDistracting: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = UmbralSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UmbralSpacing.xs)
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = if (isDistracting) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(category.displayName),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (isDistracting) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
        Text(
            text = "$selectedCount/$count",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = if (selectedCount > 0) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun AppSelectItem(
    app: InstalledApp,
    isSelected: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val icon = remember(app.packageName) {
        try {
            context.packageManager.getApplicationIcon(app.packageName)
        } catch (e: Exception) {
            null
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onToggle)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else MaterialTheme.colorScheme.surface
            )
            .padding(vertical = UmbralSpacing.sm, horizontal = UmbralSpacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UmbralSpacing.sm)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.outline
            )
        )

        if (icon != null) {
            Image(
                painter = rememberDrawablePainter(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
        }

        Text(
            text = app.name,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Select Apps Screen - Light", showBackground = true)
@Composable
private fun SelectAppsScreenPreview() {
    UmbralTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(UmbralSpacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
        ) {
            Text(
                text = "Selecciona apps a bloquear",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(UmbralSpacing.xs)) {
                UmbralChip(label = "Todas (42)", selected = true, onClick = {})
                UmbralChip(label = "Social (8)", selected = false, onClick = {})
                UmbralChip(label = "Juegos (12)", selected = false, onClick = {})
            }
        }
    }
}

@Preview(name = "Select Apps Screen - Dark", showBackground = true)
@Composable
private fun SelectAppsScreenDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(UmbralSpacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
        ) {
            Text(
                text = "Selecciona apps a bloquear",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(horizontalArrangement = Arrangement.spacedBy(UmbralSpacing.xs)) {
                UmbralChip(label = "Todas (42)", selected = true, onClick = {})
                UmbralChip(label = "Social (8)", selected = false, onClick = {})
            }
        }
    }
}
