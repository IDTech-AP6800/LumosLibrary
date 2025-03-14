package com.example.lumoslibrary

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException

// Data class to represent checked out items
data class CheckedOutItem(
    val name: String,
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
    private var users: MutableList<User>

    init {
        Log.d(TAG, "Initializing SearchUsers ...")
        this.fileName = _fileName
        this.context = _context
        val rawJson = readUsersData()
        Log.d(TAG, "Loaded raw JSON: $rawJson")
        this.users = parseJsonToModel(readUsersData()).toMutableList()
    }

    // Searches for a user by their userId
    fun searchByUserId(query: String): User? {
        return users.find { it.userId == query }
    }
    fun addItem(userId: String, updatedItems: List<CheckedOutItem>) {
        val userIndex = users.indexOfFirst { it.userId == userId }
        val existingUser = users[userIndex]
        val existingItems = existingUser.checkedOutItems.map { it.name }.toSet()

        // Filter out duplicates
        val filteredNewItems = updatedItems.filter { it.name !in existingItems }

        if (filteredNewItems.isNotEmpty()) {
            // Append new items to the existing list
            val updatedUser = existingUser.copy(
                checkedOutItems = existingUser.checkedOutItems + filteredNewItems
            )
            users[userIndex] = updatedUser
            Log.d(TAG, "Added new checked-out items for User ID: $userId")
        } else {
            Log.d(TAG, "No new items added, all were already checked out")
        }
        saveUserDataToFile()
    }

    fun removeItem(userId: String, updatedItems: List<CheckedOutItem>) {
        val userIndex = users.indexOfFirst{ it.userId == userId}
        val existingUser = users[userIndex]
        val updatedNames = updatedItems.map {it.name}.toSet()


        // Remove new items in list
        val updatedUser = existingUser.copy(
            checkedOutItems = existingUser.checkedOutItems.filter{it.name !in updatedNames}
        )
        users[userIndex] = updatedUser
        saveUserDataToFile()
    }

    // Reads the users.json file
    private fun readUsersData(): String {
        val file = File(context.filesDir, fileName)
        return if (file.exists()) {
            file.readText()
        } else {
            "[]"
        }
    }

    // Parses JSON into a list of User objects
    private fun parseJsonToModel(jsonString: String): List<User> {
        val gson = Gson()
        return gson.fromJson(jsonString, object : TypeToken<List<User>>() {}.type)
    }

    private fun saveUserDataToFile() {
        val file = File(context.filesDir, fileName)
        file.writeText(Gson().toJson(users))

        users.forEach { user ->
            Log.d(TAG, "User ID: ${user.userId} Checked-out items: ${user.checkedOutItems}")
        }
    }

    companion object {
        private const val TAG = "UserData"
    }
}
