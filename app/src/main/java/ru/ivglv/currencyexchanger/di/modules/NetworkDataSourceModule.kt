package ru.ivglv.currencyexchanger.di.modules

import dagger.Binds
import dagger.Module
import ru.ivglv.currencyexchanger.domain.repository.datasource.NetworkDataSource
import ru.ivglv.currencyexchanger.framework.network.NetworkDataSourceImpl
import javax.inject.Singleton

@Module
abstract class NetworkDataSourceModule {
    @Binds
    @Singleton
    abstract fun bindNetworkDataSource(networkDataSourceImpl: NetworkDataSourceImpl): NetworkDataSource
}