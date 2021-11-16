package com.plcoding.weightpickercompose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.Log
import android.view.MotionEvent
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlin.math.*


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
    onChange: (Int) -> Unit
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
    var innerRadius: Float = 0f


    var isOnCirclePress = false


    var currentItem = initialStep

    var action by remember {
        mutableStateOf(MotionEvent.ACTION_CANCEL)
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
    var dragStartedAngle by remember {
        mutableStateOf(0f)
    }
    var pressAngle by remember {
        mutableStateOf(0f)
    }
    var oldAngle by remember {
        mutableStateOf(angle)
    }
    var isDrag by remember {
        mutableStateOf(false)
    }

    val animationTargetState = remember { mutableStateOf(1f) }

    val animatedFloatState = animateFloatAsState(
        // Whenever the target value changes, new animation
        // will start to the new target value
        targetValue = animationTargetState.value,
        animationSpec = tween(
            durationMillis = 2000,
            easing = LinearOutSlowInEasing
        )
    )

    Canvas(
        modifier = modifier
            .pointerInteropFilter {
                when (it.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val newAngle = -atan2(
                            circleCenter.x - it.x,
                            circleCenter.y - it.y
                        ) * (180f / PI.toFloat())

                        if (getCurrentItemByClick(
                                x = it.x,
                                y = it.y,
                                xRadius = itemRadius + 10,
                                yRadius = chosenItemRadius + 10,
                                items = items
                            ) != null
                        ) {
                            isOnCirclePress = true
                            dragStartedAngle = newAngle
                        } else {
                            isOnCirclePress = false
                        }
//                            animationTargetState.value = 0.5f
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val touchAngle = -atan2(
                            circleCenter.x - it.x,
                            circleCenter.y - it.y
                        ) * (180f / PI.toFloat())
//                        if (getCurrentItemByClick(
//                                x = it.x,
//                                y = it.y,
//                                radius = itemRadius,
//                                items = items
//                            ) != null
//                        ) {
                        if (isOnCirclePress) {
                            if (abs(dragStartedAngle).roundToInt() != abs(touchAngle).roundToInt()) {
                                isDrag = true
                                val newAngle = oldAngle + (touchAngle - dragStartedAngle)
                                angle = newAngle.coerceIn(
                                    minimumValue = initial - max.toFloat(),
                                    maximumValue = initial - min.toFloat()
                                )
                            }
                        }
                    }
//                    }
                    MotionEvent.ACTION_UP -> {
                        if (isOnCirclePress) {
                            if (isDrag) {
                                angle = calcClosestAngle(angle = angle, step = step)
                                isDrag = false
                            } else {
                                val newAngle = oldAngle - dragStartedAngle
                                angle = newAngle.coerceIn(
                                    minimumValue = initial - max.toFloat(),
                                    maximumValue = initial - min.toFloat()
                                )
                                angle = calcClosestAngle(angle = angle, step = step)
                            }
                            oldAngle = angle
                            isDrag = false
//                            animationTargetState.value = 1f
                        }
                    }
                }
                onChange((initial - angle).roundToInt())
                action = it.action
                true

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
                    val x = innerRadius * cos(angleInRad) + circleCenter.x
                    val y = innerRadius * sin(angleInRad) + circleCenter.y

                    items[i / step].x = x
                    items[i / step].y = y

                    if (radiansToDegrees(radians = angleInRad).roundToInt() == middlePoint && !isDrag) {
                        currentItem = (i / step)
                        drawCircle(
                            x,
                            y,
                            chosenItemRadius * animatedFloatState.value * 1.1f,
                            Paint().apply {
                                color = items[i / step].color
                                alpha = 80
                            }
                        )
                        drawCircle(
                            x,
                            y,
                            chosenItemRadius * animatedFloatState.value,
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
                                textSize = chosenItemRadius * animatedFloatState.value * 0.4f
                                textAlign = Paint.Align.CENTER
                            }
                        )
                        drawText(
                            items[i / step].selectedTextBottom,
                            x,
                            y + style.textSize.toPx() / 2 + 10.dp.toPx(),
                            Paint().apply {
                                color = style.chosenTextColor
                                textSize = chosenItemRadius * animatedFloatState.value * 0.4f
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
                            itemRadius, //(itemRadius + (step - abs(radiansToDegrees(radians = angleInRad) - middlePoint))).toFloat(),
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
//                animationTargetState.value = 0f

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

fun calcClosestAngle(angle: Float, step: Int): Float {
    Log.i("alabama", "(angle % step): ${(angle % step)}")
    var tempAngle = angle.roundToInt() / step
    if ((angle % step) >= step / 2) {
        tempAngle++
    } else if (abs(angle % step) >= step / 2) {
        tempAngle--
    }
    return (tempAngle * step).toFloat()
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
