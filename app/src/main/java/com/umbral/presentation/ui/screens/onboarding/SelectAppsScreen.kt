package com.umbral.presentation.ui.screens.onboarding

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.umbral.domain.apps.AppCategory
import com.umbral.domain.apps.InstalledApp
import com.umbral.presentation.ui.theme.UmbralDimens
import com.umbral.presentation.viewmodel.AppPreset
import com.umbral.presentation.viewmodel.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAppsScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onContinue: () -> Unit
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<AppCategory?>(null) }
    val context = LocalContext.current

    // Prevent back navigation - exit app instead
    BackHandler {
        (context as? android.app.Activity)?.finish()
    }

    // Auto-select popular distracting apps on first load
    LaunchedEffect(installedApps) {
        if (installedApps.isNotEmpty() && uiState.selectedApps.isEmpty()) {
            // Auto-select popular distracting apps
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
                title = { Text("Selecciona apps a bloquear") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Subtitle with helpful context
            Text(
                text = "Hemos pre-seleccionado las apps más distractoras. Puedes ajustar la selección.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = UmbralDimens.screenPaddingHorizontal,
                    vertical = UmbralDimens.spaceSm
                )
            )

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar apps...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = UmbralDimens.screenPaddingHorizontal)
            )

            Spacer(modifier = Modifier.height(UmbralDimens.spaceSm))

            // Category filter chips (horizontal scroll)
            LazyRow(
                contentPadding = PaddingValues(horizontal = UmbralDimens.screenPaddingHorizontal),
                horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceXs)
            ) {
                // "All" chip
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Todas (${installedApps.size})") },
                        leadingIcon = if (selectedCategory == null) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                        } else null
                    )
                }

                // Category chips (prioritize distracting categories)
                val sortedCategories = categoryCount.keys
                    .filter { it != AppCategory.ALL && it != AppCategory.SYSTEM }
                    .sortedWith(compareBy(
                        { it !in distractingCategories }, // Distracting first
                        { it.ordinal }
                    ))

                items(sortedCategories) { category ->
                    val count = categoryCount[category] ?: 0
                    if (count > 0) {
                        val isDistracting = category in distractingCategories
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = if (selectedCategory == category) null else category
                            },
                            label = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${stringResource(category.displayName)} ($count)")
                                }
                            },
                            leadingIcon = if (selectedCategory == category) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp)) }
                            } else null,
                            colors = if (isDistracting && selectedCategory != category) {
                                FilterChipDefaults.filterChipColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                                )
                            } else {
                                FilterChipDefaults.filterChipColors()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(UmbralDimens.spaceSm))

            // Selection summary and actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = UmbralDimens.screenPaddingHorizontal),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Selection count with visual indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceXs)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${uiState.selectedApps.size}",
                            style = MaterialTheme.typography.labelSmall,
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
                            // Select all visible apps
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
                        Text("Todo")
                    }
                    TextButton(
                        onClick = {
                            // Deselect all visible apps
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
                        Text("Limpiar")
                    }
                }
            }

            Spacer(modifier = Modifier.height(UmbralDimens.spaceXs))

            // App list with category headers
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(UmbralDimens.spaceMd))
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
                        horizontal = UmbralDimens.screenPaddingHorizontal,
                        vertical = UmbralDimens.spaceXs
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
                            Spacer(modifier = Modifier.height(UmbralDimens.spaceMd))
                        }
                    }
                }
            }

            // Continue button
            Button(
                onClick = onContinue,
                enabled = uiState.selectedApps.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = UmbralDimens.screenPaddingHorizontal)
                    .padding(bottom = UmbralDimens.spaceLg)
            ) {
                Text(
                    if (uiState.selectedApps.isEmpty()) {
                        "Selecciona al menos una app"
                    } else {
                        "Continuar con ${uiState.selectedApps.size} apps"
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryHeader(
    category: AppCategory,
    count: Int,
    selectedCount: Int,
    isDistracting: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = UmbralDimens.spaceXs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceXs)
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
            if (isDistracting) {
                Text(
                    text = "⚠️",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Text(
            text = "$selectedCount/$count",
            style = MaterialTheme.typography.labelMedium,
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
    onToggle: () -> Unit
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = UmbralDimens.spaceXs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceSm)
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = { onToggle() }
        )

        if (icon != null) {
            Image(
                painter = rememberDrawablePainter(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        Text(
            text = app.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}
