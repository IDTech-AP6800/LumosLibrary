package com.example.lumoslibrary

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/*Searches the Inventory in the items json
* With help from:
* https://medium.com/@cpvasani48/reading-json-data-from-the-android-asset-folder-and-storing-in-a-model-622cc4d3f3c1
* */
class SearchInventory(_context: Context, _fileName: String) {
    //Member Variable
    private var fileName: String
    private var context: Context
    private var items: List<Item>

    init{
        this.fileName = _fileName
        this.context = _context
        this.items = parseJsonToModel(readInventory())
    }

    // Searches the inventory and returns the item based on title
    // TODO: Add a way to search for more diff categories
    public fun search(query: String): List<Item> {
        val found = items.filter{it.title == query}
        return found
    }

    //Reads the inventory
    private fun readInventory(): String{
        return context.assets.open(fileName).bufferedReader().use {it.readText()}
    }

    //Parses inventory into a list
    private fun parseJsonToModel(jsonString: String): List<Item> {
        val gson = Gson()
        return gson.fromJson(jsonString, object : TypeToken<List<Item>>() {}.type)
    }
}