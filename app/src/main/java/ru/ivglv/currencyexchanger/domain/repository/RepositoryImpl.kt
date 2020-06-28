package ru.ivglv.currencyexchanger.domain.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.toFlowable
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import ru.ivglv.currencyexchanger.domain.repository.datasource.NetworkDataSource
import ru.ivglv.currencyexchanger.domain.repository.datasource.InnerDataSource
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val innerDataSource: InnerDataSource,
    private val networkDataSource: NetworkDataSource
) : Repository {
    override fun addCurrency(name: String, value: Float): Single<Long> =
        innerDataSource.addCurrencyToDataBase(CurrencyAccount(name, value))
            /*.onErrorReturnItem(-1L)
            .subscribeOn(Schedulers.io())
            .flatMapPublisher { getExchangeRatesFromNetToDb(name) }
            .subscribe()*/

    override fun getCurrencyCount(): Flowable<Int> =
        innerDataSource.getCurrenciesCount()

    override fun getCurrencyList(): Single<List<CurrencyAccount>> =
        innerDataSource.getCurrencyList()

    override fun updateCurrencyValue(name: String, newValue: Float): Completable =
        innerDataSource.updateCurrencyValue(CurrencyAccount(name, newValue))

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