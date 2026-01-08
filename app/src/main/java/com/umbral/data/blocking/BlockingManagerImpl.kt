package com.umbral.data.blocking

import com.umbral.domain.blocking.BlockingManager
import com.umbral.domain.blocking.BlockingState
import com.umbral.domain.blocking.ForegroundAppMonitor
import com.umbral.domain.blocking.ProfileRepository
import com.umbral.domain.blocking.SessionEndedEvent
import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.expedition.domain.model.SessionReward
import com.umbral.expedition.domain.usecase.CheckAchievementsUseCase
import com.umbral.expedition.domain.usecase.GainEnergyUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockingManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileRepository: ProfileRepository,
    private val foregroundAppMonitor: ForegroundAppMonitor,
    private val expeditionRepository: ExpeditionRepository,
    private val gainEnergyUseCase: GainEnergyUseCase,
    private val checkAchievementsUseCase: CheckAchievementsUseCase
) : BlockingManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _blockingState = MutableStateFlow(BlockingState())
    override val blockingState: StateFlow<BlockingState> = _blockingState.asStateFlow()

    private val _rewardEvent = MutableSharedFlow<SessionReward>(extraBufferCapacity = 1)
    override val rewardEvent: SharedFlow<SessionReward> = _rewardEvent.asSharedFlow()

    private val _sessionEndedEvent = MutableSharedFlow<SessionEndedEvent>(extraBufferCapacity = 1)
    override val sessionEndedEvent: SharedFlow<SessionEndedEvent> = _sessionEndedEvent.asSharedFlow()

    override val isBlocking: Boolean
        get() = _blockingState.value.isActive

    // Track total sessions for achievements
    private var totalSessionsCompleted = 0

    init {
        // Observe active profile changes and update blocking state
        scope.launch {
            profileRepository.getActiveProfile().collect { profile ->
                val previousState = _blockingState.value

                if (profile != null) {
                    // Check if this is a new session starting
                    val isNewSession = !previousState.isActive
                    val sessionStartTime = if (isNewSession) {
                        System.currentTimeMillis()
                    } else {
                        previousState.sessionStartTime
                    }

                    // Generate new sessionId for new sessions
                    val sessionId = if (isNewSession) {
                        UUID.randomUUID().toString()
                    } else {
                        previousState.sessionId
                    }

                    _blockingState.value = BlockingState(
                        isActive = true,
                        activeProfileId = profile.id,
                        activeProfileName = profile.name,
                        blockedApps = profile.blockedApps.toSet(),
                        isStrictMode = profile.isStrictMode,
                        sessionStartTime = sessionStartTime,
                        sessionId = sessionId
                    )
                    startBlockingService()
                } else {
                    // Session ended - emit event and award rewards if there was an active session
                    if (previousState.isActive && previousState.sessionStartTime != null) {
                        val durationMs = System.currentTimeMillis() - previousState.sessionStartTime
                        val durationMinutes = (durationMs / 60_000).toLong()

                        // Emit session ended event for notification summary
                        if (previousState.sessionId != null && previousState.activeProfileId != null) {
                            _sessionEndedEvent.tryEmit(
                                SessionEndedEvent(
                                    sessionId = previousState.sessionId,
                                    profileId = previousState.activeProfileId,
                                    durationMinutes = durationMinutes
                                )
                            )
                            Timber.d("Session ended event emitted: ${previousState.sessionId}")
                        }

                        // Award expedition rewards
                        awardExpeditionRewards(previousState.sessionStartTime)
                    }
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

    /**
     * Award expedition rewards when a blocking session completes.
     * Calculates energy gained, XP, and checks for achievements.
     */
    private fun awardExpeditionRewards(sessionStartTime: Long) {
        scope.launch {
            try {
                // Calculate session duration in minutes
                val durationMs = System.currentTimeMillis() - sessionStartTime
                val durationMinutes = (durationMs / 60_000).toInt()

                // Only award rewards for sessions of at least 1 minute
                if (durationMinutes < 1) {
                    Timber.d("Session too short for rewards: $durationMinutes minutes")
                    return@launch
                }

                Timber.d("Awarding expedition rewards for $durationMinutes minutes")

                // Gain energy and XP
                val energyResult = gainEnergyUseCase(durationMinutes)
                Timber.d("Energy gained: ${energyResult.totalEnergy}, XP: ${energyResult.xpGained}")

                // Increment session count
                totalSessionsCompleted++

                // Get current progress for achievement checking
                val progress = expeditionRepository.getProgressOnce()
                val totalMinutes = progress?.totalBlockingMinutes ?: 0
                val currentStreak = progress?.currentStreak ?: 0

                // Check for achievements
                val achievements = checkAchievementsUseCase.checkBlockingAchievements(
                    sessionMinutes = durationMinutes,
                    totalMinutes = totalMinutes,
                    currentStreak = currentStreak,
                    totalSessions = totalSessionsCompleted
                )

                if (achievements.isNotEmpty()) {
                    Timber.d("Unlocked ${achievements.size} achievement(s): ${achievements.map { it.title }}")
                }

                // Emit reward event
                val reward = SessionReward(
                    energyResult = energyResult,
                    unlockedAchievements = achievements
                )
                _rewardEvent.tryEmit(reward)

            } catch (e: Exception) {
                // Don't crash the app if expedition rewards fail
                Timber.e(e, "Error awarding expedition rewards")
            }
        }
    }
}
