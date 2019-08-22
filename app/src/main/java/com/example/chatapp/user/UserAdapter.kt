package com.example.chatapp.user

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
                  private val userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_view, parent, false)
        return MyViewHolder(view)
    }
    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.userName.text = userList[position].userName
        holder.phoneNumber.text = userList[position].phoneNumber
        holder.checkBox.setOnCheckedChangeListener { buttonView, isChecked -> userList[holder.adapterPosition]
            .selected = isChecked }
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.add_to_chat
        val userName: TextView = itemView.user_name
        val phoneNumber: TextView = itemView.user_phone_number

    }
}