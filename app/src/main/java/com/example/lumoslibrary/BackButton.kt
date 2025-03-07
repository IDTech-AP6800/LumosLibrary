package com.example.lumoslibrary

import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton

class BackButton(activity: AppCompatActivity) {
    // Initialize "backButton" as an ImageButton representing the back icon
    private val backButton: ImageButton? = activity.findViewById(R.id.back_button)
    private val audio: Audio = Audio()
    init{
        backButton?.setOnClickListener {
            audio.playClickAudio(activity.baseContext)
            activity.onBackPressedDispatcher.onBackPressed()
        }
    }

    fun onDestroy(){
        audio.destroy()
    }
}