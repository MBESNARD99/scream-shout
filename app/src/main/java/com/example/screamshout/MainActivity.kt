package com.example.screamshout

import android.Manifest
import android.animation.ValueAnimator
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlin.math.max

/*

C’est la classe principale du jeu :

 - Gère le personnage
 - Anime le fond
 - Gère les obstacles
 - Écoute le micro pour déclencher les sauts
 - Détecte les collisions
 - Affiche et met à jour le score

 */

class MainActivity : AppCompatActivity() {

    private lateinit var gameLayout: FrameLayout //conteneur du jeu
    private lateinit var character: ImageView
    private lateinit var scoreText: TextView

    private val handler = Handler(Looper.getMainLooper()) //handler pour les mises à jour du jeu

    //Physique du jeu
    private var velocityY = 0f
    private var gravity = 1f
    private var jumpForce = 0f

    private val pillars = mutableListOf<View>()

    private var isRecording = false
    private lateinit var micListener: MicrophoneListener

    // Initialise les vues, le micro et lance la logique du jeu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameLayout = findViewById(R.id.gameLayout)
        character = findViewById(R.id.character)
        scoreText = findViewById(R.id.scoreText)

        // MICRO
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
                startBackgroundScroll()
            }
        }
    }

    // Centre le personnage en fonction de l'écran pour éviter des bugs
    private fun centerCharacterOnScreen() {
        character.x = 200f
        character.y = (gameLayout.height / 2f) - character.height / 2
    }

    // Initialisation du micro avec permission
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun initMicrophoneListener() {
        micListener = MicrophoneListener { volume ->
            if (volume > 0.15f) {
                jumpForce = max(15f, volume * 15) // Gère la puissance du saut en fonction du cri
            }
        }
        micListener.start()
        isRecording = true
    }

    // Méthode appelée toutes les ~16ms
    private fun updateGame() {
        if (!isRecording) return

        // Faire sauter le personnage
        if (jumpForce > 0) {
            velocityY = -jumpForce
            jumpForce = 0f
        }

        // Appliquer la gravité
        velocityY += gravity
        character.y += velocityY

        // Chaque pilier est déplacé vers la gauche
        val iterator = pillars.iterator()
        while (iterator.hasNext()) {
            val pillar = iterator.next()
            pillar.x -= 5f
            // SI il est plus sur l'écran, on le retire pour éviter l'utilisation de ressource inutile
            if (pillar.x + pillar.width < 0) {
                gameLayout.removeView(pillar)
                iterator.remove()
            }
        }

        checkCollisions()

        // Gestion des scores
        val previousScore = scoreText.text.toString().toInt()
        GameManager.updateScore(character, pillars)

        // Si le score augmente, on l'incrémente avec une animation
        if (GameManager.score > previousScore) {
            scoreText.text = GameManager.score.toString()
            scoreText.animate().scaleX(1.4f).scaleY(1.4f).setDuration(100).withEndAction {
                scoreText.animate().scaleX(1f).scaleY(1f).setDuration(100)
            }
        }

        handler.postDelayed({ updateGame() }, 16) // boucler la méthode
    }

    // Créer un couple de pilier (haut/bas)
    private fun createPillars() {
        PillarFactory.createPillars(
            context = this,
            gameLayout = gameLayout,
            layoutHeight = gameLayout.height,
            layoutWidth = gameLayout.width,
            pillars = pillars
        ) {
            handler.postDelayed({ createPillars() }, 4000) // créer un pilier toutes les 4s
        }
    }

    // Vérifier les collisions
    private fun checkCollisions() {
        CollisionManager.checkCharacterCollision(
            character = character,
            pillars = pillars,
            layoutHeight = gameLayout.height
        ) {
            gameOver()
        }
    }

    // Reset si fin du jeu
    private fun gameOver() {
        if (!isFinishing) {
            isRecording = false
            velocityY = 0f
            jumpForce = 0f

            GameManager.saveScore(this, GameManager.score)

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

    // Défilement de l'image en fond pour simuler un mouvement
    private fun startBackgroundScroll() {
        val bg1 = findViewById<ImageView>(R.id.backgroundScroll1)
        val bg2 = findViewById<ImageView>(R.id.backgroundScroll2)

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()

        // Défilement position initiale -> hors de l'écran vers la gauche
        val animator = ValueAnimator.ofFloat(0f, -screenWidth)
        // Durée de 10s, sur une boucle infinie avec la même vitesse
        animator.duration = 10000L
        animator.repeatCount = ValueAnimator.INFINITE
        animator.interpolator = LinearInterpolator()

        // Illusion d'un fond continu qui défile car les deux images se superposent
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            bg1.translationX = value
            bg2.translationX = value + screenWidth
            // Repositionner l'image si complètement sortie
            if (bg1.translationX <= -screenWidth) {
                bg1.translationX = bg2.translationX + screenWidth
            }
            if (bg2.translationX <= -screenWidth) {
                bg2.translationX = bg1.translationX + screenWidth
            }
        }
        animator.start()
    }

    // Nettoyage
    override fun onDestroy() {
        super.onDestroy()
        isRecording = false
        micListener.stop()
        handler.removeCallbacksAndMessages(null)
    }
}
