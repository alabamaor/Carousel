package com.plcoding.weightpickercompose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlin.random.Random
import kotlin.time.ExperimentalTime

var items: Array<CarouselItem> = arrayOf(
    CarouselItem().apply {
        icon = R.drawable.ic_1
        color = android.graphics.Color.argb(255, 246, 103, 119)
        unSelectedText = "תדלוק"
        selectedTextBottom = "תדלוק"
    },
    CarouselItem().apply {
        icon = R.drawable.ic_2
        color = android.graphics.Color.argb(255, 131, 188, 255)
        unSelectedText = "חניונים"
        selectedTextBottom = "חניונים"
    },
    CarouselItem().apply {
        icon = R.drawable.ic_3
        color = android.graphics.Color.argb(255, 48, 108, 234)
        unSelectedText = "חנייה"
        selectedTextBottom = "חנייה"
    },
    CarouselItem().apply {
        icon = R.drawable.ic_4
        color = android.graphics.Color.argb(255, 237, 184, 121)
        unSelectedText = "ביטוח"
        selectedTextBottom = "ביטוח"
    },
    CarouselItem().apply {
        icon = R.drawable.ic_5
        color = android.graphics.Color.argb(255, 73, 197, 181)
        unSelectedText = "תחבורה"
        selectedTextBottom = "תחבורה"
    },
    CarouselItem().apply {
        icon = R.drawable.ic_6
        color = android.graphics.Color.argb(255, 255, 120, 62)
        unSelectedText = "חילוץ"
        selectedTextBottom = "חילוץ"
    },
    CarouselItem().apply {
        icon = R.drawable.ic_7
        color = android.graphics.Color.argb(255, 245, 25, 145)
        unSelectedText = "שטיפומט"
        selectedTextBottom = "שטיפומט"
    }
)

var selectedScreen: MutableState<CarouselItem?> = mutableStateOf(items[items.size / 2])


class MainActivity : ComponentActivity() {


    @ExperimentalTime
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val configuration = LocalConfiguration.current

            var chosenCarouselValue by remember {
                mutableStateOf(items[items.size / 2])
            }

            val dm = resources.displayMetrics
            val screenHeight = configuration.screenHeightDp.dp
            val screenWidth = configuration.screenWidthDp.dp


            var rnd = Random.nextInt(0, items.size)

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Image(
                    modifier = Modifier.matchParentSize(),
                    painter = painterResource(R.drawable.iparked_map_bg),
                    contentDescription = "",
                )
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight(fraction = 0.1f)
                        .padding(50.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "text")
                    Text(text = "text")
                    Text(text = "text")
                }

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight(fraction = 0.65f)
                        .align(Alignment.Center)
                ) {
                    CardsRowLayout(
                        cardWidth = (screenWidth.value * 0.9).dp,
                        cardHeight = (screenHeight.value * 0.63).dp,
                        screens = items,
                        selectedScreen = chosenCarouselValue,
                        onSelectedScreenChanged = ::onSelectedCategoryChanged
                    )
                }

                Carousel(
                    items = items,
//                    initialStep = rnd,
                    context = applicationContext,
                    style = CarouselStyle(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.25f)
                        .align(Alignment.BottomCenter),
                    onItemSelected = { chosenCarouselValue = it },
                    onItemSelectedPressed = { onItemSelectedPressed(it) }
                )
            }
        }
    }

    fun onItemSelectedPressed(carouselItem: CarouselItem) {
        Toast.makeText(applicationContext, "click - ${carouselItem.unSelectedText}", Toast.LENGTH_SHORT)
            .show()
    }
}

fun onSelectedCategoryChanged(item: CarouselItem) {

    selectedScreen.value = items[findIndex(items, item)]
}

fun findIndex(arr: Array<CarouselItem>, item: CarouselItem): Int {
    return arr.indexOf(item)
}