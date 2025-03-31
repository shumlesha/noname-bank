package com.example.patterns

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.patterns.DataLayer.Entity.Account

class AccountAdapter(
    private var accounts: MutableList<Account>,
    private val onCloseClicked: (Account) -> Unit,
    private val onAccountClicked: (Account) -> Unit
) : RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    inner class AccountViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val number: TextView = view.findViewById(R.id.tvAccountNumber)
        val balance: TextView = view.findViewById(R.id.tvBalance)
        val closeButton: Button = view.findViewById(R.id.btnCloseAccount)

        init {
            view.setOnClickListener {
                onAccountClicked(accounts[adapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_account, parent, false)
        return AccountViewHolder(view)
    }

    override fun getItemCount() = accounts.size

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.number.text = account.number
        holder.balance.text = "${account.balance} ${account.currency}"

        holder.closeButton.setOnClickListener {
            onCloseClicked(account)
        }
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
