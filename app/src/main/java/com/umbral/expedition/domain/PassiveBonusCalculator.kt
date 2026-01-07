package com.umbral.expedition.domain

import com.umbral.expedition.domain.model.Companion
import com.umbral.expedition.domain.model.PassiveBonus
import java.util.Calendar

/**
 * Calculator for passive bonuses from companions.
 *
 * This class is responsible for:
 * - Calculating energy multipliers based on active companion
 * - Calculating XP multipliers based on active companion
 * - Detecting night time for SHADOW_CAT night bonus
 * - Applying location discounts
 * - Managing streak protection
 */
object PassiveBonusCalculator {

    /**
     * Calculate total energy multiplier from active companion
     *
     * @param activeCompanion Currently active companion (null if none)
     * @param isNightTime Whether current time is night (for SHADOW_CAT bonus)
     * @return Multiplier to apply to energy gains (1.0 = no bonus)
     */
    fun calculateEnergyMultiplier(
        activeCompanion: Companion?,
        isNightTime: Boolean = isNightTime()
    ): Float {
        if (activeCompanion == null) return 1.0f

        return when (val bonus = activeCompanion.passiveBonus) {
            is PassiveBonus.EnergyBoost -> {
                1.0f + (bonus.percent / 100f)
            }
            else -> 1.0f
        }
    }

    /**
     * Calculate total XP multiplier from active companion
     *
     * @param activeCompanion Currently active companion (null if none)
     * @return Multiplier to apply to XP gains (1.0 = no bonus)
     */
    fun calculateXpMultiplier(activeCompanion: Companion?): Float {
        if (activeCompanion == null) return 1.0f

        return when (val bonus = activeCompanion.passiveBonus) {
            is PassiveBonus.XpBoost -> {
                1.0f + (bonus.percent / 100f)
            }
            else -> 1.0f
        }
    }

    /**
     * Calculate location reveal cost discount from active companion
     *
     * @param baseCost Base cost of revealing location
     * @param activeCompanion Currently active companion (null if none)
     * @return Final cost after applying discount
     */
    fun calculateLocationCost(baseCost: Int, activeCompanion: Companion?): Int {
        if (activeCompanion == null) return baseCost

        return when (val bonus = activeCompanion.passiveBonus) {
            is PassiveBonus.LocationDiscountPercent -> {
                val discount = baseCost * bonus.percent / 100
                baseCost - discount
            }
            else -> baseCost
        }
    }

    /**
     * Get number of days of streak protection from active companion
     *
     * @param activeCompanion Currently active companion (null if none)
     * @return Number of days protected (0 if none)
     */
    fun getStreakProtectionDays(activeCompanion: Companion?): Int {
        if (activeCompanion == null) return 0

        return when (val bonus = activeCompanion.passiveBonus) {
            is PassiveBonus.StreakProtection -> bonus.daysProtected
            else -> 0
        }
    }

    /**
     * Check if current time is night time (6 PM - 6 AM)
     * Used for SHADOW_CAT night bonus
     *
     * @return true if current time is between 18:00-06:00
     */
    fun isNightTime(): Boolean {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return hour >= 18 || hour < 6
    }

    /**
     * Get user-friendly description of all active bonuses
     *
     * @param activeCompanion Currently active companion (null if none)
     * @return List of bonus descriptions in Spanish
     */
    fun getActiveBonusDescriptions(activeCompanion: Companion?): List<String> {
        if (activeCompanion == null) return emptyList()

        val descriptions = mutableListOf<String>()

        when (val bonus = activeCompanion.passiveBonus) {
            is PassiveBonus.EnergyBoost -> {
                descriptions.add("+${bonus.percent}% energía de bloqueo")
            }
            is PassiveBonus.XpBoost -> {
                descriptions.add("+${bonus.percent}% experiencia")
            }
            is PassiveBonus.LocationDiscountPercent -> {
                descriptions.add("-${bonus.percent}% costo de locaciones")
            }
            is PassiveBonus.StreakProtection -> {
                val days = if (bonus.daysProtected == 1) "1 día" else "${bonus.daysProtected} días"
                descriptions.add("Protege racha $days")
            }
        }

        return descriptions
    }

    /**
     * Calculate total bonus effect for display purposes
     *
     * @param activeCompanion Currently active companion
     * @return Summary of bonus effects
     */
    data class BonusSummary(
        val energyMultiplier: Float,
        val xpMultiplier: Float,
        val streakProtectionDays: Int,
        val hasLocationDiscount: Boolean,
        val descriptions: List<String>
    )

    fun getBonusSummary(activeCompanion: Companion?): BonusSummary {
        return BonusSummary(
            energyMultiplier = calculateEnergyMultiplier(activeCompanion),
            xpMultiplier = calculateXpMultiplier(activeCompanion),
            streakProtectionDays = getStreakProtectionDays(activeCompanion),
            hasLocationDiscount = activeCompanion?.passiveBonus is PassiveBonus.LocationDiscountPercent,
            descriptions = getActiveBonusDescriptions(activeCompanion)
        )
    }
}
