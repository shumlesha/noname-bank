package com.example.patterns.DataLayer.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.patterns.DataLayer.DAO.AccountDao
import com.example.patterns.DataLayer.DAO.LoanDao
import com.example.patterns.DataLayer.DAO.UserDao
import com.example.patterns.DataLayer.Entity.Account
import com.example.patterns.DataLayer.Entity.Converters
import com.example.patterns.DataLayer.Entity.Loan
import com.example.patterns.DataLayer.Entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Database(entities = [Account::class, User::class, Loan::class], version = 3)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao
    abstract fun loanDao(): LoanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mybank_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}