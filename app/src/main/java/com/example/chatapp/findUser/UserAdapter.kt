package com.example.chatapp.findUser

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_list_view.view.*

class UserAdapter(private val context: Context,
                  private val userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list_view, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = userList.size


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
       holder.userName.text = userList[position].userName
        holder.phoneNumber.text = userList[position].phoneNumber
        holder.listLayout.setOnClickListener {
            val key = FirebaseDatabase.getInstance().reference.child("chat").key
            // Id of user in db
            FirebaseDatabase.getInstance().reference.child("user")
                .child(FirebaseAuth.getInstance().uid.toString()).child("chat").child(key.toString())
                .setValue(true)
            // Id of user clicked on
            FirebaseDatabase.getInstance().reference.child("user")
                .child(userList[position].uid!!).child("chat").child(key.toString())
                .setValue(true)


        }
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.user_name
        val listLayout: LinearLayout = itemView.item_view_list
        val phoneNumber: TextView = itemView.user_phone_number


    }

}