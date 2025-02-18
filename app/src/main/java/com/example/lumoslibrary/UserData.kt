package com.example.lumoslibrary

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Data class to represent checked out items
data class CheckedOutItem(
    val name: String,
    val isReturned: Boolean,
    val isLate: Boolean
)

// Data class to represent a User
data class User(
    val userId: String,
    val checkedOutItems: List<CheckedOutItem>
)

// Class to manage searching users
class UserData(_context: Context, _fileName: String) {
    private var fileName: String
    private var context: Context
    private var users: List<User>

    init {
        Log.d(TAG, "Initializing SearchUsers ...")
        this.fileName = _fileName
        this.context = _context
        this.users = parseJsonToModel(readUsersData())
    }

    // Searches for a user by their userId
    fun searchByUserId(query: String): User? {
        return users.find { it.userId == query }
    }

    // Reads the users.json file
    private fun readUsersData(): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    // Parses JSON into a list of User objects
    private fun parseJsonToModel(jsonString: String): List<User> {
        val gson = Gson()
        return gson.fromJson(jsonString, object : TypeToken<List<User>>() {}.type)
    }

    companion object {
        private const val TAG = "SearchUsers"
    }
}
