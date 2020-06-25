package ru.ivglv.currencyexchanger.framework.network

import io.reactivex.rxjava3.core.Observable
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import ru.ivglv.currencyexchanger.domain.port.ExchangeRateSource

class ExchangeRateSourceImpl(private val exchangeRateApiService: ExchangeRateApiService) : ExchangeRateSource {
    override fun getRatesForBase(base: String, symbols: ArrayList<String>): Observable<ExchangeRate> =
        exchangeRateApiService.getRates(base, symbols.joinToString(","))
}