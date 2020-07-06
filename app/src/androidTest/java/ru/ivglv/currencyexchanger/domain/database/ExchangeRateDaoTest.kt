package ru.ivglv.currencyexchanger.domain.database

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate

class ExchangeRateDaoTest {

    lateinit var appDatabase: AppDatabase
    lateinit var exchangeRateDao: ExchangeRateDao

    @Before
    fun setUp() {
        appDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java)
            .build()
        exchangeRateDao = appDatabase.exchangeRateDao()

    }

    @After
    fun tearDown() {
        appDatabase.close()
    }

    @Test
    fun getAll() {
        val rates = ExchangeRateTestHelper.createListRates(3)
        exchangeRateDao.insert(rates)
            .flatMap { exchangeRateDao.getAll() }
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .assertNoErrors()
            .assertValue(rates)
    }

    @Test
    fun getCount() {
        val rates = ExchangeRateTestHelper.createListRates(3)
        exchangeRateDao.insert(rates)
            .flatMap { exchangeRateDao.getCount() }
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .assertNoErrors()
            .assertValue(3)
    }

    @Test
    fun getByCurrencyName() {
        val rates = ExchangeRateTestHelper.createListRates(3)
        exchangeRateDao.insert(rates)
            .flatMap { exchangeRateDao
                .getByRatePair("TestBase2", "TestRated2").firstOrError()
            }
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(ExchangeRate("TestBase2", "TestRated2", 2.0f))

    }

    @Test
    fun insert_one() {
        val rates = ExchangeRateTestHelper.createListRates(3)
        exchangeRateDao.insert(rates[1])
            .flatMap { exchangeRateDao
                .getByRatePair("TestBase1", "TestRated1").firstOrError()
            }
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(ExchangeRate("TestBase1", "TestRated1",1.0f))
    }

    @Test
    fun update() {
        val rates = ExchangeRateTestHelper.createListRates(3)
        exchangeRateDao.insert(rates)
            .flatMapCompletable { exchangeRateDao.update(rates[2].apply { rate = 2.2f }) }
            .andThen(exchangeRateDao
                .getByRatePair("TestBase2", "TestRated2")
                .firstOrError())
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(ExchangeRate("TestBase2", "TestRated2",2.2f))
    }

    @Test
    fun delete() {
        val rates = ExchangeRateTestHelper.createListRates(3)
        exchangeRateDao.insert(rates)
            .flatMapCompletable { exchangeRateDao.delete(rates[1]) }
            .andThen(exchangeRateDao.getAll())
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .assertNoErrors()
            .assertValue(rates.toMutableList().apply { removeAt(1) })
    }

    @Test
    fun deleteAll() {
        val rates = ExchangeRateTestHelper.createListRates(3)
        exchangeRateDao.insert(rates)
            .flatMapCompletable { exchangeRateDao.deleteAll() }
            .andThen(exchangeRateDao.getCount())
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .assertNoErrors()
            .assertValue(0)
    }

    private object ExchangeRateTestHelper {
        fun createListRates(count: Int): List<ExchangeRate> {
            val result = ArrayList<ExchangeRate>()
            for(i in 0 until count) {
                result.add(ExchangeRate("TestBase$i", "TestRated$i", i.toFloat()))
            }
            return result
        }

        fun createEmptyRate() = ExchangeRate("EmptyBase", "EmptyRate", 0f)
    }
}