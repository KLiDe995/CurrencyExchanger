package ru.ivglv.currencyexchanger.domain.repository.datasource

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate

interface InnerDataSource {
    fun addCurrencyToDataBase(currencyAccount: CurrencyAccount): Single<Long>
    fun addExchangeRateToDataBase(exchangeRate: ExchangeRate): Single<Long>
    fun updateCurrencyValue(currencyAccount: CurrencyAccount): Completable
    fun updateExchangeRate(exchangeRate: ExchangeRate): Completable
    fun getCurrenciesCount(): Flowable<Int>
    fun getCurrencyList(): Single<List<CurrencyAccount>>
    fun getCurrencyByName(name: String): Flowable<CurrencyAccount>
    fun getCurrencyExchangeRate(currencyName: String, ratedName: String): Flowable<ExchangeRate>
}