# Especificación: Módulo App Blocking

**Estado:** ✅ Completo
**Última actualización:** 2026-01-03
**Versión:** 1.0

---

## 1. Overview

### 1.1 Propósito

El módulo de App Blocking es responsable de **detectar y bloquear** el acceso a aplicaciones configuradas por el usuario. Utiliza la API `UsageStatsManager` de Android para monitorear qué app está en foreground y muestra un overlay cuando el usuario intenta abrir una app bloqueada.

### 1.2 Responsabilidades

| Responsabilidad | Descripción |
|-----------------|-------------|
| **Monitoreo de apps** | Detectar cuándo una app bloqueada pasa a foreground |
| **Mostrar overlay** | Bloquear visualmente el acceso a la app |
| **Gestión de estado** | Mantener estado de bloqueo (activo/inactivo) |
| **Whitelist** | Permitir siempre apps esenciales |
| **Timers** | Auto-desbloqueo después de tiempo configurado |
| **Estadísticas** | Registrar intentos de apertura bloqueados |

### 1.3 Estrategia de Implementación

**Nivel 1 (Preferido): UsageStatsManager**
- API oficial de Google
- Menor fricción en Google Play
- Requiere permiso PACKAGE_USAGE_STATS

**Nivel 2 (Fallback): AccessibilityService**
- Solo si UsageStatsManager no es suficiente
- Requiere Permission Declaration Form
- Mayor escrutinio de Google Play

Para MVP, implementaremos **solo Nivel 1**.

---

## 2. Arquitectura

### 2.1 Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────────┐
│                      Blocking Module                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌────────────────┐    ┌─────────────────┐    ┌──────────────┐ │
│  │ BlockingManager │───▶│ AppMonitorService│───▶│ OverlayManager│ │
│  └────────────────┘    └─────────────────┘    └──────────────┘ │
│          │                     │                      │         │
│          ▼                     ▼                      ▼         │
│  ┌────────────────┐    ┌─────────────────┐    ┌──────────────┐ │
│  │ BlockingState  │    │UsageStatsHelper │    │ BlockOverlay │ │
│  └────────────────┘    └─────────────────┘    │  (Composable)│ │
│          │                                     └──────────────┘ │
│          ▼                                                       │
│  ┌────────────────┐    ┌─────────────────┐    ┌──────────────┐ │
│  │BlockingRepository│   │  TimerManager   │    │NotificationMgr│ │
│  └────────────────┘    └─────────────────┘    └──────────────┘ │
│          │                     │                      │         │
│          ▼                     ▼                      ▼         │
│  ┌──────────────────────────────────────────────────────────┐  │
│  │                      Room Database                         │  │
│  │     (BlockingSessionEntity, BlockedAppAttemptEntity)       │  │
│  └──────────────────────────────────────────────────────────┘  │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
        ┌──────────┐   ┌──────────┐   ┌──────────┐
        │NfcModule │   │Profiles  │   │ Settings │
        │ (toggle) │   │ (apps)   │   │(config)  │
        └──────────┘   └──────────┘   └──────────┘
```

### 2.2 Componentes

| Componente | Tipo | Responsabilidad |
|------------|------|-----------------|
| `BlockingManager` | Singleton | API pública, coordina bloqueo |
| `AppMonitorService` | Foreground Service | Monitorea app en foreground |
| `OverlayManager` | Singleton | Muestra/oculta overlay de bloqueo |
| `BlockingState` | StateHolder | Estado reactivo del bloqueo |
| `UsageStatsHelper` | Helper | Wrapper para UsageStatsManager |
| `TimerManager` | Manager | Gestiona timers de auto-unlock |
| `BlockingRepository` | Repository | Persistencia de sesiones y stats |

### 2.3 Dependencias

```kotlin
// build.gradle.kts (app)
dependencies {
    // Work Manager para polling confiable
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Lifecycle para service
    implementation("androidx.lifecycle:lifecycle-service:2.7.0")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // DataStore para estado persistente
    implementation("androidx.datastore:datastore-preferences:1.0.0")
}
```

---

## 3. Modelos de Datos

### 3.1 BlockingState (Domain Model)

```kotlin
/**
 * Estado actual del sistema de bloqueo.
 */
data class BlockingState(
    val isActive: Boolean = false,
    val activeProfileId: String? = null,
    val activeProfileName: String? = null,
    val blockedApps: Set<String> = emptySet(), // package names
    val whitelistedApps: Set<String> = emptySet(),
    val startedAt: Instant? = null,
    val timerEndAt: Instant? = null, // null = sin timer
    val sessionId: String? = null
) {
    val hasTimer: Boolean get() = timerEndAt != null

    val remainingTime: Duration?
        get() = timerEndAt?.let { Duration.between(Instant.now(), it) }

    val isTimerExpired: Boolean
        get() = timerEndAt?.isBefore(Instant.now()) ?: false
}
```

### 3.2 BlockingSession (Domain Model)

```kotlin
/**
 * Representa una sesión de bloqueo completa.
 */
data class BlockingSession(
    val id: String = UUID.randomUUID().toString(),
    val profileId: String,
    val profileName: String,
    val startedAt: Instant,
    val endedAt: Instant? = null,
    val triggerType: TriggerType,
    val triggerSource: String? = null, // tagId o "manual"
    val blockedApps: List<String>,
    val attemptCount: Int = 0,
    val timerDuration: Duration? = null
)

enum class TriggerType {
    NFC_TAG,
    QR_CODE,
    MANUAL,
    SCHEDULED // futuro
}
```

### 3.3 BlockedAppAttempt (Domain Model)

```kotlin
/**
 * Registro de intento de abrir app bloqueada.
 */
data class BlockedAppAttempt(
    val id: String = UUID.randomUUID().toString(),
    val sessionId: String,
    val packageName: String,
    val appName: String,
    val attemptedAt: Instant,
    val wasOverridden: Boolean = false // si usó emergency unlock
)
```

### 3.4 Room Entities

```kotlin
@Entity(tableName = "blocking_sessions")
data class BlockingSessionEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "profile_id")
    val profileId: String,

    @ColumnInfo(name = "profile_name")
    val profileName: String,

    @ColumnInfo(name = "started_at")
    val startedAt: Long,

    @ColumnInfo(name = "ended_at")
    val endedAt: Long?,

    @ColumnInfo(name = "trigger_type")
    val triggerType: String,

    @ColumnInfo(name = "trigger_source")
    val triggerSource: String?,

    @ColumnInfo(name = "blocked_apps")
    val blockedApps: String, // JSON array

    @ColumnInfo(name = "attempt_count")
    val attemptCount: Int,

    @ColumnInfo(name = "timer_duration_seconds")
    val timerDurationSeconds: Long?
)

@Entity(
    tableName = "blocked_app_attempts",
    foreignKeys = [
        ForeignKey(
            entity = BlockingSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("session_id"), Index("package_name")]
)
data class BlockedAppAttemptEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "session_id")
    val sessionId: String,

    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "app_name")
    val appName: String,

    @ColumnInfo(name = "attempted_at")
    val attemptedAt: Long,

    @ColumnInfo(name = "was_overridden")
    val wasOverridden: Boolean
)
```

### 3.5 BlockingConfig (Settings)

```kotlin
/**
 * Configuración del sistema de bloqueo.
 */
data class BlockingConfig(
    val pollingIntervalMs: Long = 500L,
    val overlayDelayMs: Long = 100L,
    val showNotification: Boolean = true,
    val vibrateOnBlock: Boolean = true,
    val soundOnBlock: Boolean = false,
    val showMotivationalMessages: Boolean = true,
    val strictMode: Boolean = false // previene desinstalar app
)
```

---

## 4. Interfaces Públicas

### 4.1 BlockingManager Interface

```kotlin
/**
 * API pública para control del sistema de bloqueo.
 * Inyectado vía Hilt como Singleton.
 */
interface BlockingManager {

    /**
     * Estado actual del bloqueo como Flow reactivo.
     */
    val state: StateFlow<BlockingState>

    /**
     * Indica si el bloqueo está activo.
     */
    val isBlocking: Boolean

    /**
     * Activa el bloqueo con un perfil específico.
     *
     * @param profileId ID del perfil a usar
     * @param trigger Tipo de activación (NFC, QR, Manual)
     * @param triggerSource Fuente específica (tagId, etc)
     * @param timerMinutes Minutos para auto-unlock (null = sin timer)
     */
    suspend fun startBlocking(
        profileId: String,
        trigger: TriggerType,
        triggerSource: String? = null,
        timerMinutes: Int? = null
    ): Result<BlockingSession>

    /**
     * Desactiva el bloqueo actual.
     *
     * @param trigger Tipo de desactivación
     */
    suspend fun stopBlocking(trigger: TriggerType): Result<Unit>

    /**
     * Toggle: si está activo lo desactiva, si no lo activa.
     *
     * @param profileId Perfil a usar si se activa
     * @param trigger Tipo de trigger
     * @param triggerSource Fuente específica
     */
    suspend fun toggleBlocking(
        profileId: String?,
        trigger: TriggerType,
        triggerSource: String? = null
    ): Result<BlockingState>

    /**
     * Verifica si una app específica está bloqueada.
     */
    fun isAppBlocked(packageName: String): Boolean

    /**
     * Obtiene la sesión actual si existe.
     */
    fun getCurrentSession(): BlockingSession?

    /**
     * Extiende el timer actual.
     */
    suspend fun extendTimer(additionalMinutes: Int): Result<Unit>

    /**
     * Desbloquea temporalmente (emergency unlock).
     * Solo disponible si hay emergency unlocks restantes.
     */
    suspend fun emergencyUnlock(): Result<Unit>

    /**
     * Obtiene emergency unlocks restantes.
     */
    fun getRemainingEmergencyUnlocks(): Int
}
```

### 4.2 AppMonitorService

```kotlin
/**
 * Foreground Service que monitorea apps en foreground.
 */
@AndroidEntryPoint
class AppMonitorService : LifecycleService() {

    companion object {
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "umbral_blocking"

        fun start(context: Context) {
            val intent = Intent(context, AppMonitorService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, AppMonitorService::class.java)
            context.stopService(intent)
        }
    }

    @Inject
    lateinit var usageStatsHelper: UsageStatsHelper

    @Inject
    lateinit var overlayManager: OverlayManager

    @Inject
    lateinit var blockingManager: BlockingManager

    private val pollingJob = Job()
    private val scope = CoroutineScope(Dispatchers.Default + pollingJob)

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())
        startMonitoring()
    }

    override fun onDestroy() {
        super.onDestroy()
        pollingJob.cancel()
    }

    private fun startMonitoring() {
        scope.launch {
            while (isActive) {
                val foregroundApp = usageStatsHelper.getForegroundApp()

                if (foregroundApp != null && blockingManager.isAppBlocked(foregroundApp)) {
                    overlayManager.showOverlay(foregroundApp)
                } else {
                    overlayManager.hideOverlay()
                }

                delay(blockingManager.config.pollingIntervalMs)
            }
        }
    }

    private fun createNotification(): Notification {
        // Crear notification channel y notification
    }
}
```

### 4.3 OverlayManager Interface

```kotlin
/**
 * Gestiona la visualización del overlay de bloqueo.
 */
interface OverlayManager {

    /**
     * Estado del overlay.
     */
    val overlayState: StateFlow<OverlayState>

    /**
     * Muestra el overlay bloqueando una app.
     */
    fun showOverlay(blockedPackage: String)

    /**
     * Oculta el overlay.
     */
    fun hideOverlay()

    /**
     * Verifica si tiene permiso de overlay.
     */
    fun hasOverlayPermission(): Boolean

    /**
     * Abre settings para otorgar permiso.
     */
    fun requestOverlayPermission(activity: Activity)
}

sealed class OverlayState {
    object Hidden : OverlayState()
    data class Showing(
        val blockedPackage: String,
        val blockedAppName: String,
        val profileName: String,
        val remainingTime: Duration?,
        val motivationalMessage: String
    ) : OverlayState()
}
```

### 4.4 UsageStatsHelper

```kotlin
/**
 * Wrapper para UsageStatsManager.
 */
class UsageStatsHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val usageStatsManager = context.getSystemService(
        Context.USAGE_STATS_SERVICE
    ) as UsageStatsManager

    /**
     * Verifica si tiene permiso de usage stats.
     */
    fun hasPermission(): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /**
     * Abre settings para otorgar permiso.
     */
    fun requestPermission(activity: Activity) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        activity.startActivity(intent)
    }

    /**
     * Obtiene la app actualmente en foreground.
     *
     * @return Package name de la app en foreground, o null
     */
    fun getForegroundApp(): String? {
        val endTime = System.currentTimeMillis()
        val startTime = endTime - 10_000 // últimos 10 segundos

        val usageEvents = usageStatsManager.queryEvents(startTime, endTime)
        var lastForegroundApp: String? = null
        var lastForegroundTime = 0L

        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                if (event.timeStamp > lastForegroundTime) {
                    lastForegroundTime = event.timeStamp
                    lastForegroundApp = event.packageName
                }
            }
        }

        return lastForegroundApp
    }

    /**
     * Obtiene estadísticas de uso por día.
     */
    fun getDailyUsageStats(daysBack: Int = 7): Map<LocalDate, List<AppUsageStat>> {
        // Implementación para estadísticas
    }
}

data class AppUsageStat(
    val packageName: String,
    val appName: String,
    val totalTimeMs: Long,
    val launchCount: Int
)
```

### 4.5 TimerManager

```kotlin
/**
 * Gestiona timers para auto-unlock.
 */
interface TimerManager {

    /**
     * Estado del timer actual.
     */
    val timerState: StateFlow<TimerState>

    /**
     * Inicia un timer.
     */
    fun startTimer(durationMinutes: Int, onComplete: () -> Unit)

    /**
     * Cancela el timer actual.
     */
    fun cancelTimer()

    /**
     * Extiende el timer actual.
     */
    fun extendTimer(additionalMinutes: Int)

    /**
     * Pausa el timer (para breaks).
     */
    fun pauseTimer()

    /**
     * Reanuda el timer pausado.
     */
    fun resumeTimer()
}

sealed class TimerState {
    object Inactive : TimerState()
    data class Running(
        val startedAt: Instant,
        val endsAt: Instant,
        val remaining: Duration
    ) : TimerState()
    data class Paused(
        val remaining: Duration
    ) : TimerState()
    object Completed : TimerState()
}
```

---

## 5. Máquina de Estados

### 5.1 Blocking State Machine

```
                              ┌─────────────────┐
                              │                 │
                              ▼                 │
┌──────────┐   startBlocking  ┌──────────┐      │
│          │─────────────────▶│          │      │
│   IDLE   │                  │ BLOCKING │──────┘
│          │◀─────────────────│          │  timer expired /
└──────────┘   stopBlocking   └──────────┘  manual stop
     │                              │
     │                              │ app opened
     │                              ▼
     │                        ┌──────────┐
     │                        │          │
     │                        │ OVERLAY  │
     │                        │ SHOWING  │
     │                        └──────────┘
     │                              │
     │                              │ user navigates away
     │                              ▼
     │                        ┌──────────┐
     │                        │          │
     │                        │ BLOCKING │ (back to monitoring)
     │                        │          │
     │                        └──────────┘
     │                              │
     └──────────────────────────────┘
```

### 5.2 State Definitions

```kotlin
sealed class BlockingSessionState {
    /**
     * No hay sesión de bloqueo activa.
     */
    object Idle : BlockingSessionState()

    /**
     * Bloqueo activo, monitoreando apps.
     */
    data class Blocking(
        val session: BlockingSession,
        val timerState: TimerState
    ) : BlockingSessionState()

    /**
     * Overlay visible, bloqueando app específica.
     */
    data class OverlayShowing(
        val session: BlockingSession,
        val blockedPackage: String,
        val timerState: TimerState
    ) : BlockingSessionState()

    /**
     * Pausado temporalmente (break).
     */
    data class Paused(
        val session: BlockingSession,
        val pausedAt: Instant,
        val remainingTime: Duration?
    ) : BlockingSessionState()

    /**
     * Sesión completada (por timer o manual).
     */
    data class Completed(
        val session: BlockingSession
    ) : BlockingSessionState()
}
```

### 5.3 Actions and Transitions

```kotlin
sealed class BlockingAction {
    data class StartBlocking(
        val profileId: String,
        val trigger: TriggerType,
        val timerMinutes: Int?
    ) : BlockingAction()

    data class StopBlocking(
        val trigger: TriggerType
    ) : BlockingAction()

    data class AppOpened(
        val packageName: String
    ) : BlockingAction()

    object AppClosed : BlockingAction()

    object TimerExpired : BlockingAction()

    data class ExtendTimer(
        val minutes: Int
    ) : BlockingAction()

    object PauseSession : BlockingAction()

    object ResumeSession : BlockingAction()

    object EmergencyUnlock : BlockingAction()
}

fun BlockingSessionState.reduce(action: BlockingAction): BlockingSessionState {
    return when (this) {
        is Idle -> when (action) {
            is StartBlocking -> {
                val session = createSession(action)
                Blocking(session, TimerState.Inactive)
            }
            else -> this
        }

        is Blocking -> when (action) {
            is StopBlocking -> Completed(session)
            is AppOpened -> {
                if (isBlocked(action.packageName)) {
                    OverlayShowing(session, action.packageName, timerState)
                } else {
                    this
                }
            }
            is TimerExpired -> Completed(session)
            is ExtendTimer -> this.copy(
                timerState = extendTimerState(timerState, action.minutes)
            )
            is PauseSession -> Paused(session, Instant.now(), getRemainingTime())
            else -> this
        }

        is OverlayShowing -> when (action) {
            is AppClosed -> Blocking(session, timerState)
            is StopBlocking -> Completed(session)
            is TimerExpired -> Completed(session)
            else -> this
        }

        is Paused -> when (action) {
            is ResumeSession -> Blocking(session, resumeTimer(remainingTime))
            is StopBlocking -> Completed(session)
            else -> this
        }

        is Completed -> when (action) {
            is StartBlocking -> {
                val newSession = createSession(action)
                Blocking(newSession, TimerState.Inactive)
            }
            else -> Idle // auto-transition to Idle
        }
    }
}
```

---

## 6. Flujos Detallados

### 6.1 Flujo: Activar Bloqueo

```
┌─────────┐     ┌─────────────┐     ┌───────────────┐     ┌─────────────┐
│  USER   │     │BlockingMgr  │     │ProfilesModule │     │MonitorService│
└────┬────┘     └──────┬──────┘     └───────┬───────┘     └──────┬──────┘
     │                 │                     │                    │
     │  Tap NFC tag    │                     │                    │
     │────────────────▶│                     │                    │
     │                 │                     │                    │
     │                 │  getProfile()       │                    │
     │                 │────────────────────▶│                    │
     │                 │                     │                    │
     │                 │     Profile         │                    │
     │                 │◀────────────────────│                    │
     │                 │                     │                    │
     │                 │  Create session     │                    │
     │                 │  Update state       │                    │
     │                 │                     │                    │
     │                 │  start()            │                    │
     │                 │───────────────────────────────────────▶  │
     │                 │                     │                    │
     │                 │  Show notification  │                    │
     │                 │                     │                    │
     │   Vibrate +     │                     │                    │
     │   Notification  │                     │                    │
     │◀────────────────│                     │                    │
     │                 │                     │                    │
```

### 6.2 Flujo: App Bloqueada Detectada

```
┌─────────┐     ┌───────────────┐     ┌─────────────┐     ┌──────────────┐
│  USER   │     │MonitorService │     │BlockingMgr  │     │OverlayManager│
└────┬────┘     └───────┬───────┘     └──────┬──────┘     └──────┬───────┘
     │                  │                     │                   │
     │ Open Instagram   │                     │                   │
     │─────────────────▶│                     │                   │
     │                  │                     │                   │
     │                  │ getForegroundApp()  │                   │
     │                  │────────────────────▶│                   │
     │                  │                     │                   │
     │                  │ isAppBlocked(pkg)   │                   │
     │                  │────────────────────▶│                   │
     │                  │                     │                   │
     │                  │    true             │                   │
     │                  │◀────────────────────│                   │
     │                  │                     │                   │
     │                  │ showOverlay(pkg)    │                   │
     │                  │────────────────────────────────────────▶│
     │                  │                     │                   │
     │                  │                     │ Record attempt    │
     │                  │                     │                   │
     │   Overlay shown  │                     │                   │
     │◀─────────────────────────────────────────────────────────  │
     │                  │                     │                   │
     │ (sees overlay,   │                     │                   │
     │  goes back)      │                     │                   │
     │                  │                     │                   │
     │                  │ hideOverlay()       │                   │
     │                  │────────────────────────────────────────▶│
     │                  │                     │                   │
```

### 6.3 Flujo: Timer Expira

```
┌─────────────┐     ┌─────────────┐     ┌───────────────┐     ┌──────────────┐
│ TimerManager│     │BlockingMgr  │     │MonitorService │     │NotificationMgr│
└──────┬──────┘     └──────┬──────┘     └───────┬───────┘     └──────┬───────┘
       │                   │                     │                    │
       │ Timer completed   │                     │                    │
       │──────────────────▶│                     │                    │
       │                   │                     │                    │
       │                   │ stopBlocking()      │                    │
       │                   │                     │                    │
       │                   │ Update state        │                    │
       │                   │                     │                    │
       │                   │ stop()              │                    │
       │                   │────────────────────▶│                    │
       │                   │                     │                    │
       │                   │ Cancel notification │                    │
       │                   │                     │                    │
       │                   │ Show "Session complete"                  │
       │                   │─────────────────────────────────────────▶│
       │                   │                     │                    │
       │                   │ Save session to DB  │                    │
       │                   │                     │                    │
```

---

## 7. Integración con Android

### 7.1 Manifest Declarations

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Permisos -->
    <uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"
        tools:ignore="ProtectedPermissions" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />

    <application>

        <!-- Foreground Service para monitoreo -->
        <service
            android:name=".blocking.AppMonitorService"
            android:foregroundServiceType="specialUse"
            android:exported="false">
            <property
                android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"
                android:value="Digital wellbeing app blocker monitoring" />
        </service>

        <!-- Boot receiver para restaurar estado -->
        <receiver
            android:name=".blocking.BootReceiver"
            android:enabled="true"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>

    </application>
</manifest>
```

### 7.2 Permission Flow

```kotlin
/**
 * Verifica y solicita permisos necesarios para bloqueo.
 */
class BlockingPermissionChecker @Inject constructor(
    @ApplicationContext private val context: Context,
    private val usageStatsHelper: UsageStatsHelper,
    private val overlayManager: OverlayManager
) {

    fun checkAllPermissions(): PermissionStatus {
        return PermissionStatus(
            usageStats = usageStatsHelper.hasPermission(),
            overlay = overlayManager.hasOverlayPermission(),
            notifications = checkNotificationPermission()
        )
    }

    fun getMissingPermissions(): List<Permission> {
        val status = checkAllPermissions()
        return buildList {
            if (!status.usageStats) add(Permission.USAGE_STATS)
            if (!status.overlay) add(Permission.OVERLAY)
            if (!status.notifications) add(Permission.NOTIFICATIONS)
        }
    }

    fun requestPermission(activity: Activity, permission: Permission) {
        when (permission) {
            Permission.USAGE_STATS -> {
                val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
                activity.startActivity(intent)
            }
            Permission.OVERLAY -> {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${context.packageName}")
                )
                activity.startActivity(intent)
            }
            Permission.NOTIFICATIONS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        REQUEST_CODE_NOTIFICATIONS
                    )
                }
            }
        }
    }
}

data class PermissionStatus(
    val usageStats: Boolean,
    val overlay: Boolean,
    val notifications: Boolean
) {
    val allGranted: Boolean
        get() = usageStats && overlay && notifications
}

enum class Permission {
    USAGE_STATS,
    OVERLAY,
    NOTIFICATIONS
}
```

### 7.3 Overlay Implementation

```kotlin
/**
 * Implementación del overlay usando WindowManager.
 */
@Singleton
class OverlayManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : OverlayManager {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null

    private val _overlayState = MutableStateFlow<OverlayState>(OverlayState.Hidden)
    override val overlayState: StateFlow<OverlayState> = _overlayState.asStateFlow()

    override fun showOverlay(blockedPackage: String) {
        if (overlayView != null) return // Already showing

        val view = createOverlayView(blockedPackage)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER

        try {
            windowManager.addView(view, params)
            overlayView = view

            _overlayState.value = OverlayState.Showing(
                blockedPackage = blockedPackage,
                blockedAppName = getAppName(blockedPackage),
                profileName = getCurrentProfileName(),
                remainingTime = getRemainingTime(),
                motivationalMessage = getRandomMotivationalMessage()
            )
        } catch (e: Exception) {
            Log.e("OverlayManager", "Failed to show overlay", e)
        }
    }

    override fun hideOverlay() {
        overlayView?.let { view ->
            try {
                windowManager.removeView(view)
            } catch (e: Exception) {
                Log.e("OverlayManager", "Failed to hide overlay", e)
            }
            overlayView = null
        }
        _overlayState.value = OverlayState.Hidden
    }

    private fun createOverlayView(blockedPackage: String): View {
        return ComposeView(context).apply {
            setContent {
                BlockOverlayContent(
                    blockedPackage = blockedPackage,
                    onBack = { navigateHome() }
                )
            }
        }
    }

    private fun navigateHome() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
```

---

## 8. Overlay UI Design

### 8.1 BlockOverlayContent Composable

```kotlin
@Composable
fun BlockOverlayContent(
    blockedPackage: String,
    profileName: String,
    remainingTime: Duration?,
    motivationalMessage: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Icono de bloqueo
            Icon(
                imageVector = Icons.Outlined.Block,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            // Título
            Text(
                text = "App bloqueada",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Nombre del perfil activo
            Text(
                text = "Perfil: $profileName",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Timer (si existe)
            remainingTime?.let { time ->
                TimerDisplay(remaining = time)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mensaje motivacional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "\"$motivationalMessage\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón para volver
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Volver al inicio")
            }
        }
    }
}

@Composable
private fun TimerDisplay(remaining: Duration) {
    val hours = remaining.toHours()
    val minutes = remaining.toMinutes() % 60
    val seconds = remaining.seconds % 60

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Timer,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = if (hours > 0) {
                String.format("%d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format("%02d:%02d", minutes, seconds)
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}
```

### 8.2 Mensajes Motivacionales

```kotlin
object MotivationalMessages {
    private val messages = listOf(
        "¿Es realmente urgente?",
        "Tu yo del futuro te agradecerá esta pausa",
        "Cada minuto cuenta. Invierte en ti.",
        "La distracción puede esperar. Tu enfoque no.",
        "Respira. Reflexiona. Decide conscientemente.",
        "Este es tu umbral. Crúzalo con intención.",
        "El tiempo perdido nunca regresa.",
        "Tu atención es tu superpoder.",
        "Pequeñas victorias llevan a grandes logros.",
        "¿Qué es más importante ahora mismo?",
        "La disciplina es el puente entre metas y logros.",
        "Elige sabiamente cómo usas tu tiempo."
    )

    fun getRandom(): String = messages.random()

    fun getByTimeOfDay(): String {
        val hour = LocalTime.now().hour
        return when {
            hour < 12 -> "Buenos días. Comienza con enfoque."
            hour < 18 -> "Mantén el momentum de la tarde."
            else -> "Casi terminas el día. Un esfuerzo más."
        }
    }
}
```

---

## 9. Edge Cases y Manejo de Errores

### 9.1 Edge Cases

| # | Caso | Comportamiento Esperado |
|---|------|------------------------|
| 1 | App killed por sistema | Boot receiver restaura estado, reinicia servicio |
| 2 | Low memory | Servicio marcado como foreground, baja prioridad de kill |
| 3 | Doze mode | Servicio foreground no afectado por Doze |
| 4 | Split screen | Monitorea ambas apps, bloquea si alguna está en lista |
| 5 | App se actualiza | Package name igual, sigue bloqueada |
| 6 | App desinstalada | Remover de lista automáticamente |
| 7 | Overlay permission revocado | Detectar y solicitar de nuevo |
| 8 | Usage stats permission revocado | Mostrar error, no funciona sin esto |
| 9 | Usuario abre Settings | Agregar Settings a whitelist implícita |
| 10 | Usuario intenta desinstalar Umbral | Si strictMode, prevenir (requiere Device Admin) |

### 9.2 Error Recovery

```kotlin
/**
 * Estrategias de recuperación para el servicio de monitoreo.
 */
class BlockingRecoveryManager @Inject constructor(
    private val blockingManager: BlockingManager,
    private val dataStore: DataStore<Preferences>
) {

    /**
     * Llamado al iniciar la app para restaurar estado.
     */
    suspend fun recoverState() {
        val savedState = loadSavedState()

        if (savedState.wasBlocking) {
            // Verificar si la sesión debería seguir activa
            val shouldContinue = when {
                savedState.timerEndAt != null &&
                    Instant.parse(savedState.timerEndAt).isBefore(Instant.now()) -> {
                    // Timer expiró mientras app estaba cerrada
                    logExpiredSession(savedState)
                    false
                }
                else -> true
            }

            if (shouldContinue) {
                blockingManager.resumeSession(savedState.sessionId)
            }
        }
    }

    /**
     * Guarda estado antes de que la app muera.
     */
    suspend fun saveState(state: BlockingState) {
        dataStore.edit { prefs ->
            prefs[KEY_WAS_BLOCKING] = state.isActive
            prefs[KEY_SESSION_ID] = state.sessionId ?: ""
            prefs[KEY_PROFILE_ID] = state.activeProfileId ?: ""
            prefs[KEY_TIMER_END] = state.timerEndAt?.toString() ?: ""
        }
    }

    companion object {
        private val KEY_WAS_BLOCKING = booleanPreferencesKey("was_blocking")
        private val KEY_SESSION_ID = stringPreferencesKey("session_id")
        private val KEY_PROFILE_ID = stringPreferencesKey("profile_id")
        private val KEY_TIMER_END = stringPreferencesKey("timer_end")
    }
}
```

### 9.3 Boot Receiver

```kotlin
/**
 * Restaura estado de bloqueo después de reinicio.
 */
@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var recoveryManager: BlockingRecoveryManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Usar goAsync() para operaciones largas
            val pendingResult = goAsync()

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    recoveryManager.recoverState()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
```

---

## 10. Testing Strategy

### 10.1 Unit Tests

```kotlin
class BlockingManagerTest {

    private lateinit var blockingManager: BlockingManagerImpl
    private lateinit var mockProfilesRepository: ProfilesRepository
    private lateinit var mockBlockingRepository: BlockingRepository

    @Before
    fun setup() {
        mockProfilesRepository = mockk()
        mockBlockingRepository = mockk()

        blockingManager = BlockingManagerImpl(
            profilesRepository = mockProfilesRepository,
            blockingRepository = mockBlockingRepository,
            timerManager = FakeTimerManager(),
            dispatchers = TestDispatchers()
        )
    }

    @Test
    fun `startBlocking creates session and updates state`() = runTest {
        // Arrange
        val profile = Profile(
            id = "profile1",
            name = "Social",
            blockedApps = setOf("com.instagram.android")
        )
        coEvery { mockProfilesRepository.getById("profile1") } returns profile
        coEvery { mockBlockingRepository.insertSession(any()) } returns Unit

        // Act
        val result = blockingManager.startBlocking(
            profileId = "profile1",
            trigger = TriggerType.NFC_TAG
        )

        // Assert
        assertTrue(result.isSuccess)
        assertTrue(blockingManager.isBlocking)
        assertEquals("profile1", blockingManager.state.value.activeProfileId)
    }

    @Test
    fun `isAppBlocked returns true for blocked app`() = runTest {
        // Arrange
        setupActiveSession(blockedApps = setOf("com.instagram.android"))

        // Act & Assert
        assertTrue(blockingManager.isAppBlocked("com.instagram.android"))
        assertFalse(blockingManager.isAppBlocked("com.whatsapp"))
    }

    @Test
    fun `whitelisted apps are never blocked`() = runTest {
        // Arrange
        setupActiveSession(
            blockedApps = setOf("com.google.android.dialer"),
            whitelistedApps = setOf("com.google.android.dialer")
        )

        // Act & Assert
        assertFalse(blockingManager.isAppBlocked("com.google.android.dialer"))
    }

    @Test
    fun `toggleBlocking switches state`() = runTest {
        // Arrange
        assertFalse(blockingManager.isBlocking)

        // Act - Toggle ON
        blockingManager.toggleBlocking("profile1", TriggerType.MANUAL)

        // Assert
        assertTrue(blockingManager.isBlocking)

        // Act - Toggle OFF
        blockingManager.toggleBlocking("profile1", TriggerType.MANUAL)

        // Assert
        assertFalse(blockingManager.isBlocking)
    }
}
```

### 10.2 Integration Tests

```kotlin
@HiltAndroidTest
class BlockingIntegrationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var blockingManager: BlockingManager

    @Inject
    lateinit var profilesRepository: ProfilesRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `full blocking flow with timer`() = runTest {
        // Create profile
        val profile = Profile(
            name = "Test",
            blockedApps = setOf("com.test.app")
        )
        profilesRepository.insert(profile)

        // Start blocking with 1 minute timer
        val result = blockingManager.startBlocking(
            profileId = profile.id,
            trigger = TriggerType.MANUAL,
            timerMinutes = 1
        )

        assertTrue(result.isSuccess)
        assertTrue(blockingManager.isBlocking)
        assertNotNull(blockingManager.state.value.timerEndAt)

        // Wait for timer (use test dispatcher to advance time)
        advanceTimeBy(61_000)

        // Should auto-unlock
        assertFalse(blockingManager.isBlocking)
    }
}
```

### 10.3 Manual Testing Checklist

| # | Test Case | Steps | Expected Result |
|---|-----------|-------|-----------------|
| 1 | Basic blocking | Activate profile → Open blocked app | Overlay shown |
| 2 | Multiple apps | Block 3 apps → Open each | All show overlay |
| 3 | Whitelist works | Whitelist Phone → Block all → Open Phone | Phone opens normally |
| 4 | Timer works | Set 2 min timer → Wait | Auto-unlocks at 2 min |
| 5 | Timer countdown | Set timer → Check overlay | Shows countdown |
| 6 | Toggle via NFC | Tap tag → Check state → Tap again | Toggles on/off |
| 7 | App kill recovery | Force stop Umbral → Reopen | State restored |
| 8 | Device restart | Reboot with blocking active | Blocking resumes |
| 9 | Permission revoked | Revoke overlay → Open blocked app | Error shown |
| 10 | Split screen | Open blocked app in split | Overlay covers it |

---

## 11. Notificaciones

### 11.1 Notification Channel

```kotlin
object BlockingNotifications {
    const val CHANNEL_ID = "umbral_blocking"
    const val CHANNEL_NAME = "Bloqueo activo"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificación persistente mientras el bloqueo está activo"
                setShowBadge(false)
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}
```

### 11.2 Ongoing Notification

```kotlin
fun createBlockingNotification(
    context: Context,
    profileName: String,
    remainingTime: Duration?
): Notification {
    val intent = Intent(context, MainActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val stopIntent = Intent(context, BlockingActionReceiver::class.java).apply {
        action = ACTION_STOP_BLOCKING
    }
    val stopPendingIntent = PendingIntent.getBroadcast(
        context, 1, stopIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val text = if (remainingTime != null) {
        val minutes = remainingTime.toMinutes()
        "Perfil: $profileName • ${minutes}min restantes"
    } else {
        "Perfil: $profileName"
    }

    return NotificationCompat.Builder(context, BlockingNotifications.CHANNEL_ID)
        .setContentTitle("Bloqueo activo")
        .setContentText(text)
        .setSmallIcon(R.drawable.ic_notification_blocking)
        .setOngoing(true)
        .setContentIntent(pendingIntent)
        .addAction(R.drawable.ic_stop, "Desactivar", stopPendingIntent)
        .setCategory(NotificationCompat.CATEGORY_STATUS)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()
}
```

---

## 12. Estadísticas

### 12.1 Stats Collection

```kotlin
interface BlockingStatsCollector {

    /**
     * Registra intento de abrir app bloqueada.
     */
    suspend fun recordBlockedAttempt(
        sessionId: String,
        packageName: String
    )

    /**
     * Obtiene resumen de sesiones.
     */
    suspend fun getSessionsSummary(
        from: LocalDate,
        to: LocalDate
    ): SessionsSummary

    /**
     * Obtiene apps más bloqueadas.
     */
    suspend fun getMostBlockedApps(
        limit: Int = 10
    ): List<AppBlockStats>

    /**
     * Obtiene tiempo total bloqueado.
     */
    suspend fun getTotalBlockedTime(
        from: LocalDate,
        to: LocalDate
    ): Duration
}

data class SessionsSummary(
    val totalSessions: Int,
    val totalDuration: Duration,
    val averageDuration: Duration,
    val totalBlockedAttempts: Int,
    val sessionsPerDay: Map<LocalDate, Int>
)

data class AppBlockStats(
    val packageName: String,
    val appName: String,
    val attemptCount: Int,
    val lastAttempt: Instant
)
```

---

## 13. Criterios de Aceptación

### 13.1 Funcionales

- [ ] Detecta app en foreground usando UsageStatsManager
- [ ] Muestra overlay cuando se abre app bloqueada
- [ ] Overlay cubre pantalla completa y no es dismissable
- [ ] Botón "Volver" en overlay lleva a home
- [ ] Estado persiste cuando app es killed
- [ ] Estado se restaura después de reboot
- [ ] Timer funciona y desbloquea automáticamente
- [ ] Whitelist siempre permite apps configuradas
- [ ] Notificación persistente durante bloqueo
- [ ] Toggle funciona correctamente

### 13.2 No Funcionales

- [ ] Latencia de detección < 500ms
- [ ] Consumo de batería < 5% adicional
- [ ] Servicio sobrevive low memory conditions
- [ ] Funciona sin conexión a internet
- [ ] Soporta Android 10+ (API 29+)

### 13.3 UX

- [ ] Overlay tiene animación de entrada suave
- [ ] Mensajes motivacionales rotan
- [ ] Timer visible en overlay y notificación
- [ ] Feedback háptico al bloquear
- [ ] Permisos solicitados con explicación clara

---

## 14. Dependencias con Otros Módulos

| Módulo | Tipo | Descripción |
|--------|------|-------------|
| `NfcModule` | Input | Recibe comandos de toggle |
| `ProfilesModule` | Input | Obtiene lista de apps a bloquear |
| `SettingsModule` | Input | Lee configuración (vibración, sonido) |
| `StatsModule` | Output | Envía datos de sesiones y attempts |
| `WidgetsModule` | Output | Notifica cambios de estado |

---

**Creado:** 2026-01-03
**Autor:** Oden Forge - Spec Writer
**Próximo:** profiles-module.md
