# QR Code Module Specification

**Estado:** 🟢 Activo
**Última actualización:** 2026-01-03
**Versión:** 1.0.0
**Líneas estimadas:** ~450

---

## 1. Visión General

### 1.1 Propósito

El módulo QR Code proporciona una alternativa al NFC para activar/desactivar perfiles de bloqueo. Permite generar códigos QR únicos por perfil y escanearlos usando la cámara del dispositivo.

### 1.2 Casos de Uso

1. **Dispositivo sin NFC** - Alternativa completa para usuarios sin hardware NFC
2. **Tags físicos impresos** - Imprimir QR y pegarlo en ubicaciones físicas
3. **Compartir perfiles** - Enviar QR por mensaje para que otros activen el mismo perfil
4. **Backup de NFC tags** - Si el tag NFC falla, el QR sirve como respaldo

### 1.3 Alcance

- Generación de QR codes con payload encriptado
- Escaneo de QR usando CameraX
- Validación y parsing de payload
- Integración con sistema de perfiles
- Exportación/impresión de QR codes

### 1.4 Dependencias

```
Depende de:
├── profiles-module (datos de perfiles)
├── blocking-module (activación de bloqueo)
└── ui-module (componentes visuales)

Dependido por:
└── ui-module (QrScanScreen, QrGenerateScreen)
```

---

## 2. Arquitectura

### 2.1 Diagrama de Componentes

```
┌─────────────────────────────────────────────────────────────┐
│                       QR Module                              │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │   QrGenerator   │  │   QrScanner     │  │  QrValidator │ │
│  │                 │  │                 │  │              │ │
│  │ - generateQr()  │  │ - startScan()   │  │ - validate() │ │
│  │ - encodePayload │  │ - stopScan()    │  │ - decrypt()  │ │
│  │ - exportImage() │  │ - processFrame  │  │ - parse()    │ │
│  └────────┬────────┘  └────────┬────────┘  └──────┬───────┘ │
│           │                    │                   │         │
│           └────────────────────┼───────────────────┘         │
│                                │                             │
│                    ┌───────────▼───────────┐                 │
│                    │     QrRepository      │                 │
│                    │                       │                 │
│                    │ - getQrForProfile()   │                 │
│                    │ - saveGeneratedQr()   │                 │
│                    │ - validateAndExecute()│                 │
│                    └───────────────────────┘                 │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 Flujo de Datos

```
Generación:
Profile → QrGenerator → QrPayload → QrCode Image → Export/Display

Escaneo:
Camera Frame → QrScanner → Raw String → QrValidator → QrPayload → Action
```

---

## 3. Modelos de Dominio

### 3.1 QR Payload

```kotlin
// domain/model/QrPayload.kt

/**
 * Payload encriptado contenido en el QR code
 * Formato: umbral://v1/{encrypted_data}
 */
data class QrPayload(
    val version: Int,
    val profileId: String,
    val action: QrAction,
    val timestamp: Long,
    val signature: String  // HMAC para validación
) {
    companion object {
        const val CURRENT_VERSION = 1
        const val SCHEME = "umbral"
        const val MAX_AGE_HOURS = 24 * 365  // QR válido por 1 año
    }
}

enum class QrAction {
    ACTIVATE,    // Activar perfil
    DEACTIVATE,  // Desactivar bloqueo
    TOGGLE       // Toggle según estado actual
}

/**
 * Resultado del escaneo de QR
 */
sealed class QrScanResult {
    data class Success(
        val payload: QrPayload,
        val profile: Profile
    ) : QrScanResult()

    data class InvalidFormat(
        val rawContent: String
    ) : QrScanResult()

    data class ExpiredQr(
        val payload: QrPayload,
        val expiredAt: Long
    ) : QrScanResult()

    data class ProfileNotFound(
        val profileId: String
    ) : QrScanResult()

    data class InvalidSignature(
        val payload: QrPayload
    ) : QrScanResult()

    object NotUmbralQr : QrScanResult()
}

/**
 * Configuración de generación de QR
 */
data class QrGenerationConfig(
    val size: Int = 512,                    // Píxeles
    val errorCorrectionLevel: ErrorCorrectionLevel = ErrorCorrectionLevel.M,
    val margin: Int = 2,                    // Módulos de margen
    val foregroundColor: Int = Color.BLACK,
    val backgroundColor: Int = Color.WHITE,
    val logoOverlay: Boolean = false        // Agregar logo de Umbral al centro
)

enum class ErrorCorrectionLevel {
    L,  // ~7% recovery
    M,  // ~15% recovery
    Q,  // ~25% recovery
    H   // ~30% recovery
}
```

### 3.2 QR Code Entity

```kotlin
// domain/model/GeneratedQr.kt

/**
 * QR code generado y almacenado
 */
data class GeneratedQr(
    val id: String,
    val profileId: String,
    val action: QrAction,
    val payload: String,           // Payload encriptado
    val createdAt: Long,
    val lastScannedAt: Long?,
    val scanCount: Int,
    val isActive: Boolean          // Puede desactivarse sin eliminar
)
```

---

## 4. Interfaces Públicas

### 4.1 QrGenerator

```kotlin
// domain/QrGenerator.kt
interface QrGenerator {

    /**
     * Genera un QR code para un perfil
     * @param profileId ID del perfil
     * @param action Acción a ejecutar al escanear
     * @param config Configuración visual del QR
     * @return Bitmap del QR code generado
     */
    suspend fun generateQrCode(
        profileId: String,
        action: QrAction = QrAction.TOGGLE,
        config: QrGenerationConfig = QrGenerationConfig()
    ): Result<Bitmap>

    /**
     * Genera el payload encriptado
     * @param profileId ID del perfil
     * @param action Acción a ejecutar
     * @return Payload como string para encodear en QR
     */
    fun generatePayload(
        profileId: String,
        action: QrAction
    ): String

    /**
     * Exporta el QR como archivo de imagen
     * @param qrBitmap Bitmap del QR
     * @param filename Nombre del archivo
     * @param format Formato de imagen
     * @return URI del archivo guardado
     */
    suspend fun exportToFile(
        qrBitmap: Bitmap,
        filename: String,
        format: ImageFormat = ImageFormat.PNG
    ): Result<Uri>

    /**
     * Obtiene el QR como base64 para compartir
     */
    fun toBase64(qrBitmap: Bitmap): String
}

enum class ImageFormat {
    PNG,
    JPEG,
    WEBP
}
```

### 4.2 QrScanner

```kotlin
// domain/QrScanner.kt
interface QrScanner {

    /**
     * Estado actual del scanner
     */
    val scannerState: StateFlow<QrScannerState>

    /**
     * Último resultado de escaneo
     */
    val lastResult: SharedFlow<QrScanResult>

    /**
     * Inicia el escaneo de QR
     * @param lifecycleOwner Owner para manejar lifecycle
     * @param previewView Vista de preview de cámara
     */
    fun startScanning(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    )

    /**
     * Detiene el escaneo
     */
    fun stopScanning()

    /**
     * Procesa una imagen estática (desde galería)
     * @param imageUri URI de la imagen
     * @return Resultado del escaneo
     */
    suspend fun scanFromImage(imageUri: Uri): QrScanResult

    /**
     * Activa/desactiva la linterna
     */
    fun toggleFlashlight()

    /**
     * Estado actual de la linterna
     */
    val isFlashlightOn: StateFlow<Boolean>
}

sealed class QrScannerState {
    object Idle : QrScannerState()
    object Initializing : QrScannerState()
    object Scanning : QrScannerState()
    object Processing : QrScannerState()
    data class Error(val message: String) : QrScannerState()
}
```

### 4.3 QrValidator

```kotlin
// domain/QrValidator.kt
interface QrValidator {

    /**
     * Valida y parsea el contenido de un QR escaneado
     * @param rawContent Contenido raw del QR
     * @return Resultado de validación
     */
    suspend fun validate(rawContent: String): QrScanResult

    /**
     * Verifica si el contenido es un QR de Umbral
     */
    fun isUmbralQr(rawContent: String): Boolean

    /**
     * Desencripta el payload
     * @param encryptedPayload Payload encriptado
     * @return Payload desencriptado o null si inválido
     */
    fun decryptPayload(encryptedPayload: String): QrPayload?

    /**
     * Verifica la firma HMAC del payload
     */
    fun verifySignature(payload: QrPayload): Boolean

    /**
     * Verifica si el QR no ha expirado
     */
    fun isExpired(payload: QrPayload): Boolean
}
```

### 4.4 QrRepository

```kotlin
// domain/QrRepository.kt
interface QrRepository {

    /**
     * Genera y guarda un QR para un perfil
     */
    suspend fun generateAndSaveQr(
        profileId: String,
        action: QrAction
    ): Result<GeneratedQr>

    /**
     * Obtiene todos los QR generados para un perfil
     */
    fun getQrsForProfile(profileId: String): Flow<List<GeneratedQr>>

    /**
     * Obtiene un QR específico
     */
    suspend fun getQrById(qrId: String): GeneratedQr?

    /**
     * Incrementa el contador de escaneos
     */
    suspend fun recordScan(qrId: String)

    /**
     * Desactiva un QR (sin eliminarlo)
     */
    suspend fun deactivateQr(qrId: String)

    /**
     * Elimina un QR
     */
    suspend fun deleteQr(qrId: String)

    /**
     * Ejecuta la acción del QR escaneado
     */
    suspend fun executeQrAction(scanResult: QrScanResult.Success): Result<Unit>
}
```

---

## 5. Implementación

### 5.1 QrGeneratorImpl

```kotlin
// data/QrGeneratorImpl.kt
class QrGeneratorImpl @Inject constructor(
    private val context: Context,
    private val encryptionManager: EncryptionManager
) : QrGenerator {

    override suspend fun generateQrCode(
        profileId: String,
        action: QrAction,
        config: QrGenerationConfig
    ): Result<Bitmap> = withContext(Dispatchers.Default) {
        try {
            val payload = generatePayload(profileId, action)

            val hints = mutableMapOf<EncodeHintType, Any>().apply {
                put(EncodeHintType.ERROR_CORRECTION, config.errorCorrectionLevel.toZxing())
                put(EncodeHintType.MARGIN, config.margin)
                put(EncodeHintType.CHARACTER_SET, "UTF-8")
            }

            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(
                payload,
                BarcodeFormat.QR_CODE,
                config.size,
                config.size,
                hints
            )

            val bitmap = Bitmap.createBitmap(
                config.size,
                config.size,
                Bitmap.Config.ARGB_8888
            )

            for (x in 0 until config.size) {
                for (y in 0 until config.size) {
                    bitmap.setPixel(
                        x, y,
                        if (bitMatrix[x, y]) config.foregroundColor
                        else config.backgroundColor
                    )
                }
            }

            if (config.logoOverlay) {
                addLogoOverlay(bitmap)
            }

            Result.success(bitmap)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun generatePayload(profileId: String, action: QrAction): String {
        val payload = QrPayload(
            version = QrPayload.CURRENT_VERSION,
            profileId = profileId,
            action = action,
            timestamp = System.currentTimeMillis(),
            signature = "" // Se calcula después
        )

        val jsonPayload = Json.encodeToString(payload)
        val encrypted = encryptionManager.encrypt(jsonPayload)
        val signature = encryptionManager.hmac(encrypted)

        return "${QrPayload.SCHEME}://v${payload.version}/${encrypted}?sig=${signature}"
    }

    override suspend fun exportToFile(
        qrBitmap: Bitmap,
        filename: String,
        format: ImageFormat
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "$filename.${format.extension}"
            )

            FileOutputStream(file).use { out ->
                qrBitmap.compress(format.toCompressFormat(), 100, out)
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            Result.success(uri)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun toBase64(qrBitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun addLogoOverlay(bitmap: Bitmap) {
        val logo = BitmapFactory.decodeResource(context.resources, R.drawable.ic_umbral_logo)
        val logoSize = bitmap.width / 5
        val scaledLogo = Bitmap.createScaledBitmap(logo, logoSize, logoSize, true)

        val canvas = Canvas(bitmap)
        val left = (bitmap.width - logoSize) / 2f
        val top = (bitmap.height - logoSize) / 2f

        // Fondo blanco para el logo
        val bgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(
            left - 4, top - 4,
            left + logoSize + 4, top + logoSize + 4,
            bgPaint
        )

        canvas.drawBitmap(scaledLogo, left, top, null)
    }
}
```

### 5.2 QrScannerImpl

```kotlin
// data/QrScannerImpl.kt
class QrScannerImpl @Inject constructor(
    private val context: Context,
    private val qrValidator: QrValidator
) : QrScanner {

    private val _scannerState = MutableStateFlow<QrScannerState>(QrScannerState.Idle)
    override val scannerState: StateFlow<QrScannerState> = _scannerState.asStateFlow()

    private val _lastResult = MutableSharedFlow<QrScanResult>()
    override val lastResult: SharedFlow<QrScanResult> = _lastResult.asSharedFlow()

    private val _isFlashlightOn = MutableStateFlow(false)
    override val isFlashlightOn: StateFlow<Boolean> = _isFlashlightOn.asStateFlow()

    private var cameraProvider: ProcessCameraProvider? = null
    private var camera: Camera? = null
    private var imageAnalysis: ImageAnalysis? = null

    private val barcodeScanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    override fun startScanning(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        _scannerState.value = QrScannerState.Initializing

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()
                bindCameraUseCases(lifecycleOwner, previewView)
                _scannerState.value = QrScannerState.Scanning
            } catch (e: Exception) {
                _scannerState.value = QrScannerState.Error(e.message ?: "Error al iniciar cámara")
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun bindCameraUseCases(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView
    ) {
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analysis ->
                analysis.setAnalyzer(
                    ContextCompat.getMainExecutor(context)
                ) { imageProxy ->
                    processImage(imageProxy)
                }
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider?.unbindAll()
        camera = cameraProvider?.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalysis
        )
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImage(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees
            )

            barcodeScanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { rawValue ->
                            processScannedContent(rawValue)
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun processScannedContent(rawValue: String) {
        if (!qrValidator.isUmbralQr(rawValue)) {
            // Ignorar QR codes que no son de Umbral
            return
        }

        _scannerState.value = QrScannerState.Processing

        CoroutineScope(Dispatchers.Default).launch {
            val result = qrValidator.validate(rawValue)
            _lastResult.emit(result)
            _scannerState.value = QrScannerState.Scanning
        }
    }

    override fun stopScanning() {
        cameraProvider?.unbindAll()
        _scannerState.value = QrScannerState.Idle
        _isFlashlightOn.value = false
    }

    override suspend fun scanFromImage(imageUri: Uri): QrScanResult {
        return try {
            val inputImage = InputImage.fromFilePath(context, imageUri)
            val barcodes = barcodeScanner.process(inputImage).await()

            val qrBarcode = barcodes.firstOrNull { it.format == Barcode.FORMAT_QR_CODE }

            if (qrBarcode?.rawValue != null) {
                qrValidator.validate(qrBarcode.rawValue!!)
            } else {
                QrScanResult.NotUmbralQr
            }
        } catch (e: Exception) {
            QrScanResult.InvalidFormat(e.message ?: "")
        }
    }

    override fun toggleFlashlight() {
        camera?.cameraControl?.enableTorch(!_isFlashlightOn.value)
        _isFlashlightOn.value = !_isFlashlightOn.value
    }
}
```

### 5.3 QrValidatorImpl

```kotlin
// data/QrValidatorImpl.kt
class QrValidatorImpl @Inject constructor(
    private val encryptionManager: EncryptionManager,
    private val profileRepository: ProfileRepository
) : QrValidator {

    private val urlPattern = Regex("^${QrPayload.SCHEME}://v(\\d+)/(.+)\\?sig=(.+)$")

    override suspend fun validate(rawContent: String): QrScanResult {
        if (!isUmbralQr(rawContent)) {
            return QrScanResult.NotUmbralQr
        }

        val matchResult = urlPattern.matchEntire(rawContent)
            ?: return QrScanResult.InvalidFormat(rawContent)

        val version = matchResult.groupValues[1].toIntOrNull()
            ?: return QrScanResult.InvalidFormat(rawContent)

        if (version > QrPayload.CURRENT_VERSION) {
            return QrScanResult.InvalidFormat("Versión no soportada: $version")
        }

        val encryptedPayload = matchResult.groupValues[2]
        val signature = matchResult.groupValues[3]

        // Verificar firma
        val expectedSignature = encryptionManager.hmac(encryptedPayload)
        if (signature != expectedSignature) {
            val payload = decryptPayload(encryptedPayload)
                ?: return QrScanResult.InvalidFormat(rawContent)
            return QrScanResult.InvalidSignature(payload)
        }

        val payload = decryptPayload(encryptedPayload)
            ?: return QrScanResult.InvalidFormat(rawContent)

        // Verificar expiración
        if (isExpired(payload)) {
            return QrScanResult.ExpiredQr(
                payload = payload,
                expiredAt = payload.timestamp + (QrPayload.MAX_AGE_HOURS * 3600 * 1000)
            )
        }

        // Verificar que el perfil existe
        val profile = profileRepository.getProfileById(payload.profileId)
            ?: return QrScanResult.ProfileNotFound(payload.profileId)

        return QrScanResult.Success(payload, profile)
    }

    override fun isUmbralQr(rawContent: String): Boolean {
        return rawContent.startsWith("${QrPayload.SCHEME}://")
    }

    override fun decryptPayload(encryptedPayload: String): QrPayload? {
        return try {
            val decrypted = encryptionManager.decrypt(encryptedPayload)
            Json.decodeFromString<QrPayload>(decrypted)
        } catch (e: Exception) {
            null
        }
    }

    override fun verifySignature(payload: QrPayload): Boolean {
        // La verificación se hace en validate()
        return payload.signature.isNotEmpty()
    }

    override fun isExpired(payload: QrPayload): Boolean {
        val maxAge = QrPayload.MAX_AGE_HOURS * 3600 * 1000L
        return System.currentTimeMillis() - payload.timestamp > maxAge
    }
}
```

---

## 6. Encriptación

### 6.1 EncryptionManager

```kotlin
// security/EncryptionManager.kt
interface EncryptionManager {
    fun encrypt(plainText: String): String
    fun decrypt(cipherText: String): String
    fun hmac(data: String): String
}

class EncryptionManagerImpl @Inject constructor(
    private val context: Context
) : EncryptionManager {

    private val masterKey by lazy {
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    private val sharedPrefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            "umbral_qr_keys",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    private val secretKey: SecretKey by lazy {
        getOrCreateSecretKey()
    }

    override fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        val combined = iv + encrypted
        return Base64.encodeToString(combined, Base64.URL_SAFE or Base64.NO_WRAP)
    }

    override fun decrypt(cipherText: String): String {
        val combined = Base64.decode(cipherText, Base64.URL_SAFE or Base64.NO_WRAP)

        val iv = combined.sliceArray(0 until 12)
        val encrypted = combined.sliceArray(12 until combined.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        val decrypted = cipher.doFinal(encrypted)
        return String(decrypted, Charsets.UTF_8)
    }

    override fun hmac(data: String): String {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKey)
        val hash = mac.doFinal(data.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hash, Base64.URL_SAFE or Base64.NO_WRAP)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val existingKey = sharedPrefs.getString("qr_secret_key", null)

        return if (existingKey != null) {
            val keyBytes = Base64.decode(existingKey, Base64.DEFAULT)
            SecretKeySpec(keyBytes, "AES")
        } else {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(256)
            val newKey = keyGenerator.generateKey()

            sharedPrefs.edit()
                .putString(
                    "qr_secret_key",
                    Base64.encodeToString(newKey.encoded, Base64.DEFAULT)
                )
                .apply()

            newKey
        }
    }
}
```

---

## 7. Room Entities

### 7.1 GeneratedQrEntity

```kotlin
// data/local/entity/GeneratedQrEntity.kt
@Entity(
    tableName = "generated_qrs",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profile_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["profile_id"]),
        Index(value = ["created_at"])
    ]
)
data class GeneratedQrEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "profile_id")
    val profileId: String,

    @ColumnInfo(name = "action")
    val action: String,  // ACTIVATE, DEACTIVATE, TOGGLE

    @ColumnInfo(name = "payload")
    val payload: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "last_scanned_at")
    val lastScannedAt: Long?,

    @ColumnInfo(name = "scan_count")
    val scanCount: Int,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean
)
```

### 7.2 GeneratedQrDao

```kotlin
// data/local/dao/GeneratedQrDao.kt
@Dao
interface GeneratedQrDao {

    @Query("SELECT * FROM generated_qrs WHERE profile_id = :profileId ORDER BY created_at DESC")
    fun getQrsForProfile(profileId: String): Flow<List<GeneratedQrEntity>>

    @Query("SELECT * FROM generated_qrs WHERE id = :qrId")
    suspend fun getQrById(qrId: String): GeneratedQrEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(qr: GeneratedQrEntity)

    @Query("UPDATE generated_qrs SET last_scanned_at = :timestamp, scan_count = scan_count + 1 WHERE id = :qrId")
    suspend fun recordScan(qrId: String, timestamp: Long)

    @Query("UPDATE generated_qrs SET is_active = :isActive WHERE id = :qrId")
    suspend fun setActive(qrId: String, isActive: Boolean)

    @Query("DELETE FROM generated_qrs WHERE id = :qrId")
    suspend fun delete(qrId: String)

    @Query("DELETE FROM generated_qrs WHERE profile_id = :profileId")
    suspend fun deleteAllForProfile(profileId: String)
}
```

---

## 8. UI Components

### 8.1 QrScanScreen

```kotlin
// ui/screens/QrScanScreen.kt
@Composable
fun QrScanScreen(
    viewModel: QrScanViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onSuccess: (Profile) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    LaunchedEffect(uiState.scannedProfile) {
        uiState.scannedProfile?.let { profile ->
            onSuccess(profile)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.scan_qr_title)) },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleFlashlight) {
                        Icon(
                            imageVector = if (uiState.isFlashlightOn)
                                Icons.Default.FlashOn
                            else
                                Icons.Default.FlashOff,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                !cameraPermissionState.status.isGranted -> {
                    CameraPermissionRequest(
                        onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
                    )
                }
                else -> {
                    // Camera preview
                    AndroidView(
                        factory = { context ->
                            PreviewView(context).also { previewView ->
                                viewModel.startScanning(lifecycleOwner, previewView)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Overlay con visor
                    QrScannerOverlay(
                        state = uiState.scannerState,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Botón para escanear desde galería
                    FloatingActionButton(
                        onClick = viewModel::scanFromGallery,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    }
                }
            }

            // Error snackbar
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(error)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopScanning()
        }
    }
}

@Composable
private fun QrScannerOverlay(
    state: QrScannerState,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val scanAreaSize = size.minDimension * 0.7f
        val left = (size.width - scanAreaSize) / 2
        val top = (size.height - scanAreaSize) / 2

        // Fondo semi-transparente
        drawRect(
            color = Color.Black.copy(alpha = 0.5f),
            size = size
        )

        // Área de escaneo transparente
        drawRoundRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(scanAreaSize, scanAreaSize),
            cornerRadius = CornerRadius(16.dp.toPx()),
            blendMode = BlendMode.Clear
        )

        // Borde del área de escaneo
        val borderColor = when (state) {
            QrScannerState.Scanning -> Color.White
            QrScannerState.Processing -> Color.Yellow
            is QrScannerState.Error -> Color.Red
            else -> Color.White.copy(alpha = 0.5f)
        }

        drawRoundRect(
            color = borderColor,
            topLeft = Offset(left, top),
            size = Size(scanAreaSize, scanAreaSize),
            cornerRadius = CornerRadius(16.dp.toPx()),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}
```

### 8.2 QrDisplayCard

```kotlin
// ui/components/QrDisplayCard.kt
@Composable
fun QrDisplayCard(
    qrBitmap: Bitmap,
    profileName: String,
    action: QrAction,
    onShare: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralDimens.spaceLg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceLg)
        ) {
            // QR Code
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = stringResource(R.string.qr_code_for_profile, profileName),
                modifier = Modifier
                    .size(200.dp)
                    .clip(MaterialTheme.shapes.medium)
            )

            // Info
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(UmbralDimens.spaceXs)
            ) {
                Text(
                    text = profileName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = when (action) {
                        QrAction.ACTIVATE -> stringResource(R.string.qr_action_activate)
                        QrAction.DEACTIVATE -> stringResource(R.string.qr_action_deactivate)
                        QrAction.TOGGLE -> stringResource(R.string.qr_action_toggle)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(UmbralDimens.spaceMd)
            ) {
                OutlinedButton(onClick = onSave) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_save))
                }
                Button(onClick = onShare) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_share))
                }
            }
        }
    }
}
```

---

## 9. Testing Strategy

### 9.1 Unit Tests

```kotlin
// test/QrValidatorTest.kt
class QrValidatorTest {

    private lateinit var validator: QrValidator
    private lateinit var encryptionManager: EncryptionManager
    private lateinit var profileRepository: ProfileRepository

    @Before
    fun setup() {
        encryptionManager = mockk()
        profileRepository = mockk()
        validator = QrValidatorImpl(encryptionManager, profileRepository)
    }

    @Test
    fun `isUmbralQr returns true for valid scheme`() {
        val content = "umbral://v1/abc123?sig=xyz"
        assertTrue(validator.isUmbralQr(content))
    }

    @Test
    fun `isUmbralQr returns false for other schemes`() {
        assertFalse(validator.isUmbralQr("https://example.com"))
        assertFalse(validator.isUmbralQr("random text"))
        assertFalse(validator.isUmbralQr(""))
    }

    @Test
    fun `validate returns NotUmbralQr for non-umbral content`() = runTest {
        val result = validator.validate("https://example.com")
        assertTrue(result is QrScanResult.NotUmbralQr)
    }

    @Test
    fun `validate returns Success for valid QR`() = runTest {
        val profileId = "profile-123"
        val profile = Profile(id = profileId, name = "Test", /* ... */)

        every { encryptionManager.decrypt(any()) } returns """
            {"version":1,"profileId":"$profileId","action":"TOGGLE","timestamp":${System.currentTimeMillis()},"signature":""}
        """.trimIndent()
        every { encryptionManager.hmac(any()) } returns "valid-sig"
        coEvery { profileRepository.getProfileById(profileId) } returns profile

        val result = validator.validate("umbral://v1/encrypted?sig=valid-sig")

        assertTrue(result is QrScanResult.Success)
        assertEquals(profile, (result as QrScanResult.Success).profile)
    }
}
```

### 9.2 Integration Tests

```kotlin
// androidTest/QrScannerIntegrationTest.kt
@HiltAndroidTest
class QrScannerIntegrationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var qrGenerator: QrGenerator

    @Inject
    lateinit var qrValidator: QrValidator

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun generatedQrCanBeValidated() = runTest {
        val profileId = "test-profile"
        val payload = qrGenerator.generatePayload(profileId, QrAction.TOGGLE)

        // Simular escaneo
        val result = qrValidator.validate(payload)

        // El perfil no existe, pero el formato es válido
        assertTrue(result is QrScanResult.ProfileNotFound)
    }
}
```

---

## 10. Criterios de Aceptación

### 10.1 Generación
- [ ] QR se genera en < 100ms
- [ ] QR es legible a distancia de 30cm
- [ ] Payload encriptado no revela datos en plain text
- [ ] Logo overlay no afecta legibilidad (error correction L -> M)

### 10.2 Escaneo
- [ ] Detección en < 500ms con buena iluminación
- [ ] Funciona con QR impreso y en pantalla
- [ ] Linterna activable para baja luz
- [ ] Escaneo desde galería funciona

### 10.3 Validación
- [ ] QR de otras apps ignorados silenciosamente
- [ ] QR expirado muestra mensaje claro
- [ ] Firma inválida rechazada
- [ ] Perfil no encontrado informa al usuario

### 10.4 Seguridad
- [ ] Payload encriptado con AES-256-GCM
- [ ] HMAC verifica integridad
- [ ] Clave secreta almacenada en EncryptedSharedPreferences
- [ ] No hay información sensible en logs

---

## 11. Dependencias

```kotlin
// build.gradle.kts
dependencies {
    // ZXing para generación de QR
    implementation("com.google.zxing:core:3.5.2")

    // ML Kit para escaneo
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // CameraX
    val cameraxVersion = "1.3.1"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")

    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}
```

---

**Creado:** 2026-01-03
**Autor/Mantenedor:** Equipo Umbral
