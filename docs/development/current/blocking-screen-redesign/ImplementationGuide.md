# Guía de Implementación - Blocking Screen

**Versión:** 1.0
**Fecha:** 2026-01-15
**Estado:** 🟢 Listo para implementación

---

## Índice

1. [Estructura de Archivos](#estructura-de-archivos)
2. [Dependencias Requeridas](#dependencias-requeridas)
3. [Integración con ViewModel](#integración-con-viewmodel)
4. [Testing](#testing)
5. [Performance Optimization](#performance-optimization)
6. [Accesibilidad](#accesibilidad)
7. [Troubleshooting](#troubleshooting)

---

## Estructura de Archivos

### Ubicación en el proyecto

```
app/src/main/java/com/umbral/
├── presentation/
│   ├── ui/
│   │   ├── screens/
│   │   │   └── blocking/
│   │   │       ├── BlockingScreen.kt         ← Screen principal
│   │   │       ├── BlockingViewModel.kt      ← ViewModel (crear)
│   │   │       └── BlockingState.kt          ← Estados (ya en Screen)
│   │   └── theme/
│   │       ├── UmbralTheme.kt                ← Tema personalizado
│   │       ├── Color.kt                      ← Colores
│   │       ├── Type.kt                       ← Tipografía
│   │       └── Shape.kt                      ← Formas
│   └── navigation/
│       └── AppNavigation.kt                  ← Navegación
└── res/
    └── values/
        ├── strings.xml                        ← Strings en español
        └── themes.xml                         ← Material3 theme

```

---

## Dependencias Requeridas

### build.gradle.kts (Module: app)

```kotlin
dependencies {
    // Compose BOM (Bill of Materials)
    implementation(platform("androidx.compose:compose-bom:2024.01.00"))

    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // Material Design 3
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material:material-icons-extended")

    // Compose Animation
    implementation("androidx.compose.animation:animation")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // ViewModel Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // Hilt for DI
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

### build.gradle.kts (Module: app) - Plugins

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}
```

### gradle.properties

```properties
# Enable Jetpack Compose
android.useAndroidX=true
android.enableJetifier=true

# Kapt
kapt.incremental.apt=true
kapt.use.worker.api=true
```

---

## Integración con ViewModel

### BlockingViewModel.kt

```kotlin
package com.umbral.presentation.ui.screens.blocking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umbral.domain.repository.BlockingProfileRepository
import com.umbral.domain.repository.UsageStatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlockingViewModel @Inject constructor(
    private val profileRepository: BlockingProfileRepository,
    private val usageStatsRepository: UsageStatsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(BlockingState(
        profileName = "",
        isStrictMode = false,
        timerMinutesRemaining = null,
        focusedTimeToday = "0h 0min",
        isFirstTime = false
    ))
    val state: StateFlow<BlockingState> = _state.asStateFlow()

    init {
        loadBlockingState()
    }

    private fun loadBlockingState() {
        viewModelScope.launch {
            // Cargar perfil activo
            profileRepository.getActiveProfile()
                .collect { profile ->
                    profile?.let {
                        _state.update { currentState ->
                            currentState.copy(
                                profileName = it.name,
                                isStrictMode = it.requiresPhysicalUnlock,
                                timerMinutesRemaining = it.timerMinutesRemaining
                            )
                        }
                    }
                }
        }

        viewModelScope.launch {
            // Cargar tiempo enfocado hoy
            usageStatsRepository.getFocusedTimeToday()
                .collect { minutes ->
                    val hours = minutes / 60
                    val mins = minutes % 60
                    val timeText = if (hours > 0) {
                        "${hours}h ${mins}min"
                    } else {
                        "${mins}min"
                    }

                    _state.update { it.copy(focusedTimeToday = timeText) }
                }
        }

        viewModelScope.launch {
            // Verificar si es primera vez
            val isFirstTime = profileRepository.isFirstTimeBlocking()
            _state.update { it.copy(isFirstTime = isFirstTime) }
        }
    }

    fun onBackToHome() {
        // Navegar de vuelta al launcher
        viewModelScope.launch {
            // Implementar lógica de navegación
        }
    }

    fun onEmergencyAccess() {
        // Mostrar diálogo de confirmación
        viewModelScope.launch {
            // Implementar lógica de emergencia
        }
    }

    fun onScanNfc() {
        // Iniciar escaneo NFC
        viewModelScope.launch {
            // Implementar lógica de NFC
        }
    }
}
```

### Integración en Navigation

```kotlin
// AppNavigation.kt
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToBlocking = {
                    navController.navigate("blocking")
                }
            )
        }

        composable("blocking") {
            val viewModel: BlockingViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()

            BlockingScreen(
                state = state,
                onBackToHome = {
                    navController.popBackStack()
                    viewModel.onBackToHome()
                },
                onEmergencyAccess = viewModel::onEmergencyAccess,
                onScanNfc = viewModel::onScanNfc
            )
        }
    }
}
```

---

## Testing

### Unit Tests

```kotlin
// BlockingViewModelTest.kt
class BlockingViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: BlockingViewModel
    private lateinit var profileRepository: FakeBlockingProfileRepository
    private lateinit var usageStatsRepository: FakeUsageStatsRepository

    @Before
    fun setup() {
        profileRepository = FakeBlockingProfileRepository()
        usageStatsRepository = FakeUsageStatsRepository()
        viewModel = BlockingViewModel(profileRepository, usageStatsRepository)
    }

    @Test
    fun `initial state has default values`() = runTest {
        val state = viewModel.state.value
        assertEquals("", state.profileName)
        assertFalse(state.isStrictMode)
        assertNull(state.timerMinutesRemaining)
    }

    @Test
    fun `loads active profile correctly`() = runTest {
        // Given
        val testProfile = BlockingProfile(
            id = 1,
            name = "Trabajo",
            requiresPhysicalUnlock = true
        )
        profileRepository.setActiveProfile(testProfile)

        // When
        viewModel.loadBlockingState()

        // Then
        val state = viewModel.state.value
        assertEquals("Trabajo", state.profileName)
        assertTrue(state.isStrictMode)
    }
}
```

### UI Tests (Compose)

```kotlin
// BlockingScreenTest.kt
class BlockingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun blockingScreen_normalMode_showsCorrectButtons() {
        // Given
        val state = BlockingState(
            profileName = "Test",
            isStrictMode = false
        )

        // When
        composeTestRule.setContent {
            UmbralTheme {
                BlockingScreen(
                    state = state,
                    onBackToHome = {},
                    onEmergencyAccess = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Volver al inicio")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("¿Necesitas acceso urgente?")
            .assertIsDisplayed()
    }

    @Test
    fun blockingScreen_strictMode_showsNfcButton() {
        // Given
        val state = BlockingState(
            profileName = "Estricto",
            isStrictMode = true
        )

        // When
        composeTestRule.setContent {
            UmbralTheme {
                BlockingScreen(
                    state = state,
                    onBackToHome = {},
                    onScanNfc = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithText("Escanear para desbloquear")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Modo estricto activo")
            .assertIsDisplayed()
    }

    @Test
    fun blockingScreen_breathingAnimation_isVisible() {
        // Given
        val state = BlockingState(profileName = "Test")

        // When
        composeTestRule.setContent {
            UmbralTheme {
                BlockingScreen(
                    state = state,
                    onBackToHome = {}
                )
            }
        }

        // Then
        composeTestRule
            .onNodeWithContentDescription("Modo enfoque activo")
            .assertIsDisplayed()
    }
}
```

### Screenshot Tests (Paparazzi)

```kotlin
// BlockingScreenScreenshotTest.kt
class BlockingScreenScreenshotTest {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = DeviceConfig.PIXEL_5,
        theme = "android:Theme.Material3.DayNight"
    )

    @Test
    fun blockingScreen_normalMode_light() {
        paparazzi.snapshot {
            UmbralTheme(darkTheme = false) {
                BlockingScreen(
                    state = BlockingState(
                        profileName = "Trabajo",
                        focusedTimeToday = "2h 35min"
                    ),
                    onBackToHome = {}
                )
            }
        }
    }

    @Test
    fun blockingScreen_strictMode_dark() {
        paparazzi.snapshot {
            UmbralTheme(darkTheme = true) {
                BlockingScreen(
                    state = BlockingState(
                        profileName = "Enfoque",
                        isStrictMode = true,
                        focusedTimeToday = "4h 12min"
                    ),
                    onBackToHome = {},
                    onScanNfc = {}
                )
            }
        }
    }
}
```

---

## Performance Optimization

### 1. Animaciones

**Problema:** Animaciones consumen recursos.

**Solución:**
```kotlin
// Respetar preferencias del sistema
val animationScale = Settings.Global.getFloat(
    context.contentResolver,
    Settings.Global.ANIMATOR_DURATION_SCALE,
    1f
)

val animationEnabled = animationScale > 0f

if (animationEnabled) {
    // Mostrar animación
} else {
    // Estado estático
}
```

### 2. Recomposiciones

**Problema:** Recomposiciones innecesarias.

**Solución:**
```kotlin
// Usar remember y derivedStateOf
val currentMessage by remember {
    derivedStateOf {
        motivationalMessages[currentIndex]
    }
}

// Evitar lambdas que capturen estado mutable
val onBackToHomeStable = rememberUpdatedState(onBackToHome)
```

### 3. LaunchedEffect Keys

**Problema:** Effects que se re-ejecutan.

**Solución:**
```kotlin
// Usar keys específicas
LaunchedEffect(state.profileName) {
    // Solo se ejecuta cuando cambia el perfil
}

// Para efectos únicos
LaunchedEffect(Unit) {
    // Se ejecuta una sola vez
}
```

### 4. Infinite Animations Cleanup

**Problema:** Animaciones que no se limpian.

**Solución:**
```kotlin
DisposableEffect(Unit) {
    // Iniciar animación

    onDispose {
        // Cancelar animación
    }
}
```

---

## Accesibilidad

### 1. Content Descriptions

```kotlin
Icon(
    imageVector = Icons.Outlined.Shield,
    contentDescription = stringResource(R.string.cd_breathing_icon),
    // ...
)
```

### 2. Semantic Properties

```kotlin
modifier = Modifier.semantics {
    contentDescription = "Perfil activo: $profileName"
    role = Role.Button
}
```

### 3. Touch Targets

```kotlin
// Mínimo 48dp para elementos interactivos
Button(
    modifier = Modifier
        .fillMaxWidth()
        .height(56.dp), // > 48dp
    // ...
)
```

### 4. Contrast Checker

Ejecutar en cada commit:
```bash
./gradlew checkAccessibility
```

---

## Troubleshooting

### Problema 1: Animaciones no se ven fluidas

**Causa:** Overdraw o composiciones pesadas.

**Solución:**
```bash
# Habilitar en Developer Options
- Show GPU Overdraw
- Profile GPU Rendering

# Verificar en Layout Inspector
```

### Problema 2: Mensajes no rotan

**Causa:** LaunchedEffect no se reinicia.

**Solución:**
```kotlin
// Asegurar que key sea Unit para loop infinito
LaunchedEffect(Unit) {
    while (true) {
        delay(8000)
        // ...
    }
}
```

### Problema 3: Colores no se adaptan a Material You

**Causa:** Dynamic colors no habilitados.

**Solución:**
```kotlin
// En MainActivity.kt
setContent {
    UmbralTheme(
        dynamicColor = true // Asegurar que sea true
    ) {
        // ...
    }
}
```

### Problema 4: Haptic feedback no funciona

**Causa:** Permisos o dispositivo sin soporte.

**Solución:**
```kotlin
val haptic = LocalHapticFeedback.current

try {
    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
} catch (e: Exception) {
    // Dispositivo sin soporte, ignorar
}
```

---

## Checklist de Implementación

### Pre-implementación
- [ ] Dependencias agregadas en `build.gradle.kts`
- [ ] Estructura de carpetas creada
- [ ] Strings.xml con todos los textos en español
- [ ] Theme configurado (UmbralTheme.kt)

### Durante implementación
- [ ] BlockingScreen.kt copiado y adaptado
- [ ] BlockingViewModel.kt creado e inyectado con Hilt
- [ ] Navegación configurada
- [ ] Preview functions verificadas en Android Studio

### Testing
- [ ] Unit tests para ViewModel
- [ ] UI tests para todos los estados
- [ ] Screenshot tests generados
- [ ] Accessibility scanner ejecutado
- [ ] Manual testing en dispositivo físico

### Pre-release
- [ ] Performance profiling (GPU, CPU)
- [ ] Contrast checker passed
- [ ] Reducir motion probado
- [ ] Material You dynamic colors verificados
- [ ] Dark/Light themes verificados

---

## Recursos Adicionales

- [Material Design 3 Guidelines](https://m3.material.io/)
- [Compose Animation Docs](https://developer.android.com/jetpack/compose/animation)
- [Accessibility Best Practices](https://developer.android.com/guide/topics/ui/accessibility/principles)
- [Hilt Documentation](https://dagger.dev/hilt/)

---

**Última actualización:** 2026-01-15
**Próxima revisión:** Después de implementación inicial
