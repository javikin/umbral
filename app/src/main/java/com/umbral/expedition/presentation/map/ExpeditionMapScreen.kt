package com.umbral.expedition.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.umbral.expedition.domain.model.Biome
import com.umbral.expedition.domain.model.DiscoveryResult
import com.umbral.expedition.presentation.components.ExpeditionWelcomeBanner
import com.umbral.expedition.presentation.map.components.BiomeMapCanvas
import com.umbral.expedition.presentation.map.components.EnergyChip
import com.umbral.expedition.presentation.map.components.LocationDetailSheet

/**
 * Main expedition map screen.
 *
 * Features:
 * - Interactive canvas map with pan/zoom
 * - Fog of war over undiscovered areas
 * - Location markers (green = discovered, orange = available)
 * - Tap to select location and show details
 * - Discovery flow with energy cost
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpeditionMapScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExpeditionMapViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lastDiscoveryResult by viewModel.lastDiscoveryResult.collectAsState()
    val showWelcomeBanner by viewModel.showWelcomeBanner.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState()

    // Show snackbar for discovery results
    LaunchedEffect(lastDiscoveryResult) {
        lastDiscoveryResult?.let { result ->
            val message = when (result) {
                is DiscoveryResult.Success -> {
                    "¡${result.location.displayName} descubierta! Energía restante: ${result.energyRemaining}"
                }
                is DiscoveryResult.InsufficientEnergy -> {
                    "Energía insuficiente. Necesitas ${result.shortage} energía más."
                }
                is DiscoveryResult.AlreadyDiscovered -> {
                    "Esta locación ya fue descubierta."
                }
            }
            snackbarHostState.showSnackbar(message)
            viewModel.clearDiscoveryResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = Biome.FOREST.displayName)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    EnergyChip(energy = uiState.currentEnergy)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B5E20), // Dark forest green
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                windowInsets = WindowInsets(0.dp)
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Main map canvas
            BiomeMapCanvas(
                discoveredLocationIds = uiState.discoveredLocationIds,
                visibleLocationIds = uiState.visibleLocationIds,
                onLocationTap = { locationId ->
                    viewModel.selectLocation(locationId)
                }
            )

            // Welcome banner on top of the map
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                ExpeditionWelcomeBanner(
                    visible = showWelcomeBanner,
                    onDismiss = { viewModel.dismissWelcomeBanner() }
                )
            }

            // Show location detail sheet when a location is selected
            if (uiState.selectedLocationId != null) {
                val locationId = uiState.selectedLocationId!!
                val isDiscovered = uiState.isDiscovered(locationId)
                val location = uiState.getDiscoveredLocation(locationId)
                val energyCost = viewModel.getEnergyCost(locationId)

                LocationDetailSheet(
                    locationId = locationId,
                    isDiscovered = isDiscovered,
                    location = location,
                    currentEnergy = uiState.currentEnergy,
                    energyCost = energyCost,
                    onDismiss = { viewModel.clearSelection() },
                    onDiscover = { viewModel.discoverSelectedLocation() },
                    sheetState = sheetState
                )
            }
        }
    }
}
