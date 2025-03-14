package com.example.lumoslibrary.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.lumoslibrary.BackButton
import com.example.lumoslibrary.CurrentSession
import com.example.lumoslibrary.Item
import com.example.lumoslibrary.R
import com.example.lumoslibrary.SearchInventory
import com.example.lumoslibrary.User
import com.example.lumoslibrary.UserData

class UserItemsCheckedOutActivity : AppCompatActivity() {

    private lateinit var currentUser: User
    private lateinit var searchInventory: SearchInventory
    private lateinit var checkedOutItemsContainer: LinearLayout
    private lateinit var continueButton: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_items_checked_out)
        BackButton(this)

        checkedOutItemsContainer = findViewById(R.id.checked_items_list)

        continueButton = findViewById(R.id.continue_button)

        // Initialize searchInventory with the correct JSON file name
        searchInventory = SearchInventory(this, "items.json")

        val user = UserData(this, "users.json").searchByUserId(CurrentSession.userID)
        if (user != null) {
            currentUser = user
            Log.d(TAG, "** UserItemsCheckedOutActivity - onCreate. HERE is USER ${currentUser.userId}'s checkedOutItems: \n")
            for (a in currentUser.checkedOutItems) {
                Log.d(TAG, ">>>> ${a.name}\n")
            }
        } else {
            Log.e(TAG, "User not found in users.json for ID: ${CurrentSession.userID}")
            // Handle the missing user case (e.g., show an error message or navigate back)
        }

        // Get the list of all checked-out items
        val currentlyCheckedOutItems = currentUser.checkedOutItems
            .map { checkedOutItem -> getItemByTitle(checkedOutItem.name) } // Get full item details based on name (title)
            .filterNotNull() // Filter out any null items from the list

        // Display currently checked out items
        displayCheckedOutItems(currentlyCheckedOutItems)


        continueButton.setOnClickListener {
            val intent = Intent(this, ReturnScanActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayCheckedOutItems(checkedOutItems: List<Item>) {
        runOnUiThread {
            checkedOutItemsContainer.removeAllViews()
            val inflater = LayoutInflater.from(this)

            for (item in checkedOutItems) {
                val itemView = inflater.inflate(R.layout.item_currently_checked_out, checkedOutItemsContainer, false)

                // Set item image
                val imageView = itemView.findViewById<ImageView>(R.id.card_image)
                val imageResId = resources.getIdentifier(item.image.replace(".jpg", ""), "drawable", packageName)
                if (imageResId != 0) {
                    imageView.setImageResource(imageResId)
                }

                // Set text fields
                itemView.findViewById<TextView>(R.id.card_field1).text = item.itemType
                itemView.findViewById<TextView>(R.id.card_field2).text = item.title

                // Set loan period (Assume 14 days for books, 3 days for equipment)
                val loanPeriod = if (item.itemType == "book") "Loan Period: 14 days" else "Loan Period: 3 days"
                itemView.findViewById<TextView>(R.id.card_field3).text = loanPeriod

                // Set due date
                val dueDateTextView = itemView.findViewById<TextView>(R.id.card_field4)
                val dueDate = calculateDueDate(item.itemType)
                dueDateTextView.text = "Due: $dueDate"

                // Change due date text color based on item type
                val colorRes = if (item.itemType == "book") R.color.green else R.color.orange
                dueDateTextView.setTextColor(ContextCompat.getColor(this, colorRes))

                // Add the item view to the container
                checkedOutItemsContainer.addView(itemView)
            }
        }
    }

    private fun getItemByTitle(title: String): Item? {
        val allItems = searchInventory.getItems() // Load all items from the JSON
        val foundItem = allItems.firstOrNull { it.title.trim().equals(title.trim(), ignoreCase = true) }

        if (foundItem == null) {
            Log.e(TAG, "Item not found for title: '$title'")  // Log the error
            return null  // Return null if the item is not found
        }

        return foundItem
    }

    // Calculate due date (Example: Adds 14 days for books, 3 for equipment)
    private fun calculateDueDate(itemType: String): String {
        val calendar = java.util.Calendar.getInstance()
        val daysToAdd = if (itemType == "book") 14 else 3
        calendar.add(java.util.Calendar.DAY_OF_YEAR, daysToAdd)
        val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
        return sdf.format(calendar.time)
    }

    companion object {
        private const val TAG = "UserItemsCheckedOutActivity"
    }

}