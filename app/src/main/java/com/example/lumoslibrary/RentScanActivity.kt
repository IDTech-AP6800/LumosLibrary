package com.example.lumoslibrary

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest
import android.util.Log
import android.view.LayoutInflater
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

class RentScanActivity : AppCompatActivity() {

    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var imageCam: PreviewView
    private lateinit var searchInventory: SearchInventory
    private lateinit var itemCardContainer: LinearLayout
    private val scannedItemsList = mutableListOf<Item>()
    private val scannedItemsSet = mutableSetOf<String>()

    private var isScanning = false
    private val scanDelayHandler = Handler(Looper.getMainLooper())
    private val delayScanTime : Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rent_scan_item)
        HelpButton(this)

        imageCam = findViewById(R.id.item_camera_preview)

        itemCardContainer = findViewById(R.id.item_card_container)

        searchInventory = SearchInventory(this, "items.json")

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
            inflateItemCard(item) // Update UI with scanned item details
        } else {
            Toast.makeText(this, "Item already scanned", Toast.LENGTH_SHORT).show()
        }
    }

//    private fun updateItemCard(item: Item) {
//        runOnUiThread {
//            findViewById<TextView>(R.id.card_category).text = item.category
//            findViewById<TextView>(R.id.card_title).text = item.title
//            findViewById<TextView>(R.id.card_author).text = item.author
//            findViewById<TextView>(R.id.card_location).text = "Location: ${item.location}"
//
//            val imageView = findViewById<ImageView>(R.id.card_image)
//            val imageResId = resources.getIdentifier(item.image.replace(".jpg", ""), "drawable", packageName)
//
//            if (imageResId != 0) {
//                imageView.setImageResource(imageResId)
//            }
//        }
//    }

    private fun inflateItemCard(item: Item) {
        runOnUiThread {
            val inflater = LayoutInflater.from(this)
            val itemView = inflater.inflate(R.layout.item_card, itemCardContainer, false)

            // Populate with scanned item details
            itemView.findViewById<TextView>(R.id.card_category).text = item.category
            itemView.findViewById<TextView>(R.id.card_title).text = item.title
            itemView.findViewById<TextView>(R.id.card_author).text = item.author
            itemView.findViewById<TextView>(R.id.card_location).text = "Location: ${item.location}"

            // Set item image dynamically
            val imageView = itemView.findViewById<ImageView>(R.id.card_image)
            val imageResId = resources.getIdentifier(item.image.replace(".jpg", ""), "drawable", packageName)
            if (imageResId != 0) {
                imageView.setImageResource(imageResId)
            }

            // Add a fade-in animation
            itemView.alpha = 0f
            itemView.animate().alpha(1f).setDuration(300).start()

            // Add the inflated view to the container
            itemCardContainer.addView(itemView)
        }
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
        barcodeScanner.close()
    }

    companion object {
        private const val TAG = "RentScanActivity"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).toTypedArray()
    }
}