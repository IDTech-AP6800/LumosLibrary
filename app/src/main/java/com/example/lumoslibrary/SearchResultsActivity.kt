package com.example.lumoslibrary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var searchInventory: SearchInventory
    private lateinit var itemCardContainer: LinearLayout
    private lateinit var searchQueryEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        HelpButton(this)
        BackButton(this)

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
                    itemView.findViewById<TextView>(R.id.card_field4).text = "Aisle:  ${item.location}"
                }
                else if (item.itemType == "equipment") {
                    itemView.findViewById<TextView>(R.id.card_field1).text = item.itemType
                    itemView.findViewById<TextView>(R.id.card_field2).text = item.title
                    itemView.findViewById<TextView>(R.id.card_field3).text = "Security Deposit: $${item.security_deposit.toString()}"
                    itemView.findViewById<TextView>(R.id.card_field4).text = item.location
                }

                itemCardContainer.addView(itemView)
            }
        }
    }
}
