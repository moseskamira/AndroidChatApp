package com.example.chatapp.message

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.stfalcon.frescoimageviewer.ImageViewer
import kotlinx.android.synthetic.main.item_message.view.*

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>)
    : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun getItemCount(): Int = messageList.size
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.chatMessage.text = messageList[position].message
        holder.sender.text = messageList[position].senderId
        if (messageList[holder.adapterPosition].imageUrlList.isEmpty()) {
            holder.myImageView.visibility = View.GONE
        }

        else {
            Glide.with(context).asBitmap().load(messageList[holder.adapterPosition].imageUrlList[0])
                .into(holder.myImageView)
            holder.myImageView.setOnClickListener { view ->
                ImageViewer.Builder(view!!.context, messageList[holder.adapterPosition].imageUrlList)
                    .setStartPosition(0)
                    .show()
            }
        }
    }


    inner class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val chatMessage: TextView = itemView.message
        val sender: TextView = itemView.sender_id
        val myImageView: ImageView = itemView.my_image
    }
}
