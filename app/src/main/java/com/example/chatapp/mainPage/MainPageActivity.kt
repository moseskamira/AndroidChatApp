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
import com.example.chatapp.utils.SendNotification
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.onesignal.OneSignal
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

        OneSignal.startInit(this).init()
        OneSignal.setSubscription(true)
        OneSignal.idsAvailable(object : OneSignal.IdsAvailableHandler {
            override fun idsAvailable(userId: String?, registrationId: String?) {
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance()
                    .uid.toString()).child("notificationKey").setValue(userId)
            }
        })
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
        SendNotification("Message One", "c42a392e-bf4d-4848-9c90-953bd5260a8a", "Heading One")



        logOutButton = log_out_button
        findUserButton = find_user_button
        findUserButton.setOnClickListener {
            startActivity(Intent(applicationContext, FindUserActivity::class.java))
        }

        logOutButton.setOnClickListener {
            OneSignal.setSubscription(false)
            FirebaseAuth.getInstance().signOut()
            Fresco.initialize(this)
            val intent = Intent(applicationContext, LogInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }
        getPermission()
        initializeChatRecyclerView()
        displayChatObject()
    }

    private fun displayChatObject() {
        val userChatDbReference = FirebaseDatabase.getInstance().getReference("user")
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
            val chatId = childSnapShot.child("chat").child("chatId").value.toString()
            val myChat = Chat(chatId)
            if (chatList.isEmpty()) {
                chatList.add(myChat)
//                chatAdapter.notifyDataSetChanged()
            }
            else
                if (chatList.isNotEmpty()) {
                    for (chatIterator in chatList) {
                        if (chatIterator.chatId != myChat.chatId)
                            chatList.add(myChat)
                        chatAdapter.notifyDataSetChanged()
                    }
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
