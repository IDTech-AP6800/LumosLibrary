package com.example.lumoslibrary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.lumoslibrary.viewmodels.MainViewModel
import com.idtech.zsdk_client.Client
import com.idtech.zsdk_client.GetDevicesAsync

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        Client.GetDevicesAsync()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Observe connection status
        viewModel.connectionStatus.observe(this) { status ->
            Log.d("ConnectionStatus", "Connection status: $status")
        }

        // Trigger server connection
        viewModel.connectToServer("127.0.0.1", "42501", this)

    }
}