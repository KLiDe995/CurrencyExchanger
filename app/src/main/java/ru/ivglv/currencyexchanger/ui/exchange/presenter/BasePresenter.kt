package ru.ivglv.currencyexchanger.ui.exchange.presenter

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import moxy.MvpPresenter
import moxy.MvpView
import timber.log.Timber

open class BasePresenter<T : MvpView> : MvpPresenter<T>() {
    private val compositeDisposable = CompositeDisposable()

    protected fun unsibscribeOnDestroy(subscriber: Disposable) {
        compositeDisposable.add(subscriber)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        Timber.d("Composite Disposable cleared")
    }
}