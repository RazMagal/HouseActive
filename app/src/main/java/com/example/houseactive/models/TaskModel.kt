package com.example.houseactive.models

import com.example.houseactive.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

/**
 * TaskModel handles all Firestore operations related to tasks.
 * It acts as the data layer for task-related logic.
 */
class TaskModel {
    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid
    private val firestore = FirebaseFirestore.getInstance()

    private val usersCollection = firestore.collection("users")
    private val userDocument = usersCollection.document(userId ?: "")
    private val tasksCollection = userDocument.collection("tasks")

    /**
     * Adds a new task to the Firestore database.
     *
     * @param name The name of the task.
     * @param completed Whether the task is completed (default is false).
     */
    suspend fun addTask(name: String, completed: Boolean): Task? {
        return try {
            val taskData = mapOf(
                "name" to name,
                "completed" to completed
            )
            val documentReference = tasksCollection.add(taskData).await()
            Task(
                id = documentReference.id,
                name = name,
                completed = completed
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Marks a task as completed in Firestore.
     *
     * @param taskId The ID of the task to mark as completed.
     */
    suspend fun completeTask(taskId: String): Boolean {
        return try {
            tasksCollection.document(taskId).update("completed", true).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Fetches tasks from Firestore in real-time using a snapshot listener.
     *
     * Only tasks where `completed == false` are fetched and displayed.
     */
    fun fetchTasks(_tasks: MutableStateFlow<List<Task>>) {
        if (userId == null) return
        tasksCollection.orderBy("name", Query.Direction.ASCENDING)
            .whereEqualTo("completed", false)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val taskList = snapshot.documents.map { doc ->
                        Task(
                            id = doc.id,
                            name = doc.getString("name") ?: "",
                            completed = doc.getBoolean("completed") ?: false
                        )
                    }
                    _tasks.value = taskList
                }
            }
    }
}