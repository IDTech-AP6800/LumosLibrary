package com.example.lumoslibrary

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.util.Locale

/*Searches the Inventory in the items json
* With help from:
* https://medium.com/@cpvasani48/reading-json-data-from-the-android-asset-folder-and-storing-in-a-model-622cc4d3f3c1
* */
class SearchInventory(private val context: Context, private val fileName: String) {
    private var items: List<Item> // Now using Item from Item.kt

    init {
        Log.d(TAG, "Initializing SearchInventory...")
        items = parseJsonToModel(readInventory())
    }
    //Searches the inventory and returns the item based on the title
    fun search(query: String): List<Item> {
        Log.d(TAG, "Searching for: $query")
        val searchQuery = query.lowercase(Locale.getDefault())
        return items.filter {
            it.title.lowercase(Locale.getDefault()).contains(searchQuery) ||
                    it.category.lowercase(Locale.getDefault()).contains(searchQuery) ||
                    it.author?.lowercase(Locale.getDefault())?.contains(searchQuery) ?: false ||
                    it.location.lowercase(Locale.getDefault()).contains(searchQuery) ||
                    it.tag.lowercase(Locale.getDefault()).contains(searchQuery) ||
                    it.isbn13?.lowercase(Locale.getDefault())?.contains(searchQuery) ?: false ||
                    it.description?.lowercase(Locale.getDefault())?.contains(searchQuery) ?: false
        }
    }

    // Searches the inventory and returns the item based on ISBN-13
    fun searchByISBN(query: String): Item? {
        Log.d(TAG, "Searching by ISBN: $query")
        return items.find { it.isbn13 == query }
    }


    //Reads the inventory
    private fun readInventory(): String {
        return try {
            context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            Log.e(TAG, "Error reading inventory JSON file", e)
            "[]"
        }
    }

    //Parses the inventory
    private fun parseJsonToModel(jsonString: String): List<Item> {
        val gson = Gson()
        return gson.fromJson(jsonString, object : TypeToken<List<Item>>() {}.type)
    }

    companion object {
        private const val TAG = "SearchInventory"
    }
}
