package ru.ivglv.currencyexchanger.domain.database

import android.database.sqlite.SQLiteConstraintException
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

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
            .flatMapPublisher { currencyAccountDao.getAll() }
            .`as`(RxJavaBridge.toV3Flowable())
            .firstOrError()
            .subscribeOn(Schedulers.trampoline())
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(accounts)
    }

    @Test
    fun getCount() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts)
            .flatMap { currencyAccountDao.getCount().firstOrError() }
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(3)
    }

    @Test
    fun getByCurrencyName() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts)
            .flatMap { currencyAccountDao.getByCurrencyName("Test2").firstOrError() }
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(accounts[2])

    }

    //@Test
    fun getByCurrencyName_timeouts_whenIncorrectNameGiven() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts)
            .flatMap { currencyAccountDao.getByCurrencyName("BadName").firstOrError() }
            .`as`(RxJavaBridge.toV3Single())
            .timeout(5, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.trampoline())
            .test()
            .awaitCount(1)
            .assertError(TimeoutException::class.java)
    }

    @Test
    fun insert() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts[1])
            .flatMap { currencyAccountDao.getByCurrencyName("Test1").firstOrError() }
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(accounts[1])
    }

    @Test
    fun insert_emptylist() {
        val accounts = listOf<CurrencyAccount>()
        currencyAccountDao.insert(accounts)
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .awaitCount(1)
            .assertNoErrors()
    }

    @Test
    fun insert_conflicts() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts[1])
            .flatMap { currencyAccountDao.insert(accounts[1]) }
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .assertError(SQLiteConstraintException::class.java)
    }

    @Test
    fun update() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts)
            .flatMapCompletable { currencyAccountDao.update(accounts[2].apply { value = 2.2f }) }
            .andThen(currencyAccountDao.getByCurrencyName("Test2").firstOrError())
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(CurrencyAccount("Test2", 2.2f, 'x'))
    }

    @Test
    fun update_doesNothing_whenIncorrectNameGiven() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts)
            .flatMapCompletable { currencyAccountDao.update(CurrencyAccount("BadName", 0f, 'x')) }
            .andThen(currencyAccountDao.getByCurrencyName("Test2").firstOrError())
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(accounts[2])
    }

    @Test
    fun delete() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts)
            .flatMapCompletable { currencyAccountDao.delete(accounts[1]) }
            .andThen(currencyAccountDao.getAll())
            .`as`(RxJavaBridge.toV3Flowable())
            .firstOrError()
            .subscribeOn(Schedulers.trampoline())
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(accounts.toMutableList().apply { removeAt(1) })
    }

    @Test
    fun deleteAll() {
        val accounts = CurrencyAccountTestHelper.createListAccounts(3)
        currencyAccountDao.insert(accounts)
            .flatMapCompletable { currencyAccountDao.deleteAll() }
            .andThen(currencyAccountDao.getCount().firstOrError())
            .`as`(RxJavaBridge.toV3Single())
            .subscribeOn(Schedulers.trampoline())
            .test()
            .awaitCount(1)
            .assertNoErrors()
            .assertValue(0)
    }

    private object CurrencyAccountTestHelper {
        fun createListAccounts(count: Int): List<CurrencyAccount> {
            val result = ArrayList<CurrencyAccount>()
            for(i in 0 until count) {
                result.add(CurrencyAccount("Test$i", i.toFloat(), 'x'))
            }
            return result
        }
        fun createEmptyAccount() = CurrencyAccount("Empty", 0f, 'x')
    }
}