package com.example.uqac_progmob_project

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uqac_progmob_project.databinding.ActivityGameSessionBinding

class GameSessionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameSessionBinding
    private var numberOfPlayers = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityGameSessionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton = binding.topBanner.backButton3
        backButton.setOnClickListener {
            finish()
        }

        val gameName = intent.getStringExtra("GAME_NAME")
        binding.gameNameTextView.text = gameName

        val gameSessionNameEditText = findViewById<EditText>(R.id.gameSessionNameEditText)
        val numberOfPlayersTextView = findViewById<TextView>(R.id.numberOfPlayersTextView)
        val playersContainer = findViewById<LinearLayout>(R.id.playersContainer)
        val decreasePlayersButton = findViewById<Button>(R.id.decreasePlayersButton)
        val increasePlayersButton = findViewById<Button>(R.id.increasePlayersButton)

        decreasePlayersButton.setOnClickListener {
            if (numberOfPlayers > 2) {
                numberOfPlayers--
                numberOfPlayersTextView.text = numberOfPlayers.toString()
                updatePlayerFields(playersContainer)
            }
        }

        increasePlayersButton.setOnClickListener {
            if (numberOfPlayers < 5) {
                numberOfPlayers++
                numberOfPlayersTextView.text = numberOfPlayers.toString()
                updatePlayerFields(playersContainer)
            }
        }

        updatePlayerFields(playersContainer)

        binding.playButton.setOnClickListener {
            val gameSessionName = gameSessionNameEditText.text.toString()
            val playerNames = mutableListOf<String>()
            for (i in 0 until playersContainer.childCount) {
                val playerNameEditText = playersContainer.getChildAt(i) as EditText
                playerNames.add(playerNameEditText.text.toString())
            }

            // Logique pour démarrer le jeu
            println("Game Session Name: $gameSessionName")
            println("Number of Players: $numberOfPlayers")
            println("Player Names: $playerNames")

            Toast.makeText(this, "Démarrer le jeu $gameName", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePlayerFields(container: LinearLayout) {
        container.removeAllViews()
        for (i in 1..numberOfPlayers) {
            val playerNameEditText = EditText(this).apply {
                hint = "${getString(R.string.playerName)} $i"
            }
            container.addView(playerNameEditText)
        }
    }
}