package com.example.smprojectkotlin.ui.theme.screens

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.smprojectkotlin.R
import com.example.smprojectkotlin.ui.theme.components.AudioRecorder
import com.example.smprojectkotlin.ui.theme.components.Timeline
import com.example.smprojectkotlin.ui.theme.components.Waveform
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioRecordingScreen(
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var recordedTime by remember { mutableStateOf(0) } // in milliseconds
    var timelineStartIndex by remember { mutableStateOf(0) }
    var amplitudes by remember { mutableStateOf(listOf<Int>()) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    val audioRecorder = remember { AudioRecorder() }
    val coroutineScope = rememberCoroutineScope()

    val density = LocalDensity.current
    val lineSpacing = with(density) { 8.dp.toPx() }
    val lineWidth = with(density) { 2.dp.toPx() }
    val maxSegments = with(density) { (400.dp.toPx() / (lineWidth + lineSpacing)).toInt() }

    LaunchedEffect(isRecording) {
        while (isRecording) {
            delay(100L)
            recordedTime += 100

            val amplitude = audioRecorder.getAmplitude()
            amplitudes =
                if (amplitudes.size < maxSegments) {
                    amplitudes + amplitude
                } else {
                    amplitudes.drop(1) + amplitude
                }

            if ((recordedTime / 200) >= maxSegments / 2) {
                timelineStartIndex = (recordedTime / 200) - maxSegments / 2
            }
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            mediaPlayer?.start()
        } else {
            mediaPlayer?.pause()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Recording") },
            navigationIcon = {
                TextButton(onClick = onCancel) {
                    Text(
                        text = "Cancel",
                        color = Color.Red,
                    )
                }
            },
            actions = {
                TextButton(onClick = onSave) {
                    Text(
                        text = "Save",
                        color = Color.Blue,
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )

        // Timeline visualization
        Timeline(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            recordedTime = recordedTime,
            timelineStartIndex = timelineStartIndex,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Waveform(
                modifier = Modifier.size(400.dp),
                amplitudes = amplitudes,
                currentTime = recordedTime,
                timelineStartIndex = timelineStartIndex,
            )
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            IconButton(onClick = {
                if (isRecording) {
                    isRecording = false
                    audioRecorder.stopRecording()
                    mediaPlayer =
                        MediaPlayer().apply {
                            setDataSource(audioRecorder.getOutputFilePath())
                            prepare()
                        }
                } else {
                    isRecording = true
                    audioRecorder.startRecording()
                }
            }) {
                Icon(
                    painter =
                        painterResource(
                            id = if (isRecording) R.drawable.pause else R.drawable.play,
                        ),
                    contentDescription = if (isRecording) "Stop Recording" else "Start Recording",
                    modifier = Modifier.size(48.dp),
                )
            }

            if (!isRecording) {
                Spacer(modifier = Modifier.width(16.dp))

                IconButton(onClick = {
                    isPlaying = !isPlaying
                }) {
                    Icon(
                        painter =
                            painterResource(
                                id = if (isPlaying) R.drawable.pause else R.drawable.play,
                            ),
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        modifier = Modifier.size(48.dp),
                    )
                }
            }
        }
    }
}

fun generateRandomAmplitudes(size: Int): List<Int> {
    return List(size) { Random.nextInt(10, 100) }
}

fun updateAmplitudes(currentAmplitudes: List<Int>): List<Int> {
    return currentAmplitudes.drop(1) + Random.nextInt(10, 100)
}
