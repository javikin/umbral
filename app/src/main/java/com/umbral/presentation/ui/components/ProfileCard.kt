package com.umbral.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.NightlightRound
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.umbral.R
import com.umbral.domain.blocking.BlockingProfile
import com.umbral.presentation.ui.theme.UmbralSpacing
import com.umbral.presentation.ui.theme.UmbralTheme

// =============================================================================
// PROFILE CARD
// =============================================================================

@Composable
fun ProfileCard(
    profile: BlockingProfile,
    onClick: () -> Unit,
    onToggleActive: () -> Unit,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profileColor = try {
        Color(android.graphics.Color.parseColor(profile.colorHex))
    } catch (e: Exception) {
        MaterialTheme.colorScheme.primary
    }

    // Animations for active state
    val scale by animateFloatAsState(
        targetValue = if (profile.isActive) 1.02f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "scale"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (profile.isActive) 2.dp else 0.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "borderWidth"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (profile.isActive) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "backgroundColor"
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .scale(scale),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = if (profile.isActive) {
            BorderStroke(borderWidth, profileColor)
        } else null,
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (profile.isActive) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(UmbralSpacing.cardPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile icon with color
                ProfileIcon(
                    iconName = profile.iconName,
                    color = profileColor,
                    modifier = Modifier.size(48.dp)
                )

                Spacer(modifier = Modifier.width(UmbralSpacing.md))

                // Profile info
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (profile.isActive) {
                            Spacer(modifier = Modifier.width(UmbralSpacing.sm))
                            ActiveBadge()
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${profile.blockedApps.size} apps bloqueadas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (profile.isStrictMode) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.strict_mode),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                // Options menu
                ProfileOptionsMenu(
                    onEdit = onEdit,
                    onDelete = onDelete,
                    onToggleActive = onToggleActive,
                    isActive = profile.isActive
                )
            }

            Spacer(modifier = Modifier.height(UmbralSpacing.sm))

            // Visual indicator of app count
            LinearProgressIndicator(
                progress = { (profile.blockedApps.size / 20f).coerceAtMost(1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = profileColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

// =============================================================================
// PROFILE ICON
// =============================================================================

@Composable
fun ProfileIcon(
    iconName: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    val icon = getProfileIcon(iconName)

    Box(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}

private fun getProfileIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "productivity", "work" -> Icons.Outlined.Work
        "night", "nightlight" -> Icons.Outlined.NightlightRound
        "study", "book" -> Icons.Outlined.Book
        "social", "groups" -> Icons.Outlined.Groups
        "fitness", "exercise" -> Icons.Outlined.FitnessCenter
        else -> Icons.Outlined.Shield
    }
}

// =============================================================================
// ACTIVE BADGE
// =============================================================================

@Composable
fun ActiveBadge(modifier: Modifier = Modifier) {
    val successColor = Color(0xFF4CAF50)

    Surface(
        modifier = modifier,
        color = successColor.copy(alpha = 0.15f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = "Activo",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = successColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

// =============================================================================
// PROFILE OPTIONS MENU
// =============================================================================

@Composable
private fun ProfileOptionsMenu(
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Opciones",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(if (isActive) "Desactivar" else "Activar")
                },
                onClick = {
                    expanded = false
                    onToggleActive()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null
                    )
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.edit_profile)) },
                onClick = {
                    expanded = false
                    onEdit()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null
                    )
                }
            )

            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.delete_profile),
                        color = MaterialTheme.colorScheme.error
                    )
                },
                onClick = {
                    expanded = false
                    onDelete()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }
}

// =============================================================================
// CREATE PROFILE CARD
// =============================================================================

@Composable
fun CreateProfileCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(UmbralSpacing.cardPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(UmbralSpacing.sm))
            Text(
                text = stringResource(R.string.create_profile),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// =============================================================================
// PREVIEWS
// =============================================================================

@Preview(name = "Profile Card - Active", showBackground = true)
@Composable
private fun ProfileCardActivePreview() {
    UmbralTheme {
        ProfileCard(
            profile = BlockingProfile(
                id = "1",
                name = "Productividad",
                iconName = "work",
                colorHex = "#6650A4",
                isActive = true,
                blockedApps = listOf("1", "2", "3", "4", "5", "6", "7", "8")
            ),
            onClick = {},
            onToggleActive = {},
            onEdit = {},
            onDelete = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Profile Card - Inactive", showBackground = true)
@Composable
private fun ProfileCardInactivePreview() {
    UmbralTheme {
        ProfileCard(
            profile = BlockingProfile(
                id = "2",
                name = "Noche",
                iconName = "night",
                colorHex = "#1E88E5",
                isActive = false,
                blockedApps = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
            ),
            onClick = {},
            onToggleActive = {},
            onEdit = {},
            onDelete = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Profile Card - Strict Mode", showBackground = true)
@Composable
private fun ProfileCardStrictModePreview() {
    UmbralTheme {
        ProfileCard(
            profile = BlockingProfile(
                id = "3",
                name = "Estudio",
                iconName = "book",
                colorHex = "#43A047",
                isActive = false,
                isStrictMode = true,
                blockedApps = listOf("1", "2", "3", "4", "5")
            ),
            onClick = {},
            onToggleActive = {},
            onEdit = {},
            onDelete = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Profile Icon", showBackground = true)
@Composable
private fun ProfileIconPreview() {
    UmbralTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            ProfileIcon(
                iconName = "work",
                color = Color(0xFF6650A4),
                modifier = Modifier.size(48.dp)
            )
            ProfileIcon(
                iconName = "night",
                color = Color(0xFF1E88E5),
                modifier = Modifier.size(48.dp)
            )
            ProfileIcon(
                iconName = "book",
                color = Color(0xFF43A047),
                modifier = Modifier.size(48.dp)
            )
            ProfileIcon(
                iconName = "fitness",
                color = Color(0xFFE53935),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Preview(name = "Active Badge", showBackground = true)
@Composable
private fun ActiveBadgePreview() {
    UmbralTheme {
        ActiveBadge(modifier = Modifier.padding(16.dp))
    }
}

@Preview(name = "Create Profile Card", showBackground = true)
@Composable
private fun CreateProfileCardPreview() {
    UmbralTheme {
        CreateProfileCard(
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Dark Theme Profile Card", showBackground = true)
@Composable
private fun ProfileCardDarkPreview() {
    UmbralTheme(darkTheme = true) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileCard(
                profile = BlockingProfile(
                    id = "1",
                    name = "Productividad",
                    iconName = "work",
                    colorHex = "#6650A4",
                    isActive = true,
                    blockedApps = listOf("1", "2", "3", "4", "5", "6", "7", "8")
                ),
                onClick = {},
                onToggleActive = {},
                onEdit = {},
                onDelete = {}
            )

            ProfileCard(
                profile = BlockingProfile(
                    id = "2",
                    name = "Noche",
                    iconName = "night",
                    colorHex = "#1E88E5",
                    isActive = false,
                    blockedApps = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
                ),
                onClick = {},
                onToggleActive = {},
                onEdit = {},
                onDelete = {}
            )

            CreateProfileCard(onClick = {})
        }
    }
}
