package com.umbral.notifications.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.umbral.data.local.database.UmbralDatabase
import com.umbral.notifications.data.local.BlockedNotificationDao
import com.umbral.notifications.data.preferences.NotificationPreferences
import com.umbral.notifications.data.repository.NotificationRepositoryImpl
import com.umbral.notifications.domain.NotificationWhitelistChecker
import com.umbral.notifications.domain.repository.NotificationRepository
import com.umbral.notifications.util.NotificationPermissionManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for notifications feature dependencies.
 *
 * Provides:
 * - NotificationPermissionManager for permission checks
 * - BlockedNotificationDao for database access
 * - NotificationRepository for data operations
 * - NotificationPreferences for user whitelist management
 * - NotificationWhitelistChecker for whitelist validation
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationsModule {

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl
    ): NotificationRepository

    companion object {
        @Provides
        @Singleton
        fun provideNotificationPermissionManager(
            @ApplicationContext context: Context
        ): NotificationPermissionManager {
            return NotificationPermissionManager(context)
        }

        @Provides
        @Singleton
        fun provideBlockedNotificationDao(
            database: UmbralDatabase
        ): BlockedNotificationDao {
            return database.blockedNotificationDao()
        }

        @Provides
        @Singleton
        fun provideNotificationPreferences(
            dataStore: DataStore<Preferences>
        ): NotificationPreferences {
            return NotificationPreferences(dataStore)
        }

        @Provides
        @Singleton
        fun provideNotificationWhitelistChecker(
            notificationPreferences: NotificationPreferences
        ): NotificationWhitelistChecker {
            return NotificationWhitelistChecker(notificationPreferences)
        }
    }
}
