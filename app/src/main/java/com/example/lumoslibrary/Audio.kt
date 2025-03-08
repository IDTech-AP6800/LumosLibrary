package com.example.lumoslibrary
import android.content.Context
import android.media.MediaPlayer


class Audio {
    private var mediaPlayer: MediaPlayer? = null

    fun playClickAudio(context: Context){
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, R.raw.walkman_button)
        mediaPlayer?.start()
    }

    fun playConfirmationCompleteAudio(context: Context){
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, R.raw.short_success)
        mediaPlayer?.start()
    }

    fun destroy(){
        mediaPlayer?.release()
        mediaPlayer = null
    }

}