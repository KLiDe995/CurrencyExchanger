package ru.ivglv.currencyexchanger.domain.database

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrencyAccountDaoTest {

    lateinit var appDatabase: AppDatabase
    lateinit var currencyAccountDao: CurrencyAccountDao

    @Before
    fun setUp() {
        appDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java)
            .build()
        currencyAccountDao = appDatabase.currencyAccountDao()

    }

    @After
    fun tearDown() {
        appDatabase.close()
    }

    @Test
    fun getAll() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts)
            .flatMap { currencyAccountDao.getAll() }
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .assertNoErrors()
            .assertValue(accounts)
    }

    @Test
    fun getCount() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts)
            .flatMap { currencyAccountDao.getCount() }
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .assertNoErrors()
            .assertValue(3)
    }

    @Test
    fun getByCurrencyName() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts)
            .flatMap { currencyAccountDao.getByCurrencyName("Test2") }
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .assertNoErrors()
            .assertValue(CurrencyAccountEntity("Test2", 2.0f))

    }

    @Test
    fun insert_one() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts[1])
            .flatMap { currencyAccountDao.getByCurrencyName("Test1") }
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .assertNoErrors()
            .assertValue(CurrencyAccountEntity("Test1", 1.0f))
    }

    @Test
    fun update() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts)
            .flatMapCompletable { currencyAccountDao.update(accounts[2].apply { value = 2.2f }) }
            .andThen(currencyAccountDao.getByCurrencyName("Test2"))
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .assertNoErrors()
            .assertValue(CurrencyAccountEntity("Test2", 2.2f))
    }

    @Test
    fun delete() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts)
            .flatMapCompletable { currencyAccountDao.delete(accounts[1]) }
            .andThen(currencyAccountDao.getAll())
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .assertNoErrors()
            .assertValue(accounts.toMutableList().apply { removeAt(1) })
    }

    @Test
    fun deleteAll() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts)
            .flatMapCompletable { currencyAccountDao.deleteAll() }
            .andThen(currencyAccountDao.getCount())
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .assertNoErrors()
            .assertValue(0)
    }

    private object CurrencyAccountTestHelper {
        fun createListAccounts(count: Int): List<CurrencyAccountEntity> {
            val result = ArrayList<CurrencyAccountEntity>()
            for(i in 0 until count) {
                result.add(CurrencyAccountEntity("Test$i", i.toFloat()))
            }
            return result
        }
    }
}