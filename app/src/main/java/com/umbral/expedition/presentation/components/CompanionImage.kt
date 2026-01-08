package com.umbral.expedition.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.umbral.expedition.domain.model.CompanionType
import com.umbral.expedition.util.ExpeditionAssets

/**
 * Component for displaying companion images with proper loading states and fallbacks.
 *
 * Features:
 * - Loads static images using Coil for smooth loading
 * - Shows placeholder during loading
 * - Shows error placeholder if image fails to load
 * - Supports different evolution states
 * - Customizable size and shape
 *
 * @param companionType The type of companion to display
 * @param evolutionState Evolution level (0 = base, 1 = evolved, 2 = final)
 * @param modifier Modifier for customization
 * @param size Size of the image (default 80.dp)
 * @param backgroundColor Background color for the image container
 * @param contentDescription Accessibility description
 */
@Composable
fun CompanionImage(
    companionType: CompanionType,
    evolutionState: Int = 0,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    contentDescription: String? = companionType.displayName
) {
    val context = LocalContext.current
    val imageRes = ExpeditionAssets.companionImage(companionType, evolutionState)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageRes)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            placeholder = painterResource(imageRes),
            error = painterResource(imageRes),
            fallback = painterResource(imageRes),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(size * 0.8f) // Slightly smaller than container for padding
        )
    }
}

/**
 * Simplified companion image that uses only drawable resources without Coil.
 * Useful for preview or when async loading is not needed.
 *
 * @param companionType The type of companion to display
 * @param evolutionState Evolution level
 * @param modifier Modifier for customization
 * @param size Size of the image
 */
@Composable
fun CompanionImageSimple(
    companionType: CompanionType,
    evolutionState: Int = 0,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    val imageRes = ExpeditionAssets.companionImage(companionType, evolutionState)

    androidx.compose.foundation.Image(
        painter = painterResource(imageRes),
        contentDescription = companionType.displayName,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size)
    )
}
