package com.example.houseactive.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.example.houseactive.ui.viewmodels.AuthViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

@Composable
fun LoginScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var user by remember { mutableStateOf(Firebase.auth.currentUser) }
    val token = stringResource(com.example.houseactive.R.string.default_web_client_id)
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        authViewModel.signIn(result.data!!, { authResult ->
            user = authResult.user
        }, {
            user = null
        })
    }

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
                AsyncImage(
                    model = ImageRequest.Builder(context).data(user!!.photoUrl).build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier.size(96.dp).clip(CircleShape)
                )
                Spacer(Modifier.height(8.dp))
                Text("Welcome ${user!!.displayName}")
                Spacer(Modifier.height(10.dp))
                Button(onClick = {
                    authViewModel.signOut()
                    user = null
                }) {
                    Text("Sign out")
                }

                LaunchedEffect(user) {
                    navController.navigate("taskScreen")
                }
            }
        }
    }
}
