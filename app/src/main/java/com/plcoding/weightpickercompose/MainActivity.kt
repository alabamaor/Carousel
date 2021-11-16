package com.plcoding.weightpickercompose

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class MainActivity : ComponentActivity() {

    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val configuration = LocalConfiguration.current

            val screenHeight = configuration.screenHeightDp.dp
            val screenWidth = configuration.screenWidthDp.dp

            var weight by remember {
                mutableStateOf(80)
            }
            var items: Array<CarouselItem> = arrayOf(
                CarouselItem().apply {
                    icon = R.drawable.ic_1
                    color = Color.BLUE
                    unSelectedText = "תדלוק"
                    selectedTextBottom= "תדלוק"
                },
                CarouselItem().apply {
                    icon = R.drawable.ic_2
                    color = Color.YELLOW
                    unSelectedText = "חניונים"
                    selectedTextBottom = "חניונים"
                },
                CarouselItem().apply {
                    icon = R.drawable.ic_3
                    color = Color.GREEN
                    unSelectedText = "חנייה"
                    selectedTextBottom = "חנייה"
                },
                CarouselItem().apply {
                    icon = R.drawable.ic_4
                    color = Color.MAGENTA
                    unSelectedText = "ביטוח"
                    selectedTextBottom = "ביטוח"
                },
                CarouselItem().apply {
                    icon = R.drawable.ic_5
                    color = Color.BLACK
                    unSelectedText = "תחבורה"
                    selectedTextBottom = "תחבורה"
                },
                CarouselItem().apply {
                    icon = R.drawable.ic_6
                    color = Color.GRAY
                    unSelectedText = "חילוץ"
                    selectedTextBottom = "חילוץ"
                },
                CarouselItem().apply {
                    icon = R.drawable.ic_7
                    color = Color.LTGRAY
                    unSelectedText = "שטיפומט"
                    selectedTextBottom = "שטיפומט"
                }
            )

            var rnd = Random.nextInt(0, items.size)

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
//                SetupAnimationLayout()
                Carousel(
                    items = items,
//                    initialStep = rnd,
                    context = applicationContext,
                    style = CarouselStyle(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.3f)
                        .align(Alignment.BottomCenter)
                ) {
                    weight = it
                }
            }
        }
    }
}

