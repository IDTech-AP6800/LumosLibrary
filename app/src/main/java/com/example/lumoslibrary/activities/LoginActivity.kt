package com.example.lumoslibrary.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.lumoslibrary.BackButton
import com.example.lumoslibrary.CurrentSession
import com.example.lumoslibrary.QrCodeDrawable
import com.example.lumoslibrary.R
import com.example.lumoslibrary.UserData
import com.example.lumoslibrary.viewmodels.QrCodeViewModel
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import android.view.inputmethod.InputMethodManager
import android.view.View
import android.view.inputmethod.EditorInfo

/* Login for users in the users.json, can enter via Barcode 128 or enter the id via numpad
*  Will reject users not in the users.json
*  Connects to Rent or Return depending on the CurrentSession State */
class LoginActivity : AppCompatActivity() {

    private lateinit var barcodeScanner: BarcodeScanner
    private lateinit var imageCam: PreviewView
    private lateinit var continueButton: Button
    private lateinit var userIdEditText: EditText
    private lateinit var searchUsers: UserData
    private var userID: String? = null

    private var isProcessingScan = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        BackButton(this)

        searchUsers = UserData(this, "users.json")

        imageCam = findViewById(R.id.camera_preview)
        continueButton = findViewById(R.id.login_continue)
        userIdEditText = findViewById(R.id.user_id_text_box)
        userIdEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                hideKeyboard(v)
                continueButton.performClick() // Simulate clicking the Continue button
                return@setOnEditorActionListener true
            }
            false
        }

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions()
        }

        // Add TextWatcher for formatting XXX-XX-XXX
        userIdEditText.addTextChangedListener(object : TextWatcher {
            private var isEditing = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                if (isEditing || editable == null) return

                isEditing = true
                val raw = editable.toString().replace("-", "")

                val formatted = StringBuilder()
                for (i in raw.indices) {
                    formatted.append(raw[i])
                    if ((i == 2 || i == 4) && i != raw.length - 1) {
                        formatted.append("-")
                    }
                }

                userIdEditText.setText(formatted.toString())
                userIdEditText.setSelection(formatted.length)
                isEditing = false
            }
        })

        continueButton.setOnClickListener {
            val enteredId = userIdEditText.text.toString().replace("-", "").trim()
            if (isValidUserID(enteredId)) {
                userID = enteredId
                nextActivity()
            } else {
                Toast.makeText(this, "Invalid User ID", Toast.LENGTH_SHORT).show()
            }
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
        val previewView: PreviewView = findViewById(R.id.camera_preview)

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
                val scannedId = barcodeResults?.getOrNull(0)?.displayValue

                if (barcodeResults.isNullOrEmpty()) {
                    previewView.overlay.clear()
                    previewView.setOnTouchListener { _, _ -> false }
                    return@MlKitAnalyzer
                }

                if (scannedId != null && isValidUserID(scannedId)) {
                    isProcessingScan = true
                    userID = scannedId
                    stopScanning()
                    Log.d(TAG, "Valid barcode: $scannedId")
                } else {
                    Log.d(TAG, "Invalid barcode scanned: $scannedId")
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

    private fun isValidUserID(id: String?): Boolean {
        return id != null && id.length == 8 && id.all { it.isDigit() } && searchUsers.searchByUserId(id) != null
    }

    private fun stopScanning() {
        barcodeScanner.close() // Release scanner resources
        nextActivity()
    }

    private fun nextActivity() {
        CurrentSession.userID = userID.toString()

        val user = searchUsers.searchByUserId(userID.toString())

        if (CurrentSession.state == 2) { // Return
            if (user != null && user.checkedOutItems.isNotEmpty()) {
                val intent = Intent(this, UserItemsCheckedOutActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "You have no checked-out items to return.", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "You have no checked-out items to return.")
            }
        } else { // Rent
            val intent = Intent(this, RentScanActivity::class.java)
            startActivity(intent)
        }
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

    override fun onResume() {
        super.onResume()
        if (allPermissionsGranted()) {
            startCamera() // Restart scanning when activity is resumed
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}