package com.umbral.expedition.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.expedition.domain.mapper.LocationMapper
import com.umbral.expedition.domain.mapper.ProgressMapper
import com.umbral.expedition.domain.model.Biome
import com.umbral.expedition.domain.model.DiscoveryResult
import com.umbral.expedition.domain.model.ExpeditionFormulas
import com.umbral.expedition.domain.model.ForestBiomeData
import com.umbral.expedition.domain.model.Location
import com.umbral.expedition.domain.usecase.DiscoverLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the expedition map screen.
 * Manages map state, location discovery, and user interactions.
 */
@HiltViewModel
class ExpeditionMapViewModel @Inject constructor(
    private val repository: ExpeditionRepository,
    private val discoverLocationUseCase: DiscoverLocationUseCase
) : ViewModel() {

    // Selected location for detail sheet
    private val _selectedLocationId = MutableStateFlow<String?>(null)

    // UI state combining repository data with local state
    val uiState: StateFlow<MapUiState> = combine(
        repository.getDiscoveredLocations(),
        repository.getProgress(),
        _selectedLocationId
    ) { locations, progress, selectedId ->
        val discoveredIds = locations.map { it.id }.toSet()
        val visibleIds = ForestBiomeData.getVisibleLocationIds(discoveredIds)

        MapUiState(
            discoveredLocationIds = discoveredIds,
            visibleLocationIds = visibleIds,
            currentEnergy = progress?.totalEnergy ?: 0,
            selectedLocationId = selectedId,
            discoveredLocations = locations.map { LocationMapper.toDomain(it) }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MapUiState()
    )

    // Last discovery result for showing snackbar messages
    private val _lastDiscoveryResult = MutableStateFlow<DiscoveryResult?>(null)
    val lastDiscoveryResult: StateFlow<DiscoveryResult?> = _lastDiscoveryResult

    /**
     * Select a location to show its detail sheet
     */
    fun selectLocation(locationId: String) {
        _selectedLocationId.value = locationId
    }

    /**
     * Clear location selection (close detail sheet)
     */
    fun clearSelection() {
        _selectedLocationId.value = null
    }

    /**
     * Attempt to discover the currently selected location
     */
    fun discoverSelectedLocation() {
        val locationId = _selectedLocationId.value ?: return

        viewModelScope.launch {
            val result = discoverLocationUseCase(locationId, Biome.FOREST)
            _lastDiscoveryResult.value = result

            // Close sheet on success
            if (result is DiscoveryResult.Success) {
                _selectedLocationId.value = null
            }
        }
    }

    /**
     * Clear the last discovery result (dismiss snackbar)
     */
    fun clearDiscoveryResult() {
        _lastDiscoveryResult.value = null
    }

    /**
     * Get energy cost for discovering a specific location
     */
    fun getEnergyCost(locationId: String): Int {
        val discoveredCount = uiState.value.discoveredLocationIds.size
        return ExpeditionFormulas.locationRevealCost(discoveredCount)
    }
}

/**
 * UI state for the expedition map
 */
data class MapUiState(
    val discoveredLocationIds: Set<String> = emptySet(),
    val visibleLocationIds: Set<String> = setOf("forest_01"), // Always show starting location
    val currentEnergy: Int = 0,
    val selectedLocationId: String? = null,
    val discoveredLocations: List<Location> = emptyList()
) {
    /**
     * Check if a location has been discovered
     */
    fun isDiscovered(locationId: String): Boolean {
        return locationId in discoveredLocationIds
    }

    /**
     * Check if a location is visible on the map
     */
    fun isVisible(locationId: String): Boolean {
        return locationId in visibleLocationIds
    }

    /**
     * Get discovered location by ID
     */
    fun getDiscoveredLocation(locationId: String): Location? {
        return discoveredLocations.find { it.id == locationId }
    }
}
