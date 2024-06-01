package com.example.smprojectkotlin.ui.theme.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
    val cardColors =
        CardDefaults.cardColors(
            containerColor = White100,
        )
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(),
        colors = cardColors,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = recording.title,
                    style = ThemeStyles.titleStyle,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = recording.date,
                        style = ThemeStyles.dateStyle,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text(
                        text = recording.fileSize,
                        style = ThemeStyles.dateStyle,
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
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
