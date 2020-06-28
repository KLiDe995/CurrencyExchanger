package ru.ivglv.currencyexchanger.framework.network

data class ExchangeRateRequestResult(
    val base: String,
    val date: String,
    val rates: Map<String, Float>
) {
}