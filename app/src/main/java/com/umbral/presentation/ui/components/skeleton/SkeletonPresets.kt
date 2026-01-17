package com.umbral.presentation.ui.components.skeleton

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Umbral Design System 2.0 - Skeleton Presets
 *
 * Pre-built skeleton loading components for common UI patterns.
 *
 * ## Available Presets
 * - **SkeletonCard**: Card with image placeholder, title, subtitle
 * - **SkeletonListItem**: Avatar + title + subtitle row
 * - **SkeletonText**: Variable width lines (already in UmbralSkeleton.kt)
 * - **SkeletonProfileCard**: Matches Umbral's ProfileCard layout
 *
 * ## Usage
 * ```kotlin
 * // Loading state for a list
 * LazyColumn {
 *     items(5) {
 *         SkeletonListItem()
 *     }
 * }
 *
 * // Loading state for profile cards
 * SkeletonProfileCard()
 * ```
 */

/**
 * Skeleton for a card with image, title, and subtitle
 *
 * Represents a typical card layout with:
 * - Top image placeholder (16:9 aspect ratio)
 * - Title line (80% width)
 * - Subtitle lines (100%, 60% width)
 *
 * @param modifier Modifier for the card container
 * @param imageHeight Height of the image placeholder (default: 180dp)
 */
@Composable
fun SkeletonCard(
    modifier: Modifier = Modifier,
    imageHeight: androidx.compose.ui.unit.Dp = 180.dp
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Image placeholder
        UmbralSkeleton(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight),
            shape = RoundedCornerShape(12.dp)
        )

        // Title
        UmbralSkeleton(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(20.dp)
        )

        // Subtitle lines
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UmbralSkeleton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
            )
            UmbralSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
            )
        }
    }
}

/**
 * Skeleton for a list item with avatar, title, and subtitle
 *
 * Represents a typical list item with:
 * - Left: circular avatar (48dp)
 * - Right: title + subtitle stacked
 *
 * @param modifier Modifier for the list item container
 * @param avatarSize Size of the avatar placeholder (default: 48dp)
 */
@Composable
fun SkeletonListItem(
    modifier: Modifier = Modifier,
    avatarSize: androidx.compose.ui.unit.Dp = 48.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        UmbralSkeleton(
            modifier = Modifier.size(avatarSize),
            shape = CircleShape
        )

        // Title + Subtitle
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Title
            UmbralSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
            )
            // Subtitle
            UmbralSkeleton(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .height(14.dp)
            )
        }
    }
}

/**
 * Skeleton for Umbral's ProfileCard component
 *
 * Matches the ProfileCard layout with:
 * - Top: Profile icon placeholder
 * - Middle: Profile name
 * - Bottom: Stats row (apps count, last used)
 * - Footer: Action buttons
 *
 * @param modifier Modifier for the profile card container
 */
@Composable
fun SkeletonProfileCard(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with icon and title
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile icon placeholder
            UmbralSkeleton(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Profile name
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                UmbralSkeleton(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .height(20.dp)
                )
                UmbralSkeleton(
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(14.dp)
                )
            }
        }

        // Stats row (apps count, last used)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Apps count stat
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                UmbralSkeleton(
                    modifier = Modifier
                        .width(60.dp)
                        .height(12.dp)
                )
                UmbralSkeleton(
                    modifier = Modifier
                        .width(80.dp)
                        .height(16.dp)
                )
            }

            // Last used stat
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                UmbralSkeleton(
                    modifier = Modifier
                        .width(60.dp)
                        .height(12.dp)
                )
                UmbralSkeleton(
                    modifier = Modifier
                        .width(80.dp)
                        .height(16.dp)
                )
            }
        }

        // Action buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Edit button
            UmbralSkeleton(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp)
            )
            // Activate button
            UmbralSkeleton(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

/**
 * Skeleton for a full-width button
 *
 * @param modifier Modifier for the button container
 * @param height Height of the button (default: 48dp)
 */
@Composable
fun SkeletonButton(
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 48.dp
) {
    UmbralSkeleton(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(8.dp)
    )
}

/**
 * Skeleton for a chip/tag element
 *
 * @param modifier Modifier for the chip container
 * @param width Width of the chip (default: 80dp)
 */
@Composable
fun SkeletonChip(
    modifier: Modifier = Modifier,
    width: androidx.compose.ui.unit.Dp = 80.dp
) {
    UmbralSkeleton(
        modifier = modifier
            .width(width)
            .height(32.dp),
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * Skeleton for an app icon (square with rounded corners)
 *
 * @param modifier Modifier for the icon container
 * @param size Size of the icon (default: 48dp)
 */
@Composable
fun SkeletonAppIcon(
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 48.dp
) {
    UmbralSkeleton(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Skeleton for a grid of app icons
 *
 * @param modifier Modifier for the grid container
 * @param rows Number of rows (default: 2)
 * @param columns Number of columns (default: 4)
 * @param iconSize Size of each icon (default: 48dp)
 */
@Composable
fun SkeletonAppGrid(
    modifier: Modifier = Modifier,
    rows: Int = 2,
    columns: Int = 4,
    iconSize: androidx.compose.ui.unit.Dp = 48.dp
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        repeat(rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(columns) {
                    SkeletonAppIcon(
                        modifier = Modifier.weight(1f),
                        size = iconSize
                    )
                }
            }
        }
    }
}
