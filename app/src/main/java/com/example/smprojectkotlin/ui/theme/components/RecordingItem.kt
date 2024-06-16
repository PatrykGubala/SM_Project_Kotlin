package com.example.smprojectkotlin.ui.theme.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.smprojectkotlin.R
import com.example.smprojectkotlin.model.Recording
import com.example.smprojectkotlin.ui.theme.ThemeStyles
import com.example.smprojectkotlin.ui.theme.White100

@Composable
fun RecordingItem(
    recording: Recording,
    onPlayClick: () -> Unit,
) {
    val cardColors = CardDefaults.cardColors(containerColor = White100)
    val cardElevation = CardDefaults.cardElevation()
    val paddingHorizontal = 16.dp
    val paddingVertical = 8.dp
    val innerPadding = 4.dp
    val innerVerticalPadding = 12.dp
    val bottomPadding = 8.dp
    val textPaddingStart = 12.dp

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = paddingHorizontal)
                .padding(vertical = paddingVertical),
        elevation = cardElevation,
        colors = cardColors,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = innerPadding, vertical = innerVerticalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recording.title,
                    style = ThemeStyles.titleStyle,
                    modifier = Modifier.padding(bottom = bottomPadding, start = textPaddingStart),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = recording.date,
                        style = ThemeStyles.dateStyle,
                        modifier = Modifier.padding(end = bottomPadding, start = textPaddingStart),
                    )
                    Text(
                        text = recording.fileSize,
                        style = ThemeStyles.dateStyle,
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = recording.duration, style = ThemeStyles.durationStyle)
                IconButton(onClick = onPlayClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.play_circle),
                        contentDescription = "Play",
                        tint = Color.Black,
                    )
                }
            }
        }
    }
}
