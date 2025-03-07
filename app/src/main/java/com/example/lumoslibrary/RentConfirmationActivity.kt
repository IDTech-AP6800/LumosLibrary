package com.example.lumoslibrary

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintSet
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

class RentConfirmationActivity : AppCompatActivity() {

    private val audio: Audio = Audio()
    private lateinit var viewKonfetti: KonfettiView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_confirmation)

        val button = findViewById<AppCompatButton>(R.id.rentConf_button)
        viewKonfetti = findViewById(R.id.rentConf_konfettiView)

        button.setOnClickListener{
            audio.playClickAudio(this)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        audio.playConfirmationCompleteAudio(this)

        val party = Party(
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
        )

        viewKonfetti.start(party)


    }
    override fun onDestroy() {
        super.onDestroy()
        audio.destroy()
    }
}