package com.plcoding.weightpickercompose

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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


const val INITIAL_CIRCLE_SIZE = 0.6f
const val CIRCLE_ANIMATION_STEP = 0.01f
const val CIRCLE_ANIMATION_TIME = 8L
const val CIRCLE_ANIMATION_REVERSE_TIME = 5L
const val TRANSITION_ANIMATION_TIME = 7L
const val CHOSEN_ITEM_RATIO = 6f
const val ITEM_RATIO = 15f
const val STEP = 22
const val MIDDLE_POINT = -90


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
    initialChosenItem: Int = (items.size - 1) / 2,
    onItemSelected: (CarouselItem) -> Unit,
    onItemSelectedPressed: (CarouselItem) -> Unit
) {
    val min = minStep * STEP
    val max = maxStep * STEP
    val initial = initialChosenItem * STEP

    var chosenItemRadius = 14f
    var itemRadius: Float
    var innerRadius: Float

    var radius by remember {
        mutableStateOf(style.radius)
    }

    var itemSelected: CarouselItem?
    val scaleWidth = style.scaleWidth
    val handler = Handler(Looper.myLooper()!!)

    var currentItem by remember { mutableStateOf(initialChosenItem) }
    var center by remember { mutableStateOf(Offset.Zero) }
    var circleCenter by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }
    var startedAngle by remember { mutableStateOf(0f) }
    var oldAngle by remember { mutableStateOf(angle) }
    var isDrag by remember { mutableStateOf(false) }
    var animationTargetState by remember { mutableStateOf(1f) }

    Canvas(
        modifier = modifier
            .pointerInput(true) {
                detectTapGestures(
                    onPress = {
                        Log.i("alabama", "onPress")
                        val newAngle = -atan2(
                            circleCenter.x - it.x,
                            circleCenter.y - it.y
                        ) * (180f / PI.toFloat())
                        startedAngle = newAngle
//                        var i = 0
//                        while (i <= INITIAL_CIRCLE_SIZE / CIRCLE_ANIMATION_STEP) {
//                            handler.postDelayed({
//                                animationTargetState.value -= CIRCLE_ANIMATION_STEP
//                            }, i * 8L)
//                            i++
//                        }
//                        tryAwaitRelease()
//                        i = 0
//                        while (i <= (1f - animationTargetState.value) / CIRCLE_ANIMATION_STEP) {
//                            handler.postDelayed({
//                                animationTargetState.value += CIRCLE_ANIMATION_STEP
//                            }, i * 8L)
//                            i++
//                        }
                    },
                    onTap = {
                        Log.i("alabama", "onTap")
                        itemSelected = getCurrentItemByClick(
                            x = it.x,
                            y = it.y,
                            xRadius = chosenItemRadius,
                            yRadius = chosenItemRadius,
                            items = items
                        )
                        itemSelected?.let { item ->
                            onItemSelected(item)
                            if (item == items[currentItem]) {
                                onItemSelectedPressed.invoke(item)
//                                var i = 0
//                                animationTargetState = 1f
//                                while (i <= (1f - INITIAL_CIRCLE_SIZE) / CIRCLE_ANIMATION_STEP) {
//                                    handler.postDelayed({
//                                        animationTargetState -= CIRCLE_ANIMATION_STEP
//                                        if (animationTargetState <= INITIAL_CIRCLE_SIZE) {
//                                            animationTargetState = 1f
//                                            onItemSelectedPressed.invoke(item)
//                                        }
//                                    }, i * CIRCLE_ANIMATION_REVERSE_TIME)
//                                    i++
//                                }
                            } else {
                                val old = oldAngle
                                var iterator: Int
                                val animateSteps = abs(
                                    calcClosestAngle(
                                        angleRoundToInt = startedAngle.roundToInt(),
                                        step = STEP
                                    ).toInt()
                                )
                                isDrag = true
                                for (i in 0..animateSteps) {
                                    animationTargetState = INITIAL_CIRCLE_SIZE
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
                                        if (i == animateSteps - 1) {
                                            isDrag = false
                                            var innerI = 0
                                            while (innerI <= (1f - animationTargetState) / CIRCLE_ANIMATION_STEP) {
                                                handler.postDelayed({
                                                    animationTargetState += CIRCLE_ANIMATION_STEP
                                                }, innerI * CIRCLE_ANIMATION_TIME)
                                                innerI++
                                            }
                                        }
                                    }, i * TRANSITION_ANIMATION_TIME)
                                }
                            }
                        }
                    },
                )
            }
            .pointerInput(true) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        Log.i("alabama", "onDragStart")
                        val newAngle = -atan2(
                            circleCenter.x - it.x,
                            circleCenter.y - it.y
                        ) * (180f / PI.toFloat())
                        startedAngle = newAngle
                        animationTargetState = INITIAL_CIRCLE_SIZE
//                        var i = 0
//                        while (i <= INITIAL_CIRCLE_SIZE / CIRCLE_ANIMATION_STEP) {
//                            handler.postDelayed({
//                                animationTargetState -= CIRCLE_ANIMATION_STEP
//                            }, i * 8L)
//                            i++
//                        }
                        radius = style.radius - 60.dp
                    },
                    onDragCancel = {
                        Log.i("alabama", "onDragCancel")
                    },
                    onHorizontalDrag = { change, _ ->
//                        Log.i("alabama", "onDrag")
                        val touchAngle = -atan2(
                            circleCenter.x - change.position.x,
                            circleCenter.y - change.position.y
                        ) * (180f / PI.toFloat())
                        if (abs(startedAngle).roundToInt() != abs(touchAngle).roundToInt()) {
                            isDrag = true
                            val newAngle = oldAngle + (touchAngle - startedAngle)
                            angle = newAngle
                                .coerceIn(
                                    minimumValue = initial - (max + STEP).toFloat(),
                                    maximumValue = initial - (min - STEP).toFloat()
                                )
                        }
                    },
                    onDragEnd = {
                        Log.i("alabama", "onDragEnd")
                        val old = angle.roundToInt()
                        val new = calcClosestAngle(
                            angleRoundToInt = angle.roundToInt(),
                            step = STEP
                        ).coerceIn(
                            minimumValue = initial - max.toFloat(),
                            maximumValue = initial - min.toFloat()
                        ).roundToInt()

                        val animatedStep = abs(old - new)
                        for (i in 0..animatedStep) {
                            handler.postDelayed({
                                var iterator = i
                                if (old > new) {
                                    iterator = i * -1
                                }
                                angle = (old + iterator).toFloat()
                                oldAngle = angle
                                if (i == animatedStep - 1) {
                                    currentItem = initialChosenItem - (angle / STEP).toInt()
                                    Log.i(
                                        "alabama",
                                        "onItemSelected:${items[currentItem].unSelectedText}"
                                    )
                                    onItemSelected(items[currentItem])
                                }
                            }, i * TRANSITION_ANIMATION_TIME)
                        }
                        isDrag = false
                        radius = style.radius
                        var i = 0
                        while (i <= (1f - animationTargetState) / CIRCLE_ANIMATION_STEP) {
                            handler.postDelayed({
                                animationTargetState += CIRCLE_ANIMATION_STEP
                            }, i * CIRCLE_ANIMATION_TIME)
                            i++
                        }
                    }
                )
            }
    ) {
        center = this.center
        circleCenter = Offset(
            center.x,
            scaleWidth.toPx() / 2f + radius.toPx()
        )
        chosenItemRadius = (this.size.width / CHOSEN_ITEM_RATIO)
        itemRadius = (this.size.width / ITEM_RATIO)
        innerRadius = radius.toPx() - scaleWidth.toPx() / 2f

        for (i in min..max) {
            val angleInRad = (i - initial + angle - 90) * ((PI / 180f).toFloat())
            drawContext.canvas.nativeCanvas.apply {

                if (i % STEP == 0) {
                    var x = calcPointX(
                        radius = innerRadius,
                        angleInRad = angleInRad,
                        circleCenterX = circleCenter.x
                    )
                    var y = calcPointY(
                        radius = innerRadius,
                        angleInRad = angleInRad,
                        circleCenterY = circleCenter.y
                    )
                    if (radiansToDegrees(radians = angleInRad).roundToInt() == MIDDLE_POINT && !isDrag) {
                        y -= 50
                        currentItem = (i / STEP)
                        drawCircle(
                            x,
                            y,
                            chosenItemRadius * animationTargetState * 1.15f,
                            Paint().apply {
                                color = items[i / STEP].color
                                alpha = 65
                                maskFilter = BlurMaskFilter(
                                    chosenItemRadius * 0.8f * animationTargetState,
                                    BlurMaskFilter.Blur.SOLID
                                )
                            }
                        )
                        drawCircle(
                            x,
                            y,
                            chosenItemRadius * animationTargetState,
                            Paint().apply {
                                color = items[i / STEP].color
                            }
                        )

                        drawText(
                            items[i / STEP].selectedTextTop,
                            x,
                            y - itemRadius / 2,
                            Paint().apply {
                                color = style.chosenTextColor
                                textSize = chosenItemRadius * animationTargetState * 0.4f
                                textAlign = Paint.Align.CENTER
                            }
                        )
                        drawText(
                            items[i / STEP].selectedTextBottom,
                            x,
                            y + style.textSize.toPx() / 2 + 10.dp.toPx(),
                            Paint().apply {
                                color = style.chosenTextColor
                                textSize = chosenItemRadius * animationTargetState * 0.4f
                                typeface = Typeface.DEFAULT_BOLD
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    } else if ((radiansToDegrees(radians = angleInRad) < MIDDLE_POINT + STEP / 2 && isDrag) &&
                        (radiansToDegrees(radians = angleInRad) > MIDDLE_POINT - STEP / 2 && isDrag)
                    ) {
//                        y -= ( abs(radiansToDegrees(radians = angleInRad) - MIDDLE_POINT))
                        drawCircle(
                            x,
                            y,
                            (itemRadius + (STEP - abs(radiansToDegrees(radians = angleInRad) - MIDDLE_POINT))),
                            Paint().apply {
                                color = items[i / STEP].color
                                maskFilter =
                                    BlurMaskFilter(itemRadius * 0.2f, BlurMaskFilter.Blur.SOLID)
                            }
                        )
                        getBitmapFromVectorDrawable(context, items[i / STEP].icon)?.let {
                            drawBitmap(
                                it,
                                x - it.width / 2,
                                y - it.height / 2,
                                null
                            )
                        }

                        drawText(
                            items[i / STEP].unSelectedText,
                            x,
                            y + itemRadius * 2f,
                            Paint().apply {
                                color = style.textColor
                                textSize = itemRadius * 0.5f
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    } else {
                        if (radiansToDegrees(radians = angleInRad).roundToInt() == MIDDLE_POINT + STEP && !isDrag) {
                            x += STEP
                        } else if (radiansToDegrees(radians = angleInRad).roundToInt() == MIDDLE_POINT - STEP && !isDrag) {
                            x -= STEP
                        }
                        drawCircle(
                            x,
                            y,
                            itemRadius,
                            Paint().apply {
                                color = items[i / STEP].color
                                maskFilter =
                                    BlurMaskFilter(itemRadius * 0.2f, BlurMaskFilter.Blur.SOLID)
//                                setShadowLayer(itemRadius, x, y - itemRadius, items[i / step].color)
                            }
                        )
                        getBitmapFromVectorDrawable(context, items[i / STEP].icon)?.let {
                            drawBitmap(
                                it,
                                x - it.width / 2,
                                y - it.height / 2,
                                Paint().apply {
                                })
                        }
                        drawText(
                            items[i / STEP].unSelectedText,
                            x,
                            y + itemRadius * 2f,
                            Paint().apply {
                                color = style.textColor
                                textSize = itemRadius * 0.5f
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    }
                    items[i / STEP].x = x
                    items[i / STEP].y = y
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


