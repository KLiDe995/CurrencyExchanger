package ru.ivglv.currencyexchanger.ui.exchange.presenter

import dagger.Component
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.CurrencyAccountView
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.ExchangerView
import javax.inject.Singleton

@Singleton
@Component(modules = [PresenterTestModule::class, TestSchedulerModule::class])
interface PresenterTestComponent {
    fun getRepositoryMock(): Repository
    fun getCurrencyAccountViewMock(): CurrencyAccountView
    fun getExchangeViewMock(): ExchangerView

    fun exchangerPresenter(): ExchangerPresenter
    fun currencyCardPresenter(): CurrencyCardPresenter
}