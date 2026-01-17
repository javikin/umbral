package com.umbral.presentation.ui.components.display

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.umbral.presentation.ui.theme.DarkAccentPrimary
import com.umbral.presentation.ui.theme.DarkSuccess
import com.umbral.presentation.ui.theme.DarkTextTertiary
import com.umbral.presentation.ui.theme.UmbralMotion
import com.umbral.presentation.ui.theme.UmbralTheme

/**
 * Umbral Design System 2.0 - Avatar Component
 *
 * Display user avatars with image, initials, or default icon fallback.
 * Supports status badges with optional pulse animation.
 *
 * ## Usage
 * ```kotlin
 * // Image avatar
 * UmbralAvatar(image = profileBitmap)
 *
 * // Initials avatar
 * UmbralAvatar(initials = "JD", size = AvatarSize.Large)
 *
 * // With status badge
 * UmbralAvatar(
 *     initials = "AB",
 *     badge = AvatarBadge.Online
 * )
 *
 * // Active with pulse
 * UmbralAvatar(
 *     image = profileBitmap,
 *     badge = AvatarBadge.Active,
 *     size = AvatarSize.XLarge
 * )
 * ```
 *
 * ## Fallback Hierarchy
 * 1. Image (if provided)
 * 2. Initials (if provided, max 2 characters)
 * 3. Default person icon
 *
 * ## Accessibility
 * - Content description based on type (image/initials/default)
 * - High contrast badge colors
 * - Circular shape for clear hit area
 */
@Composable
fun UmbralAvatar(
    modifier: Modifier = Modifier,
    image: ImageBitmap? = null,
    initials: String? = null,
    size: AvatarSize = AvatarSize.Medium,
    badge: AvatarBadge = AvatarBadge.None
) {
    Box(
        modifier = modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        // Main avatar circle
        Box(
            modifier = Modifier
                .size(size.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.background,
                    shape = CircleShape
                )
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                // Priority 1: Show image if provided
                image != null -> {
                    Image(
                        bitmap = image,
                        contentDescription = "Avatar de usuario",
                        modifier = Modifier
                            .size(size.dp)
                            .clip(CircleShape)
                    )
                }
                // Priority 2: Show initials if provided
                initials != null && initials.isNotBlank() -> {
                    Text(
                        text = initials.take(2).uppercase(),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = (size.dp.value * 0.4f).sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                // Priority 3: Show default person icon
                else -> {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar predeterminado",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(size.dp * 0.6f)
                    )
                }
            }
        }

        // Badge indicator
        if (badge != AvatarBadge.None) {
            AvatarBadgeIndicator(
                badge = badge,
                avatarSize = size.dp
            )
        }
    }
}

/**
 * Badge indicator component
 * Positioned at bottom-right, 25% of avatar size
 */
@Composable
private fun AvatarBadgeIndicator(
    badge: AvatarBadge,
    avatarSize: Dp
) {
    val badgeSize = avatarSize * 0.25f

    Box(
        modifier = Modifier.size(avatarSize),
        contentAlignment = Alignment.BottomEnd
    ) {
        val badgeColor = when (badge) {
            AvatarBadge.Online -> DarkSuccess
            AvatarBadge.Offline -> DarkTextTertiary
            AvatarBadge.Active -> DarkAccentPrimary
            AvatarBadge.None -> return
        }

        // Pulse animation for Active badge
        val scale = if (badge == AvatarBadge.Active) {
            val infiniteTransition = rememberInfiniteTransition(label = "badge_pulse")
            val animatedScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = UmbralMotion.slow, easing = UmbralMotion.easeInOut),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "badge_scale"
            )
            animatedScale
        } else {
            1f
        }

        Box(
            modifier = Modifier
                .size(badgeSize * scale)
                .clip(CircleShape)
                .background(badgeColor, CircleShape)
                .border(
                    BorderStroke(2.dp, MaterialTheme.colorScheme.background),
                    CircleShape
                )
        )
    }
}

// =============================================================================
// SIZE VARIANTS
// =============================================================================

/**
 * Avatar size variants
 *
 * - Small: 32dp - List items, compact views
 * - Medium: 40dp - Default size for most uses
 * - Large: 56dp - Profile headers, emphasis
 * - XLarge: 80dp - Full profile pages
 */
enum class AvatarSize(val dp: Dp) {
    Small(32.dp),
    Medium(40.dp),
    Large(56.dp),
    XLarge(80.dp)
}

// =============================================================================
// BADGE VARIANTS
// =============================================================================

/**
 * Avatar badge types
 *
 * - Online: Green dot - user is online
 * - Offline: Gray dot - user is offline
 * - Active: Accent dot with pulse - currently active/in session
 * - None: No badge
 */
enum class AvatarBadge {
    Online,
    Offline,
    Active,
    None
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Avatar - Initials Medium", showBackground = true)
@Composable
private fun PreviewAvatarInitialsMedium() {
    UmbralTheme {
        UmbralAvatar(
            initials = "JD",
            size = AvatarSize.Medium
        )
    }
}

@Preview(name = "Avatar - Default Small", showBackground = true)
@Composable
private fun PreviewAvatarDefaultSmall() {
    UmbralTheme {
        UmbralAvatar(size = AvatarSize.Small)
    }
}

@Preview(name = "Avatar - Large with Online Badge", showBackground = true)
@Composable
private fun PreviewAvatarLargeOnline() {
    UmbralTheme {
        UmbralAvatar(
            initials = "AB",
            size = AvatarSize.Large,
            badge = AvatarBadge.Online
        )
    }
}

@Preview(name = "Avatar - XLarge with Active Badge", showBackground = true)
@Composable
private fun PreviewAvatarXLargeActive() {
    UmbralTheme {
        UmbralAvatar(
            initials = "MK",
            size = AvatarSize.XLarge,
            badge = AvatarBadge.Active
        )
    }
}

@Preview(name = "Avatar - All Sizes", showBackground = true)
@Composable
private fun PreviewAvatarAllSizes() {
    UmbralTheme {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            UmbralAvatar(initials = "SM", size = AvatarSize.Small)
            UmbralAvatar(initials = "MD", size = AvatarSize.Medium)
            UmbralAvatar(initials = "LG", size = AvatarSize.Large)
            UmbralAvatar(initials = "XL", size = AvatarSize.XLarge)
        }
    }
}

@Preview(name = "Avatar - All Badges", showBackground = true)
@Composable
private fun PreviewAvatarAllBadges() {
    UmbralTheme {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            UmbralAvatar(
                initials = "ON",
                size = AvatarSize.Large,
                badge = AvatarBadge.Online
            )
            UmbralAvatar(
                initials = "OF",
                size = AvatarSize.Large,
                badge = AvatarBadge.Offline
            )
            UmbralAvatar(
                initials = "AC",
                size = AvatarSize.Large,
                badge = AvatarBadge.Active
            )
            UmbralAvatar(
                initials = "NO",
                size = AvatarSize.Large,
                badge = AvatarBadge.None
            )
        }
    }
}
