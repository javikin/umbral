package com.umbral.expedition.presentation.companion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.umbral.R
import com.umbral.expedition.domain.model.Companion
import com.umbral.expedition.domain.model.Element
import com.umbral.expedition.presentation.companion.components.CompanionGridCard

/**
 * Screen showing grid of all companion types.
 *
 * Displays:
 * - 8 companion cards in 2-column grid
 * - States: locked, available, captured
 * - Click to view details or capture
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanionListScreen(
    onNavigateBack: () -> Unit,
    onCompanionClick: (String) -> Unit,
    viewModel: CompanionViewModel = hiltViewModel()
) {
    val companionStates by viewModel.companionStates.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showCaptureDialog by remember { mutableStateOf(false) }
    var capturedCompanion by remember { mutableStateOf<Companion?>(null) }

    // Handle UI events
    LaunchedEffect(uiEvent) {
        when (val event = uiEvent) {
            is CompanionUiEvent.CaptureSuccess -> {
                capturedCompanion = event.companion
                showCaptureDialog = true
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
                        text = "Compañeros",
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
                ),
                windowInsets = WindowInsets(0.dp)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Colecciona y evoluciona compañeros para obtener bonificaciones pasivas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stats summary
                val capturedCount = companionStates.count { it is CompanionState.Captured }
                val availableCount = companionStates.count { it is CompanionState.Available }

                Text(
                    text = stringResource(
                        R.string.companion_list_summary,
                        capturedCount,
                        8,
                        availableCount
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Companion grid
            if (companionStates.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Cargando compañeros...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(companionStates) { state ->
                        CompanionGridCard(
                            state = state,
                            onClick = {
                                when (state) {
                                    is CompanionState.Locked -> {
                                        // Show locked message
                                        // Could navigate to detail to show requirements
                                    }
                                    is CompanionState.Available -> {
                                        // Capture companion
                                        viewModel.captureCompanion(state.companionType)
                                    }
                                    is CompanionState.Captured -> {
                                        // Navigate to detail screen
                                        onCompanionClick(state.companion.id)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Capture success dialog
    if (showCaptureDialog && capturedCompanion != null) {
        CaptureSuccessDialog(
            companion = capturedCompanion!!,
            elementColor = parseElementColor(capturedCompanion!!.element),
            onDismiss = {
                showCaptureDialog = false
                capturedCompanion = null
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
