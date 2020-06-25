package ru.ivglv.currencyexchanger.domain.interactor

import io.reactivex.rxjava3.core.Observable
import ru.ivglv.currencyexchanger.domain.port.Repository
import javax.inject.Inject

class CreateCurrencyAccount @Inject constructor(private val repository: Repository) :
    Executable<Object>,
    Interactor {
    private var name: String = "Empty"
    private var value: Float = 0f

    fun setCurrencyName(name: String): CreateCurrencyAccount {
        this.name = name
        return this
    }

    fun setCurrencyValue(value: Float): CreateCurrencyAccount {
        this.value = value
        return this
    }

    override fun execute(): Observable<Object> = repository.addCurrency(name, value)
}