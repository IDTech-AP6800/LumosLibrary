package com.example.lumoslibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class SearchResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        HelpButton(this)
    }
}