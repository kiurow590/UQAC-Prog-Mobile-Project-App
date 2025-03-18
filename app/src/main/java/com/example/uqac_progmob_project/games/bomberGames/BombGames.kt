package com.example.uqac_progmob_project.games.bomberGames

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uqac_progmob_project.R

class BombGames : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameSessionName = intent.getStringExtra("GAMESESSIONNAME") ?: ""
        val playerNames = intent.getStringArrayListExtra("PLAYERSESSIONNAME") ?: listOf()

        println("Game Session Name: $gameSessionName")
        println("Number of Players: ${playerNames.size}")
        println("Player Names: $playerNames")


        setContent {
            BombGamesScreen(gameSessionName, playerNames)
        }
    }
}

@Composable
fun BombGamesScreen(gameSessionName: String, playerNames: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(gameSessionName)
        Spacer(modifier = Modifier.height(16.dp))
        PlayerBubbles(playerNames)
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedBomb()
        Spacer(modifier = Modifier.height(16.dp))
        RandomText()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(gameSessionName: String) {
    TopAppBar(
        title = { Text(text = "NomGame - $gameSessionName") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black
        )
    )
}

@Composable
fun PlayerBubbles(playerNames: List<String>) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(playerNames.size) { index ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.Gray, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_person_24), // Replace with your player icon resource
                            contentDescription = "Player Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = playerNames[index], color = Color.Black, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun AnimatedBomb() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Image(
        painter = painterResource(id = R.drawable.bomb_character_o_idle), // Replace with your bomb image resource
        contentDescription = "Bomb",
        modifier = Modifier
            .size(100.dp)
            .scale(scale)
    )
}

@Composable
fun RandomText() {
    val randomChar = remember { ('A'..'Z').random() }
    Text(text = "$randomChar...", fontSize = 24.sp)
}


@Preview(showBackground = true)
@Composable
fun PreviewBombGamesScreen() {
    BombGamesScreen(
        gameSessionName = "Sample Game",
        playerNames = listOf("Player 1", "Player 2", "Player 3")
    )
}