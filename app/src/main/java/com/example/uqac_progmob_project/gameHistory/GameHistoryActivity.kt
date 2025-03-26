package com.example.uqac_progmob_project.gameHistory
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uqac_progmob_project.BaseActivity
import com.example.uqac_progmob_project.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class GameHistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        setContent {
            val user = FirebaseAuth.getInstance().currentUser
            val userId = user?.uid ?: ""

            val gameHistoryItems = remember { mutableStateOf<List<GameHistoryItem>>(emptyList()) }

            LaunchedEffect(userId) {
                fetchUserGameHistory(userId) { history ->
                    gameHistoryItems.value = history
                }
            }

            GameHistoryScreen(
                historyData = gameHistoryItems.value,
                onBackClick = { finish() }
            )
        }
    }
}

/**
 * Récupère l'historique des parties d'un utilisateur
 */
fun fetchUserGameHistory(userId: String, onResult: (List<GameHistoryItem>) -> Unit) {
    if (userId.isEmpty()) return

    val db = FirebaseFirestore.getInstance()
    db.collection("game_sessions").document(userId).collection("sessions")
        .orderBy("timestamp", Query.Direction.DESCENDING)
        .get()
        .addOnSuccessListener { documents ->
            val historyList = documents.map { document ->
                val gameSessionName = document.getString("gameSessionName") ?: "Partie"
                val results = document.get("results") as List<*>

                val playerScores = results.map {
                    val score = (it as Map<*, *>)["score"]
                    PlayerScore(
                        playerName = it["name"] as String,
                        score = (score as? Long)?.toInt() ?: 0
                    )
                }
                GameHistoryItem(gameSessionName, document.id, playerScores)
            }
            onResult(historyList)
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Erreur lors de la récupération", e)
        }
}

@Composable
fun GameHistoryScreen(historyData: List<GameHistoryItem>, onBackClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.history_game),
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(historyData.size) { index ->
                GameHistoryItemView(historyData[index])
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}


@Composable
fun GameHistoryItemView(gameHistoryItem: GameHistoryItem) {
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded }) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = gameHistoryItem.gameName, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = if (expanded) "Collapse" else "Expand"
            )
        }
        if (expanded) {
            Text(text = gameHistoryItem.sessionName, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // Affichage des 3 premiers scores horizontalement
            Row {
                gameHistoryItem.playerScores.take(3).forEach { playerScore ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = playerScore.playerName, fontSize = 14.sp)
                        Text(text = "${playerScore.score} pts", fontSize = 14.sp)
                    }
                }
            }
            // Affichage des joueurs restants verticalement
            Column {
                gameHistoryItem.playerScores.drop(3).forEach { playerScore ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = playerScore.playerName, fontSize = 14.sp)
                        Text(text = "${playerScore.score} pts", fontSize = 14.sp)
                    }
                }
            }
        }
    }
}


data class GameHistoryItem(val gameName: String, val sessionName: String, val playerScores: List<PlayerScore>)
data class PlayerScore(val playerName: String, val score: Int)