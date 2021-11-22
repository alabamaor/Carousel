package com.plcoding.weightpickercompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

var items: Array<CarouselItem> = arrayOf(
    CarouselItem().apply {
        icon = R.drawable.ic_1
        color = android.graphics.Color.BLUE
        unSelectedText = "תדלוק"
        selectedTextBottom = "תדלוק"
    },
    CarouselItem().apply {
        icon = R.drawable.ic_2
        color = android.graphics.Color.YELLOW
        unSelectedText = "חניונים"
        selectedTextBottom = "חניונים"
    },
    CarouselItem().apply {
        icon = R.drawable.ic_3
        color = android.graphics.Color.GREEN
        unSelectedText = "חנייה"
        selectedTextBottom = "חנייה"
    },
    CarouselItem().apply {
        icon = R.drawable.ic_4
        color = android.graphics.Color.MAGENTA
        unSelectedText = "ביטוח"
        selectedTextBottom = "ביטוח"
    },
    CarouselItem().apply {
        icon = R.drawable.ic_5
        color = android.graphics.Color.BLACK
        unSelectedText = "תחבורה"
        selectedTextBottom = "תחבורה"
    },
    CarouselItem().apply {
        icon = R.drawable.ic_6
        color = android.graphics.Color.GRAY
        unSelectedText = "חילוץ"
        selectedTextBottom = "חילוץ"
    },
    CarouselItem().apply {
        icon = R.drawable.ic_7
        color = android.graphics.Color.LTGRAY
        unSelectedText = "שטיפומט"
        selectedTextBottom = "שטיפומט"
    }
)

var selectedScreen: MutableState<CarouselItem?> = mutableStateOf(items[items.size / 2])





class MainActivity : ComponentActivity() {


    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val configuration = LocalConfiguration.current


            val dm = resources.displayMetrics
            val screenHeight = configuration.screenHeightDp.dp
            val screenWidth = configuration.screenWidthDp.dp

            var chosenCarouselValue by remember {
                mutableStateOf(80)
            }

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
                        .align(Alignment.Center)) {
                    CardsRowLayout(
                        cardWidth = (screenWidth.value*0.9).dp,
                        cardHeight = (screenHeight.value*0.63).dp,
                        screens = items,
                        selectedScreen = selectedScreen.value,
                        onSelectedScreenChanged = ::onSelectedCategoryChanged)
                }

                Carousel(
                    items = items,
//                    initialStep = rnd,
                    context = applicationContext,
                    style = CarouselStyle(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.25f)
                        .align(Alignment.BottomCenter)
                ) {
                    chosenCarouselValue = it
                }
            }
        }
    }

}

fun onSelectedCategoryChanged(item: CarouselItem) {

    selectedScreen.value = items[findIndex(items, item)]
}

fun findIndex(arr: Array<CarouselItem>, item: CarouselItem): Int {
    return arr.indexOf(item)
}