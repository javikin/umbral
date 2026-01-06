package com.umbral.domain.stats

import com.umbral.data.local.dao.AppAttemptCount
import com.umbral.data.local.dao.DailyStats

/**
 * Statistics for today's blocking activity.
 */
data class TodayStats(
    val blockedMinutes: Int,
    val attemptCount: Int
)

/**
 * Statistics for weekly blocking activity.
 */
data class WeeklyStats(
    val totalMinutes: Int,
    val previousWeekMinutes: Int,
    val dailyStats: List<DailyStats>,
    val topApps: List<AppAttemptCount>
)
