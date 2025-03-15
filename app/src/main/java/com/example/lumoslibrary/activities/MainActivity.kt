package com.example.lumoslibrary.activities

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AlphaAnimation
import android.view.animation.TranslateAnimation
import java.io.File
import java.io.IOException
import com.idtech.zsdk_client.Client
import com.idtech.zsdk_client.GetDevicesAsync
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.lumoslibrary.viewmodels.MainViewModel
import androidx.annotation.RequiresApi
import kotlin.random.Random
import android.provider.Settings
import android.net.Uri
import com.example.lumoslibrary.R



/*Starts the connection with IdTech API, Handles Screen Saver Animations,
* and Holds the Proximity Sensor which changes brightness when the user gets closer */
class MainActivity : AppCompatActivity(), SensorEventListener{

    private val viewModel: MainViewModel by viewModels()
    private lateinit var sensorManager: SensorManager
    private var proximity: Sensor? = null
    lateinit var rootView: View
    private var isClicked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Proximity sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

        // Start the star fading animation in MainActivity
        startSporadicStarFadingAnimation()

        // Reset all views to their original state
        resetViews()

        // Enumerate devices immediately;
        // if the server is not yet connected, this may complete after.
        Client.GetDevicesAsync()

        val jsonFile = copyJsonToInternalStorage(this, "users.json")
        // Observe connection status
        viewModel.connectionStatus.observe(this) { status ->
            Log.d("ConnectionStatus", "Connection status: $status")
        }

        // Connect to the server (via your ViewModel).
        // Adjust the IP/port to match your environment.
        viewModel.connectToServer("127.0.0.1", "42501", this)

        // Get references to the lumos_light view
        val lumosLightView = findViewById<ImageView>(R.id.lumos_light)


        // Get references to all your ImageViews in order from top to bottom
        val views = listOf(
            findViewById<ImageView>(R.id.star2),
            findViewById<ImageView>(R.id.starv2_2),
            findViewById<ImageView>(R.id.line1_1),
            findViewById<ImageView>(R.id.linev2_1_1),
            findViewById<ImageView>(R.id.star1),
            findViewById<ImageView>(R.id.starv2_1),
            findViewById<ImageView>(R.id.line1_2),
            findViewById<ImageView>(R.id.linev2_1_2),
            findViewById<ImageView>(R.id.star3),
            findViewById<ImageView>(R.id.starv2_3),
            findViewById<ImageView>(R.id.line2_1),
            findViewById<ImageView>(R.id.linev2_2_1),
            findViewById<ImageView>(R.id.line2_2),
            findViewById<ImageView>(R.id.linev2_2_2),
            findViewById<ImageView>(R.id.star4),
            findViewById<ImageView>(R.id.starv2_4),
            findViewById<ImageView>(R.id.star5),
            findViewById<ImageView>(R.id.starv2_5),
            findViewById<ImageView>(R.id.line3_1),
            findViewById<ImageView>(R.id.linev2_3_1),
            findViewById<ImageView>(R.id.line3_2),
            findViewById<ImageView>(R.id.linev2_3_2),
            findViewById<ImageView>(R.id.star6),
            findViewById<ImageView>(R.id.starv2_6),
            findViewById<ImageView>(R.id.star7),
            findViewById<ImageView>(R.id.starv2_7),
            findViewById<ImageView>(R.id.line4_1),
            findViewById<ImageView>(R.id.linev2_4_1),
            findViewById<ImageView>(R.id.line4_2),
            findViewById<ImageView>(R.id.linev2_4_2),
            findViewById<ImageView>(R.id.line5_1),
            findViewById<ImageView>(R.id.linev2_5_1),
            findViewById<ImageView>(R.id.line5_2),
            findViewById<ImageView>(R.id.linev2_5_2),
            findViewById<ImageView>(R.id.star8),
            findViewById<ImageView>(R.id.starv2_8),
            findViewById<ImageView>(R.id.star10),
            findViewById<ImageView>(R.id.starv2_10),
            findViewById<ImageView>(R.id.star9),
            findViewById<ImageView>(R.id.starv2_9)
        )

        // OPTIONAL: If you want a simple “tap anywhere to proceed” approach:
        rootView = findViewById<View>(android.R.id.content)
        rootView.setOnClickListener {

            // Play fade-out animation for the first layout views
            val handler = Handler(mainLooper)
            var delay = 0L

            var animationsCompleted = 0

            views.forEach { view ->
                handler.postDelayed({
                    applyFadeOutAnimation(view, object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationRepeat(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            view.visibility = View.INVISIBLE
                            animationsCompleted++
                            if (animationsCompleted == views.size) {
                                applyTranslateAnimation(lumosLightView)
                            }
                        }
                    })
                }, delay)

                delay += 40
            }
        }
    }

    // Method to apply fade-out animation
    private fun applyFadeOutAnimation(view: ImageView, listener: Animation.AnimationListener) {
        val fadeOut = AlphaAnimation(1.0f, 0.0f).apply {
            duration = 400
            setAnimationListener(listener)
        }
        view.startAnimation(fadeOut)
    }

    // Translate animation method (moving upwards)
    private fun applyTranslateAnimation(view: ImageView) {
        // Ensure the view is visible and positioned properly
        view.visibility = View.VISIBLE

        // Create the translate animation (500 pixels upwards)
        val translateAnimation = TranslateAnimation(0f, 0f, 0f, -214f) // Move 214px upwards
        translateAnimation.duration = 800 // 1-second duration

        // Start the translate animation
        view.startAnimation(translateAnimation)

        // Apply fade-in animation to these views
        val handler = Handler(mainLooper)
        var delay = 0L

        // After fade-out animation is complete, switch views and start 2nd animation
        handler.postDelayed({
            switchToNewLayout()
        }, delay + 800)  // Adjust this delay based on animation duration
    }

    // Method to navigate to LandingPageActivity
    private fun navigateToLandingPage() {
        val intent = Intent(this, LandingPageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        startActivity(intent)
    }

    // Replace layout with ConstraintLayout after animation
    private fun switchToNewLayout() {
        // Switch to the new layout that uses ConstraintLayout
        setContentView(R.layout.activity_main2)

        // Re-apply the star fading animation in MainActivity2
        startSporadicStarFadingAnimationInMainActivity2()

        // Get references to the views in the new layout
        val lumosLightView = findViewById<ImageView>(R.id.lumos_light) // ImageView in second layout
        val lumosLibraryTitle = findViewById<ImageView>(R.id.lumos_library_dimmed) // ImageView in second layout
        val lumosLightGlow = findViewById<ImageView>(R.id.lumos_light_glow) // New image to swap with
        val lumosLibraryTitle2 = findViewById<ImageView>(R.id.lumos_library) // New title to swap with


        // Get references to the views in the new layout
        val newViews = listOf(
            findViewById<ImageView>(R.id.star_book1),
            findViewById<ImageView>(R.id.book_line1_1),
            findViewById<ImageView>(R.id.star_book2),
            findViewById<ImageView>(R.id.book_line1_2),
            findViewById<ImageView>(R.id.star_book3),
            findViewById<ImageView>(R.id.book_line1_3),
            findViewById<ImageView>(R.id.star_book4),
            findViewById<ImageView>(R.id.book_line1_4),
            findViewById<ImageView>(R.id.star_book5),
            findViewById<ImageView>(R.id.book_line2_1),
            findViewById<ImageView>(R.id.star_book6),
            findViewById<ImageView>(R.id.book_line2_2),
            findViewById<ImageView>(R.id.star_book7),
            findViewById<ImageView>(R.id.book_line2_3),
            findViewById<ImageView>(R.id.star_book8),
            findViewById<ImageView>(R.id.book_line3_1),
            findViewById<ImageView>(R.id.star_book9),
            findViewById<ImageView>(R.id.book_line3_2),
            findViewById<ImageView>(R.id.star_book10),
            findViewById<ImageView>(R.id.book_line3_3),
            findViewById<ImageView>(R.id.star_book11),
            findViewById<ImageView>(R.id.book_line4_1),
            findViewById<ImageView>(R.id.book_line4_2),
            findViewById<ImageView>(R.id.book_line4_3),
            findViewById<ImageView>(R.id.star_book12),
            findViewById<ImageView>(R.id.book_line5_1),
            findViewById<ImageView>(R.id.book_line5_4),
            findViewById<ImageView>(R.id.book_line5_2),
            findViewById<ImageView>(R.id.book_line5_3),
            findViewById<ImageView>(R.id.star_book15),
            findViewById<ImageView>(R.id.star_book13),
            findViewById<ImageView>(R.id.star_book14),
            findViewById<ImageView>(R.id.star_book16)
        )

        // Set all new views to be initially invisible
        newViews.forEach { view ->
            view.visibility = View.INVISIBLE
        }

        // Apply fade-in animation to these views
        val handler = Handler(mainLooper)
        var delay = 0L

        newViews.forEach { view ->
            handler.postDelayed({
                applyFadeInAnimation(view)
            }, delay)

            delay += 40
        }

        // After fade-in animations are complete, swap the elements for 1 second
        handler.postDelayed({
            swapViews(lumosLightView, lumosLightGlow, lumosLibraryTitle, lumosLibraryTitle2)
        }, 1300) // Delay timing

        // After the swap happens, reset them after 1 second
        handler.postDelayed({
            resetSwap(lumosLightView, lumosLightGlow, lumosLibraryTitle, lumosLibraryTitle2)
        }, 1400) // Delay timing

        // After reset, swap the elements for 1 second to ending state
        handler.postDelayed({
            swapViews(lumosLightView, lumosLightGlow, lumosLibraryTitle, lumosLibraryTitle2)
        }, 1475) // Delay timing


        // After light flicker animation is complete, navigate to the landing page
        handler.postDelayed({
            navigateToLandingPage()
        }, delay + 1700)  // Adjust this delay based on animation duration
    }

    // Method to apply fade-in animation
    private fun applyFadeInAnimation(view: ImageView) {
        val fadeIn = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 400 // Duration of fade-in animation
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    // Set visibility to VISIBLE right at the start of the animation
                    view.visibility = View.VISIBLE
                }

                override fun onAnimationRepeat(animation: Animation?) {
                    // No need to handle this
                }

                override fun onAnimationEnd(animation: Animation?) {
                    // Ensure that the view stays visible after the animation
                    view.visibility = View.VISIBLE
                    view.alpha = 1.0f // Ensure it's fully opaque
                }
            })
        }
        view.startAnimation(fadeIn)
    }

    // Method to swap the ImageView elements
    private fun swapViews(lumosLightView: ImageView, lumosLightGlow: ImageView, lumosLibraryTitle: ImageView, lumosLibraryTitle2: ImageView) {
        // Hide the original elements and show the swapped elements
        lumosLightView.visibility = View.INVISIBLE
        lumosLibraryTitle.visibility = View.INVISIBLE

        lumosLightGlow.visibility = View.VISIBLE
        lumosLibraryTitle2.visibility = View.VISIBLE
    }

    // Method to reset the swapped views
    private fun resetSwap(lumosLightView: ImageView, lumosLightGlow: ImageView, lumosLibraryTitle: ImageView, lumosLibraryTitle2: ImageView) {
        // Swap them back to their original state
        lumosLightGlow.visibility = View.INVISIBLE
        lumosLibraryTitle2.visibility = View.INVISIBLE

        lumosLightView.visibility = View.VISIBLE
        lumosLibraryTitle.visibility = View.VISIBLE
    }

    // Method to apply sporadic fade-in and fade-out animation with random timing
    private fun applySporadicFadeInOutAnimation(view: ImageView, delay: Long, duration: Long) {
        val fadeInOut = AlphaAnimation(0.0f, 0.5f).apply {
            this.duration = duration // Set random duration for each star
            repeatMode = AlphaAnimation.REVERSE // Reverse the animation (fade out)
            repeatCount = AlphaAnimation.INFINITE // Infinite repetition
        }

        // Add a delay before starting the animation
        Handler(mainLooper).postDelayed({
            view.startAnimation(fadeInOut)
        }, delay)
    }

    // Method to apply the continuous fade-in and fade-out animation for stars
    private fun startSporadicStarFadingAnimation()
    {
        val stars = listOf(
            findViewById<ImageView>(R.id.bg_star1),
            findViewById<ImageView>(R.id.bg_star2),
            findViewById<ImageView>(R.id.bg_star3),
            findViewById<ImageView>(R.id.bg_star4),
            findViewById<ImageView>(R.id.bg_star5),
            findViewById<ImageView>(R.id.bg_star6),
            findViewById<ImageView>(R.id.bg_star7),
            findViewById<ImageView>(R.id.bg_star8),
            findViewById<ImageView>(R.id.bg_star9),
            findViewById<ImageView>(R.id.bg_star10),
            findViewById<ImageView>(R.id.bg_star11),
            findViewById<ImageView>(R.id.bg_star12),
            findViewById<ImageView>(R.id.bg_star13),
            findViewById<ImageView>(R.id.bg_star14),
            findViewById<ImageView>(R.id.bg_star15),
            findViewById<ImageView>(R.id.bg_star16),
            findViewById<ImageView>(R.id.bg_star17),
            findViewById<ImageView>(R.id.bg_star18),
            findViewById<ImageView>(R.id.bg_star19),
            findViewById<ImageView>(R.id.bg_star20),
        )

        // Apply fade-in/fade-out animation with randomized delays and durations for each star
        stars.forEach { star ->
            star.visibility = View.INVISIBLE
            val randomDelay = Random.nextLong(0, 3000) // Random delay up to 3 seconds
            val randomDuration = Random.nextLong(1000, 2000) // Random duration between 1s and 2s
            applySporadicFadeInOutAnimation(star, randomDelay, randomDuration)
        }
    }

    // Method to apply the sporadic fade-in/fade-out animation to stars in MainActivity2
    private fun startSporadicStarFadingAnimationInMainActivity2() {
        val stars2 = listOf(
            findViewById<ImageView>(R.id.bg2_star1),
            findViewById<ImageView>(R.id.bg2_star2),
            findViewById<ImageView>(R.id.bg2_star3),
            findViewById<ImageView>(R.id.bg2_star4),
            findViewById<ImageView>(R.id.bg2_star5),
            findViewById<ImageView>(R.id.bg2_star6),
            findViewById<ImageView>(R.id.bg2_star7),
            findViewById<ImageView>(R.id.bg2_star8),
            findViewById<ImageView>(R.id.bg2_star9),
            findViewById<ImageView>(R.id.bg2_star10),
            findViewById<ImageView>(R.id.bg2_star11),
            findViewById<ImageView>(R.id.bg2_star12),
            findViewById<ImageView>(R.id.bg2_star13),
            findViewById<ImageView>(R.id.bg2_star14),
            findViewById<ImageView>(R.id.bg2_star15),
            findViewById<ImageView>(R.id.bg2_star16),
            findViewById<ImageView>(R.id.bg2_star17),
            findViewById<ImageView>(R.id.bg2_star18),
            findViewById<ImageView>(R.id.bg2_star19),
            findViewById<ImageView>(R.id.bg2_star20)
        )

        // Apply fade-in/fade-out animation with randomized delays and durations for each star
        stars2.forEach { star ->
            star.visibility = View.INVISIBLE
            val randomDelay = Random.nextLong(0, 3000) // Random delay up to 3 seconds
            val randomDuration = Random.nextLong(1000, 2000) // Random duration between 1s and 2s
            applySporadicFadeInOutAnimation(star, randomDelay, randomDuration)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    private fun copyJsonToInternalStorage(context: Context, fileName: String): File {
        val file = File(context.filesDir, fileName)

        if (!file.exists()) {
            try {
                context.assets.open(fileName).use { inputStream ->
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    private fun resetViews() {
        Log.d("MainActivity", "Resetting views to visible state.")
        val views = listOf(
            findViewById<ImageView>(R.id.star2),
            findViewById<ImageView>(R.id.starv2_2),
            findViewById<ImageView>(R.id.line1_1),
            findViewById<ImageView>(R.id.linev2_1_1),
            findViewById<ImageView>(R.id.star1),
            findViewById<ImageView>(R.id.starv2_1),
            findViewById<ImageView>(R.id.line1_2),
            findViewById<ImageView>(R.id.linev2_1_2),
            findViewById<ImageView>(R.id.star3),
            findViewById<ImageView>(R.id.starv2_3),
            findViewById<ImageView>(R.id.line2_1),
            findViewById<ImageView>(R.id.linev2_2_1),
            findViewById<ImageView>(R.id.line2_2),
            findViewById<ImageView>(R.id.linev2_2_2),
            findViewById<ImageView>(R.id.star4),
            findViewById<ImageView>(R.id.starv2_4),
            findViewById<ImageView>(R.id.star5),
            findViewById<ImageView>(R.id.starv2_5),
            findViewById<ImageView>(R.id.line3_1),
            findViewById<ImageView>(R.id.linev2_3_1),
            findViewById<ImageView>(R.id.line3_2),
            findViewById<ImageView>(R.id.linev2_3_2),
            findViewById<ImageView>(R.id.star6),
            findViewById<ImageView>(R.id.starv2_6),
            findViewById<ImageView>(R.id.star7),
            findViewById<ImageView>(R.id.starv2_7),
            findViewById<ImageView>(R.id.line4_1),
            findViewById<ImageView>(R.id.linev2_4_1),
            findViewById<ImageView>(R.id.line4_2),
            findViewById<ImageView>(R.id.linev2_4_2),
            findViewById<ImageView>(R.id.line5_1),
            findViewById<ImageView>(R.id.linev2_5_1),
            findViewById<ImageView>(R.id.line5_2),
            findViewById<ImageView>(R.id.linev2_5_2),
            findViewById<ImageView>(R.id.star8),
            findViewById<ImageView>(R.id.starv2_8),
            findViewById<ImageView>(R.id.star10),
            findViewById<ImageView>(R.id.starv2_10),
            findViewById<ImageView>(R.id.star9),
            findViewById<ImageView>(R.id.starv2_9)
        )

        views.forEach { view ->
            view.visibility = View.VISIBLE
            view.alpha = 1.0f
            view.clearAnimation()
        }
    }

    //Proximity sensor code
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //TODO? Do something if accuracy changes
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override //-> Uncomment when doing screen brightness
    fun onSensorChanged(event: SensorEvent?) {
        val distance = event!!.values[0]
        Log.d(MainActivity.TAG, "distance=$distance")

        //Brightness val: 0 - 255 (no brightness to full)

        val context = applicationContext
        val settingsCanWrite = hasWriteSettingsPermission(context)
        // These values are hypothetical, and control brightness
        val minDistance = 50
        var farValue = 70
        var closeValue = 200
        if (!settingsCanWrite) {
            changeWriteSettingsPermission(context)
        }else {
            if (distance < minDistance) {
                //50 is hypothetical and refers to 50 cm
                changeScreenBrightness(context, closeValue)
                rootView.performClick()  // Trigger the onClick listener
                isClicked = true  // Set the flag to true after click is triggered
            }
            else {
                changeScreenBrightness(context, farValue)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        proximity?.also { proximity ->
            sensorManager.registerListener(this, proximity,
                SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasWriteSettingsPermission(context: Context): Boolean{
        var ret = true
        ret = Settings.System.canWrite(context)
        return ret
    }

    private fun changeWriteSettingsPermission(context: Context) {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.setData(Uri.parse("package:$packageName"))
        startActivity(intent)
    }

    private fun changeScreenBrightness(context: Context, screenBrightnessValue: Int) {
        //Changes screen brightness to manual
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
        //Applies the given brightness
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS, screenBrightnessValue
        )
    }
}
