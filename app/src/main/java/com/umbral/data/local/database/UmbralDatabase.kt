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
import com.umbral.expedition.data.dao.AchievementDao
import com.umbral.expedition.data.dao.CompanionDao
import com.umbral.expedition.data.dao.DecorationDao
import com.umbral.expedition.data.dao.LocationDao
import com.umbral.expedition.data.dao.ProgressDao
import com.umbral.expedition.data.entity.AchievementEntity
import com.umbral.expedition.data.entity.CompanionEntity
import com.umbral.expedition.data.entity.DecorationEntity
import com.umbral.expedition.data.entity.LocationEntity
import com.umbral.expedition.data.entity.ProgressEntity

@Database(
    entities = [
        BlockingProfileEntity::class,
        BlockedAppEntity::class,
        NfcTagEntity::class,
        BlockedAttemptEntity::class,
        BlockingSessionEntity::class,
        BlockingEventEntity::class,
        CompanionEntity::class,
        LocationEntity::class,
        ProgressEntity::class,
        AchievementEntity::class,
        DecorationEntity::class
    ],
    version = 4,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class UmbralDatabase : RoomDatabase() {

    abstract fun blockingProfileDao(): BlockingProfileDao
    abstract fun nfcTagDao(): NfcTagDao
    abstract fun statsDao(): StatsDao
    abstract fun blockingEventDao(): BlockingEventDao

    // Expedition DAOs
    abstract fun companionDao(): CompanionDao
    abstract fun locationDao(): LocationDao
    abstract fun progressDao(): ProgressDao
    abstract fun achievementDao(): AchievementDao
    abstract fun decorationDao(): DecorationDao

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
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
