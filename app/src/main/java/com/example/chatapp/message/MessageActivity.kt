package com.example.chatapp.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_message.*

class MessageActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var messageAdapter: MessageAdapter
    private var messageList: ArrayList<Message> = ArrayList()
    lateinit var sendButton: Button
    lateinit var messageInput: EditText
    private lateinit var chatId: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        messageInput = message_input_id
        chatId = intent!!.extras!!.getString("chatId")!!
        sendButton = send_id
        sendButton.setOnClickListener {
            sendMessage()
        }
        initializeChatRecyclerView()
        getChatMessages()
    }

    private fun sendMessage() {
        if (messageInput.text.toString().isNotEmpty()) {
            val newMessageDb = FirebaseDatabase.getInstance().reference.child("chat")
                .child("chatId").push()
            val newMessageMap = HashMap<String, Any>()
            newMessageMap["text"] = messageInput.text.toString()
            newMessageMap["creator"] = FirebaseAuth.getInstance().uid.toString()
            newMessageDb.updateChildren(newMessageMap)

        }
        messageInput.text = null
    }

        private fun getChatMessages() {
            val chatDbReference = FirebaseDatabase.getInstance().reference.child("chat")
                .child("chatId")
            chatDbReference.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(databaseSnapShot: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(databaseSnapShot: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(databaseSnapShot: DataSnapshot, p1: String?) {
                if (databaseSnapShot.exists()) {
                    var displayedChatMessage = ""
                    var displayedSenderId = ""
                    if (databaseSnapShot.child("text").value != null) {
                        displayedChatMessage = databaseSnapShot.child("text").value.toString()
                    }

                    if (databaseSnapShot.child("creator").value != null) {
                        displayedSenderId = databaseSnapShot.child("creator").value.toString()
                    }

                    val chatMessage = Message(databaseSnapShot.key!!, displayedSenderId, displayedChatMessage)
                    messageList.add(chatMessage)
                    layoutManager.scrollToPosition(messageList.size -1)
                    messageAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
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
