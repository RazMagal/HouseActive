package com.example.houseactive.viewmodels

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.houseactive.models.AuthModel
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.launch

/**
 * AuthViewModel is responsible for managing user authentication.
 * It provides methods for signing in with Google and signing out.
 *
 * This ViewModel interacts with AuthModel to handle user authentication.
 */
class AuthViewModel : ViewModel() {
    private val authModel = AuthModel()

    /**
     * Handles Google Sign-In and authenticates the user with Firebase.
     *
     * @param intent The intent returned from the Google Sign-In activity.
     * @param onAuthComplete Callback invoked when authentication is successful, passing the AuthResult.
     * @param onAuthError Callback invoked when an error occurs, passing the Exception.
     */
    fun signIn(
        intent: Intent,
        onAuthComplete: (AuthResult) -> Unit,
        onAuthError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            authModel.signInWithGoogle(intent, onAuthComplete, onAuthError)
        }
    }

    /**
     * Signs out the currently authenticated user.
     */
    fun signOut() {
        authModel.signOut()
    }

    /**
     * Deletes all data associated with the currently authenticated user.
     *
     * @param onComplete Callback invoked when deletion is successful.
     * @param onError Callback invoked when an error occurs, passing the Exception.
     */
    fun deleteUserData(onComplete: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            authModel.deleteUserData(onComplete, onError)
        }
    }
}