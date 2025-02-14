package com.example.lumoslibrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Tapping Help Button navigates to Help Page
        val helpButton = findViewById<ImageView>(R.id.help_button)
        helpButton.setOnClickListener{
            val intent = Intent(this@MainActivity, HelpPageActivity::class.java)
            startActivity(intent)
        }
    }



}