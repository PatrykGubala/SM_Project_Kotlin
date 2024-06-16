package com.example.smprojectkotlin.ui.theme.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun Waveform(
    modifier: Modifier = Modifier,
    amplitudes: List<Int>,
    currentTime: Int,
    timelineStartIndex: Int,
) {
    val density = LocalDensity.current
    val lineSpacing = with(density) { 2.dp.toPx() }
    val lineWidth = with(density) { 1.dp.toPx() }

    Canvas(modifier = modifier) {
        val canvasWidth = min(size.width, with(density) { 400.dp.toPx() })
        val canvasHeight = size.height
        val numSegments = (canvasWidth / (lineWidth + lineSpacing)).toInt()

        val centerX = size.width / 2

        for (i in 0 until numSegments) {
            val amplitudeIndex = i + timelineStartIndex
            val amplitude = if (amplitudeIndex < amplitudes.size) amplitudes[amplitudeIndex] else 0
            val lineHeight = (amplitude / 100f) * canvasHeight
            val x = centerX + (i - numSegments / 2) * (lineWidth + lineSpacing)

            drawLine(
                color = Color.Black,
                start = androidx.compose.ui.geometry.Offset(x, (canvasHeight - lineHeight) / 2),
                end = androidx.compose.ui.geometry.Offset(x, (canvasHeight + lineHeight) / 2),
                strokeWidth = lineWidth,
                cap = StrokeCap.Round,
            )
        }

        val blueLineX = ((currentTime % (numSegments * 200)) / 200f) * (lineWidth + lineSpacing)
        drawLine(
            color = Color.Blue,
            start = androidx.compose.ui.geometry.Offset(blueLineX, 0f),
            end = androidx.compose.ui.geometry.Offset(blueLineX, canvasHeight),
            strokeWidth = 2.dp.toPx(),
        )
    }
}
