import android.content.Intent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.uqac_progmob_project.R // Remplace par ton vrai package
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.uqac_progmob_project.GameChoose
import com.example.uqac_progmob_project.settings.SettingsDialog

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

/**
 * Preview function for the MainScreen
 */
@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MainScreen()
}