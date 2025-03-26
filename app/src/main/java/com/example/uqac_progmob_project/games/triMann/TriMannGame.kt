package com.example.uqac_progmob_project.games.triMann

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
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
            TrimannGameScreen(gameSessionName, playerNames)
        }
    }
}
@SuppressLint("MutableCollectionMutableState")
@Composable
fun TrimannGameScreen(gameSessionName : String, playerNames : List<String>) {
    var dice1 by remember { mutableStateOf(1) }
    var dice2 by remember { mutableStateOf(1) }
    var resultMessage by remember { mutableStateOf("") }
    fun checkRules(d1: Int, d2: Int) {
        when {
            d1 == 3 || d2 == 3 || d1 + d2 == 3 -> {
                resultMessage = "Tu es le Tri-mann !"
            }
            d1 % 2 != 0 && d2 % 2 != 0 -> {
                resultMessage = "Double impair ! Tu bois !"
            }
            d1 % 2 == 0 && d2 % 2 == 0 -> {
                resultMessage = "Double pair ! Tu distribues !"
            }
            d1 == 6 || d2 == 6 -> {
                if (d1 == 6 && d2 == 6) {
                    resultMessage = "Deux 6 ! Désigne deux personnes pour un Chifoumi !"
                } else {
                    resultMessage = "Un 6, mets le nombre correspondant sur la table."
                }
            }
            else -> {
                resultMessage = "Lance encore les dés !"
            }
        }
    }
    // Fonction pour gérer le lancer des dés
    fun rollDice() {
        dice1 = Random.nextInt(1, 7)
        dice2 = Random.nextInt(1, 7)
        checkRules(dice1, dice2)
    }

    // Vérification des règles en fonction des dés


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Tri-mann Game", fontSize = 32.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(20.dp))

        // Affichage des dés
        Row {
            DiceImage(dice1)
            Spacer(modifier = Modifier.width(16.dp))
            DiceImage(dice2)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Affichage du message de résultat
        Text(text = resultMessage, fontSize = 24.sp, color = Color.Black)

        Spacer(modifier = Modifier.height(20.dp))

        // Bouton pour relancer les dés
        Button(onClick = { rollDice() }) {
            Text(text = "Relancer les dés")
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
    TrimannGameScreen(
        gameSessionName = "Partie Test",
        playerNames = listOf("Alice", "Bob", "Charlie")
    )
}