package com.example.screamshout

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat  // Importation nécessaire pour ContextCompat
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    private lateinit var gameLayout: FrameLayout
    private lateinit var character: ImageView

    private val handler = Handler(Looper.getMainLooper())
    private var velocityY = 0f
    private var gravity = 2f
    private var jumpForce = 0f
    private var isRecording = false
    private val pillars = mutableListOf<View>()

    private var audioRecord: AudioRecord? = null
    private val bufferSize = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gameLayout = findViewById(R.id.gameLayout)
        character = findViewById(R.id.character)

        // Demander la permission pour enregistrer le son
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 200)
        } else {
            startRecording()
        }

        gameLayout.post {
            if (gameLayout.height > 0) {
                centerCharacterOnScreen()
                updateGame() // Lancer la boucle de mise à jour dès que le layout est prêt
                createPillars() // Créer le premier pilier immédiatement après le lancement du jeu
            }
        }
    }

    private fun centerCharacterOnScreen() {
        character.x = 200f
        character.y = (gameLayout.height / 2f) - character.height / 2
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun startRecording() {
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord?.startRecording()
        isRecording = true
        listenForShouts()
    }

    private fun listenForShouts() {
        val audioBuffer = ShortArray(bufferSize)

        Thread {
            while (isRecording) {
                val readSize = audioRecord?.read(audioBuffer, 0, bufferSize) ?: 0
                val maxAmplitude = audioBuffer.take(readSize).maxOrNull() ?: 0
                val volumeLevel = maxAmplitude / 32768f

                // Si le volume est supérieur à un seuil, le personnage "saute"
                if (volumeLevel > 0.10f) {
                    jumpForce = max(15f, volumeLevel * 15)  // La force de saut est liée au volume
                }

                Thread.sleep(100)
            }
        }.start()
    }

    private fun updateGame() {
        if (!isRecording) return  // Arrête updateGame si gameOver est déjà en cours
        // Mise à jour de la position du personnage avec la gravité
        if (jumpForce > 0) {
            velocityY = -jumpForce
            jumpForce = 0f
        }

        velocityY += gravity
        character.y += velocityY

        // Si le personnage sort de l'écran, game over
        if (character.y > gameLayout.height || character.y < 0) {
            gameOver()
        }

        // Vérifier la collision avec les piliers
        checkCollisions()

        // Relancer l'actualisation du jeu toutes les 16ms (~60fps)
        handler.postDelayed({ updateGame() }, 16)
    }

    private fun createPillars() {
        // Créer des piliers toutes les 2 secondes
        val pillarWidth = 100
        val holeHeight = 700
        val randomY = (gameLayout.height - holeHeight).let { (0..it).random() }  // Calculer la position aléatoire du trou

        // Créer le pilier supérieur (au-dessus du trou)
        val topPillar = View(this).apply {
            // Couleur visible pour tester
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_dark))  // Couleur du pilier
            val layoutParams = FrameLayout.LayoutParams(pillarWidth, randomY)
            layoutParams.leftMargin = gameLayout.width  // Positionner à droite de l'écran
            this.layoutParams = layoutParams
            // Placer le pilier supérieur tout en haut
            x = gameLayout.width.toFloat()
            y = 0f
        }

        // Créer le pilier inférieur (en dessous du trou)
        val bottomPillar = View(this).apply {
            // Couleur visible pour tester
            setBackgroundColor(ContextCompat.getColor(this@MainActivity, android.R.color.holo_red_dark))  // Couleur du pilier
            val layoutParams = FrameLayout.LayoutParams(pillarWidth, gameLayout.height - randomY - holeHeight)
            layoutParams.leftMargin = gameLayout.width  // Positionner à droite de l'écran
            this.layoutParams = layoutParams
            // Placer le pilier inférieur juste après le trou
            x = gameLayout.width.toFloat()
            y = randomY + holeHeight.toFloat()
        }

        // Ajouter les piliers au layout
        gameLayout.addView(topPillar)
        gameLayout.addView(bottomPillar)

        pillars.add(topPillar)
        pillars.add(bottomPillar)

        // Log pour vérifier les coordonnées et dimensions
        Log.d("Pillars", "Top Pillar Y: ${topPillar.y}, Bottom Pillar Y: ${bottomPillar.y}, HoleHeight: $holeHeight")

        movePillars(topPillar, bottomPillar)

        // Créer d'autres piliers
        handler.postDelayed({ createPillars() }, 2000)  // Créer d'autres piliers à intervalles de 2 secondes
    }

    private fun movePillars(topPillar: View, bottomPillar: View) {
        handler.postDelayed(object : Runnable {
            override fun run() {
                topPillar.x -= 5f  // Déplacer le pilier vers la gauche
                bottomPillar.x -= 5f  // Déplacer le pilier vers la gauche

                // Log pour vérifier la position des piliers pendant le déplacement
                Log.d("Pillars", "Top Pillar X: ${topPillar.x}, Bottom Pillar X: ${bottomPillar.x}")

                // Si le pilier sort de l'écran, le supprimer
                if (topPillar.x + topPillar.width < 0) {
                    gameLayout.removeView(topPillar)
                    gameLayout.removeView(bottomPillar)
                    pillars.remove(topPillar)
                    pillars.remove(bottomPillar)
                } else {
                    handler.postDelayed(this, 16)
                }
            }
        }, 16)
    }

    private fun checkCollisions() {
        // 1. Vérification des collisions avec les bords de l'écran (haut et bas)
        if (character.y <= 0 || character.y + character.height >= gameLayout.height) {
            // Si le personnage touche le haut ou le bas de l'écran, game over
            gameOver()
        }

        // 2. Vérification des collisions avec les piliers
        for (pillar in pillars) {
            // Vérifier les bords du pilier (gauche, droite, haut, bas)
            val pillarRect = Rect(pillar.left, pillar.top, pillar.right, pillar.bottom)
            val characterRect = Rect(character.left, character.top, character.right, character.bottom)

            // Si le personnage touche un bord du pilier, game over
            if (Rect.intersects(pillarRect, characterRect)) {
                // Si collision, game over
                gameOver()
                break
            }
        }
    }

    private fun gameOver() {
        // Empêche exécution multiple
        if (!isFinishing) {
            // 1. Stop everything immediately
            handler.removeCallbacksAndMessages(null)
            isRecording = false
            velocityY = 0f
            jumpForce = 0f
            audioRecord?.stop()
            audioRecord?.release()
            audioRecord = null

            // 2. Lancer GameOverActivity immédiatement sur le thread principal
            runOnUiThread {
                val intent = Intent(this, GameOverActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
        handler.removeCallbacksAndMessages(null)
    }
}
