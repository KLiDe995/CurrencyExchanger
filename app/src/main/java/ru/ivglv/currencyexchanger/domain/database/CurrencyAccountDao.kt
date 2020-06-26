package ru.ivglv.currencyexchanger.domain.database

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface CurrencyAccountDao {
    @Query("SELECT * FROM currency_accounts")
    fun getAll(): Single<List<CurrencyAccountEntity>>
    @Query("SELECT COUNT(*) FROM currency_accounts")
    fun getCount(): Single<Int>
    @Query("SELECT * FROM currency_accounts WHERE currencyName = :name")
    fun getByCurrencyName(name: String): Single<CurrencyAccountEntity>
    @Insert
    fun insert(obj: CurrencyAccountEntity): Single<Long>
    @Insert
    fun insert(listObj: List<CurrencyAccountEntity>): Single<List<Long>>
    @Update
    fun update(obj: CurrencyAccountEntity): Completable
    @Delete
    fun delete(obj: CurrencyAccountEntity): Completable
    @Query("DELETE FROM currency_accounts")
    fun deleteAll(): Completable
}