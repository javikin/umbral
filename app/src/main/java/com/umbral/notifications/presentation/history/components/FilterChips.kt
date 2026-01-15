package com.umbral.notifications.presentation.history.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.notifications.presentation.history.FilterPeriod
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Filter chips row for app and time period filters.
 *
 * @param selectedApp Currently selected app (null = all apps)
 * @param selectedPeriod Currently selected time period
 * @param availableApps List of app names to show in dropdown
 * @param onAppSelected Callback when app filter changes
 * @param onPeriodSelected Callback when period filter changes
 */
@Composable
fun FilterChipsRow(
    selectedApp: String?,
    selectedPeriod: FilterPeriod,
    availableApps: List<String>,
    onAppSelected: (String?) -> Unit,
    onPeriodSelected: (FilterPeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAppDropdown by remember { mutableStateOf(false) }

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // App filter dropdown
        item {
            Box {
                FilterChip(
                    selected = selectedApp != null,
                    onClick = { showAppDropdown = true },
                    label = {
                        Text(selectedApp ?: "Todas las apps")
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Seleccionar app",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                DropdownMenu(
                    expanded = showAppDropdown,
                    onDismissRequest = { showAppDropdown = false }
                ) {
                    // "All apps" option
                    DropdownMenuItem(
                        text = { Text("Todas las apps") },
                        onClick = {
                            onAppSelected(null)
                            showAppDropdown = false
                        },
                        leadingIcon = if (selectedApp == null) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )

                    HorizontalDivider()

                    // Individual apps
                    availableApps.forEach { app ->
                        DropdownMenuItem(
                            text = { Text(app) },
                            onClick = {
                                onAppSelected(app)
                                showAppDropdown = false
                            },
                            leadingIcon = if (selectedApp == app) {
                                { Icon(Icons.Default.Check, contentDescription = null) }
                            } else null
                        )
                    }
                }
            }
        }

        // Period filter chips
        items(FilterPeriod.values()) { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = { Text(period.displayName) }
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Filter Chips - Light", showBackground = true)
@Composable
private fun FilterChipsRowPreview() {
    UmbralTheme {
        FilterChipsRow(
            selectedApp = null,
            selectedPeriod = FilterPeriod.TODAY,
            availableApps = listOf("Instagram", "Twitter", "WhatsApp"),
            onAppSelected = {},
            onPeriodSelected = {}
        )
    }
}

@Preview(name = "Filter Chips - With Selection", showBackground = true)
@Composable
private fun FilterChipsRowSelectedPreview() {
    UmbralTheme {
        FilterChipsRow(
            selectedApp = "Instagram",
            selectedPeriod = FilterPeriod.WEEK,
            availableApps = listOf("Instagram", "Twitter", "WhatsApp"),
            onAppSelected = {},
            onPeriodSelected = {}
        )
    }
}

@Preview(name = "Filter Chips - Dark", showBackground = true)
@Composable
private fun FilterChipsRowDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            FilterChipsRow(
                selectedApp = "WhatsApp",
                selectedPeriod = FilterPeriod.ALL,
                availableApps = listOf("Instagram", "Twitter", "WhatsApp"),
                onAppSelected = {},
                onPeriodSelected = {}
            )
        }
    }
}
