package ru.ivglv.currencyexchanger.domain.interactor

import ru.ivglv.currencyexchanger.domain.interactor.usecase.*
import javax.inject.Inject

class CurrencyAccountInteractor @Inject constructor(
    val createCurrencyAccount: CreateCurrencyAccount,
    val createCurrencyAccountList: CreateCurrencyAccountList,
    val getCurrencyAccount: GetCurrencyAccount,
    val getCurrencyCount: GetCurrencyCount,
    val getCurrencyList: GetCurrencyList,
    val updateCurrencyValue: UpdateCurrencyValue
)