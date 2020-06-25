package ru.ivglv.currencyexchanger.domain.model

data class ExchangeRate(
    val base: String,
    val date: String,
    val rates: Map<String, Float>
) {
}