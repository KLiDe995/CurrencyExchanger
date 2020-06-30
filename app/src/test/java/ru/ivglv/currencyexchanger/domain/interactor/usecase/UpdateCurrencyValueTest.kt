package ru.ivglv.currencyexchanger.domain.interactor.usecase

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.rxjava3.core.Completable
import org.junit.Test

import ru.ivglv.currencyexchanger.domain.interactor.repository.Repository
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount
import java.lang.NullPointerException

class UpdateCurrencyValueTest {

    @Test
    fun execute() {
        val testedCurrency = CurrencyAccount("TestName", 1f)
        val mock = mock<Repository> {
            on { updateCurrencyValue(testedCurrency) } doReturn Completable.complete()
        }
        val updateCurrencyValue =
            UpdateCurrencyValue(
                mock
            )

        updateCurrencyValue.apply { updatedCurrencyAccount = testedCurrency }
            .execute()
            .test()
            .assertComplete()
    }

    @Test
    fun execute_throws_whenInputNull() {
        val testedCurrency = CurrencyAccount("TestName", 1f)
        val mock = mock<Repository> {
            on { updateCurrencyValue(testedCurrency) } doReturn Completable.complete()
        }
        val updateCurrencyValue =
            UpdateCurrencyValue(
                mock
            )

        updateCurrencyValue
            .execute()
            .test()
            .assertError(NullPointerException::class.java)
    }
}