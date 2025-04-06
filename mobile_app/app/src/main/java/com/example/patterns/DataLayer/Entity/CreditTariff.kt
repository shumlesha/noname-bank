package com.example.patterns.DataLayer.Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "credit_tariffs")
data class CreditTariff(
    @PrimaryKey val id: String,
    val name: String,
    val interestRateDaily: Double,
    val createdTimestamp: String
)