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

@Composable
fun TaskScreen(taskViewModel: TaskViewModel = viewModel()) {
    val tasks by taskViewModel.tasks.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newTaskName by remember { mutableStateOf("") }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },   // Show dialog to add task
                content = { Icon(Icons.Default.Add, contentDescription = "Add Task") }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            Text("Your Tasks", style = MaterialTheme.typography.headlineSmall)
            tasks.forEach { task ->
                Text(task.name, modifier = Modifier.padding(8.dp))
                Button(
                    onClick = { taskViewModel.completeTask(task.id) }
                ) {
                    Text("Complete")
                }
            }

        }
    }

    // Dialog for adding new task
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Add New Task") },
            text = {
                Column {
                    // TODO: Add to Task ShortDescription and repeating options
//                    OutlinedTextField(
//                        value = newTaskId,
//                        onValueChange = { newTaskId = it },
//                        label = { Text("Task id") }
//                    )
//                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = newTaskName,
                        onValueChange = { newTaskName = it },
                        label = { Text("Task Name") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newTaskName.isNotEmpty()) {
                        taskViewModel.addTask(newTaskName, false)
                        newTaskName = ""
                        showDialog = false
                    }
                }) {
                    Text("Add")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}