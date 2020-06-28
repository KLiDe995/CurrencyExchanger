package ru.ivglv.currencyexchanger.framework.network

import dagger.Binds
import dagger.Module
import ru.ivglv.currencyexchanger.domain.repository.datasource.NetworkDataSource

@Module
abstract class NetworkDataSourceModule {
    @Binds
    abstract fun bindNetworkDataSource(networkDataSourceImpl: NetworkDataSourceImpl): NetworkDataSource
}