package ru.ivglv.currencyexchanger.ui.exchange.presenter

import dagger.Component
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import javax.inject.Singleton

@Singleton
@Component(modules = [ExchangePresenterTestModule::class, TestSchedulerModule::class])
interface ExchangePresenterTestComponent {
    fun getRepositoryMock(): Repository
    fun inject(exchangerPresenter: ExchangerPresenter)
}