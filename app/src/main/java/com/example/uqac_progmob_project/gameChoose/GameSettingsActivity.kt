package com.example.uqac_progmob_project.gameChoose

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uqac_progmob_project.BaseActivity
import com.example.uqac_progmob_project.R
import com.example.uqac_progmob_project.games.bomberGames.BombGames
import com.example.uqac_progmob_project.games.triMann.TriMannGame

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
                    val finalGameSessionName = if (gameSessionName.isEmpty()) "Default Game Session" else gameSessionName
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
                        }
                        getString(R.string.bleiz_garou) -> {
                            Toast.makeText(this, "Starting Bomber Game", Toast.LENGTH_SHORT).show()
                        }
                        getString(R.string.course_e_pic) -> {
                            Toast.makeText(this, "Starting Bomber Game", Toast.LENGTH_SHORT).show()
                        }
                        getString(R.string.et_boom) -> {
                            val intent = Intent(this, BombGames::class.java).apply {
                                putExtra("PLAYERSESSIONNAME", ArrayList(finalPlayerNames))
                                putExtra("GAMESESSIONNAME", finalGameSessionName)
                            }

                        }
                        else -> {
                            Toast.makeText(this, "Unknown Game: $gameName", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun GameSettingsScreen(
        gameName: String,
        onBackClick: () -> Unit,
        onPlayClick: (String, List<String>) -> Unit
    ) {
        var gameSessionName by remember { mutableStateOf("") }
        var numberOfPlayers by remember { mutableStateOf(2) }
        val playerNames = remember { mutableStateListOf("", "") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            //verticalArrangement = Arrangement.Center,
            //horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = gameName, fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            BasicTextField(
                value = gameSessionName,
                onValueChange = { gameSessionName = it },
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        if (gameSessionName.isEmpty()) {
                            Text(text = "Game Session Name", color = Color.Gray)
                        }
                        innerTextField()
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    if (numberOfPlayers > 2) {
                        numberOfPlayers--
                        playerNames.removeAt(playerNames.size - 1)
                    }
                }) {
                    Text(text = "-")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = numberOfPlayers.toString(), fontSize = 16.sp)
                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = {
                    if (numberOfPlayers < 5) {
                        numberOfPlayers++
                        playerNames.add("")
                    }
                }) {
                    Text(text = "+")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                for (i in 0 until numberOfPlayers) {
                    BasicTextField(
                        value = playerNames[i],
                        onValueChange = { playerNames[i] = it },
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                if (playerNames[i].isEmpty()) {
                                    Text(text = "Player Name ${i + 1}", color = Color.Gray)
                                }
                                innerTextField()
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onPlayClick(gameSessionName, playerNames) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Play")
            }
        }
    }
}