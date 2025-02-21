package com.example.lumoslibrary

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import kotlin.concurrent.thread

class HelpPageActivity : AppCompatActivity() {
    private val API_KEY = ""
    private val API_URL = "https://api.openai.com/v1/completions" // OpenAI API endpoint

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
                json.put("model", "text-davinci-003")
                json.put("prompt", userInput)
                json.put("max_tokens", 150)
                json.put("temperature", 0.7)

                val body = RequestBody.create("application/json".toMediaTypeOrNull(), json.toString())
                val request = Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer $API_KEY")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                val jsonResponse = JSONObject(responseBody ?: "{}")
                val aiResponse = jsonResponse.optJSONArray("choices")?.optJSONObject(0)?.optString("text", "No response") ?: "No response"

                runOnUiThread {
                    responseText.text = aiResponse.trim()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    responseText.text = "Error: ${e.message}"
                }
            }
        }
    }
}
