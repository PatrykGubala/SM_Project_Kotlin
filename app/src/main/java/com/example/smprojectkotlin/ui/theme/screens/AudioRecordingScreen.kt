package com.example.smprojectkotlin.ui.theme.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smprojectkotlin.R
import com.example.smprojectkotlin.ui.theme.components.AudioRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecordingScreen(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    navController: NavController,
) {
    var isRecording by remember { mutableStateOf(true) }
    var isPaused by remember { mutableStateOf(false) }
    var recordedTime by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }
    var recordingPath by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        audioRecorder.startRecording()
    }

    LaunchedEffect(isRecording) {
        coroutineScope.launch(Dispatchers.Default) {
            while (isRecording && !isPaused) {
                delay(100L)
                recordedTime += 100
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            if (isRecording) {
                audioRecorder.stopRecording()
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Recording") },
            navigationIcon = {
                TextButton(onClick = {
                    recordingPath?.let { File(it).delete() }
                    if (isRecording) {
                        audioRecorder.stopRecording()
                    }
                    Log.d("AudioRecordingScreen", "Recording cancelled and file deleted: $recordingPath")
                    onCancel()
                }) {
                    Text(
                        text = "Cancel",
                        color = Color.Red,
                    )
                }
            },
            actions = {
                TextButton(onClick = {
                    if (isRecording || isPaused) {
                        isRecording = false
                        isPaused = false
                        audioRecorder.stopRecording()
                        recordingPath = audioRecorder.getOutputFilePath()
                        Log.d("AudioRecordingScreen", "Recording saved at: $recordingPath")
                    }
                    onSave()
                    navController.navigate("recordingScreen")
                }) {
                    Text(
                        text = "Save",
                        color = Color.Blue,
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            FloatingActionButton(
                onClick = {
                    if (isRecording && !isPaused) {
                        isPaused = true
                        isRecording = false
                        audioRecorder.pauseRecording()
                        recordingPath = audioRecorder.getOutputFilePath()
                        Log.d("AudioRecordingScreen", "Recording paused at: $recordingPath")
                    } else if (!isRecording && isPaused) {
                        audioRecorder.stopRecording()
                        audioRecorder.deleteRecording()
                        isPaused = false
                        recordingPath = null
                        Log.d("AudioRecordingScreen", "Recording deleted")
                    } else {
                        isRecording = true
                        isPaused = false
                        audioRecorder.startRecording()
                        Log.d("AudioRecordingScreen", "Recording started at: $recordingPath")
                    }
                },
                modifier =
                    Modifier
                        .size(80.dp)
                        .padding(4.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onBackground,
            ) {
                Icon(
                    painter =
                        painterResource(
                            id =
                                when {
                                    isPaused -> R.drawable.x_circle
                                    isRecording -> R.drawable.pause
                                    else -> R.drawable.play
                                },
                        ),
                    contentDescription =
                        when {
                            isPaused -> "Delete"
                            isRecording -> "Pause"
                            else -> "Play"
                        },
                    modifier = Modifier.size(64.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = String.format("%02d:%02d,%02d", recordedTime / 1000 / 60, (recordedTime / 1000) % 60, (recordedTime % 1000) / 10),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}
