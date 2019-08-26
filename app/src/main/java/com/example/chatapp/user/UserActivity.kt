package com.example.chatapp.user

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.utils.CountryCodeConverter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_user.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class UserActivity : AppCompatActivity() {
    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userAdapter: UserAdapter
    private lateinit var userLayoutManager: RecyclerView.LayoutManager
    private var userContactList: ArrayList<User> = ArrayList()
    private var databaseUserList: ArrayList<User> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.chatapp.R.layout.activity_user)
        val createChatGroup: Button = create_chat_group
        createChatGroup.setOnClickListener {
            createChatGroup()
        }
        initializeRecyclerView()
        getPhoneContactList()
    }

    private fun getPhoneContactList() {
        val phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
            null, null, null)
        while (phones!!.moveToNext()) {
            val phoneContactName = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds
                .Phone.DISPLAY_NAME))
            var phoneContactNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds
                .Phone.NUMBER))
            phoneContactNumber = phoneContactNumber.replace(" ", "")
            phoneContactNumber = phoneContactNumber.replace("-", "")
            phoneContactNumber = phoneContactNumber.replace("(", "")
            phoneContactNumber = phoneContactNumber.replace(")", "")

            if (phoneContactNumber[0].toString() != "+") {
                phoneContactNumber = getCountryISO() + phoneContactNumber
            }
            val user = User(phoneContactName, phoneContactNumber, uid = null)
            userContactList.add(user)
            Log.d("PHONE CONTACTS", user.phoneNumber)
        }
        phones.close()
        getDatabaseUserDetail()
    }

    private fun getDatabaseUserDetail() {
        val userDatabaseReferenceInstance = FirebaseDatabase.getInstance().getReference("user")
        val query = userDatabaseReferenceInstance.orderByValue()
        query.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                if (dataSnapShot.exists()) {
                    var databasePhoneNumber: String? = null
                    var databaseUserName: String? = null
                    for (childSnapShot in dataSnapShot.children) {
                        if (childSnapShot.child("phoneNumber").value != null) {
                            databasePhoneNumber = childSnapShot.child("phoneNumber").value.toString()
                        }
                        if (childSnapShot.child("userName").value != null) {
                            databaseUserName = childSnapShot.child("userName").value.toString()
                        }
                        if (databaseUserName != null && databasePhoneNumber != null) {
                            val databaseUser = User(databaseUserName, databasePhoneNumber, childSnapShot.key)

                            if (databaseUserName == databasePhoneNumber) {
                                for (contactListIt in userContactList) {
                                    if (contactListIt.phoneNumber == databaseUser.phoneNumber) {
                                        databaseUser.userName = contactListIt.userName
                                    }
                                }
                            }
                            databaseUserList.add(databaseUser)
                            userAdapter.notifyDataSetChanged()
                        }
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

    private fun getCountryISO(): String {
        var iso: String? = null
        val telephonyManager = applicationContext.getSystemService(Context
            .TELEPHONY_SERVICE) as TelephonyManager
            if (telephonyManager.networkCountryIso != null && telephonyManager.networkCountryIso.toString() != "") {
               iso = telephonyManager.networkCountryIso.toString()
            }
        return CountryCodeConverter.Iso2Phone.getPhone(iso!!)!!.toUpperCase()
    }

    private fun initializeRecyclerView() {
        userRecyclerView = recycler_view
        userLayoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        userRecyclerView.isNestedScrollingEnabled = false
        userRecyclerView.setHasFixedSize(false)
        userRecyclerView.layoutManager = userLayoutManager
        userAdapter = UserAdapter(applicationContext, databaseUserList)
        userRecyclerView.adapter = userAdapter
    }
}
