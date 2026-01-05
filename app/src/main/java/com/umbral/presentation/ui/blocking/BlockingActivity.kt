package com.umbral.presentation.ui.blocking

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.umbral.R
import com.umbral.data.local.preferences.UmbralPreferences
import com.umbral.domain.blocking.BlockingManager
import com.umbral.presentation.ui.components.ButtonVariant
import com.umbral.presentation.ui.components.UmbralButton
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BlockingActivity : ComponentActivity() {

    @Inject
    lateinit var blockingManager: BlockingManager

    @Inject
    lateinit var preferences: UmbralPreferences

    companion object {
        const val EXTRA_BLOCKED_PACKAGE = "blocked_package"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val blockedPackage = intent.getStringExtra(EXTRA_BLOCKED_PACKAGE) ?: ""
        Timber.d("BlockingActivity started for package: $blockedPackage")

        setContent {
            UmbralTheme {
                val blockingState by blockingManager.blockingState.collectAsStateWithLifecycle()
                val currentStreak by preferences.currentStreak.collectAsState(
                    initial = runBlocking { preferences.currentStreak.first() }
                )

                BlockingScreen(
                    blockedPackageName = blockedPackage,
                    profileName = blockingState.activeProfileName,
                    currentStreak = currentStreak,
                    isStrictMode = blockingState.isStrictMode,
                    onGoHome = { goToHomeScreen() },
                    onUnlock = { handleUnlock() }
                )
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // Go to home screen instead of back to blocked app
        goToHomeScreen()
    }

    private fun goToHomeScreen() {
        val homeIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        finish()
    }

    private fun handleUnlock() {
        val state = blockingManager.blockingState.value

        if (state.isStrictMode) {
            // In strict mode, we need NFC to disable
            // For now, just go home - NFC scan will be handled by NfcScanScreen
            goToHomeScreen()
            return
        }

        lifecycleScope.launch {
            val result = blockingManager.stopBlocking(requireNfc = false)
            if (result.isSuccess) {
                Timber.d("Blocking disabled")
                goToHomeScreen()
            } else {
                Timber.e("Failed to disable blocking: ${result.exceptionOrNull()}")
            }
        }
    }
}

// =============================================================================
// BLOCKING SCREEN - SUPPORTIVE DESIGN
// =============================================================================

@Composable
fun BlockingScreen(
    blockedPackageName: String,
    profileName: String?,
    currentStreak: Int,
    isStrictMode: Boolean,
    onGoHome: () -> Unit,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.85f)
                    )
                )
            )
    ) {
        // Breathing overlay for calming effect
        BreathingOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = UmbralSpacing.screenHorizontal)
                .padding(vertical = UmbralSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Breathing shield icon
            BreathingShieldIcon(
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(UmbralSpacing.xl))

            // Supportive message
            Text(
                text = stringResource(R.string.blocking_screen_supportive_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(UmbralSpacing.sm))

            Text(
                text = stringResource(R.string.blocking_screen_supportive_message),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            // Show profile name if available
            if (profileName != null) {
                Spacer(modifier = Modifier.height(UmbralSpacing.xs))
                Text(
                    text = "Perfil: $profileName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.lg))

            // Streak motivational display
            if (currentStreak > 0) {
                StreakMotivation(streak = currentStreak)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Primary action - Go Home
            UmbralButton(
                text = stringResource(R.string.go_home),
                onClick = onGoHome,
                variant = ButtonVariant.Secondary,
                fullWidth = true,
                leadingIcon = Icons.Default.Home
            )

            // Unlock option (only if not strict mode)
            if (!isStrictMode) {
                Spacer(modifier = Modifier.height(UmbralSpacing.md))

                TextButton(onClick = onUnlock) {
                    Icon(
                        imageVector = Icons.Outlined.Nfc,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(UmbralSpacing.xs))
                    Text(
                        text = stringResource(R.string.unlock_with_nfc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            } else {
                // Strict mode indicator
                Spacer(modifier = Modifier.height(UmbralSpacing.md))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(horizontal = UmbralSpacing.md, vertical = UmbralSpacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Nfc,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.width(UmbralSpacing.sm))
                    Text(
                        text = stringResource(R.string.blocking_strict_mode_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.xl))
        }
    }
}

// =============================================================================
// BREATHING SHIELD ICON
// =============================================================================

@Composable
private fun BreathingShieldIcon(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .size(140.dp)
                .scale(scale * 1.2f)
                .background(
                    color = Color.White.copy(alpha = glowAlpha * 0.5f),
                    shape = CircleShape
                )
        )

        // Inner glow
        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale)
                .background(
                    color = Color.White.copy(alpha = glowAlpha),
                    shape = CircleShape
                )
        )

        // Shield icon
        Icon(
            imageVector = Icons.Filled.Shield,
            contentDescription = "Protección activa",
            tint = Color.White,
            modifier = Modifier
                .size(64.dp)
                .scale(scale)
        )
    }
}

// =============================================================================
// STREAK MOTIVATION
// =============================================================================

@Composable
private fun StreakMotivation(
    streak: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.large
            )
            .padding(horizontal = UmbralSpacing.lg, vertical = UmbralSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🔥",
            fontSize = 28.sp
        )
        Spacer(modifier = Modifier.width(UmbralSpacing.sm))
        Column {
            Text(
                text = "$streak ${if (streak == 1) "día" else "días"}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "de enfoque continuo",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// =============================================================================
// BREATHING OVERLAY
// =============================================================================

@Composable
private fun BreathingOverlay(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "overlay")

    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 300f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetX"
    )

    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offsetY"
    )

    // Subtle moving gradient overlay for calming effect
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(offsetX * 3, offsetY * 3),
                    radius = 600f
                )
            )
    )
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Blocking Screen - With Streak", showBackground = true)
@Composable
private fun BlockingScreenWithStreakPreview() {
    UmbralTheme {
        BlockingScreen(
            blockedPackageName = "com.twitter.android",
            profileName = "Productividad",
            currentStreak = 12,
            isStrictMode = false,
            onGoHome = {},
            onUnlock = {}
        )
    }
}

@Preview(name = "Blocking Screen - Strict Mode", showBackground = true)
@Composable
private fun BlockingScreenStrictModePreview() {
    UmbralTheme {
        BlockingScreen(
            blockedPackageName = "com.instagram.android",
            profileName = "Trabajo",
            currentStreak = 7,
            isStrictMode = true,
            onGoHome = {},
            onUnlock = {}
        )
    }
}

@Preview(name = "Blocking Screen - No Streak", showBackground = true)
@Composable
private fun BlockingScreenNoStreakPreview() {
    UmbralTheme {
        BlockingScreen(
            blockedPackageName = "com.facebook.katana",
            profileName = null,
            currentStreak = 0,
            isStrictMode = false,
            onGoHome = {},
            onUnlock = {}
        )
    }
}

@Preview(name = "Breathing Shield Icon", showBackground = true)
@Composable
private fun BreathingShieldIconPreview() {
    UmbralTheme {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            BreathingShieldIcon(modifier = Modifier.size(140.dp))
        }
    }
}

@Preview(name = "Dark Theme", showBackground = true)
@Composable
private fun BlockingScreenDarkPreview() {
    UmbralTheme(darkTheme = true) {
        BlockingScreen(
            blockedPackageName = "com.twitter.android",
            profileName = "Noche",
            currentStreak = 30,
            isStrictMode = false,
            onGoHome = {},
            onUnlock = {}
        )
    }
}
