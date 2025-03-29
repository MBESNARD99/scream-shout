package com.example.screamshout

import android.graphics.Color
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import kotlin.random.Random

class Pillar(private val gameLayout: FrameLayout, private val character: ImageView) {

    private val width = 200  // Largeur du pilier
    private val holeHeightMin = 200 // Hauteur minimale du trou
    private val holeHeightMax = 600 // Hauteur maximale du trou
    var xPosition = gameLayout.width.toFloat()
    var holeYPosition = Random.nextInt(holeHeightMin, holeHeightMax)

    private val topPillar: View = View(gameLayout.context).apply {
        setBackgroundColor(Color.GREEN)
        layoutParams = FrameLayout.LayoutParams(width, holeYPosition).apply {
            leftMargin = xPosition.toInt()
        }
    }

    private val bottomPillar: View = View(gameLayout.context).apply {
        setBackgroundColor(Color.GREEN)
        layoutParams = FrameLayout.LayoutParams(width, gameLayout.height - holeYPosition - holeHeightMax).apply {
            leftMargin = xPosition.toInt()
        }
    }

    init {
        // S'assurer que le layout est prêt avant d'ajouter les vues
        gameLayout.post {
            gameLayout.addView(topPillar)
            gameLayout.addView(bottomPillar)
            Log.d("Pillar", "Piliers ajoutés : position $xPosition, hauteur trou $holeYPosition")
        }
    }

    fun update() {
        // Déplacer les piliers vers la gauche
        xPosition -= 10f  // Vitesse des piliers qui se déplacent vers la gauche
        topPillar.x = xPosition
        bottomPillar.x = xPosition

        // Log pour vérifier la position des piliers
        Log.d("Pillar", "Mise à jour : xPosition = $xPosition")

        // Replacer les piliers une fois qu'ils ont quitté l'écran
        if (xPosition + width < 0) {
            xPosition = gameLayout.width.toFloat()
            holeYPosition = Random.nextInt(holeHeightMin, holeHeightMax)

            // Réajuster les hauteurs des piliers et les positions
            topPillar.layoutParams.height = holeYPosition
            bottomPillar.layoutParams.height = gameLayout.height - holeYPosition - holeHeightMax

            // Réassigner les layoutParams
            topPillar.layoutParams = topPillar.layoutParams
            bottomPillar.layoutParams = bottomPillar.layoutParams

            Log.d("Pillar", "Piliers réinitialisés : xPosition = $xPosition, hauteur du trou = $holeYPosition")
        }
    }

    fun checkCollision(): Boolean {
        val characterRect = Rect(
            character.left,
            character.top,
            character.right,
            character.bottom
        )

        val topPillarRect = Rect(
            topPillar.left.toInt(),
            topPillar.top.toInt(),
            topPillar.right.toInt(),
            topPillar.bottom.toInt()
        )

        val bottomPillarRect = Rect(
            bottomPillar.left.toInt(),
            bottomPillar.top.toInt(),
            bottomPillar.right.toInt(),
            bottomPillar.bottom.toInt()
        )

        return characterRect.intersect(topPillarRect) || characterRect.intersect(bottomPillarRect)
    }
}
