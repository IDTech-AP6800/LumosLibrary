package com.example.lumoslibrary.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lumoslibrary.BackButton
import com.example.lumoslibrary.HelpButton
import com.example.lumoslibrary.Item
import com.example.lumoslibrary.R
import com.example.lumoslibrary.SearchInventory
/* Displays the search results when user is looking for books/items */
class SearchResultsActivity : AppCompatActivity() {

    private lateinit var searchInventory: SearchInventory
    private lateinit var itemCardContainer: LinearLayout
    private lateinit var searchQueryEditText: EditText

    private lateinit var helpButton: HelpButton
    private lateinit var backButton: BackButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        helpButton = HelpButton(this)
        backButton = BackButton(this)

        // Initialize searchInventory and itemCardContainer
        searchInventory = SearchInventory(this, "items.json")
        itemCardContainer = findViewById(R.id.searched_item_card_containers) // Ensure this ID exists in your layout

        // Initialize the search EditText view
        searchQueryEditText = findViewById(R.id.search_bar)

        // Set OnEditorActionListener to the EditText to trigger search on pressing "Enter"
        searchQueryEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE) {
                // Trigger search when the user presses "Enter" or the "Search" action
                updateItemCards()  // Perform the search and update item cards
                true  // Return true to consume the action
            } else {
                false  // Return false to let the system handle other actions
            }
        }

    }

    private fun updateItemCards() {
        runOnUiThread {
            // Clear focus from the search bar before hiding the keyboard
            searchQueryEditText.clearFocus()

            // Hide the keyboard
            hideKeyboard(searchQueryEditText)

            itemCardContainer.removeAllViews()
            val inflater = LayoutInflater.from(this)

            // Get the search query from EditText
            val searchQuery = searchQueryEditText.text.toString()

            // Get the list of items
            val items = searchInventory.getItems()

            // Log or use the items list
            Log.d("SearchResultsActivity", "Items: $items")

            // Example of searching for an item by title
            val foundItems = searchInventory.search(searchQuery)

            // Log or use the foundItems
            Log.d("SearchResultsActivity", "Found Items: $foundItems")

            for (item in foundItems) {  // Now use `foundItems` based on the search query
                val itemView = inflater.inflate(R.layout.item_card_search_results, itemCardContainer, false)
                itemView.findViewById<TextView>(R.id.card_field2).text = item.title

                val imageView = itemView.findViewById<ImageView>(R.id.card_image)
                val imageResId = resources.getIdentifier(item.image.replace(".jpg", ""), "drawable", packageName)
                if (imageResId != 0) {
                    imageView.setImageResource(imageResId)
                }

                if (item.itemType == "book") {
                    itemView.findViewById<TextView>(R.id.card_field1).text = item.itemType
                    itemView.findViewById<TextView>(R.id.card_field2).text = item.title
                    itemView.findViewById<TextView>(R.id.card_field3).text = item.author
                    itemView.findViewById<TextView>(R.id.card_field4).text = item.location
                }
                else if (item.itemType == "equipment") {
                    itemView.findViewById<TextView>(R.id.card_field1).text = item.itemType
                    itemView.findViewById<TextView>(R.id.card_field2).text = item.title
                    itemView.findViewById<TextView>(R.id.card_field3).text = "Security Deposit: $${item.security_deposit.toString()}"
                    itemView.findViewById<TextView>(R.id.card_field4).text = item.location
                }

                // Set Click Listener to Open Popup
                itemView.setOnClickListener {
                    showItemPopup(item)
                }

                itemCardContainer.addView(itemView)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        helpButton.onDestroy()
        backButton.onDestroy()
    }

    private fun showItemPopup(item: Item) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.item_popup, null)

        val itemTitle = dialogView.findViewById<TextView>(R.id.popup_title)
        val itemDesc = dialogView.findViewById<TextView>(R.id.popup_description)
        val itemImage = dialogView.findViewById<ImageView>(R.id.popup_image)
        val itemLocation = dialogView.findViewById<TextView>(R.id.popup_location)

        val closeButton = dialogView.findViewById<ImageButton>(R.id.close_icon)

        // Set item details
        itemTitle.text = item.title
        itemDesc.text = item.description
        itemLocation.text = "Location: ${item.location}"
        val imageResId = resources.getIdentifier(item.image.replace(".jpg", ""), "drawable", packageName)
        if (imageResId != 0) {
            itemImage.setImageResource(imageResId)
        }

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()

        closeButton.setOnClickListener{
            dialog.dismiss()
        }


    }
    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
