package com.umbral.expedition.presentation.companion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.expedition.domain.PassiveBonusCalculator
import com.umbral.expedition.domain.mapper.CompanionMapper
import com.umbral.expedition.domain.mapper.ProgressMapper
import com.umbral.expedition.domain.model.CaptureResult
import com.umbral.expedition.domain.model.Companion
import com.umbral.expedition.domain.model.CompanionType
import com.umbral.expedition.domain.model.EvolutionResult
import com.umbral.expedition.domain.model.InvestEnergyResult
import com.umbral.expedition.domain.model.PlayerProgress
import com.umbral.expedition.domain.usecase.CaptureCompanionUseCase
import com.umbral.expedition.domain.usecase.EvolveCompanionUseCase
import com.umbral.expedition.domain.usecase.InvestEnergyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for companion list and detail screens.
 *
 * Handles:
 * - Displaying all companion types with their states (locked/available/captured)
 * - Capturing new companions
 * - Investing energy in companions
 * - Evolving companions
 * - Setting active companion
 */
@HiltViewModel
class CompanionViewModel @Inject constructor(
    private val repository: ExpeditionRepository,
    private val captureCompanionUseCase: CaptureCompanionUseCase,
    private val evolveCompanionUseCase: EvolveCompanionUseCase,
    private val investEnergyUseCase: InvestEnergyUseCase
) : ViewModel() {

    // ========== State ==========

    /**
     * Current player progress
     */
    val progress: StateFlow<PlayerProgress?> = repository.getProgress()
        .map { it?.let { ProgressMapper.toDomain(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /**
     * All captured companions
     */
    private val capturedCompanions: StateFlow<List<Companion>> = repository.getAllCompanions()
        .map { entities -> entities.map { CompanionMapper.toDomain(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Currently active companion
     */
    val activeCompanion: StateFlow<Companion?> = repository.getActiveCompanion()
        .map { it?.let { CompanionMapper.toDomain(it) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /**
     * Discovered locations (for requirement checking)
     */
    private val discoveredLocationIds: StateFlow<List<String>> = repository.getDiscoveredLocations()
        .map { entities -> entities.map { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Unlocked achievements (for requirement checking)
     */
    private val unlockedAchievementIds: StateFlow<List<String>> = repository.getUnlockedAchievements()
        .map { entities -> entities.map { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * All companion types with their availability status
     */
    val companionStates: StateFlow<List<CompanionState>> = combine(
        capturedCompanions,
        progress,
        discoveredLocationIds,
        unlockedAchievementIds
    ) { captured, prog, locations, achievements ->
        CompanionType.values().map { type ->
            val capturedCompanion = captured.find { it.type == type }

            when {
                // Already captured
                capturedCompanion != null -> CompanionState.Captured(capturedCompanion)

                // Check if requirements met
                prog != null && type.captureRequirement.isMet(
                    progress = prog,
                    discoveredLocationIds = locations,
                    unlockedAchievementIds = achievements
                ) -> CompanionState.Available(type)

                // Locked
                else -> CompanionState.Locked(type)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Active bonus summary
     */
    val activeBonusSummary: StateFlow<PassiveBonusCalculator.BonusSummary> = activeCompanion
        .map { PassiveBonusCalculator.getBonusSummary(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            PassiveBonusCalculator.getBonusSummary(null)
        )

    // UI Events
    private val _uiEvent = MutableStateFlow<CompanionUiEvent?>(null)
    val uiEvent = _uiEvent.asStateFlow()

    // ========== Actions ==========

    /**
     * Capture a companion of the given type
     */
    fun captureCompanion(companionType: CompanionType) {
        viewModelScope.launch {
            when (val result = captureCompanionUseCase(companionType)) {
                is CaptureResult.Success -> {
                    _uiEvent.value = CompanionUiEvent.CaptureSuccess(result.companion)
                }
                is CaptureResult.AlreadyCaptured -> {
                    _uiEvent.value = CompanionUiEvent.Error("Ya has capturado este compañero")
                }
                is CaptureResult.RequirementNotMet -> {
                    _uiEvent.value = CompanionUiEvent.Error(
                        "Requisito no cumplido: ${result.requirementDescription}"
                    )
                }
            }
        }
    }

    /**
     * Invest energy in a companion
     */
    fun investEnergy(companionId: String, amount: Int) {
        viewModelScope.launch {
            when (val result = investEnergyUseCase(companionId, amount)) {
                is InvestEnergyResult.Success -> {
                    if (result.canNowEvolve) {
                        _uiEvent.value = CompanionUiEvent.CanEvolve(result.companion)
                    } else {
                        _uiEvent.value = CompanionUiEvent.EnergyInvested(
                            amount = result.energyInvested,
                            total = result.totalEnergyInvested
                        )
                    }
                }
                is InvestEnergyResult.InsufficientEnergy -> {
                    _uiEvent.value = CompanionUiEvent.Error(
                        "Energía insuficiente. Necesitas ${result.shortage} más energía."
                    )
                }
                is InvestEnergyResult.CompanionNotFound -> {
                    _uiEvent.value = CompanionUiEvent.Error("Compañero no encontrado")
                }
                is InvestEnergyResult.AlreadyMaxEvolution -> {
                    _uiEvent.value = CompanionUiEvent.Error("Ya está en evolución máxima")
                }
            }
        }
    }

    /**
     * Evolve a companion to the next state
     */
    fun evolveCompanion(companionId: String) {
        viewModelScope.launch {
            when (val result = evolveCompanionUseCase(companionId)) {
                is EvolutionResult.Success -> {
                    _uiEvent.value = CompanionUiEvent.EvolutionSuccess(
                        companion = result.companion,
                        newState = result.newEvolutionState
                    )
                }
                is EvolutionResult.InsufficientEnergy -> {
                    _uiEvent.value = CompanionUiEvent.Error(
                        "Energía insuficiente. Necesitas ${result.shortage} más energía invertida."
                    )
                }
                is EvolutionResult.AlreadyMaxEvolution -> {
                    _uiEvent.value = CompanionUiEvent.Error("Ya está en evolución máxima")
                }
                is EvolutionResult.CompanionNotFound -> {
                    _uiEvent.value = CompanionUiEvent.Error("Compañero no encontrado")
                }
            }
        }
    }

    /**
     * Set a companion as active
     */
    fun setActiveCompanion(companionId: String) {
        viewModelScope.launch {
            repository.setActiveCompanion(companionId)
            val companion = capturedCompanions.first().find { it.id == companionId }
            if (companion != null) {
                _uiEvent.value = CompanionUiEvent.ActiveCompanionChanged(companion)
            }
        }
    }

    /**
     * Clear the current UI event (after handling)
     */
    fun clearUiEvent() {
        _uiEvent.value = null
    }

    /**
     * Get a specific companion by ID
     */
    suspend fun getCompanionById(id: String): Companion? {
        return capturedCompanions.first().find { it.id == id }
    }
}

/**
 * Represents the state of a companion type in the UI
 */
sealed class CompanionState {
    abstract val companionType: CompanionType

    /**
     * Companion is locked (requirements not met)
     */
    data class Locked(override val companionType: CompanionType) : CompanionState()

    /**
     * Companion is available to capture (requirements met)
     */
    data class Available(override val companionType: CompanionType) : CompanionState()

    /**
     * Companion has been captured
     */
    data class Captured(val companion: Companion) : CompanionState() {
        override val companionType: CompanionType
            get() = companion.type
    }
}

/**
 * UI events emitted by the ViewModel
 */
sealed class CompanionUiEvent {
    data class CaptureSuccess(val companion: Companion) : CompanionUiEvent()
    data class EnergyInvested(val amount: Int, val total: Int) : CompanionUiEvent()
    data class CanEvolve(val companion: Companion) : CompanionUiEvent()
    data class EvolutionSuccess(val companion: Companion, val newState: Int) : CompanionUiEvent()
    data class ActiveCompanionChanged(val companion: Companion) : CompanionUiEvent()
    data class Error(val message: String) : CompanionUiEvent()
}
