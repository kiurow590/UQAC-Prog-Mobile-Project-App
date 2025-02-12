package com.example.uqac_progmob_project

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uqac_progmob_project.databinding.GameChooseBinding
import com.example.uqac_progmob_project.databinding.BannerVersion1Binding

class GameChoose : AppCompatActivity() {
    private lateinit var binding: GameChooseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = GameChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}