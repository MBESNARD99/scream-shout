package com.example.screamshout

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val playButton: Button = findViewById(R.id.startGameButton)
        val rulesButton: Button = findViewById(R.id.rulesButton)
        val creditsButton: Button = findViewById(R.id.creditsButton)
        val settingsButton: ImageButton = findViewById(R.id.settingsButton)

        playButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        rulesButton.setOnClickListener {
            showRulesDialog()
        }

        creditsButton.setOnClickListener {
            showCreditsDialog()
        }

        val rotate = AnimationUtils.loadAnimation(this, R.anim.rotate)
        settingsButton.setOnClickListener {
            it.startAnimation(rotate)
            // TODO: ouvrir menu options
        }
    }

    private fun showCreditsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_credits, null)

        val dialog = AlertDialog.Builder(this, R.style.BlurDialogTheme)
            .setView(dialogView)
            .create()

        dialog.setOnShowListener {
            // Animation d'entrée
            val animIn = AnimationUtils.loadAnimation(this, R.anim.dialog_enter)
            dialogView.startAnimation(animIn)

            // Lecture du son
            val mediaPlayer = MediaPlayer.create(this, R.raw.dialog_open)
            mediaPlayer.setOnCompletionListener { it.release() }
            mediaPlayer.start()
        }

        dialogView.findViewById<Button>(R.id.closeButton).setOnClickListener {
            val animOut = AnimationUtils.loadAnimation(this, R.anim.dialog_exit)
            dialogView.startAnimation(animOut)
            dialogView.postDelayed({ dialog.dismiss() }, animOut.duration)
        }

        dialog.show()
    }

    private fun showRulesDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_rules, null)

        val dialog = AlertDialog.Builder(this, R.style.BlurDialogTheme)
            .setView(dialogView)
            .create()

        dialog.setOnShowListener {
            val animIn = AnimationUtils.loadAnimation(this, R.anim.dialog_enter)
            dialogView.startAnimation(animIn)

            val mediaPlayer = MediaPlayer.create(this, R.raw.dialog_open)
            mediaPlayer.setOnCompletionListener { it.release() }
            mediaPlayer.start()
        }

        dialogView.findViewById<Button>(R.id.closeRulesButton).setOnClickListener {
            val animOut = AnimationUtils.loadAnimation(this, R.anim.dialog_exit)
            dialogView.startAnimation(animOut)
            dialogView.postDelayed({ dialog.dismiss() }, animOut.duration)
        }

        dialog.show()
    }


}
