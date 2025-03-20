package com.example.houseactive.streakscreen

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneOffset

class StreakDataStore(private val context: Context) {
    /**
     * DATASTORE_NAME: Name of the datastore
     * STREAK_KEY: The key to the stored Int which represents the current streak.
     * LAST_OPENED_KEY: The key to the stored Long which represents the last time the app was opened.
     * getStreak: Exposes the current streak value as a Flow.
     * getLastOpened: Exposes the last time the app was opened as a Flow.
     * saveStreak: Saves the streak to the datastore.
     * saveLastOpened: Saves the timestamp of the last opening to the datastore.
     */

    // Use Preferences DataStore
    companion object {
        private const val DATASTORE_NAME = "streak_data_store"
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)
        val STREAK_KEY = intPreferencesKey("streak_key")
        val LAST_OPENED_KEY = longPreferencesKey("last_opened_key")
    }

    val getStreak: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[STREAK_KEY] ?: 0
        }

    val getLastOpened: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_OPENED_KEY] ?: 0L
        }

    suspend fun saveStreak(streak: Int) {
        context.dataStore.edit { preferences ->
            preferences[STREAK_KEY] = streak
        }
    }

    suspend fun saveLastOpened(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[LAST_OPENED_KEY] = timestamp
        }
    }
}