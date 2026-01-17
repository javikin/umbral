package com.umbral.presentation.ui.components.empty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.components.ButtonSize
import com.umbral.presentation.ui.components.ButtonVariant
import com.umbral.presentation.ui.components.UmbralButton
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Action configuration for empty state
 */
data class EmptyStateAction(
    val label: String,
    val onClick: () -> Unit
)

/**
 * Umbral Empty State Component
 *
 * Displays an empty state with illustration, title, description, and optional action.
 * Uses minimalist line art illustrations consistent with Design System 2.0.
 *
 * Visual Specifications:
 * - Illustration: 120x120.dp, uses textTertiary with accentPrimary details
 * - Title: titleMedium, textPrimary, centered
 * - Description: bodyMedium, textSecondary, centered, max 2 lines
 * - Action: Small button with 24.dp top margin
 * - Vertical spacing: 16.dp between elements
 *
 * @param illustration The type of illustration to display
 * @param title Main heading text
 * @param description Supporting text explaining the empty state
 * @param modifier Modifier for customization
 * @param action Optional action button configuration
 */
@Composable
fun UmbralEmptyState(
    illustration: EmptyStateIllustration,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: EmptyStateAction? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = UmbralSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Illustration (120x120.dp)
        EmptyStateIllustrationView(
            type = illustration
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        // Optional action button
        action?.let {
            Spacer(modifier = Modifier.height(24.dp))
            UmbralButton(
                text = it.label,
                onClick = it.onClick,
                size = ButtonSize.Small,
                variant = ButtonVariant.Primary
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "No Profiles Empty State", showBackground = true)
@Composable
private fun UmbralEmptyStateNoProfilesPreview() {
    UmbralTheme {
        UmbralEmptyState(
            illustration = EmptyStateIllustration.NoProfiles,
            title = "No tienes perfiles",
            description = "Crea tu primer perfil de bloqueo para comenzar a enfocarte",
            action = EmptyStateAction(
                label = "Crear perfil",
                onClick = {}
            )
        )
    }
}

@Preview(name = "No Apps Empty State", showBackground = true)
@Composable
private fun UmbralEmptyStateNoAppsPreview() {
    UmbralTheme {
        UmbralEmptyState(
            illustration = EmptyStateIllustration.NoApps,
            title = "Sin aplicaciones",
            description = "Agrega aplicaciones a este perfil para bloquearlas",
            action = EmptyStateAction(
                label = "Agregar apps",
                onClick = {}
            )
        )
    }
}

@Preview(name = "No Stats Empty State", showBackground = true)
@Composable
private fun UmbralEmptyStateNoStatsPreview() {
    UmbralTheme {
        UmbralEmptyState(
            illustration = EmptyStateIllustration.NoStats,
            title = "Sin estadísticas",
            description = "Comienza a usar Umbral para ver tus estadísticas de enfoque"
        )
    }
}

@Preview(name = "No NFC Empty State", showBackground = true)
@Composable
private fun UmbralEmptyStateNoNfcPreview() {
    UmbralTheme {
        UmbralEmptyState(
            illustration = EmptyStateIllustration.NoNfc,
            title = "Tag NFC no detectado",
            description = "Acerca un tag NFC compatible para configurarlo",
            action = EmptyStateAction(
                label = "Reintentar",
                onClick = {}
            )
        )
    }
}

@Preview(name = "Search Empty State", showBackground = true)
@Composable
private fun UmbralEmptyStateSearchPreview() {
    UmbralTheme {
        UmbralEmptyState(
            illustration = EmptyStateIllustration.SearchEmpty,
            title = "Sin resultados",
            description = "No encontramos nada con esa búsqueda. Intenta con otros términos"
        )
    }
}

@Preview(name = "Success Empty State", showBackground = true)
@Composable
private fun UmbralEmptyStateSuccessPreview() {
    UmbralTheme {
        UmbralEmptyState(
            illustration = EmptyStateIllustration.Success,
            title = "¡Todo listo!",
            description = "Tu perfil se configuró correctamente y está activo",
            action = EmptyStateAction(
                label = "Continuar",
                onClick = {}
            )
        )
    }
}

@Preview(name = "Error Empty State", showBackground = true)
@Composable
private fun UmbralEmptyStateErrorPreview() {
    UmbralTheme {
        UmbralEmptyState(
            illustration = EmptyStateIllustration.Error,
            title = "Algo salió mal",
            description = "No pudimos completar la operación. Por favor intenta de nuevo",
            action = EmptyStateAction(
                label = "Reintentar",
                onClick = {}
            )
        )
    }
}

@Preview(name = "Offline Empty State", showBackground = true)
@Composable
private fun UmbralEmptyStateOfflinePreview() {
    UmbralTheme {
        UmbralEmptyState(
            illustration = EmptyStateIllustration.Offline,
            title = "Sin conexión",
            description = "Verifica tu conexión a internet para continuar"
        )
    }
}

@Preview(name = "Dark Theme Empty State", showBackground = true)
@Composable
private fun UmbralEmptyStateDarkPreview() {
    UmbralTheme(darkTheme = true) {
        UmbralEmptyState(
            illustration = EmptyStateIllustration.NoProfiles,
            title = "No tienes perfiles",
            description = "Crea tu primer perfil de bloqueo para comenzar a enfocarte",
            action = EmptyStateAction(
                label = "Crear perfil",
                onClick = {}
            )
        )
    }
}

@Preview(name = "Without Action Button", showBackground = true)
@Composable
private fun UmbralEmptyStateNoActionPreview() {
    UmbralTheme {
        UmbralEmptyState(
            illustration = EmptyStateIllustration.NoStats,
            title = "Sin actividad reciente",
            description = "Tus estadísticas aparecerán aquí cuando uses la app"
        )
    }
}
