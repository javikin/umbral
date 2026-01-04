package com.umbral.presentation.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.umbral.domain.apps.InstalledApp
import com.umbral.presentation.ui.theme.UmbralDimens
import com.umbral.presentation.viewmodel.AppPreset
import com.umbral.presentation.viewmodel.OnboardingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAppsScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(installedApps, searchQuery) {
        if (searchQuery.isBlank()) {
            installedApps
        } else {
            installedApps.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Selecciona apps a bloquear") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Subtítulo
            Text(
                text = "Elige las apps que te distraen más. Podrás cambiar esto después.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(
                    horizontal = UmbralDimens.screenPaddingHorizontal,
                    vertical = UmbralDimens.spaceMd
                )
            )

            // Búsqueda
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

            Spacer(modifier = Modifier.height(UmbralDimens.spaceMd))

            // Quick select presets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = UmbralDimens.screenPaddingHorizontal),
                horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceSm)
            ) {
                PresetChip(
                    text = "Redes sociales",
                    onClick = { viewModel.selectPreset(AppPreset.SOCIAL) }
                )
                PresetChip(
                    text = "Juegos",
                    onClick = { viewModel.selectPreset(AppPreset.GAMES) }
                )
                PresetChip(
                    text = "Entretenimiento",
                    onClick = { viewModel.selectPreset(AppPreset.ENTERTAINMENT) }
                )
            }

            Spacer(modifier = Modifier.height(UmbralDimens.spaceMd))

            // Contador de seleccionadas
            Text(
                text = "${uiState.selectedApps.size} apps seleccionadas",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = UmbralDimens.screenPaddingHorizontal)
            )

            // Lista de apps
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(
                    horizontal = UmbralDimens.screenPaddingHorizontal,
                    vertical = UmbralDimens.spaceMd
                ),
                verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceXs)
            ) {
                items(
                    items = filteredApps,
                    key = { it.packageName }
                ) { app ->
                    AppSelectItem(
                        app = app,
                        isSelected = uiState.selectedApps.contains(app.packageName),
                        onToggle = { viewModel.toggleAppSelection(app.packageName) }
                    )
                }
            }

            // Botón continuar
            TextButton(
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
            .padding(vertical = UmbralDimens.spaceSm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceMd)
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
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = app.name,
                style = MaterialTheme.typography.bodyMedium
            )
            if (app.category != null) {
                Text(
                    text = app.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PresetChip(
    text: String,
    onClick: () -> Unit
) {
    SuggestionChip(
        onClick = onClick,
        label = { Text(text) },
        icon = {
            Icon(
                Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    )
}
