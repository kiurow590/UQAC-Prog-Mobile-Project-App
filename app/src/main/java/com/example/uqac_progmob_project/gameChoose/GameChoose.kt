package com.example.uqac_progmob_project.gameChoose

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uqac_progmob_project.gameHistory.GameHistoryActivity
import com.example.uqac_progmob_project.R
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.google.firebase.auth.FirebaseAuth


class GameChoose : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContent {
            var showDialog by remember { mutableStateOf(false) }
            val user = FirebaseAuth.getInstance().currentUser

            GameChooseScreen(
                onBackClick = { finish() },
                onHistoryClick = {
                    startActivity(Intent(this, GameHistoryActivity::class.java))
                },
                onAccountClick = {
                    showDialog = true
                },
                onGameClick = { gameName, gameDescription ->
                    startGameDetailActivity(gameName, gameDescription)
                }
            )

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

    private fun startGameDetailActivity(gameName: String, gameDescription: String) {
        val intent = Intent(this, GameDetailActivity::class.java).apply {
            putExtra("GAME_NAME", gameName)
            putExtra("GAME_DESCRIPTION", gameDescription)
        }
        startActivity(intent)
    }
}

@Composable
fun GameChooseScreen(
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onAccountClick: () -> Unit,
    onGameClick: (String, String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TopBanner(
            onBackClick = onBackClick,
            onHistoryClick = onHistoryClick,
            onAccountClick = onAccountClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Liste des jeux sous forme de grille 2x2
        val games = listOf(
            Triple(stringResource(id = R.string.triMannGame), stringResource(id = R.string.triMannGameDescription), false),
            Triple(stringResource(id = R.string.bleiz_garou), stringResource(id = R.string.bleiz_garou_Description), false),
            Triple(stringResource(id = R.string.course_e_pic), stringResource(id = R.string.course_e_pic_description), true),
            Triple(stringResource(id = R.string.et_boom), stringResource(id = R.string.et_boom_description), false)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(games) { (title, description, isLocked) ->
                GameButton(text = title, description = description, onClick = onGameClick, isLocked = isLocked)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBanner(
    onBackClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    TopAppBar(
        title = {stringResource(id = R.string.gamechoose) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        },
        actions = {
            IconButton(onClick = onHistoryClick) {
                Icon(Icons.Filled.Info, contentDescription = "History")
            }
            IconButton(onClick = onAccountClick) {
                Icon(Icons.Default.Person, contentDescription = "Account")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            actionIconContentColor = Color.Black
        )
    )
}

@Composable
fun GameButton(text: String, description: String, onClick: (String, String) -> Unit, isLocked: Boolean = false) {
    Button(
        onClick = { if (!isLocked) onClick(text, description) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        enabled = !isLocked
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(text, modifier = Modifier.align(Alignment.CenterStart))
            if (isLocked) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGameChooseScreen() {
    GameChooseScreen(
        onBackClick = {},
        onHistoryClick = {},
        onAccountClick = {},
        onGameClick = { _, _ -> }
    )
}