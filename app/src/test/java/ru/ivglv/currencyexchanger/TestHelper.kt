package ru.ivglv.currencyexchanger

import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate

object TestHelper {
    fun createListRates(count: Int, prefix: String = ""): List<ExchangeRate> {
        val result = ArrayList<ExchangeRate>()
        for(i in 0 until count) {
            result.add(ExchangeRate("${prefix}TestBase$i", "${prefix}TestRated$i", i.toFloat()))
        }
        return result
    }

    fun createEmptyRate() = ExchangeRate("EmptyBase", "EmptyRate", 0f)

    fun createListAccounts(count: Int): List<CurrencyAccount> {
        val result = ArrayList<CurrencyAccount>()
        for(i in 0 until count) {
            result.add(CurrencyAccount("Test$i", i.toFloat(), 'x'))
        }
        return result
    }
    fun createEmptyAccount() = CurrencyAccount("Empty", 0f, 'x')
}