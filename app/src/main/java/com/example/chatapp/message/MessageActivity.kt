package com.example.chatapp.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var messageAdapter: MessageAdapter
    private var messageList: ArrayList<Message> = ArrayList()
    lateinit var sendButton: Button
    lateinit var messageInput: EditText
    lateinit var chatId: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        initializeChatRecyclerView()
        messageInput = message_input_id
        chatId = intent!!.extras!!.getString("chatId")!!
        sendButton = send_id
        sendButton.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        if (messageInput.text.toString().isNotEmpty()) {
            val newMessageDb = FirebaseDatabase.getInstance().reference.child("chat").child("chatId").push()
            val newMessageMap = HashMap<String, Any>()
            newMessageMap["text"] = messageInput.text.toString()
            newMessageMap["creator"] = FirebaseAuth.getInstance().uid.toString()
            newMessageDb.updateChildren(newMessageMap)

        }
        messageInput.text = null
    }

    private fun initializeChatRecyclerView() {

        recyclerView = chat_recycler_view
        layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager =layoutManager

        messageAdapter = MessageAdapter(applicationContext, messageList)
        recyclerView.adapter = messageAdapter

    }




}
