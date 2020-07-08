package ru.ivglv.currencyexchanger.ui.exchange.presenter

import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.subjects.BehaviorSubject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.verification.VerificationMode
import ru.ivglv.currencyexchanger.TestHelper
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.model.ExchangeInput
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.CurrencyAccountView

class CurrencyCardPresenterTest {
    private lateinit var currencyCardPresenter: CurrencyCardPresenter
    private lateinit var repository: Repository
    private lateinit var currencyAccountView: CurrencyAccountView
    private val testedCurrencies = TestHelper.createListAccounts(2)
    private val testedRates = TestHelper.createListRates(2)

    @Before
    fun setUp() {
        val daggerTestComponent = DaggerPresenterTestComponent.create()

        currencyCardPresenter = daggerTestComponent.currencyCardPresenter()
        repository = daggerTestComponent.getRepositoryMock()
        currencyAccountView = daggerTestComponent.getCurrencyAccountViewMock()

        whenever(repository.getCurrencyCount())
            .thenReturn(Flowable.just(2))
        whenever(repository.getCurrencyList())
            .thenReturn(Flowable.just(testedCurrencies))
        whenever(currencyAccountView.updateCurrencies(testedCurrencies))
            .then {  }
        whenever(repository.getCurrencyExchangeRate(testedCurrencies[0].currencyName, testedCurrencies[1].currencyName))
            .thenReturn(Flowable.just(testedRates[0]))
        whenever(currencyAccountView.updateRecountedValueLabel(Pair(0f, 6.5625f)))
            .then {  }
    }

    @Test
    fun initCurrenciesObserver() {
        currencyCardPresenter.attachView(currencyAccountView)
        val compositeDisposableField = currencyCardPresenter.javaClass.superclass?.getDeclaredField("compositeDisposable")!!
            .also { it.isAccessible = true }
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

    @Test
    fun recountExchangeValueGet() {
        val testMethod = currencyCardPresenter.javaClass.getDeclaredMethod("recountExchangeValueGet", String::class.java, Float::class.java)
            .also { it.isAccessible = true }
        val expectedValue1 = 0f
        val actualValue1 = testMethod.invoke(currencyCardPresenter, "", 1.25f) as Float

        val expectedValue2 = 1.625f
        val actualValue2 = testMethod.invoke(currencyCardPresenter, "1.3", 1.25f) as Float

        val expectedValue3 = 1.625f
        val actualValue3 = testMethod.invoke(currencyCardPresenter, "1,3", 1.25f) as Float

        Assert.assertEquals(expectedValue1, actualValue1)
        Assert.assertEquals(expectedValue2, actualValue2)
        Assert.assertEquals(expectedValue3, actualValue3)
    }

    @Test
    fun recountExchangeValuePut() {
        val testMethod = currencyCardPresenter.javaClass.getDeclaredMethod("recountExchangeValuePut", String::class.java, Float::class.java)
            .also { it.isAccessible = true }
        val expectedValue1 = 0f
        val actualValue1 = testMethod.invoke(currencyCardPresenter, "", 1.25f) as Float

        val expectedValue2 = 1.04f
        val actualValue2 = testMethod.invoke(currencyCardPresenter, "1.3", 1.25f) as Float

        val expectedValue3 = 1.04f
        val actualValue3 = testMethod.invoke(currencyCardPresenter, "1,3", 1.25f) as Float

        Assert.assertEquals(expectedValue1, actualValue1)
        Assert.assertEquals(expectedValue2, actualValue2)
        Assert.assertEquals(expectedValue3, actualValue3)
    }

    @Test
    fun getExchangeRate() {
        val testMethod = currencyCardPresenter.javaClass.getDeclaredMethod("getExchangeRate", Pair::class.java)
            .also { it.isAccessible = true }
        (testMethod.invoke(currencyCardPresenter, Pair(testedCurrencies[0], testedCurrencies[1])) as Flowable<ExchangeRate>)
            .test()
            .awaitCount(1)
            .assertValue(testedRates[0])

        (testMethod.invoke(currencyCardPresenter, Pair(testedCurrencies[0], testedCurrencies[0])) as Flowable<ExchangeRate>)
            .test()
            .awaitCount(1)
            .assertValue(ExchangeRate(testedCurrencies[0].currencyName, testedCurrencies[0].currencyName, 1f))
    }

    @Test
    fun getRecountedExchangeValues() {
        val testMethod = currencyCardPresenter.javaClass.getDeclaredMethod("getRecountedExchangeValues", Pair::class.java)
            .also { it.isAccessible = true }
        testedRates[0].rate = 1.25f
        ExchangeInput.putterValue = "5.3"
        (testMethod.invoke(currencyCardPresenter, Pair(testedCurrencies[0], testedCurrencies[1])) as Flowable<Pair<Float, Float>>)
            .test()
            .awaitCount(1)
            .assertValue(Pair(0f, 6.625f))
    }

    @Test
    fun startExchangeValuesObservation() {
        ExchangeInput.putterCurrencyIndex = 0
        ExchangeInput.getterCurrencyIndex = 1
        testedRates[0].rate = 1.25f
        ExchangeInput.putterValue = "5.25"

        currencyCardPresenter.attachView(currencyAccountView)

        verify(currencyAccountView, times(4)).updateRecountedValueLabel(Pair(0f, 6.5625f))
    }
}