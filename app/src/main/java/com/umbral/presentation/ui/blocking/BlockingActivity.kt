package com.umbral.presentation.ui.blocking

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Diamond
import androidx.compose.material.icons.outlined.EmojiObjects
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.Waves
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

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
// MENSAJES MOTIVACIONALES POOL - Tendencias 2026
// =============================================================================

private data class MotivationalMessage(
    val text: String,
    val icon: ImageVector
)

private val motivationalMessages = listOf(
    MotivationalMessage(
        "Estás eligiendo conscientemente tu tiempo",
        Icons.Outlined.SelfImprovement
    ),
    MotivationalMessage(
        "Tu yo futuro te lo agradecerá",
        Icons.Outlined.EmojiObjects
    ),
    MotivationalMessage(
        "Pequeñas decisiones, grandes cambios",
        Icons.AutoMirrored.Outlined.TrendingUp
    ),
    MotivationalMessage(
        "Estás construyendo un mejor hábito",
        Icons.Outlined.Stars
    ),
    MotivationalMessage(
        "El control es tuyo",
        Icons.Outlined.Shield
    ),
    MotivationalMessage(
        "Cada momento cuenta",
        Icons.Outlined.Timer
    ),
    MotivationalMessage(
        "Tu atención es valiosa",
        Icons.Outlined.Diamond
    ),
    MotivationalMessage(
        "Enfócate en lo que importa",
        Icons.Outlined.Favorite
    ),
    MotivationalMessage(
        "Estás presente, estás aquí",
        Icons.Outlined.WbSunny
    ),
    MotivationalMessage(
        "Tu bienestar primero",
        Icons.Outlined.Spa
    ),
    MotivationalMessage(
        "Eligiendo calma sobre caos",
        Icons.Outlined.Waves
    ),
    MotivationalMessage(
        "Tu concentración merece protección",
        Icons.Outlined.Security
    )
)

// =============================================================================
// BLOCKING SCREEN - REDISEÑO 2026
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
    val scrollState = rememberScrollState()

    // Colores dark mode - azul profundo calmante
    val darkBlueTop = Color(0xFF0D1B2A)     // Azul noche profundo
    val darkBlueMid = Color(0xFF1B263B)     // Azul oscuro medio
    val darkBlueBottom = Color(0xFF162447)  // Azul oscuro con toque púrpura

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        darkBlueTop,
                        darkBlueMid,
                        darkBlueBottom
                    )
                )
            )
    ) {
        // Breathing overlay for calming effect
        BreathingOverlay()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = UmbralSpacing.screenHorizontal)
                .padding(vertical = UmbralSpacing.xxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Spacer(modifier = Modifier.weight(0.5f))

            // Breathing shield icon con círculos concéntricos
            BreathingShieldIcon(
                modifier = Modifier.size(140.dp)
            )

            Spacer(modifier = Modifier.height(UmbralSpacing.md))

            // Título contextual
            Text(
                text = stringResource(R.string.blocking_screen_supportive_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            // Mensaje motivacional rotativo con glassmorphism
            MotivationalCard()

            // Stats card - Streak
            if (currentStreak > 0) {
                StreakCard(streak = currentStreak)
            }

            // Perfil activo card
            if (profileName != null) {
                ProfileCard(profileName = profileName)
            }

            // Chip de modo estricto
            if (isStrictMode) {
                StrictModeChip()
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
                TextButton(
                    onClick = onUnlock,
                    modifier = Modifier.semantics {
                        contentDescription = "Desbloquear con NFC"
                    }
                ) {
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

            Spacer(modifier = Modifier.height(UmbralSpacing.md))
        }
    }
}

// =============================================================================
// MOTIVATIONAL CARD - Con rotación automática
// =============================================================================

@Composable
private fun MotivationalCard(
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(Random.nextInt(motivationalMessages.size)) }
    var previousIndices by remember { mutableStateOf(listOf<Int>()) }

    // Rotación automática cada 8 segundos
    LaunchedEffect(Unit) {
        while (true) {
            delay(8000)

            // Evitar repetición inmediata
            val availableIndices = motivationalMessages.indices.filter {
                it !in previousIndices && it != currentIndex
            }

            currentIndex = if (availableIndices.isNotEmpty()) {
                availableIndices.random()
            } else {
                previousIndices = emptyList()
                (motivationalMessages.indices).filter { it != currentIndex }.random()
            }

            previousIndices = (previousIndices + currentIndex).takeLast(3)
        }
    }

    val currentMessage = motivationalMessages[currentIndex]

    // Crossfade animation
    AnimatedContent(
        targetState = currentMessage,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "messageTransition"
    ) { message ->
        GlassCard(
            modifier = modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = message.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// =============================================================================
// STREAK CARD - Glassmorphism
// =============================================================================

@Composable
private fun StreakCard(
    streak: Int,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.LocalFireDepartment,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(28.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Racha actual",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = "$streak ${if (streak == 1) "día" else "días"} de enfoque",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

// =============================================================================
// PROFILE CARD - Glassmorphism
// =============================================================================

@Composable
private fun ProfileCard(
    profileName: String,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = "Perfil activo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Text(
                    text = profileName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}

// =============================================================================
// STRICT MODE CHIP - Con pulso animado
// =============================================================================

@Composable
private fun StrictModeChip(
    modifier: Modifier = Modifier
) {
    var pulseCount by remember { mutableIntStateOf(0) }
    val infiniteTransition = rememberInfiniteTransition(label = "strictPulse")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "strictAlpha"
    )

    LaunchedEffect(alpha) {
        if (alpha <= 0.71f) {
            pulseCount++
        }
    }

    val finalAlpha = if (pulseCount >= 6) 1f else alpha

    AssistChip(
        onClick = {},
        label = {
            Text(
                text = "Modo estricto activo",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = Color.White
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color.White.copy(alpha = 0.2f),
            labelColor = Color.White,
            leadingIconContentColor = Color.White
        ),
        border = null,
        modifier = modifier.alpha(finalAlpha)
    )
}

// =============================================================================
// GLASS CARD - Glassmorphism Component
// =============================================================================

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = Color.White.copy(alpha = 0.08f),  // Más sutil en dark mode
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        content()
    }
}

// =============================================================================
// BREATHING SHIELD ICON - Con círculos concéntricos
// =============================================================================

@Composable
private fun BreathingShieldIcon(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")

    // Animación de respiración más lenta y suave
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )

    val innerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingAlpha"
    )

    // Colores para dark mode
    val glowColor = Color(0xFF4A90D9)  // Azul brillante para contraste

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // Círculo exterior - glow difuso azul
        Box(
            modifier = Modifier
                .size(150.dp)
                .scale(scale * 1.15f)
                .background(
                    color = glowColor.copy(alpha = innerAlpha * 0.2f),
                    shape = CircleShape
                )
        )

        // Círculo medio - glow más intenso
        Box(
            modifier = Modifier
                .size(115.dp)
                .scale(scale)
                .background(
                    color = glowColor.copy(alpha = innerAlpha * 0.4f),
                    shape = CircleShape
                )
        )

        // Círculo interior con icono
        Box(
            modifier = Modifier
                .size(75.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF5BA3E0),
                            Color(0xFF3D7BBF)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Shield,
                contentDescription = "Protección activa",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
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
