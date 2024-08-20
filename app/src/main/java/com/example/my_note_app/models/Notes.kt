package com.example.my_note_app.models

import com.google.firebase.Timestamp

data class Notes(
    val userId: String = "",
    val title: String = "",
    val desription:String = "",
    val timestamp: Timestamp = Timestamp.now(),
    val colorId:Int = 0,
    val docId:String = "",

)