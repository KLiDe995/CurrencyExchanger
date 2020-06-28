package ru.ivglv.currencyexchanger.domain.interactor

import io.reactivex.rxjava3.core.Completable
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import javax.inject.Inject

class CreateCurrencyAccount @Inject constructor(private val repository: Repository) :
    Executable<Completable>,
    Interactor {
    var name: String = "Empty"
    var value: Float = 0f

    override fun execute(): Completable = repository.addCurrency(name, value)
}