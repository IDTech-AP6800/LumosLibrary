package com.example.lumoslibrary

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode



/* This code is essentially the same as the one we used for AutoSpa
    This code handles the QR Code Payment Backend, as well as the other payment methods  */
class QrCodeActivity : AppCompatActivity() {

    lateinit var barcodeScanner : BarcodeScanner
    private lateinit var imageCam: PreviewView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code)

        imageCam = findViewById(R.id.camera_preview)

        /* TODO: Set text for value above payment
        private lateinit var amountText: TextView
        //private var totalDue = CarWashSession.totalDue
        amountText.text = "Amount Due: $" + String.format("%.2f", totalDue)
        */

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
            Log.e(TAG, "All Permissions Granted")
        } else {
            requestPermissions()
        }
    }



    /*Qr Code detection and the yellow box*/
    private val activityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions())
        {
                permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) {
                Toast.makeText(baseContext, "Permission request denied",
                    Toast.LENGTH_SHORT).show()
            }
            else {
                startCamera()
            }
        }
    private fun startCamera() {
        val cameraController = LifecycleCameraController(baseContext)
        val previewView: PreviewView = findViewById(R.id.camera_preview)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        barcodeScanner = BarcodeScanning.getClient(options)

        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(this),
            MlKitAnalyzer(
                listOf(barcodeScanner),
                COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(this)
            ) { result: MlKitAnalyzer.Result? ->
                val barcodeResults =
                    result?.getValue(barcodeScanner)
                val barcodeValue =
                    barcodeResults?.getOrNull(0)?.displayValue
                val barcodeFormat =
                    barcodeResults?.getOrNull(0)?.format
                if ((barcodeResults == null) ||
                    (barcodeResults.size == 0) ||
                    (barcodeResults.first() == null)){
                    previewView.overlay.clear()
                    previewView.setOnTouchListener{_, _ -> false}
                    return@MlKitAnalyzer
                }
                if (barcodeValue != null) {
                    //If it has a value, that is it scanner
                    Log.d(TAG, "startCamera:\ncodeValue:$barcodeValue" +
                            "\nbarcodeFormat:$barcodeFormat")

                    /*TODO: Link to other page we end up adding
                    val intent = Intent(this@QrCodeActivity, PaymentProcessingActivity::class.java)
                    startActivity(intent)
                     */
                }

                val qrCodeViewModel = QrCodeViewModel(barcodeResults[0])
                val qrCodeDrawable = QrCodeDrawable(qrCodeViewModel)
                previewView.setOnTouchListener(qrCodeViewModel.qrCodeTouchCallback)
                previewView.overlay.clear()
                previewView.overlay.add(qrCodeDrawable)
            }

        )

        cameraController.bindToLifecycle(this)
        previewView.setController(cameraController)

        /* The back camera is the one for qr codes
           If you want to use the front camera, change the selector
           to use DEFAULT_FRONT_CAMERA */
        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    }
    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    /*Car moving vertically animation
* Uses code from Park Buddy Swipe Animation*/
    private fun startAnimations(car: ImageView, cam: PreviewView) {

        car.post {
            val startPos = car.y
            val endPos = (cam.y + cam.height) - car.height
            Log.d("Debug", "Start: $startPos, End: $endPos")


            // Car Swipe Indication Animations
            // 0  - 150
            val moveDown = ObjectAnimator.ofFloat(car, "y", endPos).apply {
                duration = 3000
            }

            val moveUp = ObjectAnimator.ofFloat(car, "y", startPos).apply {
                duration = 3000
            }

            val rotationTop = ObjectAnimator.ofFloat(car, "rotationX", 0f, 180f)
            val rotationBottom = ObjectAnimator.ofFloat(car, "rotationX", 180f, 0f)

            val swipeIndicationSet = AnimatorSet().apply {
                playSequentially(moveDown, rotationTop, moveUp, rotationBottom)
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        start() // Restart the animation loop
                    }
                })
                start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        barcodeScanner.close()
    }

    companion object {
        private val TAG = QrCodeActivity::class.qualifiedName
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                Manifest.permission.CAMERA
            ).toTypedArray()
    }

}
