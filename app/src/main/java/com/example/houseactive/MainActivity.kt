// MainActivity.kt  
package com.example.houseactive

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

        setContent {
            FirebaseAuthTheme {
                // Navigation controller to manage navigation between screens
                val navController = rememberNavController()

                // Setting up the navigation graph
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { LoginScreen(navController) }
                    composable("taskScreen") { TaskScreen() }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavHostController) {
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }

    // Firebase Auth launcher for Google Sign-In
    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result -> user = result.user },
        onAuthError = { user = null }
    )
    val token = stringResource(R.string.default_web_client_id)
    val context = LocalContext.current

    // Layout for the login screen
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (user == null) {
                Text("Not logged in")
                Spacer(Modifier.height(10.dp))
                Button(onClick = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(token)
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                }) {
                    Text("Sign in via Google")
                }
            } else {
                // Show profile picture and welcome message
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(user!!.photoUrl)
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp).clip(CircleShape)
                )
                Spacer(Modifier.height(8.dp))
                Text("Welcome ${user!!.displayName}")
                Spacer(Modifier.height(10.dp))
                Button(onClick = {
                    Firebase.auth.signOut()
                    user = null
                }) {
                    Text("Sign out")
                }

                // Navigate to the TaskScreen after login
                LaunchedEffect(user) {
                    navController.navigate("taskScreen")
                }
            }
        }
    }
}

@Composable
fun TaskScreen() {
    // Mock list of tasks
    val tasks = listOf(
        "Buy groceries",
        "Walk the dog",
        "Complete Kotlin project",
        "Read a book",
        "Workout"
    )

    // Layout for displaying tasks in rows
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Your Tasks", modifier = Modifier.padding(bottom = 8.dp))
        for (task in tasks) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(task) // Task description
                Button(onClick = { /* Handle task click */ }) {
                    Text("Done")
                }
            }
        }
    }
}

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}
