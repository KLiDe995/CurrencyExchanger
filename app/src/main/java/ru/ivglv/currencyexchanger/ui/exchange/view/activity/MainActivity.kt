package ru.ivglv.currencyexchanger.ui.exchange.view.activity

import android.os.Bundle
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.ivglv.currencyexchanger.ExchangeApp
import ru.ivglv.currencyexchanger.R
import ru.ivglv.currencyexchanger.ui.exchange.presenter.ExchangerPresenter
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.ExchangerView
import ru.ivglv.currencyexchanger.ui.exchange.view.adapter.CurrencyPagerAdapter

class MainActivity : MvpAppCompatActivity(), ExchangerView {

    @InjectPresenter
    lateinit var exchangerPresenter: ExchangerPresenter

    @ProvidePresenter
    fun providePresenter() = ExchangeApp.appComponent.exchangerPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapterTop = CurrencyPagerAdapter(mvpDelegate, 0.toString(), R.layout.card_currency_top)
        val viewPagerTop = findViewById<ViewPager>(R.id.viewPagerTop)
        viewPagerTop.adapter = adapterTop
        val adapterBottom = CurrencyPagerAdapter(mvpDelegate, 0.toString(), R.layout.card_currency_bottom)
        val viewPagerBottom = findViewById<ViewPager>(R.id.viewPagerBottom)
        viewPagerBottom.adapter = adapterBottom
    }
}
