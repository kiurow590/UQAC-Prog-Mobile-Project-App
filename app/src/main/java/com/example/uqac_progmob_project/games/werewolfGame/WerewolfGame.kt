package com.example.uqac_progmob_project.games.werewolfGame

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uqac_progmob_project.BaseActivity
import com.example.uqac_progmob_project.R
import com.example.uqac_progmob_project.gameChoose.FinalResult
import kotlinx.coroutines.delay

class WerewolfGame : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gameSessionName = intent.getStringExtra("GAMESESSIONNAME") ?: ""
        val playerNames = intent.getStringArrayListExtra("PLAYERSESSIONNAME") ?: listOf()

        println("Game Session Name: $gameSessionName")
        println("Number of Players: ${playerNames.size}")
        println("Player Names: $playerNames")

        setContent {
            WerewolfGame(gameSessionName, playerNames)
        }
    }
}

@Composable
fun WerewolfGame(
    gameSessionName: String,
    playerNames: List<String>
) {
    var gamePhase by remember { mutableStateOf("revealCards") }
    val roles = remember { assignRoles(playerNames).toMutableMap() }
    val eliminatedPlayers = remember { mutableStateListOf<String>() }
    val eliminatedPlayersDuringNight = remember { mutableStateListOf<String>() }
    var loverPair by remember { mutableStateOf<Pair<String, String>?>(null) }
    var wolfVictim by remember { mutableStateOf<String?>(null) }
    var witchPotionsAvailable by remember { mutableStateOf(Pair(true, true)) }
    var seerVision by remember { mutableStateOf<Pair<String, String>?>(null) }
    var nightCount by remember { mutableIntStateOf(1) }
    var gameEnded by remember { mutableStateOf(false) }
    var winnerMessage by remember { mutableStateOf("") }
    val playerScores = remember { mutableStateListOf(*List(playerNames.size) { 0 }.toTypedArray()) }

    fun checkVictory() {
        val alivePlayers = playerNames - eliminatedPlayers
        val werewolvesAlive = alivePlayers.count { roles[it] == "Loup-Garou" }
        val villagersAlive = alivePlayers.count { roles[it] != "Loup-Garou" }

        if (loverPair != null) {
            val (lover1, lover2) = loverPair!!
            if (alivePlayers.containsAll(listOf(lover1, lover2)) && alivePlayers.size == 2) {
                winnerMessage = "💖 Victoire des Amoureux !"
                playerScores[playerNames.indexOf(lover1)] = 1
                playerScores[playerNames.indexOf(lover2)] = 1
                gamePhase = "results"
                return
            }
        }

        if (werewolvesAlive > 0 && villagersAlive == 0) {
            winnerMessage = "🐺 Victoire des Loups-Garous !"
            playerNames.filter { roles[it] == "Loup-Garou" }.forEach {
                playerScores[playerNames.indexOf(it)] = 1
            }
            gamePhase = "results"
        } else if (werewolvesAlive == 0) {
            playerNames.filter { roles[it] != "Loup-Garou" }.forEach {
                playerScores[playerNames.indexOf(it)] = 1
            }
            winnerMessage = "🏡 Victoire des Villageois !"
            gamePhase = "results"
        }
    }

    if (gameEnded) {
        GameEnd(
            gameSessionName,
            playerNames,
            playerScores
        )
    } else {
        when (gamePhase) {
            "revealCards" -> WerewolfCardRevealed(playerNames, roles) { gamePhase = "night" }
            "night" -> WerewolfNightPhase(
                nightCount,
                playerNames.filterNot { it in eliminatedPlayers },
                roles,
                onThiefChoice = { chosenRole ->
                    val thiefPlayer = playerNames.find { roles[it] == "Voleur" }
                    if (thiefPlayer != null) {
                        roles[thiefPlayer] = chosenRole
                    }
                },
                onLoversChosen = { p1, p2 -> loverPair = p1 to p2 },
                onWolfVictimChosen = { victim -> wolfVictim = victim },
                onWitchAction = { healed, poisoned, target ->
                    if (healed) {
                        witchPotionsAvailable = witchPotionsAvailable.copy(first = false)
                        wolfVictim = null
                    }
                    if (poisoned) {
                        witchPotionsAvailable = witchPotionsAvailable.copy(second = false)
                        eliminatedPlayersDuringNight.add(target!!)
                    }
                },
                onSeerVision = { player, role -> seerVision = player to role },
                witchPotionsAvailable = witchPotionsAvailable,
                wolfVictim = wolfVictim,
                onGameStart = {
                    gamePhase = "day"
                    if (wolfVictim != null) {
                        eliminatedPlayersDuringNight.add(wolfVictim!!)
                    }
                    loverPair?.let { (lover1, lover2) ->
                        if (eliminatedPlayersDuringNight.contains(lover1)) {
                            eliminatedPlayersDuringNight.add(lover2)
                        } else if (eliminatedPlayersDuringNight.contains(lover2)) {
                            eliminatedPlayersDuringNight.add(lover1)
                        }
                    }
                    if (eliminatedPlayersDuringNight.isNotEmpty()) {
                        eliminatedPlayers.addAll(eliminatedPlayersDuringNight)
                    }
                    checkVictory()
                }
            )
            "day" -> WerewolfDayPhase(
                playerNames.filterNot { it in eliminatedPlayers },
                roles,
                eliminatedPlayersDuringNight,
                onPlayerEliminated = { player ->
                    if (player != null) {
                        eliminatedPlayers.add(player)
                    }
                    checkVictory()
                },
                onNightStart = {
                    gamePhase = "night"
                    nightCount++
                    eliminatedPlayersDuringNight.clear()
                    checkVictory()
                }
            )
            "results" -> GameEndScreen(winnerMessage) {
                gameEnded = true
            }
        }
    }
}

@Composable
fun GameEnd(
    gameSessionName: String,
    playerNames: List<String>,
    playerScores: List<Int>
) {
    val context = LocalContext.current
    Log.d("BombGamesScreen", "Ending game, gameSessionName: $gameSessionName, playerNames: $playerNames, playerScores: $playerScores")
    Log.d("BombGamesScreen", "Navigating to FinalResult screen")
    val intent = Intent(context, FinalResult::class.java).apply {
        putExtra("GAMESESSIONNAME", gameSessionName)
        putExtra("GAMETYPE", "Bleiz Garou")
        putStringArrayListExtra("PLAYER_NAMES", ArrayList(playerNames))
        putIntegerArrayListExtra("PLAYER_SCORES", ArrayList(playerScores))
    }
    context.startActivity(intent)
    (context as Activity).finish()
}

@Composable
fun WerewolfCardRevealed(
    playerNames: List<String>,
    roles: MutableMap<String, String>,
    onStartNightPhase: () -> Unit
) {
    var currentPlayerIndex by remember { mutableIntStateOf(0) }
    var revealed by remember { mutableStateOf(false) }
    var allRevealed by remember { mutableStateOf(false) }

    val currentPlayer = playerNames.getOrNull(currentPlayerIndex) ?: "Inconnu"
    val currentRole = roles[currentPlayer] ?: "Erreur"

    LaunchedEffect(revealed) {
        if (revealed) {
            delay(3000)
            revealed = false
            if (currentPlayerIndex == playerNames.size - 1) {
                allRevealed = true
            } else {
                currentPlayerIndex++
            }
        }
    }

    if (allRevealed) {
        onStartNightPhase()
        return
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Passer le téléphone à $currentPlayer",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .size(200.dp)
                .clickable {
                    revealed = !revealed
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (revealed) Color.White else Color.Gray)
        ) {
            AnimatedVisibility(
                visible = revealed,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = currentRole,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (!revealed) {
                if (currentPlayerIndex == playerNames.size - 1) {
                    allRevealed = true
                } else {
                    currentPlayerIndex++
                }
            }
        }) {
            Text(text = "Joueur suivant")
        }
    }
}

fun assignRoles(playerNames: List<String>): Map<String, String> {
    val playerCount = playerNames.size

    val roleDistribution = mapOf(
        8 to listOf("Chasseur", "Loup-Garou", "Loup-Garou", "Petite Fille", "Villageois", "Villageois", "Sorcière", "Voyante"),
        9 to listOf("Chasseur", "Cupidon", "Loup-Garou", "Loup-Garou", "Petite Fille", "Villageois", "Villageois", "Sorcière", "Voyante"),
        10 to listOf("Chasseur", "Cupidon", "Loup-Garou", "Loup-Garou", "Petite Fille", "Villageois", "Villageois", "Sorcière", "Voyante", "Voleur"),
        11 to listOf("Chasseur", "Cupidon", "Loup-Garou", "Loup-Garou", "Petite Fille", "Villageois", "Villageois", "Villageois", "Sorcière", "Voyante", "Voleur"),
        12 to listOf("Chasseur", "Cupidon", "Loup-Garou", "Loup-Garou", "Loup-Garou", "Petite Fille", "Villageois", "Villageois", "Villageois", "Sorcière", "Voyante", "Voleur"),
        13 to listOf("Chasseur", "Cupidon", "Loup-Garou", "Loup-Garou", "Loup-Garou", "Petite Fille", "Villageois", "Villageois", "Villageois", "Villageois", "Sorcière", "Voyante", "Voleur"),
        14 to listOf("Chasseur", "Cupidon", "Loup-Garou", "Loup-Garou", "Loup-Garou", "Loup-Garou", "Petite Fille", "Villageois", "Villageois", "Villageois", "Villageois", "Sorcière", "Voyante", "Voleur"),
        15 to listOf("Chasseur", "Cupidon", "Loup-Garou", "Loup-Garou", "Loup-Garou", "Loup-Garou", "Petite Fille", "Villageois", "Villageois", "Villageois", "Villageois", "Villageois", "Sorcière", "Voyante", "Voleur"),
        16 to listOf("Chasseur", "Cupidon", "Loup-Garou", "Loup-Garou", "Loup-Garou", "Loup-Garou", "Petite Fille", "Villageois", "Villageois", "Villageois", "Villageois", "Villageois", "Villageois", "Sorcière", "Voyante", "Voleur")
    )

    val roles = roleDistribution[playerCount]?.shuffled() ?: return emptyMap()

    return playerNames.zip(roles).toMap()
}

@Composable
fun WerewolfNightPhase(
    nightCount: Int,
    playerNames: List<String>,
    roles: MutableMap<String, String>,
    onThiefChoice: (String) -> Unit,
    onLoversChosen: (String, String) -> Unit,
    onWolfVictimChosen: (String) -> Unit,
    onWitchAction: (Boolean, Boolean, String?) -> Unit,
    onSeerVision: (String, String) -> Unit,
    witchPotionsAvailable: Pair<Boolean, Boolean>,
    wolfVictim: String?,
    onGameStart: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    val hasThief = roles.containsValue("Voleur")
    val hasCupid = roles.containsValue("Cupidon")
    val seerPlayer = playerNames.find { roles[it] == "Voyante" }
    val witchPlayer = playerNames.find { roles[it] == "Sorcière" }
    val seerAlive = seerPlayer != null && seerPlayer in playerNames
    val witchAlive = witchPlayer != null && witchPlayer in playerNames
    val firstNight = nightCount == 1

    val steps = listOf(
        "🌙 ${nombreEnOrdinal(nightCount)} : Tout le monde ferme les yeux.",

        "🃏 Le Voleur se réveille et voit les deux cartes cachées.", // step = 1
        "😴 Le Voleur se rendort.",

        "💘 Cupidon se réveille et choisit deux joueurs amoureux.", // step = 3
        "😴 Cupidon se rendort.",

        "🔮 La Voyante se réveille et regarde la carte d’un joueur.", // step = 5
        "😴 La Voyante se rendort.",

        "💏 Les Amoureux se réveillent et se reconnaissent.", // step = 7
        "😴 Les Amoureux se rendorment.",

        "🐺 Les Loups-Garous se réveillent et désignent une victime.", // step = 9
        "😴 Les Loups-Garous se rendorment.",

        "🧙‍♀️ La Sorcière se réveille et voit la victime des Loups-Garous.", // step = 11
        "😴 La Sorcière se rendort.",

        "🌄 Le Village se réveille et découvre la victime !" // step = 13
    )

    fun nextStep() {
        do {
            currentStep++
        } while (
            ((!hasThief || !firstNight) && (currentStep in listOf(1, 2))) ||
            ((!hasCupid || !firstNight) && (currentStep in listOf(3, 4, 7, 8))) ||
            (!seerAlive && (currentStep in listOf(5, 6))) ||
            (!witchAlive && (currentStep in listOf(11, 12)))
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentStep) {
            1 -> {
                val centerCards = listOf("Chasseur", "Loup-Garou") // Exemple
                ThiefChoice(centerCards) { chosenRole ->
                    onThiefChoice(chosenRole)
                    nextStep()
                }
            }

            3 -> {
                CupidChoice(playerNames) { lover1, lover2 ->
                    onLoversChosen(lover1, lover2)
                    nextStep()
                }
            }

            5 -> {
                SeerChoice(playerNames, roles) { player, role ->
                    onSeerVision(player, role)
                    nextStep()
                }
            }

            9 -> {
                WerewolvesChoice(playerNames) { victim ->
                    onWolfVictimChosen(victim)
                    nextStep()
                }
            }

            11 -> {
                WitchChoice(playerNames, witchPotionsAvailable, wolfVictim) { healed, poisoned, target ->
                    onWitchAction(healed, poisoned, target)
                    nextStep()
                }
            }

            13 -> {
                Button(onClick = { onGameStart() }) {
                    Text("Commencer la journée")
                }
            }

            else -> {
                Text(text = steps[currentStep], fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { nextStep() }) {
                    Text("Suivant")
                }
            }
        }
    }
}

@Composable
fun WerewolfDayPhase(
    playerNames: List<String>,
    roles: MutableMap<String, String>,
    eliminatedPlayers: List<String>,
    onPlayerEliminated: (String?) -> Unit,
    onNightStart: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    var votedPlayers by remember { mutableStateOf<List<String>>(emptyList()) }
    var isRevote by remember { mutableStateOf(false) }
    var showHunterChoice by remember { mutableStateOf(false) }
    var hunterTarget by remember { mutableStateOf<String?>(null) }
    var showHunterKillResult by remember { mutableStateOf(false) }

    val updatedPlayerNames = remember { playerNames.toMutableStateList() }
    val eliminatedPlayersDuringDay = remember { mutableStateListOf<String>() }

    val steps = listOf(
        "🌞 Le village se réveille",
        "🗣 Débat sur les suspects",
        "⚖️ Vote pour éliminer un joueur",
        "⚠️ Résultats de la journée"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(steps[currentStep], fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))

        when {
            showHunterChoice -> {
                HunterChoice(
                    alivePlayers = updatedPlayerNames.filterNot { it in eliminatedPlayers },
                    onPlayerChosen = { chosenPlayer ->
                        hunterTarget = chosenPlayer
                        showHunterChoice = false
                        showHunterKillResult = true
                        onPlayerEliminated(chosenPlayer)
                        eliminatedPlayersDuringDay.add(chosenPlayer)
                        updatedPlayerNames.remove(chosenPlayer)
                    }
                )
            }

            showHunterKillResult -> {
                HunterKillResult(
                    hunterTarget = hunterTarget!!,
                    role = roles[hunterTarget!!] ?: "Inconnu",
                    onConfirm = {
                        showHunterKillResult = false
                        currentStep++
                    }
                )
            }

            currentStep == 0 -> {
                NightResults(eliminatedPlayers, roles) {
                    if (eliminatedPlayers.any { roles[it] == "Chasseur" }) {
                        showHunterChoice = true
                    } else {
                        currentStep++
                    }
                }
            }

            currentStep == 1 -> DebatePhase { currentStep++ }

            currentStep == 2 -> VotingPhase(
                playerNames = updatedPlayerNames,
                tiedPlayers = votedPlayers,
                onVoteComplete = { votes ->
                    val maxVotes = votes.values.maxOrNull() ?: 0
                    val tiedPlayers = votes.filter { it.value == maxVotes }.keys.toList()

                    if (tiedPlayers.size == 1) {
                        val eliminated = tiedPlayers.first()
                        onPlayerEliminated(eliminated)
                        updatedPlayerNames.remove(eliminated)
                        eliminatedPlayersDuringDay.add(eliminated)
                        votedPlayers = tiedPlayers

                        if (roles[eliminated] == "Chasseur") {
                            showHunterChoice = true
                        } else {
                            currentStep++
                        }
                    } else {
                        votedPlayers = tiedPlayers
                        isRevote = true
                        currentStep++
                    }
                }
            )

            currentStep == 3 -> VoteResult(
                roles = roles,
                eliminatedPlayer = if (votedPlayers.size == 1) votedPlayers.first() else null,
                tiedPlayers = if (votedPlayers.size > 1) votedPlayers else emptyList(),
                onRevote = {
                    isRevote = true
                    currentStep--
                },
                onNextPhase = { onNightStart() }
            )
        }
    }
}

@Composable
fun NightResults(eliminatedPlayers: List<String>, roles: MutableMap<String, String>, onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Les victimes de la nuit sont :", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        if (eliminatedPlayers.isNotEmpty()) {
            eliminatedPlayers.forEach { player ->
                Text("💀 $player - ${roles[player] ?: "Erreur"}", fontSize = 20.sp, color = Color.Red)
            }
        } else {
            Text("🎉 Personne n'a été éliminé cette nuit !", fontSize = 18.sp, color = Color.Green)
        }

        Button(onClick = onContinue) {
            Text("Passer au débat")
        }
    }
}

@Composable
fun DebatePhase(onContinue: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Les joueurs discutent pour trouver les Loups-Garous...", fontSize = 18.sp)
        Button(onClick = onContinue) {
            Text("Passer au vote")
        }
    }
}

@Composable
fun GameEndScreen(
    winnerMessage: String,
    onConfirm: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = winnerMessage,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Magenta,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onConfirm() }) {
            Text("Continuer")
        }
    }
}

@Composable
fun VotingPhase(
    playerNames: List<String>,
    tiedPlayers: List<String>,
    onVoteComplete: (Map<String, Int>) -> Unit
) {
    var currentVoterIndex by remember { mutableIntStateOf(0) }
    val voteResults = remember { mutableStateMapOf<String, Int>() }
    var selectedVote by remember { mutableStateOf<String?>(null) }

    val currentVoter = playerNames.getOrNull(currentVoterIndex)
    val possibleVotes = if (tiedPlayers.isNotEmpty()) tiedPlayers else playerNames

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (currentVoter != null) {
            Text(
                text = "📱 Passez le téléphone à $currentVoter",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(possibleVotes) { player ->
                    val isSelected = player == selectedVote

                    SelectableCard(
                        player,
                        isSelected,
                        selectedBorderColor = Color.Green,
                        onClick = { selectedVote = if (isSelected) null else player }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedVote != null) {
                Button(
                    onClick = {
                        voteResults[selectedVote!!] = voteResults.getOrDefault(selectedVote!!, 0) + 1

                        if (currentVoterIndex < playerNames.size - 1) {
                            currentVoterIndex++
                            selectedVote = null
                        } else {
                            onVoteComplete(voteResults.toMap())
                        }
                    }
                ) {
                    Text("Confirmer le vote")
                }
            }
        }
    }
}

@Composable
fun VoteResult(
    eliminatedPlayer: String?,
    roles: Map<String, String>,
    tiedPlayers: List<String>,
    onRevote: () -> Unit,
    onNextPhase: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (eliminatedPlayer != null) {
            Text(
                text = "🚨 $eliminatedPlayer a été éliminé !",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Text(
                text = "Il était ${roles[eliminatedPlayer] ?: "Inconnu"}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNextPhase) {
                Text("Passer à la nuit 🌙")
            }
        } else if (tiedPlayers.isNotEmpty()) {
            Text(
                text = "⚖️ Égalité entre : ${tiedPlayers.joinToString(", ")}",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Un revote est nécessaire pour les départager.",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onRevote) {
                    Text("Revoter 🗳️")
                }
            }
        } else {
            Text(
                text = "⚖️ Personne n'a été éliminé aujourd’hui !",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNextPhase) {
                Text("Passer à la nuit 🌙")
            }
        }
    }
}

@Composable
fun ThiefChoice(centerCards: List<String>, onChoiceMade: (String) -> Unit) {
    var selectedCard by remember { mutableStateOf<String?>(null) }
    var hasConfirmed by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "🃏 Tu es le Voleur. Choisis une carte ou garde la tienne.",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
            centerCards.forEach { card ->
                SelectableCard(
                    card,
                    selectedCard == card,
                    selectedBorderColor = Color.Green,
                    onClick = { selectedCard = card },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        SelectableCard(
            "Garder ma carte (Villageois)",
            selectedCard == "Villageois",
            selectedBorderColor = Color.Green,
            onClick = { selectedCard = "Villageois" },
            modifier = Modifier.fillMaxWidth(0.4f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton de confirmation (apparaît uniquement après un choix)
        if (selectedCard != null && !hasConfirmed) {
            Button(onClick = { onChoiceMade(selectedCard!!); hasConfirmed = true }) {
                Text("Confirmer le choix")
            }
        }
    }
}

@Composable
fun CupidChoice(playerNames: List<String>, onLoversChosen: (String, String) -> Unit) {
    val lovers = remember { mutableStateListOf<String>() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "💘 Cupidon, choisis deux joueurs à rendre amoureux.",
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(playerNames) { player ->
                var isSelected = player in lovers

                SelectableCard(
                    player,
                    isSelected,
                    // Color pink
                    selectedBorderColor = Color(0xFFFFC0CB),
                    onClick = {
                        isSelected = !isSelected
                        if (isSelected) {
                            lovers.add(player)
                        } else {
                            lovers.remove(player)
                        }
                        if (lovers.size > 2) lovers.removeAt(0)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (lovers.size == 2) {
            Button(onClick = { onLoversChosen(lovers[0], lovers[1]) }) {
                Text("Confirmer le couple")
            }
        }
    }
}

@Composable
fun WerewolvesChoice(playerNames: List<String>, onVictimChosen: (String) -> Unit) {
    var selectedVictim by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "🐺 Les Loups-Garous choisissent une victime.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(playerNames) { player ->
                val isSelected = player == selectedVictim

                SelectableCard(
                    player,
                    isSelected,
                    selectedBorderColor = Color.Red,
                    onClick = { selectedVictim = if (isSelected) null else player },
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedVictim != null) {
            Button(onClick = { onVictimChosen(selectedVictim!!) }) {
                Text("Confirmer la victime")
            }
        }
    }
}

@Composable
fun WitchChoice(
    playerNames: List<String>,
    potionsAvailable: Pair<Boolean, Boolean>,
    wolfVictim: String?,
    onWitchAction: (Boolean, Boolean, String?) -> Unit
) {
    var selectedAction by remember { mutableStateOf<String?>(null) }
    var poisonTarget by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("🧙‍♀️ Sorcière, veux-tu utiliser une potion ?", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Affichage de la victime des Loups-Garous
        if (wolfVictim != null) {
            Text("❗ $wolfVictim a été attaqué cette nuit !", fontSize = 16.sp, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Choix entre les potions ou ne rien faire
        if (selectedAction == null) {
            Column {
                if (potionsAvailable.first && wolfVictim != null) {
                    Button(onClick = { selectedAction = "heal" }) {
                        Text("💊 Utiliser la potion de vie sur $wolfVictim")
                    }
                } else if (!potionsAvailable.first) {
                    Text("🚫 Potion de vie déjà utilisée", fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (potionsAvailable.second) {
                    Button(onClick = { selectedAction = "poison" }) {
                        Text("☠️ Utiliser la potion de mort")
                    }
                } else {
                    Text("🚫 Potion de mort déjà utilisée", fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { onWitchAction(false, false, null) }) {
                    Text("🚫 Ne rien faire")
                }
            }
        }

        // Si elle choisit la potion de mort, affichage des joueurs
        if (selectedAction == "poison") {
            Text("Choisis un joueur à éliminer :", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(playerNames.filter { it != wolfVictim }) { player ->
                    val isSelected = player == poisonTarget

                    SelectableCard(
                        player,
                        isSelected,
                        selectedBorderColor = Color.Red,
                        onClick = { poisonTarget = if (isSelected) null else player },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (poisonTarget != null) {
                Button(onClick = { onWitchAction(false, true, poisonTarget) }) {
                    Text("Confirmer l'élimination de $poisonTarget")
                }
            }
        }

        // Si elle choisit la potion de vie, passer directement à la suite
        if (selectedAction == "heal") {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onWitchAction(true, false, null) }) {
                Text("Confirmer le sauvetage de $wolfVictim")
            }
        }
    }
}

@Composable
fun SeerChoice(playerNames: List<String>, roles: Map<String, String>, onCardRevealed: (String, String) -> Unit) {
    var selectedPlayer by remember { mutableStateOf<String?>(null) }
    var revealedRole by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("🔮 Voyante, choisis un joueur à observer.", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // add spacing to grid items
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(playerNames) { player ->
                val isSelected = player == selectedPlayer
                val isDisabled = selectedPlayer != null && !isSelected

                SelectableCard(
                    player,
                    isSelected,
                    isEnabled = !isDisabled,
                    selectedBorderColor = Color.Green,
                    onClick = {
                        selectedPlayer = player
                        revealedRole = roles[player]
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (revealedRole != null && selectedPlayer != null) {
            Text("🔮 Carte de $selectedPlayer : $revealedRole", fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onCardRevealed(selectedPlayer!!, revealedRole!!) }) {
                Text("Continuer")
            }
        }
    }
}

@Composable
fun HunterChoice(
    alivePlayers: List<String>,
    onPlayerChosen: (String) -> Unit
) {
    var selectedPlayer by remember { mutableStateOf<String?>(null) }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("🎯 Le Chasseur doit éliminer un joueur", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(alivePlayers) { player ->
                SelectableCard(
                    text = player,
                    isSelected = selectedPlayer == player,
                    isEnabled = true,
                    onClick = { selectedPlayer = player }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onPlayerChosen(selectedPlayer!!) },
            enabled = selectedPlayer != null
        ) {
            Text("Confirmer l'élimination")
        }
    }
}

@Composable
fun HunterKillResult(
    hunterTarget: String,
    role: String,
    onConfirm: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "🔫 Le Chasseur a tué : $hunterTarget",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "🎭 Son rôle était : $role",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onConfirm) {
            Text("Continuer ⏩")
        }
    }
}

@Composable
fun SelectableCard(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean = true,
    modifier: Modifier = Modifier,
    cardColor: Color = Color.White,
    selectedBorderColor: Color = Color.Green,
    textColor: Color = Color.Black,
    textSize: TextUnit = 20.sp
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(enabled = isEnabled) { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = if (isSelected) BorderStroke(2.dp, selectedBorderColor) else null,
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = text,
                fontSize = textSize,
                fontWeight = FontWeight.Medium,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

fun nombreEnOrdinal(n: Int, feminin: Boolean = true): String {
    val ordinaux = mapOf(
        1 to if (feminin) "Première" else "Premier",
        2 to "Deuxième",
        3 to "Troisième",
        4 to "Quatrième",
        5 to "Cinquième",
        6 to "Sixième",
        7 to "Septième",
        8 to "Huitième",
        9 to "Neuvième",
        10 to "Dixième"
    )

    if (n in ordinaux) return ordinaux[n]!!

    return when {
        n % 10 == 1 && n != 11 -> "${nombreEnLettres(n - 1)}-et-unième"
        else -> "${nombreEnLettres(n)}ième"
    }
}

fun nombreEnLettres(n: Int): String {
    val unites = listOf(
        "", "Un", "Deux", "Trois", "Quatre", "Cinq", "Six", "Sept", "Huit", "Neuf"
    )
    val dizaines = listOf(
        "", "Dix", "Vingt", "Trente", "Quarante", "Cinquante", "Soixante", "Soixante-dix", "Quatre-vingt", "Quatre-vingt-dix"
    )

    return when {
        n < 10 -> unites[n]
        n < 20 -> listOf(
            "Dix", "Onze", "Douze", "Treize", "Quatorze", "Quinze", "Seize", "Dix-sept", "Dix-huit", "Dix-neuf"
        )[n - 10]
        n % 10 == 0 -> dizaines[n / 10]
        else -> "${dizaines[n / 10]}-${unites[n % 10]}"
    }
}