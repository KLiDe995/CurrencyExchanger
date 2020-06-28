package ru.ivglv.currencyexchanger.domain.database

import dagger.Binds
import dagger.Module
import ru.ivglv.currencyexchanger.domain.repository.datasource.InnerDataSource

@Module
abstract class InnerDatabaseModule {
    @Binds
    abstract fun bindInnerDatabase(innerDatabaseImpl: InnerDataSourceImpl): InnerDataSource
}