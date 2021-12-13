package com.plcoding.weightpickercompose

import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import kotlin.math.pow
import kotlin.math.sqrt


@ExperimentalComposeUiApi
@Composable
fun CardsRowLayout(
    screens: Array<CarouselItem>,
    canvasWidth: Float,
    cardWidth: Dp,
    cardHeight: Dp,
    selectedScreen: CarouselItem?,
    onDrag: (Int) -> Unit,
    onDragEnd: () -> Unit,
    onSelectedScreenChanged: (CarouselItem) -> Unit,
) {
    val scrollState = rememberLazyListState()
    var startDragPointX = 0f
    var currentDragPointX = 0f
    var step = 1
    var STEP = canvasWidth / 5


    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth()
                .pointerInput(true) {
                    detectHorizontalDragGestures (
                        onDragStart = {
//                        Log.i("alabama", "onDragStart")
                            currentDragPointX = it.x
                            Log.i("alabama", "offset: $it")
                        },
                        onHorizontalDrag = { change, offset ->
                            Log.i("alabama", "onDrag")
                            Log.i("alabama", "change: $change")
                            Log.i("alabama", "offset: $offset")

                            if (currentDragPointX + STEP < change.position.x) {
                                currentDragPointX = change.position.x
                                onDrag(step)
                            } else if (currentDragPointX - STEP > change.position.x) {
                                currentDragPointX = change.position.x
                                onDrag(step * -1)
                            }
                        },
                        onDragEnd = {
                            onDragEnd()
//                        Log.i("alabama", "onDragEnd")
                        }
                    )
                },
            state = scrollState,
        ) {
            items(screens) { item ->
                Screen(
                    cardHeight = cardHeight,
                    cardWidth = cardWidth,
                    screen = item,
                    isSelected = selectedScreen == item,
                    onSelectedScreenChanged = { screen ->
                        onSelectedScreenChanged(screen)
                    },
                )
            }
        }
    }
}


fun calcDistance(point1: Offset, point2: Offset): Float {
    return sqrt((point2.x - point1.x).pow(2f) + (point2.y - point1.y).pow(2f))
}