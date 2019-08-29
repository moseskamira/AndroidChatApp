package com.example.chatapp.song

class YoutubeConfig {
    companion object {
        const val  API_KEY: String = "AIzaSyByD-uSiOzUfY92pyqE0QOkCqmV_yQNfgo"
        fun getAPIKey(): String {
            return API_KEY
        }
    }
}