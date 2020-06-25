package ru.ivglv.currencyexchanger.domain.interactor

import io.reactivex.rxjava3.core.Observable
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.port.Repository
import javax.inject.Inject

class GetCurrencyList @Inject constructor(private val repository: Repository) :
    Executable<List<CurrencyAccount>>,
    Interactor {
    override fun execute(): Observable<List<CurrencyAccount>> = repository.getCurrencyList()
}