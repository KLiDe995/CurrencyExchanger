package ru.ivglv.currencyexchanger.domain.port

import io.reactivex.rxjava3.core.Observable
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate

interface Repository {
    fun addCurrency(name: String, value: Float): Observable<Object>
    fun getCurrencyList(): Observable<List<CurrencyAccount>>
    fun getCurrencyAccount(): Observable<CurrencyAccount>
    fun getCurrencyExchangeRate(name: String): Observable<ExchangeRate>
}