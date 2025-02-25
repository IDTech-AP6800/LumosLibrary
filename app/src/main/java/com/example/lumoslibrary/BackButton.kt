package com.example.lumoslibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout

class BackButton(activity: AppCompatActivity) {
    // Initialize "backButton" as an ImageButton representing the back icon
    private val backButton: ImageView = activity.findViewById<ImageButton>(R.id.back_button)
        .findViewById(R.id.back_button)

    init{
        backButton.setOnClickListener{
            activity.onBackPressedDispatcher.onBackPressed()
        }
    }
}