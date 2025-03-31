package com.example.screamshout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/*

Activité pour la page de Game Over

 */

class GameOverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_over)

        val restartButton: Button = findViewById(R.id.restartButton)
        val backToMenuButton: Button = findViewById(R.id.backToMenuButton)

        val scoreTextView: TextView = findViewById(R.id.currentScoreText)
        val bestScoreTextView: TextView = findViewById(R.id.bestScoreText)

        val playerName = GameManager.getPlayerName()
        val prefs = getSharedPreferences("leaderboard", MODE_PRIVATE)
        val bestScore = prefs.getInt(playerName, 0)

        scoreTextView.text = "Score : ${GameManager.score}"
        bestScoreTextView.text = "Meilleur score : $bestScore"

        // Action du bouton "Menu principal"
        backToMenuButton.setOnClickListener {
            GameManager.resetScore()
            startActivity(Intent(this, WelcomeActivity::class.java))
            finish()
        }

        // Action du bouton "Restart"
        restartButton.setOnClickListener {
            GameManager.resetScore()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
