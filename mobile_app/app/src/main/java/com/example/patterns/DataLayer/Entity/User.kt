package com.example.patterns.DataLayer.Entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val password: String,
    val role: UserRole,
    val isBlocked: Boolean = false
)