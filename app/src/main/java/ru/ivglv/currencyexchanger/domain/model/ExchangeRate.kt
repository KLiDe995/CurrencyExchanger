package ru.ivglv.currencyexchanger.domain.model

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "exchange_rates",
    primaryKeys = ["currencyBase", "currencyRated"],
    indices = [Index(
        value = ["currencyBase", "currencyRated"],
        name = "currency_pair",
        unique = true)])
data class ExchangeRate(
    var currencyBase: String,
    var currencyRated: String,
    var rate: Float
) {
}