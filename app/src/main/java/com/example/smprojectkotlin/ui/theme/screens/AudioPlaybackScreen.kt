package com.example.smprojectkotlin.ui.theme.screens

import android.media.MediaPlayer
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smprojectkotlin.R
import com.example.smprojectkotlin.model.Recording
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun AudioPlaybackScreen(
    navController: NavController,
    recording: Recording,
) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0) }
    val mediaPlayer = remember { MediaPlayer() }
    var totalDuration by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            mediaPlayer.start()
            scope.launch {
                while (mediaPlayer.isPlaying) {
                    currentPosition = mediaPlayer.currentPosition
                    delay(5L)
                }
                if (!mediaPlayer.isPlaying) {
                    isPlaying = false
                    if (currentPosition >= totalDuration) {
                        currentPosition = totalDuration
                    }
                }
            }
        } else {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }
    }

    LaunchedEffect(recording) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(File(recording.filePath).absolutePath)
        mediaPlayer.prepare()
        totalDuration = mediaPlayer.duration
        currentPosition = 0
    }

    val currentMinutes = (currentPosition / 1000) / 60
    val currentSeconds = (currentPosition / 1000) % 60
    val totalMinutes = (totalDuration / 1000) / 60
    val totalSeconds = (totalDuration / 1000) % 60

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Playing: ${recording.title}")

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier =
                Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Slider(
                value = currentPosition.toFloat() / 1000,
                onValueChange = {
                    mediaPlayer.seekTo((it * 1000).toInt())
                    currentPosition = (it * 1000).toInt()
                },
                valueRange = 0f..(totalDuration / 1000).toFloat(),
                modifier = Modifier.fillMaxWidth(),
                colors =
                    SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.24f),
                    ),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = String.format("%02d:%02d", currentMinutes, currentSeconds),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = String.format("%02d:%02d", totalMinutes, totalSeconds),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FloatingActionButton(
                onClick = {
                    if (isPlaying) {
                        mediaPlayer.pause()
                    } else {
                        mediaPlayer.start()
                    }
                    isPlaying = !isPlaying
                },
                modifier =
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(80.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
            ) {
                Icon(
                    painter = painterResource(id = if (isPlaying) R.drawable.pause else R.drawable.play),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(64.dp),
                )
            }
        }
    }
}
