package com.example.chatapp.myChat.mainPage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.musicPlayer.MusicPlayerActivity
import com.example.chatapp.song.MySongsActivity
import com.example.chatapp.myChat.user.UserActivity
import com.example.chatapp.myChat.user.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.onesignal.OneSignal
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {
    lateinit var audioPlayer: ImageButton
    lateinit var findUserButton: ImageButton
    lateinit var mySongsButton: Button
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
        audioPlayer.setOnClickListener {
            val playerIntent = Intent(this, MusicPlayerActivity::class.java)
            startActivity(playerIntent)
        }
        mySongsButton = songs_button
        mySongsButton.setOnClickListener {
            val songsIntent = Intent(this, MySongsActivity::class.java)
            startActivity(songsIntent)
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
        val newUserDatabaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
            .child("user")
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
