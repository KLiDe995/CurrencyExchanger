package ru.ivglv.currencyexchanger.ui.exchange.presenter.view

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount


interface CurrencyAccountView : MvpView {
    @AddToEndSingle
    fun updateCurrencies(updatedCurrencies: List<CurrencyAccount>)
}