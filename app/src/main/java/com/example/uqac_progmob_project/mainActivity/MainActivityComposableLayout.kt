package com.example.uqac_progmob_project.mainActivity

import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.uqac_progmob_project.R
import com.example.uqac_progmob_project.gameChoose.GameChoose
import com.example.uqac_progmob_project.settings.SettingsDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

/**
 * Composable function that defines the layout of the Main Screen
 */
@Composable
fun MainScreen() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val (image, greenButton, grayButton, quitButton, settingsButton) = createRefs()

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = stringResource(id = R.string.main_image_description),
            modifier = Modifier
                .size(200.dp)
                .constrainAs(image) {
                    top.linkTo(parent.top, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        Button(
            onClick = {
                Toast.makeText(context, "Bouton Vert cliqué !", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, GameChoose::class.java)
                context.startActivity(intent)
            },
            modifier = Modifier
                .constrainAs(greenButton) {
                    top.linkTo(image.bottom, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text(text = stringResource(id = R.string.green_button_text))
        }

        Button(
            onClick = {
                Toast.makeText(context, "This feature is not available yet", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .constrainAs(grayButton) {
                    top.linkTo(greenButton.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text(text = stringResource(id = R.string.gray_button_text))
        }

        Button(
            onClick = {
                (context as? ComponentActivity)?.finishAndRemoveTask()
            },
            modifier = Modifier
                .constrainAs(quitButton) {
                    top.linkTo(grayButton.bottom, margin = 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            Text(text = stringResource(id = R.string.quit))
        }

        IconButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .constrainAs(settingsButton) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    end.linkTo(parent.end)
                }
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = stringResource(id = R.string.settings_button_description)
            )
        }
    }

    if (showDialog) {
        SettingsDialog(
            onDismissRequest = { showDialog = false },
            onConfirm = { showDialog = false }
        )
    }
}

@Composable
fun SignInScreen(onSignInSuccess: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential)
                .addOnCompleteListener { authResult ->
                    if (authResult.isSuccessful) {
                        onSignInSuccess()
                    } else {
                        Toast.makeText(context, "Connexion échouée", Toast.LENGTH_SHORT).show()
                    }
                }
        } catch (e: ApiException) {
            Toast.makeText(context, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { signInWithGoogle(context, launcher) }
        ) {
            Text("Se connecter avec Google")
        }
    }
}

/**
 * Preview function for the MainScreen
 */
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}