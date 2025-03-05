package com.example.lumoslibrary

import android.Manifest
import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat

class RentConfirmationActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_confirmation)

        val button = findViewById<AppCompatButton>(R.id.rentConf_button)

        val confirmedItemsContainer = findViewById<LinearLayout>(R.id.confirmation_item_container)

        val checkedOutItems = CurrentSession.checkedOut

        if (checkedOutItems.isNullOrEmpty()) {
            Log.d(TAG, "No items in CurrentSession.checkedOut!")
        } else {
            Log.d(TAG, "** Displaying checked-out items **")
            val inflater = LayoutInflater.from(this)

            for (item in checkedOutItems) {
                val itemView = inflater.inflate(R.layout.item_confirmed_card, confirmedItemsContainer, false)

                // Set category (e.g., "Book" or "Equipment")
                itemView.findViewById<TextView>(R.id.card_field1).text = item.itemType

                // Set title
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

                // Set item image
                val imageView = itemView.findViewById<ImageView>(R.id.card_image)
                val imageResId = resources.getIdentifier(item.image.replace(".jpg", ""), "drawable", packageName)
                if (imageResId != 0) {
                    imageView.setImageResource(imageResId)
                }

                confirmedItemsContainer.addView(itemView)
            }
        }

        button.setOnClickListener{
//            val intent = Intent(this, MainActivity::class.java)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        playConfirmationCompleteAudio()

    }

    private fun playConfirmationCompleteAudio() {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, R.raw.short_success)
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
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
        private const val TAG = "RentConfirmationActivity"
    }
}