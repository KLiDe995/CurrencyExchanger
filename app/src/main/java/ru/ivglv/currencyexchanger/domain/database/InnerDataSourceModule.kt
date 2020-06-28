package ru.ivglv.currencyexchanger.domain.database

import dagger.Binds
import dagger.Module
import ru.ivglv.currencyexchanger.domain.repository.datasource.InnerDataSource

@Module
abstract class InnerDataSourceModule {
    @Binds
    abstract fun bindInnerDataSource(innerDataSourceImpl: InnerDataSourceImpl): InnerDataSource
}