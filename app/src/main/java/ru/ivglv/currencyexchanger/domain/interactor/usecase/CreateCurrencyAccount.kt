package ru.ivglv.currencyexchanger.domain.interactor.usecase

import io.reactivex.rxjava3.core.Single
import ru.ivglv.currencyexchanger.domain.interactor.Executable
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import javax.inject.Inject

class CreateCurrencyAccount @Inject constructor(private val repository: Repository) :
    Executable<Single<Long>> {
    var name: String = "Empty"
    var value: Float = 0f
    var symbol: Char = 'x'

    override fun execute(): Single<Long> = repository.addCurrency(CurrencyAccount(name, value, symbol))

}