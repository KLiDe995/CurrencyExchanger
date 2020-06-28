package ru.ivglv.currencyexchanger.framework.network

import io.reactivex.rxjava3.core.Single
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import ru.ivglv.currencyexchanger.domain.repository.datasource.NetworkDataSource

class NetworkDataSourceImpl(private val exchangeRateApiService: ExchangeRateApiService) :
    NetworkDataSource {
    override fun getRatesForBase(base: String): Single<List<ExchangeRate>> =
        exchangeRateApiService.getRates(base)
            .onErrorReturnItem(ExchangeRateRequestResult(base, "", mapOf()))
            .flatMap {
                val resultList = mutableListOf<ExchangeRate>()
                for(rate in it.rates) resultList.add(ExchangeRate(it.base, rate.key, rate.value))
                Single.just(resultList)
            }
}