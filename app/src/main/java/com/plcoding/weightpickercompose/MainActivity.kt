package com.plcoding.weightpickercompose

import android.app.Activity
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlin.math.abs
import kotlin.time.ExperimentalTime


var items: List<CarouselItem> = listOf(
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
val CARD_STEP = 0.01f
var selectedScreen: MutableState<CarouselItem?> = mutableStateOf(items[items.size / 2])
var movementFromCarousel: MutableState<Int> = mutableStateOf(0)
var movementFromCard: MutableState<Int> = mutableStateOf(0)
var isDrag: MutableState<Boolean> = mutableStateOf(false)

class MainActivity : ComponentActivity() {


    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    @ExperimentalTime
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var currentCarouselItem = remember {
                mutableStateOf(items[items.size / 2])
            }
            var scrollCurrentCarouselItem = remember {
                mutableStateOf(items[items.size / 2])
            }
            var initialChosenItem = remember { mutableStateOf(items.size / 2) }

            var oldPosition = movementFromCarousel.value

            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                Image(
                    modifier = Modifier.matchParentSize(),
                    painter = painterResource(R.drawable.iparked_map_bg),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )
                CardPager(
                    items = items,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.95f),
                    itemFraction = .75f,
                    overshootFraction = 1f,
                    initialIndex = initialChosenItem.value,
                    itemSpacing = 16.dp,
                    onChangeInside = { isDraggable, offset ->
//                            movementFromCarousel.value = offset
                        movementFromCard.value = offset
                        isDrag.value = isDraggable
//                       Log.i(
//                            "alabama",
//                            "movementFromCard: $offset, movementFromCarousel: ${movementFromCarousel.value}"
//                        )
                    },
                    onChangeOutside = movementFromCarousel.value,
                    contentFactory = { item ->
//                        var calcHeight = 0f
////                        val indexOfItem = items.indexOf(scrollCurrentCarouselItem.value)
//                        var movement: Int = getRangeStep(value = movementFromCarousel.value, index = initialChosenItem.value, step = 25) % 25
//                        if (movement <= 0.1f * 25) {
//                            maxHeight += movement.toFloat() * 0.005f
//                        } else if (movement >= 0.9f * 25) {
//                            calcHeight -= movement.toFloat() * 0.005f
//                        }else{
//                            calcHeight +=0.1f
//                        }
//                        Log.i(
//                            "alabama",
//                            "movement: $movement"
//                        )

//                            calcHeight = ( abs(movementFromCarousel.value) % 25) * 0.005f

//                        else if (indexOfItem == items.indexOf(item) - 1 && indexOfItem == items.indexOf(
//                                item
//                            ) + 1
//                        ) {
//                            calcHeight = -(25 - abs(movementFromCarousel.value) % 25) * 0.005f
//                        }

//                        Log.i(
//                            "alabama",
//                            "movementFromCarousel: ${movementFromCarousel.value} movementFromCard: ${movementFromCard.value} calcHeight: $calcHeight, items.indexOf(item): ${
//                                items.indexOf(
//                                    item
//                                )
//                            }"
//                        )
                        var maxHeight = 0.75f
                        var movement: Int = getRangeStep(
                            value = movementFromCarousel.value,
                            index = initialChosenItem.value,
                            step = 25
                        ) % 25

//
//                        Log.i(
//                            "alabama",
//                            "movementFromCarousel - ${movementFromCarousel.value}"
//                        )

                        val minStep = (25 * 0.3).toInt()
                        val maxStep = (25 * 0.7).toInt()
                        val currentStep = abs(movementFromCarousel.value % 25)

//                        if (scrollCurrentCarouselItem.value != item) {
//                            maxHeight = 0.75f - ((minStep ) * CARD_STEP)
//                            Log.i("alabama", "scrollCurrentCarouselItem.value != item -> maxHeight: $maxHeight")
//                        } else if (minStep >= currentStep && currentStep < maxStep) {
//                            maxHeight = 0.75f
//                            Log.i("alabama", "minStep >= currentStep && currentStep < maxStep -> maxHeight: $maxHeight")
//                        } else {
//                            if (currentStep < minStep) {
//                                maxHeight -= (minStep - currentStep) * CARD_STEP
//                            } else {
//                                maxHeight -= (currentStep - maxStep) * CARD_STEP
//                            }
//                            Log.i("alabama", "else -> maxHeight: $maxHeight")
//                        }

                        Card(
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier
                                .fillMaxHeight(fraction = maxHeight)
                                .fillMaxWidth(fraction = 0.95f)
                                .padding(vertical = 10.dp)
                                .coloredShadow(
                                    color = Color(0x371d4773),
                                    shadowRadius = 15.dp,
                                    alpha = 0.1f
                                ),
                        ) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(all = 16.dp)
                                    .align(Alignment.Center),
                            ) {
                                Text(
                                    text = item.unSelectedText,
                                    color = Color(
                                        red = item.color.red,
                                        green = item.color.green,
                                        blue = item.color.blue
                                    ),
                                    style = MaterialTheme.typography.h6,
                                )
                            }

                        }
                    }
                )

                Carousel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.2f)
                        .align(Alignment.BottomCenter),
//                    initialChosenItem = rnd,
                    context = applicationContext,
                    canvasWidth = resources.displayMetrics.widthPixels.toFloat(),
                    canvasHeight = resources.displayMetrics.heightPixels * 0.25f,
                    style = CarouselStyle(),
                    items = items,
                    initialChosenItem = initialChosenItem.value,
                    onItemSelectedPressed = { onItemSelectedPressed(it) },
                    onItemSelected = {
                        currentCarouselItem.value = it
                    },
                    onAngleChangeInside = {
                        movementFromCarousel.value = it
                    },
                    onScroll = {
                        scrollCurrentCarouselItem.value = it
                    },
                    onAngleChangeOutside = movementFromCard.value,
                    isDragOutside = isDrag.value,
                )
            }
        }
    }

    @Composable
    fun draw(item: CarouselItem, modifier: Modifier) {
        Text(
            text = item.unSelectedText,
            color = Color(
                red = item.color.red,
                green = item.color.green,
                blue = item.color.blue
            ),
            modifier = modifier,
        )

    }


    private fun getRangeStep(value: Int, index: Int, step: Int): Int {
        if (value <= 0) {
            return abs(value)
        }
        return abs(index * step) + value
    }

    fun onItemSelectedPressed(carouselItem: CarouselItem) {
        Toast.makeText(
            applicationContext,
            "click - ${carouselItem.unSelectedText}",
            Toast.LENGTH_SHORT
        )
            .show()
    }
}

fun getScreenHeight(activity: Activity): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = activity.windowManager.currentWindowMetrics
        val insets: Insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.height() - insets.top - insets.bottom
    } else {
        activity.resources.displayMetrics.heightPixels
    }
}

inline fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    noinline onClick: () -> Unit
): Modifier = composed {
    clickable(
        enabled = enabled,
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}