package com.example.chatapp.myChat.message

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import com.google.android.material.snackbar.Snackbar
import com.stfalcon.frescoimageviewer.ImageViewer
import kotlinx.android.synthetic.main.item_message.view.*



class MessageAdapter(private val messageActivity:MessageActivity, private val messageList: ArrayList<Message>)
    : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private lateinit var messageToDelete: Message
    private var messageToDeletePosition: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val messageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        val messageLayoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup
            .LayoutParams.WRAP_CONTENT)
        messageView.layoutParams = messageLayoutParams
        return MessageViewHolder(messageView)
    }

    override fun getItemCount(): Int = messageList.size
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.chatMessage.text = messageList[position].message
        holder.sender.text = messageList[position].senderId
        when {
            messageList[holder.adapterPosition].imageUrlList.isEmpty() -> holder.myImageView.visibility = View.GONE
            else -> {
                Glide.with(messageActivity.applicationContext).asBitmap().load(messageList[holder.adapterPosition]
                    .imageUrlList[0])
                    .into(holder.myImageView)
                holder.myImageView.setOnClickListener { view ->
                    ImageViewer.Builder(view!!.context, messageList[holder.adapterPosition].imageUrlList)
                        .setStartPosition(0)
                        .show()
                }
            }
        }

    }


    inner class MessageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val chatMessage: TextView = itemView.message
        val sender: TextView = itemView.sender_id
        val myImageView: ImageView = itemView.my_image
    }

    fun deleteItemFromRecyclerView(position: Int) {
        when {
            messageList.isNotEmpty() -> {
                messageToDeletePosition = position
                messageToDelete = messageList[messageToDeletePosition]
                messageList.remove(messageToDelete)
                notifyItemRemoved(messageToDeletePosition)
                displayUndoSnackBar()
            }
        }
    }

    private fun displayUndoSnackBar() {
        val snackBar = Snackbar.make(messageActivity.findViewById(R.id.message_coordinator),
            "Message Deleted", Snackbar.LENGTH_LONG)
        snackBar.setAction("UnDo") {
            unDoDelete()
        }
        snackBar.show()
    }

    private fun unDoDelete() {
        messageList.add(messageToDeletePosition, messageToDelete)
        notifyItemInserted(messageToDeletePosition)
    }
}
