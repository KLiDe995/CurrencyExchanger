package ru.ivglv.currencyexchanger.ui.exchange.presenter

import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import org.junit.*

import org.junit.Assert.*
import ru.ivglv.currencyexchanger.TestHelper
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import java.lang.Exception

class ExchangerPresenterTest {
    private lateinit var exchangerPresenter: ExchangerPresenter
    private lateinit var repository: Repository
    private val testedCurrencies = TestHelper.createListAccounts(2)
    private val testedRates1 = TestHelper.createListRates(3, "First")
    private val testedRates2 = TestHelper.createListRates(3, "Second")
    private val standartCurrencies = listOf(
        CurrencyAccount("USD", 100f, '$'),
        CurrencyAccount("EUR", 100f, '€'),
        CurrencyAccount("GBP", 100f, '£')
    )

    @Before
    fun setUp() {
        val daggerTestComponent = DaggerPresenterTestComponent.create()
        exchangerPresenter = daggerTestComponent.exchangerPresenter()
        repository = daggerTestComponent.getRepositoryMock()

        whenever(repository.getCurrencyCount())
            .thenReturn(Flowable.just(2))
        whenever(repository.getCurrencyList())
            .thenReturn(Flowable.just(testedCurrencies))
        whenever(repository.downloadExchangeRatesInPeriod(testedCurrencies[0].currencyName, 30))
            .thenReturn(Flowable.just(testedRates1))
        whenever(repository.downloadExchangeRatesInPeriod(testedCurrencies[1].currencyName, 30))
            .thenReturn(Flowable.just(testedRates2))
        whenever(repository.addExchangeRate(testedRates1[0]))
            .thenReturn(Single.just(1))
        whenever(repository.addExchangeRate(testedRates1[1]))
            .thenReturn(Single.just(2))
        whenever(repository.addExchangeRate(testedRates1[2]))
            .thenReturn(Single.just(3))
        whenever(repository.addExchangeRate(testedRates2[0]))
            .thenReturn(Single.error(Exception()))
        whenever(repository.addExchangeRate(testedRates2[1]))
            .thenReturn(Single.error(Exception()))
        whenever(repository.addExchangeRate(testedRates2[2]))
            .thenReturn(Single.error(Exception()))
        whenever(repository.updateExchangeRate(testedRates2[0]))
            .thenReturn(Completable.complete())
        whenever(repository.updateExchangeRate(testedRates2[1]))
            .thenReturn(Completable.complete())
        whenever(repository.updateExchangeRate(testedRates2[2]))
            .thenReturn(Completable.complete())
        whenever(repository.addCurrencyList(standartCurrencies))
            .thenReturn(Single.just(listOf(1L, 2L, 3L)))
    }

    @After
    fun tearDown() {
    }

    @Test
    fun initCurrencies_doNothing_whenCurrenciesExists() {
        val testMethod = exchangerPresenter.javaClass.getDeclaredMethod("initCurrencies")
        testMethod.isAccessible = true
        val disposable = testMethod.invoke(exchangerPresenter) as Disposable

        verify(repository, never()).addCurrencyList(standartCurrencies)
        Thread.sleep(500)
        assertTrue(disposable.isDisposed)
    }

    @Test
    fun initCurrencies_addedStandartCurrencoes_whenCurrenciesEmpty() {
        whenever(repository.getCurrencyCount()).thenReturn(Flowable.just(0))

        val testMethod = exchangerPresenter.javaClass.getDeclaredMethod("initCurrencies")
        testMethod.isAccessible = true
        val disposable = testMethod.invoke(exchangerPresenter) as Disposable

        verify(repository).addCurrencyList(standartCurrencies)
        Thread.sleep(500)
        assertTrue(disposable.isDisposed)
    }

    @Test
    fun addStarterCurrencies() {
        val testMethod = exchangerPresenter.javaClass.getDeclaredMethod("addStarterCurrencies")
        testMethod.isAccessible = true
        val disposable = testMethod.invoke(exchangerPresenter) as Disposable

        verify(repository).addCurrencyList(standartCurrencies)
        Thread.sleep(500)
        assertTrue(disposable.isDisposed)
    }

    @Test
    fun startRatesUpdate() {
        val testMethod = exchangerPresenter.javaClass.getDeclaredMethod("startRatesUpdate")
        val compositeDisposableField = exchangerPresenter.javaClass.superclass?.getDeclaredField("compositeDisposable")!!
        testMethod.isAccessible = true
        compositeDisposableField.isAccessible = true
        testMethod.invoke(exchangerPresenter)
        val compositeDisposable = compositeDisposableField.get(exchangerPresenter) as CompositeDisposable

        verify(repository).getCurrencyCount()
        verify(repository).getCurrencyList()
        verify(repository).addExchangeRate(testedRates1[2])
        verify(repository).addExchangeRate(testedRates2[2])
        verify(repository).updateExchangeRate(testedRates2[2])

        Thread.sleep(500)
        assertTrue(compositeDisposable.size() > 0)
        compositeDisposable.dispose()
    }

    @Test
    fun updateRatesForCurrency() {
        val testMethod = exchangerPresenter.javaClass.getDeclaredMethod("updateRatesForCurrency", CurrencyAccount::class.java)
        testMethod.isAccessible = true
        val disposable = testMethod.invoke(exchangerPresenter, testedCurrencies[0]) as Disposable

        verify(repository).downloadExchangeRatesInPeriod(testedCurrencies[0].currencyName, 30)
        verify(repository).addExchangeRate(testedRates1[0])
        verify(repository).addExchangeRate(testedRates1[1])
        verify(repository).addExchangeRate(testedRates1[2])
        Thread.sleep(500)
        assertTrue(disposable.isDisposed)
    }

    @Test
    fun addOrUpdateExchangeRates_createsRate() {
        val testMethod = exchangerPresenter.javaClass.getDeclaredMethod("addOrUpdateExchangeRates", List::class.java)
        testMethod.isAccessible = true
        val disposable = testMethod.invoke(exchangerPresenter, testedRates1) as Disposable

        verify(repository).addExchangeRate(testedRates1[0])
        verify(repository).addExchangeRate(testedRates1[1])
        verify(repository).addExchangeRate(testedRates1[2])
        Thread.sleep(500)
        assertTrue(disposable.isDisposed)
    }

    @Test
    fun addOrUpdateExchangeRates_updatesRate() {
        val testMethod = exchangerPresenter.javaClass.getDeclaredMethod("addOrUpdateExchangeRates", List::class.java)
        testMethod.isAccessible = true
        val disposable = testMethod.invoke(exchangerPresenter, testedRates2) as Disposable

        verify(repository).updateExchangeRate(testedRates2[0])
        verify(repository).updateExchangeRate(testedRates2[1])
        verify(repository).updateExchangeRate(testedRates2[2])
        Thread.sleep(500)
        assertTrue(disposable.isDisposed)
    }
}