package ru.ivglv.currencyexchanger.domain.port

import io.reactivex.rxjava3.core.Observable
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate

interface ExchangeRateSource {
    fun getRatesForBase(base: String, symbols: ArrayList<String>): Observable<ExchangeRate>
}