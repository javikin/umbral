package com.umbral.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 2 to 3.
 * Adds the blocking_events table for unified event tracking.
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create blocking_events table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS blocking_events (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                timestamp INTEGER NOT NULL,
                eventType TEXT NOT NULL,
                profileId TEXT,
                packageName TEXT,
                durationMinutes INTEGER,
                FOREIGN KEY (profileId) REFERENCES blocking_profiles(id) ON DELETE SET NULL
            )
        """.trimIndent())

        // Create indexes for performance
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_blocking_events_timestamp ON blocking_events(timestamp)"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_blocking_events_eventType ON blocking_events(eventType)"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_blocking_events_profileId ON blocking_events(profileId)"
        )
    }
}
