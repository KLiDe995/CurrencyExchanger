package ru.ivglv.currencyexchanger.domain.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "exchange_rates",
    primaryKeys = ["currencyBase", "currencyRated"],
    indices = [Index(
        value = ["currencyBase", "currencyRated"],
        name = "currency_pair",
        unique = true)])
data class ExchangeRateEntity(
    var currencyBase: String,
    var currencyRated: String,
    var rate: Float
)