package com.umbral.presentation.ui.screens.blocking

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Blocking Screen - Rediseño 2026
 *
 * Pantalla mostrada cuando el usuario intenta abrir una app bloqueada.
 * Diseño basado en tendencias de bienestar digital 2025-2026.
 *
 * Features:
 * - Breathing animation en icono central
 * - Mensajes motivacionales rotativos
 * - Glassmorphism en cards
 * - Soporte para modo estricto y timer
 * - Accesibilidad completa
 */

// ========================================
// Data Classes
// ========================================

data class BlockingState(
    val profileName: String,
    val isStrictMode: Boolean = false,
    val timerMinutesRemaining: Int? = null,
    val focusedTimeToday: String = "0h 0min",
    val isFirstTime: Boolean = false
)

data class MotivationalMessage(
    val text: String,
    val icon: ImageVector
)

// ========================================
// Mensajes Motivacionales Pool
// ========================================

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
        Icons.Outlined.TrendingUp
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

// ========================================
// Main Screen Composable
// ========================================

@Composable
fun BlockingScreen(
    state: BlockingState,
    onBackToHome: () -> Unit,
    onEmergencyAccess: () -> Unit = {},
    onScanNfc: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    // Background gradient
    val backgroundColor = MaterialTheme.colorScheme.background
    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        backgroundColor,
                        surfaceColor
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Icono central con breathing animation
            BreathingIcon(
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Título contextual
            Text(
                text = "Estás en modo enfoque",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            // Mensaje motivacional rotativo
            MotivationalCard()

            // Stats card (si hay tiempo registrado)
            if (state.focusedTimeToday != "0h 0min") {
                StatsCard(focusedTime = state.focusedTimeToday)
            }

            // Perfil activo
            ProfileCard(profileName = state.profileName)

            // Timer card (si hay timer activo)
            if (state.timerMinutesRemaining != null) {
                TimerCard(minutesRemaining = state.timerMinutesRemaining)
            }

            // Chip de modo estricto
            if (state.isStrictMode) {
                StrictModeChip()
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón principal
            PrimaryActionButton(
                isStrictMode = state.isStrictMode,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    if (state.isStrictMode) {
                        onScanNfc()
                    } else {
                        onBackToHome()
                    }
                }
            )

            // Link de emergencia (solo si no es modo estricto)
            if (!state.isStrictMode) {
                TextButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onEmergencyAccess()
                    },
                    modifier = Modifier.semantics {
                        contentDescription = "Acceso de emergencia"
                    }
                ) {
                    Text(
                        text = "¿Necesitas acceso urgente?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ========================================
// Breathing Icon Component
// ========================================

@Composable
private fun BreathingIcon(
    modifier: Modifier = Modifier
) {
    // Breathing animation - escala infinita suave
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )

    val innerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingAlpha"
    )

    Box(
        modifier = modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        // Círculo exterior
        Surface(
            modifier = Modifier
                .size(120.dp)
                .scale(scale),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ) {}

        // Círculo medio
        Surface(
            modifier = Modifier
                .size(90.dp)
                .scale(scale),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = innerAlpha)
        ) {}

        // Círculo interior con icono
        Surface(
            modifier = Modifier.size(60.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = "Modo enfoque activo",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

// ========================================
// Motivational Card Component
// ========================================

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
                // Reiniciar si ya se mostraron todos
                previousIndices = emptyList()
                (0 until motivationalMessages.size).filter { it != currentIndex }.random()
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
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = message.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ========================================
// Stats Card Component
// ========================================

@Composable
private fun StatsCard(
    focusedTime: String,
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
                imageVector = Icons.Outlined.BarChart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = "Tiempo enfocado hoy",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = focusedTime,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// ========================================
// Profile Card Component
// ========================================

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
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = "Perfil activo",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = profileName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// ========================================
// Timer Card Component
// ========================================

@Composable
private fun TimerCard(
    minutesRemaining: Int,
    modifier: Modifier = Modifier
) {
    val hours = minutesRemaining / 60
    val minutes = minutesRemaining % 60
    val timeText = when {
        hours > 0 -> "${hours}h ${minutes}min"
        else -> "${minutes}min"
    }

    GlassCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Timer,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Se desbloqueará en",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = timeText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// ========================================
// Strict Mode Chip Component
// ========================================

@Composable
private fun StrictModeChip(
    modifier: Modifier = Modifier
) {
    // Pulso de alpha 3 veces
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
        if (alpha == 0.7f) {
            pulseCount++
        }
    }

    val finalAlpha = if (pulseCount >= 6) 1f else alpha

    AssistChip(
        onClick = {},
        label = {
            Text(
                text = "Modo estricto activo",
                style = MaterialTheme.typography.bodySmall
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            labelColor = MaterialTheme.colorScheme.onErrorContainer,
            leadingIconContentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        modifier = modifier.alpha(finalAlpha)
    )
}

// ========================================
// Primary Action Button Component
// ========================================

@Composable
private fun PrimaryActionButton(
    isStrictMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "buttonScale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        if (isStrictMode) {
            Icon(
                imageVector = Icons.Outlined.Nfc,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 8.dp)
            )
        }

        Text(
            text = if (isStrictMode) "Escanear para desbloquear" else "Volver al inicio",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ========================================
// Glass Card Component (Glassmorphism)
// ========================================

@Composable
private fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        tonalElevation = 2.dp,
        shadowElevation = 4.dp
    ) {
        content()
    }
}

// ========================================
// Preview States
// ========================================

@Preview(name = "Light Mode - Normal", showBackground = true)
@Composable
private fun PreviewNormalMode() {
    MaterialTheme {
        BlockingScreen(
            state = BlockingState(
                profileName = "Trabajo",
                isStrictMode = false,
                focusedTimeToday = "2h 35min"
            ),
            onBackToHome = {},
            onEmergencyAccess = {}
        )
    }
}

@Preview(name = "Strict Mode", showBackground = true)
@Composable
private fun PreviewStrictMode() {
    MaterialTheme {
        BlockingScreen(
            state = BlockingState(
                profileName = "Enfoque Profundo",
                isStrictMode = true,
                focusedTimeToday = "4h 12min"
            ),
            onBackToHome = {},
            onScanNfc = {}
        )
    }
}

@Preview(name = "With Timer", showBackground = true)
@Composable
private fun PreviewWithTimer() {
    MaterialTheme {
        BlockingScreen(
            state = BlockingState(
                profileName = "Estudio",
                isStrictMode = false,
                timerMinutesRemaining = 85,
                focusedTimeToday = "1h 20min"
            ),
            onBackToHome = {},
            onEmergencyAccess = {}
        )
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
private fun PreviewDarkMode() {
    MaterialTheme(
        colorScheme = darkColorScheme()
    ) {
        BlockingScreen(
            state = BlockingState(
                profileName = "Noche",
                isStrictMode = true,
                focusedTimeToday = "3h 45min"
            ),
            onBackToHome = {},
            onScanNfc = {}
        )
    }
}
