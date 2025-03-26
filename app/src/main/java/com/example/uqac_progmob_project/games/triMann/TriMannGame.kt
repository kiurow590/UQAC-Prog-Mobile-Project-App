package com.example.uqac_progmob_project.games.triMann

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uqac_progmob_project.BaseActivity
import com.example.uqac_progmob_project.R
import kotlin.random.Random

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
@Composable
fun DiceImage(diceNumber: Int, rotation: Float) {
    // Utilisation d'une animation pour la rotation
    val rotationAnimation by animateFloatAsState(targetValue = rotation, animationSpec = tween(durationMillis = 500))

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
        modifier = Modifier
            .size(100.dp)
            .rotate(rotationAnimation)
    )
}

@SuppressLint("MutableCollectionMutableState")
@Composable
fun TriMannGameScreen(gameSessionName: String, playerNames: List<String>) {
    val players = remember { playerNames.toMutableList() }
    var currentPlayerIndex by remember { mutableStateOf(0) }
    var dice1Result by remember { mutableStateOf(0) }
    var dice2Result by remember { mutableStateOf(0) }
    var gameStarted by remember { mutableStateOf(false) }
    var rotation1 by remember { mutableStateOf(0f) }
    var rotation2 by remember { mutableStateOf(0f) }
    var playerScores by remember { mutableStateOf(IntArray(players.size) { 0 }.toMutableList()) }
    var gameEnded by remember { mutableStateOf(false) }
    var roundEnded by remember { mutableStateOf(false) }

    // Fonction pour appliquer les règles et déterminer si le tour continue ou s'il est terminé
    fun applyGameRules(dice1: Int, dice2: Int): Boolean {
        var didPlayerDrink = false  // Variable pour suivre si le joueur doit boire
        var continueRound = true    // Variable pour déterminer si le tour continue

        // Vérifier si c'est un double
        if (dice1 == dice2) {
            Log.d("TriMannGame", "${players[currentPlayerIndex]} a lancé un double!")
        }

        // Si c'est un 3 ou une somme de 3, le joueur devient le Tri Mann
        if (dice1 == 3 || dice2 == 3 || (dice1 + dice2 == 3)) {
            Log.d("TriMannGame", "${players[currentPlayerIndex]} devient le Tri Mann !")
        }

        // Vérifier si c'est un 7 ou un 9 (changer de joueur)
        if (dice1 == 7 || dice2 == 7 || (dice1 + dice2 == 7)) {
            val previousPlayerIndex = (currentPlayerIndex - 1 + players.size) % players.size
            Log.d("TriMannGame", "${players[currentPlayerIndex]} passe au joueur précédent ${players[previousPlayerIndex]}")
            currentPlayerIndex = previousPlayerIndex // Changer de joueur
            continueRound = false  // Fin du tour, passage au joueur suivant
        } else if (dice1 == 9 || dice2 == 9 || (dice1 + dice2 == 9)) {
            val nextPlayerIndex = (currentPlayerIndex + 1) % players.size
            Log.d("TriMannGame", "${players[currentPlayerIndex]} passe au joueur suivant ${players[nextPlayerIndex]}")
            currentPlayerIndex = nextPlayerIndex // Changer de joueur
            continueRound = false  // Fin du tour, passage au joueur suivant
        }

        // Vérifier si le joueur doit boire (double impair ou autre condition)
        if (dice1 % 2 != 0 && dice2 % 2 != 0) {
            Log.d("TriMannGame", "${players[currentPlayerIndex]} doit boire deux gorgées (double impair) !")
            didPlayerDrink = true
        }

        // Vérifier si c'est un double pair (distribuer)
        if (dice1 % 2 == 0 && dice1 == dice2) {
            Log.d("TriMannGame", "${players[currentPlayerIndex]} doit distribuer !")
        }

        // Vérifier si un 6 est lancé
        if (dice1 == 6 || dice2 == 6) {
            if (dice1 == 6 && dice2 == 6) {
                // Deux 6 lancés, gérer chifoumi
                Log.d("TriMannGame", "Deux 6 lancés, chifoumi entre deux joueurs !")
                // Implémenter un "chifoumi" ici
            } else {
                val total = if (dice1 == 6) dice2 else dice1
                Log.d("TriMannGame", "Posez $total gorgées sur la table")
                didPlayerDrink = true
            }
        }

        // Si le joueur n'a pas lancé un double, un 3, une somme de 3, un 7 ou un 9, le tour se termine
        if (!didPlayerDrink && dice1 != dice2 && (dice1 + dice2 != 3) && (dice1 != 7 && dice2 != 7) && (dice1 != 9 && dice2 != 9)) {
            Log.d("TriMannGame", "Le tour de ${players[currentPlayerIndex]} est terminé, aucun effet de boire.")
            continueRound = false
        }

        return continueRound
    }

    // Lancer les dés et appliquer les règles
    fun rollDiceAndApplyRules() {
        val dice1 = (1..6).random()
        val dice2 = (1..6).random()
        dice1Result = dice1
        dice2Result = dice2
        rotation1 = Random.nextFloat() * 360f  // Générer une rotation aléatoire pour le premier dé
        rotation2 = Random.nextFloat() * 360f  // Générer une rotation aléatoire pour le deuxième dé
        Log.d("TriMannGame", "Résultat des dés : $dice1, $dice2")
        roundEnded = !applyGameRules(dice1, dice2)
    }

    // Fonction pour changer de joueur
    fun nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size
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

            Button(onClick = {
                rollDiceAndApplyRules()
                if (roundEnded) {
                    nextPlayer()  // Si le tour est terminé, on passe au joueur suivant
                    roundEnded = false  // Réinitialisation de la variable de fin de tour
                }
            }) {
                Text(text = "Lancer les dés")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Affichage des images des dés avec animation de rotation
            Row(horizontalArrangement = Arrangement.Center) {
                DiceImage(dice1Result, rotation1)
                Spacer(modifier = Modifier.width(16.dp))
                DiceImage(dice2Result, rotation2)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(text = "Résultat des dés: ${dice1Result + dice2Result}", fontSize = 18.sp)
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


@Preview(showBackground = true)
@Composable
fun PreviewTrimannGameScreen() {
    TriMannGameScreen(
        gameSessionName = "Partie Test",
        playerNames = listOf("Alice", "Bob", "Charlie")
    )
}