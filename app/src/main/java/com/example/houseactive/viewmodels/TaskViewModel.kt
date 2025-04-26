package com.example.houseactive.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.houseactive.Task
import com.example.houseactive.models.TaskModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * TaskViewModel is responsible for managing the task data and business logic.
 * It follows the MVVM architecture pattern, acting as the "ViewModel" layer.
 *
 * This ViewModel interacts with TaskModel to fetch, add, and update tasks.
 * It exposes a `StateFlow` of tasks to the UI, ensuring real-time updates.
 */
class TaskViewModel : ViewModel() {
    private val taskModel = TaskModel()

    // Backing property for the list of tasks (mutable)
    private val _tasks = MutableStateFlow<List<Task>>(emptyList()) // MutableStateFlow holds the current state of tasks
    val tasks = _tasks.asStateFlow() // Exposed as an immutable StateFlow to the UI

    // Initialize the ViewModel by fetching tasks from Firestore
    init {
        fetchTasks()
    }

    /**
     * Adds a new task to the Firestore database.
     *
     * @param name The name of the task.
     * @param completed Whether the task is completed (default is false).
     */
    fun addTask(name: String, completed: Boolean) {
        viewModelScope.launch {
            val newTask = taskModel.addTask(name, completed)
            if (newTask != null) {
                val updatedTasks = _tasks.value.toMutableList()
                updatedTasks.add(newTask)
                _tasks.value = updatedTasks
            }
        }
    }

    /**
     * Marks a task as completed in Firestore and removes it from the UI.
     *
     * @param taskId The ID of the task to mark as completed.
     */
    fun completeTask(taskId: String) {
        viewModelScope.launch {
            val success = taskModel.completeTask(taskId)
            if (success) {
                _tasks.value = _tasks.value.filter { it.id != taskId }
            }
        }
    }

    /**
     * Fetches tasks from Firestore in real-time using a snapshot listener.
     */
    private fun fetchTasks() {
        taskModel.fetchTasks(_tasks)
    }
}