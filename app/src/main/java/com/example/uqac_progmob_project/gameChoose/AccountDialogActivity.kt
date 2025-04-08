// app/src/main/java/com/example/uqac_progmob_project/AccountDialogFragment.kt
package com.example.uqac_progmob_project.gameChoose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.uqac_progmob_project.R

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
        title = {
            Text(
                text = stringResource(id = R.string.account),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar
                if (userProfilePicture != null) {
                    Image(
                        painter = rememberAsyncImagePainter(userProfilePicture),
                        contentDescription = "Account Picture",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_person_24),
                        contentDescription = "Default Account Picture",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .padding(12.dp)
                    )
                }

                // Nom & Email
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.DarkGray
                    )
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmClick) {
                Text("OK", style = MaterialTheme.typography.labelLarge)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color(0xFFF7F7F7),
        tonalElevation = 6.dp
    )
}
