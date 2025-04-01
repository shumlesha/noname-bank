package com.example.patterns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.patterns.DataLayer.Entity.Payment
import com.example.patterns.DataLayer.Entity.PaymentHistoryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PaymentHistoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var paymentHistoryAdapter: PaymentHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_history)

        recyclerView = findViewById(R.id.recyclerViewPayments)
        paymentHistoryAdapter = PaymentHistoryAdapter(emptyList())
        recyclerView.apply {
            adapter = paymentHistoryAdapter
            layoutManager = LinearLayoutManager(this@PaymentHistoryActivity)
        }

        loadPaymentHistory()
    }

    private fun loadPaymentHistory() {
        CoroutineScope(Dispatchers.IO).launch {
            val payments = listOf(
                Payment("1", "2025-03-25 14:30", 1500.0, "Пополнение"),
                Payment("2", "2025-03-26 10:15", 500.0, "Снятие"),
                Payment("3", "2025-03-27 09:45", 250.0, "Перевод")
            )

            withContext(Dispatchers.Main) {
                paymentHistoryAdapter.updateData(payments)
            }
        }
    }
}