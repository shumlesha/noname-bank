package com.example.patterns

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.patterns.DataLayer.Database.AppDatabase
import com.example.patterns.DataLayer.Entity.Account
import com.example.patterns.DataLayer.Entity.Loan
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.UUID

class ClientMainActivity : AppCompatActivity() {

    private lateinit var accountDao: com.example.patterns.DataLayer.DAO.AccountDao
    private lateinit var accountAdapter: AccountAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var openAccountButton: Button

    private lateinit var tvCreditInfo: TextView
    private lateinit var btnRequestLoan: Button
    private lateinit var btnPaymentHistory: Button
    private lateinit var btnSettings: Button

    private lateinit var clientToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)

        updateCreditInfo()

        tvCreditInfo = findViewById(R.id.tvCreditInfo)
        btnRequestLoan = findViewById(R.id.btnRequestLoan)
        btnPaymentHistory = findViewById(R.id.btnPaymentHistory)
        btnSettings = findViewById(R.id.btnSettings)

        val prefs = getSharedPreferences("mybank_prefs", Context.MODE_PRIVATE)
        clientToken = prefs.getString("client_token", null) ?: run {
            finish()
            return
        }
        btnRequestLoan.setOnClickListener {
            val layout = LinearLayout(this)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(50, 40, 50, 10)

            val amountInput = EditText(this)
            amountInput.hint = "Сумма кредита"
            amountInput.inputType =
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            layout.addView(amountInput)

            val rateInput = EditText(this)
            rateInput.hint = "Процентная ставка"
            rateInput.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            layout.addView(rateInput)

            AlertDialog.Builder(this)
                .setTitle("Оформление кредита")
                .setView(layout)
                .setPositiveButton("Оформить") { _, _ ->
                    val amount = amountInput.text.toString().toDoubleOrNull()
                    val interestRate = rateInput.text.toString().toDoubleOrNull()
                    if (amount != null && interestRate != null && amount > 0 && interestRate > 0) {
                        requestLoan(amount, interestRate)
                    } else {
                        Toast.makeText(this, "Введите корректные значения", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
        btnPaymentHistory.setOnClickListener {
            startActivity(Intent(this, PaymentHistoryActivity::class.java))
        }
        btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }


        recyclerView = findViewById(R.id.recyclerViewAccounts)
        openAccountButton = findViewById(R.id.btnOpenAccount)
        accountDao = AppDatabase.getInstance(applicationContext).accountDao()

        accountAdapter = AccountAdapter(
            mutableListOf(),
            onCloseClicked = { account -> showCloseAccountConfirmation(account) },
            onAccountClicked = { account ->
                val intent = Intent(this, AccountOperationsActivity::class.java)
                intent.putExtra("account_id", account.id)
                startActivity(intent)
            }
        )
        recyclerView.apply {
            adapter = accountAdapter
            layoutManager = LinearLayoutManager(this@ClientMainActivity)
        }

        openAccountButton.setOnClickListener {
            openNewAccount()
        }

        loadAccounts()
    }


    private fun loadAccounts() {
        CoroutineScope(Dispatchers.IO).launch {
            val accounts = accountDao.getAccountsForClient(clientToken)
            withContext(Dispatchers.Main) {
                accountAdapter.updateAccounts(accounts)
            }
        }
    }

    private fun openNewAccount() {
        val newAccount = com.example.patterns.DataLayer.Entity.Account(
            id = UUID.randomUUID().toString(),
            creationTimestamp = LocalDateTime.now().toString(),
            blockedTimestamp = null,
            closedTimestamp = null,
            clientId = clientToken,
            number = "ACC-${(1000..9999).random()}",
            balance = 0.0,
            isCredit = false,
            currency = "RUB"
        )
        CoroutineScope(Dispatchers.IO).launch {
            accountDao.insertAccount(newAccount)
            loadAccounts()
        }
    }

    private fun showCloseAccountConfirmation(account: com.example.patterns.DataLayer.Entity.Account) {
        AlertDialog.Builder(this)
            .setTitle("Закрытие счета")
            .setMessage("Вы действительно хотите закрыть счет ${account.number}?")
            .setPositiveButton("Да") { _: DialogInterface, _: Int ->
                closeAccount(account)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun requestLoan(amount: Double, interestRate: Double) {
        val newLoan = Loan(
            id = UUID.randomUUID().toString(),
            accountId = "ACC-${(1000..9999).random()}",
            clientId = clientToken,
            amount = amount,
            remainingAmount = amount + (amount * interestRate / 100),
            interestRate = interestRate,
            createdTimestamp = LocalDateTime.now().toString(),
            closedTimestamp = null
        )

        CoroutineScope(Dispatchers.IO).launch {
            AppDatabase.getInstance(applicationContext).loanDao().insertLoan(newLoan)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@ClientMainActivity, "Кредит оформлен", Toast.LENGTH_SHORT)
                    .show()
                updateCreditInfo()
            }
        }

    }

    private fun updateCreditInfo() {
        CoroutineScope(Dispatchers.IO).launch {
            val loans = AppDatabase.getInstance(applicationContext).loanDao()
                .getActiveLoansForClient(clientToken)

            val creditText = if (loans.isNotEmpty()) {
                val totalRemaining = loans.sumOf { it.remainingAmount }
                "Активный кредит: ${"%.2f".format(totalRemaining)} RUB"
            } else {
                "Кредит: нет активного кредита"
            }

            withContext(Dispatchers.Main) {
                tvCreditInfo.text = creditText
            }
        }
    }

    private fun closeAccount(account: Account) {
        val closedAccount = account.copy(closedTimestamp = LocalDateTime.now().toString())

        CoroutineScope(Dispatchers.IO).launch {
            accountDao.updateAccount(closedAccount)
            withContext(Dispatchers.Main) {
                accountAdapter.removeAccount(account)
            }
        }
    }

}