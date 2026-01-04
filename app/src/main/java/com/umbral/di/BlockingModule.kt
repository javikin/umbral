package com.umbral.di

import com.umbral.data.blocking.BlockingManagerImpl
import com.umbral.data.blocking.ForegroundAppMonitorImpl
import com.umbral.domain.blocking.BlockingManager
import com.umbral.domain.blocking.ForegroundAppMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BlockingModule {

    @Binds
    @Singleton
    abstract fun bindBlockingManager(
        impl: BlockingManagerImpl
    ): BlockingManager

    @Binds
    @Singleton
    abstract fun bindForegroundAppMonitor(
        impl: ForegroundAppMonitorImpl
    ): ForegroundAppMonitor
}
