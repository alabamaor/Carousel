package com.plcoding.weightpickercompose

import android.graphics.Color


data class CarouselItem(
    var selectedTextTop: String = "Top",
    var selectedTextBottom: String = "Bottom",
    var unSelectedText: String = "LongWord",
    var icon: Int = R.drawable.ic_4,
    var color: Int = Color.CYAN,
)