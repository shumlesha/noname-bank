package com.example.patterns.DataLayer.Entity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.patterns.R

class EmployeeAccountAdapter(
    private var accounts: MutableList<Account>,
    private val onCloseClicked: (Account) -> Unit,
    private val onAccountClicked: (Account) -> Unit
) : RecyclerView.Adapter<EmployeeAccountAdapter.AccountViewHolder>() {

    inner class AccountViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAccountNumber: TextView = view.findViewById(R.id.tvAccountNumber1)
        val tvClient: TextView = view.findViewById(R.id.tvClient1)
        val tvBalance: TextView = view.findViewById(R.id.tvBalance1)
        val tvType: TextView = view.findViewById(R.id.tvType1)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus1)
        val btnDetails: Button = view.findViewById(R.id.btnDetails1)
        val btnCloseAccount: Button = view.findViewById(R.id.btnCloseAccount1)

        init {
            btnDetails.setOnClickListener {
                onAccountClicked(accounts[adapterPosition])
            }
            btnCloseAccount.setOnClickListener {
                onCloseClicked(accounts[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account_employee, parent, false)
        return AccountViewHolder(view)
    }

    override fun getItemCount() = accounts.size

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.tvAccountNumber.text = "Номер счета: ${account.number}"
        holder.tvClient.text = "Клиент: ${account.clientId}"
        holder.tvBalance.text = "Баланс: %.2f %s".format(account.balance, account.currency)

        val typeText = if (account.isCredit) "Кредитный" else "Дебетовый"
        holder.tvType.text = "Тип: $typeText"

        val statusText = if (account.closedTimestamp == null) "Активен" else "Закрыт"
        holder.tvStatus.text = "Статус: $statusText"
    }

    fun removeAccount(account: Account) {
        val index = accounts.indexOf(account)
        if (index != -1) {
            accounts.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    fun updateAccounts(newAccounts: List<Account>) {
        accounts.clear()
        accounts.addAll(newAccounts)
        notifyDataSetChanged()
    }
}
