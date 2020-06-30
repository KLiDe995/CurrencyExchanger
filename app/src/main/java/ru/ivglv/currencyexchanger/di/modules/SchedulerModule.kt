package ru.ivglv.currencyexchanger.di.modules

import dagger.Binds
import dagger.Module
import ru.ivglv.currencyexchanger.scheduler.BaseSchedulerProvider
import ru.ivglv.currencyexchanger.scheduler.SchedulerProvider

@Module
abstract class SchedulerModule {
    @Binds
    abstract fun bindSchedulerProvider(schedulerProvider: SchedulerProvider): BaseSchedulerProvider
}