package ru.ivglv.currencyexchanger.ui.exchange.presenter

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.internal.subscriptions.ArrayCompositeSubscription
import io.reactivex.rxjava3.internal.subscriptions.SubscriptionArbiter
import org.jetbrains.annotations.NotNull
import org.reactivestreams.Subscription

open class BasePresenter<T : MvpView> : MvpPresenter<T>() {
    private val compositeDisposable = CompositeDisposable()

    protected fun unsibscribeOnDestroy(subscriber: Disposable) =
        compositeDisposable.add(subscriber)

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}