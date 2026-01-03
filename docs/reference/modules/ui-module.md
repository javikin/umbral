# UI Module Specification

**Estado:** 🟢 Activo
**Última actualización:** 2026-01-03
**Versión:** 1.0.0
**Líneas estimadas:** ~900

---

## 1. Visión General

### 1.1 Propósito

El módulo UI proporciona la interfaz de usuario completa de Umbral, implementando Material Design 3 con Jetpack Compose. Define la navegación, componentes reutilizables, sistema de diseño, widgets y accesibilidad.

### 1.2 Alcance

- Sistema de navegación con Navigation Compose
- Design system con tokens de Material 3
- Pantallas principales y secundarias
- Componentes reutilizables
- Widgets para Home Screen
- Quick Settings Tiles
- Temas claro/oscuro
- Accesibilidad completa

### 1.3 Dependencias

```
Depende de:
├── nfc-module (estados de NFC para UI)
├── blocking-module (estados de bloqueo para UI)
└── profiles-module (datos de perfiles para display)

Dependido por:
└── Ninguno (módulo de presentación)
```

---

## 2. Arquitectura de Navegación

### 2.1 Estructura de Navegación

```
NavGraph
├── MainGraph (autenticado)
│   ├── HomeScreen (pantalla principal)
│   ├── ProfilesScreen (lista de perfiles)
│   │   ├── ProfileDetailScreen
│   │   └── ProfileEditScreen
│   ├── StatsScreen (estadísticas de uso)
│   ├── SettingsScreen (configuración)
│   │   ├── NfcSettingsScreen
│   │   ├── AppearanceSettingsScreen
│   │   └── AboutScreen
│   └── NfcScanScreen (escaneo NFC modal)
└── OnboardingGraph (primer uso)
    ├── WelcomeScreen
    ├── PermissionsScreen
    └── SetupProfileScreen
```

### 2.2 Definición de Rutas

```kotlin
// navigation/UmbralDestinations.kt
sealed class UmbralDestination(val route: String) {
    // Main destinations
    object Home : UmbralDestination("home")
    object Profiles : UmbralDestination("profiles")
    object Stats : UmbralDestination("stats")
    object Settings : UmbralDestination("settings")

    // Profile sub-destinations
    object ProfileDetail : UmbralDestination("profiles/{profileId}") {
        fun createRoute(profileId: String) = "profiles/$profileId"
    }

    object ProfileEdit : UmbralDestination("profiles/{profileId}/edit") {
        fun createRoute(profileId: String) = "profiles/$profileId/edit"
    }

    object ProfileCreate : UmbralDestination("profiles/new")

    // Settings sub-destinations
    object NfcSettings : UmbralDestination("settings/nfc")
    object AppearanceSettings : UmbralDestination("settings/appearance")
    object About : UmbralDestination("settings/about")

    // Modal destinations
    object NfcScan : UmbralDestination("nfc-scan")
    object QrScan : UmbralDestination("qr-scan")

    // Onboarding
    object Welcome : UmbralDestination("onboarding/welcome")
    object Permissions : UmbralDestination("onboarding/permissions")
    object SetupProfile : UmbralDestination("onboarding/setup")
}
```

### 2.3 NavHost Principal

```kotlin
// navigation/UmbralNavHost.kt
@Composable
fun UmbralNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Main graph
        composable(UmbralDestination.Home.route) {
            HomeScreen(
                onNavigateToProfiles = {
                    navController.navigate(UmbralDestination.Profiles.route)
                },
                onNavigateToNfcScan = {
                    navController.navigate(UmbralDestination.NfcScan.route)
                }
            )
        }

        composable(UmbralDestination.Profiles.route) {
            ProfilesScreen(
                onProfileClick = { profileId ->
                    navController.navigate(
                        UmbralDestination.ProfileDetail.createRoute(profileId)
                    )
                },
                onCreateProfile = {
                    navController.navigate(UmbralDestination.ProfileCreate.route)
                }
            )
        }

        composable(
            route = UmbralDestination.ProfileDetail.route,
            arguments = listOf(
                navArgument("profileId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId") ?: return@composable
            ProfileDetailScreen(
                profileId = profileId,
                onEdit = {
                    navController.navigate(
                        UmbralDestination.ProfileEdit.createRoute(profileId)
                    )
                },
                onBack = { navController.popBackStack() }
            )
        }

        // ... más destinos

        // Modal para NFC scan
        dialog(UmbralDestination.NfcScan.route) {
            NfcScanDialog(
                onDismiss = { navController.popBackStack() },
                onTagScanned = { tag ->
                    navController.popBackStack()
                    // Manejar tag escaneado
                }
            )
        }
    }
}
```

---

## 3. Design System

### 3.1 Tokens de Color

```kotlin
// theme/UmbralColors.kt

// Primary: Azul profundo (concentración, calma)
val UmbralBlue = Color(0xFF1A237E)
val UmbralBlueDark = Color(0xFF000051)
val UmbralBlueLight = Color(0xFF534BAE)

// Secondary: Verde menta (éxito, desbloqueado)
val UmbralMint = Color(0xFF00897B)
val UmbralMintDark = Color(0xFF005B4F)
val UmbralMintLight = Color(0xFF4EBAAA)

// Accent: Ámbar (advertencia, blocking activo)
val UmbralAmber = Color(0xFFFF8F00)
val UmbralAmberDark = Color(0xFFC56000)
val UmbralAmberLight = Color(0xFFFFC046)

// Error: Coral (bloqueo, peligro)
val UmbralCoral = Color(0xFFE53935)
val UmbralCoralDark = Color(0xFFAB000D)
val UmbralCoralLight = Color(0xFFFF6F60)

// Neutrals
val UmbralGray50 = Color(0xFFFAFAFA)
val UmbralGray100 = Color(0xFFF5F5F5)
val UmbralGray200 = Color(0xFFEEEEEE)
val UmbralGray300 = Color(0xFFE0E0E0)
val UmbralGray400 = Color(0xFFBDBDBD)
val UmbralGray500 = Color(0xFF9E9E9E)
val UmbralGray600 = Color(0xFF757575)
val UmbralGray700 = Color(0xFF616161)
val UmbralGray800 = Color(0xFF424242)
val UmbralGray900 = Color(0xFF212121)

// Color scheme light
val LightColorScheme = lightColorScheme(
    primary = UmbralBlue,
    onPrimary = Color.White,
    primaryContainer = UmbralBlueLight.copy(alpha = 0.2f),
    onPrimaryContainer = UmbralBlueDark,
    secondary = UmbralMint,
    onSecondary = Color.White,
    secondaryContainer = UmbralMintLight.copy(alpha = 0.2f),
    onSecondaryContainer = UmbralMintDark,
    tertiary = UmbralAmber,
    onTertiary = Color.Black,
    error = UmbralCoral,
    onError = Color.White,
    background = UmbralGray50,
    onBackground = UmbralGray900,
    surface = Color.White,
    onSurface = UmbralGray900,
    surfaceVariant = UmbralGray100,
    onSurfaceVariant = UmbralGray700,
    outline = UmbralGray300
)

// Color scheme dark
val DarkColorScheme = darkColorScheme(
    primary = UmbralBlueLight,
    onPrimary = UmbralBlueDark,
    primaryContainer = UmbralBlue.copy(alpha = 0.3f),
    onPrimaryContainer = UmbralBlueLight,
    secondary = UmbralMintLight,
    onSecondary = UmbralMintDark,
    secondaryContainer = UmbralMint.copy(alpha = 0.3f),
    onSecondaryContainer = UmbralMintLight,
    tertiary = UmbralAmberLight,
    onTertiary = Color.Black,
    error = UmbralCoralLight,
    onError = Color.Black,
    background = UmbralGray900,
    onBackground = UmbralGray50,
    surface = UmbralGray800,
    onSurface = UmbralGray50,
    surfaceVariant = UmbralGray700,
    onSurfaceVariant = UmbralGray300,
    outline = UmbralGray600
)
```

### 3.2 Tipografía

```kotlin
// theme/UmbralTypography.kt
val UmbralTypography = Typography(
    // Display - Para estados grandes (tiempo restante, estadísticas)
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Light,
        fontSize = 45.sp,
        lineHeight = 52.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),

    // Headlines - Para títulos de sección
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp
    ),

    // Title - Para cards y listas
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // Body - Para contenido principal
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),

    // Label - Para botones y chips
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
```

### 3.3 Dimensiones y Espaciado

```kotlin
// theme/UmbralDimensions.kt
object UmbralDimens {
    // Spacing scale (basado en 4dp)
    val spaceNone = 0.dp
    val spaceXxs = 2.dp
    val spaceXs = 4.dp
    val spaceSm = 8.dp
    val spaceMd = 12.dp
    val spaceLg = 16.dp
    val spaceXl = 24.dp
    val spaceXxl = 32.dp
    val spaceXxxl = 48.dp

    // Border radius
    val radiusNone = 0.dp
    val radiusSm = 4.dp
    val radiusMd = 8.dp
    val radiusLg = 12.dp
    val radiusXl = 16.dp
    val radiusFull = 9999.dp

    // Icon sizes
    val iconXs = 16.dp
    val iconSm = 20.dp
    val iconMd = 24.dp
    val iconLg = 32.dp
    val iconXl = 48.dp
    val iconXxl = 64.dp

    // Component sizes
    val buttonHeight = 48.dp
    val buttonHeightSmall = 36.dp
    val cardMinHeight = 72.dp
    val listItemHeight = 56.dp
    val appBarHeight = 64.dp
    val bottomNavHeight = 80.dp
    val fabSize = 56.dp

    // Touch targets (mínimo 48dp para accesibilidad)
    val touchTargetMin = 48.dp

    // Screen padding
    val screenPaddingHorizontal = 16.dp
    val screenPaddingVertical = 16.dp
}
```

### 3.4 Formas

```kotlin
// theme/UmbralShapes.kt
val UmbralShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp)
)
```

### 3.5 Theme Principal

```kotlin
// theme/UmbralTheme.kt
@Composable
fun UmbralTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Material You
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = UmbralTypography,
        shapes = UmbralShapes,
        content = content
    )
}
```

---

## 4. Pantallas Principales

### 4.1 Home Screen

```kotlin
// screens/home/HomeScreen.kt
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToProfiles: () -> Unit,
    onNavigateToNfcScan: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            UmbralTopAppBar(
                title = stringResource(R.string.app_name),
                actions = {
                    IconButton(onClick = { /* settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            UmbralFab(
                onClick = onNavigateToNfcScan,
                icon = Icons.Default.Nfc,
                contentDescription = stringResource(R.string.scan_nfc)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = UmbralDimens.screenPaddingHorizontal),
            verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceLg)
        ) {
            // Estado actual de bloqueo
            BlockingStatusCard(
                state = uiState.blockingState,
                onToggle = viewModel::toggleBlocking
            )

            // Perfil activo
            if (uiState.activeProfile != null) {
                ActiveProfileCard(
                    profile = uiState.activeProfile!!,
                    onViewDetails = onNavigateToProfiles
                )
            }

            // Quick actions
            QuickActionsRow(
                onScanNfc = onNavigateToNfcScan,
                onScanQr = { /* QR scan */ },
                onManualToggle = viewModel::toggleBlocking
            )

            // Estadísticas rápidas
            QuickStatsCard(
                todayBlocked = uiState.todayBlockedCount,
                weeklyTrend = uiState.weeklyTrend
            )
        }
    }
}

// HomeUiState
data class HomeUiState(
    val blockingState: BlockingState = BlockingState.Inactive,
    val activeProfile: Profile? = null,
    val todayBlockedCount: Int = 0,
    val weeklyTrend: Float = 0f, // -1 a 1
    val isLoading: Boolean = false,
    val error: String? = null
)

// HomeViewModel
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val blockingManager: BlockingManager,
    private val profileRepository: ProfileRepository,
    private val statsRepository: StatsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeBlockingState()
        observeActiveProfile()
        loadStats()
    }

    private fun observeBlockingState() {
        viewModelScope.launch {
            blockingManager.currentState.collect { state ->
                _uiState.update { it.copy(blockingState = state) }
            }
        }
    }

    private fun observeActiveProfile() {
        viewModelScope.launch {
            profileRepository.getActiveProfile().collect { profile ->
                _uiState.update { it.copy(activeProfile = profile) }
            }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            val today = statsRepository.getTodayBlockedCount()
            val weekly = statsRepository.getWeeklyTrend()
            _uiState.update {
                it.copy(
                    todayBlockedCount = today,
                    weeklyTrend = weekly
                )
            }
        }
    }

    fun toggleBlocking() {
        viewModelScope.launch {
            val currentState = _uiState.value.blockingState
            if (currentState == BlockingState.Inactive) {
                blockingManager.activate()
            } else {
                blockingManager.deactivate()
            }
        }
    }
}
```

### 4.2 Blocking Status Card

```kotlin
// components/BlockingStatusCard.kt
@Composable
fun BlockingStatusCard(
    state: BlockingState,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = state != BlockingState.Inactive

    val backgroundColor by animateColorAsState(
        targetValue = when (state) {
            BlockingState.Inactive -> MaterialTheme.colorScheme.surfaceVariant
            BlockingState.Active -> MaterialTheme.colorScheme.primaryContainer
            is BlockingState.ActiveWithTimer -> MaterialTheme.colorScheme.tertiaryContainer
            BlockingState.Paused -> MaterialTheme.colorScheme.secondaryContainer
        },
        animationSpec = tween(durationMillis = 300),
        label = "backgroundColor"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralDimens.spaceXl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceLg)
        ) {
            // Icono animado
            AnimatedBlockingIcon(isActive = isActive)

            // Estado
            Text(
                text = when (state) {
                    BlockingState.Inactive -> stringResource(R.string.status_inactive)
                    BlockingState.Active -> stringResource(R.string.status_active)
                    is BlockingState.ActiveWithTimer ->
                        stringResource(R.string.status_active_timer, state.remainingTime)
                    BlockingState.Paused -> stringResource(R.string.status_paused)
                },
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Timer countdown si aplica
            if (state is BlockingState.ActiveWithTimer) {
                TimerCountdown(
                    remainingSeconds = state.remainingSeconds,
                    totalSeconds = state.totalSeconds
                )
            }

            // Botón de toggle
            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isActive)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (isActive)
                        stringResource(R.string.btn_deactivate)
                    else
                        stringResource(R.string.btn_activate)
                )
            }
        }
    }
}

@Composable
private fun AnimatedBlockingIcon(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isActive) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconScale"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isActive) 360f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "iconRotation"
    )

    Icon(
        imageVector = if (isActive) Icons.Default.Lock else Icons.Default.LockOpen,
        contentDescription = null,
        modifier = modifier
            .size(UmbralDimens.iconXxl)
            .scale(scale)
            .rotate(rotation),
        tint = if (isActive)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.onSurfaceVariant
    )
}
```

### 4.3 Profiles Screen

```kotlin
// screens/profiles/ProfilesScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilesScreen(
    viewModel: ProfilesViewModel = hiltViewModel(),
    onProfileClick: (String) -> Unit,
    onCreateProfile: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profiles_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateProfile) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_profile))
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(modifier = Modifier.padding(paddingValues))
            }
            uiState.profiles.isEmpty() -> {
                EmptyProfilesState(
                    onCreateProfile = onCreateProfile,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(UmbralDimens.screenPaddingHorizontal),
                    verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceMd)
                ) {
                    // Perfil activo (si hay)
                    uiState.activeProfile?.let { active ->
                        item(key = "active_header") {
                            Text(
                                text = stringResource(R.string.active_profile),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = UmbralDimens.spaceSm)
                            )
                        }
                        item(key = active.id) {
                            ProfileCard(
                                profile = active,
                                isActive = true,
                                onClick = { onProfileClick(active.id) }
                            )
                        }
                    }

                    // Otros perfiles
                    val otherProfiles = uiState.profiles.filter {
                        it.id != uiState.activeProfile?.id
                    }

                    if (otherProfiles.isNotEmpty()) {
                        item(key = "other_header") {
                            Text(
                                text = stringResource(R.string.other_profiles),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(
                                    top = UmbralDimens.spaceLg,
                                    bottom = UmbralDimens.spaceSm
                                )
                            )
                        }

                        items(
                            items = otherProfiles,
                            key = { it.id }
                        ) { profile ->
                            ProfileCard(
                                profile = profile,
                                isActive = false,
                                onClick = { onProfileClick(profile.id) },
                                modifier = Modifier.animateItem()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCard(
    profile: Profile,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isActive) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isActive) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralDimens.spaceLg),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceLg)
        ) {
            // Icono del perfil
            ProfileIconBadge(
                icon = profile.icon,
                color = profile.color,
                size = UmbralDimens.iconXl
            )

            // Info del perfil
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = pluralStringResource(
                        R.plurals.apps_blocked_count,
                        profile.blockedApps.size,
                        profile.blockedApps.size
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Indicador de activo
            if (isActive) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = stringResource(R.string.active),
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
```

### 4.4 NFC Scan Dialog

```kotlin
// screens/nfc/NfcScanDialog.kt
@Composable
fun NfcScanDialog(
    viewModel: NfcScanViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onTagScanned: (NfcTag) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Observar evento de tag escaneado
    LaunchedEffect(uiState.scannedTag) {
        uiState.scannedTag?.let { tag ->
            onTagScanned(tag)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralDimens.screenPaddingHorizontal),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(UmbralDimens.spaceXl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceXl)
            ) {
                // Animación de escaneo
                NfcScanAnimation(
                    state = uiState.scanState,
                    modifier = Modifier.size(120.dp)
                )

                // Título
                Text(
                    text = when (uiState.scanState) {
                        NfcScanState.Waiting -> stringResource(R.string.nfc_waiting)
                        NfcScanState.Scanning -> stringResource(R.string.nfc_scanning)
                        NfcScanState.Success -> stringResource(R.string.nfc_success)
                        is NfcScanState.Error -> stringResource(R.string.nfc_error)
                    },
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                // Instrucciones
                Text(
                    text = when (uiState.scanState) {
                        NfcScanState.Waiting -> stringResource(R.string.nfc_instruction_approach)
                        NfcScanState.Scanning -> stringResource(R.string.nfc_instruction_hold)
                        NfcScanState.Success -> stringResource(R.string.nfc_instruction_done)
                        is NfcScanState.Error -> (uiState.scanState as NfcScanState.Error).message
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )

                // Botón de cancelar
                if (uiState.scanState != NfcScanState.Success) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(R.string.btn_cancel))
                    }
                }

                // Botón de reintentar si error
                if (uiState.scanState is NfcScanState.Error) {
                    Button(onClick = viewModel::retry) {
                        Text(stringResource(R.string.btn_retry))
                    }
                }
            }
        }
    }
}

@Composable
private fun NfcScanAnimation(
    state: NfcScanState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "nfcPulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Ondas de pulso (solo cuando esperando/escaneando)
        if (state == NfcScanState.Waiting || state == NfcScanState.Scanning) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(120.dp * (1 + index * 0.2f) * scale)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(
                                alpha = alpha / (index + 1)
                            ),
                            shape = CircleShape
                        )
                )
            }
        }

        // Icono central
        val iconTint = when (state) {
            NfcScanState.Success -> MaterialTheme.colorScheme.tertiary
            is NfcScanState.Error -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.primary
        }

        val icon = when (state) {
            NfcScanState.Success -> Icons.Default.CheckCircle
            is NfcScanState.Error -> Icons.Default.Error
            else -> Icons.Default.Nfc
        }

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = iconTint
        )
    }
}
```

---

## 5. Componentes Reutilizables

### 5.1 Profile Icon Badge

```kotlin
// components/ProfileIconBadge.kt
@Composable
fun ProfileIconBadge(
    icon: ProfileIcon,
    color: ProfileColor,
    size: Dp = UmbralDimens.iconLg,
    modifier: Modifier = Modifier
) {
    val backgroundColor = color.toComposeColor().copy(alpha = 0.2f)
    val iconTint = color.toComposeColor()

    Box(
        modifier = modifier
            .size(size + UmbralDimens.spaceMd)
            .background(
                color = backgroundColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon.toImageVector(),
            contentDescription = null,
            modifier = Modifier.size(size * 0.6f),
            tint = iconTint
        )
    }
}

fun ProfileIcon.toImageVector(): ImageVector = when (this) {
    ProfileIcon.WORK -> Icons.Default.Work
    ProfileIcon.STUDY -> Icons.Default.School
    ProfileIcon.SLEEP -> Icons.Default.Bedtime
    ProfileIcon.FOCUS -> Icons.Default.CenterFocusStrong
    ProfileIcon.SOCIAL -> Icons.Default.People
    ProfileIcon.GAMING -> Icons.Default.SportsEsports
    ProfileIcon.CUSTOM -> Icons.Default.Star
}

fun ProfileColor.toComposeColor(): Color = when (this) {
    ProfileColor.BLUE -> UmbralBlue
    ProfileColor.GREEN -> UmbralMint
    ProfileColor.ORANGE -> UmbralAmber
    ProfileColor.RED -> UmbralCoral
    ProfileColor.PURPLE -> Color(0xFF7B1FA2)
    ProfileColor.TEAL -> Color(0xFF00796B)
    ProfileColor.PINK -> Color(0xFFE91E63)
    ProfileColor.GRAY -> UmbralGray600
}
```

### 5.2 App Icon Row

```kotlin
// components/AppIconRow.kt
@Composable
fun AppIconRow(
    apps: List<InstalledApp>,
    maxVisible: Int = 5,
    onOverflowClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val visibleApps = apps.take(maxVisible)
    val overflowCount = apps.size - maxVisible

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy((-8).dp) // Overlap
    ) {
        visibleApps.forEachIndexed { index, app ->
            AppIconSmall(
                app = app,
                modifier = Modifier.zIndex((visibleApps.size - index).toFloat())
            )
        }

        if (overflowCount > 0) {
            OverflowBadge(
                count = overflowCount,
                onClick = onOverflowClick,
                modifier = Modifier.zIndex(0f)
            )
        }
    }
}

@Composable
private fun AppIconSmall(
    app: InstalledApp,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val icon = remember(app.packageName) {
        try {
            context.packageManager.getApplicationIcon(app.packageName)
        } catch (e: Exception) {
            null
        }
    }

    if (icon != null) {
        Image(
            painter = rememberDrawablePainter(icon),
            contentDescription = app.name,
            modifier = modifier
                .size(32.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
        )
    } else {
        Box(
            modifier = modifier
                .size(32.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = app.name.firstOrNull()?.toString() ?: "?",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun OverflowBadge(
    count: Int,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick)
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+$count",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
```

### 5.3 Timer Display

```kotlin
// components/TimerDisplay.kt
@Composable
fun TimerDisplay(
    remainingSeconds: Long,
    totalSeconds: Long,
    modifier: Modifier = Modifier
) {
    val progress = remember(remainingSeconds, totalSeconds) {
        if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 0f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 300),
        label = "timerProgress"
    )

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceMd)
    ) {
        // Circular progress
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(100.dp),
                strokeWidth = 8.dp,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = when {
                    progress > 0.5f -> MaterialTheme.colorScheme.primary
                    progress > 0.25f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.error
                }
            )

            // Tiempo restante
            Text(
                text = if (hours > 0) {
                    String.format("%d:%02d:%02d", hours, minutes, seconds)
                } else {
                    String.format("%02d:%02d", minutes, seconds)
                },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Label
        Text(
            text = stringResource(R.string.time_remaining),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
```

---

## 6. Widgets

### 6.1 Status Widget (Glance)

```kotlin
// widget/StatusWidget.kt
class StatusWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(
        setOf(
            DpSize(100.dp, 100.dp),  // Small
            DpSize(200.dp, 100.dp),  // Medium
            DpSize(300.dp, 150.dp)   // Large
        )
    )

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        val repository = (context.applicationContext as UmbralApp)
            .appComponent.blockingRepository()

        val state = repository.getCurrentState()

        provideContent {
            StatusWidgetContent(
                state = state,
                size = LocalSize.current
            )
        }
    }
}

@Composable
private fun StatusWidgetContent(
    state: BlockingState,
    size: DpSize
) {
    val isSmall = size.width < 150.dp

    GlanceTheme {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(
                    if (state == BlockingState.Inactive)
                        ColorProvider(Color.White)
                    else
                        ColorProvider(UmbralBlue)
                )
                .cornerRadius(16.dp)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSmall) {
                // Solo icono para widget pequeño
                Image(
                    provider = ImageProvider(
                        if (state == BlockingState.Inactive)
                            R.drawable.ic_lock_open
                        else
                            R.drawable.ic_lock
                    ),
                    contentDescription = null,
                    modifier = GlanceModifier.size(48.dp)
                )
            } else {
                // Icono + texto para widget grande
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        provider = ImageProvider(
                            if (state == BlockingState.Inactive)
                                R.drawable.ic_lock_open
                            else
                                R.drawable.ic_lock
                        ),
                        contentDescription = null,
                        modifier = GlanceModifier.size(32.dp)
                    )

                    Spacer(modifier = GlanceModifier.height(8.dp))

                    Text(
                        text = when (state) {
                            BlockingState.Inactive -> "Desbloqueado"
                            BlockingState.Active -> "Bloqueado"
                            is BlockingState.ActiveWithTimer ->
                                "Bloqueado (${state.remainingTime})"
                            BlockingState.Paused -> "Pausado"
                        },
                        style = TextStyle(
                            color = ColorProvider(
                                if (state == BlockingState.Inactive)
                                    Color.Black
                                else
                                    Color.White
                            ),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ToggleButton(
    isActive: Boolean,
    modifier: GlanceModifier = GlanceModifier
) {
    Button(
        text = if (isActive) "Desactivar" else "Activar",
        onClick = actionRunCallback<ToggleBlockingAction>(),
        modifier = modifier
    )
}

class ToggleBlockingAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val blockingManager = (context.applicationContext as UmbralApp)
            .appComponent.blockingManager()

        val current = blockingManager.currentState.first()
        if (current == BlockingState.Inactive) {
            blockingManager.activate()
        } else {
            blockingManager.deactivate()
        }

        StatusWidget().update(context, glanceId)
    }
}
```

### 6.2 Widget Receiver

```kotlin
// widget/StatusWidgetReceiver.kt
class StatusWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StatusWidget()
}
```

### 6.3 Widget Manifest

```xml
<!-- AndroidManifest.xml -->
<receiver
    android:name=".widget.StatusWidgetReceiver"
    android:exported="false">
    <intent-filter>
        <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
    </intent-filter>
    <meta-data
        android:name="android.appwidget.provider"
        android:resource="@xml/status_widget_info" />
</receiver>
```

```xml
<!-- res/xml/status_widget_info.xml -->
<appwidget-provider
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:description="@string/widget_description"
    android:initialLayout="@layout/widget_loading"
    android:minWidth="100dp"
    android:minHeight="100dp"
    android:minResizeWidth="100dp"
    android:minResizeHeight="100dp"
    android:previewImage="@drawable/widget_preview"
    android:previewLayout="@layout/widget_preview"
    android:resizeMode="horizontal|vertical"
    android:targetCellWidth="2"
    android:targetCellHeight="2"
    android:updatePeriodMillis="1800000"
    android:widgetCategory="home_screen" />
```

---

## 7. Quick Settings Tile

### 7.1 Blocking Tile Service

```kotlin
// tile/BlockingTileService.kt
@RequiresApi(Build.VERSION_CODES.N)
class BlockingTileService : TileService() {

    @Inject
    lateinit var blockingManager: BlockingManager

    override fun onCreate() {
        super.onCreate()
        (application as UmbralApp).appComponent.inject(this)
    }

    override fun onStartListening() {
        super.onStartListening()

        // Observar cambios de estado
        CoroutineScope(Dispatchers.Main).launch {
            blockingManager.currentState.collect { state ->
                updateTile(state)
            }
        }
    }

    override fun onClick() {
        super.onClick()

        CoroutineScope(Dispatchers.IO).launch {
            val current = blockingManager.currentState.first()
            if (current == BlockingState.Inactive) {
                blockingManager.activate()
            } else {
                blockingManager.deactivate()
            }
        }
    }

    private fun updateTile(state: BlockingState) {
        qsTile?.apply {
            this.state = when (state) {
                BlockingState.Inactive -> Tile.STATE_INACTIVE
                else -> Tile.STATE_ACTIVE
            }

            label = when (state) {
                BlockingState.Inactive -> getString(R.string.tile_inactive)
                BlockingState.Active -> getString(R.string.tile_active)
                is BlockingState.ActiveWithTimer ->
                    getString(R.string.tile_active_timer, state.remainingTime)
                BlockingState.Paused -> getString(R.string.tile_paused)
            }

            icon = Icon.createWithResource(
                this@BlockingTileService,
                if (state == BlockingState.Inactive)
                    R.drawable.ic_tile_inactive
                else
                    R.drawable.ic_tile_active
            )

            updateTile()
        }
    }
}
```

### 7.2 Tile Manifest

```xml
<!-- AndroidManifest.xml -->
<service
    android:name=".tile.BlockingTileService"
    android:exported="true"
    android:icon="@drawable/ic_tile_inactive"
    android:label="@string/tile_label"
    android:permission="android.permission.BIND_QUICK_SETTINGS_TILE">
    <intent-filter>
        <action android:name="android.service.quicksettings.action.QS_TILE" />
    </intent-filter>
    <meta-data
        android:name="android.service.quicksettings.ACTIVE_TILE"
        android:value="false" />
</service>
```

---

## 8. Accesibilidad

### 8.1 Content Descriptions

```kotlin
// accessibility/ContentDescriptions.kt
object ContentDescriptions {
    // Usar stringResource() en composables

    fun getBlockingStateDescription(state: BlockingState, context: Context): String {
        return when (state) {
            BlockingState.Inactive ->
                context.getString(R.string.cd_blocking_inactive)
            BlockingState.Active ->
                context.getString(R.string.cd_blocking_active)
            is BlockingState.ActiveWithTimer ->
                context.getString(R.string.cd_blocking_timer, state.remainingTime)
            BlockingState.Paused ->
                context.getString(R.string.cd_blocking_paused)
        }
    }

    fun getProfileDescription(profile: Profile, context: Context): String {
        return context.getString(
            R.string.cd_profile_description,
            profile.name,
            profile.blockedApps.size
        )
    }
}
```

### 8.2 Semantic Modifiers

```kotlin
// accessibility/SemanticModifiers.kt
fun Modifier.blockingStatusSemantics(
    state: BlockingState,
    onToggle: () -> Unit
): Modifier = this
    .semantics {
        stateDescription = when (state) {
            BlockingState.Inactive -> "Desactivado"
            BlockingState.Active -> "Activado"
            is BlockingState.ActiveWithTimer -> "Activado con temporizador"
            BlockingState.Paused -> "Pausado"
        }

        role = Role.Switch

        onClick(
            label = if (state == BlockingState.Inactive) "Activar bloqueo" else "Desactivar bloqueo"
        ) {
            onToggle()
            true
        }
    }

fun Modifier.profileCardSemantics(
    profile: Profile,
    isActive: Boolean
): Modifier = this
    .semantics {
        contentDescription = buildString {
            append("Perfil ${profile.name}")
            if (isActive) append(", activo")
            append(", ${profile.blockedApps.size} aplicaciones bloqueadas")
        }

        role = Role.Button
    }
```

### 8.3 Focus Management

```kotlin
// accessibility/FocusManager.kt
@Composable
fun rememberAccessibleFocusRequester(): FocusRequester {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        // Delay para permitir que el composable se estabilice
        delay(100)
        try {
            focusRequester.requestFocus()
        } catch (e: Exception) {
            // Ignorar si no se puede enfocar
        }
    }

    return focusRequester
}

@Composable
fun AccessibleDialog(
    onDismissRequest: () -> Unit,
    title: String,
    content: @Composable () -> Unit
) {
    val focusRequester = rememberAccessibleFocusRequester()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = title,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .focusable()
            )
        },
        text = content,
        confirmButton = {},
        modifier = Modifier.semantics {
            liveRegion = LiveRegionMode.Assertive
        }
    )
}
```

---

## 9. Animaciones

### 9.1 Transiciones de Pantalla

```kotlin
// animation/ScreenTransitions.kt
object ScreenTransitions {
    fun enterTransition(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(300)
        )
    }

    fun exitTransition(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth / 4 },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + fadeOut(
            animationSpec = tween(300)
        )
    }

    fun popEnterTransition(): EnterTransition {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth / 4 },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + fadeIn(
            animationSpec = tween(300)
        )
    }

    fun popExitTransition(): ExitTransition {
        return slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(
                durationMillis = 300,
                easing = FastOutSlowInEasing
            )
        ) + fadeOut(
            animationSpec = tween(300)
        )
    }
}
```

### 9.2 Animaciones de Estado

```kotlin
// animation/StateAnimations.kt
@Composable
fun AnimatedBlockingTransition(
    isActive: Boolean,
    content: @Composable (Boolean) -> Unit
) {
    val transition = updateTransition(
        targetState = isActive,
        label = "blockingTransition"
    )

    val scale by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "scale"
    ) { active ->
        if (active) 1.1f else 1f
    }

    val backgroundColor by transition.animateColor(
        transitionSpec = { tween(300) },
        label = "backgroundColor"
    ) { active ->
        if (active) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant
    }

    Box(
        modifier = Modifier
            .scale(scale)
            .background(backgroundColor, MaterialTheme.shapes.large)
    ) {
        content(isActive)
    }
}
```

### 9.3 Micro-interacciones

```kotlin
// animation/MicroInteractions.kt
@Composable
fun PressableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "pressScale"
    )

    Card(
        onClick = onClick,
        modifier = modifier.scale(scale),
        interactionSource = interactionSource
    ) {
        content()
    }
}

@Composable
fun SuccessCheckmark(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
        ) + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = stringResource(R.string.success),
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = modifier.size(UmbralDimens.iconXl)
        )
    }
}
```

---

## 10. Strings y Localización

### 10.1 Estructura de Strings

```xml
<!-- res/values/strings.xml -->
<resources>
    <!-- App -->
    <string name="app_name">Umbral</string>

    <!-- Navigation -->
    <string name="nav_home">Inicio</string>
    <string name="nav_profiles">Perfiles</string>
    <string name="nav_stats">Estadísticas</string>
    <string name="nav_settings">Configuración</string>

    <!-- Blocking Status -->
    <string name="status_inactive">Desbloqueado</string>
    <string name="status_active">Bloqueado</string>
    <string name="status_active_timer">Bloqueado (%1$s)</string>
    <string name="status_paused">Pausado</string>

    <!-- Buttons -->
    <string name="btn_activate">Activar Bloqueo</string>
    <string name="btn_deactivate">Desactivar</string>
    <string name="btn_cancel">Cancelar</string>
    <string name="btn_save">Guardar</string>
    <string name="btn_delete">Eliminar</string>
    <string name="btn_retry">Reintentar</string>

    <!-- Profiles -->
    <string name="profiles_title">Perfiles</string>
    <string name="active_profile">Perfil Activo</string>
    <string name="other_profiles">Otros Perfiles</string>
    <string name="add_profile">Añadir Perfil</string>
    <string name="profile_name_hint">Nombre del perfil</string>
    <string name="select_apps">Seleccionar Apps</string>

    <!-- Plurals -->
    <plurals name="apps_blocked_count">
        <item quantity="one">%d app bloqueada</item>
        <item quantity="other">%d apps bloqueadas</item>
    </plurals>

    <!-- NFC -->
    <string name="scan_nfc">Escanear NFC</string>
    <string name="nfc_waiting">Esperando tag NFC</string>
    <string name="nfc_scanning">Leyendo…</string>
    <string name="nfc_success">¡Listo!</string>
    <string name="nfc_error">Error al leer</string>
    <string name="nfc_instruction_approach">Acerca el tag NFC a tu teléfono</string>
    <string name="nfc_instruction_hold">Mantén el tag quieto</string>
    <string name="nfc_instruction_done">Tag configurado correctamente</string>

    <!-- Timer -->
    <string name="time_remaining">Tiempo restante</string>

    <!-- Widget -->
    <string name="widget_description">Muestra el estado de bloqueo actual</string>

    <!-- Quick Settings Tile -->
    <string name="tile_label">Umbral</string>
    <string name="tile_inactive">Desactivado</string>
    <string name="tile_active">Bloqueando</string>
    <string name="tile_active_timer">Bloqueando (%1$s)</string>
    <string name="tile_paused">Pausado</string>

    <!-- Accessibility -->
    <string name="cd_blocking_inactive">Bloqueo desactivado</string>
    <string name="cd_blocking_active">Bloqueo activado</string>
    <string name="cd_blocking_timer">Bloqueo activado, %1$s restante</string>
    <string name="cd_blocking_paused">Bloqueo pausado</string>
    <string name="cd_profile_description">Perfil %1$s con %2$d apps bloqueadas</string>

    <!-- Empty States -->
    <string name="empty_profiles_title">Sin perfiles</string>
    <string name="empty_profiles_message">Crea tu primer perfil para empezar a bloquear apps</string>
    <string name="empty_stats_title">Sin datos</string>
    <string name="empty_stats_message">Activa el bloqueo para ver tus estadísticas</string>

    <!-- Errors -->
    <string name="error_generic">Algo salió mal</string>
    <string name="error_nfc_not_supported">Tu dispositivo no soporta NFC</string>
    <string name="error_nfc_disabled">NFC está desactivado</string>
    <string name="error_permission_required">Permiso requerido</string>
</resources>
```

---

## 11. Testing Strategy

### 11.1 Compose UI Tests

```kotlin
// test/HomeScreenTest.kt
@HiltAndroidTest
class HomeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun homeScreen_showsBlockingStatus() {
        composeTestRule.onNodeWithText("Desbloqueado")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_activateButtonWorks() {
        composeTestRule.onNodeWithText("Activar Bloqueo")
            .performClick()

        composeTestRule.onNodeWithText("Bloqueado")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_nfcButtonOpensDialog() {
        composeTestRule.onNodeWithContentDescription("Escanear NFC")
            .performClick()

        composeTestRule.onNodeWithText("Esperando tag NFC")
            .assertIsDisplayed()
    }
}
```

### 11.2 Screenshot Tests

```kotlin
// test/ScreenshotTests.kt
class ScreenshotTests {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material.Light.NoActionBar"
    )

    @Test
    fun homeScreen_inactive() {
        paparazzi.snapshot {
            UmbralTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        blockingState = BlockingState.Inactive
                    ),
                    onNavigateToProfiles = {},
                    onNavigateToNfcScan = {}
                )
            }
        }
    }

    @Test
    fun homeScreen_active() {
        paparazzi.snapshot {
            UmbralTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        blockingState = BlockingState.Active
                    ),
                    onNavigateToProfiles = {},
                    onNavigateToNfcScan = {}
                )
            }
        }
    }

    @Test
    fun profileCard_active() {
        paparazzi.snapshot {
            UmbralTheme {
                ProfileCard(
                    profile = Profile(
                        id = "1",
                        name = "Trabajo",
                        icon = ProfileIcon.WORK,
                        color = ProfileColor.BLUE,
                        blockedApps = listOf("com.twitter", "com.instagram")
                    ),
                    isActive = true,
                    onClick = {}
                )
            }
        }
    }
}
```

---

## 12. Criterios de Aceptación

### 12.1 Navegación
- [ ] Todas las rutas navegan correctamente
- [ ] Back button funciona en todas las pantallas
- [ ] Deep links funcionan para rutas principales
- [ ] Rotación de pantalla preserva estado

### 12.2 Design System
- [ ] Colores consistentes con especificación
- [ ] Tipografía aplicada correctamente
- [ ] Espaciado uniforme (múltiplos de 4dp)
- [ ] Formas redondeadas consistentes

### 12.3 Componentes
- [ ] Todos los componentes son reutilizables
- [ ] Previews funcionan para todos los componentes
- [ ] Estados de error/loading implementados
- [ ] Animaciones suaves (60fps)

### 12.4 Widgets
- [ ] Widget muestra estado actual
- [ ] Widget se actualiza cuando cambia estado
- [ ] Quick Settings Tile funciona
- [ ] Widget soporta múltiples tamaños

### 12.5 Accesibilidad
- [ ] TalkBack funciona correctamente
- [ ] Touch targets >= 48dp
- [ ] Contraste de colores suficiente (WCAG AA)
- [ ] Content descriptions en todos los elementos interactivos
- [ ] Soporte para modo de texto grande

### 12.6 Performance
- [ ] Primera carga < 500ms
- [ ] Transiciones a 60fps
- [ ] Sin memory leaks en navegación
- [ ] Lazy loading para listas largas

---

## 13. Dependencias

```kotlin
// build.gradle.kts (app module)
dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    // Material 3
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Glance (Widgets)
    implementation("androidx.glance:glance-appwidget:1.0.0")
    implementation("androidx.glance:glance-material3:1.0.0")

    // Coil (Images)
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Accompanist (Drawables)
    implementation("com.google.accompanist:accompanist-drawablepainter:0.34.0")

    // Testing
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Screenshot testing
    testImplementation("app.cash.paparazzi:paparazzi:1.3.2")
}
```

---

**Creado:** 2026-01-03
**Autor/Mantenedor:** Equipo Umbral
