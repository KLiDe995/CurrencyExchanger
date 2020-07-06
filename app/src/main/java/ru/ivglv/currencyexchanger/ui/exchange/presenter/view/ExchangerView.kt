package ru.ivglv.currencyexchanger.ui.exchange.presenter.view

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle


interface ExchangerView : MvpView {
    @AddToEndSingle
    fun updateRateLabel(rateString: String)
}