package com.example.uqac_progmob_project.games.triMann

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uqac_progmob_project.BaseActivity
import com.example.uqac_progmob_project.R
import com.example.uqac_progmob_project.gameChoose.FinalResult
import kotlin.math.sqrt
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
    var continueTurn: Boolean by remember { mutableStateOf(true) }
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
    var showPlayerList: Boolean by remember { mutableStateOf(false) }
    var pointsToDistribute: Int by remember { mutableIntStateOf(0) }
    var selectedPlayers: List<String> by remember { mutableStateOf(emptyList()) }
    var shifoumiPlayers: List<String> by remember { mutableStateOf(emptyList()) }
    var shifoumiLoser: String? by remember { mutableStateOf(null) }
    val distributedPoints = remember { mutableStateListOf<Int>().apply { addAll(List(players.size) { 0 }) } }
    var totalDistributedPoints: Int by remember { mutableIntStateOf(0) }
    var rollResultMessage: String by remember { mutableStateOf("") }

    // Sensor Manager and Shake Detection
    val contextsensor = LocalContext.current
    val sensorManager = remember { contextsensor.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val accelerometer = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    var lastAccelerometerUpdate: Long by remember { mutableStateOf(0) }
    var lastAccelerometerValues = FloatArray(3)


    // Fonction pour changer de joueur
    fun nextPlayer() {
        if (currentPlayerIndex == (players.size) - 1) {
            trimanPlayerIndex = (0..<players.size).random()
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size
        showPlayerList = false
        shifoumiPlayers = emptyList()
        shifoumiLoser = null
        distributedPoints.clear()
        distributedPoints.addAll(List(players.size) { 0 })
        totalDistributedPoints = 0 // Reset total distributed points
    }

    // Fonction pour appliquer les règles et déterminer si le tour continue ou s'il est terminé
    fun applyGameRules(dice1: Int, dice2: Int): Boolean {
        continueTurn = false  // Variable pour déterminer si le tour continue

        // Vérifier si c'est un double pair et différent de 6
        if (dice1 == dice2 && dice1 % 2 == 0 && dice1 != 6) {
            rollResultMessage = "${players[currentPlayerIndex]} a lancé un double ${dice1Result}, il distribue alors ${dice1Result} points!"
            playerScores[currentPlayerIndex] += dice1Result
            pointsToDistribute = dice1Result  // n points à distribuer
            showPlayerList = true  // Afficher la liste des joueurs
            Log.d("TriMannGame", "Double pair, showing player list")
            continueTurn = true
        }

        // Si c'est un 3 ou une somme de 3, le trimann gagne 2 points
        if (dice1 == 3 || dice2 == 3 || (dice1 + dice2 == 3)) {
            rollResultMessage = "Tri ! ${players[trimanPlayerIndex]} gagne 2 points"
            playerScores[trimanPlayerIndex] += 2
            continueTurn = true
        }

        // Vérifier si c'est un 7 ou un 9 (donner des points avant ou après)
        if (dice1 == 7 || dice2 == 7 || (dice1 + dice2 == 7)) {
            val previousPlayerIndex = (currentPlayerIndex - 1 + players.size) % players.size
            playerScores[previousPlayerIndex] += 2
            rollResultMessage = "${players[previousPlayerIndex]} gagne 2 points"
            continueTurn = true
        } else if (dice1 == 9 || dice2 == 9 || (dice1 + dice2 == 9)) {
            val nextPlayerIndex = (currentPlayerIndex + 1) % players.size
            playerScores[nextPlayerIndex] += 2
            rollResultMessage = "${players[nextPlayerIndex]} gagne 2 points"
            continueTurn = true
        }

        // Vérifier si le joueur gagne des points (double impair ou autre condition)
        if (dice1 == dice2 && dice1 % 2 != 0) {
            rollResultMessage = "${players[currentPlayerIndex]} gagne $dice1Result points (double impair) !"
            playerScores[currentPlayerIndex] += dice1
            continueTurn = true
        }

        // Vérifier si un 6 est lancé
        if (dice1 == 6 || dice2 == 6) {
            if (dice1 == 6 && dice2 == 6) {
                rollResultMessage = "Double 6, choisissez deux joueurs pour le chifoumi !"
                showPlayerList = true  // Afficher la liste des joueurs pour sélectionner deux joueurs
                Log.d("TriMannGame", "Double 6, showing player list for shifoumi")
                selectedPlayers = emptyList()  // Réinitialiser les joueurs sélectionnés
            } else {
                val total = if (dice1 == 6) dice2 else dice1
                rollResultMessage = "Posez $total doigts sur la table"
                pointsToDistribute = 2
                showPlayerList = true  // Afficher la liste des joueurs pour sélectionner un joueur
                Log.d("TriMannGame", "Single 6, showing player list for point distribution")
            }
            continueTurn = true
        }

        if (!continueTurn) {
            rollResultMessage = "Le tour de ${players[currentPlayerIndex]} est terminé, aucun évènement n'a eu lieu."
            nextPlayer()
        }

        return !continueTurn  // Retourne true si le tour est terminé
    }

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

    // Shake Detection Listener
    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val currentTime = System.currentTimeMillis()
                if ((currentTime - lastAccelerometerUpdate) > 100) {
                    val diffTime = (currentTime - lastAccelerometerUpdate) * 1e-9
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = event.values[2]
                    // Calculer la vitesse et l'accélération
                    val acceleration = sqrt((x * x + y * y + z * z) )
                    if (acceleration > 12) {
                        rollDiceAndApplyRules()
                    }
                    lastAccelerometerUpdate = currentTime
                    lastAccelerometerValues[0] = x
                    lastAccelerometerValues[1] = y
                    lastAccelerometerValues[2] = z
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    DisposableEffect(Unit) {
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (gameStarted) {
            Text(text = rollResultMessage, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            Text(text = "Triman: ${players[trimanPlayerIndex]}", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

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

            // Afficher la liste des joueurs si nécessaire
            if (showPlayerList) {
                Log.d("TriMannGame", "Player list should be visible")
                Spacer(modifier = Modifier.height(20.dp))
                Text("Liste des joueurs:", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                LazyColumn {
                    items(players.size) { index ->
                        val player = players[index]

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(8.dp)
                        ) {
                            Text(text = player, fontSize = 16.sp)
                            if (pointsToDistribute > 0) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(onClick = {
                                        if (distributedPoints[index] > 0) {
                                            distributedPoints[index]--
                                            totalDistributedPoints--
                                        }
                                    }) {
                                        Text("-")
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = distributedPoints[index].toString(),
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(onClick = {
                                        if (totalDistributedPoints < pointsToDistribute) {
                                            distributedPoints[index]++
                                            totalDistributedPoints++
                                        }
                                    }) {
                                        Text("+")
                                    }
                                }
                            } else {
                                Button(onClick = {
                                    if (shifoumiPlayers.size < 2) {
                                        shifoumiPlayers = shifoumiPlayers + player
                                    }
                                }) {
                                    Text("Désigner")
                                }
                            }
                        }
                    }

                    if (pointsToDistribute > 0) {
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            Button(onClick = {
                                distributedPoints.forEachIndexed { index, points ->
                                    playerScores[index] += points
                                }
                                pointsToDistribute = 0
                                showPlayerList = false
                                distributedPoints.clear()
                                distributedPoints.addAll(List(players.size) { 0 })
                                totalDistributedPoints = 0 // Reset total distributed points
                            }) {
                                Text("Valider la distribution")
                            }
                        }
                    } else if (shifoumiPlayers.size == 2) {
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text("Joueurs sélectionnés pour le shifoumi : ${shifoumiPlayers.joinToString(", ")}", fontSize = 16.sp)
                            Button(onClick = {
                                shifoumiLoser = shifoumiPlayers.random()
                                playerScores[players.indexOf(shifoumiLoser!!)] += 2
                                showPlayerList = false
                                shifoumiPlayers = emptyList() // Reset shifoumi players
                                shifoumiLoser = null // Reset shifoumi loser
                            }) {
                                Text("Désigner le perdant")
                            }
                        }
                    }
                }
            }
        } else {
            Button(onClick = { gameStarted = true }) {
                Text(text = "Démarrer le jeu")
            }
        }

        if (gameEnded) {
            val context = LocalContext.current
            LaunchedEffect(Unit) {
                val intent = Intent(context, FinalResult::class.java).apply {
                    putExtra("GAMESESSIONNAME", gameSessionName)
                    putExtra("GAMETYPE", "TriMannGame")
                    putStringArrayListExtra("PLAYER_NAMES", ArrayList(playerNames))
                    putIntegerArrayListExtra("PLAYER_SCORES", ArrayList(playerScores))
                }
                context.startActivity(intent)
                (context as Activity).finish()
            }
        }

        // Bouton pour terminer le jeu
        Button(onClick = { gameEnded = true }) {
            Text(text = "Terminer le jeu")
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
