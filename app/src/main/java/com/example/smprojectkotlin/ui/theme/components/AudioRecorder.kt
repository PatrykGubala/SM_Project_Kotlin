package com.example.smprojectkotlin.ui.theme.components

import android.media.MediaRecorder
import android.os.Environment

class AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFilePath: String = ""

    fun startRecording() {
        val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        outputFilePath = "${outputDir.absolutePath}/recording_${System.currentTimeMillis()}.mp3"
        mediaRecorder =
            MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFilePath)
                prepare()
                start()
            }
    }

    fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    fun getOutputFilePath(): String {
        return outputFilePath
    }

    fun getAmplitude(): Int {
        return mediaRecorder?.maxAmplitude ?: 0
    }
}
