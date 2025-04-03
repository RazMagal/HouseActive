package com.example.houseactive.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.houseactive.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.auth.FirebaseAuth

/**
 * TaskViewModel is responsible for managing the task data and business logic.
 * It follows the MVVM architecture pattern, acting as the "ViewModel" layer.
 * 
 * This ViewModel interacts with Firestore to fetch, add, and update tasks.
 * It exposes a `StateFlow` of tasks to the UI, ensuring real-time updates.
 */
class TaskViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val userId = auth.currentUser?.uid
    private val firestore = FirebaseFirestore.getInstance()


    private val usersCollection = firestore.collection("users")
    private val userDocuemnt = usersCollection.document(userId ?: "")
    private val tasksCollection = userDocuemnt.collection("tasks")


    // Backing property for the list of tasks (mutable)
    private val _tasks = MutableStateFlow<List<Task>>(emptyList()) // MutableStateFlow holds the current state of tasks
    val tasks = _tasks.asStateFlow() // Exposed as an immutable StateFlow to the UI

    // Initialize the ViewModel by fetching tasks from Firestore
    init {
        fetchTasks()  // Load tasks when ViewModel is created
    }

    /**
     * Adds a new task to the Firestore database.
     * 
     * @param name The name of the task.
     * @param completed Whether the task is completed (default is false).
     */
    fun addTask(name: String, completed: Boolean) {
        // Create a map for the task data to send to Firestore
        val taskData = mapOf(
            "name" to name,
            "completed" to completed
            )

        // Add the task data to the Firestore "tasks" collection BEFORE creating new local Task
        tasksCollection.add(taskData)
            // On success, update the local task list with the new task
            .addOnSuccessListener { documentReference ->
                // Create the Task object locally based on the Firebase object
                val newTask = Task(
                                id = documentReference.id, // Assign Firestore document ID
                                name = name,
                                completed = completed
                                )
                // Add task to UI
                // Update the local task list with the new task
                val updatedTasks = _tasks.value.toMutableList()
                updatedTasks.add(newTask)
                _tasks.value = updatedTasks // Update the StateFlow with the new task list
            }

    }
    
    /**
     * Marks a task as completed in Firestore and removes it from the UI.
     * 
     * @param taskId The ID of the task to mark as completed.
     */
    fun completeTask(taskId: String) {
        // Update the "completed" field of the task in Firestore
        tasksCollection.document(taskId)
            .update("completed", true)
            .addOnSuccessListener {
                // On success, remove the task from the local task list
                _tasks.value = _tasks.value.filter { it.id != taskId }
            }   
    }
    
    /**
     * Fetches tasks from Firestore in real-time using a snapshot listener.
     * 
     * Only tasks where `completed == false` are fetched and displayed.
     */
    private fun fetchTasks() {
        if (userId == null) return
        // Query Firestore for tasks ordered by name and where completed == false
        tasksCollection.orderBy("name", Query.Direction.ASCENDING)
            .whereEqualTo("completed", false) // Only get incomplete tasks
            .addSnapshotListener { snapshot, e ->
                if (snapshot != null) {
                    // Map Firestore documents to Task objects
                    val taskList = snapshot.documents.map { doc ->
                        Task(
                            id = doc.id, // Firestore document ID
                            name = doc.getString("name") ?: "", // Task name
                            completed = doc.getBoolean("completed") ?: false // Task completion status
                        )
                    }
                    _tasks.value = taskList  // Update the StateFlow with the fetched tasks
                }
            }
    }
}