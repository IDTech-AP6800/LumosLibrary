package com.example.lumoslibrary

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import com.example.lumoslibrary.viewmodels.QrCodeViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

class ReturnScanActivity : AppCompatActivity() {

    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var imageCam: PreviewView
    private lateinit var totalRefundAmount: TextView
    private lateinit var searchInventory: SearchInventory
    private lateinit var itemCardContainer: LinearLayout
    private val scannedItemsList = mutableListOf<Item>()
    private val scannedItemsSet = mutableSetOf<String>()

    private var isScanning = false
    private val scanDelayHandler = Handler(Looper.getMainLooper())
    private val delayScanTime : Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_return_scan_item)
//        HelpButton(this)
        BackButton(this)

        Log.d(TAG, "onCreate: HERE in return!!!!!")

        imageCam = findViewById(R.id.item_camera_preview)

        itemCardContainer = findViewById(R.id.item_card_container)

        searchInventory = SearchInventory(this, "items.json")

        totalRefundAmount = findViewById(R.id.total_refund_amount)

        listenContinueButton()

        if (allPermissionsGranted()) {
            startCamera()
            Log.e(TAG, "All Permissions Granted")
        } else {
            requestPermissions()
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val permissionGranted = permissions.entries.all { it.value }
            if (!permissionGranted) {
                Toast.makeText(baseContext, "Permission request denied", Toast.LENGTH_SHORT).show()
            } else {
                startCamera()
            }
        }

    private fun startCamera() {
        val cameraController = LifecycleCameraController(baseContext)
        val previewView: PreviewView = findViewById(R.id.item_camera_preview)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_EAN_13, // ISBN-13 format
                Barcode.FORMAT_CODE_128 // Linear barcode format
            )
            .build()

        barcodeScanner = BarcodeScanning.getClient(options)

        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(this),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(this)
            ) { result: MlKitAnalyzer.Result? ->
                val barcodeResults = result?.getValue(barcodeScanner)
                val barcodeValue = barcodeResults?.getOrNull(0)?.displayValue
                val barcodeFormat = barcodeResults?.getOrNull(0)?.format

                if (barcodeResults.isNullOrEmpty()) {
                    previewView.overlay.clear()
                    previewView.setOnTouchListener{_, _ -> false}
                    return@MlKitAnalyzer
                }

                if (barcodeValue != null && !isScanning) {
                    isScanning = true // Prevent further scans
                    scanDelayHandler.postDelayed({ isScanning = false }, delayScanTime) // Able to scan again after 3 seconds

                    // Handle different barcode formats
                    when (barcodeFormat) {
                        Barcode.FORMAT_EAN_13 -> handleISBNBarcode(barcodeValue)
                        Barcode.FORMAT_CODE_128 -> handleLinearBarcode(barcodeValue)
                        else -> Log.d(TAG, "Unsupported barcode format")
                    }
                }

                val qrCodeViewModel = QrCodeViewModel(barcodeResults[0])
                val qrCodeDrawable = QrCodeDrawable(qrCodeViewModel)
                previewView.setOnTouchListener(qrCodeViewModel.qrCodeTouchCallback)
                previewView.overlay.clear()
                previewView.overlay.add(qrCodeDrawable)
            }
        )

        cameraController.bindToLifecycle(this)
        previewView.controller = cameraController
        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    }

    private fun handleISBNBarcode(value: String) {
        val matchedItem = searchInventory.searchByISBN(value)

        if (matchedItem != null) {
            addScannedItem(matchedItem)
            Toast.makeText(this, "Scanned: ${matchedItem.title}", Toast.LENGTH_LONG).show()
            Log.d(TAG, "Matched Book: ${matchedItem.title}, Location: ${matchedItem.location}")
        } else {
            Toast.makeText(this, "No matching book found", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "No match for ISBN: $value")
        }
    }

    private fun handleLinearBarcode(value: String) {
        val matchedItems = searchInventory.search(value)  // Uses title search

        if (matchedItems.isNotEmpty()) {
            val matchedItem = matchedItems.first()
            addScannedItem(matchedItem)
            Toast.makeText(this, "Scanned: ${matchedItem.title}", Toast.LENGTH_LONG).show()
            Log.d(TAG, "Matched Equipment: ${matchedItem.title}, Location: ${matchedItem.location}")
        } else {
            Toast.makeText(this, "No matching equipment found", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "No match for Equipment Title: $value")
        }
    }

    private fun addScannedItem(item: Item) {
        if (scannedItemsSet.add(item.title)) {
            scannedItemsList.add(item)
            updateItemCards()
        } else {
            Toast.makeText(this, "Item already scanned", Toast.LENGTH_SHORT).show()
        }
        calculateSecurityDepositTotal()
    }

    private fun removeScannedItem(item: Item) {
        if (scannedItemsSet.remove(item.title)) {
            scannedItemsList.remove(item)
            updateItemCards()
        }
        calculateSecurityDepositTotal()
    }

    private fun updateItemCards() {
        runOnUiThread {
            itemCardContainer.removeAllViews()
            val inflater = LayoutInflater.from(this)

            for (item in scannedItemsList) {
                val itemView = inflater.inflate(R.layout.item_card, itemCardContainer, false)
                itemView.findViewById<TextView>(R.id.card_field2).text = item.title

                val imageView = itemView.findViewById<ImageView>(R.id.card_image)
                val imageResId = resources.getIdentifier(item.image.replace(".jpg", ""), "drawable", packageName)
                if (imageResId != 0) {
                    imageView.setImageResource(imageResId)
                }

                val deleteButton = itemView.findViewById<ImageView>(R.id.delete_icon)
                deleteButton.setOnClickListener {
                    removeScannedItem(item)
                }

                if (item.itemType == "book") {
                    itemView.findViewById<TextView>(R.id.card_field1).text = item.itemType
                    itemView.findViewById<TextView>(R.id.card_field3).text = item.author
                }
                else if (item.itemType == "equipment") {
                    itemView.findViewById<TextView>(R.id.card_field1).text = item.itemType
                    itemView.findViewById<TextView>(R.id.card_field3).text = "Security Deposit: $${item.security_deposit.toString()}"
                }

                itemCardContainer.addView(itemView)
            }
        }
    }

    //Creates listener for the continue button
    private fun listenContinueButton() {
        val continueButton = findViewById<Button>(R.id.continue_button)
        continueButton.setOnClickListener {
            val depositTotal = findViewById<View>(R.id.total_refund_text) as TextView
            val totalDueString = depositTotal.text.toString()
//                .replace("$0.00", "")
                .replace(Regex("[^0-9.]"), "")
                .trim()

            CurrentSession.checkedOut = scannedItemsList
            RentSession.totalDue = totalDueString.toDouble()

            Log.d(TAG, "depositTotal: ${depositTotal.text} \n " +
                    "totalDueString: $totalDueString")

            val itemContainer = CurrentSession.checkedOut
            if (itemContainer == null) {
                Log.d(TAG, "CurrentSession.checkedOut is NULL!")
            } else {
                Log.d(TAG, "itemContainer! :")
                for (item in itemContainer) {
                    Log.d(TAG, "> ${item.title}\n")
                }
            }

            // Create an Intent to navigate to the PaymentOptions activity
//            val intent = Intent(this@RentScanActivity, TapSwipeInsertPaymentActivity::class.java)
//            startActivity(intent)

            Handler(Looper.getMainLooper()).postDelayed({
                if (RentSession.totalDue == 0.0) {
                    startActivity(Intent(this, RentConfirmationActivity::class.java)) // Replace with your intended activity
                    Log.d(TAG, "Total due is zero, navigating to SomeOtherActivity")
                } else {
                    startActivity(Intent(this, TapSwipeInsertPaymentActivity::class.java))
                    Log.d(TAG, "Navigating to TapSwipeInsertPaymentActivity")
                }
            }, 300) // Delay by 300ms

        }
    }

    private fun calculateSecurityDepositTotal() {
//        val totalDeposit = scannedItemsList.sumOf { it.security_deposit }
//        Log.d(TAG, "calculateSecurityDepositTotal: $totalDeposit")

        val depositTotal = findViewById<View>(R.id.total_refund_amount) as TextView

        var total = 0f

//        Log.d(TAG, "in here!")
        for (item in scannedItemsList) {
            if (item.security_deposit > 0) {
//                Log.d(TAG, "${item.title}: has desposit of ${item.security_deposit}")
                total += item.security_deposit
            }
        }

        val totalText = String.format("$%.2f", total)
        depositTotal.text = totalText

//        Log.d(TAG, "total is: $total")
//        val totalText = String.format("%.2f", totalDeposit)
//        totalRefundAmount.text = totalText
    }


    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "RentScanActivity is being destroyed")
        barcodeScanner.close()
        scanDelayHandler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()) {
            startCamera() // Restart scanning when activity is resumed
        }
    }

    companion object {
        private const val TAG = "ReturnScanActivity"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).toTypedArray()
    }
}