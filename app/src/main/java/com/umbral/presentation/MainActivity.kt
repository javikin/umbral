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
import com.umbral.domain.nfc.NfcManager
import com.umbral.presentation.navigation.MainNavigation
import com.umbral.presentation.ui.theme.UmbralTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var nfcManager: NfcManager

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
                nfcManager.processTagIntent(intent)
            }
        }
    }
}
