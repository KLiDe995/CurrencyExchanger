package ru.ivglv.currencyexchanger.ui.exchange.presenter

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.BiFunction
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
    private var currencyList: List<CurrencyAccount>? = null

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
        subscribeInputObservable(ExchangeInput.putterValueObservable)
        subscribeInputObservable(ExchangeInput.getterValueObservable)
        subscribeCurrencyIndexObservable(ExchangeInput.putterCurrencyIndexObservable, ExchangeInput.CurrencyCardType.PUT)
        subscribeCurrencyIndexObservable(ExchangeInput.getterCurrencyIndexObservable, ExchangeInput.CurrencyCardType.GET)

    }

    private fun subscribeInputObservable(subject: BehaviorSubject<*>) =
        subject
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribeOn(schedulerProvider.single())
            .observeOn(schedulerProvider.ui())
            .subscribeBy(
                onNext = {
                    Timber.d("Input value changed. Handling: $it (focus: ${ExchangeInput.inputFocus.toString()})")
                    recountExchangeValues()
                },
                onError = {
                    Timber.e(it)
                }
            )

    private fun subscribeCurrencyIndexObservable(subject: BehaviorSubject<Int>, cardType: ExchangeInput.CurrencyCardType) =
        subject
            .subscribeOn(schedulerProvider.single())
            .observeOn(schedulerProvider.ui())
            .subscribeBy(
                onNext = {
                    Timber.d("Currency Index changed. Handling: $it (focus: ${ExchangeInput.inputFocus.toString()})")
                    if(ExchangeInput.inputFocus == cardType)
                        resetInputValue(cardType)
                    else
                        recountExchangeValues()
                },
                onError = {
                    Timber.e(it)
                }
            )

    private fun resetInputValue(resettedCardType: ExchangeInput.CurrencyCardType) {
        when(resettedCardType) {
            ExchangeInput.CurrencyCardType.PUT -> ExchangeInput.putterValue = ""
            ExchangeInput.CurrencyCardType.GET -> ExchangeInput.getterValue = ""
        }
    }

    private fun recountExchangeValues() {
        if(currencyList == null) return
        getCurrencyPairFlowable(
            currencyList!![ExchangeInput.putterCurrencyIndex],
            currencyList!![ExchangeInput.getterCurrencyIndex]
        )
            .distinct()
            .doOnNext { Timber.d("Changed currencies. Updating exchange values: %s", it) }
            .flatMap { getRecountedExchangeValues(it) }
            .subscribeOn(schedulerProvider.io())
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

    private fun getCurrencyPairFlowable(firstCurrency: CurrencyAccount, secondCurrency: CurrencyAccount) =
        Flowable.zip(
            currencyAccountInteractor.getCurrencyAccount
                .apply { requiredCurrencyName = firstCurrency.currencyName }
                .execute(),
            currencyAccountInteractor.getCurrencyAccount
                .apply { requiredCurrencyName = secondCurrency.currencyName }
                .execute(),
            BiFunction<CurrencyAccount, CurrencyAccount, Pair<CurrencyAccount, CurrencyAccount>> {
                    baseCurrency, ratedCurrency ->
                Pair(baseCurrency, ratedCurrency)
            }
        )

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
        if(baseValue != "") baseValue.replace(",", ".").toFloat() / rate else 0f

    private fun recountExchangeValueGet(baseValue: String, rate: Float) =
        if(baseValue != "") baseValue.replace(",", ".").toFloat() * rate else 0f

}