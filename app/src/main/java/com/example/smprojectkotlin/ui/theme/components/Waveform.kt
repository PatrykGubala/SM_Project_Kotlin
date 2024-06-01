package com.example.smprojectkotlin.ui.theme.components

import android.graphics.Typeface
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
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
    val lineSpacing = with(density) { 8.dp.toPx() }
    val lineWidth = with(density) { 2.dp.toPx() }

    Canvas(modifier = modifier) {
        val canvasWidth = min(size.width, with(density) { 400.dp.toPx() })
        val canvasHeight = size.height
        val numSegments = (canvasWidth / (lineWidth + lineSpacing)).toInt()

        for (i in 0 until numSegments) {
            val amplitudeIndex = i + timelineStartIndex
            val amplitude = if (amplitudeIndex < amplitudes.size) amplitudes[amplitudeIndex] else 0
            val lineHeight = (amplitude / 100f) * canvasHeight
            val x = i * (lineWidth + lineSpacing)

            drawLine(
                color = Color.Black,
                start = androidx.compose.ui.geometry.Offset(x, (canvasHeight - lineHeight) / 2),
                end = androidx.compose.ui.geometry.Offset(x, (canvasHeight + lineHeight) / 2),
                strokeWidth = lineWidth,
                cap = StrokeCap.Round,
            )
        }

        drawLine(
            color = Color.Gray,
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(0f, canvasHeight),
            strokeWidth = 2.dp.toPx(),
        )

        val blueLineX = ((currentTime % (numSegments * 200)) / 200f) * (lineWidth + lineSpacing)
        drawLine(
            color = Color.Blue,
            start = androidx.compose.ui.geometry.Offset(blueLineX, 0f),
            end = androidx.compose.ui.geometry.Offset(blueLineX, canvasHeight),
            strokeWidth = 2.dp.toPx(),
        )
    }
}

@Composable
fun Timeline(
    modifier: Modifier = Modifier,
    recordedTime: Int,
    timelineStartIndex: Int,
) {
    val density = LocalDensity.current
    val lineSpacing = with(density) { 8.dp.toPx() }
    val lineWidth = with(density) { 2.dp.toPx() }

    Canvas(modifier = modifier.height(50.dp)) {
        val canvasWidth = size.width
        val numSegments = (canvasWidth / (lineWidth + lineSpacing)).toInt()

        for (i in 0 until numSegments) {
            val x = i * (lineWidth + lineSpacing)
            val timeIndex = i + timelineStartIndex

            if (timeIndex % 5 == 0) {
                drawLine(
                    color = Color.Gray,
                    start = androidx.compose.ui.geometry.Offset(x, 0f),
                    end = androidx.compose.ui.geometry.Offset(x, 20.dp.toPx()),
                    strokeWidth = lineWidth,
                    cap = StrokeCap.Round,
                )
                drawContext.canvas.nativeCanvas.drawText(
                    String.format("%02d:%02d", timeIndex / 5 / 60, timeIndex / 5 % 60),
                    x,
                    30.dp.toPx(),
                    TextPaint().apply {
                        color = Color.Gray.toArgb()
                        textSize = 12.dp.toPx()
                        typeface = Typeface.DEFAULT_BOLD
                    },
                )
            } else if (timeIndex % 1 == 0) {
                drawLine(
                    color = Color.Gray,
                    start = androidx.compose.ui.geometry.Offset(x, 0f),
                    end = androidx.compose.ui.geometry.Offset(x, 10.dp.toPx()),
                    strokeWidth = lineWidth,
                    cap = StrokeCap.Round,
                )
            }
        }

        val timelineX = canvasWidth / 2
        drawLine(
            color = Color.Blue,
            start = androidx.compose.ui.geometry.Offset(timelineX, 0f),
            end = androidx.compose.ui.geometry.Offset(timelineX, size.height),
            strokeWidth = 2.dp.toPx(),
        )
    }
}
