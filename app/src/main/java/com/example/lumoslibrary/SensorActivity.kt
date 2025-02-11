package com.example.lumoslibrary

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi


/* A sample class to handle sensor activity, uses code from ID TECH SDK
* Also adjusts brightness using this g4g tutorial:
* https://www.geeksforgeeks.org/how-to-increase-decrease-screen-brightness-in-steps-programmatically-in-android*/
class SensorActivity : Activity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var proximity: Sensor? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensor)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //TODO? Do something if accuracy changes
    }

    //@RequiresApi(Build.VERSION_CODES.M) //-> Uncomment when doing screen brightness
    override fun onSensorChanged(event: SensorEvent?) {
        val distance = event!!.values[0]
        Log.d(TAG, "distance=$distance")


        //TODO: Change brightness when distance val reaches some val
    /*  //Brightness val: 0 - 255 (no brightness to full)

        val context = applicationContext
        val settingsCanWrite = hasWriteSettingsPermission(context)
        // These values are hypothetical, and control brightness
        var farValue = 100
        var closeValue = 255
        if (!settingsCanWrite) {
            changeWriteSettingsPermission(context)
        }else {
            if (distance < 8) {
            //8 is hypothetical and refers to 8 cm
                changeScreenBrightness(context, closeValue)
            }
            else {
                changeScreenBrightness(context, farValue)
            }
        }
    */


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

    companion object {
        private const val TAG = "SensorActivity"
    }
}