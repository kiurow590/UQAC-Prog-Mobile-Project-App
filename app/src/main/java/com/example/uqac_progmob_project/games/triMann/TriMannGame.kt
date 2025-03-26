package com.example.uqac_progmob_project.games.triMann

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uqac_progmob_project.BaseActivity
import com.example.uqac_progmob_project.R

class TriMannGame : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TriMannGame", "Activité démarrée")
        val gameSessionName = intent.getStringExtra("GAMESESSIONNAME") ?: ""
        val playerNames = intent.getStringArrayListExtra("PLAYERSESSIONNAME") ?: listOf()
        Log.d("TriMannGame", "Session: $gameSessionName, Joueurs: $playerNames")

        setContent {
            TriMannGameScreen(gameSessionName, playerNames)
        }
    }
}
@SuppressLint("MutableCollectionMutableState")
@Composable
fun TriMannGameScreen(gameSessionName: String, playerNames: List<String>) {
    val players = remember { playerNames.toMutableList() }
    var currentPlayerIndex by remember { mutableStateOf(0) }
    var diceResult by remember { mutableStateOf(0) }
    var gameStarted by remember { mutableStateOf(false) }
    var playerScores by remember { mutableStateOf(IntArray(players.size) { 0 }.toMutableList()) }
    var gameEnded by remember { mutableStateOf(false) }

    // Lancer des dés
    fun rollDice(): Pair<Int, Int> {
        return (1..6).random() to (1..6).random() // Générer deux nombres indépendants
    }
    // Règle du Tri Mann et actions spécifiques selon les dés
    fun applyGameRules(dice1: Int, dice2: Int) {
        // Si c'est un 3 ou une somme de 3, le joueur devient le Tri Mann
        if (dice1 == 3 || dice2 == 3 || (dice1 + dice2 == 3)) {
            // Le joueur courant devient le Tri Mann
            Log.d("TriMannGame", "${players[currentPlayerIndex]} devient le Tri Mann !")
        }

        // Vérifier si c'est un double impair (boire)
        if (dice1 % 2 != 0 && dice2 % 2 != 0) {
            Log.d("TriMannGame", "${players[currentPlayerIndex]} doit boire deux gorgées (double impair) !")
        }

        // Vérifier si c'est un double pair (distribuer)
        if (dice1 % 2 == 0 && dice1 == dice2) {
            Log.d("TriMannGame", "${players[currentPlayerIndex]} doit distribuer !")
        }

        // Vérifier si un 6 est lancé, et gérer la règle des gorgées
        if (dice1 == 6 || dice2 == 6) {
            if (dice1 == 6 && dice2 == 6) {
                Log.d("TriMannGame", "Deux 6 lancés, chifoumi entre deux joueurs !")
                // Implémenter un "chifoumi" ici
            } else {
                val total = if (dice1 == 6) dice2 else dice1
                Log.d("TriMannGame", "Posez $total gorgées sur la table")
            }
        }

        // Règle du 7 et du 9 : affecte le joueur précédent ou suivant
        if (dice1 == 7 || dice2 == 7) {
            val previousPlayerIndex = (currentPlayerIndex - 1 + players.size) % players.size
            Log.d("TriMannGame", "${players[currentPlayerIndex]} passe au joueur précédent ${players[previousPlayerIndex]}")
        } else if (dice1 == 9 || dice2 == 9) {
            val nextPlayerIndex = (currentPlayerIndex + 1) % players.size
            Log.d("TriMannGame", "${players[currentPlayerIndex]} passe au joueur suivant ${players[nextPlayerIndex]}")
        }

        // Appliquer la règle des gorgées pour chaque lancer de dés
        if (dice1 != dice2) {
            Log.d("TriMannGame", "${players[currentPlayerIndex]} doit boire 2 gorgées")
        }
    }

    // Mise à jour de l'updateScore pour appliquer les règles
    fun updateScore() {
        val (dice1, dice2) = rollDice() // Obtenir les résultats des deux dés
        diceResult = dice1 + dice2 // Calculer le total des dés
        playerScores[currentPlayerIndex] += diceResult

        // Appliquer les règles du jeu après chaque lancer de dés
        applyGameRules(dice1, dice2)

        Log.d("TriMannGame", "Joueur ${players[currentPlayerIndex]} a lancé un $dice1 et un $dice2, total = $diceResult")
    }

    // Changer de joueur
    fun nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size
    }

    // Affichage des résultats
    LaunchedEffect(gameStarted) {
        if (gameStarted && !gameEnded) {
            updateScore()
            if (players.size == 1) {
                gameEnded = true
            } else {
                nextPlayer()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Session de Jeu: $gameSessionName", fontSize = 24.sp)
        Spacer(modifier = Modifier.height(20.dp))

        if (gameStarted) {
            Text(text = "Tour de ${players[currentPlayerIndex]}", fontSize = 20.sp)
            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = { updateScore() }) {
                Text(text = "Lancer les dés")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Affichage des résultats des deux dés
            Text(text = "Résultats des dés: $diceResult", fontSize = 18.sp)
            Text(text = "Dé 1: ${diceResult / 2}, Dé 2: ${diceResult / 2}", fontSize = 16.sp) // Optionally split the result
            Spacer(modifier = Modifier.height(10.dp))

            // Affichage des scores
            playerScores.forEachIndexed { index, score ->
                Text(text = "${players[index]} : $score", fontSize = 16.sp)
            }
        } else {
            Button(onClick = { gameStarted = true }) {
                Text(text = "Démarrer le jeu")
            }
        }

        if (gameEnded) {
            Text(text = "Le jeu est terminé !", fontSize = 24.sp)
            val winner = players[playerScores.indexOf(playerScores.maxOrNull()!!)]
            Text(text = "Le gagnant est : $winner", fontSize = 20.sp)
        }
    }

}


// Composant pour afficher l'image d'un dé
@Composable
fun DiceImage(diceNumber: Int) {
    val diceImage = when (diceNumber) {
        1 -> painterResource(id = R.drawable.dice1)
        2 -> painterResource(id = R.drawable.dice2)
        3 -> painterResource(id = R.drawable.dice3)
        4 -> painterResource(id = R.drawable.dice4)
        5 -> painterResource(id = R.drawable.dice5)
        6 -> painterResource(id = R.drawable.dice6)
        else -> painterResource(id = R.drawable.dice1)
    }

    Image(
        painter = diceImage,
        contentDescription = "Dice",
        modifier = Modifier.size(100.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTrimannGameScreen() {
    TriMannGameScreen(
        gameSessionName = "Partie Test",
        playerNames = listOf("Alice", "Bob", "Charlie")
    )
}