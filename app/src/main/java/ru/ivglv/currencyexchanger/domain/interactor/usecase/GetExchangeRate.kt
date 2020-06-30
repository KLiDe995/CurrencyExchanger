package ru.ivglv.currencyexchanger.domain.interactor.usecase

import io.reactivex.rxjava3.core.Flowable
import ru.ivglv.currencyexchanger.domain.interactor.Executable
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import javax.inject.Inject

class GetExchangeRate @Inject constructor(private val repository: Repository) :
    Executable<Flowable<ExchangeRate>> {
    var baseName: String = "EmptyBase"
    var ratedName: String = "EmptyRate"

    override fun execute(): Flowable<ExchangeRate> = repository.getCurrencyExchangeRate(baseName, ratedName)
}