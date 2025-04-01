package com.example.patterns.DataLayer.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "loans")
data class Loan(
    @PrimaryKey val id: String,
    val accountId: String,
    val clientId: String,
    val amount: Double,
    val remainingAmount: Double,
    val interestRate: Double,
    val createdTimestamp: String,
    val closedTimestamp: String?
)
