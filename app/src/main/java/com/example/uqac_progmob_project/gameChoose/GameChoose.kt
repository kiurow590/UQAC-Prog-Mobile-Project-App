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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uqac_progmob_project.AccountDialogFragment
import com.example.uqac_progmob_project.GameDetailActivity
import com.example.uqac_progmob_project.GameHistoryActivity
import com.example.uqac_progmob_project.R

class GameChoose : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContent {
            GameChooseScreen(
                onBackClick = { finish() },
                onHistoryClick = {
                    startActivity(Intent(this, GameHistoryActivity::class.java))
                },
                onAccountClick = {
                    val dialog = AccountDialogFragment()
                    dialog.show(supportFragmentManager, "AccountDialogFragment")
                },
                onGameClick = { gameName, gameDescription ->
                    startGameDetailActivity(gameName, gameDescription)
                }
            )
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
            stringResource(id = R.string.triMannGame) to stringResource(id = R.string.triMannGameDescription),
            stringResource(id = R.string.bleiz_garou) to stringResource(id = R.string.bleiz_garou_Description),
            stringResource(id = R.string.course_e_pic) to stringResource(id = R.string.course_e_pic_description),
            stringResource(id = R.string.et_boom) to stringResource(id = R.string.et_boom_description)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // ✅ Grille 2x2
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(games) { (title, description) ->
                GameButton(text = title, description = description, onClick = onGameClick)
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
        title = { Text("Game Choose") },
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
fun GameButton(text: String, description: String, onClick: (String, String) -> Unit) {
    Button(
        onClick = { onClick(text, description) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text)
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