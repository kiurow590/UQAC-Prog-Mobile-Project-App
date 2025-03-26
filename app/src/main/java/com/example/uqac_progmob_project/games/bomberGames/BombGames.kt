package com.example.uqac_progmob_project.games.bomberGames

import android.app.Activity
import android.os.Bundle
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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import com.example.uqac_progmob_project.R
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

import android.util.Log
import com.example.uqac_progmob_project.BaseActivity
import com.example.uqac_progmob_project.gameChoose.FinalResult
import kotlinx.coroutines.delay


class BombGames : BaseActivity() {
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

@SuppressLint("MutableCollectionMutableState")
@Composable
fun BombGamesScreen(gameSessionName: String, playerNames: List<String>) {
    val context = LocalContext.current
    val activity = context as? Activity
    val permission = Manifest.permission.RECORD_AUDIO
    var isPermissionGranted by remember { mutableStateOf(false) }
    var showPermissionRationale by remember { mutableStateOf(false) }
    var timerValue by remember { mutableIntStateOf((30..60).random()) } // Random timer value between 30 and 60 seconds
    var gameStarted by remember { mutableStateOf(false) }
    var countdownValue by remember { mutableIntStateOf(10) }
    var currentPlayerIndex by remember { mutableIntStateOf((0 until playerNames.size).random()) } // Randomly select the first player
    var players by remember { mutableStateOf(playerNames.toMutableList()) }
    var eliminatedPlayer by remember { mutableStateOf<String?>(null) }
    var playerScores by remember { mutableStateOf(IntArray(playerNames.size) { 0 }.toMutableList()) }
    var eliminationOrder by remember { mutableIntStateOf(playerNames.size - 1) }
    var playerIndices by remember { mutableStateOf((playerNames.indices).toMutableList()) }

    Log.d("BombGamesScreen", "gameSessionName: $gameSessionName")
    Log.d("BombGamesScreen", "playerNames: $playerNames")
    Log.d("BombGamesScreen", "isPermissionGranted: $isPermissionGranted")
    Log.d("BombGamesScreen", "showPermissionRationale: $showPermissionRationale")
    Log.d("BombGamesScreen", "timerValue: $timerValue")
    Log.d("BombGamesScreen", "gameStarted: $gameStarted")
    Log.d("BombGamesScreen", "countdownValue: $countdownValue")
    Log.d("BombGamesScreen", "currentPlayerIndex: $currentPlayerIndex")
    Log.d("BombGamesScreen", "players: $players")
    Log.d("BombGamesScreen", "eliminatedPlayer: $eliminatedPlayer")
    Log.d("BombGamesScreen", "playerScores: $playerScores")
    Log.d("BombGamesScreen", "eliminationOrder: $eliminationOrder")
    Log.d("BombGamesScreen", "playerIndices: $playerIndices")

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            isPermissionGranted = isGranted
            Log.d("BombGamesScreen", "Permission granted: $isGranted")
        }

    // Function to deduct time
    fun deductTime() {
        val deduction = (5..10).random()
        timerValue = (timerValue - deduction).coerceAtLeast(0)
        Log.d("BombGamesScreen", "Time deducted: $deduction, new timerValue: $timerValue")
    }

    // Function to eliminate the current player
    fun eliminateCurrentPlayer() {
        if (currentPlayerIndex < players.size && currentPlayerIndex < playerIndices.size) {
            eliminatedPlayer = players[currentPlayerIndex]
            playerScores[playerIndices[currentPlayerIndex]] = eliminationOrder--
            players.removeAt(currentPlayerIndex)
            playerIndices.removeAt(currentPlayerIndex)
            Log.d("BombGamesScreen", "Player eliminated: $eliminatedPlayer, new playerScores: $playerScores, new eliminationOrder: $eliminationOrder")
            if (players.size > 1) {
                currentPlayerIndex = (0 until players.size).random()
                timerValue = (30..60).random()
                countdownValue = 10
                gameStarted = false
                Log.d("BombGamesScreen", "New currentPlayerIndex: $currentPlayerIndex, new timerValue: $timerValue, new countdownValue: $countdownValue")
            }
        }
    }

    // Timer logic
    LaunchedEffect(gameStarted) {
        if (gameStarted) {
            while (timerValue > 0) {
                delay(1000L)
                timerValue--
                Log.d("BombGamesScreen", "Timer ticking, timerValue: $timerValue")
            }
            eliminateCurrentPlayer()
        }
    }

    // Countdown logic
    LaunchedEffect(countdownValue) {
        if (countdownValue > 0) {
            delay(1000L)
            countdownValue--
            Log.d("BombGamesScreen", "Countdown ticking, countdownValue: $countdownValue")
        } else {
            gameStarted = true
            Log.d("BombGamesScreen", "Game started")
        }
    }

    // Vérification initiale de la permission
    LaunchedEffect(Unit) {
        isPermissionGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        Log.d("BombGamesScreen", "Initial permission check, isPermissionGranted: $isPermissionGranted")
    }

    // Si la permission est refusée, on affiche une boîte de dialogue explicative
    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Permission requise") },
            text = { Text("Ce jeu utilise la reconnaissance vocale. Autorisez l'accès au micro pour jouer.") },
            confirmButton = {
                Button(onClick = {
                    showPermissionRationale = false
                    permissionLauncher.launch(permission)
                }) {
                    Text("Autoriser")
                }
            },
            dismissButton = {
                Button(onClick = { showPermissionRationale = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // UI du jeu si la permission est accordée
    if (isPermissionGranted) {
        if (players.size == 1) {
            playerScores[playerIndices[currentPlayerIndex]] = eliminationOrder
            EndGame(gameSessionName, playerNames, playerScores)
        } else if (!gameStarted) {
            // Display the starting player and countdown
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (eliminatedPlayer != null) {
                    Text("Joueur éliminé : $eliminatedPlayer", fontSize = 24.sp, color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Text("Le joueur qui commence est : ${players[currentPlayerIndex]}", fontSize = 24.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Début du jeu dans : $countdownValue secondes", fontSize = 24.sp)
            }
        } else {
            GameUI(gameSessionName, players, timerValue, ::deductTime, currentPlayerIndex) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size
                Log.d("BombGamesScreen", "Next player, currentPlayerIndex: $currentPlayerIndex")
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Ce jeu a besoin du micro pour fonctionner.", fontSize = 18.sp, color = Color.Red)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity!!, permission)) {
                    showPermissionRationale = true
                } else {
                    permissionLauncher.launch(permission)
                }
            }) {
                Text("Demander l'autorisation")
            }
        }
    }
}

@SuppressLint("ComposableNaming")
@Composable
fun EndGame(gameSessionName: String, playerNames: List<String>, playerScores: List<Int>) {
    val context = LocalContext.current
    Log.d("BombGamesScreen", "Ending game, gameSessionName: $gameSessionName, playerNames: $playerNames, playerScores: $playerScores")
    Log.d("BombGamesScreen", "Navigating to FinalResult screen")
    val intent = Intent(context, FinalResult::class.java).apply {
        putExtra("GAMESESSIONNAME", gameSessionName)
        putStringArrayListExtra("PLAYER_NAMES", ArrayList(playerNames))
        putIntegerArrayListExtra("PLAYER_SCORES", ArrayList(playerScores))
    }
    context.startActivity(intent)
}

@Composable
fun GameUI(
    gameSessionName: String,
    playerNames: List<String>,
    timerValue: Int,
    deductTime: () -> Unit,
    currentPlayerIndex: Int,
    onNextPlayer: () -> Unit
) {
    val context = LocalContext.current
    val randomChar = remember { ('A'..'Z').random() }
    var recognizedText by remember { mutableStateOf("") }
    var feedback by remember { mutableStateOf("") }

    val speechRecognizerHelper = remember {
        SpeechRecognizerHelper(
            context = context,
            onResult = { text ->
                recognizedText = text
                feedback = if (text.startsWith(randomChar, ignoreCase = true)) {
                    onNextPlayer()
                    "✅ Correct !"
                } else {
                    deductTime()
                    "❌ Mauvais mot, essaye encore !"
                }
            },
            onError = { error ->
                feedback = error
            }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopBar(gameSessionName)
        Spacer(modifier = Modifier.height(16.dp))
        PlayerBubbles(playerNames, currentPlayerIndex)
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedBomb(timerValue)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Trouve un mot commençant par : $randomChar", fontSize = 24.sp)

        Button(onClick = { speechRecognizerHelper.startListening() }) {
            Text(text = "🎤 Parler")
        }

        Text(text = "Vous avez dit : $recognizedText", fontSize = 18.sp)

        Text(
            text = feedback,
            fontSize = 18.sp,
            color = if (feedback.startsWith("✅")) Color.Green else Color.Red
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display the countdown timer
        Text(text = "Temps restant : $timerValue secondes", fontSize = 18.sp, color = Color.Black)
    }

    DisposableEffect(Unit) {
        onDispose {
            speechRecognizerHelper.destroy()
        }
    }
}

@Composable
fun PlayerBubbles(playerNames: List<String>, currentPlayerIndex: Int) {
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
                            .background(
                                if (index == currentPlayerIndex) Color.Yellow else Color.Gray,
                                shape = CircleShape
                            ),
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
fun AnimatedBomb(timerValue: Int) {
    val maxDuration = 500 // Maximum duration for the animation
    val minDuration = 100 // Minimum duration for the animation
    val duration = ((timerValue / 60f) * (maxDuration - minDuration) + minDuration).toInt()

    // Log the duration value
    //Log.d("AnimatedBomb", "Animation duration: $duration ms")

    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    if (timerValue > 0) {
        Image(
            painter = painterResource(id = R.drawable.bomb_character_o_idle), // Replace with your bomb image resource
            contentDescription = "Bomb",
            modifier = Modifier
                .size(100.dp)
                .scale(scale)
        )
    } else {
        ExplosionAnimation()
    }
}

@Composable
fun ExplosionAnimation() {
    val explosionImages = listOf(
        R.drawable.bomb_character_o_explode0, // Replace with your explosion image resources
        R.drawable.bomb_character_o_explode1,
        R.drawable.bomb_character_o_explode2,
        R.drawable.bomb_character_o_explode3
    )
    var currentFrame by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (currentFrame < explosionImages.size - 1) {
            delay(200L) // Adjust the delay to control the animation speed
            currentFrame++
        }
    }

    Image(
        painter = painterResource(id = explosionImages[currentFrame]),
        contentDescription = "Explosion",
        modifier = Modifier.size(100.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewBombGamesScreen() {
    BombGamesScreen(
        gameSessionName = "Sample Game",
        playerNames = listOf("Player 1", "Player 2", "Player 3")
    )
}