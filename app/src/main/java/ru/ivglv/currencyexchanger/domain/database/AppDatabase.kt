package ru.ivglv.currencyexchanger.domain.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CurrencyAccountEntity::class, ExchangeRateEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun currencyAccountDao(): CurrencyAccountDao
    abstract fun exchangeRateDao(): ExchangeRateDao
}