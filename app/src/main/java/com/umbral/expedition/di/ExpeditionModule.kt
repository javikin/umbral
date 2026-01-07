package com.umbral.expedition.di

import com.umbral.data.local.database.UmbralDatabase
import com.umbral.expedition.data.dao.AchievementDao
import com.umbral.expedition.data.dao.CompanionDao
import com.umbral.expedition.data.dao.DecorationDao
import com.umbral.expedition.data.dao.LocationDao
import com.umbral.expedition.data.dao.ProgressDao
import com.umbral.expedition.data.repository.ExpeditionRepository
import com.umbral.expedition.data.repository.ExpeditionRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for Expedition gamification dependencies.
 *
 * Provides DAOs from the UmbralDatabase for expedition features:
 * - Companions
 * - Locations
 * - Player Progress
 * - Achievements
 * - Sanctuary Decorations
 */
@Module
@InstallIn(SingletonComponent::class)
object ExpeditionModule {

    @Provides
    @Singleton
    fun provideCompanionDao(database: UmbralDatabase): CompanionDao {
        return database.companionDao()
    }

    @Provides
    @Singleton
    fun provideLocationDao(database: UmbralDatabase): LocationDao {
        return database.locationDao()
    }

    @Provides
    @Singleton
    fun provideProgressDao(database: UmbralDatabase): ProgressDao {
        return database.progressDao()
    }

    @Provides
    @Singleton
    fun provideAchievementDao(database: UmbralDatabase): AchievementDao {
        return database.achievementDao()
    }

    @Provides
    @Singleton
    fun provideDecorationDao(database: UmbralDatabase): DecorationDao {
        return database.decorationDao()
    }

    @Provides
    @Singleton
    fun provideExpeditionRepository(
        companionDao: CompanionDao,
        locationDao: LocationDao,
        progressDao: ProgressDao,
        achievementDao: AchievementDao
    ): ExpeditionRepository {
        return ExpeditionRepositoryImpl(
            companionDao = companionDao,
            locationDao = locationDao,
            progressDao = progressDao,
            achievementDao = achievementDao
        )
    }
}
