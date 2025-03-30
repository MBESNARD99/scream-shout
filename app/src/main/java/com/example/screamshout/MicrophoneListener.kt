package com.example.screamshout

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Handler
import androidx.annotation.RequiresPermission
import kotlin.math.max

class MicrophoneListener(
    private val bufferSize: Int = AudioRecord.getMinBufferSize(
        44100,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
    ),
    private val onVolumeDetected: (Float) -> Unit
) {

    private var audioRecord: AudioRecord? = null
    private var isRecording: Boolean = false

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun start() {
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            44100,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )

        audioRecord?.startRecording()
        isRecording = true

        val audioBuffer = ShortArray(bufferSize)
        Thread {
            while (isRecording) {
                val readSize = audioRecord?.read(audioBuffer, 0, bufferSize) ?: 0
                val maxAmplitude = audioBuffer.take(readSize).maxOrNull() ?: 0
                val volumeLevel = maxAmplitude / 32768f
                onVolumeDetected(volumeLevel)
                Thread.sleep(100)
            }
        }.start()
    }

    fun stop() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
}
