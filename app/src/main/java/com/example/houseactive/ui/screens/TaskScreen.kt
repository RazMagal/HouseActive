package com.example.houseactive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.houseactive.ui.viewmodels.TaskViewModel

import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Modifier

/**
 * TaskScreen is a composable function that represents the UI for displaying and managing tasks.
 * It follows the MVVM (Model-View-ViewModel) architecture pattern.
 *
 * @param taskViewModel The ViewModel that provides the data and business logic for the screen.
 */
@Composable
fun TaskScreen(taskViewModel: TaskViewModel = viewModel()) {
    // Collect the list of tasks from the ViewModel as a state. This ensures the UI updates when the data changes.
    val tasks by taskViewModel.tasks.collectAsState()
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
                    // TODO: Add to Task ShortDescription and repeating options
                    // Input field for the new task name
//                  OutlinedTextField(
//                        value = newTaskId,
//                        onValueChange = { newTaskId = it },
//                        label = { Text("Task id") }
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
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
}