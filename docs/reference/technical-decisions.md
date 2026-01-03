# Technical Decisions - Umbral

**Estado:** ✅ Completo
**Última actualización:** 2026-01-03T01:59:58Z

---

## 1. Visión General

### 1.1 Descripción
Umbral es una aplicación Android open-source que permite a los usuarios bloquear automáticamente apps de redes sociales cuando salen de casa, utilizando tags NFC como trigger físico. El nombre "Umbral" (threshold en inglés, μεταξύ/metaxy en griego) representa el concepto filosófico platónico del espacio liminal - el momento consciente de transición entre estar conectado y desconectado.

**Filosofía del producto:**
> "El umbral de tu casa es el punto de decisión consciente. Al cruzarlo, eliges tu estado digital."

### 1.2 Objetivo Principal
**Productividad y bienestar digital** - Ayudar a los usuarios a crear límites físicos saludables entre el hogar y el mundo exterior mediante bloqueo automático de apps distractoras.

### 1.3 Scope
**Modalidad:** Modo Completo (Producto Enterprise-Ready)
**Timeline estimado:** 12-16 semanas
**Target inicial:** Android (iOS ya cubierto por Foqos open source)

### 1.4 Diferenciadores vs Competencia
- **100% Open Source** (vs Brick comercial)
- **Android-first** (Foqos es iOS-only)
- **Enfoque en UX** (Foqos es muy técnico)
- **Mercado hispanohablante** (docs y UI en español)
- **Tags NFC baratos de Amazon** (< $1 USD)
- **Colaboración con Foqos** (no competencia, complemento)

---

## 2. Stack Tecnológico

### 2.1 Frontend - Android Nativo

**Framework:** Kotlin + Jetpack Compose

**Justificación:**
- ✅ Mejor rendimiento que cross-platform para features nativas críticas (NFC, app blocking)
- ✅ Acceso completo a APIs de Android sin limitaciones
- ✅ Jetpack Compose = UI declarativa moderna (similar a SwiftUI de Foqos)
- ✅ Mejor soporte NFC en background que React Native/Flutter
- ✅ UsageStatsManager y AccessibilityService requieren implementación nativa

**Versión mínima:** Android 8.0 (API 26) - Balance entre cobertura de mercado y features modernas
**Versión target:** Android 15 (API 35)

### 2.2 Arquitectura

**Patrón:** Clean Architecture + MVVM

```
app/
├── data/           # Data sources, repositories
│   ├── local/      # Room DB, DataStore
│   ├── nfc/        # NFC operations
│   └── repository/ # Repository implementations
├── domain/         # Business logic, use cases
│   ├── model/      # Domain models
│   └── usecase/    # Use cases
└── presentation/   # UI layer
    ├── screens/    # Compose screens
    ├── components/ # Reusable UI components
    └── viewmodel/  # ViewModels
```

**Justificación:**
- ✅ Separación de responsabilidades clara
- ✅ Testeable (cada capa independiente)
- ✅ Escalable para agregar features sin romper existente
- ✅ Estándar de la industria Android

### 2.3 Persistencia Local

**Database:** Room (SQLite wrapper)

**Justificación:**
- ✅ 100% offline-first (privacidad, sin dependencias cloud)
- ✅ Type-safe compile-time verification
- ✅ LiveData/Flow integration para UI reactiva
- ✅ Migrations manejables

**Preferences:** DataStore (Preferences DataStore)

**Justificación:**
- ✅ Reemplazo moderno de SharedPreferences
- ✅ Type-safe
- ✅ Coroutines support
- ✅ Migrations automáticas

### 2.4 Dependency Injection

**Framework:** Hilt (Dagger wrapper)

**Justificación:**
- ✅ Estándar oficial de Google para Android
- ✅ Compile-time safety
- ✅ Menos boilerplate que Dagger puro
- ✅ Integración perfecta con ViewModels y Compose

### 2.5 NFC

**API:** Android NFC API (android.nfc.*)

**Tags soportados:** NTAG213, NTAG215, NTAG216 (compatibles con Foqos)

**Funcionalidades:**
- ✅ Foreground dispatch para lectura confiable
- ✅ Background tag reading (Android 10+)
- ✅ Write NDEF messages con URL schemes
- ✅ Tag UID binding (bloquear con tag específico)

### 2.6 App Blocking Mechanisms

**Estrategia por niveles:**

**Nivel 1 (Preferido):** UsageStatsManager + Digital Wellbeing API
- ✅ No requiere AccessibilityService (menos fricción en Google Play)
- ✅ API oficial de Google para tracking de uso
- ✅ Puede implementar timers y límites

**Nivel 2 (Backup):** AccessibilityService
- ⚠️ Solo si Nivel 1 no es suficiente
- ⚠️ Requiere Permission Declaration Form de Google Play
- ⚠️ Mayor escrutinio en revisión de apps
- ✅ Permite bloqueo más agresivo

**Nivel 3 (Experimental):** Work Profile API
- 🔬 Crear work profile automático para apps bloqueadas
- 🔬 Puede ser demasiado intrusivo para usuarios

**Decisión inicial:** Implementar Nivel 1 para MVP, evaluar necesidad de Nivel 2 basado en feedback.

### 2.7 Testing

**Unit Tests:** JUnit 5 + MockK
**UI Tests:** Compose Testing + Espresso
**Integration Tests:** Room in-memory DB tests

**Coverage objetivo:** 70%+ para domain layer, 50%+ para presentation

### 2.8 Build System

**Build Tool:** Gradle (Kotlin DSL)
**Build Variants:** debug, release, beta
**Signing:** Play App Signing (Google manages signing key)

### 2.9 CI/CD

**Platform:** GitHub Actions (plan inicial)

**Pipeline:**
- ✅ Lint + detekt (static analysis)
- ✅ Unit tests
- ✅ Build APK/AAB
- ✅ Upload to Play Console (beta track)

### 2.10 Distribución

**Primary:** Google Play Store
**Secondary (futuro):** F-Droid (para usuarios privacy-focused)

---

## 3. Features V1 (Completo)

### 3.1 Core Features ✅

- [x] **NFC Tag Reading/Writing**
  - Detectar tags NTAG213/215/216
  - Escribir NDEF records con app identifier
  - Leer tags en foreground y background
  - Bind tag UID para seguridad (bloquear con tag específico)

- [x] **Bloqueo Básico de Apps**
  - UsageStatsManager integration
  - Detectar cuando usuario intenta abrir app bloqueada
  - Overlay/interceptor para prevenir apertura
  - Notificación cuando bloqueo está activo

- [x] **Whitelist de Apps Esenciales**
  - Apps del sistema (Phone, Messages, Camera, Calendar)
  - Apps bancarias (usuario selecciona)
  - Apps personalizadas (agregar/quitar)
  - Presets inteligentes basados en categorías

- [x] **Multiple Blocking Profiles**
  - Perfiles predefinidos: "Social Media", "Games", "News"
  - Perfiles custom del usuario
  - Diferentes perfiles para diferentes tags NFC
  - Quick switch entre perfiles

### 3.2 Nice-to-Have Features ✅

- [x] **Timer-Based Auto-Unblock**
  - Temporizador configurable (1h, 2h, 4h, custom)
  - Auto-unlock después de X tiempo fuera de casa
  - Notificación antes de desbloquear

- [x] **QR Code Alternative**
  - Fallback si NFC falla o no disponible
  - Generar QR code para imprimir y pegar en puerta
  - Mismo comportamiento que NFC

- [x] **Widgets**
  - Widget de estado (bloqueado/desbloqueado)
  - Widget para quick toggle de perfiles
  - Widget countdown timer

- [x] **Usage Statistics**
  - Tiempo bloqueado vs tiempo desbloqueado
  - Apps más bloqueadas
  - Racha de días usando Umbral
  - Gráficas semanales/mensuales

### 3.3 Advanced Features ✅

- [x] **Physical Unlock Requirement**
  - Opción: solo tag específico puede desbloquear
  - Prevenir "trampa" de desinstalar app
  - Device Admin API (con disclaimers claros)

- [x] **Focus Mode Integration**
  - Integrar con Android Digital Wellbeing Focus Mode
  - Trigger Focus Mode al activar NFC
  - Sincronizar whitelist con Focus Mode

- [x] **Shortcuts & Quick Settings**
  - Quick Settings tile para toggle rápido
  - App Shortcuts para perfiles frecuentes
  - Integración con launcher shortcuts

---

## 4. Schema de Base de Datos

**ORM:** Room

### 4.1 Entidades

```kotlin
@Entity(tableName = "blocking_profiles")
data class BlockingProfile(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String?,
    val icon: String, // Material icon name
    val createdAt: Long,
    val updatedAt: Long
)

@Entity(tableName = "blocked_apps")
data class BlockedApp(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val profileId: String, // FK to BlockingProfile
    val packageName: String,
    val appName: String,
    val iconUri: String?,
    @ColumnInfo(name = "is_whitelisted") val isWhitelisted: Boolean = false
)

@Entity(tableName = "nfc_tags")
data class NfcTag(
    @PrimaryKey val uid: String, // Tag UID
    val profileId: String?, // FK to BlockingProfile (null = any profile)
    val name: String, // User-friendly name "Puerta Principal"
    val createdAt: Long,
    val lastUsedAt: Long?
)

@Entity(tableName = "blocking_sessions")
data class BlockingSession(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val profileId: String,
    val startedAt: Long,
    val endedAt: Long?,
    val triggerMethod: TriggerMethod, // NFC, QR, MANUAL
    val tagUid: String?
)

enum class TriggerMethod { NFC, QR, MANUAL, TIMER }
```

### 4.2 Relaciones

- BlockingProfile 1:N BlockedApp
- BlockingProfile 1:N NfcTag
- BlockingProfile 1:N BlockingSession

### 4.3 Migrations

Iniciar con schema v1, planear migrations para:
- v2: Agregar campos de estadísticas
- v3: Agregar support para scheduled blocking
- v4: Agregar sync con cloud (futuro)

---

## 5. Integraciones

### 5.1 Ninguna por ahora ✅

**Decisión:** 100% local-first para V1

**Justificación:**
- ✅ Privacidad total (no data sale del dispositivo)
- ✅ Funciona 100% offline
- ✅ Desarrollo más simple y rápido
- ✅ No requiere backend/infraestructura cloud

### 5.2 Futuras Integraciones (Post-V1)

**Cloud Sync (opcional - Supabase):**
- Backup de perfiles y configuración
- Sincronización entre dispositivos
- Estadísticas agregadas anónimas

**Analytics (opcional - Privacy-focused):**
- PostHog self-hosted o Plausible
- Solo métricas agregadas y anónimas
- Opt-in explícito del usuario

---

## 6. Competidores Analizados

### 6.1 Principales Competidores

1. **Foqos** (iOS, open source)
   - ✅ Inspiración principal
   - ✅ Arquitectura SwiftUI + Family Controls API
   - ✅ Colaboración, no competencia

2. **Brick** (iOS/Android, comercial)
   - 💰 Producto comercial (~$50 USD + hardware)
   - 📦 Requiere device físico propietario
   - 🔒 Closed source

3. **Unpluq** (iOS/Android, comercial)
   - 💰 Suscripción mensual
   - 🔒 Closed source
   - ⚙️ Más features pero más complejo

### 6.2 Análisis Detallado

✅ **Completado** - Ver [competitive-analysis.md](./competitive-analysis.md) para análisis completo de:
- Feature parity entre competidores
- Fortalezas y debilidades de cada uno
- User reviews y estadísticas
- Oportunidades de diferenciación
- Matriz de priorización de features

---

## 7. Arquitectura Detallada

**Estado:** ✅ Completo
**Última actualización:** 2026-01-03T01:59:58Z

### 7.1 Arquitectura de Capas (Clean Architecture)

```
┌──────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                         │
│  ┌────────────┬────────────┬────────────┬────────────┐      │
│  │  Screens   │  ViewModels│ Components │   Widgets  │      │
│  │ (Compose)  │  (MVVM)    │ (Reusable) │  (Glance)  │      │
│  └─────┬──────┴──────┬─────┴──────┬─────┴──────┬─────┘      │
│        │             │            │            │             │
│        └─────────────┴────────────┴────────────┘             │
│                          │                                    │
│                          ▼                                    │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────┐
│                      DOMAIN LAYER                             │
│  ┌────────────┬────────────┬────────────┬────────────┐      │
│  │  Use Cases │   Models   │  Repository│   Enums    │      │
│  │  (Logic)   │  (Entities)│ Interfaces │  (Types)   │      │
│  └─────┬──────┴──────┬─────┴──────┬─────┴──────┬─────┘      │
│        │             │            │            │             │
│        └─────────────┴────────────┴────────────┘             │
│                          │                                    │
│                          ▼                                    │
└──────────────────────────────────────────────────────────────┘
                           │
                           ▼
┌──────────────────────────────────────────────────────────────┐
│                       DATA LAYER                              │
│  ┌────────────┬────────────┬────────────┬────────────┐      │
│  │ Repositories│    DAOs    │   Room DB  │   NFC API  │      │
│  │  (Impl)    │ (Queries)  │ (SQLite)   │  (Service) │      │
│  └─────┬──────┴──────┬─────┴──────┬─────┴──────┬─────┘      │
│        │             │            │            │             │
│        │             │            │            │             │
│  ┌─────▼──────┬──────▼────┬───────▼────┬───────▼─────┐     │
│  │ DataStore  │ WorkManager│ UsageStats │ Notifications│     │
│  │ (Prefs)    │ (Tasks)    │ (Blocking) │  (Channels)  │     │
│  └────────────┴───────────┴────────────┴──────────────┘     │
└──────────────────────────────────────────────────────────────┘
```

**Principios:**
1. **Dependency Rule:** Dependencias solo apuntan hacia adentro (Presentation → Domain → Data)
2. **Single Responsibility:** Cada capa tiene una responsabilidad única
3. **Testability:** Capas desacopladas, fácil mocking
4. **Scalability:** Agregar features sin modificar core

---

### 7.2 Estructura de Carpetas Completa

```
app/src/main/
├── kotlin/com/umbral/app/
│   ├── UmbralApplication.kt           # Application class
│   │
│   ├── data/                          # DATA LAYER
│   │   ├── local/                     # Local data sources
│   │   │   ├── dao/                   # Room DAOs
│   │   │   │   ├── BlockingProfileDao.kt
│   │   │   │   ├── BlockedAppDao.kt
│   │   │   │   ├── NfcTagDao.kt
│   │   │   │   ├── BlockingSessionDao.kt
│   │   │   │   └── StatisticsDao.kt
│   │   │   │
│   │   │   ├── entity/                # Room entities
│   │   │   │   ├── BlockingProfileEntity.kt
│   │   │   │   ├── BlockedAppEntity.kt
│   │   │   │   ├── NfcTagEntity.kt
│   │   │   │   ├── BlockingSessionEntity.kt
│   │   │   │   └── relations/         # Database relations
│   │   │   │       ├── ProfileWithApps.kt
│   │   │   │       ├── ProfileWithTags.kt
│   │   │   │       └── SessionWithProfile.kt
│   │   │   │
│   │   │   ├── database/              # Database config
│   │   │   │   ├── UmbralDatabase.kt
│   │   │   │   ├── Converters.kt
│   │   │   │   └── migrations/
│   │   │   │       └── Migration_1_2.kt
│   │   │   │
│   │   │   └── preferences/           # DataStore
│   │   │       ├── AppPreferences.kt
│   │   │       └── PreferencesKeys.kt
│   │   │
│   │   ├── nfc/                       # NFC operations
│   │   │   ├── NfcManager.kt
│   │   │   ├── NdefWriter.kt
│   │   │   ├── NdefReader.kt
│   │   │   └── models/
│   │   │       ├── NfcTagInfo.kt
│   │   │       └── NdefMessage.kt
│   │   │
│   │   ├── blocking/                  # App blocking
│   │   │   ├── AppBlockingManager.kt
│   │   │   ├── UsageStatsService.kt
│   │   │   ├── AccessibilityMonitor.kt
│   │   │   └── OverlayService.kt
│   │   │
│   │   ├── service/                   # Background services
│   │   │   ├── BlockingService.kt
│   │   │   ├── NfcListenerService.kt
│   │   │   └── TimerService.kt
│   │   │
│   │   ├── worker/                    # WorkManager workers
│   │   │   ├── StatsSyncWorker.kt
│   │   │   └── CleanupWorker.kt
│   │   │
│   │   └── repository/                # Repository implementations
│   │       ├── BlockingProfileRepositoryImpl.kt
│   │       ├── BlockedAppRepositoryImpl.kt
│   │       ├── NfcTagRepositoryImpl.kt
│   │       ├── BlockingSessionRepositoryImpl.kt
│   │       ├── StatisticsRepositoryImpl.kt
│   │       └── PreferencesRepositoryImpl.kt
│   │
│   ├── domain/                        # DOMAIN LAYER
│   │   ├── model/                     # Domain models (business logic)
│   │   │   ├── BlockingProfile.kt
│   │   │   ├── BlockedApp.kt
│   │   │   ├── NfcTag.kt
│   │   │   ├── BlockingSession.kt
│   │   │   ├── AppInfo.kt
│   │   │   ├── Statistics.kt
│   │   │   └── enums/
│   │   │       ├── TriggerMethod.kt
│   │   │       ├── BlockingStatus.kt
│   │   │       └── ProfileIcon.kt
│   │   │
│   │   ├── repository/                # Repository interfaces
│   │   │   ├── BlockingProfileRepository.kt
│   │   │   ├── BlockedAppRepository.kt
│   │   │   ├── NfcTagRepository.kt
│   │   │   ├── BlockingSessionRepository.kt
│   │   │   ├── StatisticsRepository.kt
│   │   │   └── PreferencesRepository.kt
│   │   │
│   │   └── usecase/                   # Use cases (business logic)
│   │       ├── profile/
│   │       │   ├── CreateProfileUseCase.kt
│   │       │   ├── UpdateProfileUseCase.kt
│   │       │   ├── DeleteProfileUseCase.kt
│   │       │   ├── GetProfilesUseCase.kt
│   │       │   └── GetActiveProfileUseCase.kt
│   │       │
│   │       ├── blocking/
│   │       │   ├── StartBlockingUseCase.kt
│   │       │   ├── StopBlockingUseCase.kt
│   │       │   ├── IsAppBlockedUseCase.kt
│   │       │   ├── GetBlockingStatusUseCase.kt
│   │       │   └── ToggleBlockingUseCase.kt
│   │       │
│   │       ├── app/
│   │       │   ├── GetInstalledAppsUseCase.kt
│   │       │   ├── AddBlockedAppUseCase.kt
│   │       │   ├── RemoveBlockedAppUseCase.kt
│   │       │   └── GetBlockedAppsUseCase.kt
│   │       │
│   │       ├── nfc/
│   │       │   ├── ReadNfcTagUseCase.kt
│   │       │   ├── WriteNfcTagUseCase.kt
│   │       │   ├── BindTagToProfileUseCase.kt
│   │       │   └── GetTagsUseCase.kt
│   │       │
│   │       └── statistics/
│   │           ├── GetUsageStatsUseCase.kt
│   │           ├── GetStreakUseCase.kt
│   │           └── GetTimeBlockedUseCase.kt
│   │
│   ├── presentation/                  # PRESENTATION LAYER
│   │   ├── MainActivity.kt
│   │   │
│   │   ├── navigation/
│   │   │   ├── NavGraph.kt
│   │   │   ├── Screen.kt
│   │   │   └── NavigationArgs.kt
│   │   │
│   │   ├── theme/
│   │   │   ├── Theme.kt
│   │   │   ├── Color.kt
│   │   │   ├── Type.kt
│   │   │   └── Shape.kt
│   │   │
│   │   ├── components/                # Reusable UI components
│   │   │   ├── AppCard.kt
│   │   │   ├── ProfileCard.kt
│   │   │   ├── StatCard.kt
│   │   │   ├── LoadingIndicator.kt
│   │   │   ├── ErrorView.kt
│   │   │   └── EmptyState.kt
│   │   │
│   │   ├── screens/                   # Feature screens
│   │   │   ├── onboarding/
│   │   │   │   ├── OnboardingScreen.kt
│   │   │   │   ├── OnboardingViewModel.kt
│   │   │   │   └── OnboardingState.kt
│   │   │   │
│   │   │   ├── home/
│   │   │   │   ├── HomeScreen.kt
│   │   │   │   ├── HomeViewModel.kt
│   │   │   │   └── HomeState.kt
│   │   │   │
│   │   │   ├── profiles/
│   │   │   │   ├── ProfileListScreen.kt
│   │   │   │   ├── ProfileDetailScreen.kt
│   │   │   │   ├── CreateProfileScreen.kt
│   │   │   │   ├── ProfileViewModel.kt
│   │   │   │   └── ProfileState.kt
│   │   │   │
│   │   │   ├── apps/
│   │   │   │   ├── AppSelectionScreen.kt
│   │   │   │   ├── AppViewModel.kt
│   │   │   │   └── AppState.kt
│   │   │   │
│   │   │   ├── nfc/
│   │   │   │   ├── NfcSetupScreen.kt
│   │   │   │   ├── NfcWriteScreen.kt
│   │   │   │   ├── NfcViewModel.kt
│   │   │   │   └── NfcState.kt
│   │   │   │
│   │   │   ├── statistics/
│   │   │   │   ├── StatisticsScreen.kt
│   │   │   │   ├── StatisticsViewModel.kt
│   │   │   │   └── StatisticsState.kt
│   │   │   │
│   │   │   └── settings/
│   │   │       ├── SettingsScreen.kt
│   │   │       ├── SettingsViewModel.kt
│   │   │       └── SettingsState.kt
│   │   │
│   │   └── widget/                    # Home screen widgets
│   │       ├── StatusWidget.kt
│   │       ├── QuickToggleWidget.kt
│   │       ├── TimerWidget.kt
│   │       └── WidgetReceiver.kt
│   │
│   ├── di/                            # Dependency Injection (Hilt)
│   │   ├── AppModule.kt
│   │   ├── DatabaseModule.kt
│   │   ├── RepositoryModule.kt
│   │   ├── UseCaseModule.kt
│   │   ├── ServiceModule.kt
│   │   └── NfcModule.kt
│   │
│   └── util/                          # Utilities
│       ├── Constants.kt
│       ├── Extension.kt
│       ├── PermissionHelper.kt
│       ├── NotificationHelper.kt
│       └── DateTimeUtil.kt
│
└── res/                               # Resources
    ├── values/
    │   ├── strings.xml                # 🇪🇸 UI text in Spanish
    │   ├── colors.xml
    │   ├── dimens.xml
    │   └── themes.xml
    │
    ├── drawable/                      # Icons and images
    ├── layout/                        # XML layouts (minimal, prefer Compose)
    └── xml/
        └── nfc_tech_filter.xml        # NFC tech filter config
```

---

### 7.3 Schema de Base de Datos (Room) - Detallado

#### Database Version 1

**Database Name:** `umbral.db`
**Version:** 1
**Export Schema:** true (para version control)

#### Tabla: `blocking_profiles`

**Propósito:** Almacenar perfiles de bloqueo con configuración de apps a bloquear.

**Definición:**
```kotlin
@Entity(
    tableName = "blocking_profiles",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["created_at"]),
        Index(value = ["is_active"])
    ]
)
data class BlockingProfileEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "icon")
    val icon: String, // Material icon name: "social_media", "games", etc.

    @ColumnInfo(name = "color")
    val color: Int, // Accent color for profile (ARGB)

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = false, // Solo un perfil activo a la vez

    @ColumnInfo(name = "is_predefined")
    val isPredefined: Boolean = false, // Perfil del sistema vs custom

    @ColumnInfo(name = "auto_unlock_minutes")
    val autoUnlockMinutes: Int? = null, // Timer: null = sin timer

    @ColumnInfo(name = "require_physical_unlock")
    val requirePhysicalUnlock: Boolean = false, // Requiere NFC para desbloquear

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis()
)
```

**Índices:**
- `PRIMARY KEY (id)`
- `UNIQUE INDEX idx_profiles_name ON blocking_profiles(name)` - Prevenir duplicados
- `INDEX idx_profiles_created ON blocking_profiles(created_at)` - Ordenar por fecha
- `INDEX idx_profiles_active ON blocking_profiles(is_active)` - Query rápida de perfil activo

**Constraints:**
- `name` NOT NULL
- `icon` NOT NULL
- Solo un perfil puede tener `is_active = true` (manejado en app layer)

---

#### Tabla: `blocked_apps`

**Propósito:** Apps bloqueadas por cada perfil.

**Definición:**
```kotlin
@Entity(
    tableName = "blocked_apps",
    foreignKeys = [
        ForeignKey(
            entity = BlockingProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE // Borrar apps al borrar perfil
        )
    ],
    indices = [
        Index(value = ["profile_id"]),
        Index(value = ["package_name"]),
        Index(value = ["profile_id", "package_name"], unique = true)
    ]
)
data class BlockedAppEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "profile_id")
    val profileId: String,

    @ColumnInfo(name = "package_name")
    val packageName: String, // ej: "com.instagram.android"

    @ColumnInfo(name = "app_name")
    val appName: String, // ej: "Instagram"

    @ColumnInfo(name = "app_icon_uri")
    val appIconUri: String? = null, // URI del icono de la app

    @ColumnInfo(name = "category")
    val category: String? = null, // "Social", "Games", "News"

    @ColumnInfo(name = "is_whitelisted")
    val isWhitelisted: Boolean = false, // Excepción: no bloquear esta app

    @ColumnInfo(name = "times_blocked")
    val timesBlocked: Int = 0, // Estadística

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis()
)
```

**Índices:**
- `PRIMARY KEY (id)`
- `INDEX idx_blocked_apps_profile ON blocked_apps(profile_id)`
- `INDEX idx_blocked_apps_package ON blocked_apps(package_name)`
- `UNIQUE INDEX idx_blocked_apps_profile_package ON blocked_apps(profile_id, package_name)` - Una app solo una vez por perfil

**Foreign Keys:**
- `profile_id` → `blocking_profiles(id)` ON DELETE CASCADE

---

#### Tabla: `nfc_tags`

**Propósito:** Tags NFC configurados y sus bindings a perfiles.

**Definición:**
```kotlin
@Entity(
    tableName = "nfc_tags",
    foreignKeys = [
        ForeignKey(
            entity = BlockingProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.SET_NULL // Mantener tag si perfil se borra
        )
    ],
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["profile_id"]),
        Index(value = ["last_used_at"])
    ]
)
data class NfcTagEntity(
    @PrimaryKey
    val uid: String, // Tag UID (unique identifier del tag NFC)

    @ColumnInfo(name = "profile_id")
    val profileId: String? = null, // null = toggle cualquier perfil activo

    @ColumnInfo(name = "name")
    val name: String, // "Puerta Principal", "Entrada Garage"

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "location")
    val location: String? = null, // Ubicación física del tag

    @ColumnInfo(name = "tag_type")
    val tagType: String, // "NTAG213", "NTAG215", etc.

    @ColumnInfo(name = "is_locked")
    val isLocked: Boolean = false, // Solo este tag puede desbloquear

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: Long? = null,

    @ColumnInfo(name = "usage_count")
    val usageCount: Int = 0
)
```

**Índices:**
- `PRIMARY KEY (uid)`
- `UNIQUE INDEX idx_nfc_tags_uid ON nfc_tags(uid)`
- `INDEX idx_nfc_tags_profile ON nfc_tags(profile_id)`
- `INDEX idx_nfc_tags_last_used ON nfc_tags(last_used_at)`

**Foreign Keys:**
- `profile_id` → `blocking_profiles(id)` ON DELETE SET NULL

---

#### Tabla: `blocking_sessions`

**Propósito:** Historial de sesiones de bloqueo para estadísticas.

**Definición:**
```kotlin
@Entity(
    tableName = "blocking_sessions",
    foreignKeys = [
        ForeignKey(
            entity = BlockingProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = NfcTagEntity::class,
            parentColumns = ["uid"],
            childColumns = ["tag_uid"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["profile_id"]),
        Index(value = ["started_at"]),
        Index(value = ["ended_at"]),
        Index(value = ["trigger_method"])
    ]
)
data class BlockingSessionEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "profile_id")
    val profileId: String,

    @ColumnInfo(name = "started_at")
    val startedAt: Long,

    @ColumnInfo(name = "ended_at")
    val endedAt: Long? = null, // null = sesión activa

    @ColumnInfo(name = "trigger_method")
    val triggerMethod: String, // "NFC", "QR", "MANUAL", "TIMER"

    @ColumnInfo(name = "tag_uid")
    val tagUid: String? = null, // Si fue activado por NFC

    @ColumnInfo(name = "apps_blocked_count")
    val appsBlockedCount: Int = 0,

    @ColumnInfo(name = "block_attempts")
    val blockAttempts: Int = 0, // Veces que usuario intentó abrir app bloqueada

    @ColumnInfo(name = "notes")
    val notes: String? = null
)
```

**Índices:**
- `PRIMARY KEY (id)`
- `INDEX idx_sessions_profile ON blocking_sessions(profile_id)`
- `INDEX idx_sessions_started ON blocking_sessions(started_at)`
- `INDEX idx_sessions_ended ON blocking_sessions(ended_at)`
- `INDEX idx_sessions_trigger ON blocking_sessions(trigger_method)`

**Foreign Keys:**
- `profile_id` → `blocking_profiles(id)` ON DELETE CASCADE
- `tag_uid` → `nfc_tags(uid)` ON DELETE SET NULL

---

#### Tabla: `app_usage_stats` (Estadísticas)

**Propósito:** Tracking detallado de bloqueos por app.

**Definición:**
```kotlin
@Entity(
    tableName = "app_usage_stats",
    indices = [
        Index(value = ["package_name", "date"], unique = true),
        Index(value = ["date"])
    ]
)
data class AppUsageStatsEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "date")
    val date: String, // "2026-01-03" formato ISO date

    @ColumnInfo(name = "times_blocked")
    val timesBlocked: Int = 0,

    @ColumnInfo(name = "total_block_duration_ms")
    val totalBlockDurationMs: Long = 0,

    @ColumnInfo(name = "last_blocked_at")
    val lastBlockedAt: Long? = null
)
```

**Índices:**
- `PRIMARY KEY (id)`
- `UNIQUE INDEX idx_stats_package_date ON app_usage_stats(package_name, date)`
- `INDEX idx_stats_date ON app_usage_stats(date)`

---

#### Tabla: `user_settings` (Configuración)

**Propósito:** Preferencias del usuario (alternativa: DataStore Preferences).

**Definición:**
```kotlin
@Entity(
    tableName = "user_settings"
)
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1, // Solo un row

    @ColumnInfo(name = "onboarding_completed")
    val onboardingCompleted: Boolean = false,

    @ColumnInfo(name = "notifications_enabled")
    val notificationsEnabled: Boolean = true,

    @ColumnInfo(name = "haptic_feedback")
    val hapticFeedback: Boolean = true,

    @ColumnInfo(name = "dark_mode")
    val darkMode: String = "SYSTEM", // "LIGHT", "DARK", "SYSTEM"

    @ColumnInfo(name = "language")
    val language: String = "es", // "es", "en"

    @ColumnInfo(name = "show_blocking_overlay")
    val showBlockingOverlay: Boolean = true,

    @ColumnInfo(name = "streak_start_date")
    val streakStartDate: String? = null, // Inicio de racha

    @ColumnInfo(name = "total_sessions")
    val totalSessions: Int = 0
)
```

**Índices:**
- `PRIMARY KEY (id)`

---

### Database Relationships (Relations)

#### 1. `ProfileWithApps`
```kotlin
data class ProfileWithApps(
    @Embedded val profile: BlockingProfileEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "profile_id"
    )
    val blockedApps: List<BlockedAppEntity>
)
```

#### 2. `ProfileWithTags`
```kotlin
data class ProfileWithTags(
    @Embedded val profile: BlockingProfileEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "profile_id"
    )
    val tags: List<NfcTagEntity>
)
```

#### 3. `SessionWithProfile`
```kotlin
data class SessionWithProfile(
    @Embedded val session: BlockingSessionEntity,

    @Relation(
        parentColumn = "profile_id",
        entityColumn = "id"
    )
    val profile: BlockingProfileEntity,

    @Relation(
        parentColumn = "tag_uid",
        entityColumn = "uid"
    )
    val tag: NfcTagEntity?
)
```

#### 4. `CompleteProfile` (Full profile data)
```kotlin
data class CompleteProfile(
    @Embedded val profile: BlockingProfileEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "profile_id"
    )
    val blockedApps: List<BlockedAppEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "profile_id"
    )
    val tags: List<NfcTagEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "profile_id"
    )
    val sessions: List<BlockingSessionEntity>
)
```

---

### Database Class

```kotlin
@Database(
    entities = [
        BlockingProfileEntity::class,
        BlockedAppEntity::class,
        NfcTagEntity::class,
        BlockingSessionEntity::class,
        AppUsageStatsEntity::class,
        UserSettingsEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class UmbralDatabase : RoomDatabase() {
    abstract fun blockingProfileDao(): BlockingProfileDao
    abstract fun blockedAppDao(): BlockedAppDao
    abstract fun nfcTagDao(): NfcTagDao
    abstract fun blockingSessionDao(): BlockingSessionDao
    abstract fun appUsageStatsDao(): AppUsageStatsDao
    abstract fun userSettingsDao(): UserSettingsDao

    companion object {
        const val DATABASE_NAME = "umbral.db"
    }
}
```

---

### Type Converters

```kotlin
class Converters {
    @TypeConverter
    fun fromTriggerMethod(value: TriggerMethod): String {
        return value.name
    }

    @TypeConverter
    fun toTriggerMethod(value: String): TriggerMethod {
        return enumValueOf(value)
    }

    @TypeConverter
    fun fromDarkMode(value: DarkMode): String {
        return value.name
    }

    @TypeConverter
    fun toDarkMode(value: String): DarkMode {
        return enumValueOf(value)
    }
}
```

---

### 7.4 DAOs (Data Access Objects)

#### BlockingProfileDao

```kotlin
@Dao
interface BlockingProfileDao {
    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: BlockingProfileEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(profiles: List<BlockingProfileEntity>)

    // READ
    @Query("SELECT * FROM blocking_profiles WHERE id = :id")
    suspend fun getById(id: String): BlockingProfileEntity?

    @Query("SELECT * FROM blocking_profiles WHERE id = :id")
    fun getByIdFlow(id: String): Flow<BlockingProfileEntity?>

    @Query("SELECT * FROM blocking_profiles ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<BlockingProfileEntity>>

    @Query("SELECT * FROM blocking_profiles WHERE is_active = 1 LIMIT 1")
    suspend fun getActiveProfile(): BlockingProfileEntity?

    @Query("SELECT * FROM blocking_profiles WHERE is_active = 1 LIMIT 1")
    fun getActiveProfileFlow(): Flow<BlockingProfileEntity?>

    @Transaction
    @Query("SELECT * FROM blocking_profiles WHERE id = :id")
    suspend fun getProfileWithApps(id: String): ProfileWithApps?

    @Transaction
    @Query("SELECT * FROM blocking_profiles WHERE id = :id")
    fun getProfileWithAppsFlow(id: String): Flow<ProfileWithApps?>

    @Transaction
    @Query("SELECT * FROM blocking_profiles WHERE id = :id")
    suspend fun getCompleteProfile(id: String): CompleteProfile?

    @Query("SELECT COUNT(*) FROM blocking_profiles")
    suspend fun getCount(): Int

    @Query("SELECT * FROM blocking_profiles WHERE is_predefined = 1")
    suspend fun getPredefinedProfiles(): List<BlockingProfileEntity>

    // UPDATE
    @Update
    suspend fun update(profile: BlockingProfileEntity)

    @Query("UPDATE blocking_profiles SET is_active = 0")
    suspend fun deactivateAll()

    @Query("UPDATE blocking_profiles SET is_active = 1 WHERE id = :id")
    suspend fun activateProfile(id: String)

    @Transaction
    suspend fun setActiveProfile(id: String) {
        deactivateAll()
        activateProfile(id)
    }

    @Query("UPDATE blocking_profiles SET updated_at = :timestamp WHERE id = :id")
    suspend fun updateTimestamp(id: String, timestamp: Long = System.currentTimeMillis())

    // DELETE
    @Delete
    suspend fun delete(profile: BlockingProfileEntity)

    @Query("DELETE FROM blocking_profiles WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM blocking_profiles WHERE is_predefined = 0")
    suspend fun deleteAllCustomProfiles()
}
```

#### BlockedAppDao

```kotlin
@Dao
interface BlockedAppDao {
    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: BlockedAppEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(apps: List<BlockedAppEntity>)

    // READ
    @Query("SELECT * FROM blocked_apps WHERE profile_id = :profileId")
    suspend fun getByProfile(profileId: String): List<BlockedAppEntity>

    @Query("SELECT * FROM blocked_apps WHERE profile_id = :profileId")
    fun getByProfileFlow(profileId: String): Flow<List<BlockedAppEntity>>

    @Query("SELECT * FROM blocked_apps WHERE profile_id = :profileId AND is_whitelisted = 0")
    suspend fun getBlockedByProfile(profileId: String): List<BlockedAppEntity>

    @Query("SELECT * FROM blocked_apps WHERE profile_id = :profileId AND is_whitelisted = 1")
    suspend fun getWhitelistedByProfile(profileId: String): List<BlockedAppEntity>

    @Query("""
        SELECT * FROM blocked_apps
        WHERE profile_id = :profileId
        AND package_name = :packageName
    """)
    suspend fun getByPackageName(profileId: String, packageName: String): BlockedAppEntity?

    @Query("SELECT package_name FROM blocked_apps WHERE profile_id = :profileId AND is_whitelisted = 0")
    suspend fun getBlockedPackageNames(profileId: String): List<String>

    @Query("SELECT COUNT(*) FROM blocked_apps WHERE profile_id = :profileId AND is_whitelisted = 0")
    suspend fun getBlockedCount(profileId: String): Int

    @Query("""
        SELECT * FROM blocked_apps
        WHERE profile_id = :profileId
        AND category = :category
    """)
    suspend fun getByCategory(profileId: String, category: String): List<BlockedAppEntity>

    // UPDATE
    @Update
    suspend fun update(app: BlockedAppEntity)

    @Query("UPDATE blocked_apps SET is_whitelisted = :isWhitelisted WHERE id = :id")
    suspend fun updateWhitelistStatus(id: String, isWhitelisted: Boolean)

    @Query("UPDATE blocked_apps SET times_blocked = times_blocked + 1 WHERE id = :id")
    suspend fun incrementTimesBlocked(id: String)

    // DELETE
    @Delete
    suspend fun delete(app: BlockedAppEntity)

    @Query("DELETE FROM blocked_apps WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM blocked_apps WHERE profile_id = :profileId")
    suspend fun deleteByProfile(profileId: String)

    @Query("DELETE FROM blocked_apps WHERE profile_id = :profileId AND package_name = :packageName")
    suspend fun deleteByPackageName(profileId: String, packageName: String)
}
```

#### NfcTagDao

```kotlin
@Dao
interface NfcTagDao {
    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tag: NfcTagEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tags: List<NfcTagEntity>)

    // READ
    @Query("SELECT * FROM nfc_tags WHERE uid = :uid")
    suspend fun getByUid(uid: String): NfcTagEntity?

    @Query("SELECT * FROM nfc_tags WHERE uid = :uid")
    fun getByUidFlow(uid: String): Flow<NfcTagEntity?>

    @Query("SELECT * FROM nfc_tags ORDER BY created_at DESC")
    fun getAllFlow(): Flow<List<NfcTagEntity>>

    @Query("SELECT * FROM nfc_tags WHERE profile_id = :profileId")
    suspend fun getByProfile(profileId: String): List<NfcTagEntity>

    @Query("SELECT * FROM nfc_tags WHERE is_locked = 1 LIMIT 1")
    suspend fun getLockedTag(): NfcTagEntity?

    @Query("SELECT COUNT(*) FROM nfc_tags")
    suspend fun getCount(): Int

    @Query("""
        SELECT * FROM nfc_tags
        ORDER BY last_used_at DESC
        LIMIT :limit
    """)
    suspend fun getRecentlyUsed(limit: Int = 5): List<NfcTagEntity>

    // UPDATE
    @Update
    suspend fun update(tag: NfcTagEntity)

    @Query("""
        UPDATE nfc_tags
        SET last_used_at = :timestamp, usage_count = usage_count + 1
        WHERE uid = :uid
    """)
    suspend fun recordUsage(uid: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE nfc_tags SET is_locked = 0")
    suspend fun unlockAll()

    @Query("UPDATE nfc_tags SET is_locked = 1 WHERE uid = :uid")
    suspend fun lockTag(uid: String)

    @Transaction
    suspend fun setLockedTag(uid: String) {
        unlockAll()
        lockTag(uid)
    }

    // DELETE
    @Delete
    suspend fun delete(tag: NfcTagEntity)

    @Query("DELETE FROM nfc_tags WHERE uid = :uid")
    suspend fun deleteByUid(uid: String)

    @Query("DELETE FROM nfc_tags WHERE profile_id = :profileId")
    suspend fun deleteByProfile(profileId: String)
}
```

#### BlockingSessionDao

```kotlin
@Dao
interface BlockingSessionDao {
    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: BlockingSessionEntity): Long

    // READ
    @Query("SELECT * FROM blocking_sessions WHERE id = :id")
    suspend fun getById(id: String): BlockingSessionEntity?

    @Query("SELECT * FROM blocking_sessions WHERE ended_at IS NULL LIMIT 1")
    suspend fun getActiveSession(): BlockingSessionEntity?

    @Query("SELECT * FROM blocking_sessions WHERE ended_at IS NULL LIMIT 1")
    fun getActiveSessionFlow(): Flow<BlockingSessionEntity?>

    @Transaction
    @Query("SELECT * FROM blocking_sessions WHERE ended_at IS NULL LIMIT 1")
    suspend fun getActiveSessionWithProfile(): SessionWithProfile?

    @Query("SELECT * FROM blocking_sessions WHERE profile_id = :profileId ORDER BY started_at DESC")
    suspend fun getByProfile(profileId: String): List<BlockingSessionEntity>

    @Query("""
        SELECT * FROM blocking_sessions
        WHERE started_at >= :startDate AND started_at <= :endDate
        ORDER BY started_at DESC
    """)
    suspend fun getByDateRange(startDate: Long, endDate: Long): List<BlockingSessionEntity>

    @Query("""
        SELECT * FROM blocking_sessions
        WHERE ended_at IS NOT NULL
        ORDER BY started_at DESC
        LIMIT :limit
    """)
    suspend fun getRecentSessions(limit: Int = 20): List<BlockingSessionEntity>

    @Query("SELECT COUNT(*) FROM blocking_sessions WHERE ended_at IS NOT NULL")
    suspend fun getCompletedSessionsCount(): Int

    @Query("""
        SELECT SUM(ended_at - started_at)
        FROM blocking_sessions
        WHERE ended_at IS NOT NULL
    """)
    suspend fun getTotalBlockingTime(): Long?

    @Query("""
        SELECT SUM(ended_at - started_at)
        FROM blocking_sessions
        WHERE ended_at IS NOT NULL
        AND started_at >= :startDate
        AND started_at <= :endDate
    """)
    suspend fun getBlockingTimeInRange(startDate: Long, endDate: Long): Long?

    @Query("""
        SELECT AVG(ended_at - started_at)
        FROM blocking_sessions
        WHERE ended_at IS NOT NULL
    """)
    suspend fun getAverageSessionDuration(): Long?

    @Query("""
        SELECT trigger_method, COUNT(*) as count
        FROM blocking_sessions
        GROUP BY trigger_method
    """)
    suspend fun getSessionsByTriggerMethod(): Map<String, Int>

    // UPDATE
    @Update
    suspend fun update(session: BlockingSessionEntity)

    @Query("UPDATE blocking_sessions SET ended_at = :timestamp WHERE id = :id")
    suspend fun endSession(id: String, timestamp: Long = System.currentTimeMillis())

    @Query("""
        UPDATE blocking_sessions
        SET block_attempts = block_attempts + 1
        WHERE id = :id
    """)
    suspend fun incrementBlockAttempts(id: String)

    // DELETE
    @Delete
    suspend fun delete(session: BlockingSessionEntity)

    @Query("DELETE FROM blocking_sessions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM blocking_sessions WHERE started_at < :beforeDate")
    suspend fun deleteOlderThan(beforeDate: Long)
}
```

---

### 7.5 Domain Models (Business Logic)

Estas son las entidades de negocio, separadas de las entidades de BD.

```kotlin
// domain/model/BlockingProfile.kt
data class BlockingProfile(
    val id: String,
    val name: String,
    val description: String?,
    val icon: ProfileIcon,
    val color: Int,
    val isActive: Boolean,
    val isPredefined: Boolean,
    val autoUnlockMinutes: Int?,
    val requirePhysicalUnlock: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
)

// domain/model/BlockedApp.kt
data class BlockedApp(
    val id: String,
    val profileId: String,
    val packageName: String,
    val appName: String,
    val appIcon: Drawable?,
    val category: AppCategory?,
    val isWhitelisted: Boolean,
    val timesBlocked: Int,
    val addedAt: Instant
)

// domain/model/NfcTag.kt
data class NfcTag(
    val uid: String,
    val profileId: String?,
    val name: String,
    val description: String?,
    val location: String?,
    val tagType: NfcTagType,
    val isLocked: Boolean,
    val createdAt: Instant,
    val lastUsedAt: Instant?,
    val usageCount: Int
)

// domain/model/BlockingSession.kt
data class BlockingSession(
    val id: String,
    val profileId: String,
    val startedAt: Instant,
    val endedAt: Instant?,
    val triggerMethod: TriggerMethod,
    val tagUid: String?,
    val appsBlockedCount: Int,
    val blockAttempts: Int,
    val notes: String?
) {
    val isActive: Boolean
        get() = endedAt == null

    val duration: Duration?
        get() = endedAt?.let { Duration.between(startedAt, it) }
}

// domain/model/AppInfo.kt
data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val category: AppCategory,
    val isSystemApp: Boolean,
    val isInstalled: Boolean
)

// domain/model/Statistics.kt
data class Statistics(
    val totalSessions: Int,
    val totalBlockingTime: Duration,
    val averageSessionDuration: Duration,
    val currentStreak: Int,
    val longestStreak: Int,
    val mostBlockedApps: List<Pair<String, Int>>, // (packageName, count)
    val sessionsByTrigger: Map<TriggerMethod, Int>,
    val weeklyStats: List<DailyStats>
)

data class DailyStats(
    val date: LocalDate,
    val sessionsCount: Int,
    val blockingTime: Duration,
    val blockAttempts: Int
)
```

---

### Enums

```kotlin
// domain/model/enums/TriggerMethod.kt
enum class TriggerMethod {
    NFC,
    QR,
    MANUAL,
    TIMER,
    SCHEDULED
}

// domain/model/enums/BlockingStatus.kt
enum class BlockingStatus {
    INACTIVE,
    ACTIVE,
    TIMER_ACTIVE
}

// domain/model/enums/ProfileIcon.kt
enum class ProfileIcon(val iconName: String) {
    SOCIAL_MEDIA("social_media"),
    GAMES("games"),
    NEWS("news"),
    SHOPPING("shopping"),
    VIDEO("video"),
    MUSIC("music"),
    CUSTOM("custom")
}

// domain/model/enums/AppCategory.kt
enum class AppCategory {
    SOCIAL,
    GAMES,
    NEWS,
    SHOPPING,
    VIDEO,
    MUSIC,
    PRODUCTIVITY,
    COMMUNICATION,
    FINANCE,
    SYSTEM,
    OTHER
}

// domain/model/enums/NfcTagType.kt
enum class NfcTagType {
    NTAG213,
    NTAG215,
    NTAG216,
    UNKNOWN
}

// domain/model/enums/DarkMode.kt
enum class DarkMode {
    LIGHT,
    DARK,
    SYSTEM
}
```

---

### 7.6 Use Cases (Business Logic)

Los Use Cases encapsulan la lógica de negocio y orquestan las llamadas a repositories.

#### Profile Use Cases

```kotlin
// domain/usecase/profile/CreateProfileUseCase.kt
class CreateProfileUseCase @Inject constructor(
    private val profileRepository: BlockingProfileRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String?,
        icon: ProfileIcon,
        color: Int
    ): Result<BlockingProfile> {
        return try {
            // Validations
            if (name.isBlank()) {
                return Result.failure(ValidationException("El nombre no puede estar vacío"))
            }

            if (name.length > 50) {
                return Result.failure(ValidationException("El nombre es demasiado largo"))
            }

            val profile = BlockingProfile(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                icon = icon,
                color = color,
                isActive = false,
                isPredefined = false,
                autoUnlockMinutes = null,
                requirePhysicalUnlock = false,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )

            profileRepository.createProfile(profile)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// domain/usecase/profile/GetActiveProfileUseCase.kt
class GetActiveProfileUseCase @Inject constructor(
    private val profileRepository: BlockingProfileRepository
) {
    operator fun invoke(): Flow<BlockingProfile?> {
        return profileRepository.getActiveProfile()
    }
}

// domain/usecase/profile/ActivateProfileUseCase.kt
class ActivateProfileUseCase @Inject constructor(
    private val profileRepository: BlockingProfileRepository,
    private val blockingService: AppBlockingManager
) {
    suspend operator fun invoke(profileId: String): Result<Unit> {
        return try {
            // Deactivate all profiles
            profileRepository.deactivateAllProfiles()

            // Activate the selected profile
            profileRepository.activateProfile(profileId)

            // Start blocking service
            blockingService.startBlocking(profileId)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// domain/usecase/profile/DeleteProfileUseCase.kt
class DeleteProfileUseCase @Inject constructor(
    private val profileRepository: BlockingProfileRepository
) {
    suspend operator fun invoke(profileId: String): Result<Unit> {
        return try {
            val profile = profileRepository.getProfileById(profileId)
                ?: return Result.failure(NotFoundException("Perfil no encontrado"))

            if (profile.isPredefined) {
                return Result.failure(ValidationException("No se pueden borrar perfiles del sistema"))
            }

            if (profile.isActive) {
                return Result.failure(ValidationException("No se puede borrar un perfil activo"))
            }

            profileRepository.deleteProfile(profileId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

#### Blocking Use Cases

```kotlin
// domain/usecase/blocking/StartBlockingUseCase.kt
class StartBlockingUseCase @Inject constructor(
    private val sessionRepository: BlockingSessionRepository,
    private val profileRepository: BlockingProfileRepository,
    private val blockingService: AppBlockingManager,
    private val notificationHelper: NotificationHelper
) {
    suspend operator fun invoke(
        profileId: String,
        triggerMethod: TriggerMethod,
        tagUid: String? = null
    ): Result<BlockingSession> {
        return try {
            // Check if there's already an active session
            val activeSession = sessionRepository.getActiveSession()
            if (activeSession != null) {
                return Result.failure(IllegalStateException("Ya hay una sesión activa"))
            }

            // Get profile
            val profile = profileRepository.getProfileById(profileId)
                ?: return Result.failure(NotFoundException("Perfil no encontrado"))

            // Get blocked apps count
            val blockedApps = profileRepository.getBlockedApps(profileId)

            // Create session
            val session = BlockingSession(
                id = UUID.randomUUID().toString(),
                profileId = profileId,
                startedAt = Instant.now(),
                endedAt = null,
                triggerMethod = triggerMethod,
                tagUid = tagUid,
                appsBlockedCount = blockedApps.size,
                blockAttempts = 0,
                notes = null
            )

            sessionRepository.createSession(session)

            // Activate profile
            profileRepository.activateProfile(profileId)

            // Start blocking
            blockingService.startBlocking(profileId)

            // Show notification
            notificationHelper.showBlockingActiveNotification(profile.name)

            // Schedule auto-unlock if configured
            profile.autoUnlockMinutes?.let { minutes ->
                blockingService.scheduleAutoUnlock(minutes)
            }

            Result.success(session)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// domain/usecase/blocking/StopBlockingUseCase.kt
class StopBlockingUseCase @Inject constructor(
    private val sessionRepository: BlockingSessionRepository,
    private val profileRepository: BlockingProfileRepository,
    private val blockingService: AppBlockingManager,
    private val notificationHelper: NotificationHelper
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            // Get active session
            val session = sessionRepository.getActiveSession()
                ?: return Result.failure(IllegalStateException("No hay sesión activa"))

            // End session
            sessionRepository.endSession(session.id)

            // Deactivate profile
            profileRepository.deactivateAllProfiles()

            // Stop blocking
            blockingService.stopBlocking()

            // Dismiss notification
            notificationHelper.dismissBlockingNotification()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// domain/usecase/blocking/IsAppBlockedUseCase.kt
class IsAppBlockedUseCase @Inject constructor(
    private val profileRepository: BlockingProfileRepository,
    private val sessionRepository: BlockingSessionRepository
) {
    suspend operator fun invoke(packageName: String): Boolean {
        val activeSession = sessionRepository.getActiveSession() ?: return false
        val blockedApps = profileRepository.getBlockedApps(activeSession.profileId)

        return blockedApps.any {
            it.packageName == packageName && !it.isWhitelisted
        }
    }
}

// domain/usecase/blocking/RecordBlockAttemptUseCase.kt
class RecordBlockAttemptUseCase @Inject constructor(
    private val sessionRepository: BlockingSessionRepository,
    private val appRepository: BlockedAppRepository
) {
    suspend operator fun invoke(packageName: String): Result<Unit> {
        return try {
            val session = sessionRepository.getActiveSession()
                ?: return Result.failure(IllegalStateException("No hay sesión activa"))

            // Increment session block attempts
            sessionRepository.incrementBlockAttempts(session.id)

            // Increment app times blocked
            appRepository.incrementTimesBlocked(session.profileId, packageName)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

#### NFC Use Cases

```kotlin
// domain/usecase/nfc/HandleNfcTagUseCase.kt
class HandleNfcTagUseCase @Inject constructor(
    private val tagRepository: NfcTagRepository,
    private val sessionRepository: BlockingSessionRepository,
    private val startBlockingUseCase: StartBlockingUseCase,
    private val stopBlockingUseCase: StopBlockingUseCase
) {
    suspend operator fun invoke(tagUid: String): Result<NfcTagAction> {
        return try {
            // Get tag from DB
            val tag = tagRepository.getTagByUid(tagUid)

            if (tag == null) {
                // Unknown tag - prompt user to configure
                return Result.success(NfcTagAction.CONFIGURE_NEW_TAG)
            }

            // Record tag usage
            tagRepository.recordUsage(tagUid)

            // Check if there's an active session
            val activeSession = sessionRepository.getActiveSession()

            if (activeSession != null) {
                // Session active - stop blocking
                stopBlockingUseCase()
                Result.success(NfcTagAction.STOPPED_BLOCKING)
            } else {
                // No session - start blocking
                val profileId = tag.profileId
                    ?: return Result.success(NfcTagAction.SELECT_PROFILE)

                startBlockingUseCase(
                    profileId = profileId,
                    triggerMethod = TriggerMethod.NFC,
                    tagUid = tagUid
                )
                Result.success(NfcTagAction.STARTED_BLOCKING)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

enum class NfcTagAction {
    CONFIGURE_NEW_TAG,
    SELECT_PROFILE,
    STARTED_BLOCKING,
    STOPPED_BLOCKING
}

// domain/usecase/nfc/WriteNfcTagUseCase.kt
class WriteNfcTagUseCase @Inject constructor(
    private val nfcManager: NfcManager,
    private val tagRepository: NfcTagRepository
) {
    suspend operator fun invoke(
        tag: Tag,
        name: String,
        profileId: String?
    ): Result<NfcTag> {
        return try {
            // Write NDEF message to tag
            val uid = nfcManager.writeNdefMessage(
                tag = tag,
                text = "umbral://activate" // URL scheme
            )

            // Save to DB
            val nfcTag = NfcTag(
                uid = uid,
                profileId = profileId,
                name = name,
                description = null,
                location = null,
                tagType = nfcManager.getTagType(tag),
                isLocked = false,
                createdAt = Instant.now(),
                lastUsedAt = null,
                usageCount = 0
            )

            tagRepository.createTag(nfcTag)
            Result.success(nfcTag)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

#### Statistics Use Cases

```kotlin
// domain/usecase/statistics/GetUsageStatsUseCase.kt
class GetUsageStatsUseCase @Inject constructor(
    private val sessionRepository: BlockingSessionRepository,
    private val settingsRepository: PreferencesRepository
) {
    suspend operator fun invoke(): Result<Statistics> {
        return try {
            val sessions = sessionRepository.getAllSessions()
            val completedSessions = sessions.filter { it.endedAt != null }

            val totalBlockingTime = completedSessions.sumOf {
                it.duration?.toMillis() ?: 0
            }

            val avgDuration = if (completedSessions.isNotEmpty()) {
                totalBlockingTime / completedSessions.size
            } else 0

            val currentStreak = calculateCurrentStreak(sessions)
            val longestStreak = calculateLongestStreak(sessions)

            val stats = Statistics(
                totalSessions = completedSessions.size,
                totalBlockingTime = Duration.ofMillis(totalBlockingTime),
                averageSessionDuration = Duration.ofMillis(avgDuration),
                currentStreak = currentStreak,
                longestStreak = longestStreak,
                mostBlockedApps = emptyList(), // TODO: Implement
                sessionsByTrigger = sessions.groupBy { it.triggerMethod }
                    .mapValues { it.value.size },
                weeklyStats = calculateWeeklyStats(sessions)
            )

            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateCurrentStreak(sessions: List<BlockingSession>): Int {
        // Logic to calculate consecutive days with sessions
        // TODO: Implement
        return 0
    }

    private fun calculateLongestStreak(sessions: List<BlockingSession>): Int {
        // Logic to calculate longest streak
        // TODO: Implement
        return 0
    }

    private fun calculateWeeklyStats(sessions: List<BlockingSession>): List<DailyStats> {
        // Group sessions by day and calculate stats
        // TODO: Implement
        return emptyList()
    }
}
```

---

### 7.7 ViewModels (MVVM Pattern)

Los ViewModels manejan el estado de UI y orquestan los Use Cases.

#### HomeViewModel

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getActiveProfileUseCase: GetActiveProfileUseCase,
    private val getActiveSessionUseCase: GetActiveSessionUseCase,
    private val startBlockingUseCase: StartBlockingUseCase,
    private val stopBlockingUseCase: StopBlockingUseCase,
    private val handleNfcTagUseCase: HandleNfcTagUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeActiveProfile()
        observeActiveSession()
    }

    private fun observeActiveProfile() {
        viewModelScope.launch {
            getActiveProfileUseCase()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { profile ->
                    _uiState.update { it.copy(activeProfile = profile) }
                }
        }
    }

    private fun observeActiveSession() {
        viewModelScope.launch {
            getActiveSessionUseCase()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { session ->
                    _uiState.update {
                        it.copy(
                            isBlocking = session != null,
                            activeSession = session
                        )
                    }
                }
        }
    }

    fun toggleBlocking() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = if (uiState.value.isBlocking) {
                stopBlockingUseCase()
            } else {
                val profileId = uiState.value.activeProfile?.id
                    ?: return@launch _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "No hay perfil activo"
                        )
                    }

                startBlockingUseCase(
                    profileId = profileId,
                    triggerMethod = TriggerMethod.MANUAL
                )
            }

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, error = null) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
            )
        }
    }

    fun handleNfcTag(tagUid: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            handleNfcTagUseCase(tagUid).fold(
                onSuccess = { action ->
                    when (action) {
                        NfcTagAction.STARTED_BLOCKING,
                        NfcTagAction.STOPPED_BLOCKING -> {
                            _uiState.update { it.copy(isLoading = false) }
                        }
                        NfcTagAction.CONFIGURE_NEW_TAG -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    showConfigureTagDialog = true,
                                    pendingTagUid = tagUid
                                )
                            }
                        }
                        NfcTagAction.SELECT_PROFILE -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    showSelectProfileDialog = true,
                                    pendingTagUid = tagUid
                                )
                            }
                        }
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class HomeUiState(
    val activeProfile: BlockingProfile? = null,
    val activeSession: BlockingSession? = null,
    val isBlocking: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showConfigureTagDialog: Boolean = false,
    val showSelectProfileDialog: Boolean = false,
    val pendingTagUid: String? = null
)
```

#### ProfileViewModel

```kotlin
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfilesUseCase: GetProfilesUseCase,
    private val createProfileUseCase: CreateProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val deleteProfileUseCase: DeleteProfileUseCase,
    private val activateProfileUseCase: ActivateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfiles()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            getProfilesUseCase()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { profiles ->
                    _uiState.update { it.copy(profiles = profiles) }
                }
        }
    }

    fun createProfile(
        name: String,
        description: String?,
        icon: ProfileIcon,
        color: Int
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            createProfileUseCase(name, description, icon, color).fold(
                onSuccess = { profile ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            showCreateDialog = false,
                            error = null
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
            )
        }
    }

    fun deleteProfile(profileId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            deleteProfileUseCase(profileId).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, error = null) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
            )
        }
    }

    fun activateProfile(profileId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            activateProfileUseCase(profileId).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, error = null) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message)
                    }
                }
            )
        }
    }

    fun showCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = true) }
    }

    fun hideCreateDialog() {
        _uiState.update { it.copy(showCreateDialog = false) }
    }
}

data class ProfileUiState(
    val profiles: List<BlockingProfile> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCreateDialog: Boolean = false
)
```

---

**NOTA:** La arquitectura detallada continúa en [architecture-part2.md](architecture-part2.md) con:
- 7.8 Dependency Injection (Hilt Modules)
- 7.9 Navigation (Jetpack Compose)
- 7.10 Background Services
- 7.11 Permission Handling
- 7.12 Error Handling Strategy
- 7.13 State Management
- 7.14 Mappers (Entity ↔ Domain)
- 7.15 Testing Strategy
- 7.16 Notification Channels
- 7.17 Constants

---

## 8. Decisiones de UX/UI

### 8.1 Design System

**Material Design 3** (Material You)

**Justificación:**
- ✅ Estándar de Android moderno
- ✅ Dynamic color theming (adapta a wallpaper del usuario)
- ✅ Componentes Compose Material3 ready
- ✅ Accesibilidad built-in

**Color Scheme:**
- Primary: Derivado de Material You (user's wallpaper)
- Semantic colors: Error (red), Success (green), Warning (amber)

**Typography:**
- System default (Roboto en mayoría de devices)
- Escalas Material Design 3

### 8.2 Idioma Principal

**Español** - UI/UX en español mexicano

**Justificación:**
- ✅ Mercado objetivo hispanohablante
- ✅ Diferenciador vs competencia (Foqos/Brick en inglés)
- ✅ Strings resources para i18n futuro (inglés en v2)

**Código:** Todo en inglés (variables, funciones, comentarios)

### 8.3 Onboarding Flow

**Crítico:** AccessibilityService/UsageStats permissions son intimidantes

**Estrategia:**
1. Bienvenida visual (concepto del "umbral")
2. Explicar "por qué" antes de pedir permisos
3. Paso a paso con video/animaciones
4. Test NFC tag al final (satisfacción inmediata)

---

## 9. Consideraciones de Google Play

### 9.1 Policy Compliance

**Accessibility Service:**
- ⚠️ Requiere Permission Declaration Form
- ⚠️ Video demo del uso correcto obligatorio
- ⚠️ Justificación detallada
- ✅ Estrategia: Usar solo si UsageStatsManager no es suficiente

**Device Admin (Physical Unlock):**
- ⚠️ Scrutinio extra de Google Play
- ✅ Estrategia: Feature opcional, disclaimer claro, easy opt-out

### 9.2 App Metadata

**Target Audience:** 18+ (por usar app blocking)
**Content Rating:** Everyone
**Category:** Productivity
**Tags:** Digital Wellbeing, Focus, NFC, App Blocker

---

## 10. Roadmap Post-V1

### 10.1 V1.1 - Refinement (2 semanas)
- Bug fixes basados en feedback
- UI polish
- Performance optimizations
- Localización inglés

### 10.2 V2.0 - Cloud Features (4-6 semanas)
- Supabase backend
- Cloud sync de perfiles
- Multi-device support
- Premium tier (cloud storage)

### 10.3 V3.0 - Advanced (6-8 semanas)
- Website blocking (VPN approach)
- Location-based triggers (no solo NFC)
- Scheduled blocking (work hours, bedtime)
- Social features (accountability partner)

---

## 11. Métricas de Éxito

### 11.1 Desarrollo

- [ ] 100% módulos definidos antes de codificar
- [ ] 0 dependencias circulares
- [ ] Documentación > 8,000 líneas antes de código
- [ ] Progreso diario documentado

### 11.2 Post-Lanzamiento

**Performance:**
- < 50ms latency en detección de NFC
- < 100ms en activar bloqueo de apps
- < 5% battery drain diario

**Engagement:**
- 70%+ weekly active users (de instalados)
- 30%+ daily NFC tag usage
- 4.0+ star rating en Play Store

**Growth:**
- 1,000 installs en primer mes
- 10% organic MoM growth
- 20%+ conversion rate de open source contribution

---

## 12. Próximos Pasos

### 12.1 Fase Pre-Desarrollo (Ahora - 2 semanas)

1. [x] `/oden:architect` - Arquitectura detallada y schema completo ✅ **COMPLETADO**
2. [ ] `/oden:analyze` - Análisis competitivo profundo (Foqos, Brick, Unpluq) ← **SIGUIENTE PASO**
3. [ ] `/oden:spec [módulo]` - Specs de 800-1200 líneas por módulo:
   - `nfc-module` - NFC reading/writing
   - `app-blocking-module` - UsageStatsManager integration
   - `profiles-module` - CRUD de perfiles
   - `ui-module` - Jetpack Compose screens
4. [ ] `/oden:plan` - Plan semana por semana (12-16 semanas)
5. [ ] `/oden:checklist` - Verificar antes de codificar

### 12.2 Fase Desarrollo (Semanas 3-16)

- Implementación siguiendo specs al pie de la letra
- Daily logging con `/oden:daily`
- Validación contra specs cada milestone

### 12.3 Fase Post-Desarrollo

- Mover docs a `completed/`
- Crear guías de usuario
- Preparar Play Store assets
- Open source launch (GitHub release)

---

**Creado:** 2026-01-03T01:53:14Z
**Última actualización:** 2026-01-03T01:53:14Z
**Generado por:** Oden Forge Wizard v1.0
**Metodología:** Documentation-First Development

---

## Referencias

- [Foqos GitHub](https://github.com/awaseem/foqos) - Inspiración iOS
- [Android NFC Guide](https://developer.android.com/develop/connectivity/nfc/nfc)
- [UsageStatsManager API](https://developer.android.com/reference/android/app/usage/UsageStatsManager)
- [Jetpack Compose Best Practices](https://developer.android.com/jetpack/compose/best-practices)
- [Material Design 3](https://m3.material.io/)
