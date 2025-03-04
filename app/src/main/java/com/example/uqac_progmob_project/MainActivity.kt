package com.example.uqac_progmob_project

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.uqac_progmob_project.databinding.MainPageBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainPageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainPageBinding.inflate(layoutInflater)
        supportActionBar?.hide()
        setContentView(binding.root)

        binding.greenButton.setOnClickListener {
            Toast.makeText(this, "Bouton Vert cliqué !", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, GameChoose::class.java)
            startActivity(intent)
        }

        binding.grayButton.setOnClickListener {
            Toast.makeText(this, "This feature is not available yet", Toast.LENGTH_SHORT).show()
        }

        binding.quitButton.setOnClickListener {
            finishAndRemoveTask()
        }

        // Configurer le bouton de paramètres pour ouvrir le fragment de dialogue
        binding.settingsButton.setOnClickListener {
            val dialog = SettingsDialogFragment()
            dialog.show(supportFragmentManager, "SettingsDialogFragment")
        }
    }
}