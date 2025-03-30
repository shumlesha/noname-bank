package com.example.patterns.DataLayer.Entity

import androidx.room.TypeConverter

enum class UserRole {
    CLIENT,
    EMPLOYEE
}

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole): String {
        return role.name
    }

    @TypeConverter
    fun toUserRole(value: String): UserRole {
        return UserRole.valueOf(value)
    }
}
