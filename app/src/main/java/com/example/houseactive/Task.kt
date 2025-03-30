package com.example.houseactive

import java.time.Instant
import java.util.Date

data class Task(
    var id: String = "",
    var name: String = "",
    var completed: Boolean = false
//    var creationDate: Date,
//    var dueDate: Date,
//    var importance: Int
    )