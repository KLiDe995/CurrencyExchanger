package ru.ivglv.currencyexchanger.domain.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount

@Entity(tableName = "currency_accounts")
data class CurrencyAccountEntity(
    @PrimaryKey
    var currencyName: String,
    var value: Float
)