package com.umbral.expedition.presentation.companion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.umbral.R
import com.umbral.expedition.domain.model.Companion
import com.umbral.expedition.domain.model.Element
import com.umbral.expedition.presentation.animation.CompanionAnimation
import com.umbral.expedition.presentation.animation.CompanionAnimationState
import com.umbral.expedition.presentation.companion.components.EvolutionProgress
import kotlinx.coroutines.launch

/**
 * Detail screen for a captured companion.
 *
 * Shows:
 * - Large companion display with element color
 * - Evolution state and progress
 * - Energy investment slider
 * - Evolve button (when ready)
 * - Set as active button
 * - Passive bonus description
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanionDetailScreen(
    companionId: String,
    onNavigateBack: () -> Unit,
    viewModel: CompanionViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var companion by remember { mutableStateOf<Companion?>(null) }
    val progress by viewModel.progress.collectAsState()
    val activeCompanion by viewModel.activeCompanion.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()

    var energyToInvest by remember { mutableFloatStateOf(0f) }
    var showEvolutionDialog by remember { mutableStateOf(false) }
    var animationState by remember { mutableStateOf(CompanionAnimationState.IDLE) }
    val maxInvestAmount = remember(progress, companion) {
        progress?.totalEnergy?.coerceAtMost(
            companion?.let { it.evolutionCost - it.energyInvested } ?: 0
        ) ?: 0
    }

    // Load companion data
    LaunchedEffect(companionId) {
        companion = viewModel.getCompanionById(companionId)
    }

    // Handle UI events
    LaunchedEffect(uiEvent) {
        when (val event = uiEvent) {
            is CompanionUiEvent.EnergyInvested -> {
                snackbarHostState.showSnackbar("¡${event.amount} energía invertida!")
                animationState = CompanionAnimationState.HAPPY
                energyToInvest = 0f
                companion = viewModel.getCompanionById(companionId)
                viewModel.clearUiEvent()
            }
            is CompanionUiEvent.CanEvolve -> {
                snackbarHostState.showSnackbar("¡${event.companion.displayName} listo para evolucionar!")
                animationState = CompanionAnimationState.HAPPY
                companion = viewModel.getCompanionById(companionId)
                viewModel.clearUiEvent()
            }
            is CompanionUiEvent.EvolutionSuccess -> {
                snackbarHostState.showSnackbar("¡Evolucionó a Estado ${event.newState}!")
                animationState = CompanionAnimationState.IDLE
                companion = viewModel.getCompanionById(companionId)
                viewModel.clearUiEvent()
            }
            is CompanionUiEvent.ActiveCompanionChanged -> {
                snackbarHostState.showSnackbar("${event.companion.displayName} activado")
                animationState = CompanionAnimationState.HAPPY
                companion = viewModel.getCompanionById(companionId)
                viewModel.clearUiEvent()
            }
            is CompanionUiEvent.Error -> {
                snackbarHostState.showSnackbar(event.message)
                viewModel.clearUiEvent()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = companion?.displayName ?: "Compañero",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (companion == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cargando...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            val comp = companion!!
            val elementColor = parseElementColor(comp.element)
            val isActive = activeCompanion?.id == comp.id

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Lottie animation display
                CompanionAnimation(
                    companionType = comp.type,
                    animationState = animationState,
                    backgroundColor = elementColor,
                    height = 250.dp,
                    onAnimationComplete = {
                        // Reset to idle after happy/evolving animation completes
                        if (animationState != CompanionAnimationState.IDLE) {
                            animationState = CompanionAnimationState.IDLE
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Name and element
                Text(
                    text = comp.displayName,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(elementColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = comp.element.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Passive bonus card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = elementColor.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Bonificación Pasiva",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = elementColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = comp.passiveBonus.getDescription(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Evolution progress
                EvolutionProgress(
                    companion = comp,
                    accentColor = elementColor
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Energy investment section
                if (!comp.isMaxEvolution) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Invertir Energía",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Disponible: ${progress?.totalEnergy ?: 0}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Cantidad: ${energyToInvest.toInt()}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = elementColor
                            )

                            Slider(
                                value = energyToInvest,
                                onValueChange = { energyToInvest = it },
                                valueRange = 0f..maxInvestAmount.toFloat(),
                                steps = if (maxInvestAmount > 10) maxInvestAmount / 10 else 0,
                                enabled = maxInvestAmount > 0
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    scope.launch {
                                        viewModel.investEnergy(comp.id, energyToInvest.toInt())
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = energyToInvest.toInt() > 0,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = elementColor
                                )
                            ) {
                                Text("Invertir Energía")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Evolve button
                AnimatedVisibility(visible = comp.canEvolve) {
                    Button(
                        onClick = {
                            showEvolutionDialog = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¡EVOLUCIONAR!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                if (comp.canEvolve) {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Set as active button
                if (isActive) {
                    OutlinedButton(
                        onClick = { /* Already active */ },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = false
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.companion_already_active),
                            color = Color(0xFF4CAF50)
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.setActiveCompanion(comp.id)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = elementColor
                        )
                    ) {
                        Text(stringResource(R.string.companion_set_active))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Evolution dialog
    if (showEvolutionDialog && companion != null) {
        EvolutionDialog(
            companion = companion!!,
            elementColor = parseElementColor(companion!!.element),
            onConfirm = {
                scope.launch {
                    viewModel.evolveCompanion(companion!!.id)
                    animationState = CompanionAnimationState.EVOLVING
                }
            },
            onDismiss = {
                showEvolutionDialog = false
            }
        )
    }
}

/**
 * Parse element color from hex string
 */
private fun parseElementColor(element: Element): Color {
    return try {
        Color(android.graphics.Color.parseColor(element.color))
    } catch (e: Exception) {
        Color.Gray
    }
}
