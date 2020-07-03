package ru.ivglv.currencyexchanger.ui.exchange.view.adapter

import androidx.viewpager.widget.PagerAdapter
import moxy.MvpDelegate

abstract class MvpBasePagerAdapter(
    private val parentDelegate: MvpDelegate<*>,
    private val childId: String
) : PagerAdapter(){
    private var mMvpDelegate: MvpDelegate<out MvpBasePagerAdapter>? = null
    init {
        getMvpDelegate().onCreate()
    }

    fun getMvpDelegate() =
        mMvpDelegate ?: let {
            mMvpDelegate = MvpDelegate(this).also { it.setParentDelegate(parentDelegate, childId) }
            mMvpDelegate!!
        }
}