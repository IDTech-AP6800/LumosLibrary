package com.example.lumoslibrary

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintSet

class RentConfirmationActivity : AppCompatActivity() {

    private val audio: Audio = Audio()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_confirmation)

        val button = findViewById<AppCompatButton>(R.id.rentConf_button)

        button.setOnClickListener{
            audio.playClickAudio(this)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        audio.playConfirmationCompleteAudio(this)

    }
    override fun onDestroy() {
        super.onDestroy()
        audio.destroy()
    }
}