package ru.ivglv.currencyexchanger.domain.interactor.usecase

import io.reactivex.rxjava3.core.Flowable
import ru.ivglv.currencyexchanger.domain.interactor.Executable
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import javax.inject.Inject

class GetCurrencyCount @Inject constructor(private val repository: Repository) :
    Executable<Flowable<Int>> {
    override fun execute(): Flowable<Int> = repository.getCurrencyCount()
}