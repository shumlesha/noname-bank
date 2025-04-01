package com.example.patterns.DataLayer.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.patterns.DataLayer.Entity.Loan

@Dao
interface LoanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: Loan)

    @Update
    suspend fun updateLoan(loan: Loan)

    @Query("SELECT * FROM loans WHERE clientId = :clientId AND closedTimestamp IS NULL")
    suspend fun getActiveLoansForClient(clientId: String): List<Loan>

    @Query("SELECT * FROM loans WHERE id = :loanId")
    suspend fun getLoanById(loanId: String): Loan?
}