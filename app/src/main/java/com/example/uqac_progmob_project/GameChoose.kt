package com.example.uqac_progmob_project

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uqac_progmob_project.databinding.GameChooseBinding

class GameChoose : BaseActivity() {
    private lateinit var binding: GameChooseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = GameChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Accéder aux éléments de la bannière
        val backButton = binding.topBanner.backButton
        val historybutton = binding.topBanner.history
        val accountbutton = binding.topBanner.account

        // Configurer le bouton retour pour fermer l'activité en cours
        backButton.setOnClickListener {
            finish()
        }


        accountbutton.setOnClickListener {
            val dialog = AccountDialogFragment()
            dialog.show(supportFragmentManager, "AccountDialogFragment")
        }
    }
}