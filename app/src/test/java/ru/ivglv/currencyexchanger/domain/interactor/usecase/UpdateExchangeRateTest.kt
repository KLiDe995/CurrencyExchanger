package ru.ivglv.currencyexchanger.domain.interactor.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.rxjava3.core.Completable
import org.junit.Test

import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate
import java.lang.NullPointerException

class UpdateExchangeRateTest {

    @Test
    fun execute() {
        val testedExchangeRate = ExchangeRate("TestBase", "TestRated", 1f)
        val mock = mock<Repository> {
            on { updateExchangeRate(testedExchangeRate) } doReturn Completable.complete()
        }
        val updateExchangeRate =
            UpdateExchangeRate(
                mock
            )

        updateExchangeRate.apply { updatedExchangeRate = testedExchangeRate }
            .execute()
            .test()
            .assertComplete()
    }

    @Test
    fun execute_throws_whenInputNull() {
        val testedExchangeRate = ExchangeRate("TestBase", "TestRated", 1f)
        val mock = mock<Repository> {
            on { updateExchangeRate(testedExchangeRate) } doReturn Completable.complete()
        }
        val updateExchangeRate =
            UpdateExchangeRate(
                mock
            )

        updateExchangeRate
            .execute()
            .test()
            .assertError(NullPointerException::class.java)
    }
}