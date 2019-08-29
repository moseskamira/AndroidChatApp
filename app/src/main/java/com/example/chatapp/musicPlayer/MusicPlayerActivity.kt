package com.example.chatapp.musicPlayer

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatapp.R
import com.mtechviral.mplaylib.MusicFinder
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import java.util.*

class MusicPlayerActivity : AppCompatActivity() {
    lateinit var albumArt: ImageView
    lateinit var playButton: ImageButton
    lateinit var shuffleButton: ImageButton
    lateinit var nextButton: ImageButton
    lateinit var prevButton: ImageButton
    lateinit var stopButton: ImageButton
    lateinit var songTitle: TextView
    lateinit var songArtiste: TextView
    var mediaPlayer: MediaPlayer? = null
    var songCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1)
        } else {
            createMediaPlayer()
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createMediaPlayer()
        } else {
            longToast("Permission Not Granted")
            finish()
        }
    }

    private fun createMediaPlayer() {
        val musicFinder = MusicFinder(contentResolver)
        musicFinder.prepare()
        val songsList: List<MusicFinder.Song> = musicFinder.allSongs
        Log.d("ALL SONGS", songsList.toString())
            val mediaPlayerUI = object : AnkoComponent<MusicPlayerActivity> {
                override fun createView(ui: AnkoContext<MusicPlayerActivity>) = with(ui) {
                    relativeLayout {
                        backgroundColor = Color.BLACK
                        albumArt = imageView {
                            scaleType = ImageView.ScaleType.FIT_CENTER
                        }.lparams(matchParent, matchParent)
                        verticalLayout {
                            backgroundColor = Color.parseColor("#99000000")
                            songTitle = textView {
                                textColor = Color.WHITE
                                typeface = Typeface.DEFAULT_BOLD
                                textSize = 18f
                            }
                            songArtiste = textView {
                                textColor = Color.WHITE
                            }
                            linearLayout {
                                prevButton = imageButton {
                                    imageResource = R.drawable.ic_skip_previous_black_24dp
                                    onClick {
                                        playPrevious()
                                    }
                                }.lparams(0, wrapContent, 0.5f)
                                playButton = imageButton {
                                    imageResource = R.drawable.ic_play_circle_outline_black_24dp
                                    onClick {
                                        playOrPause()
                                    }
                                }.lparams(0, wrapContent, 0.5f)
                                nextButton = imageButton {
                                    imageResource = R.drawable.ic_skip_next_black_24dp
                                    onClick {
                                        playNext()
                                    }
                                }.lparams(0, wrapContent, 0.5f)
                                stopButton = imageButton {
                                    imageResource = R.drawable.ic_stop_black_24dp
                                    onClick {
                                        stopPlaying()
                                    }
                                }.lparams(0, wrapContent, 0.5f)
                                shuffleButton = imageButton {
                                    imageResource = R.drawable.ic_shuffle_black_24dp
                                    onClick {
                                        playRandom()
                                    }
                                }.lparams(0, wrapContent, 0.5f)
                            }.lparams(matchParent, wrapContent) {
                                topMargin = dip(5)
                            }
                        }.lparams(matchParent, wrapContent) {
                            alignParentBottom()
                        }
                    }
                }

                fun playRandom() {
                    Collections.shuffle(songsList)
                    if (songsList.isNotEmpty()) {
                        val song = songsList[songCounter]
                    Log.d("SONG", song.toString())
                    if (mediaPlayer != null) {
                        mediaPlayer!!.reset()
                    }
                    mediaPlayer = MediaPlayer.create(this@MusicPlayerActivity, song.uri)!!
                    mediaPlayer!!.setOnCompletionListener {
                        playRandom()
                    }
                    albumArt.imageURI = song.albumArt
                    songArtiste.text = song.artist
                    mediaPlayer!!.start()
                    playButton.imageResource = R.drawable.ic_pause_circle_outline_black_24dp
                    }
                }
                fun playOrPause() {
                    val songPlaying: Boolean? = mediaPlayer!!.isPlaying
                    if (songPlaying == true) {
                        mediaPlayer!!.pause()
                        playButton.imageResource = R.drawable.ic_play_circle_outline_black_24dp
                    } else {
                        mediaPlayer!!.start()
                        playButton.imageResource = R.drawable.ic_pause_circle_outline_black_24dp
                    }
                }
                fun playNext() {
                    songCounter++
                    if (songCounter < songsList.size) {
                        playRandom()
                    } else {
                        songCounter = 0
                    }
                }
                fun playPrevious() {
                    songCounter--
                    if (songCounter >= 0) {
                        playRandom()
                    } else {
                        songCounter = 0
                    }
                }
                fun stopPlaying() {
                    val songPlaying: Boolean? = mediaPlayer!!.isPlaying
                    if (songPlaying == true) {
                        mediaPlayer!!.stop()
                        playButton.imageResource = R.drawable.ic_play_circle_outline_black_24dp
                    }
                }
            }
            mediaPlayerUI.setContentView(this@MusicPlayerActivity)
            mediaPlayerUI.playRandom()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer!!.release()
    }
}
