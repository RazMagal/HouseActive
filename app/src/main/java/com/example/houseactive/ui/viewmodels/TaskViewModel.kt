package com.example.houseactive.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.houseactive.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class TaskViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()    // Firestore instance
    private val tasksCollection = firestore.collection("tasks") // Firestore collection reference

    private val _tasks = MutableStateFlow<List<Task>>(emptyList()) // StateFlow to observe task list
    val tasks = _tasks.asStateFlow()

    init {
        fetchTasks()  // Load tasks when ViewModel is created
    }

    // Add a new task to Firestore
    fun addTask(id: String, name: String, isCompleted: Boolean) {
        val newTask = Task(id = id, name = name, isCompleted = isCompleted)
        tasksCollection.add(newTask) // Add task to Firestore
    }

    // Fetch tasks in real-time (Firestore listener)
    private fun fetchTasks() {
        tasksCollection.orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val taskList = snapshot.documents.map { doc ->
                        Task(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            isCompleted = doc.getBoolean("isCompleted") ?: false
                        )
                    }
                    _tasks.value = taskList  // Update task list
                }
            }
    }
}