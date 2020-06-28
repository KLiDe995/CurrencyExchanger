package ru.ivglv.currencyexchanger.framework.network

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate

interface ExchangeRateApiService {

    @GET("/latest")
    fun getRates(@Query("base")base: String): Single<ExchangeRateRequestResult>
}