package com.example.chatapp.mainPage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.message.MessageActivity
import kotlinx.android.synthetic.main.item_chat_view.view.*

class MainPageAdapter(private val context: Context,
                      private val chatList: ArrayList<Chat>) : RecyclerView.Adapter<MainPageAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_view,
            parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int = chatList.size
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.chatName.text = chatList[position].chatId
        holder.chatLayout.setOnClickListener {
            val intent = Intent(it.context, MessageActivity::class.java)
            val bundle = Bundle()

            bundle.putString("chatId", chatList[holder.adapterPosition].chatId)
            intent.putExtras(bundle)
            it.context.startActivity(intent)
        }
    }


    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatName: TextView = itemView.chat_title
        val chatLayout: LinearLayout = itemView.chat_item__list
    }
}