package com.example.lumoslibrary.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.lumoslibrary.Audio
import com.example.lumoslibrary.CurrentSession
import com.example.lumoslibrary.HelpButton
import com.example.lumoslibrary.R
import com.example.lumoslibrary.setOnClickListener

/* Landing page that holds a carousel with popular items/books
   Links to the search of location of books/items
*  Also links to the Rent and Return Activities via User Login */
class LandingPageActivity : AppCompatActivity() {


    private lateinit var rentButton: Button
    private lateinit var returnButton: Button
    private lateinit var searchButton: Button
    private val audio: Audio = Audio()
    private lateinit var helpButton: HelpButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        searchButton = findViewById(R.id.search_bar)

        rentButton = findViewById(R.id.rent_button)
        returnButton = findViewById(R.id.return_button)

        helpButton = HelpButton(this)

        searchButton.setOnClickListener{
            startActivity(Intent(this, SearchResultsActivity::class.java))}

        rentButton.setOnClickListener(1000L){
            audio.playClickAudio(this)
            navigateToLogin(1) }
        returnButton.setOnClickListener(1000L){
            audio.playClickAudio(this)
            navigateToLogin(2) }

    }

    private fun navigateToLogin(state: Int) {
        CurrentSession.state = state
        startActivity(Intent(this, LoginActivity::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        audio.destroy()
        helpButton.onDestroy()
    }

}