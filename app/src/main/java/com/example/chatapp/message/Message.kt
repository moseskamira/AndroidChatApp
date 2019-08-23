package com.example.chatapp.message

data class Message(var messageId: String,var  senderId: String, var message: String,
                   var imageUrlList: ArrayList<String>)