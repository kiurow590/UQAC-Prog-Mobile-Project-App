package com.example.uqac_progmob_project.gameHistory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
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

class GameHistoryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContent {
            GameHistoryScreen(
                historyData = listOf(
                    GameHistoryItem("Game 1", "Session 1", listOf(
                        PlayerScore("Player 1", 100),
                        PlayerScore("Player 2", 80),
                        PlayerScore("Player 3", 60),
                        PlayerScore("Player 4", 60),
                        PlayerScore("Player 5", 60),
                        PlayerScore("Player 6", 60),
                        PlayerScore("Player 7", 60),
                    )),
                    GameHistoryItem("Game 2", "Session 2", listOf(
                        PlayerScore("Player A", 90),
                        PlayerScore("Player B", 70),
                        PlayerScore("Player C", 50)
                    ))
                ),
                onBackClick = { finish() }
            )
        }
    }
}

@Composable
fun GameHistoryScreen(historyData: List<GameHistoryItem>, onBackClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = stringResource(id = R.string.history_game), fontSize = 24.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
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
            // Display the first three player scores in a row
            Row {
                gameHistoryItem.playerScores.take(3).forEach { playerScore ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = playerScore.playerName, fontSize = 14.sp)
                        Text(text = playerScore.score.toString(), fontSize = 14.sp)
                    }
                }
            }
            // Display the remaining player scores in a column
            Column {
                gameHistoryItem.playerScores.drop(3).forEach { playerScore ->
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = playerScore.playerName, fontSize = 14.sp)
                        Text(text = playerScore.score.toString(), fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

data class GameHistoryItem(val gameName: String, val sessionName: String, val playerScores: List<PlayerScore>)
data class PlayerScore(val playerName: String, val score: Int)