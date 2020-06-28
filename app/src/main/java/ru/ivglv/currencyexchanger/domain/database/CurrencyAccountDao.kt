package ru.ivglv.currencyexchanger.domain.database

import androidx.room.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import ru.ivglv.currencyexchanger.domain.model.CurrencyAccount

@Dao
interface CurrencyAccountDao {
    @Query("SELECT * FROM currency_accounts")
    fun getAll(): Single<List<CurrencyAccount>>
    @Query("SELECT COUNT(*) FROM currency_accounts")
    fun getCount(): Flowable<Int>
    @Query("SELECT * FROM currency_accounts WHERE currencyName = :name")
    fun getByCurrencyName(name: String): Flowable<CurrencyAccount>
    @Insert
    fun insert(obj: CurrencyAccount): Single<Long>
    @Insert
    fun insert(listObj: List<CurrencyAccount>): Single<List<Long>>
    @Update
    fun update(obj: CurrencyAccount): Completable
    @Delete
    fun delete(obj: CurrencyAccount): Completable
    @Query("DELETE FROM currency_accounts")
    fun deleteAll(): Completable
}