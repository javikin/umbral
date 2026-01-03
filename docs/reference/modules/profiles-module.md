# Especificación: Módulo Profiles

**Estado:** ✅ Completo
**Última actualización:** 2026-01-03
**Versión:** 1.0

---

## 1. Overview

### 1.1 Propósito

El módulo de Profiles gestiona los **perfiles de bloqueo** que definen qué aplicaciones serán bloqueadas en cada contexto (trabajo, estudio, casa, etc.). Permite a los usuarios crear configuraciones personalizadas y reutilizables.

### 1.2 Responsabilidades

| Responsabilidad | Descripción |
|-----------------|-------------|
| **CRUD Perfiles** | Crear, leer, actualizar, eliminar perfiles |
| **Gestión de apps** | Agregar/quitar apps de perfiles |
| **Whitelist global** | Apps siempre permitidas en todos los perfiles |
| **Perfiles predefinidos** | Plantillas listas para usar |
| **Asociación NFC/QR** | Vincular perfiles a tags/códigos |
| **Configuración por perfil** | Timer default, strict mode, etc. |

### 1.3 Alcance

**Incluye:**
- Perfiles personalizados ilimitados
- 3 perfiles predefinidos (Social, Juegos, Noticias)
- Whitelist global de apps esenciales
- Iconos y colores personalizables
- Timer default por perfil

**No incluye:**
- Sincronización en la nube (V1)
- Perfiles compartidos entre usuarios
- Perfiles basados en ubicación (futuro)
- Perfiles basados en horario (futuro)

---

## 2. Arquitectura

### 2.1 Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────┐
│                     Profiles Module                          │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌─────────────────┐    ┌──────────────────┐                │
│  │ ProfilesManager │───▶│ProfilesRepository│                │
│  └─────────────────┘    └──────────────────┘                │
│          │                      │                            │
│          │                      ▼                            │
│          │              ┌──────────────────┐                │
│          │              │   ProfileDao     │                │
│          │              └──────────────────┘                │
│          │                      │                            │
│          ▼                      ▼                            │
│  ┌─────────────────┐    ┌──────────────────┐                │
│  │ WhitelistManager│    │  Room Database   │                │
│  └─────────────────┘    │ (ProfileEntity,  │                │
│          │              │  ProfileAppEntity,│                │
│          ▼              │  WhitelistEntity) │                │
│  ┌─────────────────┐    └──────────────────┘                │
│  │  AppsProvider   │                                         │
│  │ (PackageManager)│                                         │
│  └─────────────────┘                                         │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
        ┌──────────┐   ┌──────────┐   ┌──────────┐
        │ Blocking │   │    NFC   │   │   UI     │
        │ Module   │   │  Module  │   │  Module  │
        └──────────┘   └──────────┘   └──────────┘
```

### 2.2 Componentes

| Componente | Tipo | Responsabilidad |
|------------|------|-----------------|
| `ProfilesManager` | Singleton | API pública para perfiles |
| `ProfilesRepository` | Repository | Persistencia y queries |
| `WhitelistManager` | Singleton | Gestión de whitelist global |
| `AppsProvider` | Helper | Lista apps instaladas del sistema |
| `ProfileDao` | DAO | Operaciones de base de datos |

### 2.3 Dependencias

```kotlin
// build.gradle.kts (app)
dependencies {
    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")

    // Kotlinx Serialization (para JSON de apps)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
}
```

---

## 3. Modelos de Datos

### 3.1 Profile (Domain Model)

```kotlin
/**
 * Perfil de bloqueo que define qué apps bloquear.
 *
 * @property id UUID único del perfil
 * @property name Nombre visible del perfil
 * @property description Descripción opcional
 * @property icon Identificador del icono
 * @property color Color accent del perfil
 * @property blockedApps Package names de apps a bloquear
 * @property isPreset Si es un perfil predefinido (no eliminable)
 * @property defaultTimerMinutes Timer por defecto al activar (null = sin timer)
 * @property requirePhysicalUnlock Si requiere tag NFC/QR para desbloquear
 * @property createdAt Fecha de creación
 * @property lastUsedAt Última vez que se usó
 * @property useCount Veces que se ha activado
 */
data class Profile(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String? = null,
    val icon: ProfileIcon = ProfileIcon.BLOCK,
    val color: ProfileColor = ProfileColor.BLUE,
    val blockedApps: Set<String> = emptySet(),
    val isPreset: Boolean = false,
    val defaultTimerMinutes: Int? = null,
    val requirePhysicalUnlock: Boolean = false,
    val createdAt: Instant = Instant.now(),
    val lastUsedAt: Instant? = null,
    val useCount: Int = 0
) {
    val appCount: Int get() = blockedApps.size
}
```

### 3.2 ProfileIcon (Enum)

```kotlin
/**
 * Iconos disponibles para perfiles.
 */
enum class ProfileIcon(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
) {
    BLOCK(R.drawable.ic_block, R.string.icon_block),
    SOCIAL(R.drawable.ic_social, R.string.icon_social),
    GAMES(R.drawable.ic_games, R.string.icon_games),
    NEWS(R.drawable.ic_news, R.string.icon_news),
    WORK(R.drawable.ic_work, R.string.icon_work),
    STUDY(R.drawable.ic_study, R.string.icon_study),
    HOME(R.drawable.ic_home, R.string.icon_home),
    SLEEP(R.drawable.ic_sleep, R.string.icon_sleep),
    FOCUS(R.drawable.ic_focus, R.string.icon_focus),
    CUSTOM(R.drawable.ic_custom, R.string.icon_custom)
}
```

### 3.3 ProfileColor (Enum)

```kotlin
/**
 * Colores disponibles para perfiles.
 */
enum class ProfileColor(
    val lightColor: Color,
    val darkColor: Color
) {
    BLUE(Color(0xFF2196F3), Color(0xFF64B5F6)),
    RED(Color(0xFFF44336), Color(0xFFE57373)),
    GREEN(Color(0xFF4CAF50), Color(0xFF81C784)),
    ORANGE(Color(0xFFFF9800), Color(0xFFFFB74D)),
    PURPLE(Color(0xFF9C27B0), Color(0xFFBA68C8)),
    TEAL(Color(0xFF009688), Color(0xFF4DB6AC)),
    PINK(Color(0xFFE91E63), Color(0xFFF06292)),
    INDIGO(Color(0xFF3F51B5), Color(0xFF7986CB))
}
```

### 3.4 InstalledApp (Domain Model)

```kotlin
/**
 * Representa una app instalada en el dispositivo.
 */
data class InstalledApp(
    val packageName: String,
    val name: String,
    val icon: Drawable,
    val category: AppCategory,
    val isSystemApp: Boolean,
    val lastUpdated: Instant
) {
    companion object {
        /**
         * Apps del sistema que siempre están en whitelist por defecto.
         */
        val SYSTEM_WHITELIST = setOf(
            "com.android.dialer",
            "com.google.android.dialer",
            "com.samsung.android.dialer",
            "com.android.messaging",
            "com.google.android.apps.messaging",
            "com.samsung.android.messaging",
            "com.android.camera",
            "com.google.android.camera",
            "com.android.settings",
            "com.google.android.calendar",
            "com.android.calendar"
        )
    }
}

enum class AppCategory(
    @StringRes val labelRes: Int
) {
    SOCIAL(R.string.category_social),
    GAMES(R.string.category_games),
    NEWS(R.string.category_news),
    ENTERTAINMENT(R.string.category_entertainment),
    PRODUCTIVITY(R.string.category_productivity),
    COMMUNICATION(R.string.category_communication),
    OTHER(R.string.category_other)
}
```

### 3.5 Room Entities

```kotlin
@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "icon")
    val icon: String, // ProfileIcon name

    @ColumnInfo(name = "color")
    val color: String, // ProfileColor name

    @ColumnInfo(name = "is_preset")
    val isPreset: Boolean,

    @ColumnInfo(name = "default_timer_minutes")
    val defaultTimerMinutes: Int?,

    @ColumnInfo(name = "require_physical_unlock")
    val requirePhysicalUnlock: Boolean,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: Long?,

    @ColumnInfo(name = "use_count")
    val useCount: Int
)

@Entity(
    tableName = "profile_apps",
    primaryKeys = ["profile_id", "package_name"],
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("profile_id")]
)
data class ProfileAppEntity(
    @ColumnInfo(name = "profile_id")
    val profileId: String,

    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "added_at")
    val addedAt: Long
)

@Entity(tableName = "whitelist")
data class WhitelistEntity(
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "is_system")
    val isSystem: Boolean, // Si fue agregada automáticamente

    @ColumnInfo(name = "added_at")
    val addedAt: Long
)
```

---

## 4. Interfaces Públicas

### 4.1 ProfilesManager Interface

```kotlin
/**
 * API pública para gestión de perfiles.
 */
interface ProfilesManager {

    /**
     * Todos los perfiles como Flow reactivo.
     */
    val profiles: Flow<List<Profile>>

    /**
     * Perfil activo actualmente (null si no hay bloqueo).
     */
    val activeProfile: StateFlow<Profile?>

    /**
     * Obtiene un perfil por ID.
     */
    suspend fun getById(id: String): Profile?

    /**
     * Obtiene perfiles predefinidos.
     */
    suspend fun getPresets(): List<Profile>

    /**
     * Crea un nuevo perfil.
     */
    suspend fun create(profile: Profile): Result<Profile>

    /**
     * Actualiza un perfil existente.
     */
    suspend fun update(profile: Profile): Result<Unit>

    /**
     * Elimina un perfil (no permite eliminar presets).
     */
    suspend fun delete(id: String): Result<Unit>

    /**
     * Duplica un perfil existente.
     */
    suspend fun duplicate(id: String, newName: String): Result<Profile>

    /**
     * Agrega apps a un perfil.
     */
    suspend fun addApps(profileId: String, packageNames: Set<String>): Result<Unit>

    /**
     * Quita apps de un perfil.
     */
    suspend fun removeApps(profileId: String, packageNames: Set<String>): Result<Unit>

    /**
     * Registra uso de un perfil (incrementa contador, actualiza lastUsed).
     */
    suspend fun recordUsage(profileId: String): Result<Unit>

    /**
     * Obtiene el perfil más usado.
     */
    suspend fun getMostUsed(): Profile?

    /**
     * Obtiene el último perfil usado.
     */
    suspend fun getLastUsed(): Profile?

    /**
     * Busca perfiles por nombre.
     */
    fun search(query: String): Flow<List<Profile>>
}
```

### 4.2 WhitelistManager Interface

```kotlin
/**
 * Gestiona la whitelist global de apps siempre permitidas.
 */
interface WhitelistManager {

    /**
     * Apps en whitelist como Flow.
     */
    val whitelist: Flow<Set<String>>

    /**
     * Verifica si una app está en whitelist.
     */
    suspend fun isWhitelisted(packageName: String): Boolean

    /**
     * Agrega app a whitelist.
     */
    suspend fun add(packageName: String): Result<Unit>

    /**
     * Quita app de whitelist (no permite quitar apps del sistema).
     */
    suspend fun remove(packageName: String): Result<Unit>

    /**
     * Resetea whitelist a defaults del sistema.
     */
    suspend fun resetToDefaults(): Result<Unit>

    /**
     * Obtiene apps del sistema que están en whitelist por defecto.
     */
    fun getSystemDefaults(): Set<String>
}
```

### 4.3 AppsProvider Interface

```kotlin
/**
 * Provee información sobre apps instaladas.
 */
interface AppsProvider {

    /**
     * Todas las apps instaladas.
     */
    fun getInstalledApps(): Flow<List<InstalledApp>>

    /**
     * Apps por categoría.
     */
    fun getAppsByCategory(category: AppCategory): Flow<List<InstalledApp>>

    /**
     * Busca apps por nombre.
     */
    fun search(query: String): Flow<List<InstalledApp>>

    /**
     * Obtiene info de una app específica.
     */
    suspend fun getApp(packageName: String): InstalledApp?

    /**
     * Obtiene solo apps no del sistema.
     */
    fun getUserApps(): Flow<List<InstalledApp>>

    /**
     * Apps sugeridas para bloquear (redes sociales, juegos).
     */
    suspend fun getSuggestedApps(): List<InstalledApp>

    /**
     * Detecta categoría de una app.
     */
    suspend fun detectCategory(packageName: String): AppCategory
}
```

### 4.4 ProfileDao

```kotlin
@Dao
interface ProfileDao {

    @Query("SELECT * FROM profiles ORDER BY last_used_at DESC")
    fun getAll(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :id")
    suspend fun getById(id: String): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE is_preset = 1")
    suspend fun getPresets(): List<ProfileEntity>

    @Query("SELECT * FROM profiles ORDER BY use_count DESC LIMIT 1")
    suspend fun getMostUsed(): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE last_used_at IS NOT NULL ORDER BY last_used_at DESC LIMIT 1")
    suspend fun getLastUsed(): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE name LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<ProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfileEntity)

    @Update
    suspend fun update(profile: ProfileEntity)

    @Query("DELETE FROM profiles WHERE id = :id AND is_preset = 0")
    suspend fun delete(id: String): Int

    @Query("UPDATE profiles SET use_count = use_count + 1, last_used_at = :timestamp WHERE id = :id")
    suspend fun recordUsage(id: String, timestamp: Long)

    // Profile Apps

    @Query("SELECT package_name FROM profile_apps WHERE profile_id = :profileId")
    fun getAppsForProfile(profileId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addApps(apps: List<ProfileAppEntity>)

    @Query("DELETE FROM profile_apps WHERE profile_id = :profileId AND package_name IN (:packageNames)")
    suspend fun removeApps(profileId: String, packageNames: List<String>)

    @Query("DELETE FROM profile_apps WHERE profile_id = :profileId")
    suspend fun removeAllApps(profileId: String)

    // Whitelist

    @Query("SELECT package_name FROM whitelist")
    fun getWhitelist(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM whitelist WHERE package_name = :packageName)")
    suspend fun isWhitelisted(packageName: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToWhitelist(entity: WhitelistEntity)

    @Query("DELETE FROM whitelist WHERE package_name = :packageName AND is_system = 0")
    suspend fun removeFromWhitelist(packageName: String): Int

    @Query("DELETE FROM whitelist WHERE is_system = 0")
    suspend fun clearUserWhitelist()
}
```

---

## 5. Perfiles Predefinidos

### 5.1 Preset Definitions

```kotlin
/**
 * Generador de perfiles predefinidos.
 */
object PresetProfiles {

    fun createPresets(): List<Profile> = listOf(
        createSocialProfile(),
        createGamesProfile(),
        createNewsProfile()
    )

    private fun createSocialProfile() = Profile(
        id = "preset_social",
        name = "Redes Sociales",
        description = "Bloquea las principales redes sociales",
        icon = ProfileIcon.SOCIAL,
        color = ProfileColor.BLUE,
        blockedApps = setOf(
            "com.instagram.android",
            "com.facebook.katana",
            "com.facebook.orca", // Messenger
            "com.twitter.android",
            "com.zhiliaoapp.musically", // TikTok
            "com.snapchat.android",
            "com.linkedin.android",
            "com.pinterest",
            "com.reddit.frontpage",
            "com.tumblr",
            "com.bereal.ft"
        ),
        isPreset = true,
        defaultTimerMinutes = 60
    )

    private fun createGamesProfile() = Profile(
        id = "preset_games",
        name = "Juegos",
        description = "Bloquea aplicaciones de juegos",
        icon = ProfileIcon.GAMES,
        color = ProfileColor.RED,
        blockedApps = emptySet(), // Se llena dinámicamente con apps de categoría Games
        isPreset = true,
        defaultTimerMinutes = 120
    )

    private fun createNewsProfile() = Profile(
        id = "preset_news",
        name = "Noticias",
        description = "Bloquea apps de noticias y feeds",
        icon = ProfileIcon.NEWS,
        color = ProfileColor.ORANGE,
        blockedApps = setOf(
            "flipboard.app",
            "com.google.android.apps.magazines",
            "com.apple.news",
            "com.cnn.mobile.android.phone",
            "com.foxnews.android",
            "com.nytimes.android",
            "com.washingtonpost.rainbow",
            "com.guardian"
        ),
        isPreset = true,
        defaultTimerMinutes = 30
    )
}
```

### 5.2 Dynamic Preset Population

```kotlin
/**
 * Llena perfiles predefinidos con apps instaladas.
 */
class PresetPopulator @Inject constructor(
    private val appsProvider: AppsProvider,
    private val profilesManager: ProfilesManager
) {

    /**
     * Actualiza preset de juegos con apps instaladas de categoría Games.
     */
    suspend fun populateGamesPreset() {
        val gameApps = appsProvider.getAppsByCategory(AppCategory.GAMES).first()
        val packageNames = gameApps.map { it.packageName }.toSet()

        profilesManager.addApps("preset_games", packageNames)
    }

    /**
     * Detecta nuevas apps instaladas y sugiere agregarlas a presets.
     */
    suspend fun detectNewApps(): List<AppSuggestion> {
        val suggestions = mutableListOf<AppSuggestion>()

        val socialPreset = profilesManager.getById("preset_social")
        val installedSocialApps = appsProvider.getAppsByCategory(AppCategory.SOCIAL).first()

        installedSocialApps.forEach { app ->
            if (app.packageName !in socialPreset!!.blockedApps) {
                suggestions.add(AppSuggestion(
                    app = app,
                    suggestedProfileId = "preset_social"
                ))
            }
        }

        return suggestions
    }
}

data class AppSuggestion(
    val app: InstalledApp,
    val suggestedProfileId: String
)
```

---

## 6. Flujos Detallados

### 6.1 Flujo: Crear Perfil

```
┌─────────┐     ┌──────────────┐     ┌─────────────────┐     ┌──────────┐
│  USER   │     │  ViewModel   │     │ ProfilesManager │     │   DAO    │
└────┬────┘     └──────┬───────┘     └────────┬────────┘     └────┬─────┘
     │                 │                       │                   │
     │  Tap "Nuevo"    │                       │                   │
     │────────────────▶│                       │                   │
     │                 │                       │                   │
     │  Fill form      │                       │                   │
     │────────────────▶│                       │                   │
     │                 │                       │                   │
     │  Tap "Guardar"  │                       │                   │
     │────────────────▶│                       │                   │
     │                 │                       │                   │
     │                 │ validate()            │                   │
     │                 │ (name not empty,      │                   │
     │                 │  at least 1 app)      │                   │
     │                 │                       │                   │
     │                 │  create(profile)      │                   │
     │                 │──────────────────────▶│                   │
     │                 │                       │                   │
     │                 │                       │  insert()         │
     │                 │                       │──────────────────▶│
     │                 │                       │                   │
     │                 │                       │  addApps()        │
     │                 │                       │──────────────────▶│
     │                 │                       │                   │
     │                 │   Result.Success      │                   │
     │                 │◀──────────────────────│                   │
     │                 │                       │                   │
     │  Show success   │                       │                   │
     │  Navigate back  │                       │                   │
     │◀────────────────│                       │                   │
     │                 │                       │                   │
```

### 6.2 Flujo: Seleccionar Apps

```
┌─────────┐     ┌──────────────┐     ┌─────────────────┐
│  USER   │     │  ViewModel   │     │  AppsProvider   │
└────┬────┘     └──────┬───────┘     └────────┬────────┘
     │                 │                       │
     │  Tap "Add apps" │                       │
     │────────────────▶│                       │
     │                 │                       │
     │                 │ getInstalledApps()    │
     │                 │──────────────────────▶│
     │                 │                       │
     │                 │   List<InstalledApp>  │
     │                 │◀──────────────────────│
     │                 │                       │
     │   Show app list │                       │
     │◀────────────────│                       │
     │                 │                       │
     │  Search "insta" │                       │
     │────────────────▶│                       │
     │                 │                       │
     │                 │ search("insta")       │
     │                 │──────────────────────▶│
     │                 │                       │
     │                 │   Filtered list       │
     │                 │◀──────────────────────│
     │                 │                       │
     │   Show filtered │                       │
     │◀────────────────│                       │
     │                 │                       │
     │  Tap Instagram  │                       │
     │────────────────▶│                       │
     │                 │                       │
     │                 │ toggleSelection()     │
     │                 │                       │
     │   Update UI     │                       │
     │◀────────────────│                       │
     │                 │                       │
```

### 6.3 Flujo: Gestionar Whitelist

```
┌─────────┐     ┌──────────────┐     ┌──────────────────┐
│  USER   │     │  ViewModel   │     │ WhitelistManager │
└────┬────┘     └──────┬───────┘     └────────┬─────────┘
     │                 │                       │
     │  Open Settings  │                       │
     │────────────────▶│                       │
     │                 │                       │
     │  Tap Whitelist  │                       │
     │────────────────▶│                       │
     │                 │                       │
     │                 │ whitelist.collect()   │
     │                 │──────────────────────▶│
     │                 │                       │
     │                 │   Set<String>         │
     │                 │◀──────────────────────│
     │                 │                       │
     │   Show list     │                       │
     │   (with system  │                       │
     │    apps marked) │                       │
     │◀────────────────│                       │
     │                 │                       │
     │  Tap "Add"      │                       │
     │────────────────▶│                       │
     │                 │                       │
     │  Select app     │                       │
     │────────────────▶│                       │
     │                 │                       │
     │                 │ add(packageName)      │
     │                 │──────────────────────▶│
     │                 │                       │
     │                 │   Result.Success      │
     │                 │◀──────────────────────│
     │                 │                       │
     │   Update list   │                       │
     │◀────────────────│                       │
     │                 │                       │
```

---

## 7. Validaciones

### 7.1 Profile Validation

```kotlin
/**
 * Validador de perfiles.
 */
object ProfileValidator {

    sealed class ValidationError(val messageResId: Int) {
        object EmptyName : ValidationError(R.string.error_profile_name_empty)
        object NameTooLong : ValidationError(R.string.error_profile_name_too_long)
        object NoApps : ValidationError(R.string.error_profile_no_apps)
        object DuplicateName : ValidationError(R.string.error_profile_duplicate_name)
        object CannotDeletePreset : ValidationError(R.string.error_cannot_delete_preset)
        object CannotEditPresetName : ValidationError(R.string.error_cannot_edit_preset_name)
    }

    fun validate(profile: Profile): List<ValidationError> {
        val errors = mutableListOf<ValidationError>()

        if (profile.name.isBlank()) {
            errors.add(ValidationError.EmptyName)
        }

        if (profile.name.length > MAX_NAME_LENGTH) {
            errors.add(ValidationError.NameTooLong)
        }

        if (profile.blockedApps.isEmpty()) {
            errors.add(ValidationError.NoApps)
        }

        return errors
    }

    suspend fun validateCreate(
        profile: Profile,
        existingProfiles: List<Profile>
    ): List<ValidationError> {
        val errors = validate(profile).toMutableList()

        if (existingProfiles.any { it.name.equals(profile.name, ignoreCase = true) }) {
            errors.add(ValidationError.DuplicateName)
        }

        return errors
    }

    fun validateDelete(profile: Profile): ValidationError? {
        return if (profile.isPreset) {
            ValidationError.CannotDeletePreset
        } else {
            null
        }
    }

    companion object {
        const val MAX_NAME_LENGTH = 50
    }
}
```

### 7.2 Whitelist Validation

```kotlin
/**
 * Validador de whitelist.
 */
object WhitelistValidator {

    sealed class ValidationError(val messageResId: Int) {
        object CannotRemoveSystemApp : ValidationError(R.string.error_cannot_remove_system_whitelist)
        object AppNotInstalled : ValidationError(R.string.error_app_not_installed)
        object AlreadyWhitelisted : ValidationError(R.string.error_already_whitelisted)
    }

    fun validateRemove(
        packageName: String,
        systemDefaults: Set<String>
    ): ValidationError? {
        return if (packageName in systemDefaults) {
            ValidationError.CannotRemoveSystemApp
        } else {
            null
        }
    }
}
```

---

## 8. Sincronización de Apps

### 8.1 App Uninstall Handler

```kotlin
/**
 * Maneja cuando una app es desinstalada del dispositivo.
 */
@AndroidEntryPoint
class AppUninstallReceiver : BroadcastReceiver() {

    @Inject
    lateinit var profilesManager: ProfilesManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_PACKAGE_REMOVED) {
            val packageName = intent.data?.schemeSpecificPart ?: return

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    profilesManager.removeAppFromAllProfiles(packageName)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
```

### 8.2 App Category Detection

```kotlin
/**
 * Detecta la categoría de una app usando Play Store metadata.
 */
class AppCategoryDetector @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val packageManager = context.packageManager

    /**
     * Detecta categoría basándose en ApplicationInfo.category (API 26+)
     * y heurísticas de package name.
     */
    fun detectCategory(packageName: String): AppCategory {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val appInfo = packageManager.getApplicationInfo(packageName, 0)
                return when (appInfo.category) {
                    ApplicationInfo.CATEGORY_SOCIAL -> AppCategory.SOCIAL
                    ApplicationInfo.CATEGORY_GAME -> AppCategory.GAMES
                    ApplicationInfo.CATEGORY_NEWS -> AppCategory.NEWS
                    ApplicationInfo.CATEGORY_VIDEO,
                    ApplicationInfo.CATEGORY_AUDIO,
                    ApplicationInfo.CATEGORY_IMAGE -> AppCategory.ENTERTAINMENT
                    ApplicationInfo.CATEGORY_PRODUCTIVITY -> AppCategory.PRODUCTIVITY
                    else -> detectByPackageName(packageName)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                return AppCategory.OTHER
            }
        }

        return detectByPackageName(packageName)
    }

    private fun detectByPackageName(packageName: String): AppCategory {
        return when {
            SOCIAL_PACKAGES.any { packageName.contains(it) } -> AppCategory.SOCIAL
            GAME_PACKAGES.any { packageName.contains(it) } -> AppCategory.GAMES
            NEWS_PACKAGES.any { packageName.contains(it) } -> AppCategory.NEWS
            else -> AppCategory.OTHER
        }
    }

    companion object {
        private val SOCIAL_PACKAGES = listOf(
            "instagram", "facebook", "twitter", "tiktok", "snapchat",
            "linkedin", "pinterest", "reddit", "tumblr", "whatsapp"
        )
        private val GAME_PACKAGES = listOf(
            "game", "games", "play", "puzzle", "arcade", "casino"
        )
        private val NEWS_PACKAGES = listOf(
            "news", "cnn", "bbc", "nytimes", "guardian", "flipboard"
        )
    }
}
```

---

## 9. Edge Cases

### 9.1 Edge Cases

| # | Caso | Comportamiento Esperado |
|---|------|------------------------|
| 1 | App desinstalada mientras está en perfil | Remover automáticamente de todos los perfiles |
| 2 | Perfil sin apps después de uninstall | Mantener perfil, mostrar warning al activar |
| 3 | Intentar eliminar perfil preset | Error, no permitido |
| 4 | Nombre de perfil duplicado | Error en validación |
| 5 | Nombre con solo espacios | Tratar como vacío, error |
| 6 | Perfil con 100+ apps | Permitir, pero optimizar UI |
| 7 | App en whitelist agregada a perfil | Prevalece whitelist, no se bloquea |
| 8 | Eliminar app de whitelist del sistema | No permitido |
| 9 | Profile activo se elimina | Primero detener bloqueo, luego eliminar |
| 10 | Cambiar apps de perfil activo | Actualizar bloqueo en tiempo real |

---

## 10. Testing Strategy

### 10.1 Unit Tests

```kotlin
class ProfilesManagerTest {

    private lateinit var profilesManager: ProfilesManagerImpl
    private lateinit var mockDao: ProfileDao

    @Before
    fun setup() {
        mockDao = mockk()
        profilesManager = ProfilesManagerImpl(
            dao = mockDao,
            dispatchers = TestDispatchers()
        )
    }

    @Test
    fun `create profile with valid data succeeds`() = runTest {
        // Arrange
        val profile = Profile(name = "Test", blockedApps = setOf("com.test"))
        coEvery { mockDao.insert(any()) } just Runs
        coEvery { mockDao.addApps(any()) } just Runs

        // Act
        val result = profilesManager.create(profile)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { mockDao.insert(any()) }
    }

    @Test
    fun `create profile with empty name fails`() = runTest {
        val profile = Profile(name = "", blockedApps = setOf("com.test"))

        val result = profilesManager.create(profile)

        assertTrue(result.isFailure)
    }

    @Test
    fun `delete preset profile fails`() = runTest {
        val preset = Profile(id = "preset_social", name = "Social", isPreset = true)
        coEvery { mockDao.getById("preset_social") } returns preset.toEntity()

        val result = profilesManager.delete("preset_social")

        assertTrue(result.isFailure)
    }

    @Test
    fun `addApps updates profile correctly`() = runTest {
        val profileId = "test_id"
        coEvery { mockDao.addApps(any()) } just Runs

        val result = profilesManager.addApps(profileId, setOf("com.new.app"))

        assertTrue(result.isSuccess)
        coVerify { mockDao.addApps(match { it.any { app -> app.packageName == "com.new.app" } }) }
    }
}

class WhitelistManagerTest {

    @Test
    fun `system apps cannot be removed from whitelist`() = runTest {
        val manager = WhitelistManagerImpl(mockDao)
        coEvery { mockDao.removeFromWhitelist(any()) } returns 0

        val result = manager.remove("com.android.dialer")

        assertTrue(result.isFailure)
    }
}
```

### 10.2 Manual Testing Checklist

| # | Test Case | Steps | Expected Result |
|---|-----------|-------|-----------------|
| 1 | Create profile | Fill form → Save | Profile in list |
| 2 | Edit profile | Open → Change → Save | Changes persisted |
| 3 | Delete profile | Long press → Delete | Removed from list |
| 4 | Cannot delete preset | Try to delete Social | Error message |
| 5 | Add apps | Open picker → Select → Save | Apps in profile |
| 6 | Search apps | Type "insta" | Instagram shown |
| 7 | Whitelist works | Add to whitelist → Block → Open | App opens |
| 8 | App uninstall | Uninstall app in profile | Removed automatically |
| 9 | Duplicate name | Create with existing name | Error shown |
| 10 | 50+ apps | Add many apps | Performance OK |

---

## 11. Criterios de Aceptación

### 11.1 Funcionales

- [ ] CRUD completo de perfiles
- [ ] Perfiles predefinidos no eliminables
- [ ] Apps se pueden agregar/quitar de perfiles
- [ ] Whitelist global funciona correctamente
- [ ] Apps del sistema en whitelist por defecto
- [ ] Búsqueda de apps funciona
- [ ] Apps desinstaladas se remueven automáticamente
- [ ] Validaciones previenen datos inválidos
- [ ] Iconos y colores personalizables
- [ ] Timer default configurable por perfil

### 11.2 No Funcionales

- [ ] Lista de apps carga en < 1 segundo
- [ ] Soporta 100+ perfiles
- [ ] Soporta 500+ apps instaladas
- [ ] Datos persisten entre reinstalaciones (backup)

### 11.3 UX

- [ ] Formulario de creación intuitivo
- [ ] Selección de apps con checkboxes claros
- [ ] Iconos representativos
- [ ] Colores distinguibles
- [ ] Mensajes de error claros en español

---

## 12. Dependencias con Otros Módulos

| Módulo | Tipo | Descripción |
|--------|------|-------------|
| `BlockingModule` | Output | Provee lista de apps a bloquear |
| `NfcModule` | Output | Perfil asociado a cada tag |
| `UIModule` | Output | Datos para pantallas de perfiles |
| `SettingsModule` | Input | Configuración global que afecta perfiles |

---

**Creado:** 2026-01-03
**Autor:** Oden Forge - Spec Writer
**Próximo:** ui-module.md
