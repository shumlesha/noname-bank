package com.example.patterns

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)

        // Нажатие на кнопку "Войти"
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Простая проверка заполнения
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Пожалуйста, введите email и пароль", Toast.LENGTH_SHORT).show()
            } else {
                // Здесь можете реализовать свою логику авторизации
                // например, запрос на сервер или проверка в локальной БД

                Toast.makeText(this, "Вход выполнен", Toast.LENGTH_SHORT).show()

                // При успешном входе вы можете открыть другую Activity,
                // где будет отображаться информация о счетах, профиле и т.д.
                // startActivity(Intent(this, MainActivity::class.java))
                // finish()
            }
        }
    }
}