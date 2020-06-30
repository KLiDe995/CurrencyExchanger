package ru.ivglv.currencyexchanger.domain.interactor.usecase

import io.reactivex.rxjava3.core.Completable
import ru.ivglv.currencyexchanger.domain.interactor.Executable
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import java.lang.NullPointerException
import javax.inject.Inject

class UpdateCurrencyValue @Inject constructor(private val repository: Repository) :
    Executable<Completable> {
    var updatedCurrencyAccount: CurrencyAccount? = null
    override fun execute(): Completable =
        updatedCurrencyAccount?.let {
            repository.updateCurrencyValue(updatedCurrencyAccount!!)
        } ?: Completable.fromAction { throw NullPointerException("CurrencyAccount is null") }
}