package com.example.lumoslibrary.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.example.lumoslibrary.Audio
import com.example.lumoslibrary.CheckedOutItem
import com.example.lumoslibrary.CurrentSession
import com.example.lumoslibrary.Item
import com.example.lumoslibrary.R
import com.example.lumoslibrary.SearchInventory
import com.example.lumoslibrary.User
import com.example.lumoslibrary.UserData
import com.example.lumoslibrary.setOnClickListener
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/* Return confirmation page that displays items rented w/ Confetti
*  Updates json to reflect that user returned items */
class ReturnConfirmationActivity : AppCompatActivity() {

    private val audio: Audio = Audio()
    private lateinit var konfettiView: KonfettiView
    private lateinit var searchInventory: SearchInventory
    private lateinit var returnedItemsContainer: LinearLayout
    private lateinit var checkedOutItemsContainer: LinearLayout

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timeoutRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_confirmation)
        konfettiView = findViewById(R.id.returnConf_konfettiView)

        // Initialize searchInventory with the correct JSON file name
        searchInventory = SearchInventory(this, "items.json")

        returnedItemsContainer = findViewById(R.id.returned_items_list)
        checkedOutItemsContainer = findViewById(R.id.checked_out_items_list)

//        // Get the current user based on the userID from CurrentSession
        val currentUser: User? = UserData(this, "users.json").searchByUserId(CurrentSession.userID)

        if (currentUser != null) {
            // Get scanned items (from ReturnScanActivity). If null, assign an empty list.
            val scannedItems = CurrentSession.checkedOut ?: listOf()

            Log.d(TAG, "ReturnConfirmationAct - onCreate: after getting current user, these are the items that were just returned: ")
            for (e in scannedItems) {
                Log.d(TAG, "-----${e.title}\n")
            }

            // Display the items returned (scanned items)
            displayReturnedItems(scannedItems)

            if (currentUser != null) {
                Log.d(
                    TAG, "*** THIS IS THE CURRENT USER's currently checked out items: \n" +
                        "${currentUser.checkedOutItems}")
            }

            // Get the list of all checked-out items
            val currentlyCheckedOutItems = currentUser.checkedOutItems
                .map { checkedOutItem ->
                    // Get full item details based on name (title)
                    getItemByTitle(checkedOutItem.name)
                }

            // Filter out any null items from the list
            val remainingItems = currentlyCheckedOutItems.filterNotNull()

            // Find the TextView
            val checkedOutTextView: TextView = findViewById(R.id.returnConf_text_checked)

            // Assuming remainingItems is a List or Collection of checked-out items
            if (remainingItems.isEmpty()) {
                checkedOutTextView.text = "No other items currently checked out"
            } else {
                checkedOutTextView.text = "Currently checked out:"
            }


            // Optionally display the remaining items (not returned)
            displayCheckedOutItems(remainingItems)
        } else {
            Log.e(TAG, "User data not found!")
        }

        val userData = UserData(this, "users.json")
        val currentUserId = CurrentSession.userID  // Get current user ID

        val newCheckedOutItems = CurrentSession.checkedOut?.map { item ->
            CheckedOutItem(item.title, isLate = if (item.security_deposit == 0) false else Random.nextBoolean())
        } ?: emptyList()

        userData.removeItem(currentUserId, newCheckedOutItems)

        val button = findViewById<AppCompatButton>(R.id.returnConf_button)

        button.setOnClickListener(1000L){
            audio.playClickAudio(this)
            // Cancel the timeout when the user click on confirm button
            cancelTimeout()
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
        }
        audio.playConfirmationCompleteAudio(this)

        val party = Party(
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
        )

        konfettiView.start(party)

        // Set up timeout runnable to navigate after 1 minute
        timeoutRunnable = Runnable {
            // Navigate to the next activity after the timeout
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Start the timeout countdown
        handler.postDelayed(timeoutRunnable, TimeUnit.MINUTES.toMillis(1))
    }

    private fun cancelTimeout() {
        handler.removeCallbacks(timeoutRunnable)
    }

    // Function to display items that have been returned (scanned for return)
    private fun displayReturnedItems(scannedItems: List<Item>) {
        runOnUiThread {
            returnedItemsContainer.removeAllViews()
            val inflater = LayoutInflater.from(this)

            for (item in scannedItems) {
                val itemView = inflater.inflate(R.layout.item_returned_card, returnedItemsContainer, false)

                // Set item image
                val imageView = itemView.findViewById<ImageView>(R.id.card_image)
                val imageResId = resources.getIdentifier(item.image.replace(".jpg", ""), "drawable", packageName)
                if (imageResId != 0) {
                    imageView.setImageResource(imageResId)
                }

                // Set text fields
                itemView.findViewById<TextView>(R.id.card_field1).text = item.itemType
                itemView.findViewById<TextView>(R.id.card_field2).text = item.title
                itemView.findViewById<TextView>(R.id.card_field3).text = item.location

                // Add the item view to the container
                returnedItemsContainer.addView(itemView)
            }
        }
    }

    // Function to display items that are still checked out (not returned)
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

    // Helper function to get Item by its title
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

    override fun onDestroy() {
        super.onDestroy()
        audio.destroy()
        cancelTimeout()
    }

    companion object {
        private const val TAG = "ReturnConfirmationActivity"
    }
}