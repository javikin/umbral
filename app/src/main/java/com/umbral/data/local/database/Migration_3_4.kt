package com.umbral.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 3 to 4.
 * Adds Expedition gamification tables:
 * - companions
 * - discovered_locations
 * - player_progress
 * - achievements
 * - sanctuary_decorations
 */
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create companions table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS companions (
                id TEXT PRIMARY KEY NOT NULL,
                type TEXT NOT NULL,
                name TEXT,
                evolution_state INTEGER NOT NULL DEFAULT 1,
                energy_invested INTEGER NOT NULL DEFAULT 0,
                captured_at INTEGER NOT NULL,
                is_active INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        database.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_companions_type ON companions(type)"
        )

        // Create discovered_locations table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS discovered_locations (
                id TEXT PRIMARY KEY NOT NULL,
                biome_id TEXT NOT NULL,
                discovered_at INTEGER NOT NULL,
                energy_spent INTEGER NOT NULL,
                lore_read INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_discovered_locations_biome_id ON discovered_locations(biome_id)"
        )

        // Create player_progress table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS player_progress (
                id INTEGER PRIMARY KEY NOT NULL DEFAULT 1,
                level INTEGER NOT NULL DEFAULT 1,
                current_xp INTEGER NOT NULL DEFAULT 0,
                total_energy INTEGER NOT NULL DEFAULT 0,
                stars INTEGER NOT NULL DEFAULT 0,
                current_streak INTEGER NOT NULL DEFAULT 0,
                longest_streak INTEGER NOT NULL DEFAULT 0,
                total_blocking_minutes INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        // Insert initial progress row
        database.execSQL("""
            INSERT INTO player_progress (id, level, current_xp, total_energy, stars, current_streak, longest_streak, total_blocking_minutes)
            VALUES (1, 1, 0, 0, 0, 0, 0, 0)
        """.trimIndent())

        // Create achievements table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS achievements (
                id TEXT PRIMARY KEY NOT NULL,
                category TEXT NOT NULL,
                progress INTEGER NOT NULL DEFAULT 0,
                target INTEGER NOT NULL,
                unlocked_at INTEGER,
                stars_reward INTEGER NOT NULL
            )
        """.trimIndent())

        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_achievements_category ON achievements(category)"
        )

        // Create sanctuary_decorations table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS sanctuary_decorations (
                id TEXT PRIMARY KEY NOT NULL,
                type TEXT NOT NULL,
                position_x REAL,
                position_y REAL,
                purchased_at INTEGER NOT NULL
            )
        """.trimIndent())
    }
}
