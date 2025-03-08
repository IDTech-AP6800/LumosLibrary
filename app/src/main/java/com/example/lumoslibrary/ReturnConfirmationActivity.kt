package com.example.lumoslibrary

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class ReturnConfirmationActivity : AppCompatActivity() {

    private val audio: Audio = Audio()
    private lateinit var konfettiView: KonfettiView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_confirmation)
        konfettiView = findViewById(R.id.returnConf_konfettiView)

        val button = findViewById<AppCompatButton>(R.id.returnConf_button)

        button.setOnClickListener(1000L){
            audio.playClickAudio(this)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        audio.playConfirmationCompleteAudio(this)

        val party = Party(
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
        )

        konfettiView.start(party)
    }
    override fun onDestroy() {
        super.onDestroy()
        audio.destroy()
    }
}