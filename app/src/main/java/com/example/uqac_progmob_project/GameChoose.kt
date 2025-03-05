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

        binding.gameTrimann.setOnClickListener {
            startGameDetailActivity(getString(R.string.triMannGame), getString(R.string.triMannGameDescription))
        }

        binding.gameBleizGarou.setOnClickListener {
            startGameDetailActivity(getString(R.string.bleiz_garou), getString(R.string.bleiz_garou_Description))
        }

        binding.gameCourseEPic.setOnClickListener {
            startGameDetailActivity(getString(R.string.course_e_pic), getString(R.string.course_e_pic_description))
        }

        binding.gameEtBoom.setOnClickListener {
            startGameDetailActivity(getString(R.string.et_boom), getString(R.string.et_boom_description))
        }

    }

    /**
     * Démarrer l'activité de détail du jeu
     */
    private fun startGameDetailActivity(gameName: String, gameDescription: String) {
        val intent = Intent(this, GameDetailActivity::class.java).apply {
            putExtra("GAME_NAME", gameName)
            putExtra("GAME_DESCRIPTION", gameDescription)
        }
        startActivity(intent)
    }
}