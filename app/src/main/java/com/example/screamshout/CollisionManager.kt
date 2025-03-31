package com.example.screamshout

import android.graphics.Rect
import android.view.View
import android.widget.ImageView

/*

Objet qui gère les collisions entre le personnage
et les piliers du jeu

 */

object CollisionManager {

    fun checkCharacterCollision(
        character: ImageView,
        pillars: List<View>,
        layoutHeight: Int,
        onCollision: () -> Unit
    ) {
        // Rectangle du personnage
        val characterRect = Rect()
        character.getHitRect(characterRect)

        // Collision avec les bords (haut/bas de l’écran)
        if (characterRect.top <= 0 || characterRect.bottom >= layoutHeight) {
            onCollision()
            return
        }

        // Vérification de collision avec les piliers
        for (pillar in pillars) {
            val pillarRect = Rect()
            pillar.getHitRect(pillarRect)

            if (Rect.intersects(characterRect, pillarRect)) {
                onCollision()
                return
            }
        }
    }
}
