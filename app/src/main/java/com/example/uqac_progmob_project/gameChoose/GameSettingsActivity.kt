package com.example.uqac_progmob_project.gameChoose

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uqac_progmob_project.BaseActivity
import com.example.uqac_progmob_project.R
import com.example.uqac_progmob_project.games.bomberGames.BombGames
import com.example.uqac_progmob_project.games.triMann.TriMannGame
import com.example.uqac_progmob_project.games.werewolfGame.WerewolfGame

class GameSettingsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val gameName = intent.getStringExtra("GAME_NAME") ?: ""

        setContent {
            GameSettingsScreen(
                gameName = gameName,
                onBackClick = { finish() },
                onPlayClick = { gameSessionName, playerNames ->
                    // Set default values if necessary
                    val finalGameSessionName =
                        if (gameSessionName.isEmpty()) "Default Game Session" else gameSessionName
                    val finalPlayerNames = playerNames.mapIndexed { index, name ->
                        if (name.isEmpty()) "Player ${index + 1}" else name
                    }

                    println("Game Session Name: $finalGameSessionName")
                    println("Number of Players: ${finalPlayerNames.size}")
                    println("Player Names: $finalPlayerNames")

                    when (gameName) {
                        getString(R.string.triMannGame) -> {
                            val intent = Intent(this, TriMannGame::class.java).apply {
                                putExtra("PLAYERSESSIONNAME", ArrayList(finalPlayerNames))
                                putExtra("GAMESESSIONNAME", finalGameSessionName)
                            }
                            startActivity(intent)
                            finish()
                        }

                        getString(R.string.bleiz_garou) -> {
                            val intent = Intent(this, WerewolfGame::class.java).apply {
                                putExtra("PLAYERSESSIONNAME", ArrayList(finalPlayerNames))
                                putExtra("GAMESESSIONNAME", finalGameSessionName)
                            }
                            startActivity(intent)
                            finish()
                        }

                        getString(R.string.course_e_pic) -> {
                            Toast.makeText(this, "Starting Bomber Game", Toast.LENGTH_SHORT).show()
                        }

                        getString(R.string.et_boom) -> {
                            val intent = Intent(this, BombGames::class.java).apply {
                                putExtra("PLAYERSESSIONNAME", ArrayList(finalPlayerNames))
                                putExtra("GAMESESSIONNAME", finalGameSessionName)
                            }
                            startActivity(intent)
                            finish()

                        }

                        else -> {
                            Toast.makeText(this, "Unknown Game: $gameName", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            )
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GameSettingsScreen(
        gameName: String,
        onBackClick: () -> Unit,
        onPlayClick: (String, List<String>) -> Unit
    ) {
        // Couleurs claires en nuances de gris
        val backgroundColor = Color(0xFFF5F5F5)
        val borderGray = Color(0xFFCCCCCC)
        val textGray = Color(0xFF333333)
        val labelGray = Color(0xFF666666)
        val buttonGray = Color(0xFFE0E0E0)
        val minPlayers = if (gameName == stringResource(id = R.string.bleiz_garou)) 8 else 2

        var gameSessionName by remember { mutableStateOf("") }
        var numberOfPlayers by remember { mutableIntStateOf(minPlayers) }
        val playerNames = remember { mutableStateListOf<String>().apply {
            repeat(minPlayers) { add("") }
        } }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = backgroundColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = textGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = gameSessionName,
                    onValueChange = { gameSessionName = it },
                    label = { Text("Nom de la partie", color = labelGray) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = borderGray,
                        focusedBorderColor = textGray,
                        unfocusedTextColor = textGray,
                        focusedTextColor = textGray,
                        cursorColor = textGray,
                        unfocusedLabelColor = labelGray,
                        focusedLabelColor = labelGray
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        if (numberOfPlayers > minPlayers) {
                            numberOfPlayers--
                            playerNames.removeAt(playerNames.size - 1)
                        }
                    }) {
                        Icon(Icons.Default.Remove, contentDescription = "Moins", tint = textGray)
                    }
                    Text(
                        text = "$numberOfPlayers joueurs",
                        fontSize = 16.sp,
                        color = textGray,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = {
                        if (numberOfPlayers < 16) {
                            numberOfPlayers++
                            playerNames.add("")
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Ajouter", tint = textGray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Zone Scrollable
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    for (i in 0 until numberOfPlayers) {
                        OutlinedTextField(
                            value = playerNames[i],
                            onValueChange = { playerNames[i] = it },
                            label = { Text("Nom du joueur ${i + 1}", color = labelGray) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = borderGray,
                                focusedBorderColor = textGray,
                                unfocusedTextColor = textGray,
                                focusedTextColor = textGray,
                                cursorColor = textGray,
                                unfocusedLabelColor = labelGray,
                                focusedLabelColor = labelGray
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onPlayClick(gameSessionName, playerNames) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonGray,
                        contentColor = textGray
                    )
                ) {
                    Text("Jouer", fontSize = 18.sp)
                }
            }
        }
    }
}


@Composable
fun PreviewGameSettingsScreen() {
    GameSettingsActivity().GameSettingsScreen(
        gameName = "Exemple de Jeu",
        onBackClick = { /* Action de retour */ },
        onPlayClick = { gameSessionName, playerNames ->
            println("Nom de la partie : $gameSessionName")
            println("Joueurs : $playerNames")
        }
    )
}