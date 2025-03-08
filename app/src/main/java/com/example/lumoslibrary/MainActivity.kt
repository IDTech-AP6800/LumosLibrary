package com.example.lumoslibrary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.lumoslibrary.viewmodels.MainViewModel
import com.idtech.zsdk_client.Client
import com.idtech.zsdk_client.GetDevicesAsync
import android.widget.ImageView
import androidx.activity.viewModels
import com.idtech.zsdk_client.GetDevicesAsync
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Enumerate devices immediately;
        // if the server is not yet connected, this may complete after.
        Client.GetDevicesAsync()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val jsonFile = copyJsonToInternalStorage(this, "users.json")
        // Observe connection status
        viewModel.connectionStatus.observe(this) { status ->
            Log.d("ConnectionStatus", "Connection status: $status")
        }

        // Connect to the server (via your ViewModel).
        // Adjust the IP/port to match your environment.
        viewModel.connectToServer("127.0.0.1", "42501", this)

        // OPTIONAL: If you want a simple “tap anywhere to proceed” approach:
        val rootView = findViewById<View>(android.R.id.content)
        rootView.setOnClickListener {
            // Launch PaymentActivity on tap
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
        }
    }

    private fun copyJsonToInternalStorage(context: Context, fileName: String): File {
        val file = File(context.filesDir, fileName)

        if (!file.exists()) {
            try {
                context.assets.open(fileName).use { inputStream ->
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    companion object{
        private const val TAG = "MainActivity"
    }
}
