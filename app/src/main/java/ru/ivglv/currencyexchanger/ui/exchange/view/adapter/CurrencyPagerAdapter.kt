package ru.ivglv.currencyexchanger.ui.exchange.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.ivglv.currencyexchanger.ExchangeApp
import ru.ivglv.currencyexchanger.R
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.ui.exchange.presenter.CurrencyCardPresenter
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.CurrencyAccountView
import kotlin.text.*

class CurrencyPagerAdapter(
    private val parentDelegate: MvpDelegate<*>,
    private val childId: String,
    private val cardCurrencylayout: Int
) : MvpBasePagerAdapter(parentDelegate, childId), CurrencyAccountView {
    @InjectPresenter
    lateinit var currencyCardPresenter: CurrencyCardPresenter
    private var currencyAccountList = listOf<CurrencyAccount>()

    @ProvidePresenter
    fun providePresenter(): CurrencyCardPresenter = ExchangeApp.appComponent.currencyCardPresenter()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(container.context).inflate(cardCurrencylayout, container, false)
        setLabels(view, position)
        container.addView(view)
        return view
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean =
        view == `object`

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeViewAt(position)
    }

    override fun getCount(): Int =
        currencyAccountList.count()

    private fun setLabels(view: View, position: Int) {
        val currencyNameLabel = view.findViewById<TextView>(R.id.currencyNameLabel)
        val currencyValueLabel = view.findViewById<TextView>(R.id.currencyValueLabel)
        currencyNameLabel.text = currencyAccountList[position].currencyName
        currencyValueLabel.text =
            String.format("${currencyAccountList[position].currencySymbol}%.2f", currencyAccountList[position].value)
    }

    override fun updateCurrencies(updatedCurrencies: List<CurrencyAccount>) {
        currencyAccountList = updatedCurrencies
        notifyDataSetChanged()
    }
}