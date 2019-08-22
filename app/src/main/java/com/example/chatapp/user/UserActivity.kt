package com.example.chatapp.user

import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_user.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var myAdapter: UserAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private var contactList: ArrayList<User> = ArrayList()
    private var databaseUserList: ArrayList<User> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        val createChatGroup: Button = create_chat_group
        createChatGroup.setOnClickListener {
            createChatGroup()
        }
        initializeRecyclerView()
        getPhoneContactList()
    }

    private fun getPhoneContactList() {
        val phoneContacts: Cursor = contentResolver.query (
            ContactsContract.CommonDataKinds
                .Phone.CONTENT_URI, null, null, null, null )!!
        if (phoneContacts.count > 0) {
            phoneContacts.moveToFirst()
            val phoneContactUserNme: String = phoneContacts.getString(phoneContacts
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneContactNumber: String = phoneContacts.getString(phoneContacts
                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
            val user = User(phoneContactUserNme, phoneContactNumber, uid = null)
            contactList.add(user)
            getUserDetail(user)
        }
    }

    private fun getUserDetail(user: User) {
        val userDatabaseReferenceInstance = FirebaseDatabase.getInstance().getReference("user")
        val query = userDatabaseReferenceInstance.orderByValue()
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                if (dataSnapShot.exists()) {
                    var databasePhoneNumber = ""
                    var databaseUserName = ""
                    for (childSnapShot in dataSnapShot.children) {
                        if (childSnapShot.child("phoneNumber").value != null) {
                            databasePhoneNumber = childSnapShot.child("phoneNumber").value.toString()
                        }
                        if (childSnapShot.child("userName").value != null) {
                            databaseUserName = childSnapShot.child("userName").value.toString()
                        }

                        val databaseUser = User(databaseUserName, databasePhoneNumber, childSnapShot.key)
                        if (databaseUserName == databasePhoneNumber)
                            for (contactListIterator in contactList) {
                                if (contactListIterator.phoneNumber == user.phoneNumber) {
                                    databaseUser.userName = contactListIterator.userName
                                }
                            }
                        databaseUserList.add(databaseUser)
                        myAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Database Error", databaseError.toString())
            }
            })
        }

    private fun createChatGroup() {
        val key = FirebaseDatabase.getInstance().reference.child("chat").push().key
        val chatInfoDatabase = FirebaseDatabase.getInstance().reference.child("chat")
            .child(key.toString()).child("info")
        FirebaseDatabase.getInstance().reference.child("user")
            .child(FirebaseAuth.getInstance().uid.toString()).child("chat").child(key.toString())
            .setValue(true)
        val chatMap  = HashMap<String, Any?>()
        chatMap["id"] = key
        chatMap["users/${FirebaseAuth.getInstance().uid}"] = true
        var validChat = false
        for (user in databaseUserList) {
            if (user.selected) {
                validChat = true
                chatMap["users/ ${user.uid}"] = true
                FirebaseDatabase.getInstance().reference.child("user")
                    .child(user.uid!!).child("chat").child(key.toString())
                    .setValue(true)
            }
        }
        if (validChat) {
            chatInfoDatabase.updateChildren(chatMap)
            FirebaseDatabase.getInstance().reference.child("user").child(FirebaseAuth.getInstance().uid!!)
                .child("chat").child(key!!).setValue(true)
        }
    }

    private fun initializeRecyclerView() {
        recyclerView = recycler_view
        layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setHasFixedSize(false)
        recyclerView.layoutManager =layoutManager
        myAdapter = UserAdapter(applicationContext, databaseUserList)
        recyclerView.adapter = myAdapter
    }
}
