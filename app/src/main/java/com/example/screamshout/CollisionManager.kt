package com.example.screamshout

import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

object CollisionManager {

    fun checkCharacterCollision(
        character: ImageView,
        pillars: List<View>,
        layoutHeight: Int,
        onCollision: () -> Unit
    ) {
        val characterRect = Rect()
        character.getHitRect(characterRect)

        if (characterRect.top <= 0 || characterRect.bottom >= layoutHeight) {
            onCollision()
            return
        }

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
