package com.example.houseactive.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TaskViewModel : ViewModel() {
    private val _tasks = MutableStateFlow(listOf("Buy groceries", "Walk the dog", "Workout"))
    val tasks = _tasks.asStateFlow()

    fun addTask(task: String) {
        _tasks.value = _tasks.value + task
    }
}