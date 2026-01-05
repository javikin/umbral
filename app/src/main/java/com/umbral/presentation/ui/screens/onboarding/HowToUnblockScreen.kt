package com.umbral.presentation.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Nfc
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.R
import com.umbral.presentation.ui.components.ButtonVariant
import com.umbral.presentation.ui.components.UmbralButton
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HowToUnblockScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    // Animation states
    var showHeader by remember { mutableStateOf(false) }
    var showNfc by remember { mutableStateOf(false) }
    var showQr by remember { mutableStateOf(false) }
    var showButton by remember { mutableStateOf(false) }

    // Staggered animation
    LaunchedEffect(Unit) {
        delay(100)
        showHeader = true
        delay(200)
        showNfc = true
        delay(200)
        showQr = true
        delay(300)
        showButton = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = UmbralSpacing.screenHorizontal),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(UmbralSpacing.lg))

            // Header
            AnimatedVisibility(
                visible = showHeader,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { -50 }
                        )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.onboarding_unlock_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(UmbralSpacing.sm))

                    Text(
                        text = stringResource(R.string.onboarding_unlock_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.xxl))

            // NFC Option Card
            AnimatedVisibility(
                visible = showNfc,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { 50 }
                        )
            ) {
                UnlockOptionCard(
                    icon = Icons.Outlined.Nfc,
                    title = stringResource(R.string.onboarding_unlock_nfc_title),
                    description = stringResource(R.string.onboarding_unlock_nfc_desc)
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.md))

            // QR Code Option Card
            AnimatedVisibility(
                visible = showQr,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            initialOffsetY = { 50 }
                        )
            ) {
                UnlockOptionCard(
                    icon = Icons.Outlined.QrCodeScanner,
                    title = stringResource(R.string.onboarding_unlock_qr_title),
                    description = stringResource(R.string.onboarding_unlock_qr_desc)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Continue button
            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(animationSpec = tween(400)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessMedium
                            ),
                            initialOffsetY = { 100 }
                        )
            ) {
                UmbralButton(
                    text = stringResource(R.string.onboarding_unlock_button),
                    onClick = onContinue,
                    fullWidth = true,
                    variant = ButtonVariant.Primary
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.xl))
        }
    }
}

@Composable
private fun UnlockOptionCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.large
            )
            .padding(UmbralSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon container
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(UmbralSpacing.md))

        // Text content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "How To Unlock - Light", showBackground = true)
@Composable
private fun HowToUnblockScreenPreview() {
    UmbralTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(UmbralSpacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
        ) {
            Text(
                text = "¿Cómo desbloquear?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold
            )

            UnlockOptionCard(
                icon = Icons.Outlined.Nfc,
                title = "Tag NFC",
                description = "Acerca tu teléfono al tag NFC que configuraste"
            )

            UnlockOptionCard(
                icon = Icons.Outlined.QrCodeScanner,
                title = "Código QR",
                description = "Escanea el código QR que generaste"
            )
        }
    }
}

@Preview(name = "How To Unlock - Dark", showBackground = true)
@Composable
private fun HowToUnblockScreenDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(UmbralSpacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(UmbralSpacing.md)
        ) {
            Text(
                text = "¿Cómo desbloquear?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )

            UnlockOptionCard(
                icon = Icons.Outlined.Nfc,
                title = "Tag NFC",
                description = "Acerca tu teléfono al tag NFC que configuraste"
            )

            UnlockOptionCard(
                icon = Icons.Outlined.QrCodeScanner,
                title = "Código QR",
                description = "Escanea el código QR que generaste"
            )
        }
    }
}
