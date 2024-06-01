package com.example.smprojectkotlin.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

object ThemeStyles {
    val titleStyle =
        TextStyle(
            color = Black100,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )

    val dateStyle =
        TextStyle(
            color = Grey80,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
        )

    val durationStyle =
        TextStyle(
            color = Black100,
            fontSize = 14.sp,
            fontWeight = FontWeight.Light,
        )

    val searchStyle =
        TextStyle(
            color = White100,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
        )
}
