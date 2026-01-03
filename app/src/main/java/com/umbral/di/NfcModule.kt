package com.umbral.di

import com.umbral.data.nfc.NfcManagerImpl
import com.umbral.domain.nfc.NfcManager
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
}
