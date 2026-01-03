# Onboarding Module Specification

**Estado:** 🟢 Activo
**Última actualización:** 2026-01-03
**Versión:** 1.0.0
**Líneas estimadas:** ~400

---

## 1. Visión General

### 1.1 Propósito

El módulo Onboarding guía a nuevos usuarios a través de la configuración inicial de Umbral. Presenta la propuesta de valor, solicita permisos necesarios, y ayuda a crear el primer perfil de bloqueo.

### 1.2 Objetivos

1. **Educar** - Explicar qué hace Umbral y cómo funciona
2. **Permisos** - Solicitar permisos necesarios de forma gradual
3. **Configurar** - Crear el primer perfil funcional
4. **Activar** - Lograr que el usuario active el bloqueo por primera vez

### 1.3 Flujo de Pantallas

```
┌─────────────┐    ┌──────────────┐    ┌─────────────────┐
│   Welcome   │───▶│  How It      │───▶│   Permissions   │
│   Screen    │    │  Works       │    │   Screen        │
└─────────────┘    └──────────────┘    └────────┬────────┘
                                                │
┌─────────────┐    ┌──────────────┐    ┌────────▼────────┐
│   Success   │◀───│  First       │◀───│   Select Apps   │
│   Screen    │    │  Profile     │    │   Screen        │
└──────┬──────┘    └──────────────┘    └─────────────────┘
       │
       ▼
   Home Screen
```

### 1.4 Dependencias

```
Depende de:
├── profiles-module (creación de perfil)
├── blocking-module (verificación de permisos)
└── ui-module (componentes visuales)

Dependido por:
└── MainActivity (punto de entrada)
```

---

## 2. Modelos de Dominio

### 2.1 Estado del Onboarding

```kotlin
// domain/model/OnboardingState.kt

/**
 * Estado completo del onboarding
 */
data class OnboardingState(
    val currentStep: OnboardingStep,
    val completedSteps: Set<OnboardingStep>,
    val permissionStates: PermissionStates,
    val selectedApps: List<String>,
    val profileName: String,
    val isComplete: Boolean
)

enum class OnboardingStep {
    WELCOME,
    HOW_IT_WORKS,
    PERMISSIONS,
    SELECT_APPS,
    CREATE_PROFILE,
    SUCCESS
}

data class PermissionStates(
    val usageStats: PermissionStatus,
    val overlay: PermissionStatus,
    val notifications: PermissionStatus,
    val nfc: NfcStatus
)

enum class PermissionStatus {
    NOT_REQUESTED,
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED
}

enum class NfcStatus {
    NOT_AVAILABLE,
    DISABLED,
    ENABLED
}
```

### 2.2 Preferencias de Onboarding

```kotlin
// domain/model/OnboardingPreferences.kt

/**
 * Preferencias persistidas del onboarding
 */
data class OnboardingPreferences(
    val hasCompletedOnboarding: Boolean,
    val completedAt: Long?,
    val skippedPermissions: Set<String>,
    val firstProfileId: String?
)
```

---

## 3. Interfaces Públicas

### 3.1 OnboardingManager

```kotlin
// domain/OnboardingManager.kt
interface OnboardingManager {

    /**
     * Estado actual del onboarding
     */
    val state: StateFlow<OnboardingState>

    /**
     * Verifica si el onboarding está completo
     */
    suspend fun isOnboardingComplete(): Boolean

    /**
     * Avanza al siguiente paso
     */
    fun nextStep()

    /**
     * Retrocede al paso anterior
     */
    fun previousStep()

    /**
     * Salta un paso opcional
     */
    fun skipStep()

    /**
     * Actualiza las apps seleccionadas
     */
    fun updateSelectedApps(apps: List<String>)

    /**
     * Actualiza el nombre del perfil
     */
    fun updateProfileName(name: String)

    /**
     * Crea el perfil inicial y completa el onboarding
     */
    suspend fun completeOnboarding(): Result<String>  // Returns profile ID

    /**
     * Resetea el onboarding (para testing)
     */
    suspend fun resetOnboarding()
}
```

### 3.2 PermissionHelper

```kotlin
// domain/PermissionHelper.kt
interface PermissionHelper {

    /**
     * Verifica el estado de todos los permisos
     */
    fun checkAllPermissions(): PermissionStates

    /**
     * Verifica un permiso específico
     */
    fun checkPermission(permission: RequiredPermission): PermissionStatus

    /**
     * Abre settings para un permiso específico
     */
    fun openPermissionSettings(permission: RequiredPermission)

    /**
     * Verifica si NFC está disponible y habilitado
     */
    fun checkNfcStatus(): NfcStatus

    /**
     * Abre settings de NFC
     */
    fun openNfcSettings()

    /**
     * Permisos mínimos requeridos para funcionar
     */
    fun hasMinimumPermissions(): Boolean

    /**
     * Todos los permisos recomendados
     */
    fun hasAllRecommendedPermissions(): Boolean
}

enum class RequiredPermission {
    USAGE_STATS,      // Obligatorio
    OVERLAY,          // Obligatorio
    NOTIFICATIONS,    // Recomendado
    POST_NOTIFICATIONS // Android 13+
}
```

---

## 4. Pantallas

### 4.1 Welcome Screen

```kotlin
// ui/onboarding/WelcomeScreen.kt
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(UmbralDimens.spaceXxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo animado
            AnimatedUmbralLogo(
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(UmbralDimens.spaceXxl))

            // Título
            Text(
                text = stringResource(R.string.onboarding_welcome_title),
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(UmbralDimens.spaceLg))

            // Subtítulo
            Text(
                text = stringResource(R.string.onboarding_welcome_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(UmbralDimens.spaceXxxl))

            // Botón de inicio
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = stringResource(R.string.onboarding_get_started),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
```

### 4.2 How It Works Screen

```kotlin
// ui/onboarding/HowItWorksScreen.kt
@Composable
fun HowItWorksScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
            // Pager con explicaciones
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                ExplanationPage(
                    page = howItWorksPages[page]
                )
            }

            // Indicadores de página
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(UmbralDimens.spaceLg),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { index ->
                    PageIndicator(
                        isSelected = pagerState.currentPage == index
                    )
                }
            }

            // Botón continuar
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = UmbralDimens.screenPaddingHorizontal)
                    .padding(bottom = UmbralDimens.spaceXl)
            ) {
                Text(stringResource(R.string.btn_continue))
            }
        }
    }
}

@Composable
private fun ExplanationPage(
    page: HowItWorksPage
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(UmbralDimens.spaceXl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ilustración
        Image(
            painter = painterResource(page.illustration),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(UmbralDimens.spaceXl))

        // Título
        Text(
            text = stringResource(page.title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(UmbralDimens.spaceMd))

        // Descripción
        Text(
            text = stringResource(page.description),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class HowItWorksPage(
    @DrawableRes val illustration: Int,
    @StringRes val title: Int,
    @StringRes val description: Int
)

private val howItWorksPages = listOf(
    HowItWorksPage(
        illustration = R.drawable.ill_nfc_tap,
        title = R.string.onboarding_how_1_title,
        description = R.string.onboarding_how_1_desc
    ),
    HowItWorksPage(
        illustration = R.drawable.ill_apps_blocked,
        title = R.string.onboarding_how_2_title,
        description = R.string.onboarding_how_2_desc
    ),
    HowItWorksPage(
        illustration = R.drawable.ill_focus,
        title = R.string.onboarding_how_3_title,
        description = R.string.onboarding_how_3_desc
    )
)
```

### 4.3 Permissions Screen

```kotlin
// ui/onboarding/PermissionsScreen.kt
@Composable
fun PermissionsScreen(
    viewModel: OnboardingViewModel = hiltViewModel(),
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.onboarding_permissions_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = UmbralDimens.screenPaddingHorizontal)
        ) {
            // Explicación
            Text(
                text = stringResource(R.string.onboarding_permissions_explanation),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = UmbralDimens.spaceLg)
            )

            // Lista de permisos
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceMd)
            ) {
                // Usage Stats (obligatorio)
                item {
                    PermissionCard(
                        icon = Icons.Default.BarChart,
                        title = stringResource(R.string.permission_usage_title),
                        description = stringResource(R.string.permission_usage_desc),
                        status = uiState.permissionStates.usageStats,
                        isRequired = true,
                        onRequestPermission = {
                            viewModel.openPermissionSettings(RequiredPermission.USAGE_STATS)
                        }
                    )
                }

                // Overlay (obligatorio)
                item {
                    PermissionCard(
                        icon = Icons.Default.Layers,
                        title = stringResource(R.string.permission_overlay_title),
                        description = stringResource(R.string.permission_overlay_desc),
                        status = uiState.permissionStates.overlay,
                        isRequired = true,
                        onRequestPermission = {
                            viewModel.openPermissionSettings(RequiredPermission.OVERLAY)
                        }
                    )
                }

                // Notifications (recomendado)
                item {
                    PermissionCard(
                        icon = Icons.Default.Notifications,
                        title = stringResource(R.string.permission_notifications_title),
                        description = stringResource(R.string.permission_notifications_desc),
                        status = uiState.permissionStates.notifications,
                        isRequired = false,
                        onRequestPermission = {
                            viewModel.openPermissionSettings(RequiredPermission.NOTIFICATIONS)
                        }
                    )
                }

                // NFC status
                item {
                    NfcStatusCard(
                        status = uiState.permissionStates.nfc,
                        onOpenSettings = { viewModel.openNfcSettings() }
                    )
                }
            }

            // Botón continuar
            val canContinue = uiState.permissionStates.usageStats == PermissionStatus.GRANTED &&
                    uiState.permissionStates.overlay == PermissionStatus.GRANTED

            Button(
                onClick = onContinue,
                enabled = canContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = UmbralDimens.spaceLg)
            ) {
                Text(stringResource(R.string.btn_continue))
            }

            if (!canContinue) {
                Text(
                    text = stringResource(R.string.onboarding_permissions_required),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = UmbralDimens.spaceMd)
                )
            }
        }
    }
}

@Composable
private fun PermissionCard(
    icon: ImageVector,
    title: String,
    description: String,
    status: PermissionStatus,
    isRequired: Boolean,
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when (status) {
                PermissionStatus.GRANTED -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                PermissionStatus.DENIED,
                PermissionStatus.PERMANENTLY_DENIED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralDimens.spaceLg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceLg)
        ) {
            // Icono
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = when (status) {
                    PermissionStatus.GRANTED -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(32.dp)
            )

            // Texto
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceXs)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall
                    )
                    if (isRequired) {
                        Text(
                            text = "*",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Estado / Botón
            when (status) {
                PermissionStatus.GRANTED -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(R.string.granted),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                else -> {
                    TextButton(onClick = onRequestPermission) {
                        Text(stringResource(R.string.btn_grant))
                    }
                }
            }
        }
    }
}

@Composable
private fun NfcStatusCard(
    status: NfcStatus,
    onOpenSettings: () -> Unit
) {
    val (icon, title, description, actionText) = when (status) {
        NfcStatus.ENABLED -> Triple(
            Icons.Default.Nfc,
            R.string.nfc_enabled,
            R.string.nfc_enabled_desc
        ) to null

        NfcStatus.DISABLED -> Triple(
            Icons.Default.NfcOff,
            R.string.nfc_disabled,
            R.string.nfc_disabled_desc
        ) to R.string.btn_enable

        NfcStatus.NOT_AVAILABLE -> Triple(
            Icons.Default.NfcOff,
            R.string.nfc_not_available,
            R.string.nfc_not_available_desc
        ) to null
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralDimens.spaceLg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceLg)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (status == NfcStatus.ENABLED)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = stringResource(description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (actionText != null) {
                TextButton(onClick = onOpenSettings) {
                    Text(stringResource(actionText))
                }
            } else if (status == NfcStatus.ENABLED) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
```

### 4.4 Select Apps Screen

```kotlin
// ui/onboarding/SelectAppsScreen.kt
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
                title = { Text(stringResource(R.string.onboarding_select_apps_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
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
                text = stringResource(R.string.onboarding_select_apps_subtitle),
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
                placeholder = { Text(stringResource(R.string.search_apps)) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = UmbralDimens.screenPaddingHorizontal)
            )

            // Quick select presets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(UmbralDimens.spaceLg),
                horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceSm)
            ) {
                PresetChip(
                    text = stringResource(R.string.preset_social),
                    onClick = { viewModel.selectPreset(AppPreset.SOCIAL) }
                )
                PresetChip(
                    text = stringResource(R.string.preset_games),
                    onClick = { viewModel.selectPreset(AppPreset.GAMES) }
                )
                PresetChip(
                    text = stringResource(R.string.preset_entertainment),
                    onClick = { viewModel.selectPreset(AppPreset.ENTERTAINMENT) }
                )
            }

            // Contador de seleccionadas
            Text(
                text = stringResource(
                    R.string.apps_selected_count,
                    uiState.selectedApps.size
                ),
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
            Button(
                onClick = onContinue,
                enabled = uiState.selectedApps.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(UmbralDimens.screenPaddingHorizontal)
                    .padding(bottom = UmbralDimens.spaceLg)
            ) {
                Text(
                    stringResource(
                        R.string.btn_continue_with_apps,
                        uiState.selectedApps.size
                    )
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

enum class AppPreset {
    SOCIAL,
    GAMES,
    ENTERTAINMENT
}
```

### 4.5 Success Screen

```kotlin
// ui/onboarding/SuccessScreen.kt
@Composable
fun SuccessScreen(
    profileName: String,
    appsCount: Int,
    onStartBlocking: () -> Unit,
    onLater: () -> Unit
) {
    val confettiController = rememberConfettiController()

    LaunchedEffect(Unit) {
        confettiController.fire()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Confetti
        ConfettiEffect(controller = confettiController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(UmbralDimens.spaceXxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Checkmark animado
            AnimatedSuccessIcon(
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(UmbralDimens.spaceXl))

            // Título
            Text(
                text = stringResource(R.string.onboarding_success_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(UmbralDimens.spaceMd))

            // Resumen
            Text(
                text = stringResource(
                    R.string.onboarding_success_summary,
                    profileName,
                    appsCount
                ),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(UmbralDimens.spaceXxxl))

            // Botón principal
            Button(
                onClick = onStartBlocking,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.btn_start_blocking))
            }

            Spacer(modifier = Modifier.height(UmbralDimens.spaceMd))

            // Botón secundario
            TextButton(onClick = onLater) {
                Text(stringResource(R.string.btn_later))
            }
        }
    }
}

@Composable
private fun AnimatedSuccessIcon(
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0f) }
    val checkProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        checkProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(300, easing = LinearEasing)
        )
    }

    Box(
        modifier = modifier
            .scale(scale.value)
            .background(
                MaterialTheme.colorScheme.primaryContainer,
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        // Checkmark path animation
        Canvas(modifier = Modifier.size(50.dp)) {
            val path = Path().apply {
                moveTo(size.width * 0.2f, size.height * 0.5f)
                lineTo(size.width * 0.4f, size.height * 0.7f)
                lineTo(size.width * 0.8f, size.height * 0.3f)
            }

            val pathMeasure = PathMeasure()
            pathMeasure.setPath(path, false)

            val animatedPath = Path()
            pathMeasure.getSegment(
                0f,
                pathMeasure.length * checkProgress.value,
                animatedPath,
                true
            )

            drawPath(
                path = animatedPath,
                color = Color(0xFF1A237E),
                style = Stroke(
                    width = 6.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }
    }
}
```

---

## 5. Strings

```xml
<!-- res/values/strings.xml -->
<resources>
    <!-- Onboarding - Welcome -->
    <string name="onboarding_welcome_title">Bienvenido a Umbral</string>
    <string name="onboarding_welcome_subtitle">Recupera tu atención. Bloquea distracciones con un simple tap NFC.</string>
    <string name="onboarding_get_started">Comenzar</string>

    <!-- Onboarding - How It Works -->
    <string name="onboarding_how_1_title">Configura tu tag NFC</string>
    <string name="onboarding_how_1_desc">Escribe un tag NFC con tu perfil de bloqueo. Puedes usar cualquier tag NTAG213/215/216.</string>
    <string name="onboarding_how_2_title">Toca para bloquear</string>
    <string name="onboarding_how_2_desc">Cuando toques el tag, las apps seleccionadas se bloquean automáticamente.</string>
    <string name="onboarding_how_3_title">Enfócate sin distracciones</string>
    <string name="onboarding_how_3_desc">Trabaja, estudia o descansa sin la tentación de abrir apps adictivas.</string>

    <!-- Onboarding - Permissions -->
    <string name="onboarding_permissions_title">Permisos necesarios</string>
    <string name="onboarding_permissions_explanation">Umbral necesita algunos permisos para funcionar correctamente. Estos permisos son necesarios para detectar qué apps están abiertas y mostrar la pantalla de bloqueo.</string>
    <string name="onboarding_permissions_required">Los permisos marcados con * son obligatorios</string>

    <string name="permission_usage_title">Acceso a uso de apps</string>
    <string name="permission_usage_desc">Necesario para detectar cuándo abres una app bloqueada</string>
    <string name="permission_overlay_title">Mostrar sobre otras apps</string>
    <string name="permission_overlay_desc">Necesario para mostrar la pantalla de bloqueo</string>
    <string name="permission_notifications_title">Notificaciones</string>
    <string name="permission_notifications_desc">Recomendado para mostrarte el estado del bloqueo</string>

    <string name="nfc_enabled">NFC habilitado</string>
    <string name="nfc_enabled_desc">Podrás usar tags NFC para activar el bloqueo</string>
    <string name="nfc_disabled">NFC deshabilitado</string>
    <string name="nfc_disabled_desc">Habilita NFC para usar tags físicos. También puedes usar códigos QR.</string>
    <string name="nfc_not_available">NFC no disponible</string>
    <string name="nfc_not_available_desc">Tu dispositivo no tiene NFC. Podrás usar códigos QR como alternativa.</string>

    <string name="btn_grant">Permitir</string>
    <string name="btn_enable">Habilitar</string>
    <string name="granted">Concedido</string>

    <!-- Onboarding - Select Apps -->
    <string name="onboarding_select_apps_title">Selecciona apps a bloquear</string>
    <string name="onboarding_select_apps_subtitle">Elige las apps que te distraen más. Podrás cambiar esto después.</string>
    <string name="search_apps">Buscar apps…</string>
    <string name="preset_social">+ Redes sociales</string>
    <string name="preset_games">+ Juegos</string>
    <string name="preset_entertainment">+ Entretenimiento</string>
    <string name="apps_selected_count">%d apps seleccionadas</string>
    <string name="btn_continue_with_apps">Continuar con %d apps</string>

    <!-- Onboarding - Success -->
    <string name="onboarding_success_title">¡Todo listo!</string>
    <string name="onboarding_success_summary">Tu perfil "%1$s" está configurado con %2$d apps para bloquear.</string>
    <string name="btn_start_blocking">Activar bloqueo ahora</string>
    <string name="btn_later">Más tarde</string>
</resources>
```

---

## 6. Persistencia

```kotlin
// data/OnboardingPreferencesDataStore.kt
class OnboardingPreferencesDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
        val COMPLETED_AT_KEY = longPreferencesKey("onboarding_completed_at")
        val FIRST_PROFILE_ID_KEY = stringPreferencesKey("first_profile_id")
        val SKIPPED_PERMISSIONS_KEY = stringSetPreferencesKey("skipped_permissions")
    }

    val preferences: Flow<OnboardingPreferences> = dataStore.data.map { prefs ->
        OnboardingPreferences(
            hasCompletedOnboarding = prefs[COMPLETED_KEY] ?: false,
            completedAt = prefs[COMPLETED_AT_KEY],
            skippedPermissions = prefs[SKIPPED_PERMISSIONS_KEY] ?: emptySet(),
            firstProfileId = prefs[FIRST_PROFILE_ID_KEY]
        )
    }

    suspend fun markComplete(profileId: String) {
        dataStore.edit { prefs ->
            prefs[COMPLETED_KEY] = true
            prefs[COMPLETED_AT_KEY] = System.currentTimeMillis()
            prefs[FIRST_PROFILE_ID_KEY] = profileId
        }
    }

    suspend fun reset() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
```

---

## 7. Testing

```kotlin
// test/OnboardingViewModelTest.kt
class OnboardingViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: OnboardingViewModel
    private lateinit var onboardingManager: OnboardingManager
    private lateinit var permissionHelper: PermissionHelper

    @Before
    fun setup() {
        onboardingManager = mockk(relaxed = true)
        permissionHelper = mockk()
        viewModel = OnboardingViewModel(onboardingManager, permissionHelper)
    }

    @Test
    fun `nextStep advances to next step`() {
        viewModel.nextStep()
        verify { onboardingManager.nextStep() }
    }

    @Test
    fun `completeOnboarding creates profile and marks complete`() = runTest {
        coEvery { onboardingManager.completeOnboarding() } returns Result.success("profile-1")

        viewModel.completeOnboarding()

        coVerify { onboardingManager.completeOnboarding() }
    }
}
```

---

## 8. Criterios de Aceptación

### 8.1 Flujo General
- [ ] Usuario puede completar onboarding en < 2 minutos
- [ ] Cada pantalla es clara y no abrumadora
- [ ] Navegación back funciona correctamente
- [ ] Estado se preserva si app se cierra durante onboarding

### 8.2 Permisos
- [ ] Permisos obligatorios bloquean continuar si no están concedidos
- [ ] Links a settings funcionan correctamente
- [ ] Estado de NFC se detecta correctamente
- [ ] Mensaje claro si NFC no disponible

### 8.3 Selección de Apps
- [ ] Búsqueda filtra correctamente
- [ ] Presets seleccionan apps correctas
- [ ] Al menos 1 app debe estar seleccionada para continuar
- [ ] Iconos de apps se cargan correctamente

### 8.4 Completar
- [ ] Perfil se crea correctamente
- [ ] Preferencias se guardan
- [ ] Onboarding no se muestra de nuevo
- [ ] Animación de éxito se reproduce

---

**Creado:** 2026-01-03
**Autor/Mantenedor:** Equipo Umbral
