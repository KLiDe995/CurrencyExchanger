package ru.ivglv.currencyexchanger.ui.exchange.presenter

import dagger.Binds
import dagger.Module
import ru.ivglv.currencyexchanger.scheduler.BaseSchedulerProvider
import ru.ivglv.currencyexchanger.scheduler.TrampolineSchedulerProvider

@Module
abstract class TestSchedulerModule {
    @Binds
    abstract fun bindTestSchedulerProvider(trampolineSchedulerProvider: TrampolineSchedulerProvider): BaseSchedulerProvider
}