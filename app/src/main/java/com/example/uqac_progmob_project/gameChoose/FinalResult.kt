package com.example.uqac_progmob_project.gameChoose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uqac_progmob_project.BaseActivity

class FinalResult : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameSessionName = intent.getStringExtra("GAMESESSIONNAME") ?: "Partie"
        val playerNames = intent.getStringArrayListExtra("PLAYER_NAMES") ?: listOf()
        val playerScores = intent.getIntegerArrayListExtra("PLAYER_SCORES") ?: listOf()

        // Log the incoming values
        Log.d("FinalResult", "gameSessionName: $gameSessionName")
        Log.d("FinalResult", "playerNames: $playerNames")
        Log.d("FinalResult", "playerScores: $playerScores")

        // Associate names and scores into a list of objects
        val players = playerNames.zip(playerScores) { name, score -> PlayerResult(name, score) }

        setContent {
            FinalResultScreen(gameSessionName, players)
        }
    }
}

// Modèle de données pour stocker les résultats des joueurs
data class PlayerResult(val name: String, val score: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalResultScreen(gameSessionName: String, players: List<PlayerResult>) {
    // Trier les joueurs par score décroissant
    val sortedPlayers = players.sortedByDescending { it.score }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text(text = "Résultat - $gameSessionName") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Affichage des résultats sous forme de tableau
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sortedPlayers) { player ->
                ResultRow(player)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { /* TODO: Ajouter la navigation vers le menu principal */ }) {
            Text("Retour au menu")
        }
    }
}

// Composable pour afficher une ligne du tableau des résultats
@Composable
fun ResultRow(player: PlayerResult) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = player.name, fontSize = 18.sp, color = Color.Black)
        Text(text = "${player.score} pts", fontSize = 18.sp, color = Color.Black)
    }
}

// Aperçu pour le mode design
@Preview(showBackground = true)
@Composable
fun PreviewFinalResultScreen() {
    val samplePlayers = listOf(
        PlayerResult("Alice", 15),
        PlayerResult("Bob", 10),
        PlayerResult("Charlie", 8)
    )
    FinalResultScreen(gameSessionName = "Partie Test", players = samplePlayers)
}
