package ru.ivglv.currencyexchanger.di.modules

import dagger.Binds
import dagger.Module
import ru.ivglv.currencyexchanger.domain.database.InnerDataSourceImpl
import ru.ivglv.currencyexchanger.domain.repository.datasource.InnerDataSource
import javax.inject.Singleton

@Module
abstract class InnerDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindInnerDataSource(innerDataSourceImpl: InnerDataSourceImpl): InnerDataSource
}