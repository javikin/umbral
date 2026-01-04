package com.umbral.di

import com.umbral.data.onboarding.OnboardingManagerImpl
import com.umbral.data.onboarding.PermissionHelperImpl
import com.umbral.domain.onboarding.OnboardingManager
import com.umbral.domain.onboarding.PermissionHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingModule {

    @Binds
    @Singleton
    abstract fun bindOnboardingManager(
        impl: OnboardingManagerImpl
    ): OnboardingManager

    @Binds
    @Singleton
    abstract fun bindPermissionHelper(
        impl: PermissionHelperImpl
    ): PermissionHelper
}
