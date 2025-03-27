package com.example.uqac_progmob_project.gameChoose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uqac_progmob_project.BaseActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FinalResult : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        val gameSessionName = intent.getStringExtra("GAMESESSIONNAME") ?: "Partie"
        val gameType = intent.getStringExtra("GAMETYPE") ?: "Unknown"
        Log.d("FinalResult", "Game Type: $gameType")
        val playerNames = intent.getStringArrayListExtra("PLAYER_NAMES") ?: listOf()
        val playerScores = intent.getIntegerArrayListExtra("PLAYER_SCORES") ?: listOf()

        val players = playerNames.zip(playerScores) { name, score -> PlayerResult(name, score) }

        // Save results to Firestore
        saveResultsToFirestore(gameSessionName, players, gameType)

        setContent {
            FinalResultScreen(gameSessionName, players, this)
        }
    }
}

// Modèle de données pour stocker les résultats des joueurs
data class PlayerResult(val name: String, val score: Int)


fun saveResultsToFirestore(gameSessionName: String, players: List<PlayerResult>, gameType: String) {
    val user = FirebaseAuth.getInstance().currentUser
    val userId = user?.uid ?: return // If the user is not logged in, do nothing

    val db = FirebaseFirestore.getInstance()
    val sessionRef = db.collection("game_sessions").document(userId).collection("sessions").document(gameSessionName)

    val results = players.map { player ->
        mapOf("name" to player.name, "score" to player.score)
    }

    val data = hashMapOf(
        "gameSessionName" to gameSessionName,
        "gameType" to gameType,
        "results" to results,
        "timestamp" to FieldValue.serverTimestamp()
    )

    sessionRef.set(data)
        .addOnSuccessListener {
            Log.d("Firestore", "Results successfully saved")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Error saving results", e)
        }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalResultScreen(gameSessionName: String, players: List<PlayerResult>, activity: ComponentActivity) {
    // Sort players by descending score
    val sortedPlayers = players.sortedByDescending { it.score }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text(text = "Résultat - $gameSessionName") },
            navigationIcon = {
                IconButton(onClick = { activity.finish() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display results in a table
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sortedPlayers) { player ->
                ResultRow(player)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { /* TODO: Add navigation to the main menu */ }) {
            Text("Retour au menu")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { activity.finish() }) {
            Text("Quitter l'application")
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
    FinalResultScreen(
        gameSessionName = "Partie Test", players = samplePlayers,
        activity = ComponentActivity()
    )
}
