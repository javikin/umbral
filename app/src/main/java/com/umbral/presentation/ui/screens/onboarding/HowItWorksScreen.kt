package com.umbral.presentation.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralDimens

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HowItWorksScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 3 })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Pager con explicaciones
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                ExplanationPage(
                    page = howItWorksPages[page]
                )
            }

            // Indicadores de página
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(UmbralDimens.spaceLg),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(3) { index ->
                    PageIndicator(
                        isSelected = pagerState.currentPage == index,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }

            // Botón continuar
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = UmbralDimens.screenPaddingHorizontal)
                    .padding(bottom = UmbralDimens.spaceXl)
            ) {
                Text("Continuar")
            }
        }
    }
}

@Composable
private fun ExplanationPage(
    page: HowItWorksPage
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(UmbralDimens.spaceXl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Ilustración (icono grande)
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = page.icon,
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(UmbralDimens.spaceXl))

        // Título
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(UmbralDimens.spaceMd))

        // Descripción
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PageIndicator(
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
    )
}

data class HowItWorksPage(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private val howItWorksPages = listOf(
    HowItWorksPage(
        icon = Icons.Default.Nfc,
        title = "Configura tu tag NFC",
        description = "Escribe un tag NFC con tu perfil de bloqueo. Puedes usar cualquier tag NTAG213/215/216."
    ),
    HowItWorksPage(
        icon = Icons.Default.Block,
        title = "Toca para bloquear",
        description = "Cuando toques el tag, las apps seleccionadas se bloquean automáticamente."
    ),
    HowItWorksPage(
        icon = Icons.Default.LightMode,
        title = "Enfócate sin distracciones",
        description = "Trabaja, estudia o descansa sin la tentación de abrir apps adictivas."
    )
)
