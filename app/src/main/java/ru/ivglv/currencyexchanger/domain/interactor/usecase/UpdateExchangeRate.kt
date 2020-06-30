package ru.ivglv.currencyexchanger.domain.interactor.usecase

import io.reactivex.rxjava3.core.Completable
import ru.ivglv.currencyexchanger.domain.interactor.Executable
import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import java.lang.NullPointerException
import javax.inject.Inject

class UpdateExchangeRate @Inject constructor(private val repository: Repository) :
    Executable<Completable> {
    var updatedExchangeRate: ExchangeRate? = null
    override fun execute(): Completable =
        updatedExchangeRate?.let {
            repository.updateExchangeRate(updatedExchangeRate!!)
        } ?: Completable.fromAction { throw NullPointerException("ExchangeRate is null") }
}