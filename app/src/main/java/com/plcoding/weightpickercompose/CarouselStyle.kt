package com.plcoding.weightpickercompose

import android.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class CarouselStyle(
    val textColor: Int = Color.BLACK,
    val chosenTextColor: Int = Color.WHITE,
    val textSize: TextUnit = 18.sp,
    val chosenTextSize: TextUnit = 22.sp,
    val step: Int = 14,
)
