package com.example.lumoslibrary

import android.view.View

/*
    Ensures that clicks don't call the audio cues twice
    Uses code from:
    https://stackoverflow.com/questions/60190103/how-can-i-debounce-a-setonclicklistener-for-1-second-using-kotlin-coroutines
 */
class DebounceOnClickListener(private val interval: Long,
private val listenerBlock: (View) -> Unit): View.OnClickListener {
    private var lastClickTime = 0L

    override fun onClick(v: View) {
        val time = System.currentTimeMillis()
        if (time - lastClickTime >= interval) {
            lastClickTime = time
            listenerBlock(v)
        }
    }
}

fun View.setOnClickListener(debounceInterval: Long, listenerBlock: (View) -> Unit) =
    setOnClickListener(DebounceOnClickListener(debounceInterval, listenerBlock))