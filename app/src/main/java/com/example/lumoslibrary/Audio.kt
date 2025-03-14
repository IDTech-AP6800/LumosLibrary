package com.example.lumoslibrary
import android.content.Context
import android.media.MediaPlayer

/* Handles the audio cues used for button clicks and confirmations
*  By default uses a click for buttons */
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