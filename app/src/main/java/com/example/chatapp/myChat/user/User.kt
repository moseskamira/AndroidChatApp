package com.example.chatapp.myChat.user

import java.io.Serializable

data class User(var uid: String?) : Serializable {
    lateinit var userName: String
    lateinit var phoneNumber: String
    var notificationKey: String? =null
    var selected: Boolean = false

constructor(userName: String, phoneNumber: String, uid: String?) : this(uid) {
    this.userName = userName
    this.phoneNumber = phoneNumber
}
}
