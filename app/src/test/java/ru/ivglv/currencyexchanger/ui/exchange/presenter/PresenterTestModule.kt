package ru.ivglv.currencyexchanger.ui.exchange.presenter

import com.nhaarman.mockitokotlin2.mock
import dagger.Module
import dagger.Provides
import org.mockito.Mockito
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.interactor.usecase.*
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.CurrencyAccountView
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.ExchangerView
import javax.inject.Singleton

@Module
class PresenterTestModule {
    @Provides
    @Singleton
    fun provideRepository(): Repository = mock()

    @Provides
    fun provideCurrencyAccountView(): CurrencyAccountView = mock()

    @Provides
    fun provideExchangerView(): ExchangerView = mock()
}