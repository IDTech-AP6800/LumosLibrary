package com.example.lumoslibrary.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.lumoslibrary.R
import com.example.lumoslibrary.UserData
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

class UserScanActivity : AppCompatActivity() {

    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var imageCam: PreviewView
    private lateinit var searchUsers: UserData

    private var isProcessingScan = false
    private val scanDelayHandler = Handler(Looper.getMainLooper())
    private val scanDelayTime: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.temp_user_scan)

        imageCam = findViewById(R.id.user_camera_preview)
        searchUsers = UserData(this, "users.json")

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.entries.all { it.value }) {
                startCamera()
            } else {
                Toast.makeText(this, "Permission request denied", Toast.LENGTH_SHORT).show()
            }
        }

    private fun startCamera() {
        val cameraController = LifecycleCameraController(baseContext)
        val previewView: PreviewView = findViewById(R.id.user_camera_preview)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_CODE_128) // Code 128 for User ID
            .build()

        barcodeScanner = BarcodeScanning.getClient(options)

        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(this),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(this)
            ) { result ->
                val barcodeResults = result?.getValue(barcodeScanner)
                val barcodeValue = barcodeResults?.getOrNull(0)?.displayValue

                if (barcodeResults.isNullOrEmpty() || isProcessingScan) return@MlKitAnalyzer

                if (barcodeValue != null) {
                    isProcessingScan = true
                    handleUserBarcode(barcodeValue)

                    scanDelayHandler.postDelayed({ isProcessingScan = false }, scanDelayTime)
                }
            }
        )

        cameraController.bindToLifecycle(this)
        previewView.controller = cameraController
        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    }

    private fun handleUserBarcode(value: String) {
        val matchedUser = searchUsers.searchByUserId(value)

        if (matchedUser != null) {
            Toast.makeText(this, "User Found: ${matchedUser.userId}", Toast.LENGTH_LONG).show()
            Log.d(TAG, "Matched User ID: ${matchedUser.userId}")

            val checkedOutItems = matchedUser.checkedOutItems.joinToString("\n") {
                "${it.name} (Late: ${it.isLate})"
            }

            Toast.makeText(this, "Checked Out:\n$checkedOutItems", Toast.LENGTH_LONG).show()
            Log.d(TAG, "Checked Out Items: \n$checkedOutItems")

            stopScanning()
        } else {
            Toast.makeText(this, "User not found. Try again.", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "No match for User ID: $value")
        }
    }

    private fun stopScanning() {
        barcodeScanner.close() // Release scanner resources
        //TODO: move to the next activity
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        barcodeScanner.close()
    }

    companion object {
        private const val TAG = "UserScanActivity"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
