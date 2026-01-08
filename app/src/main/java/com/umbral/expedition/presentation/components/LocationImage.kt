package com.umbral.expedition.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.umbral.expedition.util.ExpeditionAssets

/**
 * Component for displaying location background images.
 *
 * Features:
 * - Loads location images using Coil
 * - Shows placeholder during loading
 * - Supports fogged/undiscovered state with overlay
 * - Gradient overlay for better text readability
 * - Customizable aspect ratio
 *
 * @param locationId Unique location identifier (e.g., "forest_01")
 * @param modifier Modifier for customization
 * @param height Height of the location image
 * @param isDiscovered Whether the location has been discovered (affects fog overlay)
 * @param showGradient Whether to show a gradient overlay for text readability
 * @param contentDescription Accessibility description
 */
@Composable
fun LocationImage(
    locationId: String,
    modifier: Modifier = Modifier,
    height: Dp = 200.dp,
    isDiscovered: Boolean = true,
    showGradient: Boolean = true,
    contentDescription: String? = "Imagen de locación"
) {
    val context = LocalContext.current
    val imageRes = if (isDiscovered) {
        ExpeditionAssets.locationImage(locationId)
    } else {
        ExpeditionAssets.locationFoggedImage()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        // Background image
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageRes)
                .crossfade(true)
                .build(),
            contentDescription = contentDescription,
            placeholder = painterResource(imageRes),
            error = painterResource(imageRes),
            fallback = painterResource(imageRes),
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Fog overlay for undiscovered locations
        if (!isDiscovered) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.6f))
            )
        }

        // Gradient overlay for better text readability
        if (showGradient) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
            )
        }
    }
}

/**
 * Simplified location image using only drawable resources.
 * Useful for preview or when async loading is not needed.
 *
 * @param locationId Unique location identifier
 * @param modifier Modifier for customization
 * @param height Height of the image
 */
@Composable
fun LocationImageSimple(
    locationId: String,
    modifier: Modifier = Modifier,
    height: Dp = 200.dp
) {
    val imageRes = ExpeditionAssets.locationImage(locationId)

    androidx.compose.foundation.Image(
        painter = painterResource(imageRes),
        contentDescription = "Imagen de locación",
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(12.dp))
    )
}
