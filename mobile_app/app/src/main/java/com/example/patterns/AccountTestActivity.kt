package com.example.patterns

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.IOException

class AccountTestActivity : AppCompatActivity() {

    private val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_test)

        val etClientId = findViewById<EditText>(R.id.etClientId)
        val etCurrency = findViewById<EditText>(R.id.etCurrency)
        val btnTest = findViewById<Button>(R.id.btnTest)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        btnTest.setOnClickListener {
            val clientId = etClientId.text.toString().trim()
            val currency = etCurrency.text.toString().trim()

            if (clientId.isEmpty() || currency.isEmpty()) {
                tvResult.text = "Введите clientId и currency"
                return@setOnClickListener
            }

            val json = JSONObject().apply {
                put("clientId", clientId)
                put("currency", currency)
            }

            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody = RequestBody.create(mediaType, json.toString())

            val request = Request.Builder()
                .url("http://10.0.2.2:8080/api/account/create")
                .post(requestBody)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        tvResult.text = "Ошибка: ${e.message}"
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            runOnUiThread {
                                tvResult.text = "Неожиданный ответ: $response"
                            }
                        } else {
                            val responseBody = response.body?.string()
                            runOnUiThread {
                                tvResult.text = responseBody
                            }
                        }
                    }
                }
            })
        }
    }
}
