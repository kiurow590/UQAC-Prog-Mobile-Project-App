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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun TriMannGameScreen(gameSessionName: String, playerNames: List<String>) {
    val players = remember { playerNames.toMutableList() }

    var currentPlayerIndex: Int by remember { mutableIntStateOf(0) }
    var dice1Result: Int by remember { mutableIntStateOf(0) }
    var dice2Result: Int by remember { mutableIntStateOf(0) }
    var gameStarted: Boolean by remember { mutableStateOf(false) }
    var rotation1: Float by remember { mutableFloatStateOf(0f) }
    var rotation2: Float by remember { mutableFloatStateOf(0f) }
    var playerScores: MutableList<Int> by remember { mutableStateOf(IntArray(players.size) { 0 }.toMutableList()) }
    var gameEnded: Boolean by remember { mutableStateOf(false) }
    var roundEnded: Boolean by remember { mutableStateOf(false) }
    var trimanPlayerIndex: Int by remember { mutableIntStateOf((0..<players.size).random()) }
    var givenPoints: MutableList<Int> by remember { mutableStateOf(IntArray(players.size){0}.toMutableList()) }
    var showDistributionDialog by remember { mutableStateOf(false) }
    // Fonction pour changer de joueur
    fun nextPlayer() {
        if (currentPlayerIndex==(players.size)-1){
            trimanPlayerIndex = (0..<players.size).random();
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size

    }
    // Fonction pour appliquer les règles et déterminer si le tour continue ou s'il est terminé
    fun applyGameRules(dice1: Int, dice2: Int): Boolean {

        var didPlayerDrink = false  // Variable pour suivre si le joueur doit boire
        var continueRound = true    // Variable pour déterminer si le tour continue

        // Vérifier si c'est un double pair
        if (dice1 == dice2 && dice1%2==0) {
            Log.d("TriMannGame", "${players[currentPlayerIndex]} a lancé un double pair, il distribue alors ${dice1Result} points!")
            //Ajouter component pour le choix de la distribution des points
            playerScores[currentPlayerIndex] +=dice1Result;
        }

        // Si c'est un 3 ou une somme de 3, le trimann gagne 2 points
        if (dice1 == 3 || dice2 == 3 || (dice1 + dice2 == 3)) {
            Log.d("TriMannGame", "${players[trimanPlayerIndex]} gagne 2 points ")
            playerScores[trimanPlayerIndex] +=2;
        }

        // Vérifier si c'est un 7 ou un 9 (changer de joueur)
        if (dice1 == 7 || dice2 == 7 || (dice1 + dice2 == 7)) {
            val previousPlayerIndex = (currentPlayerIndex - 1 + players.size) % players.size
            playerScores[previousPlayerIndex]+=2;
            Log.d("TriMannGame", "${players[previousPlayerIndex]} gagne 2 points")

            continueRound = false  // Fin du tour, passage au joueur suivant
        } else if (dice1 == 9 || dice2 == 9 || (dice1 + dice2 == 9)) {
            val nextPlayerIndex = (currentPlayerIndex + 1) % players.size
            playerScores[nextPlayerIndex]+=2;
            Log.d("TriMannGame", "${players[currentPlayerIndex]} gagne 2 points ")

            continueRound = false  // Fin du tour, passage au joueur suivant
        }

        // Vérifier si le joueur doit boire (double impair ou autre condition)
        if (dice1==dice2 && dice1 % 2 != 0) {
            Log.d("TriMannGame", "${players[currentPlayerIndex]} doit boire deux gorgées (double impair) !")
            didPlayerDrink = true
            playerScores[currentPlayerIndex] +=dice1;
        }



        // Vérifier si un 6 est lancé
        if (dice1 == 6 || dice2 == 6) {
            if (dice1 == 6 && dice2 == 6) {
                // Deux 6 lancés, gérer chifoumi
                Log.d("TriMannGame", "Deux 6 lancés, chifoumi entre deux joueurs !")
                // Implémenter un "chifoumi" ici
            } else {
                val total = if (dice1 == 6) dice2 else dice1
                Log.d("TriMannGame", "Posez $total doigts sur la table")
                didPlayerDrink = true
            }
        }

        // Si le joueur n'a pas lancé un double, un 3, une somme de 3, un 7 ou un 9, le tour se termine
        if (!didPlayerDrink && dice1 != dice2 && (dice1 + dice2 != 3) && (dice1 != 7 && dice2 != 7) && (dice1 != 9 && dice2 != 9)) {
            Log.d("TriMannGame", "Le tour de ${players[currentPlayerIndex]} est terminé, aucun effet de boire.")
            nextPlayer();
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


    @Composable
    fun DistributionPopup(
        players: List<String>,
        currentPlayerIndex: Int,
        playerScores: MutableList<Int>,
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        var givenPoints by remember { mutableStateOf(mutableMapOf<Int, Int>()) }

        ModalBottomSheet(
            onDismissRequest = { onDismiss() }
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Distribuez les gorgées", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(10.dp))

                players.forEachIndexed { index, player ->
                    if (index != currentPlayerIndex) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Text(text = player, fontSize = 18.sp)
                            Text(text = "${givenPoints[index] ?: 0} gorgées", fontSize = 16.sp)
                            Button(onClick = {
                                givenPoints[index] = (givenPoints[index] ?: 0) + 1
                            }) {
                                Text("+")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = {
                    givenPoints.forEach { (index, points) ->
                        playerScores[index] += points
                    }
                    givenPoints.clear()
                    onConfirm()
                }) {
                    Text("Valider")
                }
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