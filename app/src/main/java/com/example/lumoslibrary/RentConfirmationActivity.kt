package com.example.lumoslibrary

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintSet

class RentConfirmationActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_confirmation)

        val button = findViewById<AppCompatButton>(R.id.rentConf_button)

        button.setOnClickListener{
//            val intent = Intent(this, MainActivity::class.java)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        playConfirmationCompleteAudio()

    }

    private fun playConfirmationCompleteAudio() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, R.raw.short_success)
        mediaPlayer?.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}