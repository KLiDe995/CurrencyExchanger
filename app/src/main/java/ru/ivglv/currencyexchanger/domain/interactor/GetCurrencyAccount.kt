package ru.ivglv.currencyexchanger.domain.interactor

import io.reactivex.rxjava3.core.Observable
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.port.Repository
import javax.inject.Inject

class GetCurrencyAccount @Inject constructor(private val repository: Repository) :
    Executable<CurrencyAccount>,
    Interactor {
    override fun execute(): Observable<CurrencyAccount> = repository.getCurrencyAccount()
}