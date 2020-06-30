package ru.ivglv.currencyexchanger.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.ivglv.currencyexchanger.domain.database.AppDatabase
import javax.inject.Singleton

@Module
class AppDatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "database")
            .build()

    @Provides
    fun provideCurrencyAccountDao(appDatabase: AppDatabase) = appDatabase.currencyAccountDao()

    @Provides
    fun provideExchangeRateDao(appDatabase: AppDatabase) = appDatabase.exchangeRateDao()
}