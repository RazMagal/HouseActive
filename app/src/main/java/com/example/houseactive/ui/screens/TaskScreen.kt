package com.example.houseactive.ui.screens

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.houseactive.viewmodels.TaskViewModel

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

/**
 * TaskScreen is a composable function that represents the UI for displaying and managing tasks.
 * It follows the MVVM (Model-View-ViewModel) architecture pattern.
 *
 * @param taskViewModel The ViewModel that provides the data and business logic for the screen.
 */
@Composable
fun TaskScreen(taskViewModel: TaskViewModel = viewModel()) {
    val tasks by taskViewModel.tasks.collectAsState()
    val taskCompleted by taskViewModel.taskCompleted.collectAsState()

    var showAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(taskCompleted) {
        if (taskCompleted) {
            Log.d("TaskScreen", "Task completed signal received. Triggering animation.")
            showAnimation = true
            taskViewModel.resetTaskCompletedSignal() // Reset the signal
        }
    }

    // State to control whether the "Add Task" dialog is visible
    var showDialog by remember { mutableStateOf(false) }
    // State to hold the name of the new task being added
    var newTaskName by remember { mutableStateOf("") }
    
    // Scaffold provides a basic layout structure with a floating action button
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },   // Show dialog to add task
                content = { Icon(Icons.Default.Add, contentDescription = "Add Task") }
            )
        }
    ) { innerPadding ->
        // Column is a vertical layout container
        Column(modifier = Modifier
                .padding(innerPadding) // Padding to avoid overlapping with system UI
                .padding(16.dp)        // Additional padding for spacing
                ) {
            // Display a header for the task list
            Text("Your Tasks", style = MaterialTheme.typography.headlineSmall)
            tasks.forEach { task ->
                Text(task.name, modifier = Modifier.padding(8.dp))
                Button(
                    onClick = { taskViewModel.completeTask(task.id) } // Call ViewModel to complete the task
                ) {
                    Text("Complete")
                }
            }

        }
    }

    // Dialog for adding new task
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false }, // Close the dialog when dismissed
            title = { Text("Add New Task") },          // Dialog title
            text = {
                Column {
                    OutlinedTextField(
                        value = newTaskName,
                        onValueChange = { newTaskName = it }, // Update state as user types
                        label = { Text("Task Name") }         // Label for the input field
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newTaskName.isNotEmpty()) {
                        taskViewModel.addTask(newTaskName, false) // Add the task via ViewModel
                        newTaskName = ""   // Reset the input field
                        showDialog = false // Close the dialog
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                // Button to cancel adding a new task
                Button(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Move CelebrationAnimation to the end of the layout hierarchy to ensure it is drawn on top
    if (showAnimation) {
        CelebrationAnimation(onAnimationEnd = {
            Log.d("TaskScreen", "Animation ended. Hiding animation.")
            showAnimation = false
        })
    }
}

@Composable
fun CelebrationAnimation(onAnimationEnd: () -> Unit) {
    var visible by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                "🎉 Task Completed! 🎉",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LaunchedEffect(Unit) {
            Log.d("CelebrationAnimation", "Animation started.")
            kotlinx.coroutines.delay(2000) // Show animation for 2 seconds
            Log.d("CelebrationAnimation", "Animation delay completed.")
            visible = false // Trigger fade-out animation
            kotlinx.coroutines.delay(300) // Wait for fade-out to complete
            onAnimationEnd()
        }
    }
}