package com.umbral.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 5 to 6.
 * Adds blockNotifications column to blocking_profiles table.
 * This allows users to enable/disable notification blocking per profile.
 */
val MIGRATION_5_6 = object : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add blockNotifications column with default value true (existing behavior)
        database.execSQL(
            "ALTER TABLE blocking_profiles ADD COLUMN blockNotifications INTEGER NOT NULL DEFAULT 1"
        )
    }
}
