package com.plcoding.weightpickercompose

import android.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CarouselStyle(
    val scaleWidth: Dp = 70.dp,
    val radius: Dp = 300.dp,
    val textColor: Int = Color.BLACK,
    val chosenTextColor: Int = Color.WHITE,
    val textSize: TextUnit = 18.sp,
    val choseTextSize: TextUnit = 22.sp,
    val step: Int = 14,
)
