package com.umbral.di

import android.content.Context
import com.umbral.data.qr.QrGeneratorImpl
import com.umbral.data.qr.QrScannerImpl
import com.umbral.data.qr.QrValidatorImpl
import com.umbral.data.security.EncryptionManager
import com.umbral.data.security.EncryptionManagerImpl
import com.umbral.domain.blocking.ProfileRepository
import com.umbral.domain.qr.QrGenerator
import com.umbral.domain.qr.QrScanner
import com.umbral.domain.qr.QrValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QrModule {

    @Provides
    @Singleton
    fun provideEncryptionManager(
        @ApplicationContext context: Context
    ): EncryptionManager {
        return EncryptionManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideQrGenerator(
        @ApplicationContext context: Context,
        encryptionManager: EncryptionManager
    ): QrGenerator {
        return QrGeneratorImpl(context, encryptionManager)
    }

    @Provides
    @Singleton
    fun provideQrValidator(
        encryptionManager: EncryptionManager,
        profileRepository: ProfileRepository
    ): QrValidator {
        return QrValidatorImpl(encryptionManager, profileRepository)
    }

    @Provides
    fun provideQrScanner(
        @ApplicationContext context: Context,
        qrValidator: QrValidator
    ): QrScanner {
        return QrScannerImpl(context, qrValidator)
    }
}
