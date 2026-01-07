package com.umbral.expedition.domain.model

/**
 * Achievement definitions and metadata.
 * Contains all 30 achievements across 3 categories.
 */
object AchievementDefinitions {

    val ALL: List<AchievementDef> = listOf(
        // ========== BLOCKING CATEGORY (10 achievements) ==========
        AchievementDef(
            id = "first_step",
            category = Category.BLOCKING,
            title = "Primer Paso",
            description = "Completa tu primera sesión de bloqueo",
            target = 1,
            starsReward = 5
        ),
        AchievementDef(
            id = "golden_hour",
            category = Category.BLOCKING,
            title = "Hora Dorada",
            description = "Completa una sesión de 60 minutos",
            target = 60,
            starsReward = 10
        ),
        AchievementDef(
            id = "marathoner",
            category = Category.BLOCKING,
            title = "Maratonista",
            description = "Completa una sesión de 120 minutos",
            target = 120,
            starsReward = 20
        ),
        AchievementDef(
            id = "consistent_7",
            category = Category.BLOCKING,
            title = "Consistente",
            description = "Mantén una racha de 7 días",
            target = 7,
            starsReward = 15
        ),
        AchievementDef(
            id = "dedicated_14",
            category = Category.BLOCKING,
            title = "Dedicado",
            description = "Mantén una racha de 14 días",
            target = 14,
            starsReward = 25
        ),
        AchievementDef(
            id = "master_30",
            category = Category.BLOCKING,
            title = "Maestro",
            description = "Mantén una racha de 30 días",
            target = 30,
            starsReward = 50
        ),
        AchievementDef(
            id = "centurion",
            category = Category.BLOCKING,
            title = "Centurión",
            description = "Completa 100 sesiones totales",
            target = 100,
            starsReward = 30
        ),
        AchievementDef(
            id = "thousand_min",
            category = Category.BLOCKING,
            title = "Mil Minutos",
            description = "Acumula 1000 minutos bloqueados",
            target = 1000,
            starsReward = 25
        ),
        AchievementDef(
            id = "iron_will",
            category = Category.BLOCKING,
            title = "Voluntad de Hierro",
            description = "Acumula 5000 minutos bloqueados",
            target = 5000,
            starsReward = 50
        ),
        AchievementDef(
            id = "legend",
            category = Category.BLOCKING,
            title = "Leyenda",
            description = "Acumula 10000 minutos bloqueados",
            target = 10000,
            starsReward = 100
        ),

        // ========== EXPLORATION CATEGORY (10 achievements) ==========
        AchievementDef(
            id = "novice_explorer",
            category = Category.EXPLORATION,
            title = "Explorador Novato",
            description = "Descubre tu primera locación",
            target = 1,
            starsReward = 5
        ),
        AchievementDef(
            id = "cartographer",
            category = Category.EXPLORATION,
            title = "Cartógrafo",
            description = "Descubre 5 locaciones diferentes",
            target = 5,
            starsReward = 10
        ),
        AchievementDef(
            id = "adventurer",
            category = Category.EXPLORATION,
            title = "Aventurero",
            description = "Descubre 10 locaciones diferentes",
            target = 10,
            starsReward = 20
        ),
        AchievementDef(
            id = "master_explorer",
            category = Category.EXPLORATION,
            title = "Maestro Explorador",
            description = "Descubre las 15 locaciones del mapa",
            target = 15,
            starsReward = 50
        ),
        AchievementDef(
            id = "lore_reader_5",
            category = Category.EXPLORATION,
            title = "Lector de Lore",
            description = "Lee 5 historias de locaciones",
            target = 5,
            starsReward = 10
        ),
        AchievementDef(
            id = "historian",
            category = Category.EXPLORATION,
            title = "Historiador",
            description = "Lee todas las historias disponibles",
            target = 15,
            starsReward = 30
        ),
        AchievementDef(
            id = "biome_complete",
            category = Category.EXPLORATION,
            title = "Bioma Completo",
            description = "Completa el 100% del Bosque Místico",
            target = 100,
            starsReward = 50
        ),
        AchievementDef(
            id = "collector",
            category = Category.EXPLORATION,
            title = "Coleccionista",
            description = "Captura todos los compañeros base",
            target = 8,
            starsReward = 40
        ),
        AchievementDef(
            id = "no_stone",
            category = Category.EXPLORATION,
            title = "Sin Piedra Sin Voltear",
            description = "Encuentra todos los secretos ocultos",
            target = 5,
            starsReward = 25
        ),
        AchievementDef(
            id = "speedrunner",
            category = Category.EXPLORATION,
            title = "Speedrunner",
            description = "Completa el bioma en menos de 14 días",
            target = 1,
            starsReward = 75
        ),

        // ========== COMPANION CATEGORY (10 achievements) ==========
        AchievementDef(
            id = "first_friend",
            category = Category.COMPANION,
            title = "Primer Amigo",
            description = "Captura tu primer compañero",
            target = 1,
            starsReward = 5
        ),
        AchievementDef(
            id = "duo",
            category = Category.COMPANION,
            title = "Dúo",
            description = "Captura 2 compañeros diferentes",
            target = 2,
            starsReward = 10
        ),
        AchievementDef(
            id = "team",
            category = Category.COMPANION,
            title = "Equipo",
            description = "Captura 4 compañeros diferentes",
            target = 4,
            starsReward = 20
        ),
        AchievementDef(
            id = "all_together",
            category = Category.COMPANION,
            title = "Todos Juntos",
            description = "Captura los 8 compañeros base",
            target = 8,
            starsReward = 40
        ),
        AchievementDef(
            id = "first_evolution",
            category = Category.COMPANION,
            title = "Primera Evolución",
            description = "Evoluciona un compañero por primera vez",
            target = 1,
            starsReward = 15
        ),
        AchievementDef(
            id = "evolutionist",
            category = Category.COMPANION,
            title = "Evolucionista",
            description = "Evoluciona 3 compañeros diferentes",
            target = 3,
            starsReward = 25
        ),
        AchievementDef(
            id = "master_breeder",
            category = Category.COMPANION,
            title = "Maestro Criador",
            description = "Consigue todas las 24 evoluciones",
            target = 24,
            starsReward = 100
        ),
        AchievementDef(
            id = "best_friend",
            category = Category.COMPANION,
            title = "Mejor Amigo",
            description = "Acumula 1000 energía en un compañero",
            target = 1000,
            starsReward = 20
        ),
        AchievementDef(
            id = "eternal_bond",
            category = Category.COMPANION,
            title = "Vínculo Eterno",
            description = "Alcanza la evolución máxima de un compañero",
            target = 1,
            starsReward = 30
        ),
        AchievementDef(
            id = "full_sanctuary",
            category = Category.COMPANION,
            title = "Santuario Lleno",
            description = "Todos los compañeros en evolución máxima",
            target = 8,
            starsReward = 150
        )
    )

    enum class Category(val displayName: String) {
        BLOCKING("Bloqueo"),
        EXPLORATION("Exploración"),
        COMPANION("Compañeros")
    }

    /**
     * Get achievement definition by ID
     */
    fun getById(id: String): AchievementDef? = ALL.find { it.id == id }

    /**
     * Get all achievements for a category
     */
    fun getByCategory(category: Category): List<AchievementDef> =
        ALL.filter { it.category == category }
}

/**
 * Achievement definition data class
 */
data class AchievementDef(
    val id: String,
    val category: AchievementDefinitions.Category,
    val title: String,
    val description: String,
    val target: Int,
    val starsReward: Int
)
