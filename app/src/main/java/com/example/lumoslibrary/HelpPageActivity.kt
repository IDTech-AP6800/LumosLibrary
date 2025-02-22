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
                        put("content", "You are a Library assistant. These are books and items that you have in your library [\n" +
                                "  {\n" +
                                "    \"name\": \"Introduction to Database Systems\",\n" +
                                "    \"category\": \"textbook\",\n" +
                                "    \"author\": \"C.J. Date\",\n" +
                                "    \"location\": \"F3\",\n" +
                                "    \"amount\": 3,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"book\",\n" +
                                "    \"image\": \"book_introduction_to_database_systems.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Calculus: Early Transcendentals\",\n" +
                                "    \"category\": \"textbook\",\n" +
                                "    \"author\": \"James Stewart\",\n" +
                                "    \"location\": \"H2\",\n" +
                                "    \"amount\": 2,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"book\",\n" +
                                "    \"image\": \"book_calculus_early_transcendentals.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Harry Potter and the Philosopher's Stone\",\n" +
                                "    \"category\": \"fantasy\",\n" +
                                "    \"author\": \"J. K. Rowling\",\n" +
                                "    \"location\": \"J1\",\n" +
                                "    \"amount\": 1,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"book\",\n" +
                                "    \"image\": \"book_harry_potter_philosophers_stone.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Advanced Potion Making\",\n" +
                                "    \"category\": \"textbook\",\n" +
                                "    \"author\": \"Libatius Borage\",\n" +
                                "    \"location\": \"C5\",\n" +
                                "    \"amount\": 1,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"book\",\n" +
                                "    \"image\": \"book_advanced_potion_making.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Project Hail Mary\",\n" +
                                "    \"category\": \"fiction\",\n" +
                                "    \"author\": \"Andy Weir\",\n" +
                                "    \"location\": \"E3\",\n" +
                                "    \"amount\": 4,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"book\",\n" +
                                "    \"image\": \"book_project_hail_mary.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Sapiens: A Brief History of Humankind\",\n" +
                                "    \"category\": \"non-fiction\",\n" +
                                "    \"author\": \"Yuval Noah Harari\",\n" +
                                "    \"location\": \"D3\",\n" +
                                "    \"amount\": 3,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"book\",\n" +
                                "    \"image\": \"book_sapiens_brief_history_of_humankind.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"The Midnight Library\",\n" +
                                "    \"category\": \"fiction\",\n" +
                                "    \"author\": \"Matt Haig\",\n" +
                                "    \"location\": \"E4\",\n" +
                                "    \"amount\": 5,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"book\",\n" +
                                "    \"image\": \"book_midnight_library.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Thinking, Fast and Slow\",\n" +
                                "    \"category\": \"psychology\",\n" +
                                "    \"author\": \"Daniel Kahneman\",\n" +
                                "    \"location\": \"D4\",\n" +
                                "    \"amount\": 2,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"book\",\n" +
                                "    \"image\": \"book_thinking_fast_and_slow.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Astrophysics for People in a Hurry\",\n" +
                                "    \"category\": \"science\",\n" +
                                "    \"author\": \"Neil deGrasse Tyson\",\n" +
                                "    \"location\": \"G3\",\n" +
                                "    \"amount\": 3,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"book\",\n" +
                                "    \"image\": \"book_astrophysics_for_people_in_a_hurry.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"The Silent Patient\",\n" +
                                "    \"category\": \"mystery\",\n" +
                                "    \"author\": \"Alex Michaelides\",\n" +
                                "    \"location\": \"D5\",\n" +
                                "    \"amount\": 2,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"book\",\n" +
                                "    \"image\": \"book_the_silent_patient.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Apple M2 Macbook\",\n" +
                                "    \"category\": \"laptop\",\n" +
                                "    \"description\": \"Apple MacBook with M2 chip, 13-inch Retina display, 256GB SSD, and 8GB RAM.\",\n" +
                                "    \"amount\": 2,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"equipment\",\n" +
                                "    \"image\": \"equipment_macbook_m2.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"HP Chromebook\",\n" +
                                "    \"category\": \"laptop\",\n" +
                                "    \"description\": \"HP Chromebook with Intel Celeron processor, 14-inch HD display, and 64GB eMMC storage.\",\n" +
                                "    \"amount\": 2,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"equipment\",\n" +
                                "    \"image\": \"equipment_hp_chromebook.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Nintendo Switch\",\n" +
                                "    \"category\": \"game console\",\n" +
                                "    \"description\": \"Portable gaming console with detachable Joy-Con controllers and a 6.2-inch LCD screen.\",\n" +
                                "    \"amount\": 1,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"equipment\",\n" +
                                "    \"image\": \"equipment_nintendo_switch.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Sony PlayStation 4\",\n" +
                                "    \"category\": \"game console\",\n" +
                                "    \"description\": \"Next-gen gaming console with ultra-fast SSD, ray tracing support, and a DualSense controller.\",\n" +
                                "    \"amount\": 0,\n" +
                                "    \"isAvailable\": false,\n" +
                                "    \"tag\": \"equipment\",\n" +
                                "    \"image\": \"equipment_sony_playstation_4.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Microsoft Xbox Series X\",\n" +
                                "    \"category\": \"game console\",\n" +
                                "    \"description\": \"Powerful gaming console with 4K gaming capabilities, 1TB SSD, and Xbox Game Pass support.\",\n" +
                                "    \"amount\": 1,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"equipment\",\n" +
                                "    \"image\": \"equipment_xbox_series_x.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Logitech MX Master 3S\",\n" +
                                "    \"category\": \"accessory\",\n" +
                                "    \"description\": \"Ergonomic wireless mouse with fast scrolling and multi-device support.\",\n" +
                                "    \"amount\": 3,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"equipment\",\n" +
                                "    \"image\": \"equipment_logitech_mx_master_3s.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Apple iPad Air\",\n" +
                                "    \"category\": \"tablet\",\n" +
                                "    \"description\": \"10.9-inch Liquid Retina display, M1 chip, and Apple Pencil (2nd gen) support.\",\n" +
                                "    \"amount\": 2,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"equipment\",\n" +
                                "    \"image\": \"equipment_apple_ipad_air.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Samsung Galaxy Tab S8\",\n" +
                                "    \"category\": \"tablet\",\n" +
                                "    \"description\": \"11-inch LCD display, Snapdragon 8 Gen 1 processor, and S Pen support.\",\n" +
                                "    \"amount\": 3,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"equipment\",\n" +
                                "    \"image\": \"equipment_samsung_galaxy_tab_s8.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Logitech K780\",\n" +
                                "    \"category\": \"accessory\",\n" +
                                "    \"description\": \"Wireless multi-device keyboard with quiet keys and an integrated phone and tablet stand.\",\n" +
                                "    \"amount\": 2,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"equipment\",\n" +
                                "    \"image\": \"equipment_logitech_k780.jpg\"\n" +
                                "  },\n" +
                                "  {\n" +
                                "    \"name\": \"Sony WH-1000XM5\",\n" +
                                "    \"category\": \"accessory\",\n" +
                                "    \"description\": \"Wireless noise-canceling headphones with superior sound quality and long battery life.\",\n" +
                                "    \"amount\": 3,\n" +
                                "    \"isAvailable\": true,\n" +
                                "    \"tag\": \"equipment\",\n" +
                                "    \"image\": \"equipment_sony_wh_1000xm5.jpg\"\n" +
                                "  }\n" +
                                "] Answer questions about them and direct people to them." +
                                "refer toyourself as a cutting edge AI Assistant")
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
