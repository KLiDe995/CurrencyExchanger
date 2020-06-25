package ru.ivglv.currencyexchanger.framework.network

import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {
    @Provides
    fun provideExchangeRateApiService(retrofit: Retrofit) = retrofit.create(ExchangeRateApiService::class.java)

    @Provides
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
    fun provideRxJava3CallAdapterFactory() = RxJava3CallAdapterFactory.create()

    @Provides
    fun provideGsonConverterFactory() = GsonConverterFactory.create()
}