package ru.ivglv.currencyexchanger.ui.exchange.presenter

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.kotlin.toFlowable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import moxy.InjectViewState
import ru.ivglv.currencyexchanger.domain.interactor.CurrencyAccountInteractor
import ru.ivglv.currencyexchanger.domain.interactor.ExchangeRateInteractor
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.model.ExchangeInput
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import ru.ivglv.currencyexchanger.scheduler.BaseSchedulerProvider
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.ExchangerView
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@InjectViewState
@Singleton
class ExchangerPresenter @Inject constructor(
    private var currencyAccountInteractor: CurrencyAccountInteractor,
    private var exchangeRateInteractor: ExchangeRateInteractor,
    private var schedulerProvider: BaseSchedulerProvider
) : BasePresenter<ExchangerView>() {

    private val currencyNetFlows = CompositeDisposable()
    private var currentRateString = ""
    private val updateRateFlows = CompositeDisposable()
    private var currencyList: List<CurrencyAccount> = listOf()
        set(value) {
            field = value
            currencyListObservable.onNext(value)
        }
    private val currencyListObservable = BehaviorSubject.createDefault(currencyList)

    override fun onFirstViewAttach() {
        initCurrencies()
        startRatesUpdate()
        startExchangeValuesObservation()
    }

    private fun initCurrencies() =
        currencyAccountInteractor.getCurrencyCount.execute()
            .subscribeOn(schedulerProvider.io())
            .firstOrError()
            .subscribeBy(
                onSuccess = {
                    Timber.d("Init currencies in db")
                    if(it == 0) addStarterCurrencies()
                },
                onError = {
                    Timber.e(it)
                }
            )

    private fun addStarterCurrencies() =
        currencyAccountInteractor.createCurrencyAccountList
            .apply {
                currencies = getStarterCurrencyPack()
            }
            .execute()
            .subscribeOn(schedulerProvider.io())
            .subscribeBy(
                onSuccess = {
                    Timber.d("Added starter currencies")
                },
                onError = {
                    Timber.e(it)
                }
            )

    private fun getStarterCurrencyPack() =
        listOf(
            CurrencyAccount("USD", 100f, '$'),
            CurrencyAccount("EUR", 100f, '€'),
            CurrencyAccount("GBP", 100f, '£')
        ) // TODO: вынести в конфигурацию

    private fun startRatesUpdate() {
        val subscription = currencyAccountInteractor.getCurrencyCount.execute()
            .flatMapSingle {
                currencyAccountInteractor.getCurrencyList.execute()
                    .firstOrError()
                    .doOnSuccess {
                        currencyList = it
                        updateRatesForCurrencyList(it)
                    }
            }
            .subscribeOn(schedulerProvider.io())
            .subscribeBy(
                onNext = {
                    //Timber.d("Rates updated")
                },
                onError = {
                    Timber.e(it)
                }
            )
        unsibscribeOnDestroy(subscription)
    }

    private fun updateRatesForCurrencyList(currencyAccountList: List<CurrencyAccount>) {
        if(currencyNetFlows.size() != currencyAccountList.size) {
            currencyNetFlows.clear()
            for(currencyAccount in currencyAccountList)
                updateRatesForCurrency(currencyAccount).addTo(currencyNetFlows)
        }
    }

    private fun updateRatesForCurrency(currencyAccount: CurrencyAccount) =
        exchangeRateInteractor.downloadExchangeRates.apply {
            currencyBaseName = currencyAccount.currencyName
        }.execute()
            .subscribeOn(schedulerProvider.io())
            .doOnNext {
                Timber.d("Rates downloaded for %s: %s", currencyAccount, it)
                addOrUpdateExchangeRates(it) }
            .subscribeBy(
                onNext = {
                    Timber.d("Rates updated for %s", currencyAccount)
                },
                onError = {
                    Timber.e(it)
                }
            )

    private fun addOrUpdateExchangeRates(exchangeRateList: List<ExchangeRate>) =
        exchangeRateList.toFlowable()
            .subscribeOn(schedulerProvider.computation())
            .flatMapSingle { exchangeRate ->
                exchangeRateInteractor.createExchangeRate.apply { this.exchangeRate = exchangeRate }
                    .execute()
                    .onErrorResumeNext {
                        exchangeRateInteractor.updateExchangeRate.apply { updatedExchangeRate = exchangeRate }
                        .execute().toSingleDefault(0)
                    }
                    .subscribeOn(schedulerProvider.io())
            }
            .subscribeBy(
                onNext = {
                    //Timber.d("Rate updated %s", it.toString())
                },
                onError = {
                    Timber.e(it)
                }
            )

    override fun onDestroy() {
        super.onDestroy()
        currencyNetFlows.clear()
    }

    fun currencyAccountSelectedIndexChanged(index: Int, currencyCardType: ExchangeInput.CurrencyCardType) {
        when(currencyCardType) {
            ExchangeInput.CurrencyCardType.PUT -> {
                if (ExchangeInput.putterCurrencyIndex != index)
                    ExchangeInput.putterCurrencyIndex = index
                ExchangeInput.putterValue = ""
            }
            ExchangeInput.CurrencyCardType.GET -> {
                if(ExchangeInput.getterCurrencyIndex != index)
                    ExchangeInput.getterCurrencyIndex = index
                ExchangeInput.getterValue = ""
            }
        }
    }

    private fun startExchangeValuesObservation() {
        currencyListObservable.toFlowable(BackpressureStrategy.LATEST)
            .filter { it.isNotEmpty() }
            .subscribeBy(
                onNext = {
                    Timber.d("Updating rate label...")
                    updateRateFlows.clear()
                    updateRateInfo()
                },
                onError = { Timber.e(it) }
            )
    }

    private fun updateRateInfo() {
        getCurrencyPairFlowable()
            .subscribeOn(schedulerProvider.single())
            .doOnNext { Timber.d("Changed currencies. Updating rate label: $it") }
            .flatMap {
                getExchangeRateString(it)
            }
            .observeOn(schedulerProvider.ui())
            .subscribeBy(
                onNext = {
                    Timber.d("Rate label updated: $it")
                    currentRateString = it
                    viewState.updateRateLabel(it) },
                onError = {
                    Timber.e(it)
                }
            ).addTo(updateRateFlows)
    }

    private fun getCurrencyPairFlowable() =
        Flowable.merge(
            ExchangeInput.putterCurrencyIndexObservable
                .toFlowable(BackpressureStrategy.LATEST),
            ExchangeInput.getterCurrencyIndexObservable
                .toFlowable(BackpressureStrategy.LATEST)
        )
            .debounce(100, TimeUnit.MILLISECONDS, schedulerProvider.computation())
            .map { Pair(currencyList[ExchangeInput.putterCurrencyIndex], currencyList[ExchangeInput.getterCurrencyIndex]) }

    private fun getExchangeRateString(currencyPair: Pair<CurrencyAccount, CurrencyAccount>) =
        if (currencyPair.first == currencyPair.second)
            Flowable.just(String.format("${currencyPair.first.currencySymbol}1 = ${currencyPair.second.currencySymbol}1"))
        else
            exchangeRateInteractor.getExchangeRate
                .apply {
                    baseName = currencyPair.first.currencyName
                    ratedName = currencyPair.second.currencyName
                }
                .execute()
                .distinct()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.single())
                .doOnNext { Timber.d("Got exchange rate $it") }
                .map { String.format("${currencyPair.first.currencySymbol}1 = ${currencyPair.second.currencySymbol}%.2f", it.rate) }
                .filter { it != currentRateString }
}