package com.example.uqac_progmob_project.mainActivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import com.example.uqac_progmob_project.BaseActivity
import com.example.uqac_progmob_project.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val user = FirebaseAuth.getInstance().currentUser

        setContent {
            if (user == null) {
                SignInScreen {
                    recreate() // Recharge l'activity après connexion
                }
            } else {
                MainScreen()
            }
        }
    }
}

fun signInWithGoogle(context: Context, launcher: ActivityResultLauncher<Intent>) {
    val webClientId = context.getString(R.string.web_client_id)
    val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(webClientId)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, options)
    val signInIntent = googleSignInClient.signInIntent
    launcher.launch(signInIntent)
}
