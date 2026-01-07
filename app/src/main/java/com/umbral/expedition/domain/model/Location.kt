package com.umbral.expedition.domain.model

/**
 * Domain model for a discovered location in the expedition map.
 * This is the clean domain representation without Room dependencies.
 */
data class Location(
    val id: String,
    val biome: Biome,
    val discoveredAt: Long,
    val energySpent: Int,
    val loreRead: Boolean = false
) {
    /**
     * Display name of this location
     * Actual location names would come from a strings resource or definition
     */
    val displayName: String
        get() = getLocationName(id)

    /**
     * Lore text for this location
     * Actual lore would come from a strings resource or definition
     */
    val loreText: String
        get() = getLocationLore(id)

    /**
     * Days since discovery
     */
    val daysSinceDiscovery: Int
        get() {
            val now = System.currentTimeMillis()
            val diff = now - discoveredAt
            return (diff / (1000 * 60 * 60 * 24)).toInt()
        }

    /**
     * Check if lore has been read
     */
    val hasReadLore: Boolean
        get() = loreRead

    companion object {
        /**
         * Get location display name by ID
         * In production, this would fetch from strings.xml or a definitions object
         */
        private fun getLocationName(id: String): String = when (id) {
            "forest_01" -> "Claro del Bosque"
            "forest_02" -> "Arroyo Antiguo"
            "forest_03" -> "Roble Centenario"
            "forest_04" -> "Cueva Luminosa"
            "forest_05" -> "Lago Sereno"
            "forest_06" -> "Sendero Oculto"
            "forest_07" -> "Ruinas Misteriosas"
            "forest_08" -> "Torre del Guardián"
            "forest_09" -> "Cascada Cristalina"
            "forest_10" -> "Bosque de Hongos"
            "forest_11" -> "Colina Vista"
            "forest_12" -> "Santuario Perdido"
            "forest_13" -> "Puente de Raíces"
            "forest_14" -> "Claro de las Luciérnagas"
            "forest_15" -> "Corazón del Bosque"
            else -> "Locación Desconocida"
        }

        /**
         * Get location lore text by ID
         * In production, this would fetch from strings.xml or a definitions object
         */
        private fun getLocationLore(id: String): String = when (id) {
            "forest_01" -> "Un claro tranquilo donde la luz del sol penetra entre las copas de los árboles. Este es un lugar perfecto para comenzar tu aventura."
            "forest_02" -> "Un antiguo arroyo cuyas aguas han fluido durante siglos. Dice la leyenda que sus aguas tienen propiedades curativas."
            "forest_03" -> "Un roble majestuoso que ha estado aquí por más de 500 años. Los habitantes del bosque lo consideran sagrado."
            "forest_04" -> "Una cueva iluminada por cristales bioluminiscentes. Es el hogar de criaturas mágicas raras."
            "forest_05" -> "Un lago de aguas tranquilas que refleja el cielo como un espejo. Es conocido por su belleza y paz."
            "forest_06" -> "Un sendero apenas visible, oculto entre la maleza. Solo los exploradores más dedicados lo encuentran."
            "forest_07" -> "Ruinas de una civilización antigua. Los secretos de este lugar están esperando ser descubiertos."
            "forest_08" -> "Una torre solitaria que vigila el bosque. Nadie recuerda quién la construyó o por qué."
            "forest_09" -> "Una cascada cuyas aguas son tan claras que puedes ver cada piedra en el fondo. Es un espectáculo impresionante."
            "forest_10" -> "Un bosque dentro del bosque, lleno de hongos gigantes. Es como entrar en otro mundo."
            "forest_11" -> "Una colina desde la cual puedes ver todo el bosque. El atardecer desde aquí es inolvidable."
            "forest_12" -> "Un santuario olvidado, cubierto de musgo y enredaderas. Aún se siente la magia en el aire."
            "forest_13" -> "Un puente natural formado por raíces de árboles entrelazadas. Es una maravilla de la naturaleza."
            "forest_14" -> "Un claro que se ilumina por la noche con miles de luciérnagas. Es uno de los lugares más mágicos del bosque."
            "forest_15" -> "El centro espiritual del Bosque Místico. Aquí es donde todo comenzó y donde todo converge."
            else -> "Esta locación guarda secretos aún por descubrir."
        }

        /**
         * Get all forest location IDs in order
         */
        fun getAllForestLocationIds(): List<String> = (1..15).map { "forest_%02d".format(it) }
    }
}
