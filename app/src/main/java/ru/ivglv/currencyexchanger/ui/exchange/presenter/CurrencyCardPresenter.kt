package ru.ivglv.currencyexchanger.ui.exchange.presenter

import io.reactivex.rxjava3.kotlin.subscribeBy
import moxy.InjectViewState
import ru.ivglv.currencyexchanger.di.AppComponent
import ru.ivglv.currencyexchanger.domain.interactor.CurrencyAccountInteractor
import ru.ivglv.currencyexchanger.scheduler.BaseSchedulerProvider
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.CurrencyAccountView
import javax.inject.Inject

@InjectViewState
class CurrencyCardPresenter @Inject constructor(
    private var currencyAccountInteractor: CurrencyAccountInteractor,
    private var schedulerProvider: BaseSchedulerProvider
) : BasePresenter<CurrencyAccountView>() {

    override fun onFirstViewAttach() {
        initCurrenciesObserver()
    }

    private fun initCurrenciesObserver() {
        val disposable = currencyAccountInteractor.getCurrencyCount
            .execute()
            .subscribeOn(schedulerProvider.io())
            .flatMap {
                currencyAccountInteractor
                    .getCurrencyList
                    .execute()
            }
            .subscribeBy(
                onNext = { viewState.updateCurrencies(it) },
                onError = { it.printStackTrace() } // TODO: Log
            )
        unsibscribeOnDestroy(disposable)
    }

}