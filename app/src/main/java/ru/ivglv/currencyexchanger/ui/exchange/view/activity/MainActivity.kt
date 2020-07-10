package ru.ivglv.currencyexchanger.ui.exchange.view.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.ivglv.currencyexchanger.ExchangeApp
import ru.ivglv.currencyexchanger.R
import ru.ivglv.currencyexchanger.domain.model.ExchangeInput
import ru.ivglv.currencyexchanger.ui.exchange.presenter.ExchangerPresenter
import ru.ivglv.currencyexchanger.ui.exchange.presenter.view.ExchangerView
import ru.ivglv.currencyexchanger.ui.exchange.view.adapter.CurrencyPagerAdapter
import java.time.Duration

class MainActivity : MvpAppCompatActivity(), ExchangerView {
    lateinit var viewPagerTop: ViewPager
    lateinit var viewPagerBottom: ViewPager
    @InjectPresenter
    lateinit var exchangerPresenter: ExchangerPresenter
    @ProvidePresenter
    fun providePresenter() = ExchangeApp.appComponent.exchangerPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPagerTop = initViewPager(R.id.viewPagerTop, R.layout.card_currency_top, ExchangeInput.CurrencyCardType.PUT)
        viewPagerBottom = initViewPager(R.id.viewPagerBottom, R.layout.card_currency_bottom, ExchangeInput.CurrencyCardType.GET)
        initScrollListeners()
        initButtonListener()
    }

    private fun initViewPager(viewPagerId: Int, cardLayout: Int, cardType: ExchangeInput.CurrencyCardType): ViewPager {
        val adapter = CurrencyPagerAdapter(mvpDelegate, cardType, cardLayout)
        val viewPager = findViewById<ViewPager>(viewPagerId)
        viewPager.adapter = adapter
        return viewPager
    }

    fun initScrollListeners() {
        viewPagerTop.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                exchangerPresenter.currencyAccountSelectedIndexChanged(position, ExchangeInput.CurrencyCardType.PUT)
            }
            override fun onPageScrollStateChanged(state: Int) {
                // ignored
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // ignored
            }

        })
        viewPagerBottom.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                exchangerPresenter.currencyAccountSelectedIndexChanged(position, ExchangeInput.CurrencyCardType.GET)
            }
            override fun onPageScrollStateChanged(state: Int) {
                // ignored
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // ignored
            }
        })
    }

    override fun updateRateLabel(rateString: String) {
        val rateTextView = findViewById<TextView>(R.id.exchangeRateLabel)
        rateTextView.text = rateString
    }

    override fun showExchangeImpossibleMessage() {
        Toast
            .makeText(this, R.string.impossibleExchangeMessage, Toast.LENGTH_SHORT)
            .show()
    }

    fun initButtonListener() {
        val buttonExchange = findViewById<Button>(R.id.exchangeButton)
        buttonExchange.setOnClickListener { exchangerPresenter.exchangeButtonClicked() }
    }

    override fun updateButtonVisibility(visibility: Int) {
        val buttonExchange = findViewById<Button>(R.id.exchangeButton)
        buttonExchange.isEnabled = visibility == View.VISIBLE
    }
}
