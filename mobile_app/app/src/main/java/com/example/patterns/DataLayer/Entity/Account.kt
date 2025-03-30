package com.example.patterns.DataLayer.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "accounts")
data class Account(
    @PrimaryKey val id: String,
    val creationTimestamp: String,
    val blockedTimestamp: String?,
    val closedTimestamp: String?,
    val clientId: String,
    val number: String,
    val balance: Double,
    val isCredit: Boolean,
    val currency: String
)