package com.example.chatapp.myChat.message

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MessageActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var imageRecyclerView: RecyclerView
    private lateinit var imageLayoutManager: RecyclerView.LayoutManager
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var messageAdapter: MessageAdapter
    private var messageList: ArrayList<Message> = ArrayList()
    private lateinit var sendButton: ImageButton
    private lateinit var photoGalleryButton: ImageButton
    private lateinit var messageInput: EditText
    lateinit var camera: ImageButton
    private lateinit var messageDatabase: DatabaseReference
    private lateinit var myChatObject: Chat
    lateinit var cordinator: RelativeLayout
    private val imageIdList: ArrayList<String> = ArrayList()
    private var totalImagesUploaded: Int = 0
    lateinit var photoFilePath: String
    private val mediaUriList: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        Fresco.initialize(this)
        messageInput = message_input_id
        camera = cam_id
        myChatObject = intent.getSerializableExtra("chatObject") as Chat
        sendButton = send_id
        photoGalleryButton = add_media_button
        sendButton.setOnClickListener {
            sendMessage()
        }
        photoGalleryButton.setOnClickListener {
            openPhotoGallery()
        }
        camera.setOnClickListener {
            checkCameraPermission()
        }
        messageInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
              textChanged(s)
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
               textChanged(s)
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textChanged(s)
            }
        })
        initializeChatRecyclerView()
        initializeImageRecyclerView()
        getChatMessages()
        cordinator = findViewById(R.id.message_coordinator)
    }

    private fun textChanged(k: CharSequence) {
        when {
            k.isNotEmpty() -> {
                sendButton.visibility = View.VISIBLE
                camera.visibility = View.GONE
            }
            else -> {
                sendButton.visibility = View.GONE
                camera.visibility = View.VISIBLE
            }
        }
    }

    private fun sendMessage() {
        messageDatabase = FirebaseDatabase.getInstance().reference.child("chat").child(myChatObject.chatId)
            .child("messages")
        val messageId = messageDatabase.push().key
        val newMessageMap = HashMap<String, Any>()
        val creatorId = messageDatabase.child("creator").push().key
        newMessageMap["creator/$creatorId"] = FirebaseAuth.getInstance().uid.toString()
        val textId = messageDatabase.child("text").push().key
        when {
            messageInput.text.toString().isNotEmpty() -> newMessageMap["text/$textId"] = messageInput.text.toString()
        }
        when {
            mediaUriList.isNotEmpty() -> for (mediaUri in mediaUriList) {
                val imageId = messageDatabase.child("media").push().key
                imageIdList.add(imageId!!)
                when {
                    messageId != null -> {
                        val filePath: StorageReference = FirebaseStorage.getInstance().reference
                            .child("chat").child("chatId").child(messageId).child(imageId)
                        val uploadTask: UploadTask = filePath.putFile(Uri.parse(mediaUri))
                        uploadTask.addOnSuccessListener {
                            filePath.downloadUrl.addOnSuccessListener { uri ->
                                newMessageMap["media/${imageIdList[totalImagesUploaded] + "/"}"] = uri.toString()
                                totalImagesUploaded++
                                when (totalImagesUploaded) {
                                    mediaUriList.size -> updateDatabaseWithNewMessage(messageDatabase, newMessageMap)
                                }
                            }
                        }
                    }
                }
            }
            else -> when {
                messageInput.text.toString().isNotEmpty() -> updateDatabaseWithNewMessage(messageDatabase,
                    newMessageMap)
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
        val message: String = when {
            newMessageMap["text"] != null -> newMessageMap["text"].toString()
            else -> "Sent Media"
        }
        for(user in myChatObject.addUserToList(newUser)) {
            when {
                user.uid != FirebaseAuth.getInstance().uid && user.notificationKey != null -> SendNotification(message,
                    user.notificationKey!!, "New Message")
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
                when {
                    databaseSnapShot.exists() -> {
                        var displayedChatMessage = ""
                        var displayedSenderId = ""
                        val imageUrlList: ArrayList<String> = ArrayList()
                        when {
                            databaseSnapShot.child("text").value != null -> for (textSnapShot in
                            databaseSnapShot.child("text").children) {
                                displayedChatMessage = textSnapShot.value.toString()
                            }
                        }
                        when {
                            databaseSnapShot.child("creator").value != null -> for (creatorSnapShot
                            in databaseSnapShot.child("creator").children) {
                                displayedSenderId = creatorSnapShot.value.toString()
                            }
                        }
                        when {
                            databaseSnapShot.child("media").childrenCount > 0 -> for (imageSnapShot
                            in databaseSnapShot.child("media").children) {
                                imageUrlList.add(imageSnapShot.value.toString())
                            }
                        }
                        val chatMessage = Message(databaseSnapShot.key!!, displayedSenderId, displayedChatMessage,
                            imageUrlList)
                        messageList.add(chatMessage)
                        layoutManager.scrollToPosition(messageList.size - 1)
                        messageAdapter.notifyDataSetChanged()
                    }
                }
            }
            override fun onChildAdded(databaseSnapShot: DataSnapshot, p1: String?) {
                when {
                    databaseSnapShot.exists() -> {
                        var displayedChatMessage = ""
                        var displayedSenderId = ""
                        val imageUrlList: ArrayList<String> = ArrayList()
                        messageList.clear()
                        when {
                            databaseSnapShot.child("text").value != null -> for (textSnapShot in
                            databaseSnapShot.child("text").children) {
                                displayedChatMessage = textSnapShot.value.toString()
                            }
                        }
                        when {
                            databaseSnapShot.child("creator").value != null -> for (creatorSnapShot
                            in databaseSnapShot.child("creator").children) {
                                displayedSenderId = creatorSnapShot.value.toString()
                            }
                        }
                        when {
                            databaseSnapShot.child("media").childrenCount > 0 -> for (imageSnapShot
                            in databaseSnapShot.child("media").children) {
                                imageUrlList.add(imageSnapShot.value.toString())
                            }
                        }
                        val chatMessage = Message(databaseSnapShot.key!!, displayedSenderId, displayedChatMessage,
                            imageUrlList)
                        messageList.add(chatMessage)
                        layoutManager.scrollToPosition(messageList.size - 1)
                        messageAdapter.notifyDataSetChanged()
                    }
                }
            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                TODO("not implemented")
                //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun openPhotoGallery() {
        val mediaIntent = Intent()
        mediaIntent.type = "image/*"
        mediaIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        mediaIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(mediaIntent, "Select Picture(s)"), 1)
    }


    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager
                .PERMISSION_GRANTED -> {
                camera.isEnabled = false
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE ), 2)
            } else -> { camera.isEnabled = true
            takePicture()}
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when {
            requestCode == 2 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED -> {
                camera.isEnabled = true
                takePicture()
            }
        }
    }

    private fun takePicture() {
        val picIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        when {
            picIntent.resolveActivity(packageManager) != null -> {
                val photoFile = createPhoneFile()
                when {
                    photoFile != null -> {
                        photoFilePath = photoFile.absolutePath
                        val photoUri: Uri = FileProvider.getUriForFile(this,
                            "com.example.chatapp.myChat.message.GenericFileProvider", photoFile)
                        picIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        startActivityForResult(picIntent, 3)
                        // Add Code Here

                    }
                }
            }
        }
    }

    private fun createPhoneFile(): File? {
        var photo: File? = null
        val photoName: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        when {
            photoName.isNotEmpty() -> {
                val photoDirectory = File(Environment.getExternalStorageDirectory().absolutePath +
                        File.separator + "DCIM/Camera")
                when {
                    !photoDirectory.exists() -> photoDirectory.mkdirs()
                }
                photo = File.createTempFile(photoName, ".jpg", photoDirectory)
            }
        }
        return photo
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == 1 && resultCode == Activity.RESULT_OK -> {
                sendButton.visibility = View.VISIBLE
                camera.visibility = View.GONE

                when {
                    data!!.clipData == null -> mediaUriList.add(data.data.toString())
                    else -> for (i in data.clipData?.itemCount.toString()) {
                        mediaUriList.add(data.clipData?.getItemAt(i.toInt())?.uri.toString())
                    }
                }
                imageAdapter.notifyDataSetChanged()
            }
            requestCode == 3 && resultCode == Activity.RESULT_OK -> {
                val bitmap: Bitmap = BitmapFactory.decodeFile(photoFilePath)
            }
        }
    }

    private fun initializeChatRecyclerView() {
        recyclerView = chat_recycler_view
        layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager = layoutManager
        messageAdapter = MessageAdapter(this, messageList)
        recyclerView.adapter = messageAdapter
        val itemTouchHelper = ItemTouchHelper(SwipeToDelete(messageAdapter,applicationContext))
        itemTouchHelper.attachToRecyclerView(recyclerView)
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
