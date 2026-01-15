package com.umbral.presentation.ui.screens.profiles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.umbral.R
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.presentation.ui.components.CreateProfileCard
import com.umbral.presentation.ui.components.ProfileCard
import com.umbral.presentation.ui.components.UmbralButton
import com.umbral.presentation.ui.components.ButtonVariant
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import com.umbral.presentation.viewmodel.ProfilesViewModel
import kotlinx.coroutines.delay

// =============================================================================
// PROFILES SCREEN
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(
    onNavigateToProfileDetail: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfilesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Animation state for staggered entrance
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            delay(100)
            showContent = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading) {
            // Loading state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else if (uiState.profiles.isEmpty()) {
            // Empty state
            EmptyProfilesContent(
                onCreateProfile = viewModel::createDefaultProfile,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Profiles list with staggered animation
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = UmbralSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(UmbralSpacing.cardSpacing)
            ) {
                item { Spacer(modifier = Modifier.height(UmbralSpacing.md)) }

                itemsIndexed(
                    items = uiState.profiles,
                    key = { _, profile -> profile.id }
                ) { index, profile ->
                    AnimatedVisibility(
                        visible = showContent,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                delayMillis = index * 50
                            )
                        ) + slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { 50 }
                        ),
                        exit = fadeOut() + slideOutVertically()
                    ) {
                        ProfileCard(
                            profile = profile,
                            onClick = { onNavigateToProfileDetail(profile.id) },
                            onToggleActive = {
                                if (profile.isActive) {
                                    viewModel.deactivateProfile(profile.id)
                                } else {
                                    viewModel.activateProfile(profile.id)
                                }
                            },
                            onEdit = { onNavigateToProfileDetail(profile.id) },
                            onDelete = { viewModel.showDeleteDialog(profile) }
                        )
                    }
                }

                // Create new profile card at bottom
                item {
                    AnimatedVisibility(
                        visible = showContent,
                        enter = fadeIn(
                            animationSpec = tween(
                                durationMillis = 300,
                                delayMillis = uiState.profiles.size * 50
                            )
                        ) + slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { 50 }
                        )
                    ) {
                        CreateProfileCard(
                            onClick = { onNavigateToProfileDetail("new") }
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(UmbralSpacing.lg)) }
            }
        }

        // Delete confirmation dialog
        if (uiState.showDeleteDialog && uiState.selectedProfile != null) {
            DeleteProfileDialog(
                profileName = uiState.selectedProfile!!.name,
                onConfirm = viewModel::deleteProfile,
                onDismiss = viewModel::hideDeleteDialog
            )
        }
    }
}

// =============================================================================
// EMPTY STATE
// =============================================================================

@Composable
private fun EmptyProfilesContent(
    onCreateProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(UmbralSpacing.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(UmbralSpacing.md))

        Text(
            text = stringResource(R.string.profiles_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(UmbralSpacing.lg))

        UmbralButton(
            text = stringResource(R.string.create_profile),
            onClick = onCreateProfile,
            variant = ButtonVariant.Primary,
            leadingIcon = Icons.Default.Add
        )
    }
}

// =============================================================================
// DELETE DIALOG
// =============================================================================

@Composable
private fun DeleteProfileDialog(
    profileName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.delete_profile),
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Text("¿Estás seguro de que quieres eliminar \"$profileName\"? Esta acción no se puede deshacer.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Profiles Screen - Empty", showBackground = true)
@Composable
private fun ProfilesScreenEmptyPreview() {
    UmbralTheme {
        ProfilesScreenContent(
            profiles = emptyList(),
            isLoading = false
        )
    }
}

@Preview(name = "Profiles Screen - With Profiles", showBackground = true)
@Composable
private fun ProfilesScreenWithProfilesPreview() {
    UmbralTheme {
        ProfilesScreenContent(
            profiles = listOf(
                BlockingProfile(
                    id = "1",
                    name = "Productividad",
                    iconName = "work",
                    colorHex = "#6650A4",
                    isActive = true,
                    blockedApps = listOf("1", "2", "3", "4", "5", "6", "7", "8")
                ),
                BlockingProfile(
                    id = "2",
                    name = "Noche",
                    iconName = "night",
                    colorHex = "#1E88E5",
                    isActive = false,
                    blockedApps = (1..12).map { it.toString() }
                ),
                BlockingProfile(
                    id = "3",
                    name = "Estudio",
                    iconName = "book",
                    colorHex = "#43A047",
                    isActive = false,
                    isStrictMode = true,
                    blockedApps = listOf("1", "2", "3", "4", "5")
                )
            ),
            isLoading = false
        )
    }
}

@Preview(name = "Profiles Screen - Loading", showBackground = true)
@Composable
private fun ProfilesScreenLoadingPreview() {
    UmbralTheme {
        ProfilesScreenContent(
            profiles = emptyList(),
            isLoading = true
        )
    }
}

@Preview(name = "Profiles Screen - Dark Theme", showBackground = true)
@Composable
private fun ProfilesScreenDarkPreview() {
    UmbralTheme(darkTheme = true) {
        ProfilesScreenContent(
            profiles = listOf(
                BlockingProfile(
                    id = "1",
                    name = "Productividad",
                    iconName = "work",
                    colorHex = "#6650A4",
                    isActive = true,
                    blockedApps = listOf("1", "2", "3", "4", "5", "6", "7", "8")
                ),
                BlockingProfile(
                    id = "2",
                    name = "Noche",
                    iconName = "night",
                    colorHex = "#1E88E5",
                    isActive = false,
                    blockedApps = (1..12).map { it.toString() }
                )
            ),
            isLoading = false
        )
    }
}

// =============================================================================
// PREVIEW HELPER
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfilesScreenContent(
    profiles: List<BlockingProfile>,
    isLoading: Boolean
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Perfiles",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Crear perfil"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else if (profiles.isEmpty()) {
            EmptyProfilesContent(
                onCreateProfile = {},
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = UmbralSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(UmbralSpacing.cardSpacing)
            ) {
                item { Spacer(modifier = Modifier.height(UmbralSpacing.sm)) }

                itemsIndexed(
                    items = profiles,
                    key = { _, profile -> profile.id }
                ) { _, profile ->
                    ProfileCard(
                        profile = profile,
                        onClick = {},
                        onToggleActive = {},
                        onEdit = {},
                        onDelete = {}
                    )
                }

                item {
                    CreateProfileCard(onClick = {})
                }

                item { Spacer(modifier = Modifier.height(UmbralSpacing.lg)) }
            }
        }
    }
}
