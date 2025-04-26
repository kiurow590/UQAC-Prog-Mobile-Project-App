package com.example.uqac_progmob_project.games.werewolfGame

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global.getString
import android.speech.tts.TextToSpeech
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
import com.example.uqac_progmob_project.R
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uqac_progmob_project.BaseActivity
import com.example.uqac_progmob_project.gameChoose.FinalResult
import kotlinx.coroutines.delay
import java.util.Locale

class WerewolfGame : BaseActivity(), TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tts = TextToSpeech(this, this)

        val gameSessionName = intent.getStringExtra("GAMESESSIONNAME") ?: ""
        val playerNames = intent.getStringArrayListExtra("PLAYERSESSIONNAME") ?: listOf()

        println("Game Session Name: $gameSessionName")
        println("Number of Players: ${playerNames.size}")
        println("Player Names: $playerNames")

        setContent {
            WerewolfGame(gameSessionName, playerNames) { text ->
                speakOut(text)
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts!!.setLanguage(Locale.FRENCH)

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeech", "Language not supported")
            }
        } else {
            Log.e("TextToSpeech", "Initialization failed")
        }
    }

    override fun onDestroy() {
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }

        super.onDestroy()
    }

    fun speakOut(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }
}

@Composable
fun WerewolfGame(
    gameSessionName: String,
    playerNames: List<String>,
    speakOut: (String) -> Unit
) {
    val context = LocalContext.current
    var gamePhase by remember { mutableStateOf("revealCards") }
    val roles = remember { assignRoles(context, playerNames).toMutableMap() }
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

    val werewolf = stringResource(id = R.string.bleiz_garou_role_werewolf)

    fun checkVictory() {
        val alivePlayers = playerNames - eliminatedPlayers
        val werewolvesAlive = alivePlayers.count { roles[it] == werewolf }
        val villagersAlive = alivePlayers.count { roles[it] != werewolf }

        if (loverPair != null) {
            val (lover1, lover2) = loverPair!!
            if (alivePlayers.containsAll(listOf(lover1, lover2)) && alivePlayers.size == 2) {
                winnerMessage = context.getString(R.string.bleiz_garou_winner_message_lovers)
                playerScores[playerNames.indexOf(lover1)] = 1
                playerScores[playerNames.indexOf(lover2)] = 1
                gamePhase = "results"
                return
            }
        }

        if (werewolvesAlive > 0 && villagersAlive == 0) {
            winnerMessage = context.getString(R.string.bleiz_garou_winner_message_werewolves)
            playerNames.filter { roles[it] == werewolf }.forEach {
                playerScores[playerNames.indexOf(it)] = 1
            }
            gamePhase = "results"
        } else if (werewolvesAlive == 0) {
            playerNames.filter { roles[it] != werewolf }.forEach {
                playerScores[playerNames.indexOf(it)] = 1
            }
            winnerMessage = context.getString(R.string.bleiz_garou_winner_message_villager)
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
                    val thiefPlayer = playerNames.find { roles[it] == context.getString(R.string.bleiz_garou_role_thief) }
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
            text = stringResource(id = R.string.bleiz_garou_pass_the_phone_to, currentPlayer),
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
            Text(text = stringResource(id = R.string.bleiz_garou_next_player))
        }
    }
}

fun assignRoles(context: Context, playerNames: List<String>): Map<String, String> {
    val playerCount = playerNames.size

    val roleDistribution = mapOf(
        8 to listOf(
            context.getString(R.string.bleiz_garou_role_hunter),
            context.getString(R.string.bleiz_garou_role_werewolf),
            context.getString(R.string.bleiz_garou_role_werewolf),
            context.getString(R.string.bleiz_garou_role_little_girl),
            context.getString(R.string.bleiz_garou_role_villager),
            context.getString(R.string.bleiz_garou_role_villager),
            context.getString(R.string.bleiz_garou_role_witch),
            context.getString(R.string.bleiz_garou_role_seer)
        ),
        9 to listOf(
            context.getString(R.string.bleiz_garou_role_hunter),
            context.getString(R.string.bleiz_garou_role_cupid),
            context.getString(R.string.bleiz_garou_role_werewolf),
            context.getString(R.string.bleiz_garou_role_werewolf),
            context.getString(R.string.bleiz_garou_role_little_girl),
            context.getString(R.string.bleiz_garou_role_villager),
            context.getString(R.string.bleiz_garou_role_villager),
            context.getString(R.string.bleiz_garou_role_witch),
            context.getString(R.string.bleiz_garou_role_seer)
        )
        // Ajoutez les autres distributions ici...
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
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(0) }

    val hasThief = roles.containsValue(stringResource(id = R.string.bleiz_garou_role_thief))
    val hasCupid = roles.containsValue(stringResource(id = R.string.bleiz_garou_role_cupid))
    val seerPlayer = playerNames.find { roles[it] == stringResource(id = R.string.bleiz_garou_role_seer) }
    val witchPlayer = playerNames.find { roles[it] == stringResource(id = R.string.bleiz_garou_role_witch) }
    val seerAlive = seerPlayer != null && seerPlayer in playerNames
    val witchAlive = witchPlayer != null && witchPlayer in playerNames
    val firstNight = nightCount == 1


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

    val currentLocale = LocalContext.current.resources.configuration.locales[0]
    val steps = stringArrayResource(id = R.array.bleiz_garou_night_phase_step)
        .map {
            String.format(it, nombreEnOrdinal(nightCount, locale = currentLocale))
        }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentStep) {
            1 -> {
                val centerCards = listOf(
                    context.getString(R.string.bleiz_garou_role_hunter),
                    context.getString(R.string.bleiz_garou_role_werewolf)
                )
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
                    Text(context.getString(R.string.bleiz_garou_start_day))
                }
            }

            else -> {
                Text(text = steps[currentStep], fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { nextStep() }) {
                    Text(context.getString(R.string.bleiz_garou_next))
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
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(0) }
    var votedPlayers by remember { mutableStateOf<List<String>>(emptyList()) }
    var isRevote by remember { mutableStateOf(false) }
    var showHunterChoice by remember { mutableStateOf(false) }
    var hunterTarget by remember { mutableStateOf<String?>(null) }
    var showHunterKillResult by remember { mutableStateOf(false) }

    val updatedPlayerNames = remember { playerNames.toMutableStateList() }
    val eliminatedPlayersDuringDay = remember { mutableStateListOf<String>() }

    val steps = stringArrayResource(id = R.array.bleiz_garou_day_phase_step)

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
                    if (eliminatedPlayers.any { roles[it] == context.getString(R.string.bleiz_garou_role_hunter) }) {
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

                        if (roles[eliminated] == context.getString(R.string.bleiz_garou_role_hunter)) {
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
        Text(stringResource(id = R.string.bleiz_garou_night_results_victims_are), fontSize = 18.sp, fontWeight = FontWeight.Bold)

        if (eliminatedPlayers.isNotEmpty()) {
            eliminatedPlayers.forEach { player ->
                Text("💀 $player - ${roles[player] ?: "Erreur"}", fontSize = 20.sp, color = Color.Red)
            }
        } else {
            Text(stringResource(id = R.string.bleiz_garou_night_results_no_victims), fontSize = 18.sp, color = Color.Green)
        }

        Button(onClick = onContinue) {
            Text(stringResource(id = R.string.bleiz_garou_night_results_go_debate))
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
        Text(stringResource(id = R.string.bleiz_garou_debate_phase), fontSize = 18.sp)
        Button(onClick = onContinue) {
            Text(stringResource(id = R.string.bleiz_garou_debate_phase_go_vote))
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
            Text(stringResource(id = R.string.bleiz_garou_next))
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
                text = stringResource(id = R.string.bleiz_garou_pass_the_phone_to, currentVoter),
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
                    Text(stringResource(id = R.string.bleiz_garou_voting_phase_confirm))
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
                text = stringResource(id = R.string.bleiz_garou_vote_result, eliminatedPlayer),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
            Text(
                text = stringResource(id = R.string.bleiz_garou_vote_result_he_was, roles[eliminatedPlayer] ?: "Inconnu"),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNextPhase) {
                Text(stringResource(id = R.string.bleiz_garou_vote_result_go_to_night))
            }
        } else if (tiedPlayers.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.bleiz_garou_vote_result_tied, tiedPlayers.joinToString(", ")),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Yellow
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.bleiz_garou_vote_result_revote_needed),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onRevote) {
                    Text(stringResource(id = R.string.bleiz_garou_vote_result_revote))
                }
            }
        } else {
            Text(
                text = stringResource(id = R.string.bleiz_garou_vote_result_no_eliminated),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Green
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNextPhase) {
                Text(stringResource(id = R.string.bleiz_garou_vote_result_go_to_night))
            }
        }
    }
}

@Composable
fun ThiefChoice(centerCards: List<String>, onChoiceMade: (String) -> Unit) {
    val context = LocalContext.current
    var selectedCard by remember { mutableStateOf<String?>(null) }
    var hasConfirmed by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(id = R.string.bleiz_garou_thief_choice),
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
            stringResource(id = R.string.bleiz_garou_thief_choice_keep_card),
            selectedCard == stringResource(id = R.string.bleiz_garou_role_villager),
            selectedBorderColor = Color.Green,
            onClick = { selectedCard = context.getString(R.string.bleiz_garou_role_villager)},
            modifier = Modifier.fillMaxWidth(0.4f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Bouton de confirmation (apparaît uniquement après un choix)
        if (selectedCard != null && !hasConfirmed) {
            Button(onClick = { onChoiceMade(selectedCard!!); hasConfirmed = true }) {
                Text(stringResource(id = R.string.bleiz_garou_thief_choice_confirm))
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
            text = stringResource(id = R.string.bleiz_garou_cupid_choice),
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
                Text(stringResource(id = R.string.bleiz_garou_cupid_choice_confirm))
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
            stringResource(id = R.string.bleiz_garou_werewolves_choice),
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
                Text(stringResource(id = R.string.bleiz_garou_werewolves_choice_confirm))
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
        Text(stringResource(id = R.string.bleiz_garou_witch_choice), fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Affichage de la victime des Loups-Garous
        if (wolfVictim != null) {
            Text(stringResource(id = R.string.bleiz_garou_witch_choice_attacked_player, wolfVictim), fontSize = 16.sp, color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Choix entre les potions ou ne rien faire
        if (selectedAction == null) {
            Column {
                if (potionsAvailable.first && wolfVictim != null) {
                    Button(onClick = { selectedAction = "heal" }) {
                        Text(stringResource(id = R.string.bleiz_garou_witch_choice_use_heal, wolfVictim))
                    }
                } else if (!potionsAvailable.first) {
                    Text(stringResource(id = R.string.bleiz_garou_witch_choice_already_used_heal), fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (potionsAvailable.second) {
                    Button(onClick = { selectedAction = "poison" }) {
                        Text(stringResource(id = R.string.bleiz_garou_witch_choice_use_poison))
                    }
                } else {
                    Text(stringResource(id = R.string.bleiz_garou_witch_choice_already_used_poison), fontSize = 14.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(onClick = { onWitchAction(false, false, null) }) {
                    Text(stringResource(id = R.string.bleiz_garou_witch_choice_do_nothing))
                }
            }
        }

        // Si elle choisit la potion de mort, affichage des joueurs
        if (selectedAction == "poison") {
            Text(stringResource(id = R.string.bleiz_garou_witch_choice_choose_player), fontSize = 16.sp, fontWeight = FontWeight.Bold)

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
                    Text(stringResource(id = R.string.bleiz_garou_witch_choice_confirm_kill, poisonTarget!!))
                }
            }
        }

        // Si elle choisit la potion de vie, passer directement à la suite
        if (selectedAction == "heal") {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onWitchAction(true, false, null) }) {
                Text(stringResource(id = R.string.bleiz_garou_witch_choice_confirm_heal, wolfVictim!!))
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
        Text(stringResource(id = R.string.bleiz_garou_seer_choice), fontSize = 18.sp, fontWeight = FontWeight.Bold)

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
            Text(stringResource(id = R.string.bleiz_garou_seer_choice_reveal, selectedPlayer!!, revealedRole!!), fontSize = 20.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { onCardRevealed(selectedPlayer!!, revealedRole!!) }) {
                Text(stringResource(id = R.string.bleiz_garou_next))
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
        Text(stringResource(id = R.string.bleiz_garou_hunter_choice), fontSize = 22.sp, fontWeight = FontWeight.Bold)

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
            Text(stringResource(id = R.string.bleiz_garou_hunter_choice_confirm))
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
            text = stringResource(id = R.string.bleiz_garou_hunter_kill, hunterTarget),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.bleiz_garou_hunter_kill_he_was, role),
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onConfirm) {
            Text(stringResource(id = R.string.bleiz_garou_next))
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

fun nombreEnOrdinal(n: Int, feminin: Boolean = true, locale: Locale = Locale.FRENCH): String {
    return when (locale) {
        Locale.FRENCH -> {
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

            when {
                n % 10 == 1 && n != 11 -> "${nombreEnLettres(n - 1)}-et-unième"
                else -> "${nombreEnLettres(n)}ième"
            }
        }

        Locale.ENGLISH -> {
            val suffix = when {
                n % 100 in 11..13 -> "th"
                n % 10 == 1 -> "st"
                n % 10 == 2 -> "nd"
                n % 10 == 3 -> "rd"
                else -> "th"
            }
            "$n$suffix"
        }

        else -> throw IllegalArgumentException("Unsupported locale")
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