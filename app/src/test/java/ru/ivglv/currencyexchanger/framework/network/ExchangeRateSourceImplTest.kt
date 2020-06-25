package ru.ivglv.currencyexchanger.framework.network

import android.database.Observable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.functions.Predicate
import org.junit.Before
import org.junit.Test

import ru.ivglv.currencyexchanger.domain.model.ExchangeRate

class ExchangeRateSourceImplTest {
    lateinit var api: ExchangeRateApiService
    lateinit var exchangeRateSourceImpl: ExchangeRateSourceImpl

    @Before
    fun setUp() {
        api = DaggerNetworkServiceFactory.factory().create("https://api.exchangeratesapi.io").exchangeRateApi()
        exchangeRateSourceImpl = ExchangeRateSourceImpl(api)
    }

    @Test
    fun getRatesForBase_returnsCorrectObject() {
        val expectedRate = ExchangeRate("USD",
            "any",
            mapOf("EUR" to 0f, "GBP" to 0f)
        )
        exchangeRateSourceImpl.getRatesForBase("USD", arrayListOf("EUR", "GBP"))
            .test()
            .await()
            .assertValue { t ->
                expectedRate.base == t.base && expectedRate.rates.keys == t.rates.keys
            }
    }

    @Test
    fun getRatesForBase_fails() {
        val expectedRate = ExchangeRate("USD",
            "any",
            mapOf("EUR1" to 0f, "GBP" to 0f)
        )
        exchangeRateSourceImpl.getRatesForBase("USD", arrayListOf("EUR", "GBP"))
            .test()
            .await()
            .assertValue { t ->
                expectedRate.base == t.base && expectedRate.rates.keys != t.rates.keys
            }
    }
}