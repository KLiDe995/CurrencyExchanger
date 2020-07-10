package ru.ivglv.currencyexchanger.ui.exchange.presenter.view

import android.view.View
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip


interface ExchangerView : MvpView {
    @AddToEndSingle
    fun updateRateLabel(rateString: String)
    @Skip
    fun showExchangeImpossibleMessage()
    @AddToEndSingle
    fun updateButtonVisibility(visibility: Int)
}