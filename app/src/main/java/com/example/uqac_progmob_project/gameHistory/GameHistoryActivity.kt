package com.example.uqac_progmob_project.gameHistory

import android.os.Bundle
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import com.example.uqac_progmob_project.R
import com.example.uqac_progmob_project.databinding.ActivityGameHistoryBinding

class GameHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameHistoryBinding
    private lateinit var expandableListView: ExpandableListView
    private lateinit var adapter: GameHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityGameHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton = binding.topBanner.backButton3
        backButton.setOnClickListener {
            finish()
        }

        expandableListView = findViewById(R.id.historyExpandableListView)

        // Example data
        val historyData = listOf(
            GameHistoryItem("Game 1", "Session 1", listOf(
                PlayerScore("Player 1", 100),
                PlayerScore("Player 2", 80),
                PlayerScore("Player 3", 60)
            )),
            GameHistoryItem("Game 2", "Session 2", listOf(
                PlayerScore("Player A", 90),
                PlayerScore("Player B", 70),
                PlayerScore("Player C", 50)
            ))
        )

        adapter = GameHistoryAdapter(this, historyData)
        expandableListView.setAdapter(adapter)
    }
}

data class GameHistoryItem(val gameName: String, val sessionName: String, val playerScores: List<PlayerScore>)
data class PlayerScore(val playerName: String, val score: Int)