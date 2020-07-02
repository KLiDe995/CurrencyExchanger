package ru.ivglv.currencyexchanger.ui.exchange.presenter

import com.arellomobile.mvp.InjectViewState
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.toFlowable
import ru.ivglv.currencyexchanger.ExchangeApp
import ru.ivglv.currencyexchanger.domain.interactor.CurrencyAccountInteractor
import ru.ivglv.currencyexchanger.domain.interactor.ExchangeRateInteractor
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import ru.ivglv.currencyexchanger.scheduler.BaseSchedulerProvider
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.ExchangerView
import javax.inject.Inject

@InjectViewState
class ExchangerPresenter : BasePresenter<ExchangerView>() {
    @Inject
    lateinit var currencyAccountInteractor: CurrencyAccountInteractor
    @Inject
    lateinit var exchangeRateInteractor: ExchangeRateInteractor
    @Inject
    lateinit var schedulerProvider: BaseSchedulerProvider

    private var currencyNetSubscribers = CompositeDisposable()


    override fun onFirstViewAttach() {
        ExchangeApp.appComponent.inject(this)
        initCurrencies()
        startRatesUpdate()
    }

    private fun initCurrencies() =
        currencyAccountInteractor.getCurrencyCount.execute()
            .subscribeOn(schedulerProvider.io())
            .firstOrError()
            .subscribeBy(
                onSuccess = { if(it == 0) addStarterCurrencies() },
                onError = { it.printStackTrace() } // TODO: Log
            )

    private fun addStarterCurrencies() =
        currencyAccountInteractor.createCurrencyAccountList
            .apply {
                currencies = getStarterCurrencyPack()
            }
            .execute()
            .subscribeOn(schedulerProvider.io())
            .subscribeBy(
                onError = { it.printStackTrace() } // TODO: Log
            )

    private fun getStarterCurrencyPack() =
        listOf(
            CurrencyAccount("USD", 100f, '$'),
            CurrencyAccount("EUR", 100f, '€'),
            CurrencyAccount("GBP", 100f, '£')
        ) // TODO: вынести в конфигурацию

    private fun startRatesUpdate() {
        val subscription = currencyAccountInteractor.getCurrencyCount.execute()
            .subscribeOn(schedulerProvider.io())
            .flatMapSingle {
                currencyAccountInteractor.getCurrencyList.execute()
                    .subscribeOn(schedulerProvider.io())
                    .doOnSuccess { updateRatesForCurrencyList(it) }
            }
            .subscribeBy(
                onError = { it.printStackTrace() } // TODO: Log
            )
        unsibscribeOnDestroy(subscription)
    }

    private fun updateRatesForCurrencyList(currencyAccountList: List<CurrencyAccount>) {
        if(currencyNetSubscribers.size() != currencyAccountList.size) {
            currencyNetSubscribers.clear()
            for(currencyAccount in currencyAccountList)
                updateRatesForCurrency(currencyAccount).addTo(currencyNetSubscribers)
        }
    }

    private fun updateRatesForCurrency(currencyAccount: CurrencyAccount) =
        exchangeRateInteractor.downloadExchangeRates.apply {
            currencyBaseName = currencyAccount.currencyName
        }.execute()
            .subscribeOn(schedulerProvider.io())
            .doOnNext { addOrUpdateExchangeRates(it) }
            .subscribeBy(
                onError = { it.printStackTrace() } // TODO: Log
            )

    private fun addOrUpdateExchangeRates(exchangeRateList: List<ExchangeRate>) =
        exchangeRateList.toFlowable()
            .subscribeOn(schedulerProvider.computation())
            .flatMapSingle { exchangeRate ->
                exchangeRateInteractor.createExchangeRate.apply { this.exchangeRate = exchangeRate }
                    .execute()
                    .subscribeOn(schedulerProvider.io())
                    .onErrorResumeNext {
                        exchangeRateInteractor.updateExchangeRate.apply { updatedExchangeRate = exchangeRate }
                        .execute().toSingleDefault(0)
                    }
            }
            .subscribeBy(
                onError = { it.printStackTrace() } // TODO: Log
            )

    override fun onDestroy() {
        super.onDestroy()
        currencyNetSubscribers.clear()
    }
}