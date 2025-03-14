package com.example.lumoslibrary

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.commons.text.similarity.JaroWinklerSimilarity
import java.io.IOException
import java.util.Locale

/* Searches the Inventory using fuzzy search from the items.json */
class SearchInventory(private val context: Context, private val fileName: String) {
    private var items: List<Item>

    init {
        Log.d(TAG, "Initializing SearchInventory...")
        items = parseJsonToModel(readInventory())
    }

    // Public method to access the items list
    fun getItems(): List<Item> {
        items = parseJsonToModel(readInventory())
        return items
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
                item.itemType,
                item.isbn13,
                item.description
            ).any { field ->
                val fieldLower = field.lowercase(Locale.getDefault())
                jw.apply(searchQuery, fieldLower) >= similarityThreshold
            }
        }
    }

    // Searches the inventory and returns the item based on ISBN-13
    fun searchByISBN(query: String): Item? {
        Log.d(TAG, "Searching by ISBN: $query")
        return items.find { it.isbn13 == query }
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
