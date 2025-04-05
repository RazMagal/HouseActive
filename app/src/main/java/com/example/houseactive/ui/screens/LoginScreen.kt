package com.example.houseactive.ui.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.houseactive.ui.viewmodels.AuthViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

/**
 * LoginScreen is a composable function that handles user authentication.
 * It allows users to sign in via Google, sign out, and navigate to the main app.
 *
 * @param navController The NavController used for navigation between screens.
 * @param authViewModel The ViewModel responsible for authentication logic.
 */
@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    // State to track the currently signed-in user
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    var openDialog by remember { mutableStateOf(false) }
    // Retrieve the Google Sign-In client ID from resources
    val token = stringResource(com.example.houseactive.R.string.default_web_client_id)
    
    // Get the current context (used for creating the Google Sign-In client)
    val context = LocalContext.current

    // Launcher for handling the result of the Google Sign-In activity
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // Call the AuthViewModel's signIn function to handle authentication
        authViewModel.signIn(result.data!!, { authResult ->
            user = authResult.user
        }, {
            // On authentication error, set the user state to null
            user = null
        })
    }

    // Box layout to center the content on the screen
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        // Column layout to stack UI elements vertically
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Display when the user is not logged in
            if (user == null) {
                Text("Not logged in")
                Spacer(Modifier.height(10.dp))
                Button(onClick = {
                    // Configure Google Sign-In options
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(token) // Request the ID token for Firebase authentication
                        .requestEmail() // Request the user's email
                        .build()
                        
                    // Create a Google Sign-In client
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    
                    // Launch the Google Sign-In activity
                    launcher.launch(googleSignInClient.signInIntent)
                }) {
                    Text("Sign in via Google") // Button text
                }
            } else {
                // Display when the user is logged in
                AsyncImage(
                    model = ImageRequest.Builder(context).data(user!!.photoUrl).build(), // Load the user's profile picture
                    contentScale = ContentScale.Crop,
                    contentDescription = null, // No content description for accessibility
                    modifier = Modifier.size(96.dp).clip(CircleShape) // Circular profile picture
                )
                Spacer(Modifier.height(8.dp))
                Text("Welcome ${user!!.displayName}") // Display the user's name
                Spacer(Modifier.height(10.dp))
                Button(onClick = {
                    // Sign out the user and reset the user state
                    authViewModel.signOut()
                    user = null
                }) {
                    Text("Sign out") // Button text
                }
                Button(onClick = {
                    // Navigate to the task screen
                    navController.navigate("taskScreen")
                }) {
                    Text("Proceed to app") // Button text
                }
                Button(onClick = {
                    openDialog = true
                }) {
                    Text("Delete user data")
                }
                if (openDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            openDialog = false
                        },
                        title = { Text("Are you sure?") },
                        text = { Text("This action will permanently delete your data.") },
                        confirmButton = {
                            TextButton(onClick = {
                                authViewModel.deleteUserData(
                                    onComplete = {
                                        // Successfully deleted the user data.
                                        Toast.makeText(context, "Data deleted Successfully", Toast.LENGTH_LONG).show()

                                    },
                                    onError = { exception ->
                                        // Handle the error as needed (e.g., show a toast message).
                                        Toast.makeText(context, "Error deleting data", Toast.LENGTH_LONG).show()
                                    }
                                )
                                openDialog = false
                                user= null
                            }) {
                                Text("Yes")
                            }
                        },
                        dismissButton = { TextButton(onClick = { openDialog = false }) { Text("No") } }
                    )
                }
            }
        }
    }
}
