package com.example.smprojectkotlin.ui.theme.screens

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smprojectkotlin.R
import com.example.smprojectkotlin.model.Recording
import com.linc.audiowaveform.AudioWaveform
import com.linc.audiowaveform.model.AmplitudeType
import com.linc.audiowaveform.model.WaveformAlignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import linc.com.amplituda.Amplituda
import linc.com.amplituda.exceptions.io.AmplitudaIOException
import java.io.File

suspend fun processAudio(
    context: Context,
    filePath: String,
): List<Int>? {
    return withContext(Dispatchers.IO) {
        try {
            val amplituda = Amplituda(context)
            val result = amplituda.processAudio(filePath).get()
            val amplitudes = result.amplitudesAsList()
            Log.d("AudioProcessing", "Successfully processed audio, amplitudes: $amplitudes")
            amplitudes
        } catch (e: AmplitudaIOException) {
            e.printStackTrace()
            Log.e("AudioProcessing", "Error processing audio: ${e.message}")
            null
        }
    }
}

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
    val amplitudes = remember { mutableStateListOf<Int>() }
    var waveformProgress by remember { mutableStateOf(0F) }
    val context = LocalContext.current

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
                    waveformProgress = currentPosition.toFloat() / totalDuration
                    delay(100L)
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

        scope.launch {
            Log.d("AudioPlaybackScreen", "Processing audio for file: ${recording.filePath}")
            val processedAmplitudes = processAudio(context, recording.filePath)
            if (processedAmplitudes != null) {
                amplitudes.clear()
                amplitudes.addAll(processedAmplitudes)
                Log.d("AudioPlaybackScreen", "Loaded amplitudes: $amplitudes")
            } else {
                Log.e("AudioPlaybackScreen", "Failed to load amplitudes")
            }
        }
    }

    val currentMinutes = (currentPosition / 1000) / 60
    val currentSeconds = (currentPosition / 1000) % 60
    val totalMinutes = (totalDuration / 1000) / 60
    val totalSeconds = (totalDuration / 1000) % 60

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 0.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Playing: ${recording.title}",
        )
        Spacer(modifier = Modifier.height(64.dp))

        AudioWaveform(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(300.dp),
            waveformAlignment = WaveformAlignment.Center,
            amplitudeType = AmplitudeType.Avg,
            progressBrush = SolidColor(Color.Magenta),
            waveformBrush = SolidColor(Color.LightGray),
            spikeWidth = 4.dp,
            spikePadding = 2.dp,
            spikeRadius = 4.dp,
            progress = waveformProgress,
            amplitudes = amplitudes.toList(), // Make sure to pass the list of amplitudes
            onProgressChange = { newProgress ->
                Log.d("AudioWaveform", "Progress changed: $newProgress")
                waveformProgress = newProgress
                mediaPlayer.seekTo((newProgress * totalDuration).toInt())
            },
            onProgressChangeFinished = {},
        )
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

            Spacer(modifier = Modifier.height(0.dp))

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

            Spacer(modifier = Modifier.height(8.dp))

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
                        .size(80.dp)
                        .padding(4.dp),
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
