package com.example.lumoslibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout

class LandingPageActivity : AppCompatActivity() {

//    private lateinit var motionLayout: MotionLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        // Find views
//        motionLayout = findViewById(R.id.motion_layout_books)

        HelpButton(this)
    }
}