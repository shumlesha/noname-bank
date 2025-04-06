package com.example.patterns.DataLayer.Entity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.patterns.R

class UserManagementAdapter(
    private val users: MutableList<User>,
    private val onToggleBlockClicked: (User) -> Unit
) : RecyclerView.Adapter<UserManagementAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUserEmail: TextView = itemView.findViewById(R.id.tvUserEmail)
        val tvUserRole: TextView = itemView.findViewById(R.id.tvUserRole)
        val tvUserBlocked: TextView = itemView.findViewById(R.id.tvUserBlocked)
        val btnBlockToggle: Button = itemView.findViewById(R.id.btnBlockToggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_management, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.tvUserEmail.text = user.email
        holder.tvUserRole.text = "Роль: ${user.role.name}"
        holder.tvUserBlocked.text = if (user.isBlocked) {
            "Статус: Заблокирован"
        } else {
            "Статус: Активен"
        }
        holder.btnBlockToggle.text = if (user.isBlocked) "Разблокировать" else "Заблокировать"

        holder.btnBlockToggle.setOnClickListener {
            onToggleBlockClicked(user)
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }
}