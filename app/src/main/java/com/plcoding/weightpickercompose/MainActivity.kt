package com.plcoding.weightpickercompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.graphics.Color
import android.util.Log
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlin.random.Random

class MainActivity : ComponentActivity() {
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
                },
                CarouselItem().apply {
                    icon = R.drawable.ic_2
                    color = Color.YELLOW
                    unSelectedText = "חניונים"
                },
                CarouselItem().apply {
                    icon = R.drawable.ic_3
                    color = Color.GREEN
                    unSelectedText = "חנייה"
                },
                CarouselItem().apply {
                    icon = R.drawable.ic_4
                    color = Color.MAGENTA
                    unSelectedText = "ביטוח"
                },
                CarouselItem().apply {
                    icon = R.drawable.ic_5
                    color = Color.BLACK
                    unSelectedText = "תחבורה"
                },
                CarouselItem().apply {
                    icon = R.drawable.ic_6
                    color = Color.GRAY
                    unSelectedText = "חילוץ"
                },
                CarouselItem().apply {
                    icon = R.drawable.ic_7
                    color = Color.LTGRAY
                    unSelectedText = "שטיפומט"
                }
            )

            var rnd = Random.nextInt(0, items.size)

            Log.i("alabama", "screenWidth: $screenWidth")
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
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

