# Statistics Module Specification

**Estado:** 🟢 Activo
**Última actualización:** 2026-01-03
**Versión:** 1.0.0
**Líneas estimadas:** ~650

---

## 1. Visión General

### 1.1 Propósito

El módulo Statistics proporciona tracking, análisis y visualización del uso de Umbral. Registra intentos de acceso bloqueados, tiempo de bloqueo activo, y genera métricas de productividad para motivar al usuario.

### 1.2 Métricas Principales

1. **Intentos Bloqueados** - Cuántas veces se intentó abrir apps bloqueadas
2. **Tiempo Ahorrado** - Estimación basada en tiempo promedio por app
3. **Racha (Streak)** - Días consecutivos usando el bloqueo
4. **Apps Más Bloqueadas** - Ranking de apps por intentos
5. **Patrones Temporales** - Horas/días de mayor tentación

### 1.3 Alcance

- Tracking de eventos de bloqueo
- Agregación de datos por período
- Cálculo de métricas derivadas
- Visualización con gráficas
- Exportación de datos (CSV/JSON)
- Gamificación (logros, rachas)

### 1.4 Dependencias

```
Depende de:
├── blocking-module (eventos de bloqueo)
└── profiles-module (datos de perfiles)

Dependido por:
└── ui-module (StatsScreen, widgets)
```

---

## 2. Arquitectura

### 2.1 Diagrama de Componentes

```
┌──────────────────────────────────────────────────────────────┐
│                     Statistics Module                         │
├──────────────────────────────────────────────────────────────┤
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐  │
│  │ StatsTracker   │  │ StatsAnalyzer  │  │ StatsExporter  │  │
│  │                │  │                │  │                │  │
│  │ - recordEvent  │  │ - aggregate()  │  │ - toCSV()      │  │
│  │ - startSession │  │ - calculate()  │  │ - toJSON()     │  │
│  │ - endSession   │  │ - compare()    │  │ - share()      │  │
│  └───────┬────────┘  └───────┬────────┘  └───────┬────────┘  │
│          │                   │                   │            │
│          └───────────────────┼───────────────────┘            │
│                              │                                │
│                   ┌──────────▼──────────┐                     │
│                   │   StatsRepository   │                     │
│                   │                     │                     │
│                   │ - getDailyStats()   │                     │
│                   │ - getWeeklyStats()  │                     │
│                   │ - getStreakInfo()   │                     │
│                   │ - getTopApps()      │                     │
│                   └──────────┬──────────┘                     │
│                              │                                │
│                   ┌──────────▼──────────┐                     │
│                   │    Room Database    │                     │
│                   │                     │                     │
│                   │ - BlockedAttempt    │                     │
│                   │ - BlockingSession   │                     │
│                   │ - DailySummary      │                     │
│                   └─────────────────────┘                     │
└──────────────────────────────────────────────────────────────┘
```

### 2.2 Flujo de Datos

```
Tracking:
BlockingManager → StatsTracker → Room DB

Análisis:
Room DB → StatsAnalyzer → Aggregated Stats → UI

Exportación:
Room DB → StatsExporter → CSV/JSON → Share Intent
```

---

## 3. Modelos de Dominio

### 3.1 Eventos y Sesiones

```kotlin
// domain/model/BlockedAttempt.kt

/**
 * Registro de un intento de abrir una app bloqueada
 */
data class BlockedAttempt(
    val id: String,
    val packageName: String,
    val appName: String,
    val profileId: String,
    val timestamp: Long,
    val dayOfWeek: Int,      // 1-7 (Lunes-Domingo)
    val hourOfDay: Int       // 0-23
)

/**
 * Sesión de bloqueo activo
 */
data class BlockingSession(
    val id: String,
    val profileId: String,
    val startTime: Long,
    val endTime: Long?,
    val triggeredBy: TriggerType,
    val blockedAttempts: Int,
    val durationSeconds: Long?
) {
    val isActive: Boolean get() = endTime == null
}

enum class TriggerType {
    NFC_TAG,
    QR_CODE,
    MANUAL,
    TIMER,
    SCHEDULE
}
```

### 3.2 Resúmenes Agregados

```kotlin
// domain/model/StatsSummary.kt

/**
 * Resumen diario de estadísticas
 */
data class DailySummary(
    val date: LocalDate,
    val totalBlockedAttempts: Int,
    val totalSessionsCount: Int,
    val totalBlockingSeconds: Long,
    val estimatedTimeSavedSeconds: Long,
    val topBlockedApp: String?,
    val topBlockedAppCount: Int,
    val uniqueAppsBlocked: Int,
    val peakHour: Int?           // Hora con más intentos
)

/**
 * Estadísticas semanales
 */
data class WeeklySummary(
    val weekStart: LocalDate,
    val weekEnd: LocalDate,
    val dailySummaries: List<DailySummary>,
    val totalBlockedAttempts: Int,
    val totalSessionsCount: Int,
    val totalBlockingSeconds: Long,
    val estimatedTimeSavedSeconds: Long,
    val averageAttemptsPerDay: Float,
    val trendVsPreviousWeek: Float,  // -1 a 1 (mejora = negativo)
    val mostProductiveDay: DayOfWeek?,
    val leastProductiveDay: DayOfWeek?
)

/**
 * Estadísticas mensuales
 */
data class MonthlySummary(
    val month: YearMonth,
    val weeklySummaries: List<WeeklySummary>,
    val totalBlockedAttempts: Int,
    val totalSessionsCount: Int,
    val totalBlockingHours: Float,
    val estimatedTimeSavedHours: Float,
    val streakDays: Int,
    val longestStreak: Int,
    val topApps: List<AppBlockCount>
)

data class AppBlockCount(
    val packageName: String,
    val appName: String,
    val count: Int,
    val percentage: Float
)
```

### 3.3 Racha y Logros

```kotlin
// domain/model/StreakInfo.kt

/**
 * Información de racha actual
 */
data class StreakInfo(
    val currentStreak: Int,
    val longestStreak: Int,
    val streakStartDate: LocalDate?,
    val lastActiveDate: LocalDate?,
    val isActiveToday: Boolean
) {
    val willBreakTomorrow: Boolean
        get() = !isActiveToday && currentStreak > 0
}

/**
 * Logros desbloqueados
 */
data class Achievement(
    val id: String,
    val type: AchievementType,
    val title: String,
    val description: String,
    val unlockedAt: Long?,
    val progress: Float,      // 0-1
    val threshold: Int
) {
    val isUnlocked: Boolean get() = unlockedAt != null
}

enum class AchievementType {
    STREAK_3_DAYS,
    STREAK_7_DAYS,
    STREAK_30_DAYS,
    BLOCKED_100_ATTEMPTS,
    BLOCKED_500_ATTEMPTS,
    BLOCKED_1000_ATTEMPTS,
    SAVED_1_HOUR,
    SAVED_10_HOURS,
    SAVED_24_HOURS,
    FIRST_NFC_SCAN,
    FIRST_QR_SCAN,
    CREATED_3_PROFILES
}
```

### 3.4 Configuración de Tiempo Estimado

```kotlin
// domain/model/TimeEstimateConfig.kt

/**
 * Configuración para estimar tiempo ahorrado
 */
data class TimeEstimateConfig(
    val defaultMinutesPerAttempt: Int = 5,
    val appSpecificMinutes: Map<String, Int> = defaultAppMinutes
) {
    companion object {
        val defaultAppMinutes = mapOf(
            "com.instagram.android" to 10,
            "com.twitter.android" to 8,
            "com.facebook.katana" to 12,
            "com.zhiliaoapp.musically" to 15,  // TikTok
            "com.google.android.youtube" to 20,
            "com.reddit.frontpage" to 10,
            "com.snapchat.android" to 5
        )
    }

    fun getMinutesForApp(packageName: String): Int {
        return appSpecificMinutes[packageName] ?: defaultMinutesPerAttempt
    }
}
```

---

## 4. Interfaces Públicas

### 4.1 StatsTracker

```kotlin
// domain/StatsTracker.kt
interface StatsTracker {

    /**
     * Registra un intento de abrir una app bloqueada
     */
    suspend fun recordBlockedAttempt(
        packageName: String,
        appName: String,
        profileId: String
    )

    /**
     * Inicia una nueva sesión de bloqueo
     */
    suspend fun startBlockingSession(
        profileId: String,
        triggeredBy: TriggerType
    ): String  // Returns session ID

    /**
     * Finaliza una sesión de bloqueo
     */
    suspend fun endBlockingSession(sessionId: String)

    /**
     * Obtiene la sesión activa actual
     */
    suspend fun getActiveSession(): BlockingSession?

    /**
     * Incrementa el contador de intentos de la sesión activa
     */
    suspend fun incrementSessionAttempts(sessionId: String)
}
```

### 4.2 StatsAnalyzer

```kotlin
// domain/StatsAnalyzer.kt
interface StatsAnalyzer {

    /**
     * Obtiene resumen de hoy
     */
    suspend fun getTodaySummary(): DailySummary

    /**
     * Obtiene resumen de un día específico
     */
    suspend fun getDailySummary(date: LocalDate): DailySummary?

    /**
     * Obtiene resumen semanal
     */
    suspend fun getWeeklySummary(weekStart: LocalDate? = null): WeeklySummary

    /**
     * Obtiene resumen mensual
     */
    suspend fun getMonthlySummary(month: YearMonth? = null): MonthlySummary

    /**
     * Obtiene información de racha
     */
    suspend fun getStreakInfo(): StreakInfo

    /**
     * Obtiene top N apps más bloqueadas
     */
    suspend fun getTopBlockedApps(
        limit: Int = 5,
        period: StatsPeriod = StatsPeriod.ALL_TIME
    ): List<AppBlockCount>

    /**
     * Obtiene distribución por hora del día
     */
    suspend fun getHourlyDistribution(
        period: StatsPeriod = StatsPeriod.LAST_7_DAYS
    ): Map<Int, Int>

    /**
     * Obtiene distribución por día de la semana
     */
    suspend fun getWeekdayDistribution(
        period: StatsPeriod = StatsPeriod.LAST_30_DAYS
    ): Map<DayOfWeek, Int>

    /**
     * Calcula tendencia comparando períodos
     */
    suspend fun calculateTrend(
        currentPeriod: StatsPeriod,
        previousPeriod: StatsPeriod
    ): Float  // -1 a 1

    /**
     * Obtiene logros y su progreso
     */
    suspend fun getAchievements(): List<Achievement>

    /**
     * Verifica y desbloquea nuevos logros
     */
    suspend fun checkAndUnlockAchievements(): List<Achievement>
}

enum class StatsPeriod {
    TODAY,
    YESTERDAY,
    LAST_7_DAYS,
    LAST_30_DAYS,
    THIS_MONTH,
    LAST_MONTH,
    ALL_TIME
}
```

### 4.3 StatsExporter

```kotlin
// domain/StatsExporter.kt
interface StatsExporter {

    /**
     * Exporta estadísticas a CSV
     */
    suspend fun exportToCSV(
        period: StatsPeriod = StatsPeriod.ALL_TIME,
        includeRawData: Boolean = false
    ): Result<Uri>

    /**
     * Exporta estadísticas a JSON
     */
    suspend fun exportToJSON(
        period: StatsPeriod = StatsPeriod.ALL_TIME
    ): Result<Uri>

    /**
     * Genera un share intent con resumen
     */
    suspend fun generateShareSummary(
        period: StatsPeriod = StatsPeriod.LAST_7_DAYS
    ): ShareSummary
}

data class ShareSummary(
    val text: String,
    val imageUri: Uri?  // Imagen generada con stats
)
```

### 4.4 StatsRepository

```kotlin
// domain/StatsRepository.kt
interface StatsRepository {

    // === Blocked Attempts ===

    fun getBlockedAttempts(
        startTime: Long,
        endTime: Long
    ): Flow<List<BlockedAttempt>>

    suspend fun getBlockedAttemptsCount(
        startTime: Long,
        endTime: Long
    ): Int

    suspend fun getBlockedAttemptsByApp(
        startTime: Long,
        endTime: Long
    ): Map<String, Int>

    // === Sessions ===

    fun getSessions(
        startTime: Long,
        endTime: Long
    ): Flow<List<BlockingSession>>

    suspend fun getTotalBlockingTime(
        startTime: Long,
        endTime: Long
    ): Long  // Segundos

    // === Daily Summaries ===

    fun getDailySummaries(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<DailySummary>>

    suspend fun getOrCreateDailySummary(date: LocalDate): DailySummary

    suspend fun updateDailySummary(summary: DailySummary)

    // === Streak ===

    suspend fun getStreakInfo(): StreakInfo

    suspend fun updateStreakInfo(info: StreakInfo)

    // === Achievements ===

    fun getAchievements(): Flow<List<Achievement>>

    suspend fun unlockAchievement(type: AchievementType)

    suspend fun updateAchievementProgress(type: AchievementType, progress: Float)
}
```

---

## 5. Room Entities

### 5.1 BlockedAttemptEntity

```kotlin
// data/local/entity/BlockedAttemptEntity.kt
@Entity(
    tableName = "blocked_attempts",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["package_name"]),
        Index(value = ["profile_id"])
    ]
)
data class BlockedAttemptEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "app_name")
    val appName: String,

    @ColumnInfo(name = "profile_id")
    val profileId: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "day_of_week")
    val dayOfWeek: Int,

    @ColumnInfo(name = "hour_of_day")
    val hourOfDay: Int
)
```

### 5.2 BlockingSessionEntity

```kotlin
// data/local/entity/BlockingSessionEntity.kt
@Entity(
    tableName = "blocking_sessions",
    indices = [
        Index(value = ["start_time"]),
        Index(value = ["profile_id"])
    ]
)
data class BlockingSessionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "profile_id")
    val profileId: String,

    @ColumnInfo(name = "start_time")
    val startTime: Long,

    @ColumnInfo(name = "end_time")
    val endTime: Long?,

    @ColumnInfo(name = "triggered_by")
    val triggeredBy: String,

    @ColumnInfo(name = "blocked_attempts")
    val blockedAttempts: Int,

    @ColumnInfo(name = "duration_seconds")
    val durationSeconds: Long?
)
```

### 5.3 DailySummaryEntity

```kotlin
// data/local/entity/DailySummaryEntity.kt
@Entity(
    tableName = "daily_summaries",
    indices = [Index(value = ["date"], unique = true)]
)
data class DailySummaryEntity(
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: String,  // yyyy-MM-dd

    @ColumnInfo(name = "total_blocked_attempts")
    val totalBlockedAttempts: Int,

    @ColumnInfo(name = "total_sessions_count")
    val totalSessionsCount: Int,

    @ColumnInfo(name = "total_blocking_seconds")
    val totalBlockingSeconds: Long,

    @ColumnInfo(name = "estimated_time_saved_seconds")
    val estimatedTimeSavedSeconds: Long,

    @ColumnInfo(name = "top_blocked_app")
    val topBlockedApp: String?,

    @ColumnInfo(name = "top_blocked_app_count")
    val topBlockedAppCount: Int,

    @ColumnInfo(name = "unique_apps_blocked")
    val uniqueAppsBlocked: Int,

    @ColumnInfo(name = "peak_hour")
    val peakHour: Int?
)
```

### 5.4 AchievementEntity

```kotlin
// data/local/entity/AchievementEntity.kt
@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "unlocked_at")
    val unlockedAt: Long?,

    @ColumnInfo(name = "progress")
    val progress: Float,

    @ColumnInfo(name = "threshold")
    val threshold: Int
)
```

### 5.5 DAOs

```kotlin
// data/local/dao/StatsDao.kt
@Dao
interface StatsDao {

    // === Blocked Attempts ===

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlockedAttempt(attempt: BlockedAttemptEntity)

    @Query("""
        SELECT * FROM blocked_attempts
        WHERE timestamp >= :startTime AND timestamp <= :endTime
        ORDER BY timestamp DESC
    """)
    fun getBlockedAttempts(startTime: Long, endTime: Long): Flow<List<BlockedAttemptEntity>>

    @Query("""
        SELECT COUNT(*) FROM blocked_attempts
        WHERE timestamp >= :startTime AND timestamp <= :endTime
    """)
    suspend fun getBlockedAttemptsCount(startTime: Long, endTime: Long): Int

    @Query("""
        SELECT package_name, COUNT(*) as count
        FROM blocked_attempts
        WHERE timestamp >= :startTime AND timestamp <= :endTime
        GROUP BY package_name
        ORDER BY count DESC
    """)
    suspend fun getBlockedAttemptsByApp(startTime: Long, endTime: Long): List<AppCountTuple>

    @Query("""
        SELECT hour_of_day, COUNT(*) as count
        FROM blocked_attempts
        WHERE timestamp >= :startTime AND timestamp <= :endTime
        GROUP BY hour_of_day
    """)
    suspend fun getHourlyDistribution(startTime: Long, endTime: Long): List<HourCountTuple>

    @Query("""
        SELECT day_of_week, COUNT(*) as count
        FROM blocked_attempts
        WHERE timestamp >= :startTime AND timestamp <= :endTime
        GROUP BY day_of_week
    """)
    suspend fun getWeekdayDistribution(startTime: Long, endTime: Long): List<DayCountTuple>

    // === Sessions ===

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: BlockingSessionEntity)

    @Update
    suspend fun updateSession(session: BlockingSessionEntity)

    @Query("SELECT * FROM blocking_sessions WHERE end_time IS NULL LIMIT 1")
    suspend fun getActiveSession(): BlockingSessionEntity?

    @Query("""
        SELECT SUM(duration_seconds) FROM blocking_sessions
        WHERE start_time >= :startTime AND start_time <= :endTime
    """)
    suspend fun getTotalBlockingTime(startTime: Long, endTime: Long): Long?

    // === Daily Summaries ===

    @Query("SELECT * FROM daily_summaries WHERE date = :date")
    suspend fun getDailySummary(date: String): DailySummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailySummary(summary: DailySummaryEntity)

    @Query("""
        SELECT * FROM daily_summaries
        WHERE date >= :startDate AND date <= :endDate
        ORDER BY date ASC
    """)
    fun getDailySummaries(startDate: String, endDate: String): Flow<List<DailySummaryEntity>>

    // === Achievements ===

    @Query("SELECT * FROM achievements")
    fun getAchievements(): Flow<List<AchievementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: AchievementEntity)

    @Query("UPDATE achievements SET unlocked_at = :unlockedAt WHERE type = :type")
    suspend fun unlockAchievement(type: String, unlockedAt: Long)

    @Query("UPDATE achievements SET progress = :progress WHERE type = :type")
    suspend fun updateProgress(type: String, progress: Float)
}

data class AppCountTuple(
    @ColumnInfo(name = "package_name") val packageName: String,
    @ColumnInfo(name = "count") val count: Int
)

data class HourCountTuple(
    @ColumnInfo(name = "hour_of_day") val hour: Int,
    @ColumnInfo(name = "count") val count: Int
)

data class DayCountTuple(
    @ColumnInfo(name = "day_of_week") val day: Int,
    @ColumnInfo(name = "count") val count: Int
)
```

---

## 6. UI Components

### 6.1 StatsScreen

```kotlin
// ui/screens/StatsScreen.kt
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.stats_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::exportStats) {
                        Icon(Icons.Default.Share, contentDescription = null)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(UmbralDimens.screenPaddingHorizontal),
            verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceLg)
        ) {
            // Período selector
            item {
                PeriodSelector(
                    selectedPeriod = uiState.selectedPeriod,
                    onPeriodSelected = viewModel::selectPeriod
                )
            }

            // Resumen principal
            item {
                MainStatsCard(
                    blockedAttempts = uiState.blockedAttempts,
                    timeSaved = uiState.timeSavedMinutes,
                    streak = uiState.currentStreak
                )
            }

            // Gráfica de tendencia
            item {
                TrendChart(
                    data = uiState.dailyData,
                    period = uiState.selectedPeriod
                )
            }

            // Top apps
            item {
                TopAppsCard(
                    apps = uiState.topApps
                )
            }

            // Distribución por hora
            item {
                HourlyDistributionChart(
                    data = uiState.hourlyDistribution
                )
            }

            // Logros
            item {
                AchievementsSection(
                    achievements = uiState.achievements,
                    onAchievementClick = { /* Show details */ }
                )
            }
        }
    }
}
```

### 6.2 MainStatsCard

```kotlin
// ui/components/MainStatsCard.kt
@Composable
fun MainStatsCard(
    blockedAttempts: Int,
    timeSaved: Int,  // Minutos
    streak: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralDimens.spaceLg),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = blockedAttempts.toString(),
                label = stringResource(R.string.stats_blocked),
                icon = Icons.Default.Block
            )

            VerticalDivider(
                modifier = Modifier.height(64.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            StatItem(
                value = formatTimeSaved(timeSaved),
                label = stringResource(R.string.stats_time_saved),
                icon = Icons.Default.Schedule
            )

            VerticalDivider(
                modifier = Modifier.height(64.dp),
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
            )

            StatItem(
                value = "$streak",
                label = stringResource(R.string.stats_streak),
                icon = Icons.Default.LocalFireDepartment,
                valueColor = if (streak >= 7)
                    MaterialTheme.colorScheme.tertiary
                else
                    MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun StatItem(
    value: String,
    label: String,
    icon: ImageVector,
    valueColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceXs)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

private fun formatTimeSaved(minutes: Int): String {
    return when {
        minutes < 60 -> "${minutes}m"
        minutes < 1440 -> "${minutes / 60}h ${minutes % 60}m"
        else -> "${minutes / 1440}d ${(minutes % 1440) / 60}h"
    }
}
```

### 6.3 TrendChart

```kotlin
// ui/components/TrendChart.kt
@Composable
fun TrendChart(
    data: List<DailyDataPoint>,
    period: StatsPeriod,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(UmbralDimens.spaceLg)
        ) {
            Text(
                text = stringResource(R.string.stats_trend),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(UmbralDimens.spaceMd))

            if (data.isEmpty()) {
                EmptyChartPlaceholder()
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    drawTrendLine(data, this)
                }

                // X-axis labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    data.take(7).forEach { point ->
                        Text(
                            text = point.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

data class DailyDataPoint(
    val date: LocalDate,
    val value: Int,
    val label: String
)

private fun DrawScope.drawTrendLine(data: List<DailyDataPoint>, scope: DrawScope) {
    if (data.size < 2) return

    val maxValue = data.maxOfOrNull { it.value } ?: 1
    val minValue = 0

    val path = Path()
    val fillPath = Path()

    val stepX = size.width / (data.size - 1)

    data.forEachIndexed { index, point ->
        val x = index * stepX
        val y = size.height - ((point.value - minValue).toFloat() / (maxValue - minValue) * size.height)

        if (index == 0) {
            path.moveTo(x, y)
            fillPath.moveTo(x, size.height)
            fillPath.lineTo(x, y)
        } else {
            path.lineTo(x, y)
            fillPath.lineTo(x, y)
        }
    }

    // Cerrar path para fill
    fillPath.lineTo(size.width, size.height)
    fillPath.close()

    // Dibujar área bajo la curva
    drawPath(
        path = fillPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF1A237E).copy(alpha = 0.3f),
                Color(0xFF1A237E).copy(alpha = 0.0f)
            )
        )
    )

    // Dibujar línea
    drawPath(
        path = path,
        color = Color(0xFF1A237E),
        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
    )

    // Dibujar puntos
    data.forEachIndexed { index, point ->
        val x = index * stepX
        val y = size.height - ((point.value - minValue).toFloat() / (maxValue - minValue) * size.height)

        drawCircle(
            color = Color.White,
            radius = 6.dp.toPx(),
            center = Offset(x, y)
        )
        drawCircle(
            color = Color(0xFF1A237E),
            radius = 4.dp.toPx(),
            center = Offset(x, y)
        )
    }
}
```

### 6.4 TopAppsCard

```kotlin
// ui/components/TopAppsCard.kt
@Composable
fun TopAppsCard(
    apps: List<AppBlockCount>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(UmbralDimens.spaceLg),
            verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceMd)
        ) {
            Text(
                text = stringResource(R.string.stats_top_apps),
                style = MaterialTheme.typography.titleMedium
            )

            if (apps.isEmpty()) {
                Text(
                    text = stringResource(R.string.stats_no_data),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                apps.forEachIndexed { index, app ->
                    TopAppRow(
                        rank = index + 1,
                        app = app
                    )
                }
            }
        }
    }
}

@Composable
private fun TopAppRow(
    rank: Int,
    app: AppBlockCount,
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

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceMd)
    ) {
        // Rank
        Text(
            text = "#$rank",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.width(32.dp)
        )

        // App icon
        if (icon != null) {
            Image(
                painter = rememberDrawablePainter(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(app.appName.first().toString())
            }
        }

        // App name
        Text(
            text = app.appName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Count and percentage
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${app.count}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${(app.percentage * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

### 6.5 AchievementsSection

```kotlin
// ui/components/AchievementsSection.kt
@Composable
fun AchievementsSection(
    achievements: List<Achievement>,
    onAchievementClick: (Achievement) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(UmbralDimens.spaceLg),
            verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceMd)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.stats_achievements),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${achievements.count { it.isUnlocked }}/${achievements.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceMd)
            ) {
                items(achievements) { achievement ->
                    AchievementBadge(
                        achievement = achievement,
                        onClick = { onAchievementClick(achievement) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AchievementBadge(
    achievement: Achievement,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (achievement.isUnlocked) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentAlpha = if (achievement.isUnlocked) 1f else 0.5f

    Column(
        modifier = modifier
            .width(80.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceXs)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = achievement.type.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .alpha(contentAlpha),
                tint = if (achievement.isUnlocked)
                    MaterialTheme.colorScheme.onTertiaryContainer
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!achievement.isUnlocked && achievement.progress > 0) {
                CircularProgressIndicator(
                    progress = { achievement.progress },
                    modifier = Modifier.size(56.dp),
                    strokeWidth = 3.dp,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
            }
        }

        Text(
            text = achievement.title,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.alpha(contentAlpha)
        )
    }
}

private val AchievementType.icon: ImageVector
    get() = when (this) {
        AchievementType.STREAK_3_DAYS,
        AchievementType.STREAK_7_DAYS,
        AchievementType.STREAK_30_DAYS -> Icons.Default.LocalFireDepartment

        AchievementType.BLOCKED_100_ATTEMPTS,
        AchievementType.BLOCKED_500_ATTEMPTS,
        AchievementType.BLOCKED_1000_ATTEMPTS -> Icons.Default.Shield

        AchievementType.SAVED_1_HOUR,
        AchievementType.SAVED_10_HOURS,
        AchievementType.SAVED_24_HOURS -> Icons.Default.Schedule

        AchievementType.FIRST_NFC_SCAN -> Icons.Default.Nfc
        AchievementType.FIRST_QR_SCAN -> Icons.Default.QrCode
        AchievementType.CREATED_3_PROFILES -> Icons.Default.Folder
    }
```

---

## 7. Testing Strategy

### 7.1 Unit Tests

```kotlin
// test/StatsAnalyzerTest.kt
class StatsAnalyzerTest {

    private lateinit var analyzer: StatsAnalyzer
    private lateinit var repository: StatsRepository

    @Before
    fun setup() {
        repository = mockk()
        analyzer = StatsAnalyzerImpl(repository, TimeEstimateConfig())
    }

    @Test
    fun `getTodaySummary returns correct aggregation`() = runTest {
        val today = LocalDate.now()
        val attempts = listOf(
            BlockedAttempt("1", "com.twitter", "Twitter", "p1", now(), 1, 10),
            BlockedAttempt("2", "com.twitter", "Twitter", "p1", now(), 1, 11),
            BlockedAttempt("3", "com.instagram", "Instagram", "p1", now(), 1, 10)
        )

        coEvery { repository.getBlockedAttemptsCount(any(), any()) } returns 3
        coEvery { repository.getBlockedAttemptsByApp(any(), any()) } returns mapOf(
            "com.twitter" to 2,
            "com.instagram" to 1
        )

        val summary = analyzer.getTodaySummary()

        assertEquals(3, summary.totalBlockedAttempts)
        assertEquals("com.twitter", summary.topBlockedApp)
        assertEquals(2, summary.topBlockedAppCount)
    }

    @Test
    fun `calculateTrend returns negative for improvement`() = runTest {
        // Esta semana: 10 intentos, semana pasada: 20 intentos
        coEvery { repository.getBlockedAttemptsCount(any(), any()) } returnsMany listOf(10, 20)

        val trend = analyzer.calculateTrend(
            StatsPeriod.LAST_7_DAYS,
            StatsPeriod.LAST_7_DAYS  // Shifted
        )

        assertTrue(trend < 0) // Mejora = valor negativo
    }
}
```

---

## 8. Criterios de Aceptación

### 8.1 Tracking
- [ ] Cada intento bloqueado se registra con timestamp
- [ ] Sesiones se inician/finalizan correctamente
- [ ] Datos persisten entre reinicios de app
- [ ] No hay pérdida de datos en crash

### 8.2 Análisis
- [ ] Resúmenes diarios/semanales/mensuales correctos
- [ ] Tendencias calculadas comparando períodos
- [ ] Top apps ordenadas correctamente
- [ ] Distribución horaria precisa

### 8.3 Visualización
- [ ] Gráficas renderizan a 60fps
- [ ] Datos se actualizan en tiempo real
- [ ] Período selector funciona
- [ ] Empty states claros

### 8.4 Exportación
- [ ] CSV válido y parseable
- [ ] JSON bien formado
- [ ] Share intent funciona

### 8.5 Gamificación
- [ ] Rachas calculadas correctamente
- [ ] Logros se desbloquean al cumplir criterio
- [ ] Progreso se muestra para logros parciales
- [ ] Notificación al desbloquear logro

---

## 9. Dependencias

```kotlin
// build.gradle.kts
dependencies {
    // Room ya incluido en otros módulos

    // Charts (opcional - puede usar Canvas nativo)
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // Date/Time
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")

    // CSV Export
    implementation("com.opencsv:opencsv:5.9")
}
```

---

**Creado:** 2026-01-03
**Autor/Mantenedor:** Equipo Umbral
