package ru.ivglv.currencyexchanger.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.ivglv.currencyexchanger.di.modules.*
import ru.ivglv.currencyexchanger.framework.network.BaseUrl
import ru.ivglv.currencyexchanger.ui.exchange.presenter.ExchangerPresenter
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppDatabaseModule::class,
    InnerDataSourceModule::class,
    NetworkModule::class,
    NetworkDataSourceModule::class,
    RepositoryModule::class,
    SchedulerModule::class
])
interface AppComponent {
    fun inject(exchangerPresenter: ExchangerPresenter)

    @Component.Builder
    interface Builder {
        @BindsInstance fun netApiBaseUrl(@BaseUrl baseUrl: String): Builder
        @BindsInstance fun context(context: Context): Builder
        fun build(): AppComponent
    }
}