package com.example.patterns.DataLayer.Entity

import androidx.room.Entity

@Entity(tableName = "payments")
data class Payment(
    val id: String,
    val date: String,
    val amount: Double,
    val description: String
)
