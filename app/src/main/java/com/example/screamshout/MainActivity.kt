package com.example.screamshout

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    private lateinit var gameLayout: FrameLayout
    private lateinit var character: ImageView
    private lateinit var scoreText: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var velocityY = 0f
    private var gravity = 1f
    private var jumpForce = 0f
    private var isRecording = false
    private val pillars = mutableListOf<View>()
    private lateinit var micListener: MicrophoneListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameLayout = findViewById(R.id.gameLayout)
        character = findViewById(R.id.character)
        scoreText = findViewById(R.id.scoreText)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 200)
        } else {
            initMicrophoneListener()
        }

        gameLayout.post {
            if (gameLayout.height > 0) {
                centerCharacterOnScreen()
                updateGame()
                createPillars()
            }
        }
    }

    private fun centerCharacterOnScreen() {
        character.x = 200f
        character.y = (gameLayout.height / 2f) - character.height / 2
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun initMicrophoneListener() {
        micListener = MicrophoneListener { volume ->
            if (volume > 0.15f) {
                jumpForce = max(15f, volume * 15)
            }
        }
        micListener.start()
        isRecording = true
    }

    private fun updateGame() {
        if (!isRecording) return

        if (jumpForce > 0) {
            velocityY = -jumpForce
            jumpForce = 0f
        }

        velocityY += gravity
        character.y += velocityY

        val iterator = pillars.iterator()
        while (iterator.hasNext()) {
            val pillar = iterator.next()
            pillar.x -= 5f
            if (pillar.x + pillar.width < 0) {
                gameLayout.removeView(pillar)
                iterator.remove()
            }
        }

        checkCollisions()

        val previousScore = scoreText.text.toString().toInt()
        GameManager.updateScore(character, pillars)
        if (GameManager.score > previousScore) {
            scoreText.text = GameManager.score.toString()
            scoreText.animate().scaleX(1.4f).scaleY(1.4f).setDuration(100).withEndAction {
                scoreText.animate().scaleX(1f).scaleY(1f).setDuration(100)
            }
        }

        handler.postDelayed({ updateGame() }, 16)
    }

    private fun createPillars() {
        PillarFactory.createPillars(
            context = this,
            gameLayout = gameLayout,
            layoutHeight = gameLayout.height,
            layoutWidth = gameLayout.width,
            pillars = pillars
        ) {
            handler.postDelayed({ createPillars() }, 4000)
        }
    }

    private fun checkCollisions() {
        CollisionManager.checkCharacterCollision(
            character = character,
            pillars = pillars,
            layoutHeight = gameLayout.height
        ) {
            gameOver()
        }
    }

    private fun gameOver() {
        if (!isFinishing) {
            isRecording = false
            velocityY = 0f
            jumpForce = 0f

            GameManager.handleGameOver(
                activity = this,
                handler = handler,
                audioRecord = null,
                character = character,
                pillars = pillars,
                gameLayout = gameLayout
            ) {
                micListener.stop()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isRecording = false
        micListener.stop()
        handler.removeCallbacksAndMessages(null)
    }
}
