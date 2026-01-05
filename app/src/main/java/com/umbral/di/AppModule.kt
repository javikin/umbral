package com.umbral.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.umbral.data.local.dao.BlockingProfileDao
import com.umbral.data.local.dao.NfcTagDao
import com.umbral.data.local.dao.StatsDao
import com.umbral.data.local.database.UmbralDatabase
import com.umbral.data.local.preferences.UmbralPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "umbral_preferences")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): UmbralDatabase {
        return UmbralDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideUmbralPreferences(
        dataStore: DataStore<Preferences>
    ): UmbralPreferences {
        return UmbralPreferences(dataStore)
    }

    @Provides
    @Singleton
    fun provideBlockingProfileDao(
        database: UmbralDatabase
    ): BlockingProfileDao {
        return database.blockingProfileDao()
    }

    @Provides
    @Singleton
    fun provideNfcTagDao(
        database: UmbralDatabase
    ): NfcTagDao {
        return database.nfcTagDao()
    }

    @Provides
    @Singleton
    fun provideStatsDao(
        database: UmbralDatabase
    ): StatsDao {
        return database.statsDao()
    }

    @Provides
    @Singleton
    fun providePackageManager(
        @ApplicationContext context: Context
    ): PackageManager {
        return context.packageManager
    }
}
