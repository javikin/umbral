package com.umbral.notifications.di

import android.content.Context
import com.umbral.notifications.util.NotificationPermissionManager
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
 * - Future: Repository for notification data
 * - Future: UseCase for notification filtering
 */
@Module
@InstallIn(SingletonComponent::class)
object NotificationsModule {

    @Provides
    @Singleton
    fun provideNotificationPermissionManager(
        @ApplicationContext context: Context
    ): NotificationPermissionManager {
        return NotificationPermissionManager(context)
    }

    // TODO (Issue #65): Provide NotificationRepository
    // TODO (Issue #65): Provide NotificationDao
    // TODO (Issue #64): Provide NotificationFilterUseCase
}
