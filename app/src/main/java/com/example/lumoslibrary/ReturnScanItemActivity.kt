package com.example.lumoslibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ReturnScanItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_scan_item)

        HelpButton(this)
    }
}