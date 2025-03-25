// app/src/main/java/com/example/uqac_progmob_project/AccountDialogFragment.kt
package com.example.uqac_progmob_project.gameChoose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.uqac_progmob_project.R
import coil.compose.rememberImagePainter

import com.google.firebase.auth.FirebaseAuth

class AccountDialogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val user = FirebaseAuth.getInstance().currentUser
            var showDialog by remember { mutableStateOf(true) }

            if (showDialog && user != null) {
                AccountDialog(
                    userName = user.displayName ?: "Unknown",
                    userEmail = user.email ?: "Unknown",
                    userProfilePicture = user.photoUrl?.toString(),
                    onDismissRequest = { showDialog = false },
                    onConfirmClick = { showDialog = false }
                )
            }
        }
    }
}

@Composable
fun AccountDialog(
    userName: String,
    userEmail: String,
    userProfilePicture: String?,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(id = R.string.account)) },
        text = {
            Column {
                if (userProfilePicture != null) {
                    Image(
                        painter = rememberImagePainter(userProfilePicture),
                        contentDescription = "Account Picture",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = "Account Picture",
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                Text("User Name: $userName")
                Text("Email: $userEmail")
                Text("Score Global: 100")
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text("OK")
            }
        }
    )
}