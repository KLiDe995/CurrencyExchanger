package ru.ivglv.currencyexchanger.domain.interactor.usecase

import io.reactivex.rxjava3.core.Flowable
import ru.ivglv.currencyexchanger.domain.interactor.Executable
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import javax.inject.Inject

class DownloadExchangeRates @Inject constructor(private val repository: Repository) :
    Executable<Flowable<List<ExchangeRate>>> {
    var currencyBaseName = "Empty"
    var periodInSec = 30L
    override fun execute(): Flowable<List<ExchangeRate>> = repository.downloadExchangeRatesInPeriod(currencyBaseName, periodInSec)
}