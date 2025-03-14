package com.example.lumoslibrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.example.lumoslibrary.activities.HelpPageActivity

/* Displays Help Button*/
class HelpButton (activity: AppCompatActivity) {
    private val helpButton: ImageView? = activity.findViewById(R.id.help_button)
    private val audio: Audio = Audio()
    init {
        helpButton?.setOnClickListener(1000L) {
            audio.playClickAudio(activity.baseContext)
            val intent = Intent(activity, HelpPageActivity::class.java)
            activity.startActivity(intent)
        }
    }

    fun onDestroy(){
        audio.destroy()
    }
}