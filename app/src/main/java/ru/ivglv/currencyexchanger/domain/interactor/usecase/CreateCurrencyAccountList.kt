package ru.ivglv.currencyexchanger.domain.interactor.usecase

import io.reactivex.rxjava3.core.Single
import ru.ivglv.currencyexchanger.domain.interactor.Executable
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import javax.inject.Inject

class CreateCurrencyAccountList @Inject constructor(private val repository: Repository) :
    Executable<Single<List<Long>>> {
    var currencies: List<Pair<String, Float>> = listOf()

    override fun execute(): Single<List<Long>> = repository.addCurrencyList(currencies.map { CurrencyAccount(it.first, it.second) })

}