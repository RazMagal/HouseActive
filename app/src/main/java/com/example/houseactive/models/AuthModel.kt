package com.example.houseactive.models

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

/**
 * AuthModel handles all authentication and Firestore operations.
 * It acts as the data layer for authentication-related logic.
 */
class AuthModel {
    private val auth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    /**
     * Signs in the user with Google credentials.
     *
     * @param intent The intent returned from the Google Sign-In activity.
     * @param onAuthComplete Callback invoked when authentication is successful, passing the AuthResult.
     * @param onAuthError Callback invoked when an error occurs, passing the Exception.
     */
    suspend fun signInWithGoogle(intent: Intent, onAuthComplete: (AuthResult) -> Unit, onAuthError: (Exception) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            val authResult = auth.signInWithCredential(credential).await()
            onAuthComplete(authResult)

            val userId = authResult.user!!.uid
            val userData = mapOf("id" to userId)

            val userDoc = firestore.collection("users").document(userId).get().await()
            if (!userDoc.exists()) {
                firestore.collection("users").document(userId).set(userData).await()
            }
        } catch (e: Exception) {
            onAuthError(e)
        }
    }

    /**
     * Signs out the currently authenticated user.
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Deletes all data associated with the currently authenticated user.
     *
     * @param onComplete Callback invoked when deletion is successful.
     * @param onError Callback invoked when an error occurs, passing the Exception.
     */
    suspend fun deleteUserData(onComplete: () -> Unit, onError: (Exception) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            try {
                firestore.clearPersistence()
                val userDocRef = firestore.collection("users").document(user.uid)
                userDocRef.delete().await()
                user.delete().await()
                onComplete()
            } catch (e: Exception) {
                onError(e)
            }
        } else {
            onError(Exception("No user is currently authenticated"))
        }
    }
}