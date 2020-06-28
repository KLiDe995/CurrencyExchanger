package ru.ivglv.currencyexchanger.domain.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides

@Module
class AppDatabaseModule {
    @Provides
    fun provideAppDatabase(context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "database")
            .build()

    @Provides
    fun provideCurrencyAccountDao(appDatabase: AppDatabase) = appDatabase.currencyAccountDao()

    @Provides
    fun provideExchangeRateDao(appDatabase: AppDatabase) = appDatabase.exchangeRateDao()
}