package com.example.lumoslibrary

import android.content.Context

interface LedUtils {

    fun lightOn(context: Context, ledId: Int, lightLevel: Int)

}