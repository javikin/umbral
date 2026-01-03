package com.umbral.di

import com.umbral.data.nfc.NfcManagerImpl
import com.umbral.data.nfc.NfcRepositoryImpl
import com.umbral.domain.nfc.NfcManager
import com.umbral.domain.nfc.NfcRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NfcModule {

    @Binds
    @Singleton
    abstract fun bindNfcManager(
        impl: NfcManagerImpl
    ): NfcManager

    @Binds
    @Singleton
    abstract fun bindNfcRepository(
        impl: NfcRepositoryImpl
    ): NfcRepository
}
