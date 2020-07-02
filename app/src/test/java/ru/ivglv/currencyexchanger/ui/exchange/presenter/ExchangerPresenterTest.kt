package ru.ivglv.currencyexchanger.ui.exchange.presenter

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import org.junit.*

import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.interactor.usecase.*
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.reflect.Method

class ExchangerPresenterTest {
    private val exchangerPresenter = ExchangerPresenter()
    private lateinit var repository: Repository
    private val testedCurrencies = PresenterTestHelper.createListAccounts(2)
    private val testedRates1 = PresenterTestHelper.createListRates(3)
    private val testedRates2 = PresenterTestHelper.createListRates(3)

    @Before
    fun setUp() {
        val daggerTestComponent = DaggerExchangePresenterTestComponent.create()
        daggerTestComponent.inject(exchangerPresenter)
        repository = daggerTestComponent.getRepositoryMock()

        whenever(repository.getCurrencyCount())
            .thenReturn(Flowable.just(2))
        whenever(repository.getCurrencyList())
            .thenReturn(Single.just(testedCurrencies))
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
    }

    @After
    fun tearDown() {
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
        assertTrue(compositeDisposable.isDisposed)
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

    private object PresenterTestHelper {
        fun createListRates(count: Int): List<ExchangeRate> {
            val result = ArrayList<ExchangeRate>()
            for(i in 0 until count) {
                result.add(ExchangeRate("TestBase$i", "TestRated$i", i.toFloat()))
            }
            return result
        }

        fun createEmptyRate() = ExchangeRate("EmptyBase", "EmptyRate", 0f)

        fun createListAccounts(count: Int): List<CurrencyAccount> {
            val result = ArrayList<CurrencyAccount>()
            for(i in 0 until count) {
                result.add(CurrencyAccount("Test$i", i.toFloat()))
            }
            return result
        }
        fun createEmptyAccount() = CurrencyAccount("Empty", 0f)
    }
}