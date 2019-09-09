package com.example.chatapp.song

import android.os.Bundle
import android.widget.Button
import com.example.chatapp.R
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener
import com.google.android.youtube.player.YouTubePlayerView
import kotlinx.android.synthetic.main.activity_my_songs.*


class MySongsActivity : YouTubeBaseActivity() {
    lateinit var playVideoButton: Button
    lateinit var playerView: YouTubePlayerView
    lateinit var onInitializedListner: OnInitializedListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_songs)
        playerView = youtube_view
        onInitializedListner = object :OnInitializedListener{
            override fun onInitializationSuccess(provider: YouTubePlayer.Provider?, youtubePlayer: YouTubePlayer?,
                                                 p2: Boolean) {
                val myVideoList: ArrayList<String> = ArrayList()
                myVideoList.add("eNjvLfEL7Wg")
                myVideoList.add("hVO38nJ2IhY")
                youtubePlayer!!.loadVideos(myVideoList)
//                youtubePlayer!!.loadPlaylist("https://youtu.be/eNjvLfEL7Wg")

            }
            override fun onInitializationFailure(p0: YouTubePlayer.Provider?, p1: YouTubeInitializationResult?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        playVideoButton = youtube_play
        playVideoButton.setOnClickListener {
            playerView.initialize(YoutubeConfig.getAPIKey(), onInitializedListner )
        }
    }
}
