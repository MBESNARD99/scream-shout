package com.example.screamshout

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

/*

Crée deux piliers (haut et bas) avec un trou entre les deux,
les ajoute au layout du jeu et à la liste des piliers.

 */

object PillarFactory {

    fun createPillars(
        context: Context,
        gameLayout: FrameLayout,
        layoutHeight: Int,
        layoutWidth: Int,
        pillars: MutableList<View>,
        onPillarsCreated: () -> Unit
    ) {
        val pillarWidth = 100 // Largeur fixe des piliers
        val holeHeight = 700 // Taille de l'ouverture entre les deux piliers
        val randomY = (layoutHeight - holeHeight).let { (0..it).random() } // Position Y aléatoire du haut du trou

        // Création du pilier haut
        val topPillar = ImageView(context).apply {
            setImageResource(R.drawable.pillar_top)
            scaleType = ImageView.ScaleType.FIT_XY // Étirer la taille selon l'écran
            layoutParams = FrameLayout.LayoutParams(pillarWidth, randomY).apply {
                leftMargin = layoutWidth // Position horizontale (à droite de l'écran)
            }
            x = layoutWidth.toFloat()
            y = 0f
        }

        // Création du pilier bas
        val bottomPillar = ImageView(context).apply {
            setImageResource(R.drawable.pillar_bottom)
            scaleType = ImageView.ScaleType.FIT_XY
            layoutParams = FrameLayout.LayoutParams(
                pillarWidth,
                layoutHeight - randomY - holeHeight // Hauteur restante sous le trou
            ).apply {
                leftMargin = layoutWidth
            }
            x = layoutWidth.toFloat()
            y = randomY + holeHeight.toFloat() // Position verticale après le trou
        }

        gameLayout.addView(topPillar)
        gameLayout.addView(bottomPillar)

        pillars.add(topPillar)
        pillars.add(bottomPillar)

        onPillarsCreated()
    }
}
