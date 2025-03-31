package com.example.uqac_progmob_project.mainActivity


import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.lifecycleScope
import com.example.uqac_progmob_project.BaseActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity() {

    private val googleAuthHelper by lazy { GoogleAuthHelper(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContent {
            var user by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

            LaunchedEffect(user) {
                user = FirebaseAuth.getInstance().currentUser
            }

            if (user == null) {
                SignInScreen {
                    googleAuthHelper.signInWithGoogle(lifecycleScope)
                }
            } else {
                MainScreen()
            }
        }
    }
}