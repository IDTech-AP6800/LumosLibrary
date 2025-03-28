package com.example.lumoslibrary

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import com.example.lumoslibrary.viewmodels.QrCodeViewModel

// Adds the bounding box around the Qr code
// Help from: https://github.com/android/camera-samples/blob/main/CameraX-MLKit/app/src/main/java/com/example/camerax_mlkit/QrCodeDrawable.kt
class QrCodeDrawable(qrCodeViewModel: QrCodeViewModel) : Drawable() {
    private val boundingRectPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.YELLOW
        strokeWidth = 5F
        alpha = 200
    }

    private val qrCodeViewModel = qrCodeViewModel

    override fun draw(canvas: Canvas) {
        canvas.drawRect(qrCodeViewModel.boundingRect, boundingRectPaint)
    }

    override fun setAlpha(alpha: Int) {
        boundingRectPaint.alpha = alpha
    }

    override fun setColorFilter(p0: ColorFilter?) {
        boundingRectPaint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

}