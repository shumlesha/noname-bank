package com.example.patterns.DataLayer.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.patterns.DataLayer.Entity.Account

@Dao
interface AccountDao {
    @Query("SELECT * FROM accounts WHERE clientId = :clientId AND closedTimestamp IS NULL")
    suspend fun getAccountsForClient(clientId: String): List<Account>

    @Query("SELECT * FROM accounts WHERE id = :accountId")
    suspend fun getAccountById(accountId: String): Account?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: Account)

    @Update
    suspend fun updateAccount(account: Account)

    @Query("SELECT * FROM accounts WHERE closedTimestamp IS NULL")
    suspend fun getAllAccounts(): List<Account>

}