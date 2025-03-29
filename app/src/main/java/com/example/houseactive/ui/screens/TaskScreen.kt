package com.example.houseactive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.houseactive.ui.viewmodels.TaskViewModel

@Composable
fun TaskScreen(taskViewModel: TaskViewModel = viewModel()) {
    val tasks by taskViewModel.tasks.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Your Tasks", modifier = Modifier.padding(bottom = 8.dp))
        tasks.forEach { task ->
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Text(task)
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {}) {
                    Text("Done")
                }
            }
        }
    }
}
