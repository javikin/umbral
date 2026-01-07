package com.umbral.expedition.presentation.map.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.umbral.expedition.domain.model.Location

/**
 * Modal bottom sheet showing details of a selected location.
 *
 * For discovered locations:
 * - Shows name and lore text
 *
 * For undiscovered locations:
 * - Shows name with lock icon
 * - Shows energy cost
 * - Shows "Descubrir" button (disabled if insufficient energy)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailSheet(
    locationId: String,
    isDiscovered: Boolean,
    location: Location?,
    currentEnergy: Int,
    energyCost: Int,
    onDismiss: () -> Unit,
    onDiscover: () -> Unit,
    sheetState: SheetState,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isDiscovered && location != null) {
                // Discovered location content
                DiscoveredLocationContent(location = location)
            } else {
                // Undiscovered location content
                UndiscoveredLocationContent(
                    locationId = locationId,
                    currentEnergy = currentEnergy,
                    energyCost = energyCost,
                    onDiscover = onDiscover
                )
            }
        }
    }
}

/**
 * Content for a discovered location
 */
@Composable
private fun DiscoveredLocationContent(
    location: Location,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Location name
        Text(
            text = location.displayName,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Discovery badge
        Text(
            text = "✓ Descubierta",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lore text
        Text(
            text = location.loreText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Discovery stats
        Text(
            text = "Energía gastada: ${location.energySpent}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (location.daysSinceDiscovery > 0) {
            Text(
                text = "Descubierta hace ${location.daysSinceDiscovery} día(s)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Content for an undiscovered location
 */
@Composable
private fun UndiscoveredLocationContent(
    locationId: String,
    currentEnergy: Int,
    energyCost: Int,
    onDiscover: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasEnoughEnergy = currentEnergy >= energyCost
    val locationName = Location.getLocationName(locationId)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Locked icon
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Location name
        Text(
            text = locationName,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Mystery text
        Text(
            text = "Esta locación aún no ha sido descubierta",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Energy cost display
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = Color(0xFFFFD700), // Gold
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$energyCost energía",
                style = MaterialTheme.typography.titleMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Discover button
        Button(
            onClick = onDiscover,
            enabled = hasEnoughEnergy,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (hasEnoughEnergy) {
                    "Descubrir"
                } else {
                    "Energía insuficiente"
                }
            )
        }

        if (!hasEnoughEnergy) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Necesitas ${energyCost - currentEnergy} energía más",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Helper to get location name from ID (mirrors Location.kt logic)
 */
private fun Location.Companion.getLocationName(id: String): String = when (id) {
    "forest_01" -> "Claro del Bosque"
    "forest_02" -> "Arroyo Antiguo"
    "forest_03" -> "Roble Centenario"
    "forest_04" -> "Cueva Luminosa"
    "forest_05" -> "Lago Sereno"
    "forest_06" -> "Sendero Oculto"
    "forest_07" -> "Ruinas Misteriosas"
    "forest_08" -> "Torre del Guardián"
    "forest_09" -> "Cascada Cristalina"
    "forest_10" -> "Bosque de Hongos"
    "forest_11" -> "Colina Vista"
    "forest_12" -> "Santuario Perdido"
    "forest_13" -> "Puente de Raíces"
    "forest_14" -> "Claro de las Luciérnagas"
    "forest_15" -> "Corazón del Bosque"
    else -> "Locación Desconocida"
}
