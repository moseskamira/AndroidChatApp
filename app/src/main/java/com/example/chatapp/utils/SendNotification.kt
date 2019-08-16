package com.example.chatapp.utils
import com.onesignal.OneSignal

import org.json.JSONObject

class SendNotification(var message: String, private var notificationKey: String, private var heading: String) {



    private val notificationContent: JSONObject = JSONObject("{'contents' : {'en' : '$message'}, ' include_player_ids'" +
            " : ['$notificationKey'],' headings' : {'en' : '$heading'}}")
    init {
        OneSignal.postNotification(notificationContent, null)
    }

}