package com.example.lumoslibrary

/* Holds the current session of the user renting/ returning */
object CurrentSession {
    var userID: String = "0"
    var state: Int = 0 // 1: Rent, 2: Return
    var checkedOut: List<Item>? = null
}