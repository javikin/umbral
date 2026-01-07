package com.umbral.notifications.presentation.history

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/**
 * Extension functions for formatting Instant timestamps.
 */

/**
 * Format Instant as time string (e.g., "10:35 AM" or "3:15 PM").
 * Uses 24-hour format by default.
 */
fun Instant.formatTime(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        .withZone(ZoneId.systemDefault())
    return formatter.format(this)
}

/**
 * Format Instant as relative date and time string.
 * Examples: "Hoy, 10:30 AM", "Ayer, 3:00 PM", "15/12/2025, 9:45 AM"
 */
fun Instant.formatRelativeDateTime(): String {
    val now = Instant.now()
    val zoneId = ZoneId.systemDefault()
    val dateTime = this.atZone(zoneId)
    val nowDateTime = now.atZone(zoneId)

    val daysDiff = ChronoUnit.DAYS.between(dateTime.toLocalDate(), nowDateTime.toLocalDate())

    val dateString = when (daysDiff.toInt()) {
        0 -> "Hoy"
        1 -> "Ayer"
        else -> {
            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
            dateFormatter.format(dateTime)
        }
    }

    val timeString = formatTime()
    return "$dateString, $timeString"
}
