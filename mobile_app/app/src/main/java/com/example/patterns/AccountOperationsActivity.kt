package com.example.patterns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.patterns.DataLayer.Database.AppDatabase
import com.example.patterns.DataLayer.Entity.Account
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccountOperationsActivity : AppCompatActivity() {

    private lateinit var account: Account
    private lateinit var accountDao: com.example.patterns.DataLayer.DAO.AccountDao

    private lateinit var tvAccountNumber: TextView
    private lateinit var tvBalance: TextView
    private lateinit var btnDeposit: Button
    private lateinit var btnWithdraw: Button
    private lateinit var btnTransferOwn: Button
    private lateinit var btnTransferOther: Button
    private lateinit var btnConvert: Button
    private lateinit var etAmount: EditText
    private lateinit var etTargetCurrency: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_operations)

        accountDao = AppDatabase.getInstance(applicationContext).accountDao()

        tvAccountNumber = findViewById(R.id.tvAccountNumber)
        tvBalance = findViewById(R.id.tvBalance)
        btnDeposit = findViewById(R.id.btnDeposit)
        btnWithdraw = findViewById(R.id.btnWithdraw)
        btnTransferOwn = findViewById(R.id.btnTransferOwn)
        btnTransferOther = findViewById(R.id.btnTransferOther)
        btnConvert = findViewById(R.id.btnConvert)
        etAmount = findViewById(R.id.etAmount)
        etTargetCurrency = findViewById(R.id.etTargetCurrency)

        val accountId = intent.getStringExtra("account_id")
        if (accountId == null) {
            finish()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            account = accountDao.getAccountById(accountId) ?: return@launch
            withContext(Dispatchers.Main) {
                updateUI()
            }
        }

        btnDeposit.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull()
            if (amount != null) {
                depositMoney(amount)
            }
        }

        btnWithdraw.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull()
            if (amount != null) {
                withdrawMoney(amount)
            }
        }

        btnTransferOwn.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull()
            if (amount != null && amount > 0) {
                showAccountSelectionDialog(isOwnAccountsOnly = true, amount = amount)
            } else {
                Toast.makeText(this, "Введите корректную сумму", Toast.LENGTH_SHORT).show()
            }
        }

        btnTransferOther.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull()
            if (amount != null && amount > 0) {
                showAccountSelectionDialog(isOwnAccountsOnly = false, amount = amount)
            } else {
                Toast.makeText(this, "Введите корректную сумму", Toast.LENGTH_SHORT).show()
            }
        }

        btnConvert.setOnClickListener {
            val targetCurrency = etTargetCurrency.text.toString()
            if (targetCurrency.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val conversionRate = getConversionRate(account.currency, targetCurrency)
                    withContext(Dispatchers.Main) {
                        tvBalance.text = "Баланс: ${account.balance} ${account.currency} = ${account.balance * conversionRate} $targetCurrency"
                    }
                }
            }
        }
    }

    private fun updateUI() {
        tvAccountNumber.text = account.number
        tvBalance.text = "${account.balance} ${account.currency}"
    }
    private fun showAccountSelectionDialog(isOwnAccountsOnly: Boolean, amount: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            val accounts = if (isOwnAccountsOnly) {
                accountDao.getAccountsForClient(account.clientId).filter { it.id != account.id }
            } else {
                accountDao.getAllAccounts().filter { it.id != account.id }
            }

            val accountItems = accounts.map { "${it.number} (${it.balance} ${it.currency})" }.toTypedArray()

            withContext(Dispatchers.Main) {
                AlertDialog.Builder(this@AccountOperationsActivity)
                    .setTitle("Выберите счет для перевода")
                    .setItems(accountItems) { _, which ->
                        val selectedAccount = accounts[which]
                        transferFunds(account, selectedAccount, amount)
                        reloadAccount()
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            }
        }
    }

    private fun reloadAccount() {
        CoroutineScope(Dispatchers.IO).launch {
            account = accountDao.getAccountById(account.id) ?: account
            withContext(Dispatchers.Main) {
                updateUI()
            }
        }
    }

    private fun depositMoney(amount: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            account = account.copy(balance = account.balance + amount)
            accountDao.updateAccount(account)
            reloadAccount()
        }
    }

    private fun withdrawMoney(amount: Double) {
        CoroutineScope(Dispatchers.IO).launch {
            if (account.balance >= amount) {
                account = account.copy(balance = account.balance - amount)
                accountDao.updateAccount(account)
                reloadAccount()
            }
        }
    }

    private fun transferFunds(from: Account, to: Account, amount: Double) {
        val updatedFrom = from.copy(balance = from.balance - amount)
        val updatedTo = to.copy(balance = to.balance + amount)
        CoroutineScope(Dispatchers.IO).launch {
            accountDao.updateAccount(updatedFrom)
        }
        CoroutineScope(Dispatchers.IO).launch {
            accountDao.updateAccount(updatedTo)
        }
    }

    private suspend fun getConversionRate(fromCurrency: String, toCurrency: String): Double {
        return 1.1
    }
}