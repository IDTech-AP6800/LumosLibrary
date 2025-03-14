package com.example.lumoslibrary.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.lumoslibrary.CurrentSession
import com.example.lumoslibrary.HelpButton
import com.example.lumoslibrary.Item
import com.example.lumoslibrary.R
import com.example.lumoslibrary.SearchInventory

class LandingPageActivity : AppCompatActivity() {

    private lateinit var rentButton: Button
    private lateinit var returnButton: Button
    private lateinit var searchButton: Button
    private lateinit var itemsFromJson: List<Item> // To hold the items loaded from the JSON file

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)

        searchButton = findViewById(R.id.search_bar)

        rentButton = findViewById(R.id.rent_button)
        returnButton = findViewById(R.id.return_button)

        HelpButton(this)

        // Use SearchInventory to load items
        val searchInventory = SearchInventory(this, "items.json")
        itemsFromJson = searchInventory.getItems() // Get the items list

        // Set up the onClick listener 1
        val advancedPotionMakingFrame = findViewById<FrameLayout>(R.id.plus_1) as ImageView
        advancedPotionMakingFrame.setOnClickListener {
            // Find the corresponding item in the list
            val item = itemsFromJson.find { it.title == "Advanced Potion Making" }

            // Show the popup
            item?.let { showItemPopup(it) }
        }

        // Set up the onClick listener 2
        val astrophysicsFrame = findViewById<FrameLayout>(R.id.plus_2) as ImageView
        astrophysicsFrame.setOnClickListener {
            // Find the corresponding item in the list
            val item = itemsFromJson.find { it.title == "Astrophysics for People in a Hurry" }

            // Show the popup
            item?.let { showItemPopup(it) }
        }

        // Set up the onClick listener 3
        val calculusFrame = findViewById<FrameLayout>(R.id.plus_3) as ImageView
        calculusFrame.setOnClickListener {
            // Find the corresponding item in the list
            val item = itemsFromJson.find { it.title == "Calculus: Early Transcendentals" }

            // Show the popup
            item?.let { showItemPopup(it) }
        }

        // Set up the onClick listener 4
        val harryPotterFrame = findViewById<FrameLayout>(R.id.plus_4) as ImageView
        harryPotterFrame.setOnClickListener {
            // Find the corresponding item in the list
            val item = itemsFromJson.find { it.title == "Harry Potter and the Philosopher's Stone" }

            // Show the popup
            item?.let { showItemPopup(it) }
        }

        // Set up the onClick listener 5
        val projectHailMaryFrame = findViewById<FrameLayout>(R.id.plus_5) as ImageView
        projectHailMaryFrame.setOnClickListener {
            // Find the corresponding item in the list
            val item = itemsFromJson.find { it.title == "Project Hail Mary" }

            // Show the popup
            item?.let { showItemPopup(it) }
        }

        // Set up the onClick listener 6
        val macbookFrame = findViewById<FrameLayout>(R.id.plus_eq_1) as ImageView
        macbookFrame.setOnClickListener {
            // Find the corresponding item in the list
            val item = itemsFromJson.find { it.title == "Apple M2 Macbook" }

            // Show the popup
            item?.let { showItemPopup(it) }
        }

        // Set up the onClick listener 7
        val ipadFrame = findViewById<FrameLayout>(R.id.plus_eq_2) as ImageView
        ipadFrame.setOnClickListener {
            // Find the corresponding item in the list
            val item = itemsFromJson.find { it.title == "Apple iPad Air" }

            // Show the popup
            item?.let { showItemPopup(it) }
        }

        // Set up the onClick listener 8
        val ps4Frame = findViewById<FrameLayout>(R.id.plus_eq_3) as ImageView
        ps4Frame.setOnClickListener {
            // Find the corresponding item in the list
            val item = itemsFromJson.find { it.title == "Sony PlayStation 4" }

            // Show the popup
            item?.let { showItemPopup(it) }
        }

        // Set up the onClick listener 9
        val nintendoSwitchFrame = findViewById<FrameLayout>(R.id.plus_eq_4) as ImageView
        nintendoSwitchFrame.setOnClickListener {
            // Find the corresponding item in the list
            val item = itemsFromJson.find { it.title == "Nintendo Switch" }

            // Show the popup
            item?.let { showItemPopup(it) }
        }

        // Set up the onClick listener 10
        val sonyXM5Frame = findViewById<FrameLayout>(R.id.plus_eq_5) as ImageView
        sonyXM5Frame.setOnClickListener {
            // Find the corresponding item in the list
            val item = itemsFromJson.find { it.title == "Sony WH-1000XM5" }

            // Show the popup
            item?.let { showItemPopup(it) }
        }

        searchButton.setOnClickListener{startActivity(Intent(this, SearchResultsActivity::class.java))}

        rentButton.setOnClickListener { navigateToLogin(1) }
        returnButton.setOnClickListener { navigateToLogin(2) }

    }

    // Function to show the item details in a popup
    private fun showItemPopup(item: Item) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.item_popup, null)

        val itemTitle = dialogView.findViewById<TextView>(R.id.popup_title)
        val itemDesc = dialogView.findViewById<TextView>(R.id.popup_description)
        val itemLocation = dialogView.findViewById<TextView>(R.id.popup_location)
        val itemImage = dialogView.findViewById<ImageView>(R.id.popup_image)

        val closeButton = dialogView.findViewById<ImageButton>(R.id.close_icon)

        // Set item details
        itemTitle.text = item.title
        itemDesc.text = item.description
        itemLocation.text = "Location: ${item.location}"

        // Load image dynamically using the image name
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

        closeButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun navigateToLogin(state: Int) {
        CurrentSession.state = state
        startActivity(Intent(this, LoginActivity::class.java))
    }

}