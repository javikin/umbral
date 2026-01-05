# Task #22: Arquitectura de Categorías de Apps

## Diagrama de Componentes

```
┌──────────────────────────────────────────────────────────┐
│                    AppSelectorScreen                      │
│  ┌────────────────────────────────────────────────────┐  │
│  │              CategoryFilter (Nuevo)                 │  │
│  │  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐              │  │
│  │  │ Chip │ │ Chip │ │ Chip │ │ Chip │ ... (scroll) │  │
│  │  └──────┘ └──────┘ └──────┘ └──────┘              │  │
│  └────────────────────────────────────────────────────┘  │
│                         │                                 │
│                         ▼                                 │
│  ┌────────────────────────────────────────────────────┐  │
│  │           AppSelectorViewModel                      │  │
│  │  • selectedCategory: MutableStateFlow              │  │
│  │  • selectCategory(category)                        │  │
│  │  • filterApps(apps, query, category, includeSystem)│  │
│  └────────────────────────────────────────────────────┘  │
│                         │                                 │
│                         ▼                                 │
│  ┌────────────────────────────────────────────────────┐  │
│  │        InstalledAppsProviderImpl                    │  │
│  │  • getLaunchableApps()                             │  │
│  │  • Categoriza cada app al cargarla                 │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
                          │
                          ▼
┌──────────────────────────────────────────────────────────┐
│                  Domain Layer                             │
│  ┌────────────────────────────────────────────────────┐  │
│  │  InstalledApp                                       │  │
│  │  • packageName: String                             │  │
│  │  • name: String                                    │  │
│  │  • category: AppCategory  ← (Era nullable String) │  │
│  │  • isSystemApp: Boolean                            │  │
│  └────────────────────────────────────────────────────┘  │
│                         │                                 │
│  ┌────────────────────────────────────────────────────┐  │
│  │  AppCategory (Enum)                                 │  │
│  │  • ALL, SOCIAL, COMMUNICATION, GAMES, etc.         │  │
│  │  • displayName: @StringRes                         │  │
│  │  • icon: ImageVector                               │  │
│  │  • fromPackageName(packageName): AppCategory       │  │
│  └────────────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────┘
                          │
                          ▼
┌──────────────────────────────────────────────────────────┐
│                Resources (strings.xml)                    │
│  • category_all = "Todas"                                │
│  • category_social = "Redes Sociales"                    │
│  • category_communication = "Comunicación"               │
│  • category_games = "Juegos"                             │
│  • ...                                                   │
└──────────────────────────────────────────────────────────┘
```

## Flujo de Datos

```
1. Usuario toca chip de categoría
   └─> CategoryFilter.onClick()
       └─> viewModel.selectCategory(category)
           └─> _selectedCategory.value = category
               └─> combine() re-evalúa
                   └─> filterApps(..., category, ...)
                       └─> Filtra apps donde app.category == selectedCategory
                           └─> UI se actualiza con apps filtradas

2. App se carga desde sistema
   └─> InstalledAppsProviderImpl.getLaunchableApps()
       └─> packageManager.queryIntentActivities()
           └─> Para cada app:
               └─> InstalledApp(
                       packageName = ...,
                       category = AppCategory.fromPackageName(packageName)
                   )
                   └─> Categoría asignada basándose en package name
```

## Lógica de Categorización

```kotlin
AppCategory.fromPackageName(packageName: String): AppCategory {
    val lower = packageName.lowercase()
    
    return when {
        // Patrones específicos primero
        lower.contains("facebook") -> SOCIAL
        lower.contains("whatsapp") -> COMMUNICATION
        lower.contains("game") -> GAMES
        
        // Patrones genéricos después
        lower.contains("android.") -> SYSTEM
        
        // Default
        else -> OTHER
    }
}
```

## Integración con Filtros Existentes

```
┌─────────────────────────────────────────────────────┐
│              AppSelectorViewModel                    │
│                                                      │
│  filterApps(apps, query, category, includeSystem) { │
│      return apps                                     │
│          .filter { category == ALL || app.category == category }  ← Nuevo
│          .filter { includeSystem || !app.isSystemApp }           ← Existente
│          .filter { app.name.contains(query) }                    ← Existente
│  }                                                   │
└─────────────────────────────────────────────────────┘
```

## Ventajas del Diseño

### 1. Separación de Responsabilidades
- **UI Layer:** Solo renderiza chips y maneja eventos
- **ViewModel:** Gestiona estado y lógica de filtrado
- **Domain Layer:** Define categorías y reglas de categorización
- **Data Layer:** Aplica categorización al cargar datos

### 2. Type Safety
```kotlin
// Antes (String nullable, error-prone)
val category: String? = null
if (category == "Social") { ... }  // Typo no detectado

// Después (Enum, compile-time safety)
val category: AppCategory = AppCategory.SOCIAL
if (category == AppCategory.SOCIAL) { ... }  // Typo detectado
```

### 3. Internacionalización
```kotlin
// Strings centralizados en strings.xml
Text(stringResource(category.displayName))
// Fácil agregar strings-es, strings-en, etc.
```

### 4. Testabilidad
```kotlin
@Test
fun `fromPackageName categorizes facebook as SOCIAL`() {
    val category = AppCategory.fromPackageName("com.facebook.katana")
    assertEquals(AppCategory.SOCIAL, category)
}

@Test
fun `filter shows only games when GAMES selected`() {
    viewModel.selectCategory(AppCategory.GAMES)
    val filtered = viewModel.uiState.value.filteredApps
    assertTrue(filtered.all { it.category == AppCategory.GAMES })
}
```

### 5. Extensibilidad
```kotlin
// Agregar nueva categoría es simple:
enum class AppCategory {
    // ...
    FINANCE(R.string.category_finance, Icons.Default.AccountBalance)
}

// Actualizar lógica en un solo lugar:
companion object {
    fun fromPackageName(packageName: String): AppCategory {
        when {
            lower.contains("bank") || lower.contains("wallet") -> FINANCE
            // ...
        }
    }
}
```

## Performance

### Tiempo de Categorización
- **Una sola vez** al cargar apps (no en cada render)
- **O(1)** por app (simple string matching)
- **Total:** ~50-100 apps × O(1) = insignificante

### Memoria
- Enum instances son singleton (no overhead)
- String resources se cargan lazy
- Total overhead: < 1KB

### UI Rendering
- FilterChips son ligeros (Material 3)
- Solo 10 chips máximo
- Scrolling nativo de Compose

## Mejoras Futuras

### 1. Categorías Dinámicas
```kotlin
// Agregar categorías basadas en uso
val topCategories = usageStats.getTopCategories(limit = 5)
CategoryFilter(categories = topCategories + AppCategory.ALL)
```

### 2. Machine Learning
```kotlin
// Categorización más inteligente
val category = MLCategorizer.predict(
    packageName = app.packageName,
    appName = app.name,
    permissions = app.permissions
)
```

### 3. Contador de Apps
```kotlin
FilterChip(
    label = { Text("${stringResource(category.displayName)} (${count})") }
)
```

### 4. Categorías Personalizadas
```kotlin
// Usuario define sus propias categorías
data class CustomCategory(
    val name: String,
    val icon: ImageVector,
    val packagePatterns: List<String>
)
```
