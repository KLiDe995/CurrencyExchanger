package ru.ivglv.currencyexchanger.domain.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import ru.ivglv.currencyexchanger.domain.repository.datasource.NetworkDataSource
import ru.ivglv.currencyexchanger.domain.repository.datasource.InnerDataSource
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryImpl @Inject constructor(
    private val innerDataSource: InnerDataSource,
    private val networkDataSource: NetworkDataSource
) : Repository {
    override fun addCurrency(currencyAccount: CurrencyAccount): Single<Long> =
        innerDataSource.addCurrencyToDataBase(currencyAccount)

    override fun addCurrencyList(currencyAccountList: List<CurrencyAccount>): Single<List<Long>> =
        innerDataSource.addCurrencyListToDataBase(currencyAccountList)

    override fun getCurrencyCount(): Flowable<Int> =
        innerDataSource.getCurrenciesCount()

    override fun getCurrencyList(): Single<List<CurrencyAccount>> =
        innerDataSource.getCurrencyList()

    override fun updateCurrencyValue(currencyAccount: CurrencyAccount): Completable =
        innerDataSource.updateCurrencyValue(currencyAccount)

    override fun getCurrencyAccount(currencyName: String): Flowable<CurrencyAccount> =
        innerDataSource.getCurrencyByName(currencyName)

    override fun getCurrencyExchangeRate(currencyName: String, ratedName: String): Flowable<ExchangeRate> =
        innerDataSource.getCurrencyExchangeRate(currencyName, ratedName)

    override fun downloadExchangeRatesInPeriod(currencyName: String, periodSec: Long): Flowable<List<ExchangeRate>> =
        networkDataSource.getRatesForBase(currencyName)
            .repeatWhen { it.delay(periodSec, TimeUnit.SECONDS) }
            /*.subscribeOn(Schedulers.io())
            .flatMap {
                it.toFlowable()
                    .subscribeOn(Schedulers.io())
                    .flatMapSingle { exchangeRate -> addOrUpdateExchangeRate(exchangeRate) }
            }*/

    override fun addExchangeRate(exchangeRate: ExchangeRate): Single<Long> =
        innerDataSource.addExchangeRateToDataBase(exchangeRate)

    override fun updateExchangeRate(exchangeRate: ExchangeRate): Completable =
        innerDataSource.updateExchangeRate(exchangeRate)
}