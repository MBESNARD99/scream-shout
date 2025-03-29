package com.example.screamshout

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val playButton: Button = findViewById(R.id.playButton)
        playButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Ferme la fenêtre de bienvenue
        }
    }
}
