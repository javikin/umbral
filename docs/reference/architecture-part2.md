# Arquitectura Detallada - Parte 2

**Continuación de:** `technical-decisions.md` sección 7
**Última actualización:** 2026-01-03T01:59:58Z

---

## 7.8 Dependency Injection (Hilt Modules)

### AppModule

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun providePackageManager(context: Context): PackageManager {
        return context.packageManager
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(
        context: Context
    ): NotificationHelper {
        return NotificationHelper(context)
    }

    @Provides
    @Singleton
    fun providePermissionHelper(
        context: Context
    ): PermissionHelper {
        return PermissionHelper(context)
    }
}
```

### DatabaseModule

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): UmbralDatabase {
        return Room.databaseBuilder(
            context,
            UmbralDatabase::class.java,
            UmbralDatabase.DATABASE_NAME
        )
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Populate predefined profiles on first run
                }
            })
            .fallbackToDestructiveMigration() // TODO: Add proper migrations
            .build()
    }

    @Provides
    fun provideBlockingProfileDao(database: UmbralDatabase) =
        database.blockingProfileDao()

    @Provides
    fun provideBlockedAppDao(database: UmbralDatabase) =
        database.blockedAppDao()

    @Provides
    fun provideNfcTagDao(database: UmbralDatabase) =
        database.nfcTagDao()

    @Provides
    fun provideBlockingSessionDao(database: UmbralDatabase) =
        database.blockingSessionDao()

    @Provides
    fun provideAppUsageStatsDao(database: UmbralDatabase) =
        database.appUsageStatsDao()

    @Provides
    fun provideUserSettingsDao(database: UmbralDatabase) =
        database.userSettingsDao()

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = {
                context.preferencesDataStoreFile("umbral_preferences")
            }
        )
    }
}
```

### RepositoryModule

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBlockingProfileRepository(
        impl: BlockingProfileRepositoryImpl
    ): BlockingProfileRepository

    @Binds
    @Singleton
    abstract fun bindBlockedAppRepository(
        impl: BlockedAppRepositoryImpl
    ): BlockedAppRepository

    @Binds
    @Singleton
    abstract fun bindNfcTagRepository(
        impl: NfcTagRepositoryImpl
    ): NfcTagRepository

    @Binds
    @Singleton
    abstract fun bindBlockingSessionRepository(
        impl: BlockingSessionRepositoryImpl
    ): BlockingSessionRepository

    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(
        impl: StatisticsRepositoryImpl
    ): StatisticsRepository

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        impl: PreferencesRepositoryImpl
    ): PreferencesRepository
}
```

### ServiceModule

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideNfcManager(
        @ApplicationContext context: Context
    ): NfcManager {
        return NfcManager(context)
    }

    @Provides
    @Singleton
    fun provideAppBlockingManager(
        @ApplicationContext context: Context,
        sessionRepository: BlockingSessionRepository,
        profileRepository: BlockingProfileRepository
    ): AppBlockingManager {
        return AppBlockingManager(context, sessionRepository, profileRepository)
    }

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }
}
```

---

## 7.9 Navigation (Jetpack Compose Navigation)

### Screen Sealed Class

```kotlin
sealed class Screen(val route: String) {
    // Main screens
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Profiles : Screen("profiles")
    object Statistics : Screen("statistics")
    object Settings : Screen("settings")

    // Detail screens
    object ProfileDetail : Screen("profile/{profileId}") {
        fun createRoute(profileId: String) = "profile/$profileId"
    }

    object CreateProfile : Screen("profile/create")
    object EditProfile : Screen("profile/{profileId}/edit") {
        fun createRoute(profileId: String) = "profile/$profileId/edit"
    }

    object AppSelection : Screen("apps/{profileId}") {
        fun createRoute(profileId: String) = "apps/$profileId"
    }

    object NfcSetup : Screen("nfc/setup")
    object NfcWrite : Screen("nfc/write/{profileId}") {
        fun createRoute(profileId: String) = "nfc/write/$profileId"
    }

    object QrCode : Screen("qr/{profileId}") {
        fun createRoute(profileId: String) = "qr/$profileId"
    }
}
```

### NavGraph

```kotlin
@Composable
fun UmbralNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Onboarding.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Onboarding
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Home
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToProfiles = {
                    navController.navigate(Screen.Profiles.route)
                },
                onNavigateToStats = {
                    navController.navigate(Screen.Statistics.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // Profiles
        composable(Screen.Profiles.route) {
            ProfileListScreen(
                onProfileClick = { profileId ->
                    navController.navigate(Screen.ProfileDetail.createRoute(profileId))
                },
                onCreateProfile = {
                    navController.navigate(Screen.CreateProfile.route)
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // Profile Detail
        composable(
            route = Screen.ProfileDetail.route,
            arguments = listOf(
                navArgument("profileId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId")!!
            ProfileDetailScreen(
                profileId = profileId,
                onEditProfile = {
                    navController.navigate(Screen.EditProfile.createRoute(profileId))
                },
                onSelectApps = {
                    navController.navigate(Screen.AppSelection.createRoute(profileId))
                },
                onSetupNfc = {
                    navController.navigate(Screen.NfcWrite.createRoute(profileId))
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // Create Profile
        composable(Screen.CreateProfile.route) {
            CreateProfileScreen(
                onProfileCreated = { profileId ->
                    navController.navigate(Screen.ProfileDetail.createRoute(profileId)) {
                        popUpTo(Screen.Profiles.route)
                    }
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // Edit Profile
        composable(
            route = Screen.EditProfile.route,
            arguments = listOf(
                navArgument("profileId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId")!!
            EditProfileScreen(
                profileId = profileId,
                onSaved = {
                    navController.navigateUp()
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // App Selection
        composable(
            route = Screen.AppSelection.route,
            arguments = listOf(
                navArgument("profileId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId")!!
            AppSelectionScreen(
                profileId = profileId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // NFC Setup
        composable(Screen.NfcSetup.route) {
            NfcSetupScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // NFC Write
        composable(
            route = Screen.NfcWrite.route,
            arguments = listOf(
                navArgument("profileId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId")!!
            NfcWriteScreen(
                profileId = profileId,
                onTagWritten = {
                    navController.navigateUp()
                },
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // Statistics
        composable(Screen.Statistics.route) {
            StatisticsScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // Settings
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }

        // QR Code
        composable(
            route = Screen.QrCode.route,
            arguments = listOf(
                navArgument("profileId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId")!!
            QrCodeScreen(
                profileId = profileId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}
```

---

## 7.10 Background Services

### BlockingService (Foreground Service)

```kotlin
class BlockingService : Service() {

    private val binder = LocalBinder()
    private val notificationHelper by lazy { NotificationHelper(this) }

    private var isBlocking = false
    private var currentProfileId: String? = null

    inner class LocalBinder : Binder() {
        fun getService(): BlockingService = this@BlockingService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_BLOCKING -> {
                val profileId = intent.getStringExtra(EXTRA_PROFILE_ID)
                if (profileId != null) {
                    startBlocking(profileId)
                }
            }
            ACTION_STOP_BLOCKING -> {
                stopBlocking()
            }
        }

        return START_STICKY
    }

    private fun startBlocking(profileId: String) {
        if (isBlocking) return

        currentProfileId = profileId
        isBlocking = true

        // Start foreground service
        val notification = notificationHelper.createBlockingNotification()
        startForeground(NOTIFICATION_ID, notification)

        // Start usage monitoring
        startUsageMonitoring()
    }

    private fun stopBlocking() {
        if (!isBlocking) return

        isBlocking = false
        currentProfileId = null

        // Stop foreground service
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()

        // Stop usage monitoring
        stopUsageMonitoring()
    }

    private fun startUsageMonitoring() {
        // Register usage stats observer
        // Check for blocked app launches
        // Show overlay when blocked app is launched
    }

    private fun stopUsageMonitoring() {
        // Unregister usage stats observer
    }

    companion object {
        const val ACTION_START_BLOCKING = "com.umbral.START_BLOCKING"
        const val ACTION_STOP_BLOCKING = "com.umbral.STOP_BLOCKING"
        const val EXTRA_PROFILE_ID = "profile_id"
        private const val NOTIFICATION_ID = 1001
    }
}
```

### NfcListenerService (Background NFC)

```kotlin
class NfcListenerService : HostApduService() {

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray? {
        // Handle background NFC tag detection
        return null
    }

    override fun onDeactivated(reason: Int) {
        // Handle NFC deactivation
    }
}
```

### TimerService (WorkManager Worker)

```kotlin
class TimerWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val sessionId = inputData.getString(KEY_SESSION_ID) ?: return Result.failure()
        val durationMinutes = inputData.getInt(KEY_DURATION, 0)

        // Wait for the specified duration
        delay(durationMinutes * 60 * 1000L)

        // Stop blocking after timer expires
        val intent = Intent(applicationContext, BlockingService::class.java).apply {
            action = BlockingService.ACTION_STOP_BLOCKING
        }
        applicationContext.startService(intent)

        return Result.success()
    }

    companion object {
        const val KEY_SESSION_ID = "session_id"
        const val KEY_DURATION = "duration_minutes"
    }
}
```

---

## 7.11 Permission Handling

### PermissionHelper

```kotlin
class PermissionHelper(private val context: Context) {

    // NFC Permission
    fun hasNfcPermission(): Boolean {
        val nfcManager = context.getSystemService(Context.NFC_SERVICE) as? NfcManager
        return nfcManager?.defaultAdapter != null
    }

    fun isNfcEnabled(): Boolean {
        val nfcManager = context.getSystemService(Context.NFC_SERVICE) as? NfcManager
        return nfcManager?.defaultAdapter?.isEnabled == true
    }

    // Usage Stats Permission
    fun hasUsageStatsPermission(): Boolean {
        val appOpsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        } else {
            appOpsManager.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                context.packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun requestUsageStatsPermission(activity: Activity) {
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
        activity.startActivity(intent)
    }

    // Accessibility Permission
    fun hasAccessibilityPermission(): Boolean {
        val service = "${context.packageName}/${AccessibilityMonitor::class.java.name}"
        val settingValue = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return settingValue?.contains(service) == true
    }

    fun requestAccessibilityPermission(activity: Activity) {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        activity.startActivity(intent)
    }

    // Overlay Permission
    fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }

    fun requestOverlayPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${context.packageName}")
            )
            activity.startActivity(intent)
        }
    }

    // Notification Permission (Android 13+)
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun requestNotificationPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_NOTIFICATIONS
            )
        }
    }

    // Check all required permissions
    fun hasAllRequiredPermissions(): Boolean {
        return hasNfcPermission() &&
                isNfcEnabled() &&
                hasUsageStatsPermission() &&
                hasOverlayPermission() &&
                hasNotificationPermission()
    }

    companion object {
        const val REQUEST_CODE_NOTIFICATIONS = 1001
    }
}
```

### Permission Flow (Onboarding)

```kotlin
@Composable
fun PermissionRequestScreen(
    onAllPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val permissionHelper = remember { PermissionHelper(context) }

    var currentStep by remember { mutableStateOf(PermissionStep.NFC) }

    LaunchedEffect(Unit) {
        // Check which permissions are already granted
        when {
            !permissionHelper.hasUsageStatsPermission() -> {
                currentStep = PermissionStep.USAGE_STATS
            }
            !permissionHelper.hasOverlayPermission() -> {
                currentStep = PermissionStep.OVERLAY
            }
            !permissionHelper.hasNotificationPermission() -> {
                currentStep = PermissionStep.NOTIFICATIONS
            }
            else -> {
                onAllPermissionsGranted()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentStep) {
            PermissionStep.NFC -> {
                NfcPermissionStep(
                    onContinue = {
                        currentStep = PermissionStep.USAGE_STATS
                    }
                )
            }
            PermissionStep.USAGE_STATS -> {
                UsageStatsPermissionStep(
                    onGranted = {
                        currentStep = PermissionStep.OVERLAY
                    },
                    onRequest = {
                        permissionHelper.requestUsageStatsPermission(activity)
                    }
                )
            }
            PermissionStep.OVERLAY -> {
                OverlayPermissionStep(
                    onGranted = {
                        currentStep = PermissionStep.NOTIFICATIONS
                    },
                    onRequest = {
                        permissionHelper.requestOverlayPermission(activity)
                    }
                )
            }
            PermissionStep.NOTIFICATIONS -> {
                NotificationPermissionStep(
                    onGranted = {
                        onAllPermissionsGranted()
                    },
                    onRequest = {
                        permissionHelper.requestNotificationPermission(activity)
                    }
                )
            }
        }
    }
}

enum class PermissionStep {
    NFC,
    USAGE_STATS,
    OVERLAY,
    NOTIFICATIONS
}
```

---

## 7.12 Error Handling Strategy

### Custom Exceptions

```kotlin
// domain/model/exception/UmbralException.kt
sealed class UmbralException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class ValidationException(message: String) : UmbralException(message)
    class NotFoundException(message: String) : UmbralException(message)
    class PermissionDeniedException(message: String) : UmbralException(message)
    class NfcException(message: String, cause: Throwable? = null) : UmbralException(message, cause)
    class DatabaseException(message: String, cause: Throwable? = null) : UmbralException(message, cause)
    class ServiceException(message: String, cause: Throwable? = null) : UmbralException(message, cause)
}
```

### Global Error Handler

```kotlin
class GlobalExceptionHandler(
    private val context: Context,
    private val analyticsLogger: AnalyticsLogger? = null
) {
    fun handle(exception: Throwable): UserFacingError {
        // Log to analytics/crash reporting
        analyticsLogger?.logException(exception)

        return when (exception) {
            is UmbralException.ValidationException -> {
                UserFacingError(
                    title = context.getString(R.string.error_validation_title),
                    message = exception.message ?: context.getString(R.string.error_validation_message),
                    severity = ErrorSeverity.WARNING
                )
            }
            is UmbralException.NotFoundException -> {
                UserFacingError(
                    title = context.getString(R.string.error_not_found_title),
                    message = exception.message ?: context.getString(R.string.error_not_found_message),
                    severity = ErrorSeverity.ERROR
                )
            }
            is UmbralException.PermissionDeniedException -> {
                UserFacingError(
                    title = context.getString(R.string.error_permission_title),
                    message = exception.message ?: context.getString(R.string.error_permission_message),
                    severity = ErrorSeverity.ERROR,
                    action = ErrorAction.OPEN_SETTINGS
                )
            }
            is UmbralException.NfcException -> {
                UserFacingError(
                    title = context.getString(R.string.error_nfc_title),
                    message = exception.message ?: context.getString(R.string.error_nfc_message),
                    severity = ErrorSeverity.ERROR
                )
            }
            is IOException -> {
                UserFacingError(
                    title = context.getString(R.string.error_network_title),
                    message = context.getString(R.string.error_network_message),
                    severity = ErrorSeverity.WARNING,
                    action = ErrorAction.RETRY
                )
            }
            else -> {
                UserFacingError(
                    title = context.getString(R.string.error_unknown_title),
                    message = context.getString(R.string.error_unknown_message),
                    severity = ErrorSeverity.ERROR
                )
            }
        }
    }
}

data class UserFacingError(
    val title: String,
    val message: String,
    val severity: ErrorSeverity,
    val action: ErrorAction? = null
)

enum class ErrorSeverity {
    INFO,
    WARNING,
    ERROR,
    CRITICAL
}

enum class ErrorAction {
    RETRY,
    OPEN_SETTINGS,
    CONTACT_SUPPORT
}
```

---

## 7.13 State Management (StateFlow Pattern)

### Repository Implementation Example

```kotlin
class BlockingProfileRepositoryImpl @Inject constructor(
    private val profileDao: BlockingProfileDao,
    private val blockedAppDao: BlockedAppDao,
    private val mapper: ProfileMapper
) : BlockingProfileRepository {

    // Flow para observar cambios
    override fun getActiveProfile(): Flow<BlockingProfile?> {
        return profileDao.getActiveProfileFlow()
            .map { entity ->
                entity?.let { mapper.toDomain(it) }
            }
    }

    override fun getAllProfiles(): Flow<List<BlockingProfile>> {
        return profileDao.getAllFlow()
            .map { entities ->
                entities.map { mapper.toDomain(it) }
            }
    }

    // Operaciones suspend para acciones únicas
    override suspend fun createProfile(profile: BlockingProfile) {
        val entity = mapper.toEntity(profile)
        profileDao.insert(entity)
    }

    override suspend fun getProfileById(id: String): BlockingProfile? {
        return profileDao.getById(id)?.let { mapper.toDomain(it) }
    }

    override suspend fun updateProfile(profile: BlockingProfile) {
        val entity = mapper.toEntity(profile)
        profileDao.update(entity)
    }

    override suspend fun deleteProfile(id: String) {
        profileDao.deleteById(id)
    }

    override suspend fun activateProfile(id: String) {
        profileDao.setActiveProfile(id)
    }

    override suspend fun deactivateAllProfiles() {
        profileDao.deactivateAll()
    }

    override suspend fun getBlockedApps(profileId: String): List<BlockedApp> {
        return blockedAppDao.getBlockedByProfile(profileId)
            .map { mapper.toBlockedAppDomain(it) }
    }
}
```

---

## 7.14 Mappers (Entity ↔ Domain)

### ProfileMapper

```kotlin
class ProfileMapper @Inject constructor() {

    fun toDomain(entity: BlockingProfileEntity): BlockingProfile {
        return BlockingProfile(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            icon = ProfileIcon.valueOf(entity.icon.uppercase()),
            color = entity.color,
            isActive = entity.isActive,
            isPredefined = entity.isPredefined,
            autoUnlockMinutes = entity.autoUnlockMinutes,
            requirePhysicalUnlock = entity.requirePhysicalUnlock,
            createdAt = Instant.ofEpochMilli(entity.createdAt),
            updatedAt = Instant.ofEpochMilli(entity.updatedAt)
        )
    }

    fun toEntity(domain: BlockingProfile): BlockingProfileEntity {
        return BlockingProfileEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            icon = domain.icon.iconName,
            color = domain.color,
            isActive = domain.isActive,
            isPredefined = domain.isPredefined,
            autoUnlockMinutes = domain.autoUnlockMinutes,
            requirePhysicalUnlock = domain.requirePhysicalUnlock,
            createdAt = domain.createdAt.toEpochMilli(),
            updatedAt = domain.updatedAt.toEpochMilli()
        )
    }

    fun toBlockedAppDomain(entity: BlockedAppEntity): BlockedApp {
        return BlockedApp(
            id = entity.id,
            profileId = entity.profileId,
            packageName = entity.packageName,
            appName = entity.appName,
            appIcon = null, // Load separately if needed
            category = entity.category?.let { AppCategory.valueOf(it) },
            isWhitelisted = entity.isWhitelisted,
            timesBlocked = entity.timesBlocked,
            addedAt = Instant.ofEpochMilli(entity.addedAt)
        )
    }

    fun toBlockedAppEntity(domain: BlockedApp): BlockedAppEntity {
        return BlockedAppEntity(
            id = domain.id,
            profileId = domain.profileId,
            packageName = domain.packageName,
            appName = domain.appName,
            appIconUri = null,
            category = domain.category?.name,
            isWhitelisted = domain.isWhitelisted,
            timesBlocked = domain.timesBlocked,
            addedAt = domain.addedAt.toEpochMilli()
        )
    }
}
```

---

## 7.15 Testing Strategy

### Unit Tests (Domain Layer)

```kotlin
class CreateProfileUseCaseTest {

    private lateinit var profileRepository: BlockingProfileRepository
    private lateinit var useCase: CreateProfileUseCase

    @Before
    fun setup() {
        profileRepository = mockk()
        useCase = CreateProfileUseCase(profileRepository)
    }

    @Test
    fun `createProfile with valid data returns success`() = runTest {
        // Given
        val name = "Social Media"
        val icon = ProfileIcon.SOCIAL_MEDIA
        val color = 0xFF6200EE.toInt()

        coEvery { profileRepository.createProfile(any()) } just Runs

        // When
        val result = useCase(name, null, icon, color)

        // Then
        assertTrue(result.isSuccess)
        coVerify { profileRepository.createProfile(any()) }
    }

    @Test
    fun `createProfile with blank name returns failure`() = runTest {
        // When
        val result = useCase("", null, ProfileIcon.SOCIAL_MEDIA, 0xFF6200EE.toInt())

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
    }

    @Test
    fun `createProfile with name too long returns failure`() = runTest {
        // Given
        val longName = "a".repeat(51)

        // When
        val result = useCase(longName, null, ProfileIcon.SOCIAL_MEDIA, 0xFF6200EE.toInt())

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
    }
}
```

### Integration Tests (Data Layer)

```kotlin
@RunWith(AndroidJUnit4::class)
class BlockingProfileDaoTest {

    private lateinit var database: UmbralDatabase
    private lateinit var profileDao: BlockingProfileDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            UmbralDatabase::class.java
        ).build()
        profileDao = database.blockingProfileDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertProfile_and_getById_returnsCorrectProfile() = runTest {
        // Given
        val profile = BlockingProfileEntity(
            id = "test-id",
            name = "Test Profile",
            description = "Test Description",
            icon = "social_media",
            color = 0xFF6200EE.toInt(),
            isActive = false,
            isPredefined = false,
            autoUnlockMinutes = null,
            requirePhysicalUnlock = false
        )

        // When
        profileDao.insert(profile)
        val retrieved = profileDao.getById("test-id")

        // Then
        assertEquals(profile.id, retrieved?.id)
        assertEquals(profile.name, retrieved?.name)
    }

    @Test
    fun setActiveProfile_deactivatesOthers() = runTest {
        // Given
        val profile1 = createTestProfile("id1", "Profile 1")
        val profile2 = createTestProfile("id2", "Profile 2")

        profileDao.insert(profile1)
        profileDao.insert(profile2)

        // When
        profileDao.setActiveProfile("id2")

        // Then
        val activeProfile = profileDao.getActiveProfile()
        assertEquals("id2", activeProfile?.id)
        assertTrue(activeProfile?.isActive == true)

        val profile1Retrieved = profileDao.getById("id1")
        assertFalse(profile1Retrieved?.isActive == true)
    }
}
```

### UI Tests (Presentation Layer)

```kotlin
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun homeScreen_displaysActiveProfile() {
        // Given
        val testProfile = BlockingProfile(
            id = "test-id",
            name = "Social Media",
            description = null,
            icon = ProfileIcon.SOCIAL_MEDIA,
            color = 0xFF6200EE.toInt(),
            isActive = true,
            isPredefined = false,
            autoUnlockMinutes = null,
            requirePhysicalUnlock = false,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        // When
        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(activeProfile = testProfile),
                onToggleBlocking = {},
                onNavigateToProfiles = {},
                onNavigateToStats = {},
                onNavigateToSettings = {}
            )
        }

        // Then
        composeTestRule
            .onNodeWithText("Social Media")
            .assertIsDisplayed()
    }

    @Test
    fun homeScreen_toggleBlocking_triggersAction() {
        // Given
        var toggleCalled = false

        composeTestRule.setContent {
            HomeScreen(
                uiState = HomeUiState(isBlocking = false),
                onToggleBlocking = { toggleCalled = true },
                onNavigateToProfiles = {},
                onNavigateToStats = {},
                onNavigateToSettings = {}
            )
        }

        // When
        composeTestRule
            .onNodeWithContentDescription("Toggle blocking")
            .performClick()

        // Then
        assertTrue(toggleCalled)
    }
}
```

---

## 7.16 Notification Channels

### NotificationHelper

```kotlin
class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val blockingChannel = NotificationChannel(
                CHANNEL_BLOCKING,
                context.getString(R.string.notification_channel_blocking),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.notification_channel_blocking_desc)
                setShowBadge(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }

            val alertsChannel = NotificationChannel(
                CHANNEL_ALERTS,
                context.getString(R.string.notification_channel_alerts),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.notification_channel_alerts_desc)
                setShowBadge(true)
                enableVibration(true)
            }

            notificationManager.createNotificationChannels(
                listOf(blockingChannel, alertsChannel)
            )
        }
    }

    fun createBlockingNotification(): Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_BLOCKING)
            .setContentTitle(context.getString(R.string.notification_blocking_title))
            .setContentText(context.getString(R.string.notification_blocking_text))
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .build()
    }

    fun showBlockingActiveNotification(profileName: String) {
        val notification = createBlockingNotification()
        notificationManager.notify(NOTIFICATION_ID_BLOCKING, notification)
    }

    fun dismissBlockingNotification() {
        notificationManager.cancel(NOTIFICATION_ID_BLOCKING)
    }

    fun showBlockAttemptNotification(appName: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setContentTitle(context.getString(R.string.notification_block_attempt_title))
            .setContentText(
                context.getString(R.string.notification_block_attempt_text, appName)
            )
            .setSmallIcon(R.drawable.ic_notification)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID_BLOCK_ATTEMPT, notification)
    }

    companion object {
        const val CHANNEL_BLOCKING = "blocking"
        const val CHANNEL_ALERTS = "alerts"

        const val NOTIFICATION_ID_BLOCKING = 1001
        const val NOTIFICATION_ID_BLOCK_ATTEMPT = 1002
    }
}
```

---

## 7.17 Constants

```kotlin
object Constants {
    // Database
    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "umbral.db"

    // Preferences
    const val PREF_ONBOARDING_COMPLETED = "onboarding_completed"
    const val PREF_DARK_MODE = "dark_mode"
    const val PREF_LANGUAGE = "language"

    // NFC
    const val NFC_MIME_TYPE = "application/vnd.umbral.nfc"
    const val NFC_URL_SCHEME = "umbral://activate"

    // Timeouts
    const val NFC_READ_TIMEOUT_MS = 5000L
    const val NFC_WRITE_TIMEOUT_MS = 10000L

    // Limits
    const val MAX_PROFILE_NAME_LENGTH = 50
    const val MAX_BLOCKED_APPS_PER_PROFILE = 100
    const val MAX_NFC_TAGS = 10

    // Performance
    const val TARGET_NFC_LATENCY_MS = 50
    const val TARGET_BLOCKING_LATENCY_MS = 100

    // Analytics (if enabled)
    const val MAX_DAILY_BATTERY_DRAIN_PERCENT = 5
}
```

---

**FIN DE ARQUITECTURA DETALLADA - PARTE 2**

Esta arquitectura completamente especificada está lista para implementación directa. Cada componente ha sido diseñado siguiendo Clean Architecture, SOLID principles, y las mejores prácticas de Android moderno.