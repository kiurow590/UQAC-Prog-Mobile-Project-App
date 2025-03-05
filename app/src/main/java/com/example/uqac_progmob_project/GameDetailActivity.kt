package com.example.uqac_progmob_project

import android.content.Intent
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
            val intent = Intent(this, GameSessionActivity::class.java).apply {
                putExtra("GAME_NAME", gameName)
            }
            startActivity(intent)
        }
    }
}