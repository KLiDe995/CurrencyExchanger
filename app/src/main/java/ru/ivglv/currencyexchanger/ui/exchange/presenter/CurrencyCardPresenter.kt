package ru.ivglv.currencyexchanger.ui.exchange.presenter

import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import moxy.InjectViewState
import ru.ivglv.currencyexchanger.domain.interactor.CurrencyAccountInteractor
import ru.ivglv.currencyexchanger.domain.interactor.ExchangeRateInteractor
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.model.ExchangeInput
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import ru.ivglv.currencyexchanger.scheduler.BaseSchedulerProvider
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.CurrencyAccountView
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@InjectViewState
@Singleton
class CurrencyCardPresenter @Inject constructor(
    private var currencyAccountInteractor: CurrencyAccountInteractor,
    private var exchangeRateInteractor: ExchangeRateInteractor,
    private var schedulerProvider: BaseSchedulerProvider
) : BasePresenter<CurrencyAccountView>() {
    //private var updateValueFlows = CompositeDisposable()
    private var currencyList: List<CurrencyAccount> = listOf()
        set(value) {
            field = value
            currencyListObservable.onNext(value)
        }
    private val currencyListObservable = BehaviorSubject.createDefault(currencyList)

    override fun onFirstViewAttach() {
        startCurrenciesObservation()
        startExchangeValuesObservation()
    }

    private fun startCurrenciesObservation() {
        val disposable = currencyAccountInteractor.getCurrencyCount
            .execute()
            .flatMap { getCurrencyList(it) }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribeBy(
                onNext = {
                    Timber.d("Update currencies on ViewPager: $it")
                    currencyList = it
                    viewState.updateCurrencies(it)
                },
                onError = {
                    Timber.e(it)
                }
            )
        unsibscribeOnDestroy(disposable)
    }

    private fun getCurrencyList(dbCurrencyCount: Int) =
        if(dbCurrencyCount != 0)
            currencyAccountInteractor
                .getCurrencyList
                .execute()
        else
            Flowable.just(listOf())

    fun setInputFocus(cardType: ExchangeInput.CurrencyCardType?) {
        ExchangeInput.inputFocus = cardType
        Timber.d("Set focus: ${ExchangeInput.inputFocus}")
    }

    fun exchangeValueInputChanged(newValue: String, cardType: ExchangeInput.CurrencyCardType) {
        when(cardType) {
            ExchangeInput.CurrencyCardType.PUT ->
                if(ExchangeInput.putterValue != newValue)
                    ExchangeInput.putterValue = newValue
            ExchangeInput.CurrencyCardType.GET ->
                if(ExchangeInput.getterValue != newValue)
                    ExchangeInput.getterValue = newValue
        }
    }

    private fun startExchangeValuesObservation() {
        currencyListObservable.toFlowable(BackpressureStrategy.LATEST)
            .filter { it.isNotEmpty() }
            .subscribeBy(
                onNext = {
                    Timber.d("Start recounting value routine (OBSERVE VALUES)...")
                    recountExchangeValues()
                },
                onError = { Timber.e(it) }
            )
    }

    private fun recountExchangeValues() {
        getCurrencyPairFlowable()
            .subscribeOn(schedulerProvider.single())
            .observeOn(schedulerProvider.io())
            .flatMap {
                getRecountedExchangeValues(it)
            }
            .observeOn(schedulerProvider.ui())
            .subscribeBy(
                onNext = {
                    Timber.d("Updating recounted values: $it")
                    viewState.updateRecountedValueLabel(it) },
                onError = {
                    Timber.e(it)
                }
            )
    }

    private fun getCurrencyPairFlowable() =
        Flowable.merge(
            ExchangeInput.putterValueObservable
                .toFlowable(BackpressureStrategy.LATEST),
            ExchangeInput.getterValueObservable
                .toFlowable(BackpressureStrategy.LATEST),
            ExchangeInput.putterCurrencyIndexObservable
                .toFlowable(BackpressureStrategy.LATEST),
            ExchangeInput.getterCurrencyIndexObservable
                .toFlowable(BackpressureStrategy.LATEST)
        )
            .distinctUntilChanged()
            .map { Pair(currencyList[ExchangeInput.putterCurrencyIndex], currencyList[ExchangeInput.getterCurrencyIndex]) }

    private fun getRecountedExchangeValues(currencyPair: Pair<CurrencyAccount, CurrencyAccount>) =
        getExchangeRate(currencyPair)
            .map { Pair<Float, Float>(
                recountExchangeValuePut(ExchangeInput.getterValue, it.rate),
                recountExchangeValueGet(ExchangeInput.putterValue, it.rate)
            ) }

    private fun getExchangeRate(currencyPair: Pair<CurrencyAccount, CurrencyAccount>) =
        if (currencyPair.first == currencyPair.second)
            Flowable.just(ExchangeRate(currencyPair.first.currencyName, currencyPair.second.currencyName, 1f))
        else
            exchangeRateInteractor.getExchangeRate
                .apply {
                    baseName = currencyPair.first.currencyName
                    ratedName = currencyPair.second.currencyName
                }
                .execute()
                .distinct()

    private fun recountExchangeValuePut(baseValue: String, rate: Float) =
        if (ExchangeInput.inputFocus == ExchangeInput.CurrencyCardType.PUT)
            ExchangeInput.putterValue.toFloatSafe()
        else
            baseValue.toFloatSafe() / rate

    private fun recountExchangeValueGet(baseValue: String, rate: Float) =
        if(ExchangeInput.inputFocus == ExchangeInput.CurrencyCardType.GET)
            ExchangeInput.getterValue.toFloatSafe()
        else
            baseValue.toFloatSafe() * rate

    private fun String.toFloatSafe() =
        if(this != "") this.replace(",", ".").toFloat()
        else 0f
}