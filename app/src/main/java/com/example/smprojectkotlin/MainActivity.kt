package com.example.smprojectkotlin

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smprojectkotlin.model.Recording
import com.example.smprojectkotlin.ui.theme.SMProjectKotlinTheme
import com.example.smprojectkotlin.ui.theme.screens.AudioRecordingScreen
import com.example.smprojectkotlin.ui.theme.screens.RecordingScreen
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private val requiredPermissions =
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.ACCESS_MEDIA_LOCATION,
        )

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                Log.d("Permissions", "All requested permissions granted")
            } else {
                Log.d("Permissions", "Some permissions denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkAndRequestPermissions()

        setContent {
            SMProjectKotlinTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainContent()
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (!hasAllPermissions()) {
            requestPermissionsLauncher.launch(requiredPermissions)
        } else {
            Log.d("Permissions", "All permissions already granted")
        }
    }

    private fun hasAllPermissions() =
        requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

    @Composable
    fun MainContent() {
        val navController = rememberNavController()
        val recordings = remember { mutableStateOf(listOf<Recording>()) }

        LaunchedEffect(key1 = true) {
            recordings.value = loadRecordingsFromStorage()
        }

        NavHost(navController = navController, startDestination = "recordingScreen") {
            composable("recordingScreen") {
                RecordingScreen(recordings = recordings.value, onStartRecording = { navController.navigate("audioRecordingScreen") })
            }
            composable("audioRecordingScreen") {
                AudioRecordingScreen(onCancel = { navController.popBackStack() }, onSave = { navController.popBackStack() })
            }
        }
    }

    private fun loadRecordingsFromStorage(): List<Recording> {
        val musicRoot = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
        val recordings = mutableListOf<Recording>()
        searchFilesRecursively(musicRoot, recordings)
        return recordings
    }

    private fun searchFilesRecursively(
        directory: File,
        recordings: MutableList<Recording>,
    ) {
        Log.d("FileSearch", "Searching in directory: ${directory.absolutePath}")
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                Log.d("FileSearch", "Entering directory: ${file.absolutePath}")
                searchFilesRecursively(file, recordings)
            } else if (file.isFile && file.name.endsWith(".mp3")) {
                Log.d("FileSearch", "MP3 File found: ${file.absolutePath}, Size: ${file.length()} bytes")
                val duration = getDurationInMinutesAndSeconds(file)
                val recording =
                    Recording(
                        id = file.nameWithoutExtension,
                        title = file.nameWithoutExtension,
                        date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(java.util.Date(file.lastModified())),
                        duration = duration,
                        fileSize = convertFileSizeToReadableFormat(file.length()),
                    )
                recordings.add(recording)
            } else {
                Log.d("FileSearch", "Skipped: ${file.absolutePath}")
            }
        }
    }

    private fun getDurationInMinutesAndSeconds(file: File): String {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(file.absolutePath)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val durationMs = durationStr?.toLongOrNull() ?: 0L
        retriever.release()
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun convertFileSizeToReadableFormat(sizeInBytes: Long): String {
        val kiloBytes = sizeInBytes / 1024
        val megaBytes = kiloBytes / 1024
        return if (megaBytes > 0) {
            "$megaBytes MB"
        } else if (kiloBytes > 0) {
            "$kiloBytes KB"
        } else {
            "$sizeInBytes Bytes"
        }
    }
}
