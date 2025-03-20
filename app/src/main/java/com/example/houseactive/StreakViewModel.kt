package com.example.houseactive.streakscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class StreakViewModel(private val dataStore: StreakDataStore) : ViewModel() {
    /**
     * _streak: A MutableStateFlow that holds the current streak value.
     * streak: A StateFlow (read-only) that exposes the current streak to the UI.
     * init block: When the StreakViewModel is created, it gets the streak from datastore, and calls updateStreak.
     *
     * updateStreak():
     *      Gets the last opened date from the datastore
     *      If it's the first time the app is opened, it saves the current time and exits.
     *      If the last opened date is before today:
     *          If it was yesterday, increments the streak.
     *          Otherwise, resets the streak to 0.
     *      Saves the new streak and today's time to the datastore.
     */

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak

    init {
        viewModelScope.launch {
            _streak.value = dataStore.getStreak.first()
            updateStreak()
        }
    }

    private suspend fun updateStreak() {
        val lastOpenedTimestamp = dataStore.getLastOpened.first()
        val lastOpenedDate = LocalDate.ofEpochDay(lastOpenedTimestamp)
        val today = LocalDate.now()

        if (lastOpenedTimestamp == 0L) {
            dataStore.saveLastOpened(today.toEpochDay())
            return
        }

        if (lastOpenedDate.isBefore(today)) {
            if (lastOpenedDate.isEqual(today.minusDays(1))) {
                _streak.value++
            } else {
                _streak.value = 0
            }

            dataStore.saveStreak(_streak.value)
            dataStore.saveLastOpened(today.toEpochDay())
        }
    }
}