package com.example.lumoslibrary

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
        val sendButton: Button = findViewById(R.id.send_button)
        val responseText: TextView = findViewById(R.id.response_text)

        sendButton.setOnClickListener {
            val userInput = inputField.text.toString()
            inputField.text.clear() // Clear input field on submit
            fetchAIResponse(userInput, responseText)
        }
    }

    private fun fetchAIResponse(userInput: String, responseText: TextView) {
        thread {
            try {
                val client = OkHttpClient()
                val json = JSONObject()

                // Use `messages` array for chat-based models
                json.put("model", "gpt-4")
                json.put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "You are a helpful assistant.")
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
}
