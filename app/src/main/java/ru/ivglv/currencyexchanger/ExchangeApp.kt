package ru.ivglv.currencyexchanger

import android.app.Application
import ru.ivglv.currencyexchanger.di.AppComponent
import ru.ivglv.currencyexchanger.di.DaggerAppComponent
import timber.log.Timber

class ExchangeApp : Application() {
    companion object {
        lateinit var appComponent: AppComponent
    }
    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .netApiBaseUrl("https://api.exchangeratesapi.io")
            .context(this)
            .build()

        if(BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }
}