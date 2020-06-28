package ru.ivglv.currencyexchanger.domain.interactor

import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository

class GetExchangeRate(private val repository: Repository) :
    Executable<Flowable<ExchangeRate>>,
    Interactor {
    var baseName: String = "EmptyBase"
    var ratedName: String = "EmptyRate"

    override fun execute(): Flowable<ExchangeRate> = repository.getCurrencyExchangeRate(baseName, ratedName)
}