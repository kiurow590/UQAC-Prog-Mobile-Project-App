package com.example.uqac_progmob_project

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uqac_progmob_project.databinding.ActivityGameDetailBinding

class GameDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityGameDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val backButton = binding.topBanner.backButton3
        backButton.setOnClickListener {
            finish()
        }

        val gameName = intent.getStringExtra("GAME_NAME")
        val gameDescription = intent.getStringExtra("GAME_DESCRIPTION")

        binding.gameNameTextView.text = gameName
        binding.gameDescriptionTextView.text = gameDescription

        // si on clique sur le bouton de retour on ferme l'activité


        binding.playButton.setOnClickListener {
            // Logique pour démarrer le jeu
            // en fonction du jeu on joue un Toast
            when (gameName) {
                "Trimann" -> {
                    // Démarrer le jeu Trimann
                    Toast.makeText(this, "Démarrer le jeu Trimann", Toast.LENGTH_SHORT).show()
                }
                "Bleiz Garou" -> {
                    // Démarrer le jeu Bleiz Garou
                    Toast.makeText(this, "Démarrer le jeu Bleiz Garou", Toast.LENGTH_SHORT).show()
                }
                "Course E-Pic" -> {
                    // Démarrer le jeu Course E-Pic
                    Toast.makeText(this, "Démarrer le jeu Course E-Pic", Toast.LENGTH_SHORT).show()
                }
                "Et Boom" -> {
                    // Démarrer le jeu Et Boom
                    Toast.makeText(this, "Démarrer le jeu Et Boom", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}