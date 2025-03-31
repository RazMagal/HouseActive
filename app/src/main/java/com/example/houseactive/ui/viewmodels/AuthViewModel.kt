package com.example.houseactive.ui.viewmodels

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


/**
 * AuthViewModel is responsible for managing user authentication.
 * It provides methods for signing in with Google and signing out.
 * 
 * This ViewModel interacts with Firebase Authentication to handle user authentication.
 */
class AuthViewModel : ViewModel() {
    // Firebase Authentication instance
    val auth = Firebase.auth

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
        // Retrieve the Google Sign-In account from the intent
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        try {
            // Get the Google account from the task result
            val account = task.getResult(ApiException::class.java)!!
            // Create a Firebase credential using the Google account's ID token
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            // Launch a coroutine to perform the Firebase sign-in operation
            viewModelScope.launch {
                try {
                    // Sign in with the credential and await the result
                    val authResult = auth.signInWithCredential(credential).await()
                    // Invoke the success callback with the authentication result
                    onAuthComplete(authResult)
                } catch (e: Exception) {
                    // Invoke the error callback if an exception occurs
                    onAuthError(e)
                }
            }
        } catch (e: ApiException) {
            // Handle errors related to Google Sign-In
            onAuthError(e)
        }
    }

   /**
     * Signs out the currently authenticated user.
     */
    fun signOut() {
        auth.signOut() // Firebase sign-out
    }
}