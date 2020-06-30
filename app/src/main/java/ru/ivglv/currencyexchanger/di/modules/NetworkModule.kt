package ru.ivglv.currencyexchanger.di.modules

import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.ivglv.currencyexchanger.framework.network.BaseUrl
import ru.ivglv.currencyexchanger.framework.network.ExchangeRateApiService
import javax.inject.Singleton

@Module
class NetworkModule{
    @Provides
    @Singleton
    fun provideExchangeRateApiService(retrofit: Retrofit) = retrofit.create(ExchangeRateApiService::class.java)

    @Provides
    @Singleton
    fun provideRetrofit(
        rxJava3CallAdapterFactory: RxJava3CallAdapterFactory,
        gsonConverterFactory: GsonConverterFactory,
        @BaseUrl baseUrl: String
    ) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addCallAdapterFactory(rxJava3CallAdapterFactory)
        .addConverterFactory(gsonConverterFactory)
        .build()

    @Provides
    @Singleton
    fun provideRxJava3CallAdapterFactory() = RxJava3CallAdapterFactory.create()

    @Provides
    @Singleton
    fun provideGsonConverterFactory() = GsonConverterFactory.create()
}