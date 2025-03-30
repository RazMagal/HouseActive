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
    fun addTask(name: String, completed: Boolean) {
        val newTask = Task(name = name, completed = completed)
        tasksCollection.add(newTask) // Add task to Firestore
    }
    // Mark a task as completed (this will remove it from UI)
    fun completeTask(taskId: String) {
        tasksCollection.document(taskId)
            .update("completed", true)
            .addOnSuccessListener {
                // Remove task from UI after successful update
                _tasks.value = _tasks.value.filter { it.id != taskId }
            }   
    }
    // Fetch tasks in real-time (Firestore listener)
    // Fetch only tasks where completed == false
    private fun fetchTasks() {
        tasksCollection.orderBy("name", Query.Direction.ASCENDING)
            .whereEqualTo("completed", false) // Only get incomplete tasks
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    val taskList = snapshot.documents.map { doc ->
                        Task(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            completed = doc.getBoolean("completed") ?: false
                        )
                    }
                    _tasks.value = taskList  // Update task list
                }
            }
    }
}