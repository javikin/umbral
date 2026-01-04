package com.umbral.di

import com.umbral.data.apps.InstalledAppsProviderImpl
import com.umbral.data.apps.InstalledAppsRepositoryImpl
import com.umbral.domain.apps.InstalledAppsProvider
import com.umbral.domain.apps.InstalledAppsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppsModule {

    @Binds
    @Singleton
    abstract fun bindInstalledAppsProvider(
        impl: InstalledAppsProviderImpl
    ): InstalledAppsProvider

    @Binds
    @Singleton
    abstract fun bindInstalledAppsRepository(
        impl: InstalledAppsRepositoryImpl
    ): InstalledAppsRepository
}
