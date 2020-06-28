package ru.ivglv.currencyexchanger.domain.interactor

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import javax.inject.Inject

class GetCurrencyAccount @Inject constructor(private val repository: Repository) :
    Executable<Flowable<CurrencyAccount>>,
    Interactor {
    var requiredCurrencyName: String = "Empty"
    override fun execute(): Flowable<CurrencyAccount> = repository.getCurrencyAccount(requiredCurrencyName)
}