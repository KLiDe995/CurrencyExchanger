package ru.ivglv.currencyexchanger.framework.network

import org.junit.Before
import org.junit.Test
import retrofit2.HttpException

import ru.ivglv.currencyexchanger.domain.model.ExchangeRate

class NetworkDataSourceImplTest {
    lateinit var api: ExchangeRateApiService
    lateinit var exchangeRateSourceImpl: NetworkDataSourceImpl

    @Before
    fun setUp() {
        api = DaggerNetworkServiceTestFactory.factory().create("https://api.exchangeratesapi.io").exchangeRateApi()
        exchangeRateSourceImpl = NetworkDataSourceImpl(api)
    }

    @Test
    fun getRatesForBase_returnsCorrectList() {
        val expectedBaseName = "USD"
        exchangeRateSourceImpl.getRatesForBase("USD")
            .test()
            .await()
            .assertNoErrors()
            .assertValue { it.isNotEmpty() && it.any { rate -> rate.currencyBase == expectedBaseName } }
    }

    @Test
    fun getRatesForBase_returnsEmptyList_whenGivenBadBaseName() {
        exchangeRateSourceImpl.getRatesForBase("badBase")
            .test()
            .await()
            .assertValue(listOf<ExchangeRate>())
    }
}