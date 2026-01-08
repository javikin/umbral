package com.umbral.expedition.domain.model

/**
 * Enum representing all companion types available in the game.
 * Each companion has unique characteristics and capture requirements.
 */
enum class CompanionType(
    val id: String,
    val displayName: String,
    val element: Element,
    val passiveBonus: PassiveBonus,
    val captureRequirement: CaptureRequirement
) {
    // ========== Starter Companions (Always Available) ==========

    LEAF_SPRITE(
        id = "leaf_sprite",
        displayName = "Espíritu de Hoja",
        element = Element.NATURE,
        passiveBonus = PassiveBonus.EnergyBoost(percent = 5),
        captureRequirement = CaptureRequirement.AlwaysAvailable
    ),

    EMBER_FOX(
        id = "ember_fox",
        displayName = "Zorro de Brasa",
        element = Element.FIRE,
        passiveBonus = PassiveBonus.XpBoost(percent = 5),
        captureRequirement = CaptureRequirement.AlwaysAvailable
    ),

    // ========== Early Unlock Companions ==========

    AQUA_TURTLE(
        id = "aqua_turtle",
        displayName = "Tortuga de Agua",
        element = Element.WATER,
        passiveBonus = PassiveBonus.StreakProtection(daysProtected = 1),
        captureRequirement = CaptureRequirement.MinimumLevel(3)
    ),

    SKY_BIRD(
        id = "sky_bird",
        displayName = "Ave del Cielo",
        element = Element.AIR,
        passiveBonus = PassiveBonus.LocationDiscountPercent(10),
        captureRequirement = CaptureRequirement.LocationsDiscovered(3)
    ),

    // ========== Mid-Game Companions ==========

    STONE_GOLEM(
        id = "stone_golem",
        displayName = "Gólem de Piedra",
        element = Element.EARTH,
        passiveBonus = PassiveBonus.EnergyBoost(percent = 10),
        captureRequirement = CaptureRequirement.MinimumLevel(5)
    ),

    THUNDER_WOLF(
        id = "thunder_wolf",
        displayName = "Lobo de Trueno",
        element = Element.ELECTRIC,
        passiveBonus = PassiveBonus.XpBoost(percent = 10),
        captureRequirement = CaptureRequirement.MinimumStreak(7)
    ),

    // ========== Late-Game Companions ==========

    SHADOW_CAT(
        id = "shadow_cat",
        displayName = "Gato de Sombra",
        element = Element.DARK,
        passiveBonus = PassiveBonus.LocationDiscountPercent(15),
        captureRequirement = CaptureRequirement.LocationsDiscovered(10)
    ),

    CRYSTAL_DEER(
        id = "crystal_deer",
        displayName = "Venado de Cristal",
        element = Element.LIGHT,
        passiveBonus = PassiveBonus.StreakProtection(daysProtected = 2),
        captureRequirement = CaptureRequirement.AchievementUnlocked(
            listOf("master_explorer", "dedicated_14")
        )
    );

    companion object {
        /**
         * Get companion type by ID
         */
        fun fromId(id: String): CompanionType? = values().find { it.id == id }

        /**
         * Get all starter companions (available from beginning)
         */
        fun getStarters(): List<CompanionType> = values().filter {
            it.captureRequirement is CaptureRequirement.AlwaysAvailable
        }

        /**
         * Get all non-starter companions
         */
        fun getUnlockables(): List<CompanionType> = values().filter {
            it.captureRequirement !is CaptureRequirement.AlwaysAvailable
        }
    }
}

/**
 * Element types for companions (affects visual design and lore)
 */
enum class Element(val displayName: String, val color: String) {
    NATURE("Naturaleza", "#4CAF50"),
    FIRE("Fuego", "#FF5722"),
    WATER("Agua", "#2196F3"),
    AIR("Aire", "#00BCD4"),
    EARTH("Tierra", "#795548"),
    ELECTRIC("Eléctrico", "#FFC107"),
    DARK("Oscuridad", "#424242"),
    LIGHT("Luz", "#FFEB3B")
}

/**
 * Passive bonuses that companions provide when active
 */
sealed class PassiveBonus {
    /**
     * Increase energy gained from blocking by X%
     */
    data class EnergyBoost(val percent: Int) : PassiveBonus()

    /**
     * Increase XP gained by X%
     */
    data class XpBoost(val percent: Int) : PassiveBonus()

    /**
     * Reduce location reveal cost by X%
     */
    data class LocationDiscountPercent(val percent: Int) : PassiveBonus()

    /**
     * Protect streak for X days if you miss a day
     */
    data class StreakProtection(val daysProtected: Int) : PassiveBonus()

    /**
     * Get user-friendly description (in Spanish)
     */
    fun getDescription(): String = when (this) {
        is EnergyBoost -> "+$percent% energía de bloqueo"
        is XpBoost -> "+$percent% experiencia"
        is LocationDiscountPercent -> "-$percent% costo de locaciones"
        is StreakProtection -> {
            if (daysProtected == 1) {
                "Protege racha 1 día"
            } else {
                "Protege racha $daysProtected días"
            }
        }
    }
}
