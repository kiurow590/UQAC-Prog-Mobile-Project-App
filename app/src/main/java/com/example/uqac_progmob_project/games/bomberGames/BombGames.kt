package com.example.uqac_progmob_project.games.bomberGames

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
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
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat

import android.util.Log
import kotlin.compareTo
import kotlin.dec
import kotlin.rem

class BombGames : ComponentActivity() {
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

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            isPermissionGranted = isGranted
        }

    // Function to deduct time
    fun deductTime() {
        val deduction = (5..10).random()
        timerValue = (timerValue - deduction).coerceAtLeast(0)
    }

    // Function to eliminate the current player
    fun eliminateCurrentPlayer() {
        eliminatedPlayer = players[currentPlayerIndex]
        players.removeAt(currentPlayerIndex)
        if (players.size > 1) {
            currentPlayerIndex = (0 until players.size).random()
            timerValue = (30..60).random()
            countdownValue = 10
            gameStarted = false
        }
    }

    // Timer logic
    LaunchedEffect(gameStarted) {
        if (gameStarted) {
            while (timerValue > 0) {
                kotlinx.coroutines.delay(1000L)
                timerValue--
            }
            eliminateCurrentPlayer()
        }
    }

    // Countdown logic
    LaunchedEffect(countdownValue) {
        if (countdownValue > 0) {
            kotlinx.coroutines.delay(1000L)
            countdownValue--
        } else {
            gameStarted = true
        }
    }

    // Vérification initiale de la permission
    LaunchedEffect(Unit) {
        isPermissionGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
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
            // Display the winner
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Le gagnant est : ${players[0]}", fontSize = 24.sp)
            }
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
fun PlayerBubbles(playerNames: List<String>) {
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
                            .background(Color.Gray, shape = CircleShape),
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

@Composable
fun AnimatedBomb(timerValue: Int) {
    val maxDuration = 500 // Maximum duration for the animation
    val minDuration = 100 // Minimum duration for the animation
    val duration = ((timerValue / 60f) * (maxDuration - minDuration) + minDuration).toInt()

    // Log the duration value
    Log.d("AnimatedBomb", "Animation duration: $duration ms")

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
            kotlinx.coroutines.delay(200L) // Adjust the delay to control the animation speed
            currentFrame++
        }
    }

    Image(
        painter = painterResource(id = explosionImages[currentFrame]),
        contentDescription = "Explosion",
        modifier = Modifier.size(100.dp)
    )
}

@Composable
fun RandomText() {
    val randomChar = remember { ('A'..'Z').random() }
    Text(text = "$randomChar...", fontSize = 24.sp)
}


@Preview(showBackground = true)
@Composable
fun PreviewBombGamesScreen() {
    BombGamesScreen(
        gameSessionName = "Sample Game",
        playerNames = listOf("Player 1", "Player 2", "Player 3")
    )
}