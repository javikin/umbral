package com.umbral.presentation.ui.screens.profiles

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.umbral.R
import com.umbral.domain.nfc.NfcTag
import com.umbral.presentation.viewmodel.ProfileDetailViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAppSelector: (List<String>) -> Unit = {},
    onNavigateToNfcScan: () -> Unit = {},
    onNavigateToQrScan: (String) -> Unit = {},
    selectedApps: List<String>? = null,
    modifier: Modifier = Modifier,
    viewModel: ProfileDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle selected apps returned from AppSelectorScreen
    LaunchedEffect(selectedApps) {
        selectedApps?.let { apps ->
            viewModel.setBlockedApps(apps)
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isNewProfile) "Nuevo perfil"
                        else "Editar perfil"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                },
                windowInsets = WindowInsets(0.dp)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name field
                item {
                    OutlinedTextField(
                        value = uiState.name,
                        onValueChange = viewModel::updateName,
                        label = { Text("Nombre del perfil") },
                        placeholder = { Text("Ej: Trabajo, Estudio, Noche...") },
                        singleLine = true,
                        isError = uiState.error != null && uiState.name.isBlank(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Color selector
                item {
                    ColorSelector(
                        selectedColor = uiState.colorHex,
                        onColorSelected = viewModel::updateColor
                    )
                }

                // Icon selector
                item {
                    IconSelector(
                        selectedIcon = uiState.iconName,
                        selectedColor = uiState.colorHex,
                        onIconSelected = viewModel::updateIcon
                    )
                }

                // Strict mode toggle
                item {
                    StrictModeCard(
                        isEnabled = uiState.isStrictMode,
                        onToggle = viewModel::toggleStrictMode
                    )
                }

                // Blocked apps section
                item {
                    BlockedAppsSection(
                        blockedApps = uiState.blockedApps,
                        onAddApp = { onNavigateToAppSelector(uiState.blockedApps) },
                        onRemoveApp = viewModel::removeBlockedApp
                    )
                }

                // Activation methods section
                item {
                    ActivationMethodsSection(
                        linkedTags = uiState.linkedTags,
                        availableTags = uiState.availableTags,
                        profileNames = uiState.profileNames,
                        profileId = uiState.profileId,
                        onUnlinkTag = viewModel::unlinkTag,
                        onLinkTag = viewModel::linkTagToProfile,
                        onAddNfcTag = onNavigateToNfcScan,
                        onAddQrCode = onNavigateToQrScan
                    )
                }

                // Save button
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.saveProfile(onNavigateBack) },
                        enabled = !uiState.isSaving && uiState.name.isNotBlank(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (uiState.isNewProfile) "Crear perfil" else "Guardar cambios")
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColorSelector(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeader(title = "Color")
        Spacer(modifier = Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileDetailViewModel.AVAILABLE_COLORS.forEach { colorHex ->
                val color = try {
                    Color(android.graphics.Color.parseColor(colorHex))
                } catch (e: Exception) {
                    MaterialTheme.colorScheme.primary
                }
                val isSelected = colorHex == selectedColor

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color)
                        .then(
                            if (isSelected) {
                                Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onColorSelected(colorHex) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun IconSelector(
    selectedIcon: String,
    selectedColor: String,
    onIconSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val color = try {
        Color(android.graphics.Color.parseColor(selectedColor))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    Column(modifier = modifier) {
        SectionHeader(title = "Icono")
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(ProfileDetailViewModel.AVAILABLE_ICONS) { iconName ->
                val icon = getIconForName(iconName)
                val isSelected = iconName == selectedIcon

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) color.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .then(
                            if (isSelected) {
                                Modifier.border(2.dp, color, CircleShape)
                            } else {
                                Modifier
                            }
                        )
                        .clickable { onIconSelected(iconName) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = iconName,
                        tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StrictModeCard(
    isEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) {
                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = if (isEnabled) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Modo estricto",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "No se puede desactivar el bloqueo sin el tag NFC",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() }
            )
        }
    }
}

@Composable
private fun BlockedAppsSection(
    blockedApps: List<String>,
    onAddApp: () -> Unit,
    onRemoveApp: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionHeader(title = "Apps bloqueadas (${blockedApps.size})")
            IconButton(onClick = onAddApp) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar app",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (blockedApps.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No hay apps bloqueadas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Toca + para agregar apps",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    blockedApps.forEachIndexed { index, packageName ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // TODO: Show app icon
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = packageName.take(1).uppercase(),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = getAppNameFromPackage(packageName),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { onRemoveApp(packageName) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Quitar",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivationMethodsSection(
    linkedTags: List<NfcTag>,
    availableTags: List<NfcTag>,
    profileNames: Map<String, String>,
    profileId: String,
    onUnlinkTag: (String) -> Unit,
    onLinkTag: (String) -> Unit,
    onAddNfcTag: () -> Unit,
    onAddQrCode: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showNfcBottomSheet by remember { mutableStateOf(false) }
    var showQrBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        SectionHeader(title = "Métodos de activación")
        Spacer(modifier = Modifier.height(8.dp))

        // Lista de tags asociados
        if (linkedTags.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    linkedTags.forEach { tag ->
                        val isQrCode = tag.uid.startsWith("QR_")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isQrCode) Icons.Default.QrCode2 else Icons.Default.Nfc,
                                contentDescription = if (isQrCode) "Código QR" else "Tag NFC",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = tag.name,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isQrCode) "QR" else "NFC",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                if (tag.location != null) {
                                    Text(
                                        text = tag.location,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(
                                onClick = { onUnlinkTag(tag.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Desvincular",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Botones para agregar nuevos métodos
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón para agregar tag NFC
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showNfcBottomSheet = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Nfc,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tag NFC",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Botón para agregar código QR
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showQrBottomSheet = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Código QR",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Mensaje informativo si no hay tags
        if (linkedTags.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Agrega un tag NFC o código QR para activar este perfil",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }

    // Bottom Sheet para seleccionar NFC tag
    if (showNfcBottomSheet) {
        ActivationMethodSelectorBottomSheet(
            title = "Tag NFC",
            icon = Icons.Default.Nfc,
            availableTags = availableTags.filter { !it.uid.startsWith("QR_") }, // Solo NFC
            profileNames = profileNames,
            onDismiss = { showNfcBottomSheet = false },
            onSelectTag = { tagId ->
                onLinkTag(tagId)
                scope.launch { showNfcBottomSheet = false }
            },
            onCreateNew = {
                showNfcBottomSheet = false
                onAddNfcTag()
            }
        )
    }

    // Bottom Sheet para seleccionar código QR
    if (showQrBottomSheet) {
        ActivationMethodSelectorBottomSheet(
            title = "Código QR",
            icon = Icons.Default.QrCode2,
            availableTags = availableTags.filter { it.uid.startsWith("QR_") }, // Solo QR
            profileNames = profileNames,
            onDismiss = { showQrBottomSheet = false },
            onSelectTag = { tagId ->
                onLinkTag(tagId)
                scope.launch { showQrBottomSheet = false }
            },
            onCreateNew = {
                showQrBottomSheet = false
                onAddQrCode(profileId)
            }
        )
    }
}

private fun getIconForName(name: String): ImageVector {
    return when (name) {
        "shield" -> Icons.Default.Shield
        "lock" -> Icons.Default.Lock
        "work" -> Icons.Default.Work
        "home" -> Icons.Default.Home
        "night" -> Icons.Default.Nightlight
        "focus" -> Icons.Default.Visibility
        "fitness" -> Icons.Default.FitnessCenter
        "study" -> Icons.Default.School
        else -> Icons.Default.Shield
    }
}

private fun getAppNameFromPackage(packageName: String): String {
    // TODO: Get real app name from PackageManager
    return packageName.split(".").lastOrNull()?.replaceFirstChar { it.uppercase() }
        ?: packageName
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivationMethodSelectorBottomSheet(
    title: String,
    icon: ImageVector,
    availableTags: List<NfcTag>,
    profileNames: Map<String, String>,
    onDismiss: () -> Unit,
    onSelectTag: (String) -> Unit,
    onCreateNew: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))

            // Lista de tags disponibles
            if (availableTags.isEmpty()) {
                // Estado vacío
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp, horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay tags disponibles",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Crea uno nuevo para vincular a este perfil",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            } else {
                // Lista de tags
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(availableTags) { tag ->
                        val linkedToOtherProfile = tag.profileId != null
                        val linkedProfileName = tag.profileId?.let { profileNames[it] }
                        TagListItem(
                            tag = tag,
                            linkedProfileName = linkedProfileName,
                            isEnabled = !linkedToOtherProfile,
                            onSelect = { if (!linkedToOtherProfile) onSelectTag(tag.id) }
                        )
                    }
                }
            }

            // Botón para crear nuevo
            HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable { onCreateNew() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Crear nuevo $title",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun TagListItem(
    tag: NfcTag,
    linkedProfileName: String?,
    isEnabled: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = remember {
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            .withZone(ZoneId.systemDefault())
    }

    val formattedDate = remember(tag.createdAt) {
        tag.createdAt
            .atZone(ZoneId.systemDefault())
            .format(dateFormatter)
    }

    val contentAlpha = if (isEnabled) 1f else 0.5f

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(
                if (isEnabled) Modifier.clickable { onSelect() }
                else Modifier
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled)
                MaterialTheme.colorScheme.surfaceVariant
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono del tag
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.primary.copy(
                            alpha = if (isEnabled) 0.1f else 0.05f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (tag.uid.startsWith("QR_")) Icons.Default.QrCode2 else Icons.Default.Nfc,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = contentAlpha),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del tag
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tag.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha)
                )
                if (tag.location != null) {
                    Text(
                        text = tag.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha)
                    )
                }
                // Mostrar el perfil vinculado si está deshabilitado
                if (!isEnabled && linkedProfileName != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Vinculado a: $linkedProfileName",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                        )
                    }
                } else {
                    Text(
                        text = "Creado: $formattedDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f * contentAlpha)
                    )
                }
            }

            // Icono de selección (solo si está habilitado)
            if (isEnabled) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = "Vincular",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
