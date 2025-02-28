package com.example.lumoslibrary

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import kotlin.concurrent.thread

class HelpPageActivity : AppCompatActivity() {
    private val API_KEY = ""
    private val API_URL = "https://api.openai.com/v1/chat/completions"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_page)

        val inputField: EditText = findViewById(R.id.input_field)
        val responseText: TextView = findViewById(R.id.response_text)

        val query1: Button = findViewById(R.id.query_1)
        val query2: Button = findViewById(R.id.query_2)
        val query3: Button = findViewById(R.id.query_3)

        // Handle Enter key press
        inputField.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event?.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)) {
                val userInput = inputField.text.toString().trim()
                if (userInput.isNotEmpty()) {
                    inputField.text.clear()
                    checkInternetAndFetchResponse(userInput, responseText)
                }
                return@setOnEditorActionListener true
            }
            false
        }

        // Handle Suggested Query Button Clicks
        query1.setOnClickListener {
            inputField.setText(query1.text.toString())
            checkInternetAndFetchResponse(query1.text.toString(), responseText)
        }
        query2.setOnClickListener {
            inputField.setText(query2.text.toString())
            checkInternetAndFetchResponse(query2.text.toString(), responseText)
        }
        query3.setOnClickListener {
            inputField.setText(query3.text.toString())
            checkInternetAndFetchResponse(query3.text.toString(), responseText)
        }
    }

    private fun checkInternetAndFetchResponse(userInput: String, responseText: TextView) {
        if (!isInternetAvailable()) {
            showNoInternetPopup()
        } else {
            fetchAIResponse(userInput, responseText)
        }
    }

    private fun fetchAIResponse(userInput: String, responseText: TextView) {
        thread {
            try {
                val client = OkHttpClient()
                val json = JSONObject()

                json.put("model", "gpt-4")
                json.put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "You are a cutting-edge AI assistant for a library. Provide answers based on available books and equipment.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", userInput)
                    })
                })
                json.put("max_tokens", 150)
                json.put("temperature", 0.7)

                val body = RequestBody.create("application/json".toMediaType(), json.toString())
                val request = Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (responseBody != null) {
                    val jsonResponse = JSONObject(responseBody)

                    if (jsonResponse.has("error")) {
                        val errorMessage = jsonResponse.optJSONObject("error")?.optString("message", "Unknown error")
                        runOnUiThread {
                            responseText.text = "API Error: $errorMessage"
                        }
                    } else {
                        val aiResponse = jsonResponse.optJSONArray("choices")
                            ?.optJSONObject(0)
                            ?.optJSONObject("message")
                            ?.optString("content", "No response") ?: "No response"

                        runOnUiThread {
                            responseText.text = aiResponse.trim()
                        }
                    }
                } else {
                    runOnUiThread {
                        responseText.text = "Error: Empty response from API"
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    responseText.text = "Error: ${e.message}"
                }
            }
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showNoInternetPopup() {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle("No Internet Connection")
                .setMessage("Please check your internet connection and try again.")
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }
}
