package ru.ivglv.currencyexchanger.domain.interactor.usecase

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import ru.ivglv.currencyexchanger.domain.interactor.Executable
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import javax.inject.Inject

class GetCurrencyList @Inject constructor(private val repository: Repository) :
    Executable<Flowable<List<CurrencyAccount>>> {
    override fun execute(): Flowable<List<CurrencyAccount>> = repository.getCurrencyList()
}