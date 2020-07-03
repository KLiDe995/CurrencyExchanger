package ru.ivglv.currencyexchanger.domain.interactor.usecase

import io.reactivex.rxjava3.core.Flowable
import ru.ivglv.currencyexchanger.domain.interactor.Executable
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import javax.inject.Inject

class GetCurrencyAccount @Inject constructor(private val repository: Repository) :
    Executable<Flowable<CurrencyAccount>> {
    var requiredCurrencyName: String = "Empty"
    override fun execute(): Flowable<CurrencyAccount> = repository.getCurrencyAccount(requiredCurrencyName)
}