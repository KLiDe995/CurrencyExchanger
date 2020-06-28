package ru.ivglv.currencyexchanger.domain.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate

@Database(entities = [CurrencyAccount::class, ExchangeRate::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyAccountDao(): CurrencyAccountDao
    abstract fun exchangeRateDao(): ExchangeRateDao
}