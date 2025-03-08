package com.example.lumoslibrary

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import com.idtech.zsdk_client.CancelTransactionAsync
import com.idtech.zsdk_client.Client
import com.idtech.zsdk_client.GetDevicesAsync
import com.idtech.zsdk_client.SetAutoAuthenticateAsync
import com.idtech.zsdk_client.SetAutoCompleteAsync
import com.idtech.zsdk_client.StartTransactionAsync
import com.idtech.zsdk_client.StartTransactionResponseData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*This class handles all payment types at once (excluding qr)
* Uses code that we used for both ParkBuddy and AutoSpa */
class TapSwipeInsertPaymentActivity : AppCompatActivity() {

    private lateinit var insertCardOption: LinearLayout
    private lateinit var swipeCardOption: LinearLayout
    private lateinit var tapCardOption: LinearLayout
    private lateinit var qrCodeButton: ImageButton
    private var selectedPaymentMethod: String? = null
    private val audio: Audio = Audio()
    private lateinit var backButton: BackButton

    private var devices: List<String> = emptyList()
    private var connectedDeviceId: String? = null
    //Start w/MSR
    private var currPayment: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tap_swipe_insert_payment)
        backButton = BackButton(this)

        val totalDueTextView: TextView = findViewById(R.id.heading)
        totalDueTextView.text = "Total Due: $${String.format("%.2f", RentSession.totalDue)}"

        insertCardOption = findViewById(R.id.insert_card_option)
        swipeCardOption = findViewById(R.id.swipe_card_option)
        tapCardOption = findViewById(R.id.tap_card_option)
        qrCodeButton = findViewById(R.id.qr_code_button)

        //First time select payment method to be swipe
        selectPaymentMethod(swipeCardOption, "Swipe")

        // This area changes the UI + The calls to the ZSDK
        // 1 = MSR, 2 = CTLS, 4 = EMV
        insertCardOption.setOnClickListener(1000L) {
            audio.playClickAudio(this)
            selectPaymentMethod(insertCardOption, "Insert")
            cancelTransaction() // Need to test this
            currPayment = 4
            startTransaction(currPayment)
        }
        swipeCardOption.setOnClickListener(1000L) {
            audio.playClickAudio(this)
            selectPaymentMethod(swipeCardOption, "Swipe")
            cancelTransaction()
            currPayment = 1
            startTransaction(currPayment)
        }
        tapCardOption.setOnClickListener(1000L) {
            audio.playClickAudio(this)
            selectPaymentMethod(tapCardOption, "Tap")
            cancelTransaction()
            currPayment = 2
            startTransaction(currPayment)
        }

        // qrCodeButton Navigates to pay with scanner
        qrCodeButton.setOnClickListener(1000L){
            audio.playClickAudio(this)
            cancelTransaction()
            val intent = Intent(this, QrCodeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun selectPaymentMethod(selectedView: LinearLayout, method: String) {
        // Reset all options to default color
        resetAllOptions()

        // Change selected option's background (active state)
        selectedView.alpha = 1.0f
        selectedPaymentMethod = method
        Log.d("DEBUG", "Selected payment option: $selectedPaymentMethod")
    }

    private fun resetAllOptions() {
        // Dim the unselected options
        // Default is MSR
        insertCardOption.alpha = 0.5f
        swipeCardOption.alpha = 0.5f
        tapCardOption.alpha = 0.5f
    }

    private fun startTransaction(transInterfaceType: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            // Enumerate devices
            if (enumerateDevices()) {
                connectedDeviceId = devices.firstOrNull()
            }

            connectedDeviceId?.let { deviceId ->
                // Enable auto-authenticate
                Client.SetAutoAuthenticateAsync(deviceId, true)
                    .waitForCompletionWithTimeout(1000)

                // Enable auto-complete
                Client.SetAutoCompleteAsync(deviceId, true)
                    .waitForCompletionWithTimeout(1000)

                // Define transaction parameters
                val amount = 0.1
                val amountOther = 0.0
                val transType: UByte = 0u
                val transTimeout: UByte = 100u

                Log.d(TAG,"Gonna start command")
                // Start the transaction
                val startTransCmd = Client.StartTransactionAsync(
                    deviceId,
                    amount, amountOther, transType, transTimeout,
                    transInterfaceType.toUByte(), 3000
                )

                startTransCmd.waitForCompletion()

                // Check the transaction status
                val resultData: StartTransactionResponseData? = startTransCmd.getResultData()
                if (resultData != null) {
                    // If transaction succeeds, navigate to ProcessingActivity
                    withContext(Dispatchers.Main) {
                        navigateToProcessingActivity()
                    }
                }
            }
        }
    }

    private fun cancelTransaction() {
        connectedDeviceId?.let { connectedDeviceId ->
            CoroutineScope(Dispatchers.IO).launch {
                Client.CancelTransactionAsync(connectedDeviceId)
                Log.d(TAG, "Transaction canceled successfully.")
            }
        } ?: Log.d(TAG, "No connected device ID available for canceling transaction.")
    }

    private suspend fun enumerateDevices(): Boolean {
        return runCatching {
            val cmd = Client.GetDevicesAsync()
            cmd.waitForCompletionWithTimeout(3000)
            devices = cmd.devices
            devices.isNotEmpty()
        }.getOrDefault(false)
    }

    private fun navigateToProcessingActivity() {
        // After paying, you go to confirm
        val intent = Intent(this, RentConfirmationActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onPause() {
        super.onPause()

        //When the activity is paused, cancel transaction
        cancelTransaction()
    }

    override fun onResume() {
        super.onResume()

        //When the activity is resumed, start the transaction again
        startTransaction(currPayment)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTransaction()
        audio.destroy()
        backButton.onDestroy()
    }

    companion object {
        private const val TAG = "PaymentActivity" // Log tag
    }
}
