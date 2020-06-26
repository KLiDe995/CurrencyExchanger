package ru.ivglv.currencyexchanger.domain.database

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface ExchangeRateDao {
    @Query("SELECT * FROM exchange_rates")
    fun getAll(): Flowable<List<ExchangeRateEntity>>
    @Query("SELECT COUNT(*) FROM exchange_rates")
    fun getCount(): Single<Int>
    @Query("SELECT * FROM exchange_rates WHERE currencyBase = :base AND currencyRated = :rated")
    fun getByRatePair(base: String, rated: String): ExchangeRateEntity
    @Insert
    fun insert(obj: ExchangeRateEntity): Completable
    @Insert
    fun insert(listObj: List<ExchangeRateEntity>): Single<List<Long>>
    @Update
    fun update(obj: ExchangeRateEntity): Completable
    @Delete
    fun delete(obj: ExchangeRateEntity): Completable
    @Query("DELETE FROM currency_accounts")
    fun deleteAll(): Completable
}