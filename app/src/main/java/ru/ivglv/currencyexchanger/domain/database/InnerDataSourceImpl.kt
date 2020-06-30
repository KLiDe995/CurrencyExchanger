package ru.ivglv.currencyexchanger.domain.database

import hu.akarnokd.rxjava3.bridge.RxJavaBridge
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import ru.ivglv.currencyexchanger.domain.repository.datasource.InnerDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InnerDataSourceImpl @Inject constructor(
    private val currencyAccountDao: CurrencyAccountDao,
    private val exchangeRateDao: ExchangeRateDao
) : InnerDataSource {
    override fun addCurrencyToDataBase(currencyAccount: CurrencyAccount): Single<Long> =
        currencyAccountDao.insert(currencyAccount)
            .`as`(RxJavaBridge.toV3Single())

    override fun addCurrencyListToDataBase(currencyAccounts: List<CurrencyAccount>): Single<List<Long>> =
        currencyAccountDao.insert(currencyAccounts)
            .`as`(RxJavaBridge.toV3Single())

    override fun addExchangeRateToDataBase(exchangeRate: ExchangeRate): Single<Long> =
        exchangeRateDao.insert(exchangeRate)
            .`as`(RxJavaBridge.toV3Single())

    override fun updateCurrencyValue(currencyAccount: CurrencyAccount): Completable =
        currencyAccountDao.update(currencyAccount)
            .`as`(RxJavaBridge.toV3Completable())

    override fun updateExchangeRate(exchangeRate: ExchangeRate): Completable =
        exchangeRateDao.update(exchangeRate)
            .`as`(RxJavaBridge.toV3Completable())

    override fun getCurrenciesCount(): Flowable<Int> =
        currencyAccountDao.getCount().`as`(RxJavaBridge.toV3Flowable())

    override fun getCurrencyList(): Single<List<CurrencyAccount>> =
        currencyAccountDao.getAll()
            .`as`(RxJavaBridge.toV3Single())

    override fun getCurrencyByName(name: String): Flowable<CurrencyAccount> =
        currencyAccountDao.getByCurrencyName(name)
            .`as`(RxJavaBridge.toV3Flowable())

    override fun getCurrencyExchangeRate(currencyName: String, ratedName: String): Flowable<ExchangeRate> =
        exchangeRateDao.getByRatePair(currencyName, ratedName)
            .`as`(RxJavaBridge.toV3Flowable())
}