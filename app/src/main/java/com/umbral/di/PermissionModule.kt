package com.umbral.di

import com.umbral.data.permission.PermissionManagerImpl
import com.umbral.domain.permission.PermissionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PermissionModule {

    @Binds
    @Singleton
    abstract fun bindPermissionManager(
        impl: PermissionManagerImpl
    ): PermissionManager
}
