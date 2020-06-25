package ru.ivglv.currencyexchanger.domain.interactor

import io.reactivex.rxjava3.core.Observable
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import ru.ivglv.currencyexchanger.domain.port.Repository

class GetExchangeRate(private val repository: Repository) :
    Executable<ExchangeRate>,
    Interactor {
    lateinit var currencyName: String

    fun setCurrency(name: String) {
        currencyName = name
    }
    override fun execute(): Observable<ExchangeRate> = repository.getCurrencyExchangeRate(currencyName)
}