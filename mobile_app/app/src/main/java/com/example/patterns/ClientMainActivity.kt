package com.example.patterns

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.patterns.DataLayer.Database.AppDatabase
import com.example.patterns.DataLayer.Entity.Account
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

    private lateinit var clientToken: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_client)

        val prefs = getSharedPreferences("mybank_prefs", Context.MODE_PRIVATE)
        clientToken = prefs.getString("client_token", null) ?: run {
            finish()
            return
        }

        recyclerView = findViewById(R.id.recyclerViewAccounts)
        openAccountButton = findViewById(R.id.btnOpenAccount)

        accountDao = AppDatabase.getInstance(applicationContext).accountDao()

        accountAdapter = AccountAdapter(mutableListOf()) { account ->
            showCloseAccountConfirmation(account)
        }
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