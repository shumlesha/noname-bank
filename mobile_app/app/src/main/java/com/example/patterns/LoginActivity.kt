package com.example.patterns

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.patterns.DataLayer.Database.AppDatabase
import com.example.patterns.DataLayer.Entity.User
import com.example.patterns.DataLayer.Entity.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private val testUsers = listOf(
        User("client@example.com", "client123", UserRole.CLIENT),
        User("employee@example.com", "employee123", UserRole.EMPLOYEE)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailEditText = findViewById<EditText>(R.id.editTextEmail)
        val passwordEditText = findViewById<EditText>(R.id.editTextPassword)
        val loginButton = findViewById<Button>(R.id.buttonLogin)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = testUsers.find {
                it.email.equals(email, ignoreCase = true) && it.password == password
            }

            if (user != null) {
                val token = user.email

                val prefs = getSharedPreferences("mybank_prefs", Context.MODE_PRIVATE)
                prefs.edit().putString("client_token", token).apply()

                if (user.role == UserRole.CLIENT) {
                    startActivity(Intent(this, ClientMainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Доступно только для клиентов", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Неверный email или пароль", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
