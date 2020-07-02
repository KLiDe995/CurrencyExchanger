package ru.ivglv.currencyexchanger.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_accounts")
data class CurrencyAccount(
    @PrimaryKey
    var currencyName: String,
    var value: Float,
    var currencySymbol: Char
) {}