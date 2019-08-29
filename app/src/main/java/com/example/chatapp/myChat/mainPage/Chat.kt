package com.example.chatapp.myChat.mainPage

import com.example.chatapp.myChat.user.User
import java.io.Serializable


data class Chat (var chatId: String) : Serializable{
    private val userArrayList: ArrayList<User> = ArrayList()
    fun addUserToList(user: User): ArrayList<User>{
        userArrayList.add(user)
        return userArrayList
    }

}
