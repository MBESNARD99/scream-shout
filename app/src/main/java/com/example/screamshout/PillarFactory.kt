package com.example.screamshout

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat

object PillarFactory {

    fun createPillars(
        context: Context,
        gameLayout: FrameLayout,
        layoutHeight: Int,
        layoutWidth: Int,
        pillars: MutableList<View>,
        onPillarsCreated: () -> Unit
    ) {
        val pillarWidth = 100
        val holeHeight = 700
        val randomY = (layoutHeight - holeHeight).let { (0..it).random() }

        val topPillar = View(context).apply {
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            layoutParams = FrameLayout.LayoutParams(pillarWidth, randomY).apply {
                leftMargin = layoutWidth
            }
            x = layoutWidth.toFloat()
            y = 0f
        }

        val bottomPillar = View(context).apply {
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_dark))
            layoutParams = FrameLayout.LayoutParams(pillarWidth, layoutHeight - randomY - holeHeight).apply {
                leftMargin = layoutWidth
            }
            x = layoutWidth.toFloat()
            y = randomY + holeHeight.toFloat()
        }

        gameLayout.addView(topPillar)
        gameLayout.addView(bottomPillar)

        pillars.add(topPillar)
        pillars.add(bottomPillar)

        onPillarsCreated()
    }
}
