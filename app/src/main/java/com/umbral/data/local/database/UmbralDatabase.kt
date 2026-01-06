package com.umbral.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.umbral.data.local.dao.BlockingEventDao
import com.umbral.data.local.dao.BlockingProfileDao
import com.umbral.data.local.dao.NfcTagDao
import com.umbral.data.local.dao.StatsDao
import com.umbral.data.local.entity.BlockedAppEntity
import com.umbral.data.local.entity.BlockedAttemptEntity
import com.umbral.data.local.entity.BlockingEventEntity
import com.umbral.data.local.entity.BlockingProfileEntity
import com.umbral.data.local.entity.BlockingSessionEntity
import com.umbral.data.local.entity.NfcTagEntity

@Database(
    entities = [
        BlockingProfileEntity::class,
        BlockedAppEntity::class,
        NfcTagEntity::class,
        BlockedAttemptEntity::class,
        BlockingSessionEntity::class,
        BlockingEventEntity::class
    ],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class UmbralDatabase : RoomDatabase() {

    abstract fun blockingProfileDao(): BlockingProfileDao
    abstract fun nfcTagDao(): NfcTagDao
    abstract fun statsDao(): StatsDao
    abstract fun blockingEventDao(): BlockingEventDao

    companion object {
        private const val DATABASE_NAME = "umbral_database"

        @Volatile
        private var INSTANCE: UmbralDatabase? = null

        fun getInstance(context: Context): UmbralDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UmbralDatabase::class.java,
                    DATABASE_NAME
                )
                    .addMigrations(MIGRATION_2_3)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
