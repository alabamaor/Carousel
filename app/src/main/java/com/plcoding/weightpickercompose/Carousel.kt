package com.plcoding.weightpickercompose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlin.math.*
import kotlin.time.ExperimentalTime


@ExperimentalTime
@ExperimentalComposeUiApi
@Composable
fun Carousel(
    modifier: Modifier = Modifier,
    context: Context,
    style: CarouselStyle = CarouselStyle(),
    items: Array<CarouselItem> = arrayOf(),
    minStep: Int = 0,
    maxStep: Int = (items.size - 1),
    initialStep: Int = (items.size - 1) / 2,
    onItemSelected: (CarouselItem) -> Unit,
    onItemSelectedPressed: () -> Unit
) {
    val step = 20
    val middlePoint = -90
    val min = minStep
    val max = maxStep * step
    val initial = initialStep * step

    var chosenItemRadius = 14f
    var itemRadius = 14f

    val scaleWidth = style.scaleWidth
    val radius = style.radius
    var innerRadius = 0f

    val handler = Handler(Looper.myLooper()!!)

    var currentItem by remember {
        mutableStateOf(initialStep)
    }
    var center by remember {
        mutableStateOf(Offset.Zero)
    }
    var circleCenter by remember {
        mutableStateOf(Offset.Zero)
    }
    var angle by remember {
        mutableStateOf(0f)
    }
    var startedAngle by remember {
        mutableStateOf(0f)
    }

    var oldAngle by remember {
        mutableStateOf(angle)
    }

    var isDrag by remember {
        mutableStateOf(false)
    }

    var isTap by remember {
        mutableStateOf(false)
    }

    var itemSelected: CarouselItem? = items[initialStep]

    var animationTargetState = remember { mutableStateOf(1f) }

    var animateFloat = animateFloatAsState(
        // Whenever the target value changes, new animation
        // will start to the new target value
        targetValue = animationTargetState.value,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearOutSlowInEasing
        )
    )
    Canvas(
        modifier = modifier
            .pointerInput(true) {
                detectTapGestures(
                    onPress = {

//                        Log.i("alabama", "onPress")
                        val newAngle = -atan2(
                            circleCenter.x - it.x,
                            circleCenter.y - it.y
                        ) * (180f / PI.toFloat())
                        startedAngle = newAngle
                    },
                    onTap = {
                        itemSelected = getCurrentItemByClick(
                            x = it.x,
                            y = it.y,
                            xRadius = chosenItemRadius ,
                            yRadius = chosenItemRadius ,
                            items = items
                        )
                        itemSelected?.let { item ->
                            onItemSelected(item)
                            if (item == items[currentItem]) {
                                onItemSelectedPressed.invoke()
                            } else {
                                val old = oldAngle
                                var iterator: Int
                                val animateSteps = abs(calcClosestAngle(
                                    angleRoundToInt = startedAngle.roundToInt(),
                                    step = step
                                ).toInt())
                                isDrag = true
                                for (i in 0..animateSteps) {
                                        handler.postDelayed({
                                            iterator = i
                                        if (startedAngle > 0) {
                                            iterator = i * -1
                                        }
                                        angle = (old + iterator).coerceIn(
                                            minimumValue = initial - max.toFloat(),
                                            maximumValue = initial - min.toFloat()
                                        )
                                        oldAngle = angle
                                            if (i == animateSteps-1){
                                                isDrag = false
                                            }
                                    }, i * 10L)
                                }
                            }
                        }

//                        angle = angle.coerceIn(
//                            minimumValue = initial - max.toFloat(),
//                            maximumValue = initial - min.toFloat()
//                        )
                        animationTargetState.value = 1f
                    },
                )
            }
            .pointerInput(true) {
                detectDragGestures(
                    onDragStart = {
                        animationTargetState.value = 0.7f
                    },
                    onDrag = { change, offset ->
//                        Log.i("alabama", "onDrag")
                        val touchAngle = -atan2(
                            circleCenter.x - change.position.x,
                            circleCenter.y - change.position.y
                        ) * (180f / PI.toFloat())
                        if (abs(startedAngle).roundToInt() != abs(touchAngle).roundToInt()) {
                            isDrag = true
                            val newAngle = oldAngle + (touchAngle - startedAngle)
                            angle = newAngle.coerceIn(
                                minimumValue = initial - max.toFloat(),
                                maximumValue = initial - min.toFloat()
                            )
                        }
                    },
//                    onDragCancel = { Log.i("alabama", "onDragCancel") },
//                    onDragStart = { offset -> Log.i("alabama", "onDragStart") },
                    onDragEnd = {
//                        Log.i("alabama", "onDragEnd")

                        /*
                        val old = oldAngle
                        var iterator: Int
                        for (i in 0..abs(calcClosestAngle(
                            angleRoundToInt = startedAngle.roundToInt(),
                            step = step
                        ).toInt())) {
                            handler.postDelayed({
                                iterator = i
                                if (startedAngle > 0) {
                                    iterator = i * -1
                                }
                                angle = (old + iterator).coerceIn(
                                    minimumValue = initial - max.toFloat(),
                                    maximumValue = initial - min.toFloat()
                                )
                                oldAngle = angle
                            }, i * 10L)
                        }

                        */




                        angle = calcClosestAngle(
                            angleRoundToInt = angle.roundToInt(),
                            step = step
                        ).coerceIn(
                            minimumValue = initial - max.toFloat(),
                            maximumValue = initial - min.toFloat()
                        )

                        oldAngle = angle
                        isDrag = false
                        animationTargetState.value = 1f
                        currentItem = initialStep - (angle / step).toInt()
                        Log.i("alabama", "onItemSelected:${items[currentItem].unSelectedText}")
                        onItemSelected(items[currentItem])
                    }
                )
            }
    ) {
        center = this.center
        circleCenter = Offset(
            center.x,
            scaleWidth.toPx() / 2f + radius.toPx()
        )
        chosenItemRadius = (this.size.width / 8f)
        itemRadius = (this.size.width / 16f)
        innerRadius = radius.toPx() - scaleWidth.toPx() / 2f

        for (i in min..max) {

            val angleInRad = (i - initial + angle - 90) * ((PI / 180f).toFloat())

            drawContext.canvas.nativeCanvas.apply {

                if (i % step == 0) {
                    val x = calcPointX(
                        radius = innerRadius,
                        angleInRad = angleInRad,
                        circleCenterX = circleCenter.x
                    )
                    val y = calcPointY(
                        radius = innerRadius,
                        angleInRad = angleInRad,
                        circleCenterY = circleCenter.y
                    )

                    items[i / step].x = x
                    items[i / step].y = y
                    var a = isDrag
                    if (radiansToDegrees(radians = angleInRad).roundToInt() == middlePoint && !isDrag) {
                        currentItem = (i / step)
                        drawCircle(
                            x,
                            y,
                            chosenItemRadius * animateFloat.value * 1.2f,
                            Paint().apply {
                                color = items[i / step].color
                                alpha = 50
                            }
                        )
                        drawCircle(
                            x,
                            y,
                            chosenItemRadius * animateFloat.value,
                            Paint().apply {
                                color = items[i / step].color
                            }
                        )

                        drawText(
                            items[i / step].selectedTextTop,
                            x,
                            y - itemRadius / 2,
                            Paint().apply {
                                color = style.chosenTextColor
                                textSize = chosenItemRadius * animateFloat.value * 0.4f
                                textAlign = Paint.Align.CENTER
                            }
                        )
                        drawText(
                            items[i / step].selectedTextBottom,
                            x,
                            y + style.textSize.toPx() / 2 + 10.dp.toPx(),
                            Paint().apply {
                                color = style.chosenTextColor
                                textSize = chosenItemRadius * animateFloat.value * 0.4f
                                typeface = Typeface.DEFAULT_BOLD
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    } else if ((radiansToDegrees(radians = angleInRad) < middlePoint + step / 2 && isDrag) &&
                        (radiansToDegrees(radians = angleInRad) > middlePoint - step / 2 && isDrag)
                    ) {
                        drawCircle(
                            x,
                            y,
                            (itemRadius + (step - abs(radiansToDegrees(radians = angleInRad) - middlePoint))),
                            Paint().apply {
                                color = items[i / step].color
                            }
                        )
                        getBitmapFromVectorDrawable(context, items[i / step].icon)?.let {
                            drawBitmap(
                                it,
                                x - it.width / 2,
                                y - it.height / 2,
                                null
                            )
                        }

                        drawText(
                            items[i / step].unSelectedText,
                            x,
                            y + chosenItemRadius,
                            Paint().apply {
                                color = style.textColor
                                textSize = (itemRadius) * 0.7f
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    } else {
                        drawCircle(
                            x,
                            y,
                            itemRadius,
                            Paint().apply {
                                color = items[i / step].color
                            }
                        )
                        getBitmapFromVectorDrawable(context, items[i / step].icon)?.let {
                            drawBitmap(
                                it,
                                x - it.width / 2,
                                y - it.height / 2,
                                Paint().apply {
                                })
                        }
                        drawText(
                            items[i / step].unSelectedText,
                            x,
                            y + chosenItemRadius,
                            Paint().apply {
                                color = style.textColor
                                textSize = itemRadius * 0.7f
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    }
                }
            }
        }
    }
}

fun degreesToRadians(degrees: Float): Double {
    return degrees * PI / 180.0
}

fun radiansToDegrees(radians: Float): Float {
    return (radians * 180.0 / PI).toFloat()
}

fun getCurrentItemByClick(
    x: Float,
    y: Float,
    items: Array<CarouselItem>,
    xRadius: Float,
    yRadius: Float
): CarouselItem? {

    for (item in items) {
        if ((x > item.x - xRadius && x < item.x + xRadius) &&
            (y > item.y - yRadius && y < item.y + yRadius)
        )
            return item
    }
    return null
}

fun calcClosestAngle(angleRoundToInt: Int, step: Int): Float {
    var tempAngle = angleRoundToInt / step
    if ((angleRoundToInt % step) >= step / 2) {
        tempAngle++
    } else if (abs(angleRoundToInt % step) >= step / 2) {
        tempAngle--
    }
    return (tempAngle * step).toFloat()
}

fun calcPointX(circleCenterX: Float, angleInRad: Float, radius: Float): Float {
    return radius * cos(angleInRad) + circleCenterX
}

fun calcPointY(circleCenterY: Float, angleInRad: Float, radius: Float): Float {
    return radius * sin(angleInRad) + circleCenterY
}

fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
    val drawable = ContextCompat.getDrawable(context, drawableId)
    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

