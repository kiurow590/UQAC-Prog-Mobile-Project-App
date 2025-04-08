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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
                val category = document.getString("gameType") ?: "Unknown"
                val results = document.get("results") as List<*>

                val playerScores = results.map {
                    val score = (it as Map<*, *>)["score"]
                    PlayerScore(
                        playerName = it["name"] as String,
                        score = (score as? Long)?.toInt() ?: 0
                    )
                }
                GameHistoryItem(gameSessionName, document.id, playerScores, category)
            }
            onResult(historyList)
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Erreur lors de la récupération", e)
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameHistoryScreen(historyData: List<GameHistoryItem>, onBackClick: () -> Unit) {
    val groupedHistory = historyData.groupBy { it.category }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TopAppBar(
                    title = { stringResource(id = R.string.gamechoose) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White,
                        titleContentColor = Color.Black,
                        actionIconContentColor = Color.Black
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Texte encadré sous la TopAppBar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(id = R.string.history_game),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(16.dp),
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    groupedHistory.forEach { (category, items) ->
                        item {
                            CategoryItemView(category, items)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CategoryItemView(category: String, items: List<GameHistoryItem>) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier
            .clickable { expanded = !expanded }
            .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = category,
                    fontSize = 20.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                items.forEach { gameHistoryItem ->
                    GameHistoryItemView(gameHistoryItem)
                }
            }
        }
    }
}



@Composable
fun GameHistoryItemView(gameHistoryItem: GameHistoryItem) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { expanded = !expanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = gameHistoryItem.gameName,
                    fontSize = 18.sp,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Session: ${gameHistoryItem.sessionName}",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    gameHistoryItem.playerScores.take(3).forEach { playerScore ->
                        Column {
                            Text(
                                text = playerScore.playerName,
                                fontSize = 14.sp,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${playerScore.score} pts",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                gameHistoryItem.playerScores.drop(3).forEach { playerScore ->
                    Column(modifier = Modifier.padding(top = 8.dp)) {
                        Text(
                            text = playerScore.playerName,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${playerScore.score} pts",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewGameHistoryScreen() {
    val sampleData = listOf(
        GameHistoryItem(
            gameName = "Partie 1",
            sessionName = "Session 1",
            playerScores = listOf(
                PlayerScore("Joueur 1", 100),
                PlayerScore("Joueur 2", 80),
                PlayerScore("Joueur 3", 60)
            ),
            category = "Catégorie A"
        ),
        GameHistoryItem(
            gameName = "Partie 2",
            sessionName = "Session 2",
            playerScores = listOf(
                PlayerScore("Joueur 1", 120),
                PlayerScore("Joueur 2", 90)
            ),
            category = "Catégorie B"
        )
    )

    GameHistoryScreen(
        historyData = sampleData,
        onBackClick = {}
    )
}


data class GameHistoryItem(val gameName: String, val sessionName: String, val playerScores: List<PlayerScore>, val category: String)
data class PlayerScore(val playerName: String, val score: Int)

