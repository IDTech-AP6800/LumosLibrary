package com.example.lumoslibrary

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton

class ReturnConfirmationActivity : AppCompatActivity() {

    private val audio: Audio = Audio()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_confirmation)

        val button = findViewById<AppCompatButton>(R.id.returnConf_button)

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