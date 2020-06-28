package ru.ivglv.currencyexchanger.domain.database

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.ivglv.currencyexchanger.domain.model.ExchangeRate

@Dao
interface ExchangeRateDao {
    @Query("SELECT * FROM exchange_rates")
    fun getAll(): Single<List<ExchangeRate>>
    @Query("SELECT COUNT(*) FROM exchange_rates")
    fun getCount(): Single<Int>
    @Query("SELECT * FROM exchange_rates WHERE currencyBase = :base AND currencyRated = :rated")
    fun getByRatePair(base: String, rated: String): Flowable<ExchangeRate>
    @Insert
    fun insert(obj: ExchangeRate): Single<Long>
    @Insert
    fun insert(listObj: List<ExchangeRate>): Single<List<Long>>
    @Update
    fun update(obj: ExchangeRate): Completable
    @Delete
    fun delete(obj: ExchangeRate): Completable
    @Query("DELETE FROM exchange_rates")
    fun deleteAll(): Completable
}