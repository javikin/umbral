# Especificación: Módulo NFC

**Estado:** ✅ Completo
**Última actualización:** 2026-01-03
**Versión:** 1.0

---

## 1. Overview

### 1.1 Propósito

El módulo NFC es el **core diferenciador** de Umbral. Permite a los usuarios activar/desactivar el bloqueo de apps mediante el tap de un tag NFC físico, creando fricción real entre el usuario y sus apps distractoras.

### 1.2 Responsabilidades

| Responsabilidad | Descripción |
|-----------------|-------------|
| **Lectura de tags** | Detectar y leer tags NFC en foreground y background |
| **Escritura de tags** | Escribir identificador Umbral en tags NTAG |
| **Gestión de tags** | CRUD de tags registrados en la app |
| **Toggle de bloqueo** | Activar/desactivar bloqueo al detectar tag conocido |
| **Validación** | Verificar compatibilidad y autenticidad de tags |

### 1.3 Alcance

**Incluye:**
- Soporte para NTAG213, NTAG215, NTAG216
- Lectura en foreground (app abierta)
- Lectura en background (pantalla encendida, app cerrada)
- Escritura de mensajes NDEF
- Asociación tag-perfil

**No incluye:**
- Tags MIFARE Classic (legacy, inseguros)
- Lectura con pantalla apagada (limitación Android)
- Emulación HCE (Host Card Emulation)
- NFC peer-to-peer

---

## 2. Arquitectura

### 2.1 Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────┐
│                        NFC Module                            │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐  │
│  │  NfcManager  │───▶│  TagReader   │───▶│  TagWriter   │  │
│  └──────────────┘    └──────────────┘    └──────────────┘  │
│         │                   │                    │          │
│         ▼                   ▼                    ▼          │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐  │
│  │ NfcRepository│◀───│  NdefParser  │    │ NdefBuilder  │  │
│  └──────────────┘    └──────────────┘    └──────────────┘  │
│         │                                                    │
│         ▼                                                    │
│  ┌──────────────────────────────────────────────────────┐  │
│  │                    Room Database                       │  │
│  │                   (NfcTagEntity)                       │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                              │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
                    ┌──────────────────┐
                    │  BlockingModule  │
                    │   (toggle)       │
                    └──────────────────┘
```

### 2.2 Componentes

| Componente | Tipo | Responsabilidad |
|------------|------|-----------------|
| `NfcManager` | Singleton | Coordina operaciones NFC, lifecycle |
| `TagReader` | Class | Lee tags y parsea contenido NDEF |
| `TagWriter` | Class | Escribe mensajes NDEF a tags |
| `NdefParser` | Object | Parsea mensajes NDEF a modelos |
| `NdefBuilder` | Object | Construye mensajes NDEF desde modelos |
| `NfcRepository` | Repository | Persistencia y queries de tags |
| `NfcTagEntity` | Entity | Modelo de base de datos |

### 2.3 Dependencias

```kotlin
// build.gradle.kts (app)
dependencies {
    // Android NFC (incluido en SDK)
    // No requiere dependencias externas

    // Room para persistencia
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // Hilt para DI
    implementation("com.google.dagger:hilt-android:2.50")
    ksp("com.google.dagger:hilt-compiler:2.50")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

---

## 3. Modelos de Datos

### 3.1 NfcTag (Domain Model)

```kotlin
/**
 * Representa un tag NFC registrado en Umbral.
 *
 * @property id UUID único del tag en la app
 * @property uid Identificador físico del tag (7-10 bytes hex)
 * @property name Nombre dado por el usuario (ej: "Puerta Principal")
 * @property location Ubicación opcional (ej: "Casa", "Oficina")
 * @property profileId Perfil asociado al tag (nullable = usa último activo)
 * @property createdAt Fecha de registro
 * @property lastUsedAt Última vez que se usó el tag
 * @property useCount Veces que se ha usado
 */
data class NfcTag(
    val id: String = UUID.randomUUID().toString(),
    val uid: String,
    val name: String,
    val location: String? = null,
    val profileId: String? = null,
    val createdAt: Instant = Instant.now(),
    val lastUsedAt: Instant? = null,
    val useCount: Int = 0
)
```

### 3.2 NfcTagEntity (Room Entity)

```kotlin
@Entity(
    tableName = "nfc_tags",
    indices = [
        Index(value = ["uid"], unique = true),
        Index(value = ["profile_id"])
    ]
)
data class NfcTagEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "uid")
    val uid: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "location")
    val location: String?,

    @ColumnInfo(name = "profile_id")
    val profileId: String?,

    @ColumnInfo(name = "created_at")
    val createdAt: Long, // epoch millis

    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: Long?,

    @ColumnInfo(name = "use_count")
    val useCount: Int
)
```

### 3.3 NdefPayload (NDEF Message Model)

```kotlin
/**
 * Payload que se escribe/lee del tag NFC.
 * Formato: URI record con scheme "umbral://"
 *
 * Ejemplo: umbral://tag/v1/abc123def456
 */
data class NdefPayload(
    val version: Int = 1,
    val tagId: String, // ID único generado por Umbral
    val checksum: String // CRC32 para validación
) {
    companion object {
        const val SCHEME = "umbral"
        const val HOST = "tag"
        const val CURRENT_VERSION = 1

        fun toUri(payload: NdefPayload): String {
            return "$SCHEME://$HOST/v${payload.version}/${payload.tagId}?c=${payload.checksum}"
        }

        fun fromUri(uri: String): NdefPayload? {
            // Parse y validación
        }
    }
}
```

### 3.4 TagType (Enum)

```kotlin
/**
 * Tipos de tags NFC soportados.
 */
enum class TagType(
    val techName: String,
    val minMemory: Int,
    val maxMemory: Int,
    val supported: Boolean
) {
    NTAG213("NTAG213", 144, 144, true),
    NTAG215("NTAG215", 504, 504, true),
    NTAG216("NTAG216", 888, 888, true),
    MIFARE_ULTRALIGHT("MIFARE Ultralight", 64, 192, true),
    MIFARE_CLASSIC("MIFARE Classic", 1024, 4096, false), // Inseguro
    UNKNOWN("Unknown", 0, 0, false);

    companion object {
        fun fromTag(tag: Tag): TagType {
            val techList = tag.techList
            return when {
                techList.contains("android.nfc.tech.NfcA") -> {
                    // Detectar NTAG específico por SAK/ATQA
                    detectNtagType(tag)
                }
                techList.contains("android.nfc.tech.MifareClassic") -> MIFARE_CLASSIC
                techList.contains("android.nfc.tech.MifareUltralight") -> MIFARE_ULTRALIGHT
                else -> UNKNOWN
            }
        }
    }
}
```

### 3.5 NfcResult (Sealed Class)

```kotlin
/**
 * Resultado de operaciones NFC.
 */
sealed class NfcResult<out T> {
    data class Success<T>(val data: T) : NfcResult<T>()
    data class Error(val error: NfcError) : NfcResult<Nothing>()
}

enum class NfcError(val messageResId: Int) {
    // Hardware
    NFC_NOT_AVAILABLE(R.string.error_nfc_not_available),
    NFC_DISABLED(R.string.error_nfc_disabled),

    // Tag
    TAG_NOT_SUPPORTED(R.string.error_tag_not_supported),
    TAG_READ_ONLY(R.string.error_tag_read_only),
    TAG_TOO_SMALL(R.string.error_tag_too_small),
    TAG_LOST(R.string.error_tag_lost),
    TAG_IO_ERROR(R.string.error_tag_io),

    // Data
    INVALID_NDEF(R.string.error_invalid_ndef),
    INVALID_PAYLOAD(R.string.error_invalid_payload),
    CHECKSUM_MISMATCH(R.string.error_checksum_mismatch),

    // Write
    WRITE_FAILED(R.string.error_write_failed),
    TAG_ALREADY_REGISTERED(R.string.error_tag_already_registered),

    // General
    UNKNOWN_ERROR(R.string.error_unknown)
}
```

---

## 4. Interfaces Públicas

### 4.1 NfcManager Interface

```kotlin
/**
 * Punto de entrada principal para operaciones NFC.
 * Inyectado vía Hilt como Singleton.
 */
interface NfcManager {

    /**
     * Estado actual del hardware NFC.
     */
    val nfcState: StateFlow<NfcState>

    /**
     * Eventos de tags detectados.
     */
    val tagEvents: SharedFlow<TagEvent>

    /**
     * Verifica si NFC está disponible y habilitado.
     */
    fun isNfcAvailable(): Boolean

    /**
     * Abre configuración de NFC del sistema.
     */
    fun openNfcSettings(context: Context)

    /**
     * Habilita detección de tags en foreground.
     * Llamar en onResume() de Activity.
     */
    fun enableForegroundDispatch(activity: Activity)

    /**
     * Deshabilita detección de tags en foreground.
     * Llamar en onPause() de Activity.
     */
    fun disableForegroundDispatch(activity: Activity)

    /**
     * Procesa un Intent que contiene un tag NFC.
     * Llamar cuando Activity recibe Intent con ACTION_TAG_DISCOVERED.
     */
    suspend fun processTagIntent(intent: Intent): NfcResult<TagEvent>

    /**
     * Escribe payload de Umbral en un tag.
     * Tag debe estar presente y conectado.
     */
    suspend fun writeTag(tag: Tag, payload: NdefPayload): NfcResult<Unit>

    /**
     * Formatea y escribe un tag vacío.
     */
    suspend fun formatAndWriteTag(tag: Tag, payload: NdefPayload): NfcResult<Unit>
}

/**
 * Estado del hardware NFC.
 */
sealed class NfcState {
    object NotAvailable : NfcState()
    object Disabled : NfcState()
    object Enabled : NfcState()
    object Scanning : NfcState()
}

/**
 * Evento cuando se detecta un tag.
 */
sealed class TagEvent {
    data class KnownTag(val tag: NfcTag) : TagEvent()
    data class UnknownTag(val uid: String, val type: TagType, val androidTag: Tag) : TagEvent()
    data class InvalidTag(val error: NfcError) : TagEvent()
}
```

### 4.2 NfcRepository Interface

```kotlin
/**
 * Repositorio para persistencia de tags NFC.
 */
interface NfcRepository {

    /**
     * Obtiene todos los tags registrados.
     */
    fun getAllTags(): Flow<List<NfcTag>>

    /**
     * Obtiene un tag por su ID.
     */
    suspend fun getTagById(id: String): NfcTag?

    /**
     * Obtiene un tag por su UID físico.
     */
    suspend fun getTagByUid(uid: String): NfcTag?

    /**
     * Registra un nuevo tag.
     */
    suspend fun insertTag(tag: NfcTag): Result<Unit>

    /**
     * Actualiza un tag existente.
     */
    suspend fun updateTag(tag: NfcTag): Result<Unit>

    /**
     * Elimina un tag.
     */
    suspend fun deleteTag(id: String): Result<Unit>

    /**
     * Incrementa contador de uso y actualiza lastUsedAt.
     */
    suspend fun recordTagUsage(id: String): Result<Unit>

    /**
     * Obtiene tags por perfil asociado.
     */
    fun getTagsByProfile(profileId: String): Flow<List<NfcTag>>
}
```

### 4.3 TagReader Class

```kotlin
/**
 * Lee y parsea tags NFC.
 */
class TagReader @Inject constructor() {

    /**
     * Lee el contenido NDEF de un tag.
     *
     * @param tag Tag Android detectado
     * @return Payload de Umbral o null si no es tag de Umbral
     */
    suspend fun readTag(tag: Tag): NfcResult<NdefPayload?> {
        return withContext(Dispatchers.IO) {
            try {
                val ndef = Ndef.get(tag) ?: return@withContext NfcResult.Success(null)

                ndef.connect()
                try {
                    val message = ndef.ndefMessage ?: return@withContext NfcResult.Success(null)
                    val payload = NdefParser.parseMessage(message)
                    NfcResult.Success(payload)
                } finally {
                    ndef.close()
                }
            } catch (e: TagLostException) {
                NfcResult.Error(NfcError.TAG_LOST)
            } catch (e: IOException) {
                NfcResult.Error(NfcError.TAG_IO_ERROR)
            }
        }
    }

    /**
     * Obtiene información del tag sin leer NDEF.
     */
    fun getTagInfo(tag: Tag): TagInfo {
        return TagInfo(
            uid = tag.id.toHexString(),
            type = TagType.fromTag(tag),
            techList = tag.techList.toList(),
            isWritable = Ndef.get(tag)?.isWritable ?: false,
            maxSize = Ndef.get(tag)?.maxSize ?: 0
        )
    }
}

data class TagInfo(
    val uid: String,
    val type: TagType,
    val techList: List<String>,
    val isWritable: Boolean,
    val maxSize: Int
)
```

### 4.4 TagWriter Class

```kotlin
/**
 * Escribe contenido NDEF en tags NFC.
 */
class TagWriter @Inject constructor() {

    companion object {
        const val MIN_TAG_SIZE = 48 // bytes mínimos para payload Umbral
    }

    /**
     * Escribe payload de Umbral en un tag.
     */
    suspend fun writeTag(tag: Tag, payload: NdefPayload): NfcResult<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val ndef = Ndef.get(tag)

                if (ndef != null) {
                    // Tag ya tiene NDEF
                    writeToNdef(ndef, payload)
                } else {
                    // Tag virgen, intentar formatear
                    val ndefFormatable = NdefFormatable.get(tag)
                    if (ndefFormatable != null) {
                        formatAndWrite(ndefFormatable, payload)
                    } else {
                        NfcResult.Error(NfcError.TAG_NOT_SUPPORTED)
                    }
                }
            } catch (e: TagLostException) {
                NfcResult.Error(NfcError.TAG_LOST)
            } catch (e: IOException) {
                NfcResult.Error(NfcError.WRITE_FAILED)
            }
        }
    }

    private suspend fun writeToNdef(ndef: Ndef, payload: NdefPayload): NfcResult<Unit> {
        if (!ndef.isWritable) {
            return NfcResult.Error(NfcError.TAG_READ_ONLY)
        }

        val message = NdefBuilder.buildMessage(payload)
        if (ndef.maxSize < message.byteArrayLength) {
            return NfcResult.Error(NfcError.TAG_TOO_SMALL)
        }

        ndef.connect()
        try {
            ndef.writeNdefMessage(message)
            return NfcResult.Success(Unit)
        } finally {
            ndef.close()
        }
    }

    private suspend fun formatAndWrite(
        formatable: NdefFormatable,
        payload: NdefPayload
    ): NfcResult<Unit> {
        val message = NdefBuilder.buildMessage(payload)

        formatable.connect()
        try {
            formatable.format(message)
            return NfcResult.Success(Unit)
        } finally {
            formatable.close()
        }
    }
}
```

---

## 5. Máquina de Estados

### 5.1 NFC Session State Machine

```
                                    ┌─────────────────────┐
                                    │                     │
                                    ▼                     │
┌──────────┐   NFC enabled    ┌──────────┐   tag detected  │
│          │─────────────────▶│          │────────────────┘
│   IDLE   │                  │ SCANNING │
│          │◀─────────────────│          │
└──────────┘   NFC disabled   └──────────┘
     │                              │
     │                              │ valid tag
     │                              ▼
     │                        ┌──────────┐
     │                        │          │
     │                        │ READING  │
     │                        │          │
     │                        └──────────┘
     │                              │
     │              ┌───────────────┼───────────────┐
     │              │               │               │
     │              ▼               ▼               ▼
     │        ┌──────────┐   ┌──────────┐   ┌──────────┐
     │        │  KNOWN   │   │ UNKNOWN  │   │  ERROR   │
     │        │   TAG    │   │   TAG    │   │          │
     │        └──────────┘   └──────────┘   └──────────┘
     │              │               │               │
     │              │               │               │
     │              ▼               ▼               │
     │        ┌──────────┐   ┌──────────┐          │
     │        │  TOGGLE  │   │ REGISTER │          │
     │        │ BLOCKING │   │  PROMPT  │          │
     │        └──────────┘   └──────────┘          │
     │              │               │               │
     └──────────────┴───────────────┴───────────────┘
                         return to SCANNING
```

### 5.2 State Definitions

```kotlin
sealed class NfcSessionState {
    /**
     * NFC disponible pero no escaneando activamente.
     */
    object Idle : NfcSessionState()

    /**
     * Esperando detección de tag.
     */
    object Scanning : NfcSessionState()

    /**
     * Tag detectado, leyendo contenido.
     */
    data class Reading(val tagUid: String) : NfcSessionState()

    /**
     * Tag reconocido de Umbral.
     */
    data class KnownTag(val tag: NfcTag) : NfcSessionState()

    /**
     * Tag no reconocido (nuevo o de otra app).
     */
    data class UnknownTag(
        val uid: String,
        val type: TagType,
        val androidTag: Tag
    ) : NfcSessionState()

    /**
     * Error durante operación NFC.
     */
    data class Error(val error: NfcError) : NfcSessionState()

    /**
     * Ejecutando toggle de bloqueo.
     */
    data class TogglingBlock(val tag: NfcTag) : NfcSessionState()

    /**
     * Mostrando prompt para registrar tag.
     */
    data class RegisterPrompt(
        val uid: String,
        val type: TagType,
        val androidTag: Tag
    ) : NfcSessionState()
}
```

### 5.3 State Transitions

```kotlin
sealed class NfcAction {
    object EnableNfc : NfcAction()
    object DisableNfc : NfcAction()
    data class TagDetected(val intent: Intent) : NfcAction()
    data class TagRead(val payload: NdefPayload?) : NfcAction()
    data class TagRecognized(val tag: NfcTag) : NfcAction()
    data class TagUnknown(val uid: String, val type: TagType, val androidTag: Tag) : NfcAction()
    data class ReadError(val error: NfcError) : NfcAction()
    object ToggleComplete : NfcAction()
    object RegisterComplete : NfcAction()
    object Dismiss : NfcAction()
}

fun NfcSessionState.reduce(action: NfcAction): NfcSessionState {
    return when (this) {
        is Idle -> when (action) {
            is EnableNfc -> Scanning
            else -> this
        }
        is Scanning -> when (action) {
            is DisableNfc -> Idle
            is TagDetected -> Reading(extractUid(action.intent))
            else -> this
        }
        is Reading -> when (action) {
            is TagRecognized -> KnownTag(action.tag)
            is TagUnknown -> UnknownTag(action.uid, action.type, action.androidTag)
            is ReadError -> Error(action.error)
            else -> this
        }
        is KnownTag -> when (action) {
            is ToggleComplete -> Scanning
            is Dismiss -> Scanning
            else -> TogglingBlock(tag)
        }
        is UnknownTag -> when (action) {
            is RegisterComplete -> Scanning
            is Dismiss -> Scanning
            else -> RegisterPrompt(uid, type, androidTag)
        }
        is Error -> when (action) {
            is Dismiss -> Scanning
            else -> this
        }
        else -> this
    }
}
```

---

## 6. Flujos Detallados

### 6.1 Flujo: Detección de Tag en Foreground

```
┌─────────┐     ┌──────────┐     ┌───────────┐     ┌────────────┐
│  USER   │     │ ACTIVITY │     │ NfcManager │     │ TagReader  │
└────┬────┘     └────┬─────┘     └─────┬─────┘     └──────┬─────┘
     │               │                  │                  │
     │  Tap NFC tag  │                  │                  │
     │──────────────▶│                  │                  │
     │               │                  │                  │
     │               │  onNewIntent()   │                  │
     │               │─────────────────▶│                  │
     │               │                  │                  │
     │               │                  │ processTagIntent()
     │               │                  │─────────────────▶│
     │               │                  │                  │
     │               │                  │   readTag()      │
     │               │                  │◀─────────────────│
     │               │                  │                  │
     │               │                  │   NdefPayload    │
     │               │                  │◀─────────────────│
     │               │                  │                  │
     │               │                  │ Check if known   │
     │               │                  │       tag        │
     │               │                  │                  │
     │               │  TagEvent.KnownTag                  │
     │               │◀─────────────────│                  │
     │               │                  │                  │
     │   Vibrate +   │                  │                  │
     │   Show UI     │                  │                  │
     │◀──────────────│                  │                  │
     │               │                  │                  │
```

### 6.2 Flujo: Escritura de Nuevo Tag

```
┌─────────┐     ┌──────────┐     ┌───────────┐     ┌────────────┐
│  USER   │     │ ViewModel │     │ NfcManager │     │ TagWriter  │
└────┬────┘     └────┬─────┘     └─────┬─────┘     └──────┬─────┘
     │               │                  │                  │
     │ "Add new tag" │                  │                  │
     │──────────────▶│                  │                  │
     │               │                  │                  │
     │               │ startWriteMode() │                  │
     │               │─────────────────▶│                  │
     │               │                  │                  │
     │ Show "Tap tag"│                  │                  │
     │◀──────────────│                  │                  │
     │               │                  │                  │
     │  Tap NFC tag  │                  │                  │
     │───────────────┼─────────────────▶│                  │
     │               │                  │                  │
     │               │  TagEvent.       │                  │
     │               │  UnknownTag      │                  │
     │               │◀─────────────────│                  │
     │               │                  │                  │
     │               │ Generate payload │                  │
     │               │ (UUID, checksum) │                  │
     │               │                  │                  │
     │               │  writeTag()      │                  │
     │               │─────────────────▶│                  │
     │               │                  │ writeTag()       │
     │               │                  │─────────────────▶│
     │               │                  │                  │
     │               │                  │   Result.Success │
     │               │                  │◀─────────────────│
     │               │                  │                  │
     │               │ Insert to DB     │                  │
     │               │                  │                  │
     │ "Tag saved!"  │                  │                  │
     │◀──────────────│                  │                  │
     │               │                  │                  │
```

### 6.3 Flujo: Toggle de Bloqueo

```
┌─────────┐     ┌───────────┐     ┌──────────────┐     ┌─────────────────┐
│  USER   │     │ NfcManager │     │ NfcRepository │     │ BlockingManager │
└────┬────┘     └─────┬─────┘     └───────┬──────┘     └────────┬────────┘
     │                │                    │                     │
     │  Tap known tag │                    │                     │
     │───────────────▶│                    │                     │
     │                │                    │                     │
     │                │ getTagByUid()      │                     │
     │                │───────────────────▶│                     │
     │                │                    │                     │
     │                │    NfcTag          │                     │
     │                │◀───────────────────│                     │
     │                │                    │                     │
     │                │ recordTagUsage()   │                     │
     │                │───────────────────▶│                     │
     │                │                    │                     │
     │                │ toggleBlocking(profileId)                │
     │                │──────────────────────────────────────────▶│
     │                │                    │                     │
     │                │                    │    BlockingState    │
     │                │◀──────────────────────────────────────────│
     │                │                    │                     │
     │   Vibrate +    │                    │                     │
     │   Notification │                    │                     │
     │◀───────────────│                    │                     │
     │                │                    │                     │
```

---

## 7. Integración con Android

### 7.1 Manifest Declarations

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Permisos NFC -->
    <uses-permission android:name="android.permission.NFC" />

    <!-- Requerir NFC para instalación -->
    <uses-feature
        android:name="android.hardware.nfc"
        android:required="false" />

    <application>

        <!-- Activity principal con intent filters para NFC -->
        <activity
            android:name=".ui.MainActivity"
            android:launchMode="singleTop"
            android:exported="true">

            <!-- Lanzador normal -->
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

            <!-- NFC Tag Discovery -->
            <intent-filter>
                <action android:name="android.nfc.action.NDEF_DISCOVERED" />
                <category android:name="android.intent.category.DEFAULT" />
                <data
                    android:scheme="umbral"
                    android:host="tag" />
            </intent-filter>

            <!-- Fallback para tags sin NDEF -->
            <intent-filter>
                <action android:name="android.nfc.action.TAG_DISCOVERED" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>

            <!-- Metadata para foreground dispatch -->
            <meta-data
                android:name="android.nfc.action.TECH_DISCOVERED"
                android:resource="@xml/nfc_tech_filter" />
        </activity>

    </application>
</manifest>
```

### 7.2 NFC Tech Filter (res/xml/nfc_tech_filter.xml)

```xml
<resources xmlns:xliff="urn:oasis:names:tc:xliff:document:1.2">
    <tech-list>
        <tech>android.nfc.tech.Ndef</tech>
    </tech-list>
    <tech-list>
        <tech>android.nfc.tech.NdefFormatable</tech>
    </tech-list>
    <tech-list>
        <tech>android.nfc.tech.NfcA</tech>
    </tech-list>
    <tech-list>
        <tech>android.nfc.tech.MifareUltralight</tech>
    </tech-list>
</resources>
```

### 7.3 Activity Integration

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var nfcManager: NfcManager

    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        // Procesar intent inicial si viene de NFC
        handleIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                nfcManager.enableForegroundDispatch(this)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.let {
            nfcManager.disableForegroundDispatch(this)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            NfcAdapter.ACTION_NDEF_DISCOVERED,
            NfcAdapter.ACTION_TECH_DISCOVERED,
            NfcAdapter.ACTION_TAG_DISCOVERED -> {
                lifecycleScope.launch {
                    nfcManager.processTagIntent(intent)
                }
            }
        }
    }
}
```

---

## 8. Edge Cases y Manejo de Errores

### 8.1 Edge Cases

| # | Caso | Comportamiento Esperado |
|---|------|------------------------|
| 1 | NFC deshabilitado en sistema | Mostrar diálogo con botón para abrir settings |
| 2 | Tag retirado durante lectura | Mostrar error "Tag perdido" y reintentar automático |
| 3 | Tag retirado durante escritura | Mostrar error "Escritura fallida", invitar a reintentar |
| 4 | Tag de solo lectura | Mostrar error específico, sugerir usar otro tag |
| 5 | Tag muy pequeño (<48 bytes) | Mostrar error específico, listar tags compatibles |
| 6 | Tag de otra app (con NDEF pero no Umbral) | Tratar como tag nuevo, ofrecer sobrescribir |
| 7 | Tag MIFARE Classic | Mostrar error "Tag no soportado por seguridad" |
| 8 | Múltiples tags cerca | Android maneja, solo procesa uno |
| 9 | Tag ya registrado (por UID) | Detectar, usar registro existente |
| 10 | Tag registrado en otro dispositivo | Funciona normal (ID en tag, no en device) |

### 8.2 Error Handling Strategy

```kotlin
/**
 * Manejo centralizado de errores NFC.
 */
class NfcErrorHandler @Inject constructor(
    private val context: Context
) {

    fun handleError(error: NfcError): ErrorAction {
        return when (error) {
            NfcError.NFC_NOT_AVAILABLE -> ErrorAction.ShowMessage(
                title = R.string.error_title_nfc_unavailable,
                message = R.string.error_message_nfc_unavailable,
                action = null
            )

            NfcError.NFC_DISABLED -> ErrorAction.ShowMessage(
                title = R.string.error_title_nfc_disabled,
                message = R.string.error_message_nfc_disabled,
                action = ErrorAction.Action(
                    label = R.string.action_open_settings,
                    onClick = { openNfcSettings() }
                )
            )

            NfcError.TAG_LOST -> ErrorAction.ShowSnackbar(
                message = R.string.error_tag_lost,
                action = ErrorAction.Action(
                    label = R.string.action_retry,
                    onClick = { retryLastOperation() }
                )
            )

            NfcError.TAG_NOT_SUPPORTED -> ErrorAction.ShowMessage(
                title = R.string.error_title_tag_unsupported,
                message = R.string.error_message_tag_unsupported,
                action = ErrorAction.Action(
                    label = R.string.action_learn_more,
                    onClick = { showSupportedTags() }
                )
            )

            // ... otros errores

            else -> ErrorAction.ShowSnackbar(
                message = error.messageResId,
                action = null
            )
        }
    }
}

sealed class ErrorAction {
    data class ShowMessage(
        val title: Int,
        val message: Int,
        val action: Action?
    ) : ErrorAction()

    data class ShowSnackbar(
        val message: Int,
        val action: Action?
    ) : ErrorAction()

    data class Action(
        val label: Int,
        val onClick: () -> Unit
    )
}
```

### 8.3 Retry Logic

```kotlin
/**
 * Política de reintentos para operaciones NFC.
 */
class NfcRetryPolicy {
    companion object {
        const val MAX_RETRIES = 3
        const val RETRY_DELAY_MS = 500L
    }

    suspend fun <T> withRetry(
        operation: suspend () -> NfcResult<T>
    ): NfcResult<T> {
        var lastError: NfcError? = null

        repeat(MAX_RETRIES) { attempt ->
            when (val result = operation()) {
                is NfcResult.Success -> return result
                is NfcResult.Error -> {
                    lastError = result.error

                    // Solo reintentar errores transitorios
                    if (result.error.isRetryable()) {
                        delay(RETRY_DELAY_MS * (attempt + 1))
                    } else {
                        return result
                    }
                }
            }
        }

        return NfcResult.Error(lastError ?: NfcError.UNKNOWN_ERROR)
    }

    private fun NfcError.isRetryable(): Boolean {
        return this in listOf(
            NfcError.TAG_LOST,
            NfcError.TAG_IO_ERROR
        )
    }
}
```

---

## 9. Testing Strategy

### 9.1 Unit Tests

```kotlin
class NdefParserTest {

    @Test
    fun `parseMessage with valid Umbral URI returns payload`() {
        val uri = "umbral://tag/v1/abc123?c=xyz789"
        val message = createNdefMessage(uri)

        val result = NdefParser.parseMessage(message)

        assertNotNull(result)
        assertEquals(1, result.version)
        assertEquals("abc123", result.tagId)
        assertEquals("xyz789", result.checksum)
    }

    @Test
    fun `parseMessage with invalid scheme returns null`() {
        val uri = "https://example.com/tag"
        val message = createNdefMessage(uri)

        val result = NdefParser.parseMessage(message)

        assertNull(result)
    }

    @Test
    fun `parseMessage with missing checksum returns null`() {
        val uri = "umbral://tag/v1/abc123"
        val message = createNdefMessage(uri)

        val result = NdefParser.parseMessage(message)

        assertNull(result)
    }
}

class NdefBuilderTest {

    @Test
    fun `buildMessage creates valid NDEF message`() {
        val payload = NdefPayload(
            version = 1,
            tagId = "test123",
            checksum = "abc"
        )

        val message = NdefBuilder.buildMessage(payload)

        assertEquals(1, message.records.size)
        assertTrue(message.records[0].tnf == NdefRecord.TNF_WELL_KNOWN)
    }
}

class TagTypeTest {

    @Test
    fun `NTAG213 is supported`() {
        assertTrue(TagType.NTAG213.supported)
    }

    @Test
    fun `MIFARE Classic is not supported`() {
        assertFalse(TagType.MIFARE_CLASSIC.supported)
    }
}
```

### 9.2 Integration Tests

```kotlin
@HiltAndroidTest
class NfcManagerIntegrationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var nfcManager: NfcManager

    @Inject
    lateinit var nfcRepository: NfcRepository

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `processTagIntent with known tag emits KnownTag event`() = runTest {
        // Arrange
        val existingTag = NfcTag(
            uid = "04:AB:CD:EF:12:34:56",
            name = "Test Tag"
        )
        nfcRepository.insertTag(existingTag)

        val intent = createMockNfcIntent(existingTag.uid)

        // Act
        val result = nfcManager.processTagIntent(intent)

        // Assert
        assertTrue(result is NfcResult.Success)
        val event = (result as NfcResult.Success).data
        assertTrue(event is TagEvent.KnownTag)
        assertEquals(existingTag.uid, (event as TagEvent.KnownTag).tag.uid)
    }

    @Test
    fun `processTagIntent with unknown tag emits UnknownTag event`() = runTest {
        val intent = createMockNfcIntent("04:FF:FF:FF:FF:FF:FF")

        val result = nfcManager.processTagIntent(intent)

        assertTrue(result is NfcResult.Success)
        val event = (result as NfcResult.Success).data
        assertTrue(event is TagEvent.UnknownTag)
    }
}
```

### 9.3 Manual Testing Checklist

| # | Test Case | Steps | Expected Result |
|---|-----------|-------|-----------------|
| 1 | First tag setup | App fresh install → Tap new tag | Prompt to name tag |
| 2 | Toggle blocking | Tap registered tag → Check apps | Apps blocked/unblocked |
| 3 | Multiple tags | Register 3 tags → Use each | All work independently |
| 4 | NFC disabled | Disable NFC → Open app | Shows enable NFC prompt |
| 5 | Tag too fast | Tap and remove quickly | Shows error, allows retry |
| 6 | Wrong tag type | Use MIFARE Classic | Shows unsupported error |
| 7 | Tag from other app | Use tag with other NDEF | Offers to overwrite |
| 8 | Background detection | App minimized → Tap tag | Opens app and toggles |

---

## 10. Consideraciones de Seguridad

### 10.1 Threat Model

| Amenaza | Impacto | Mitigación |
|---------|---------|------------|
| Tag clonado | Usuario podría desbloquear sin tag original | Bajo impacto (no es vault), usar UID check adicional |
| NDEF manipulation | Payload modificado maliciosamente | Checksum CRC32, validación estricta |
| Replay attack | Captura y replay de NFC | No aplica (no hay secretos en tag) |
| DoS via NFC | Flood de lecturas | Rate limiting en procesamiento |

### 10.2 Security Measures

```kotlin
/**
 * Validación de seguridad para payloads NFC.
 */
object NfcSecurityValidator {

    /**
     * Valida integridad del payload.
     */
    fun validatePayload(payload: NdefPayload): Boolean {
        // 1. Verificar versión soportada
        if (payload.version > NdefPayload.CURRENT_VERSION) {
            return false
        }

        // 2. Verificar checksum
        val expectedChecksum = calculateChecksum(payload.tagId)
        if (payload.checksum != expectedChecksum) {
            return false
        }

        // 3. Verificar formato de tagId
        if (!isValidTagId(payload.tagId)) {
            return false
        }

        return true
    }

    private fun calculateChecksum(tagId: String): String {
        val crc = CRC32()
        crc.update(tagId.toByteArray())
        return crc.value.toString(16).padStart(8, '0')
    }

    private fun isValidTagId(tagId: String): Boolean {
        // UUID format or hex string
        return tagId.matches(Regex("^[a-fA-F0-9-]+$")) &&
               tagId.length in 8..36
    }
}
```

---

## 11. Métricas y Logging

### 11.1 Analytics Events

```kotlin
sealed class NfcAnalyticsEvent {
    data class TagDetected(
        val tagType: TagType,
        val isKnown: Boolean
    ) : NfcAnalyticsEvent()

    data class TagRegistered(
        val tagType: TagType,
        val hasProfile: Boolean
    ) : NfcAnalyticsEvent()

    data class TagUsed(
        val tagId: String,
        val action: String // "block" or "unblock"
    ) : NfcAnalyticsEvent()

    data class NfcError(
        val errorType: String,
        val tagType: TagType?
    ) : NfcAnalyticsEvent()
}
```

### 11.2 Debug Logging

```kotlin
/**
 * Logger específico para operaciones NFC.
 */
object NfcLogger {
    private const val TAG = "Umbral.NFC"

    fun tagDetected(uid: String, type: TagType) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Tag detected: uid=$uid, type=$type")
        }
    }

    fun payloadRead(payload: NdefPayload?) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Payload read: $payload")
        }
    }

    fun writeAttempt(tagUid: String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Write attempt: uid=$tagUid")
        }
    }

    fun error(error: NfcError, exception: Exception? = null) {
        Log.e(TAG, "NFC Error: $error", exception)
    }
}
```

---

## 12. Criterios de Aceptación

### 12.1 Funcionales

- [ ] Detecta tags NTAG213/215/216 en foreground
- [ ] Detecta tags con pantalla encendida y app en background
- [ ] Escribe payload Umbral en tags vírgenes
- [ ] Lee y valida payloads Umbral existentes
- [ ] Registra nuevos tags con nombre personalizado
- [ ] Asocia tags a perfiles de bloqueo
- [ ] Hace toggle de bloqueo al detectar tag conocido
- [ ] Muestra error claro para tags no soportados
- [ ] Permite eliminar tags registrados
- [ ] Mantiene historial de uso por tag

### 12.2 No Funcionales

- [ ] Tiempo de detección a toggle < 500ms
- [ ] Funciona sin conexión a internet
- [ ] No consume batería en background (event-driven)
- [ ] Maneja gracefully NFC deshabilitado
- [ ] Soporta hasta 100 tags registrados
- [ ] Datos persisten entre reinstalaciones (si backup habilitado)

### 12.3 UX

- [ ] Feedback háptico al detectar tag
- [ ] Sonido opcional de confirmación
- [ ] Animación de "esperando tag" clara
- [ ] Mensajes de error en español, accionables
- [ ] Flujo de registro < 30 segundos

---

## 13. Dependencias con Otros Módulos

| Módulo | Tipo | Descripción |
|--------|------|-------------|
| `BlockingModule` | Output | Envía comando de toggle |
| `ProfilesModule` | Input | Lee perfil asociado a tag |
| `SettingsModule` | Input | Lee preferencias de sonido/vibración |
| `AnalyticsModule` | Output | Envía eventos de uso |

---

**Creado:** 2026-01-03
**Autor:** Oden Forge - Spec Writer
**Próximo:** blocking-module.md
