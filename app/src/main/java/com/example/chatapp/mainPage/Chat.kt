package com.example.chatapp.mainPage

import com.example.chatapp.user.User
import java.io.Serializable


data class Chat (var chatId: String) : Serializable{
    private val userArrayList: ArrayList<User> = ArrayList()
    fun addUserToList(user: User): ArrayList<User>{
        userArrayList.add(user)
        return userArrayList
    }

}
