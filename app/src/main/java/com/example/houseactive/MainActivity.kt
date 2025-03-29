package com.example.houseactive

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.example.houseactive.ui.theme.FirebaseAuthTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // This sets the content of the activity using Jetpack Compose
        setContent {
            // FirebaseAuthTheme is a custom theme for our app
            FirebaseAuthTheme {
                // State to hold the current user, starts as the currently signed-in user or null
                var user by remember { mutableStateOf(Firebase.auth.currentUser) }
                // This creates a launcher for handling the sign-in intent and processing the result
                val launcher = rememberFirebaseAuthLauncher(
                    // Callback for when the sign-in is successful
                    onAuthComplete = { result ->
                        user = result.user
                    },
                    // Callback for when there's an error during sign-in
                    onAuthError = {
                        // If there's an error, we set user to null, effectively signing them out
                        // We set to null because we don't know if they have a user
                        user = null
                    }
                )
                val token = stringResource(R.string.default_web_client_id)
                val context = LocalContext.current
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                    ) {
                        // If the user is not logged in (user is null)
                        if (user == null) {
                            Text("Not logged in")
                            Spacer(Modifier.height(10.dp))
                            // Button to trigger the Google sign-in process
                            Button(onClick = {
                                // Configure the Google Sign-In options
                                val gso =
                                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                        // We want an ID token to verify the user with our backend
                                        .requestIdToken(token)
                                        // we want the email of the user for the app
                                        .requestEmail()
                                        .build()
                                // Creates a Google sign-in client with the specified options
                                val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                // Launches the sign-in intent, which opens a Google sign-in window
                                launcher.launch(googleSignInClient.signInIntent)
                            }) {
                                Text("Sign in via Google")
                            }
                        } // end if user is null
                        else {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(user!!.photoUrl)
                                    .build(),
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("Welcome ${user!!.displayName}")
                            Spacer(Modifier.height(10.dp))
                            // Button to sign the user out
                            Button(onClick = {
                                // Signs the user out of Firebase
                                Firebase.auth.signOut()
                                // Resets the user state to null
                                user = null
                                }) {
                                Text("Sign out")
                            }
                        }
                    }
                }
            }
        }
    }

    // This is a composable function for managing the Google Sign-In flow
    @Composable
    fun rememberFirebaseAuthLauncher(
        // Callback for when the sign-in is successful
        onAuthComplete: (AuthResult) -> Unit,
        // Callback for when there's an error during sign-in
        onAuthError: (ApiException) -> Unit,
    ): ManagedActivityResultLauncher<Intent, ActivityResult> {
        // we need a coroutine for the async operation
        val scope = rememberCoroutineScope()
        // rememberLauncherForActivityResult creates a launcher to start an activity and get a result
        return rememberLauncherForActivityResult(StartActivityForResult()) { result ->
            // Tries to get the signed-in account from the intent data
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // If getting the account was successful, we proceed
                val account = task.getResult(ApiException::class.java)!!
                // Gets the credential for Firebase authentication
                val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                // We create a new coroutine
                scope.launch {
                    // Signs in to Firebase with the Google credential (this is an asynchronous operation)
                    val authResult = Firebase.auth.signInWithCredential(credential).await()
                    // Calls the completion callback with the result of the Firebase sign-in
                    onAuthComplete(authResult)
                }
            } catch (e: ApiException) {
                onAuthError(e)
            }
        }
    }
}