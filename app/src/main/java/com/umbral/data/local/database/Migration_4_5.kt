package com.umbral.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migration from version 4 to 5.
 * Adds blocked_notifications table for storing intercepted notifications during blocking sessions.
 */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create blocked_notifications table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS blocked_notifications (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                session_id TEXT NOT NULL,
                package_name TEXT NOT NULL,
                app_name TEXT NOT NULL,
                title TEXT,
                text TEXT,
                timestamp INTEGER NOT NULL,
                icon_uri TEXT,
                is_read INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        // Create indices for efficient queries
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_blocked_notifications_session_id ON blocked_notifications(session_id)"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_blocked_notifications_package_name ON blocked_notifications(package_name)"
        )
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS index_blocked_notifications_timestamp ON blocked_notifications(timestamp)"
        )
    }
}
