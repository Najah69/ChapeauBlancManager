package com.emage.odoo.core.di

import com.emage.odoo.core.network.JsonRPCProvider
import com.emage.odoo.core.network.OdooApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJsonRPCProvider(): JsonRPCProvider = JsonRPCProvider()

    @Provides
    @Singleton
    fun provideOdooApiService(provider: JsonRPCProvider): OdooApiService =
        OdooApiService(provider)
}
