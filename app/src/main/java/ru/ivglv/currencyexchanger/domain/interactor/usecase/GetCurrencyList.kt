package ru.ivglv.currencyexchanger.domain.interactor.usecase

import io.reactivex.rxjava3.core.Single
import ru.ivglv.currencyexchanger.domain.interactor.Executable
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import javax.inject.Inject

class GetCurrencyList @Inject constructor(private val repository: Repository) :
    Executable<Single<List<CurrencyAccount>>> {
    override fun execute(): Single<List<CurrencyAccount>> = repository.getCurrencyList()
}