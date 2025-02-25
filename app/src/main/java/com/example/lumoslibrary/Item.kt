package com.example.lumoslibrary

/*Data class for the items in library inventory*/
data class Item(
    val title: String, // Title of item
    val category: String, // Category of item used for search (ex: textbook, laptop, accessory, etc.)
    val author: String, // Author of book
    val location: String, // Location of item in the library
    val security_deposit: Int, // Security deposit associated with the item (0 for books, $10 for equipment)
    val isAvailable: Boolean, // Indicates availability of item in the library
    val itemType: String, // Book or Equipment
    val image: String, // Image associated with the item
    val description: String, // Description of the item
    val isbn13: String // ISBN of the book
)
