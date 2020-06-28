package ru.ivglv.currencyexchanger.domain.interactor

import io.reactivex.rxjava3.core.Observable

interface Executable<T> {
    fun execute(): T
}