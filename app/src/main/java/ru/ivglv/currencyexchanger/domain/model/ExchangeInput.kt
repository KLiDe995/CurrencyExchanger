package ru.ivglv.currencyexchanger.domain.model

import io.reactivex.rxjava3.subjects.BehaviorSubject

object ExchangeInput {
    var inputFocus: CurrencyCardType? = null
    var putterValue: String = ""
        set(value) {
            field = value
            putterValueObservable.onNext(value)
        }
    var getterValue: String = ""
        set(value) {
            field = value
            getterValueObservable.onNext(value)
        }
    var putterCurrencyIndex: Int = 0
        set(value) {
            field = value
            putterCurrencyIndexObservable.onNext(value)
        }
    var getterCurrencyIndex: Int = 0
        set(value) {
            field = value
            getterCurrencyIndexObservable.onNext(value)
        }

    val putterValueObservable = BehaviorSubject.createDefault(putterValue)
    val getterValueObservable = BehaviorSubject.createDefault(getterValue)
    val putterCurrencyIndexObservable = BehaviorSubject.createDefault(putterCurrencyIndex)
    val getterCurrencyIndexObservable = BehaviorSubject.createDefault(getterCurrencyIndex)

    enum class CurrencyCardType {
        PUT,
        GET
    }
}