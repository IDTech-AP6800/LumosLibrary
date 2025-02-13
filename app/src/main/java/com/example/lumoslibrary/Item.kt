package com.example.lumoslibrary

/*Data class for the items in library inventory*/
data class Item(
    val title: String,
    val category: String,
    val author: String,
    val location: String,
    val amount: Int,
    val isAvailable: Boolean,
    val tag: String,
    val image: String,
    val description: String,
    val isbn13: String
)

//Questions:
/*
* What does tag refer to?
* Do we need an isAvailable?, This makes sense if amount always stays the same
* Does amount refer to the stock?
 */