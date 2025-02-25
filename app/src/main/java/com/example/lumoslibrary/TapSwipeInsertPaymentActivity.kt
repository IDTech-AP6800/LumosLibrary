package com.example.lumoslibrary

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class TapSwipeInsertPaymentActivity : AppCompatActivity() {

    private lateinit var insertCardOption: LinearLayout
    private lateinit var swipeCardOption: LinearLayout
    private lateinit var tapCardOption: LinearLayout
    private var selectedPaymentMethod: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tap_swipe_insert_payment)
        BackButton(this)

        insertCardOption = findViewById(R.id.insert_card_option)
        swipeCardOption = findViewById(R.id.swipe_card_option)
        tapCardOption = findViewById(R.id.tap_card_option)

        insertCardOption.setOnClickListener { selectPaymentMethod(insertCardOption, "Insert") }
        swipeCardOption.setOnClickListener { selectPaymentMethod(swipeCardOption, "Swipe") }
        tapCardOption.setOnClickListener { selectPaymentMethod(tapCardOption, "Tap") }
    }

    private fun selectPaymentMethod(selectedView: LinearLayout, method: String) {
        // Reset all options to default color
        resetAllOptions()

        // Change selected option's background (active state)
        selectedView.alpha = 1.0f
        selectedPaymentMethod = method
        Log.d("DEBUG", "Selected payment option: ${selectedPaymentMethod}")
    }

    private fun resetAllOptions() {
        // Dim the unselected options
        insertCardOption.alpha = 0.5f
        swipeCardOption.alpha = 0.5f
        tapCardOption.alpha = 0.5f
    }
}
