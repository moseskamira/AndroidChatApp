package com.example.chatapp.mainPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.login.LogInActivity
import com.example.chatapp.R
import com.example.chatapp.findUser.FindUserActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main_page.*

class MainPageActivity : AppCompatActivity() {
    lateinit var logOutButton: Button
    lateinit var findUserButton: Button
    private var initialised: Boolean = false
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatLayoutManager: RecyclerView.LayoutManager
    private lateinit var chatAdapter: MainPageAdapter
    private var chatList: ArrayList<Chat> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)
        logOutButton = log_out_button

        findUserButton = find_user_button
        findUserButton.setOnClickListener {
            startActivity(Intent(applicationContext, FindUserActivity::class.java))
        }

        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(applicationContext, LogInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        getPermission()
        displayChatObject()
    }

    private fun displayChatObject() {
        val userChatDbReference = FirebaseDatabase.getInstance().getReference("user")
            .child(FirebaseAuth.getInstance().uid.toString()).child("chat")
        val query = userChatDbReference.orderByValue()
        query.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                if (dataSnapShot.exists()) {
                    createChatInstance(dataSnapShot)
                    }
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun createChatInstance(dataSnapShot: DataSnapshot) {
        for (childSnapShot in dataSnapShot.children) {
            val myChatId = childSnapShot.value.toString()
            val myChat = Chat(myChatId)
            if (chatList.isEmpty()) {
                chatList.add(myChat)
                initializeChatRecyclerView()
                chatAdapter.notifyDataSetChanged()
            }
            else
                for (chatIterator in chatList) {
                    if (chatIterator.chatId != myChat.chatId)
                        chatList.add(myChat)
                    initializeChatRecyclerView()
                    chatAdapter.notifyDataSetChanged()
                }
        }
    }

    private fun initializeChatRecyclerView() {
        chatRecyclerView = chat_recycler_view
        chatLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        chatRecyclerView.isNestedScrollingEnabled = false
        chatRecyclerView.setHasFixedSize(false)
        chatRecyclerView.layoutManager = chatLayoutManager
        chatAdapter = MainPageAdapter(applicationContext, chatList)
        chatRecyclerView.adapter = chatAdapter
    }

    private fun getPermission() {
        val permissionArrayList = arrayOf(android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.READ_CONTACTS)
        requestPermissions(permissionArrayList, 1)
    }
}
