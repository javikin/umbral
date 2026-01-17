package com.umbral.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Umbral Design System Search Field
 *
 * A pill-shaped search input with search icon and clear button.
 * Provides a clean, modern search experience with Material 3 styling.
 *
 * @param value Current text value
 * @param onValueChange Callback when text changes
 * @param modifier Modifier for customization
 * @param placeholder Hint text when empty
 * @param onClear Callback when clear button is clicked (also clears the text)
 *
 * Usage:
 * ```
 * var searchQuery by remember { mutableStateOf("") }
 * UmbralSearchField(
 *     value = searchQuery,
 *     onValueChange = { searchQuery = it },
 *     placeholder = "Buscar aplicaciones..."
 * )
 * ```
 */
@Composable
fun UmbralSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar...",
    onClear: () -> Unit = { }
) {
    val focusRequester = remember { FocusRequester() }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(48.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(percent = 50) // Pill shape
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(percent = 50)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                focusRequester.requestFocus()
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = UmbralSpacing.md, vertical = UmbralSpacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Search icon (always visible)
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                modifier = Modifier.size(UmbralSpacing.iconSizeMedium),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(UmbralSpacing.iconTextSpacing))

            // Text field
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { /* Handle search action if needed */ }
                    )
                )

                // Placeholder text
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Clear button (only visible when text is not empty)
            if (value.isNotEmpty()) {
                Spacer(modifier = Modifier.width(UmbralSpacing.xs))
                IconButton(
                    onClick = {
                        onValueChange("")
                        onClear()
                    },
                    modifier = Modifier.size(UmbralSpacing.iconSizeMedium)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Limpiar búsqueda",
                        modifier = Modifier.size(UmbralSpacing.iconSizeSmall),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Empty Search Field", showBackground = true)
@Composable
private fun UmbralSearchFieldEmptyPreview() {
    UmbralTheme {
        var searchText by remember { mutableStateOf("") }
        UmbralSearchField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = "Buscar aplicaciones...",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Search Field with Text", showBackground = true)
@Composable
private fun UmbralSearchFieldWithTextPreview() {
    UmbralTheme {
        var searchText by remember { mutableStateOf("Instagram") }
        UmbralSearchField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = "Buscar aplicaciones...",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Full Width Search Field", showBackground = true)
@Composable
private fun UmbralSearchFieldFullWidthPreview() {
    UmbralTheme {
        var searchText by remember { mutableStateOf("") }
        UmbralSearchField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = "Buscar...",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Preview(name = "Dark Theme Search Field", showBackground = true)
@Composable
private fun UmbralSearchFieldDarkPreview() {
    UmbralTheme(darkTheme = true) {
        var searchText by remember { mutableStateOf("Facebook") }
        UmbralSearchField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = "Buscar aplicaciones...",
            modifier = Modifier.padding(16.dp)
        )
    }
}
