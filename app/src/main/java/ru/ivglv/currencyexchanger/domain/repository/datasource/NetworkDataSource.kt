package ru.ivglv.currencyexchanger.domain.repository.datasource

import io.reactivex.rxjava3.core.Single
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate

interface NetworkDataSource {
    fun getRatesForBase(base: String): Single<List<ExchangeRate>>
}