package ru.ivglv.currencyexchanger.ui.exchange.presenter

import com.nhaarman.mockitokotlin2.mock
import dagger.Module
import dagger.Provides
import org.mockito.Mockito
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.interactor.usecase.*
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import javax.inject.Singleton

@Module
class ExchangePresenterTestModule {
    @Provides
    @Singleton
    fun provideRepository(): Repository = mock()
}