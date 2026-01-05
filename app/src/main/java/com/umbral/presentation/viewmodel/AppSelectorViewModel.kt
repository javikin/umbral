package com.umbral.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.domain.apps.AppCategory
import com.umbral.domain.apps.InstalledApp
import com.umbral.domain.apps.InstalledAppsProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppSelectorUiState(
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: AppCategory = AppCategory.ALL,
    val includeSystemApps: Boolean = false,
    val allApps: List<InstalledApp> = emptyList(),
    val filteredApps: List<InstalledApp> = emptyList(),
    val selectedPackages: Set<String> = emptySet(),
    val initiallySelectedPackages: Set<String> = emptySet()
) {
    val hasChanges: Boolean
        get() = selectedPackages != initiallySelectedPackages

    val selectedCount: Int
        get() = selectedPackages.size
}

@HiltViewModel
class AppSelectorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val installedAppsProvider: InstalledAppsProvider
) : ViewModel() {

    // Receive already blocked apps as comma-separated string
    private val alreadyBlockedApps: String = savedStateHandle.get<String>("blockedApps") ?: ""

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow(AppCategory.ALL)
    private val _includeSystemApps = MutableStateFlow(false)
    private val _allApps = MutableStateFlow<List<InstalledApp>>(emptyList())
    private val _selectedPackages = MutableStateFlow<Set<String>>(emptySet())
    private val _isLoading = MutableStateFlow(true)

    val uiState: StateFlow<AppSelectorUiState> = combine(
        _isLoading,
        _searchQuery,
        _selectedCategory,
        _includeSystemApps,
        _allApps,
        _selectedPackages
    ) { flows ->
        val isLoading = flows[0] as Boolean
        val query = flows[1] as String
        val category = flows[2] as AppCategory
        val includeSystem = flows[3] as Boolean
        val apps = flows[4] as List<InstalledApp>
        val selected = flows[5] as Set<String>

        val filtered = filterApps(apps, query, category, includeSystem)

        AppSelectorUiState(
            isLoading = isLoading,
            searchQuery = query,
            selectedCategory = category,
            includeSystemApps = includeSystem,
            allApps = apps,
            filteredApps = filtered,
            selectedPackages = selected,
            initiallySelectedPackages = parseBlockedApps()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppSelectorUiState()
    )

    init {
        loadApps()
        initializeSelection()
    }

    private fun parseBlockedApps(): Set<String> {
        return if (alreadyBlockedApps.isBlank()) {
            emptySet()
        } else {
            alreadyBlockedApps.split(",").filter { it.isNotBlank() }.toSet()
        }
    }

    private fun initializeSelection() {
        _selectedPackages.value = parseBlockedApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load all launchable apps (including system apps, we'll filter in UI)
                val apps = installedAppsProvider.getLaunchableApps(includeSystemApps = true)
                _allApps.value = apps
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun filterApps(
        apps: List<InstalledApp>,
        query: String,
        category: AppCategory,
        includeSystem: Boolean
    ): List<InstalledApp> {
        return apps
            .filter { app ->
                // Filter by category
                category == AppCategory.ALL || app.category == category
            }
            .filter { app ->
                // Filter by system app setting
                includeSystem || !app.isSystemApp
            }
            .filter { app ->
                // Filter by search query
                if (query.isBlank()) {
                    true
                } else {
                    app.name.contains(query, ignoreCase = true) ||
                            app.packageName.contains(query, ignoreCase = true)
                }
            }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: AppCategory) {
        _selectedCategory.value = category
    }

    fun toggleSystemApps() {
        _includeSystemApps.update { !it }
    }

    fun toggleAppSelection(packageName: String) {
        _selectedPackages.update { current ->
            if (packageName in current) {
                current - packageName
            } else {
                current + packageName
            }
        }
    }

    fun selectAll() {
        val currentFiltered = uiState.value.filteredApps
        _selectedPackages.update { current ->
            current + currentFiltered.map { it.packageName }
        }
    }

    fun deselectAll() {
        val currentFiltered = uiState.value.filteredApps
        _selectedPackages.update { current ->
            current - currentFiltered.map { it.packageName }.toSet()
        }
    }

    fun getSelectedPackages(): List<String> {
        return _selectedPackages.value.toList()
    }
}
