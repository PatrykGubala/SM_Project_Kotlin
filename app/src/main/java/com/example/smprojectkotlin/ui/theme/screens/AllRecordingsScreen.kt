package com.example.smprojectkotlin.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.smprojectkotlin.R
import com.example.smprojectkotlin.model.Recording
import com.example.smprojectkotlin.ui.theme.Grey40
import com.example.smprojectkotlin.ui.theme.ThemeStyles.searchStyle
import com.example.smprojectkotlin.ui.theme.components.RecordingItem

@Composable
fun AllRecordingsScreen(
    recordings: List<Recording>,
    onStartRecording: () -> Unit,
    onPlayRecording: (Recording) -> Unit,
) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }

    val filteredRecordings by rememberUpdatedState(
        recordings.filter {
            it.title.contains(searchText.text, ignoreCase = true)
        },
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = searchText,
                singleLine = true,
                onValueChange = { searchText = it },
                modifier =
                    Modifier
                        .weight(1f)
                        .background(Grey40, RoundedCornerShape(45.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                decorationBox = { innerTextField ->
                    if (searchText.text.isEmpty()) {
                        Text(
                            "Search",
                            style = searchStyle,
                        )
                    }
                    innerTextField()
                },
            )
        }

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filteredRecordings) { recording ->
                RecordingItem(
                    recording,
                    onPlayClick = { onPlayRecording(recording) },
                )
            }
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            FloatingActionButton(
                onClick = onStartRecording,
                modifier =
                    Modifier
                        .size(80.dp)
                        .padding(4.dp),
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.play),
                    contentDescription = "Start Recording",
                    modifier = Modifier.size(64.dp),
                )
            }
        }
    }
}
