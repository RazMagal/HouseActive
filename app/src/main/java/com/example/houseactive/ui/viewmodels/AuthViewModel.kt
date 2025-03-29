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

class AuthViewModel : ViewModel() {
    val auth = Firebase.auth

    // Sign-in function using Google
    fun signIn(intent: Intent, onAuthComplete: (AuthResult) -> Unit, onAuthError: (Exception) -> Unit) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)

            viewModelScope.launch {
                try {
                    val authResult = auth.signInWithCredential(credential).await()
                    onAuthComplete(authResult)
                } catch (e: Exception) {
                    onAuthError(e)
                }
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }

    // Sign-out function
    fun signOut() {
        auth.signOut()
    }
}