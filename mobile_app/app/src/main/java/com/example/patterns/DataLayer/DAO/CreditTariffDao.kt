package com.example.patterns.DataLayer.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.patterns.DataLayer.Entity.Account
import com.example.patterns.DataLayer.Entity.CreditTariff

@Dao
interface CreditTariffDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTariff(tariff: CreditTariff)

    @Query("SELECT * FROM credit_tariffs")
    suspend fun getAllTariffs(): List<CreditTariff>

    @Query("SELECT * FROM accounts WHERE closedTimestamp IS NULL")
    suspend fun getAllActiveAccounts(): List<Account>
}