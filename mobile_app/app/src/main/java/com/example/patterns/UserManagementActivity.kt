package com.example.patterns

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.patterns.DataLayer.DAO.UserDao
import com.example.patterns.DataLayer.Database.AppDatabase
import com.example.patterns.DataLayer.Entity.User
import com.example.patterns.DataLayer.Entity.UserManagementAdapter
import com.example.patterns.DataLayer.Entity.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UserManagementActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etRole: EditText
    private lateinit var btnAddUser: Button

    private lateinit var userDao: UserDao
    private lateinit var userAdapter: UserManagementAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        recyclerView = findViewById(R.id.recyclerViewUsers1)
        etEmail = findViewById(R.id.etEmail1)
        etPassword = findViewById(R.id.etPassword1)
        etRole = findViewById(R.id.etRole1)
        btnAddUser = findViewById(R.id.btnAddUser1)

        userDao = AppDatabase.getInstance(applicationContext).userDao()

        userAdapter = UserManagementAdapter(mutableListOf(), onToggleBlockClicked = { user ->
            toggleUserBlock(user)
        })

        recyclerView.apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(this@UserManagementActivity)
        }

        btnAddUser.setOnClickListener {
            addUser()
        }

        loadUsers()
    }

    private fun loadUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            val users = userDao.getAllUsers()
            withContext(Dispatchers.Main) {
                userAdapter.updateUsers(users)
            }
        }
    }

    private fun addUser() {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val roleText = etRole.text.toString().trim().uppercase()

        if (email.isBlank() || password.isBlank() || roleText.isBlank()) {
            Toast.makeText(this, "Все поля обязательны", Toast.LENGTH_SHORT).show()
            return
        }

        val role = try {
            UserRole.valueOf(roleText)
        } catch (e: IllegalArgumentException) {
            Toast.makeText(this, "Роль должна быть CLIENT или EMPLOYEE", Toast.LENGTH_SHORT).show()
            return
        }

        val newUser = User(email, password, role, isBlocked = false)

        CoroutineScope(Dispatchers.IO).launch {
            userDao.insertUser(newUser)
            withContext(Dispatchers.Main) {
                Toast.makeText(this@UserManagementActivity, "Пользователь добавлен", Toast.LENGTH_SHORT).show()
                etEmail.text.clear()
                etPassword.text.clear()
                etRole.text.clear()
                loadUsers()
            }
        }
    }

    private fun toggleUserBlock(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            val updatedUser = user.copy(isBlocked = !user.isBlocked)
            userDao.updateUser(updatedUser)
            withContext(Dispatchers.Main) {
                loadUsers()
            }
        }
    }
}
