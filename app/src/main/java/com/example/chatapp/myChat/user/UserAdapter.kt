package com.example.chatapp.myChat.user

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import kotlinx.android.synthetic.main.item_user_view.view.*

class UserAdapter(private val context: Context,
                  private val databaseUserList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val userView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_view, parent, false)
        val viewLayoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
            .LayoutParams.WRAP_CONTENT)
        userView.layoutParams = viewLayoutParams
        return UserViewHolder(userView)
    }
    override fun getItemCount(): Int = databaseUserList.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
       holder.userName.text = databaseUserList[position].userName
        holder.phoneNumber.text = databaseUserList[position].phoneNumber
        holder.checkBox.setOnCheckedChangeListener { _, isChecked -> databaseUserList[holder.adapterPosition]
            .selected = isChecked }
    }


    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.user_name
        val phoneNumber: TextView = itemView.user_phone_number
        val checkBox: CheckBox = itemView.add_to_chat
    }
}