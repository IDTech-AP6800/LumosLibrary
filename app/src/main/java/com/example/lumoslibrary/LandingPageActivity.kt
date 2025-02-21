package com.example.lumoslibrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout

class LandingPageActivity : AppCompatActivity() {

//    private lateinit var motionLayout: MotionLayout

    private lateinit var rentButton: Button
    private lateinit var returnButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        rentButton = findViewById(R.id.rent_button)
        returnButton = findViewById(R.id.return_button)

        HelpButton(this)

        // Find views
//        motionLayout = findViewById(R.id.motion_layout_books)

        rentButton.setOnClickListener { navigateToLogin(1) }
        returnButton.setOnClickListener { navigateToLogin(2) }
    }

    private fun navigateToLogin(state: Int) {
        CurrentSession.state = state
        startActivity(Intent(this, LoginActivity::class.java))
    }

}