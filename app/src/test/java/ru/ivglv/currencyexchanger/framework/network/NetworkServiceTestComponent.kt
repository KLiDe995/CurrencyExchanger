package ru.ivglv.currencyexchanger.framework.network

import dagger.BindsInstance
import dagger.Component
import ru.ivglv.currencyexchanger.di.modules.NetworkModule
import javax.inject.Singleton

@Singleton
@Component(modules = [NetworkModule::class])
interface NetworkServiceTestComponent {
    fun exchangeRateApi(): ExchangeRateApiService

    @Component.Builder
    interface Builder {
        fun netApiBaseUrl(@BindsInstance @BaseUrl baseUrl: String): Builder
        fun build(): NetworkServiceTestComponent
    }
}