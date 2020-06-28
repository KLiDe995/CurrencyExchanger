package ru.ivglv.currencyexchanger.framework.network

import dagger.BindsInstance
import dagger.Component

@Component(modules = [NetworkModule::class])
interface NetworkServiceTestFactory {
    fun exchangeRateApi(): ExchangeRateApiService

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance @BaseUrl baseUrl: String): NetworkServiceTestFactory
    }
}