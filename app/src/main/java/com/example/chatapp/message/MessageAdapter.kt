package com.example.chatapp.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import kotlinx.android.synthetic.main.item_message.view.*

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int = messageList.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.chatMessage.text = messageList[position].message
        holder.sender.text = messageList[position].senderId
    }

    inner class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val chatMessage: TextView = itemView.message
        val sender: TextView = itemView.sender_id



    }

}
