package com.example.patterns

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.patterns.DataLayer.Database.AppDatabase
import com.example.patterns.DataLayer.Entity.CreditTariff
import com.example.patterns.DataLayer.Entity.EmployeeAccountAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID

class EmployeeMainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var employeeAccountAdapter: EmployeeAccountAdapter
    private lateinit var accountDao: com.example.patterns.DataLayer.DAO.AccountDao
    private lateinit var creditTariffDao: com.example.patterns.DataLayer.DAO.CreditTariffDao
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var etTariffName: EditText
    private lateinit var etInterestRate: EditText
    private lateinit var btnCreateTariff: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_main)

        recyclerView = findViewById(R.id.recyclerViewAllAccounts)
        etTariffName = findViewById(R.id.etTariffName)
        etInterestRate = findViewById(R.id.etInterestRate)
        btnCreateTariff = findViewById(R.id.btnCreateTariff)

        val db = AppDatabase.getInstance(applicationContext)
        accountDao = db.accountDao()
        creditTariffDao = db.creditTariffDao()

        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Настраиваем обработку выбора пункта меню
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_accounts -> {
                    // По сути, мы уже на экране счетов — можно добавить логику, если нужно
                    true
                }
                R.id.nav_users -> {
                    // Переходим в UserManagementActivity
                    startActivity(Intent(this, UserManagementActivity::class.java))
                    true
                }
                else -> false
            }
        }

        employeeAccountAdapter = EmployeeAccountAdapter(
            mutableListOf(),
            onCloseClicked = { account ->
                CoroutineScope(Dispatchers.IO).launch {
                    val closedAccount = account.copy(closedTimestamp = LocalDateTime.now().toString())
                    accountDao.updateAccount(closedAccount)
                    loadAllAccounts()
                }
            },
            onAccountClicked = { account ->
                val intent = Intent(this, AccountOperationsActivity::class.java)
                intent.putExtra("account_id", account.id)
                startActivity(intent)
            }
        )

        recyclerView.apply {
            adapter = employeeAccountAdapter
            layoutManager = LinearLayoutManager(this@EmployeeMainActivity)
        }

        btnCreateTariff.setOnClickListener {
            createCreditTariff()
        }

        loadAllAccounts()
    }

    private fun loadAllAccounts() {
        CoroutineScope(Dispatchers.IO).launch {
            val accounts = accountDao.getAllActiveAccounts()
            withContext(Dispatchers.Main) {
                employeeAccountAdapter.updateAccounts(accounts)
            }
        }
    }

    private fun createCreditTariff() {
        val name = etTariffName.text.toString()
        val rate = etInterestRate.text.toString().toDoubleOrNull()

        if (name.isBlank() || rate == null || rate <= 0) {
            Toast.makeText(this, "Введите корректные данные тарифа", Toast.LENGTH_SHORT).show()
            return
        }

        val newTariff = CreditTariff(
            id = UUID.randomUUID().toString(),
            name = name,
            interestRateDaily = rate,
            createdTimestamp = LocalDateTime.now().toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            creditTariffDao.insertTariff(newTariff)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@EmployeeMainActivity, "Тариф \"$name\" создан", Toast.LENGTH_SHORT).show()
                etTariffName.text.clear()
                etInterestRate.text.clear()
            }
        }
    }
}