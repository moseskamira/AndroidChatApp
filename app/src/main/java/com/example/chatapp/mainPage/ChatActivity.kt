package com.example.chatapp.mainPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.login.LogInActivity
import com.example.chatapp.R
import com.example.chatapp.music.MusicPlayerActivity
import com.example.chatapp.user.UserActivity
import com.example.chatapp.user.User
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    lateinit var audioPlayer: Button
    lateinit var logOutButton: Button
    lateinit var findUserButton: Button
    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var chatLayoutManager: RecyclerView.LayoutManager
    private lateinit var chatAdapter: ChatAdapter
    private var chatList: ArrayList<Chat> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        OneSignal.startInit(this).init()
        OneSignal.setSubscription(true)
        OneSignal.idsAvailable { userId, _ ->
            FirebaseDatabase.getInstance().reference.child("user").child(FirebaseAuth.getInstance()
                .uid.toString()).child("notificationKey").setValue(userId)
        }
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
        findUserButton = find_user_button
        findUserButton.setOnClickListener {
            startActivity(Intent(applicationContext, UserActivity::class.java))
        }
        audioPlayer = audio_player_button
        logOutButton = log_out_button
        audioPlayer.setOnClickListener {
            val playerIntent = Intent(this, MusicPlayerActivity::class.java)
            startActivity(playerIntent)
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
            .child(FirebaseAuth.getInstance().uid.toString()).child("chat")
        userChatDbReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                if (dataSnapShot.exists()) {
                    createChatInstance(dataSnapShot)
                    }
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented")
                //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun createChatInstance(dataSnapShot: DataSnapshot) {
        for (childSnapShot in dataSnapShot.children) {
            val chatId = childSnapShot.key.toString()
            val myChat = Chat(chatId)
            if (chatList.isEmpty()) {
                chatList.add(myChat)
                chatAdapter.notifyDataSetChanged()
            }
            else
                if (chatList.isNotEmpty()) {
                    for (chatIterator in chatList) {
                        if (chatIterator.chatId != myChat.chatId)
                            chatList.add(myChat)
                        getChatData(myChat.chatId)
                    }

                }
        }
    }

    private fun getChatData(chatId: String) {
        val newChatDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference.child("chat")
            .child(chatId).child("info")
        newChatDatabase.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented")
                //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                if (dataSnapShot.exists()) {
                    val myChatId: String
                    if (dataSnapShot.child("id").value != null) {
                        myChatId = dataSnapShot.child("id").value.toString()
                        for (userSnapShot in dataSnapShot.child("users").children) {
                            for (newChat in chatList) {
                                if (newChat.chatId == myChatId) {
                                    val  newUser = User(userSnapShot.key.toString())
                                    newChat.addUserToList(newUser)
                                    getUserData(newUser)
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    private fun getUserData(newUser: User) {
        val newUserDatabaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("user")
            .child(newUser.uid.toString())
        newUserDatabaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented")
                //To change body of created functions use File | Settings | File Templates.
            }
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                val user = User(dataSnapShot.key)
                if (dataSnapShot.child("notificationKey").value != null) {
                    user.notificationKey = dataSnapShot.child("notificationKey").value.toString()
                    for (mChat in chatList) {
                        for (mUserIterator: User in mChat.addUserToList(user)) {
                            if (mUserIterator.uid == user.notificationKey) {
                                mUserIterator.notificationKey = user.notificationKey
                            }
                        }
                    }

                }
                chatAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initializeChatRecyclerView() {
        chatRecyclerView = chat_recycler_view
        chatLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        chatRecyclerView.isNestedScrollingEnabled = false
        chatRecyclerView.setHasFixedSize(false)
        chatRecyclerView.layoutManager = chatLayoutManager
        chatAdapter = ChatAdapter(applicationContext, chatList)
        chatRecyclerView.adapter = chatAdapter
    }

    private fun getPermission() {
        val permissionArrayList = arrayOf(android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.READ_CONTACTS)
        requestPermissions(permissionArrayList, 1)
    }
}
