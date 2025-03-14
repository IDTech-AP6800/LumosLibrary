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
import com.example.lumoslibrary.Audio
import com.example.lumoslibrary.CheckedOutItem
import com.example.lumoslibrary.CurrentSession
import com.example.lumoslibrary.R
import com.example.lumoslibrary.UserData
import com.example.lumoslibrary.setOnClickListener
import kotlin.random.Random
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.util.concurrent.TimeUnit

/* Rent confirmation page that displays items rented w/ Confetti
*  Updates json to reflect new rental */
class RentConfirmationActivity : AppCompatActivity() {

    private val audio: Audio = Audio()
    private lateinit var viewKonfetti: KonfettiView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_confirmation)

        val button = findViewById<AppCompatButton>(R.id.rentConf_button)
        viewKonfetti = findViewById(R.id.rentConf_konfettiView)

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

        button.setOnClickListener(1000L) {
            val userData = UserData(this, "users.json")
            val currentUserId = CurrentSession.userID  // Get current user ID
            val newCheckedOutItems = CurrentSession.checkedOut?.map { item ->
                CheckedOutItem(item.title, isLate = if (item.security_deposit == 0) false else Random.nextBoolean())
            } ?: emptyList()

            userData.addItem(currentUserId, newCheckedOutItems)
            audio.playClickAudio(this)
            val intent = Intent(this, LandingPageActivity::class.java)
            startActivity(intent)
        }

        audio.playConfirmationCompleteAudio(this)

        val party = Party(
            emitter = Emitter(duration = 100, TimeUnit.MILLISECONDS).max(100)
        )

        viewKonfetti.start(party)
    }
    override fun onDestroy() {
        super.onDestroy()
        audio.destroy()
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