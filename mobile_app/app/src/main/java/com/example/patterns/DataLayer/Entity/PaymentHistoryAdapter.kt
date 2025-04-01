package com.example.patterns.DataLayer.Entity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.patterns.R

class PaymentHistoryAdapter(private var payments: List<Payment>) :
    RecyclerView.Adapter<PaymentHistoryAdapter.PaymentViewHolder>() {

    inner class PaymentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPaymentDate: TextView = itemView.findViewById(R.id.tvPaymentDate)
        val tvPaymentAmount: TextView = itemView.findViewById(R.id.tvPaymentAmount)
        val tvPaymentDescription: TextView = itemView.findViewById(R.id.tvPaymentDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_history, parent, false)
        return PaymentViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentViewHolder, position: Int) {
        val payment = payments[position]
        holder.tvPaymentDate.text = payment.date
        holder.tvPaymentAmount.text = String.format("%.2f", payment.amount)
        holder.tvPaymentDescription.text = payment.description
    }

    override fun getItemCount(): Int = payments.size

    fun updateData(newPayments: List<Payment>) {
        payments = newPayments
        notifyDataSetChanged()
    }
}