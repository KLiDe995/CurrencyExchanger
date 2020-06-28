package ru.ivglv.currencyexchanger.domain.interactor.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.Disposable
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate

interface Repository {
    fun addCurrency(name: String, value: Float): Single<Long>
    fun addExchangeRate(exchangeRate: ExchangeRate): Single<Long>
    fun updateCurrencyValue(name: String, newValue: Float): Completable
    fun updateExchangeRate(exchangeRate: ExchangeRate): Completable
    fun getCurrencyCount(): Flowable<Int>
    fun getCurrencyList(): Single<List<CurrencyAccount>>
    fun getCurrencyAccount(currencyName: String): Flowable<CurrencyAccount>
    fun getCurrencyExchangeRate(currencyName: String, ratedName: String): Flowable<ExchangeRate>
    fun downloadExchangeRatesInPeriod(currencyName: String, periodSec: Long): Flowable<List<ExchangeRate>>

}