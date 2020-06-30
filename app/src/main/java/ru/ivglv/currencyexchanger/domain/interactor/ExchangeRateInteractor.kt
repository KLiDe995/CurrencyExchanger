package ru.ivglv.currencyexchanger.domain.interactor

import ru.ivglv.currencyexchanger.domain.interactor.usecase.CreateExchangeRate
import ru.ivglv.currencyexchanger.domain.interactor.usecase.DownloadExchangeRates
import ru.ivglv.currencyexchanger.domain.interactor.usecase.GetExchangeRate
import ru.ivglv.currencyexchanger.domain.interactor.usecase.UpdateExchangeRate
import javax.inject.Inject

class ExchangeRateInteractor @Inject constructor(
    val createExchangeRate: CreateExchangeRate,
    val downloadExchangeRates: DownloadExchangeRates,
    val getExchangeRate: GetExchangeRate,
    val updateExchangeRate: UpdateExchangeRate
)