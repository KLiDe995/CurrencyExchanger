package ru.ivglv.currencyexchanger.domain.interactor.usecase

import io.reactivex.rxjava3.core.Single
import ru.ivglv.currencyexchanger.domain.interactor.Executable
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import javax.inject.Inject

class CreateExchangeRate @Inject constructor(private val repository: Repository) :
    Executable<Single<Long>> {
    var exchangeRate: ExchangeRate? = null
    var currencyBaseName = "Empty"
    var currencyRatedName = "Empty"
    var rateValue = 0f
    override fun execute(): Single<Long> =
        repository.addExchangeRate(exchangeRate ?: ExchangeRate(currencyBaseName, currencyRatedName, rateValue))
}