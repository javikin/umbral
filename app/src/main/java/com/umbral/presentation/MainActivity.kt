package com.umbral.presentation

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.umbral.data.local.preferences.UmbralPreferences
import com.umbral.domain.blocking.ProfileRepository
import com.umbral.domain.nfc.NfcManager
import com.umbral.domain.nfc.NfcResult
import com.umbral.domain.nfc.TagEvent
import com.umbral.presentation.navigation.MainNavigation
import com.umbral.presentation.ui.theme.UmbralTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var nfcManager: NfcManager

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Inject
    lateinit var preferences: UmbralPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UmbralTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation()
                }
            }
        }

        // Handle NFC intent if activity was started from tag discovery
        handleNfcIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        // Enable NFC foreground dispatch when activity is in foreground
        nfcManager.enableForegroundDispatch(this)
        nfcManager.refreshState()
    }

    override fun onPause() {
        super.onPause()
        // Disable NFC foreground dispatch when activity goes to background
        nfcManager.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle NFC tag when activity is already running
        handleNfcIntent(intent)
    }

    private fun handleNfcIntent(intent: Intent?) {
        if (intent == null) return

        val action = intent.action
        if (action == NfcAdapter.ACTION_NDEF_DISCOVERED ||
            action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            action == NfcAdapter.ACTION_TECH_DISCOVERED
        ) {
            Timber.d("NFC intent received: $action")
            lifecycleScope.launch {
                val result = nfcManager.processTagIntent(intent)

                // Handle known tag globally - toggle profile/blocking
                if (result is NfcResult.Success) {
                    val event = result.data
                    if (event is TagEvent.KnownTag) {
                        Timber.d("Known tag scanned: ${event.tag.name}, profileId: ${event.tag.profileId}")
                        toggleProfileOrBlocking(event.tag.profileId)
                    }
                }
            }
        }
    }

    private suspend fun toggleProfileOrBlocking(profileId: String?) {
        if (profileId != null) {
            // Toggle the associated profile
            val profile = profileRepository.getProfileById(profileId)
            if (profile != null) {
                if (profile.isActive) {
                    // Profile is active, deactivate it
                    Timber.d("Deactivating profile: ${profile.name}")
                    profileRepository.deactivateAllProfiles()
                    preferences.setBlockingEnabled(false)
                } else {
                    // Profile is not active, activate it
                    Timber.d("Activating profile: ${profile.name}")
                    profileRepository.activateProfile(profileId)
                    preferences.setBlockingEnabled(true)
                }
            } else {
                // Profile not found, fallback to toggle blocking
                toggleBlocking()
            }
        } else {
            // No profile associated, just toggle blocking
            toggleBlocking()
        }
    }

    private suspend fun toggleBlocking() {
        val isEnabled = preferences.blockingEnabled.first()
        Timber.d("Toggling blocking: $isEnabled -> ${!isEnabled}")
        preferences.setBlockingEnabled(!isEnabled)
    }
}
