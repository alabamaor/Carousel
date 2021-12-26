package com.plcoding.weightpickercompose

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.abs
import kotlin.time.ExperimentalTime


@ExperimentalComposeUiApi
@ExperimentalTime
@Composable
fun CarouselView(
    modifier: Modifier = Modifier,
    items: List<CarouselItem> = listOf(),
    onItemSelectedPressed: (CarouselItem) -> Unit,
    onItemSelected: (CarouselItem) -> Unit,
    applicationContext: Context,
    resources: Resources
) {
    val CARD_STEP = 0.01f
    var selectedScreen: MutableState<CarouselItem?> =
        remember { mutableStateOf(items[items.size / 2]) }
    var movementFromCarousel: MutableState<Int> = remember { mutableStateOf(0) }
    var movementFromCard: MutableState<Int> = remember { mutableStateOf(0) }
    var isDrag: MutableState<Boolean> = remember { mutableStateOf(false) }

    var currentCarouselItem = remember {
        mutableStateOf(items[items.size / 2])
    }
    var scrollCurrentCarouselItem = remember {
        mutableStateOf(items[items.size / 2])
    }
    var initialChosenItem = remember { mutableStateOf(items.size / 2) }

    var oldPosition = movementFromCarousel.value
    Box(
        modifier = modifier
    ) {

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
                var maxHeight = 0.65f
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

                /* if (scrollCurrentCarouselItem.value != item) {
                     maxHeight = 0.75f - ((minStep ) * CARD_STEP)
                     Log.i("alabama", "scrollCurrentCarouselItem.value != item -> maxHeight: $maxHeight")
                 } else if (minStep >= currentStep && currentStep < maxStep) {
                     maxHeight = 0.75f
                     Log.i("alabama", "minStep >= currentStep && currentStep < maxStep -> maxHeight: $maxHeight")
                 } else {
                     if (currentStep > minStep) {
                         maxHeight -= (minStep - currentStep) * CARD_STEP
                     } else {
                         maxHeight -= (currentStep - maxStep) * CARD_STEP
                     }
                     Log.i("alabama", "else -> maxHeight: $maxHeight")
                 }*/

                if (isDrag.value) {

                }


                Card(
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .align(alignment = Alignment.Center)
                        .fillMaxHeight(fraction = maxHeight)
                        .fillMaxWidth()
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
                .fillMaxHeight(fraction = 0.25f)
                .align(Alignment.BottomCenter),
//                    initialChosenItem = rnd,
            context = applicationContext,
            canvasWidth = resources.displayMetrics.widthPixels.toFloat(),
            canvasHeight = resources.displayMetrics.heightPixels * 0.25f,
            style = CarouselStyle(),
            items = items,
            initialChosenItem = initialChosenItem.value,
            onItemSelectedPressed = { item ->
                onItemSelectedPressed(item)
            },
            onItemSelected = { item ->
                currentCarouselItem.value = item
                onItemSelected(item)
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

private fun getRangeStep(value: Int, index: Int, step: Int): Int {
    if (value <= 0) {
        return abs(value)
    }
    return abs(index * step) + value
}

val Number.toPx
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

fun convertPixelsToDp(px: Float, context: Context): Float {
    return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}