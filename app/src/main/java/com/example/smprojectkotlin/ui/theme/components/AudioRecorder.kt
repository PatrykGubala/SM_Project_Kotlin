package com.example.smprojectkotlin.ui.theme.components

import android.content.Context
import android.media.MediaRecorder
import android.os.Environment
import android.util.Log
import java.io.File

class AudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var outputFilePath: String = ""
    var isPaused: Boolean = false
    var isRecording: Boolean = false

    fun startRecording() {
        val outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
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
        isRecording = true
        isPaused = false
        Log.d("AudioRecorder", "Recording started: $outputFilePath")
    }

    fun pauseRecording() {
        if (isRecording && !isPaused) {
            mediaRecorder?.pause()
            isPaused = true
            Log.d("AudioRecorder", "Recording paused: $outputFilePath")
        }
    }

    fun stopRecording() {
        if (isRecording) {
            mediaRecorder?.apply {
                stop()
                release()
            }
            isRecording = false
            isPaused = false
            Log.d("AudioRecorder", "Recording stopped: $outputFilePath")
        }
        mediaRecorder = null
    }

    fun deleteRecording() {
        val file = File(outputFilePath)
        if (file.exists()) {
            file.delete()
            Log.d("AudioRecorder", "Recording deleted: $outputFilePath")
        }
    }

    fun getOutputFilePath(): String {
        return outputFilePath
    }

    fun getAmplitude(): Int {
        return mediaRecorder?.maxAmplitude ?: 0
    }
}
