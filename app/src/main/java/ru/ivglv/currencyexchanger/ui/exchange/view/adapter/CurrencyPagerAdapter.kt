package ru.ivglv.currencyexchanger.ui.exchange.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.google.android.material.textfield.TextInputEditText
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.ivglv.currencyexchanger.ExchangeApp
import ru.ivglv.currencyexchanger.R
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import ru.ivglv.currencyexchanger.domain.model.ExchangeInput
import ru.ivglv.currencyexchanger.ui.exchange.presenter.CurrencyCardPresenter
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.CurrencyAccountView
import timber.log.Timber

class CurrencyPagerAdapter(
    private val parentDelegate: MvpDelegate<*>,
    private val cardType: ExchangeInput.CurrencyCardType,
    private val cardCurrencylayout: Int
) : MvpBasePagerAdapter(parentDelegate, cardType.toString()), CurrencyAccountView {

    @InjectPresenter
    lateinit var currencyCardPresenter: CurrencyCardPresenter
    @ProvidePresenter
    fun providePresenter(): CurrencyCardPresenter = ExchangeApp.appComponent.currencyCardPresenter()

    private var currencyAccountList = listOf<CurrencyAccount>()
    private var exchangeInputTextEditList = mutableMapOf<Int, TextInputEditText>()
    private var currencyValueLabelList = mutableMapOf<Int, TextView>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        Timber.d("Instantiating view for VIEW CARD $cardType")
        val view = LayoutInflater.from(container.context).inflate(cardCurrencylayout, container, false)
        exchangeInputTextEditList[position] = view.findViewById(R.id.exchangeValueInput)
        setLabels(view, position)
        initListeners(position)
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
        currencyValueLabelList[position] = view.findViewById(R.id.currencyValueLabel)
        currencyNameLabel.text = currencyAccountList[position].currencyName
        updateValueLabel()
    }

    private fun updateValueLabel() {
        for(i in 0 until currencyValueLabelList.count())
            currencyValueLabelList[i]?.text = String.format("${currencyAccountList[i].currencySymbol}%.2f", currencyAccountList[i].value)
    }

    private fun initListeners(position: Int) {
        exchangeInputTextEditList[position]?.doOnTextChanged {
                text, _, _, _ ->
            currencyCardPresenter.exchangeValueInputChanged(text.toString(), cardType)
        }
        exchangeInputTextEditList[position]?.onFocusChangeListener =
            View.OnFocusChangeListener { view, hasFocus ->
                if(hasFocus) {
                    currencyCardPresenter.exchangeValueInputChanged((view as TextInputEditText).text.toString(), cardType)
                    currencyCardPresenter.setInputFocus(cardType)
                }
            }
    }

    override fun updateCurrencies(updatedCurrencies: List<CurrencyAccount>) {
        currencyAccountList = updatedCurrencies
        notifyDataSetChanged()
        updateValueLabel()
    }

    override fun updateRecountedValueLabel(recountedValuePair: Pair<Float, Float>) {
        if(exchangeInputTextEditList.isNotEmpty() &&
            ExchangeInput.inputFocus != null &&
            ExchangeInput.inputFocus != cardType)
            when(cardType) {
                ExchangeInput.CurrencyCardType.PUT ->
                    exchangeInputTextEditList[ExchangeInput.putterCurrencyIndex]
                        ?.setText(
                            if(recountedValuePair.first == 0f) ""
                            else String.format("%.2f", recountedValuePair.first))
                ExchangeInput.CurrencyCardType.GET ->
                    exchangeInputTextEditList[ExchangeInput.getterCurrencyIndex]
                        ?.setText(
                            if(recountedValuePair.second == 0f) ""
                            else String.format("%.2f", recountedValuePair.second))
            }
    }
}