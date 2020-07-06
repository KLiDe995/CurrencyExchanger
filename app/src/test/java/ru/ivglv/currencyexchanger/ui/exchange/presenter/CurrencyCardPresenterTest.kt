package ru.ivglv.currencyexchanger.ui.exchange.presenter

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import ru.ivglv.currencyexchanger.TestHelper
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.model.ExchangeInput
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.CurrencyAccountView

class CurrencyCardPresenterTest {
    private lateinit var currencyCardPresenter: CurrencyCardPresenter
    private lateinit var repository: Repository
    private lateinit var currencyAccountView: CurrencyAccountView
    private val testedCurrencies = TestHelper.createListAccounts(2)

    @Before
    fun setUp() {
        val daggerTestComponent = DaggerPresenterTestComponent.create()

        currencyCardPresenter = daggerTestComponent.currencyCardPresenter()
        repository = daggerTestComponent.getRepositoryMock()
        currencyAccountView = daggerTestComponent.getCurrencyAccountViewMock()

        whenever(repository.getCurrencyCount()).thenReturn(Flowable.just(2))
        whenever(repository.getCurrencyList()).thenReturn(Flowable.just(testedCurrencies))
        whenever(currencyAccountView.updateCurrencies(testedCurrencies)).then {  }
        whenever(currencyAccountView.clearExchangeValueTextInput()).then {  }
    }

    @Test
    fun initCurrenciesObserver() {
        currencyCardPresenter.attachView(currencyAccountView)
        val compositeDisposableField = currencyCardPresenter.javaClass.superclass?.getDeclaredField("compositeDisposable")!!
        compositeDisposableField.isAccessible = true
        val compositeDisposable = compositeDisposableField.get(currencyCardPresenter) as CompositeDisposable

        verify(repository).getCurrencyCount()
        verify(repository).getCurrencyList()
        verify(currencyAccountView).updateCurrencies(testedCurrencies)
        compositeDisposable.dispose()
    }

    @Test
    fun exchangeValueInputChanged() {
        currencyCardPresenter.exchangeValueInputChanged("NewValuePut", ExchangeInput.CurrencyCardType.PUT)
        currencyCardPresenter.exchangeValueInputChanged("NewValueGet", ExchangeInput.CurrencyCardType.GET)
        ExchangeInput.putterValueObservable
            .test()
            .awaitCount(1)
            .assertValue("NewValuePut")

        ExchangeInput.getterValueObservable
            .test()
            .awaitCount(1)
            .assertValue("NewValueGet")
    }
}