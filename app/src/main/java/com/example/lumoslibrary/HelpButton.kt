package com.example.lumoslibrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class HelpButton (activity: AppCompatActivity) {
    private val helpButton: ImageView = activity.findViewById<ImageView>(R.id.help_button)

    init {
        helpButton.setOnClickListener {
            val intent = Intent(activity, HelpPageActivity::class.java)
            activity.startActivity(intent)
        }
    }
}