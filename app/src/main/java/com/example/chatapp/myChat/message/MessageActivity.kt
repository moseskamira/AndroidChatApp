package com.example.chatapp.myChat.message

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.myChat.mainPage.Chat
import com.example.chatapp.myChat.user.User
import com.example.chatapp.utils.SendNotification
import com.facebook.drawee.backends.pipeline.Fresco
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_message.*

@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MessageActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    lateinit var imageRecyclerView: RecyclerView
    lateinit var imageLayoutManager: RecyclerView.LayoutManager
    lateinit var imageAdapter: ImageAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var messageAdapter: MessageAdapter
    private var messageList: ArrayList<Message> = ArrayList()
    lateinit var sendButton: Button
    lateinit var mediaButton: Button
    lateinit var messageInput: EditText
    lateinit var messageDatabase: DatabaseReference
    private lateinit var myChatObject: Chat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        Fresco.initialize(this)
        messageInput = message_input_id
        myChatObject = intent.getSerializableExtra("chatObject") as Chat
        sendButton = send_id
        mediaButton = add_media_button
        sendButton.setOnClickListener {
            sendMessage()
        }
        mediaButton.setOnClickListener {
            openMediaGallery()
        }
        initializeChatRecyclerView()
        initializeImageRecyclerView()
        getChatMessages()
    }

    private val imageIdList: ArrayList<String> = ArrayList()
    var totalImagesUploaded: Int = 0

    private fun sendMessage() {
        messageDatabase = FirebaseDatabase.getInstance().reference.child("chat").child(myChatObject.chatId)
            .child("messages")
        val messageId = messageDatabase.push().key
        val newMessageMap = HashMap<String, Any>()
        newMessageMap["creator"] = FirebaseAuth.getInstance().uid.toString()

            if (messageInput.text.toString().isNotEmpty()) {
                newMessageMap["text"] = messageInput.text.toString()
            }
            if (mediaUriList.isNotEmpty()) {
                for (mediaUri in mediaUriList) {
                    val imageId = messageDatabase.child("media").push().key
                    imageIdList.add(imageId!!)
                    if (messageId != null) {
                        val filePath: StorageReference = FirebaseStorage.getInstance().reference
                            .child("chat").child("chatId").child(messageId).child(imageId)
                        val uploadTask: UploadTask = filePath.putFile(Uri.parse(mediaUri))
                        uploadTask.addOnSuccessListener {
                            filePath.downloadUrl.addOnSuccessListener { uri ->
                                newMessageMap["media/${imageIdList[totalImagesUploaded] + "/"}"] = uri.toString()
                                totalImagesUploaded++
                                if (totalImagesUploaded == mediaUriList.size) {
                                    updateDatabaseWithNewMessage(messageDatabase, newMessageMap)
                                }
                            }
                        }
                    }
                }
            } else {
                if (messageInput.text.toString().isNotEmpty()) {
                    updateDatabaseWithNewMessage(messageDatabase, newMessageMap)
                }
            }
    }

    private fun updateDatabaseWithNewMessage(newMessageDb: DatabaseReference, newMessageMap: Map<String, Any>) {
        newMessageDb.updateChildren(newMessageMap)
        messageInput.text = null
        mediaUriList.clear()
        imageIdList.clear()
        imageAdapter.notifyDataSetChanged()

        val newUser = User(FirebaseAuth.getInstance().uid)
        val message: String = if (newMessageMap["text"] != null) {
            newMessageMap["text"].toString()
        } else {
            "Sent Media"
        }
        for(user in myChatObject.addUserToList(newUser)) {
            if (user.uid != FirebaseAuth.getInstance().uid && user.notificationKey != null) {
                SendNotification(message, user.notificationKey!!, "New Message")
            }
        }
    }

    private fun getChatMessages() {
        val newMessageDatabase = FirebaseDatabase.getInstance().reference.child("chat")
            .child(myChatObject.chatId)

        newMessageDatabase.addChildEventListener(object : ChildEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                TODO("not implemented")
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(databaseSnapShot: DataSnapshot, p1: String?) {
                TODO("not implemented")
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(databaseSnapShot: DataSnapshot, p1: String?) {
                if (databaseSnapShot.exists()) {
                    var displayedChatMessage = ""
                    var displayedSenderId = ""
                    val imageUrlList: ArrayList<String> = ArrayList()
                    if (databaseSnapShot.child("text").value != null) {
                        displayedChatMessage = databaseSnapShot.child("text").value.toString()
                    }
                    if (databaseSnapShot.child("creator").value != null) {
                        displayedSenderId = databaseSnapShot.child("creator").value.toString()
                    }
                    if (databaseSnapShot.child("media").childrenCount > 0) {
                        for (imageSnapShot in databaseSnapShot.child("media").children) {
                            imageUrlList.add(imageSnapShot.value.toString())
                        }
                    }
                    val chatMessage = Message(databaseSnapShot
                        .key!!, displayedSenderId, displayedChatMessage, imageUrlList)

                    messageList.add(chatMessage)
                    layoutManager.scrollToPosition(messageList.size - 1)
                    messageAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildAdded(databaseSnapShot: DataSnapshot, p1: String?) {
                if (databaseSnapShot.exists()) {
                    var displayedChatMessage = ""
                    var displayedSenderId = ""
                    val imageUrlList: ArrayList<String> = ArrayList()
                    messageList.clear()
                    if (databaseSnapShot.child("text").value != null) {
                        displayedChatMessage = databaseSnapShot.child("text").value.toString()
                    }
                    if (databaseSnapShot.child("creator").value != null) {
                        displayedSenderId = databaseSnapShot.child("creator").value.toString()
                    }
                    if (databaseSnapShot.child("media").childrenCount > 0) {
                        for (imageSnapShot in databaseSnapShot.child("media").children) {
                            imageUrlList.add(imageSnapShot.value.toString())
                        }
                    }
                    val chatMessage = Message(databaseSnapShot
                        .key!!, displayedSenderId, displayedChatMessage, imageUrlList)

                    messageList.add(chatMessage)
                    layoutManager.scrollToPosition(messageList.size - 1)
                    messageAdapter.notifyDataSetChanged()
                }
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                TODO("not implemented")
                //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun initializeChatRecyclerView() {
        recyclerView = chat_recycler_view
        layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = layoutManager
        messageAdapter = MessageAdapter(applicationContext, messageList)
        recyclerView.adapter = messageAdapter
        val itemTouchHelper = ItemTouchHelper(SwipeToDelete(messageAdapter,applicationContext))
        itemTouchHelper.attachToRecyclerView(recyclerView)


    }

    private val pickImageContent = 1
    private val mediaUriList: ArrayList<String> = ArrayList()

    private fun openMediaGallery() {
        val mediaIntent = Intent()
        mediaIntent.type = "image/*"
        mediaIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        mediaIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(mediaIntent, "Select Picture(s)"), pickImageContent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == pickImageContent) {
            if (data!!.clipData == null) {
                mediaUriList.add(data.data.toString())
            } else {
                for (i in data.clipData.itemCount.toString()) {
                    mediaUriList.add(data.clipData.getItemAt(i.toInt()).uri.toString())
                }
            }
            imageAdapter.notifyDataSetChanged()
        }
    }

    private fun initializeImageRecyclerView() {
        imageRecyclerView = media_recycler_view
        imageLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)
        imageRecyclerView.isNestedScrollingEnabled = false
        imageRecyclerView.setHasFixedSize(false)
        imageRecyclerView.layoutManager = imageLayoutManager
        imageAdapter = ImageAdapter(applicationContext, mediaUriList)
        imageRecyclerView.adapter = imageAdapter
    }

}
