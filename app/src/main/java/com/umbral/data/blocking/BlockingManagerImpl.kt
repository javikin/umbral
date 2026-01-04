package com.umbral.data.blocking

import com.umbral.domain.blocking.BlockingManager
import com.umbral.domain.blocking.BlockingState
import com.umbral.domain.blocking.ForegroundAppMonitor
import com.umbral.domain.blocking.ProfileRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockingManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileRepository: ProfileRepository,
    private val foregroundAppMonitor: ForegroundAppMonitor
) : BlockingManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _blockingState = MutableStateFlow(BlockingState())
    override val blockingState: StateFlow<BlockingState> = _blockingState.asStateFlow()

    override val isBlocking: Boolean
        get() = _blockingState.value.isActive

    init {
        // Observe active profile changes and update blocking state
        scope.launch {
            profileRepository.getActiveProfile().collect { profile ->
                if (profile != null) {
                    _blockingState.value = BlockingState(
                        isActive = true,
                        activeProfileId = profile.id,
                        activeProfileName = profile.name,
                        blockedApps = profile.blockedApps.toSet(),
                        isStrictMode = profile.isStrictMode
                    )
                    startBlockingService()
                } else {
                    _blockingState.value = BlockingState()
                    stopBlockingService()
                }
            }
        }
    }

    override suspend fun startBlocking(profileId: String): Result<Unit> {
        return try {
            Timber.d("Starting blocking with profile: $profileId")
            profileRepository.activateProfile(profileId).getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error starting blocking")
            Result.failure(e)
        }
    }

    override suspend fun stopBlocking(requireNfc: Boolean): Result<Unit> {
        return try {
            val currentState = _blockingState.value

            // Check if strict mode requires NFC
            if (requireNfc && currentState.isStrictMode) {
                return Result.failure(
                    IllegalStateException("Strict mode requires NFC tag to disable blocking")
                )
            }

            Timber.d("Stopping blocking")
            profileRepository.deactivateAllProfiles().getOrThrow()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error stopping blocking")
            Result.failure(e)
        }
    }

    override suspend fun toggleBlocking(profileId: String): Result<Unit> {
        val currentState = _blockingState.value

        return if (currentState.isActive && currentState.activeProfileId == profileId) {
            // Same profile is active, stop blocking (respect strict mode)
            stopBlocking(requireNfc = currentState.isStrictMode)
        } else {
            // Different profile or not active, start blocking
            startBlocking(profileId)
        }
    }

    override fun isAppBlocked(packageName: String): Boolean {
        val state = _blockingState.value
        if (!state.isActive) return false

        // Never block ourselves or essential system apps
        if (packageName == context.packageName) return false
        if (isEssentialSystemApp(packageName)) return false

        return packageName in state.blockedApps
    }

    override suspend fun getCurrentForegroundApp(): String? {
        return foregroundAppMonitor.getCurrentForegroundApp()
    }

    private fun isEssentialSystemApp(packageName: String): Boolean {
        return packageName in setOf(
            "com.android.systemui",
            "com.android.settings",
            "com.android.phone",
            "com.android.dialer",
            "com.android.emergency",
            "com.google.android.dialer",
            "com.samsung.android.dialer"
        )
    }

    private fun startBlockingService() {
        try {
            val intent = Intent(context, BlockingService::class.java).apply {
                action = BlockingService.ACTION_START
            }
            context.startForegroundService(intent)
            Timber.d("Blocking service started")
        } catch (e: Exception) {
            Timber.e(e, "Error starting blocking service")
        }
    }

    private fun stopBlockingService() {
        try {
            val intent = Intent(context, BlockingService::class.java).apply {
                action = BlockingService.ACTION_STOP
            }
            context.startService(intent)
            Timber.d("Blocking service stop requested")
        } catch (e: Exception) {
            Timber.e(e, "Error stopping blocking service")
        }
    }
}
