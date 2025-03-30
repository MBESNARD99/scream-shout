package com.example.screamshout

import android.app.Activity
import android.content.Intent
import android.media.AudioRecord
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView

object GameManager {

    var score: Int = 0
    private val countedPillarPairs = mutableSetOf<View>()

    fun handleGameOver(
        activity: Activity,
        handler: Handler,
        audioRecord: AudioRecord?,
        character: ImageView,
        pillars: MutableList<View>,
        gameLayout: FrameLayout,
        onCleanup: () -> Unit = {}
    ) {
        handler.removeCallbacksAndMessages(null)

        audioRecord?.stop()
        audioRecord?.release()

        onCleanup()

        activity.runOnUiThread {
            val intent = Intent(activity, GameOverActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }
    }

    fun updateScore(character: ImageView, pillars: List<View>) {
        for (i in 0 until pillars.size step 2) {
            val pillar = pillars[i]
            if (!countedPillarPairs.contains(pillar) && pillar.x + pillar.width < character.x) {
                score++
                countedPillarPairs.add(pillar)
            }
        }
    }

    fun resetScore() {
        score = 0
        countedPillarPairs.clear()
    }
}
