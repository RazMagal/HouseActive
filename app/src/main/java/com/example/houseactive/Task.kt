package com.example.houseactive

import java.time.Instant
import java.util.Date

data class Task(
    var id: Int,
    var name: String,
    var creationDate: Date,
    var dueDate: Date,
    var importance: Int
    )

fun getTaskList() : List<Task> {
    return listOf<Task>(
        Task(1, "Clean the bathroom", Date.from(Instant.now()),
            Date.from(Instant.now().plusSeconds(3600)), 2),
        Task(2, "Mop the floor", Date.from(Instant.now()),
            Date.from(Instant.now().plusSeconds(3600)), 1),
        Task(3, "Do the dishes", Date.from(Instant.now()),
            Date.from(Instant.now().plusSeconds(3600)), 3),
    )
}
