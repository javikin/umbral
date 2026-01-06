package com.umbral.glance.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.Action
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.umbral.glance.theme.WidgetColors

/**
 * Reusable Glance Widget Components for Umbral
 *
 * These components provide consistent styling and behavior across all widgets.
 */

/**
 * Standard widget container with rounded corners and padding
 */
@Composable
fun WidgetContainer(
    modifier: GlanceModifier = GlanceModifier,
    onClick: Action? = null,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(WidgetColors.surface)
            .cornerRadius(16.dp)
            .padding(16.dp)
            .then(if (onClick != null) GlanceModifier.clickable(onClick) else GlanceModifier),
        contentAlignment = Alignment.TopStart
    ) {
        content()
    }
}

/**
 * Widget title text
 */
@Composable
fun WidgetTitle(
    text: String,
    modifier: GlanceModifier = GlanceModifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = WidgetColors.onSurface
        )
    )
}

/**
 * Widget body text
 */
@Composable
fun WidgetBodyText(
    text: String,
    modifier: GlanceModifier = GlanceModifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = WidgetColors.onSurfaceVariant
        )
    )
}

/**
 * Widget caption/label text (smaller, secondary)
 */
@Composable
fun WidgetCaptionText(
    text: String,
    modifier: GlanceModifier = GlanceModifier
) {
    Text(
        text = text,
        modifier = modifier,
        style = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = WidgetColors.onSurfaceVariant
        )
    )
}

/**
 * Status badge (for active/blocked states)
 */
@Composable
fun StatusBadge(
    text: String,
    isActive: Boolean,
    modifier: GlanceModifier = GlanceModifier
) {
    Box(
        modifier = modifier
            .background(if (isActive) WidgetColors.primary else WidgetColors.surfaceVariant)
            .cornerRadius(8.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isActive) WidgetColors.onPrimary else WidgetColors.onSurfaceVariant
            )
        )
    }
}

/**
 * Icon with label (for stats display)
 */
@Composable
fun IconWithLabel(
    icon: ImageProvider,
    label: String,
    value: String,
    modifier: GlanceModifier = GlanceModifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            provider = icon,
            contentDescription = label,
            modifier = GlanceModifier.size(20.dp)
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        Column {
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = WidgetColors.onSurface
                )
            )
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = WidgetColors.onSurfaceVariant
                )
            )
        }
    }
}

/**
 * Divider line
 */
@Composable
fun WidgetDivider(
    modifier: GlanceModifier = GlanceModifier
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(WidgetColors.outline)
    )
}

/**
 * Empty state message
 */
@Composable
fun EmptyState(
    message: String,
    modifier: GlanceModifier = GlanceModifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = WidgetColors.onSurfaceVariant
            )
        )
    }
}

/**
 * Loading indicator (text-based since Glance doesn't support animated CircularProgressIndicator)
 */
@Composable
fun LoadingIndicator(
    modifier: GlanceModifier = GlanceModifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Cargando...",
            style = TextStyle(
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = WidgetColors.onSurfaceVariant
            )
        )
    }
}
