package com.example.screamshout

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SoundVisualizerView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var volumeLevel: Float = 0f
    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 10f
    }

    fun updateLevel(level: Float) {
        volumeLevel = level.coerceIn(0f, 1f)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val heightOffset = height - (volumeLevel * height) // Plus on crie, plus ça monte
        canvas.drawLine(width / 2f, height.toFloat(), width / 2f, heightOffset, paint)
    }
}