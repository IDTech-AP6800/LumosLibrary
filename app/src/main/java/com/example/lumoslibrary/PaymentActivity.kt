package com.example.lumoslibrary

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.idtech.zsdk_client.CancelTransactionAsync
import com.idtech.zsdk_client.Client
import com.idtech.zsdk_client.GetDevicesAsync
import com.idtech.zsdk_client.SetAutoAuthenticateAsync
import com.idtech.zsdk_client.SetAutoCompleteAsync
import com.idtech.zsdk_client.StartMSRTransAsync
import com.idtech.zsdk_client.StartTransactionAsync
import com.idtech.zsdk_client.StartTransactionResponseData
import com.idtech.zsdk_client.api.StartEMVTransactionAsync
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*This class handles all payment types at once (excluding qr)
* Uses code that we used for both ParkBuddy and AutoSpa */
class PaymentActivity : AppCompatActivity() {

    private var devices: List<String> = emptyList()
    private var connectedDeviceId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        startEMVTransaction()

    }


    private fun startEMVTransaction() {
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
                val transT: UShort = 100u
                val transTime: Int = 100
                val transInterfaceType: Int = 1 // 4 for EMV (Insert)

                Log.d(TAG,"Gonna start command")
                // Start the transaction
                val startTransCmd = Client.StartEMVTransactionAsync(
                    deviceId,
                    amount, amountOther, transType, transTime, false,
                    3000
                )

                    /*Client.StartTransactionAsync(
                    deviceId,
                    amount, amountOther, transType, transTimeout,
                    transInterfaceType.toUByte(), 3000
                )*/

                Log.d(TAG, "Before wait")
                startTransCmd.waitForCompletion()
                Log.d(TAG, "After wait")

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
                Client.CancelTransactionAsync(connectedDeviceId!!)
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
        /* TODO: Add transition once other pages are linked
         */
        val intent = Intent(this, QrCodeActivity::class.java)
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
        startEMVTransaction()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelTransaction()
    }

    companion object {
        private const val TAG = "PaymentActivity" // Log tag
    }
}