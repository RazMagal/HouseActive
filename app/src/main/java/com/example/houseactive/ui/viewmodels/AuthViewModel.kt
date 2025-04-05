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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore


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

    // Firebase Firestore instance
    private val firestore: FirebaseFirestore = Firebase.firestore

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

                    // Create a user document in Firestore if it doesn't exist
                    val userId = authResult.user!!.uid
                    
                    val userData = mapOf(
                        "id" to userId,
                    )
                    
                    // Check if the user document already exists
                    val userDoc = firestore.collection("users").document(userId).get().await()
                    if (!userDoc.exists()) {
                        // Create the document only if it doesn't exist
                        firestore.collection("users").document(userId).set(userData).await()
                    }

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

    /**
     * Deletes all data associated with the currently authenticated user.
     * This includes the user's Firebase Authentication account and their data in Firestore.
     *
     * @param onComplete Callback invoked when deletion is successful.
     * @param onError Callback invoked when an error occurs, passing the Exception.
     */
    fun deleteUserData(onComplete: () -> Unit, onError: (Exception) -> Unit) {
        viewModelScope.launch {
            val user = auth.currentUser
            if (user != null) {
                try {
                    // Delete user data from Firestore
                    val userDocRef = firestore.collection("users").document(user.uid)
                    userDocRef.delete().await()
                    // Delete the authenticated user's account from Firebase Auth
                    user.delete().await()
                    onComplete()
                } catch (e: Exception) {
                    onError(e)
                }
            } else {
                 // Handle the case where there's no current user
                onError(Exception("No user is currently authenticated"))
            }
        }
    }
}