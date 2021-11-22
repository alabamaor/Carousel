package com.plcoding.weightpickercompose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
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

    var currentItem = 0

    var action by remember {
        mutableStateOf(MotionEvent.ACTION_CANCEL)
    }

    var animated by remember {
        mutableStateOf(0)
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
    var valueAnimation by remember {
        mutableStateOf(0f)
    }
    var oldAngle by remember {
        mutableStateOf(angle)
    }
    var isDrag by remember {
        mutableStateOf(false)
    }

//
//    val FRAME_DELAY = 200 // in ms
//
//    val mBitmapIndex = AtomicInteger()
//    var mView: View
//    var mThread: Thread
//
//    val mBitmaps = arrayOf(
//        getBitmapFromVectorDrawable(context, R.drawable.ic_accessibility),
//        getBitmapFromVectorDrawable(context, R.drawable.ic_accessibility1)
//    )

    val animationTargetState = remember { mutableStateOf(0.5f) }

    var animatedFloatState = animateFloatAsState(
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

//                        animationTargetState.value = 0.5f
                        isOnCirclePress = (getCurrentItemByClick(
                            x = it.x,
                            y = it.y,
                            xRadius = itemRadius + 20,
                            yRadius = chosenItemRadius+ 20,
                            items = items
                        ) != null)
                        dragStartedAngle = newAngle

                    }
                    MotionEvent.ACTION_MOVE -> {
                        val touchAngle = -atan2(
                            circleCenter.x - it.x,
                            circleCenter.y - it.y
                        ) * (180f / PI.toFloat())
                        if (abs(dragStartedAngle).roundToInt() != abs(touchAngle).roundToInt()) {
                            isDrag = true
                            val newAngle = oldAngle + (touchAngle - dragStartedAngle)
                            angle = newAngle.coerceIn(
                                minimumValue = initial - max.toFloat(),
                                maximumValue = initial - min.toFloat()
                            )
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        val newAngle = if (!isDrag && isOnCirclePress) {
                            oldAngle - dragStartedAngle
                        } else {
                            angle
                        }
                        angle = calcClosestAngle(angle = newAngle, step = step).coerceIn(
                            minimumValue = initial - max.toFloat(),
                            maximumValue = initial - min.toFloat()
                        )
                        oldAngle = angle
                        isDrag = false
                        valueAnimation = 0f
                    }
                }
                onChange((initial - angle).roundToInt())
                action = it.action
                true

            }
    ) {
//        mThread = object : Thread() {
//            override fun run() {
//                while (true) {
//                    if (animated >= 1000) animated = 0
//                    animated += 1
//                    try {
//                        sleep(FRAME_DELAY.toLong())
//                    } catch (e: InterruptedException) {
//                        e.printStackTrace()
//                    }
//                    mBitmapIndex.incrementAndGet()
//                }
//            }
//        }
//        mThread.start()

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

//                        animationTargetState.value = 0.5f
                        currentItem = (i / step)
                        drawCircle(
                            x,
                            y,
                            chosenItemRadius * animatedFloatState.value * 1.2f + valueAnimation,
                            Paint().apply {
                                color = items[i / step].color
                                alpha = 50
                            }
                        )
                        drawCircle(
                            x,
                            y,
                            chosenItemRadius * animatedFloatState.value + valueAnimation,
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
                        animationTargetState.value = 1f
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
//                        mBitmaps[abs(mBitmapIndex.get() % mBitmaps.size)]?.let {
//                            drawBitmap(
//                                it,
//                                x - 100,
//                                y - 100,
//                                null
//                            )
//                        }
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

fun calcClosestAngle(angle: Float, step: Int): Float {
    var tempAngle = angle.roundToInt() / step
    if ((angle.roundToInt() % step) >= step / 2) {
        tempAngle++// 0
     } else if (abs(angle.roundToInt() % step) >= step / 2) {
        tempAngle-- // -2
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