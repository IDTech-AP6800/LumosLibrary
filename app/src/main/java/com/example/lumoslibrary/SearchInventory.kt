package com.example.lumoslibrary

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import java.io.IOException
import java.util.Locale

class SearchInventory(private val context: Context, private val fileName: String) {
    private var items: List<Item>

    init {
        Log.d(TAG, "Initializing SearchInventory...")
        items = parseJsonToModel(readInventory())
    }

    // Searches the inventory using fuzzy matching
    fun search(query: String): List<Item> {
        Log.d(TAG, "Fuzzy Searching for: $query")
        val searchQuery = query.lowercase(Locale.getDefault())
        val similarityThreshold = 0.75  // higher = stricter

        val jw = JaroWinklerSimilarity()

        return items.filter { item ->
            listOfNotNull(
                item.title,
                item.category,
                item.author,
                item.location,
                item.tag,
                item.isbn13,
                item.description
            ).any { field ->
                val fieldLower = field.lowercase(Locale.getDefault())
                jw.apply(searchQuery, fieldLower) >= similarityThreshold
            }
        }
    }

    // Reads JSON file from assets folder
    private fun readInventory(): String {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Log.e(TAG, "Error reading inventory JSON file", e)
            "[]"
        }
    }

    // Parses JSON string to a list of Item objects
    private fun parseJsonToModel(jsonString: String): List<Item> {
        val gson = Gson()
        return gson.fromJson(jsonString, object : TypeToken<List<Item>>() {}.type)
    }

    companion object {
        private const val TAG = "SearchInventory"
    }
}
