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
import androidx.compose.ui.unit.sp
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
    onSave: (String) -> Unit,
    navController: NavController,
) {
    var isRecording by remember { mutableStateOf(true) }
    var isPaused by remember { mutableStateOf(false) }
    var recordedTime by remember { mutableStateOf(0) }
    val context = LocalContext.current
    val audioRecorder = remember { AudioRecorder(context) }
    var recordingPath by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var showDialog by remember { mutableStateOf(false) }
    var recordingTitle by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        audioRecorder.startRecording()
    }

    LaunchedEffect(isRecording) {
        coroutineScope.launch(Dispatchers.Default) {
            while (isRecording && !isPaused) {
                delay(20L)
                recordedTime += 20
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
                        fontSize = 18.sp,
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
                    showDialog = true
                }) {
                    Text(
                        text = "Save",
                        color = Color.Blue,
                        fontSize = 18.sp,
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
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = String.format("%02d:%02d,%02d", recordedTime / 1000 / 60, (recordedTime / 1000) % 60, (recordedTime % 1000) / 10),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 48.sp),
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

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
                        recordedTime = 0
                        Log.d("AudioRecordingScreen", "Recording deleted and reset")
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
                contentColor = MaterialTheme.colorScheme.background,
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

        if (showDialog) {
            AlertDialog(
                onDismissRequest = {
                    showDialog = false
                },
                title = {
                    Text(
                        text = "Save Recording",
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Enter a title for your recording:",
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = recordingTitle,
                            onValueChange = { recordingTitle = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors =
                                TextFieldDefaults.colors(
                                    focusedTextColor = MaterialTheme.colorScheme.background,
                                    unfocusedTextColor = MaterialTheme.colorScheme.background,
                                    cursorColor = MaterialTheme.colorScheme.primary,
                                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                ),
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            recordingPath?.let {
                                val newFilePath = audioRecorder.renameRecording(recordingTitle)
                                onSave(newFilePath)
                                navController.navigate("recordingScreen")
                            }
                        },
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false
                        },
                    ) {
                        Text("Cancel")
                    }
                },
            )
        }
    }
}
