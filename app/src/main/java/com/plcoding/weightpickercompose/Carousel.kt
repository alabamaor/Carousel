package com.plcoding.weightpickercompose

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.math.*


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
    var min = minStep
    var max = maxStep * style.step
    var initial = initialStep * style.step

    var chosenItemRadius: Float
    var itemRadius: Float
    var outerRadius: Float
    var innerRadius: Float
    val unchosenTextSize: TextUnit = 18.sp
    val chosenTextSize: TextUnit = 22.sp


    var step = 14
    val radius = style.radius

    val scaleWidth = style.scaleWidth
    var currentValue = initial
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
    Canvas(
        modifier = modifier
            .pointerInput(true) {
                detectTapGestures(
//                onTap = {offset ->
//                    dragStartedAngle = atan2(
//                        circleCenter.x - offset.x,
//                        circleCenter.y - offset.y
//                    ) * (180f / PI.toFloat())
//
//                    angle = dragStartedAngle.coerceIn(
//                        minimumValue = initial - max.toFloat(),
//                        maximumValue = initial - min.toFloat()
//                    )
//                },

                    onLongPress = { offset ->
                        Log.i("alabama", "onLongPress")
                    },
                    onTap = { offset ->
                        val touchAngle = atan2(
                            circleCenter.x - offset.x,
                            circleCenter.y - offset.y
                        ) * (180f / PI.toFloat())

                        val newAngle = oldAngle + (touchAngle - dragStartedAngle)
                        angle = newAngle.coerceIn(
                            minimumValue = initial - max.toFloat(),
                            maximumValue = initial - min.toFloat()
                        )

                        var a = angle.roundToInt() / style.step
                        if (angle % style.step >= style.step / 2) {
                            a++
                        }
                        angle = (a * style.step).toFloat()

                        Log.i(
                            "alabama - detectTap",
                            "offset: $offset " +
                                    "angle: $angle " +
                                    "dragStartedAngle: $dragStartedAngle "
                        )
                        oldAngle = angle
                    }
                )
            }.pointerInput(true) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragStartedAngle = -atan2(
                            circleCenter.x - offset.x,
                            circleCenter.y - offset.y
                        ) * (180f / PI.toFloat())
                        isDrag = true
                        Log.i(
                            "alabama - onDragStart ",
                            "dragStartedAngle: $dragStartedAngle " +
                                    "offset: $offset"
                        )
                    },
                    onDragEnd = {
                        var a = angle.roundToInt() / style.step
                        if (angle % style.step >= style.step / 2) {
                            a++
                        }
                        angle = (a * style.step).toFloat()
                        oldAngle = angle
                        isDrag = false
                        Log.i(
                            "alabama - onDragEnd",
                            "oldAngle: $oldAngle" +
                                    "angleInDeg: " + radiansToDegrees(oldAngle) +
                                    "round angleInDeg: " + radiansToDegrees(oldAngle).roundToInt()
                        )
                    }
                ) { change, _ ->
                    val touchAngle = -atan2(
                        circleCenter.x - change.position.x,
                        circleCenter.y - change.position.y
                    ) * (180f / PI.toFloat())

                    val newAngle = oldAngle + (touchAngle - dragStartedAngle)
                    angle = newAngle.coerceIn(
                        minimumValue = initial - max.toFloat(),
                        maximumValue = initial - min.toFloat()
                    )

                    onChange((initial - angle).roundToInt())
                }
            }
    ) {
        center = this.center
        circleCenter = Offset(
            center.x,
            scaleWidth.toPx() / 2f + radius.toPx()
        )

        chosenItemRadius = (this.size.width / 8f)
        itemRadius = (this.size.width / 18f)
        outerRadius = radius.toPx() + scaleWidth.toPx() / 2f
        innerRadius = radius.toPx() - scaleWidth.toPx() / 2f



        drawContext.canvas.nativeCanvas.apply {
//            drawCircle(
//                circleCenter.x,
//                circleCenter.y,
//                radius.toPx(),
//                Paint().apply {
//                    strokeWidth = scaleWidth.toPx()
//                    color = Color.WHITE
//                    setStyle(Paint.Style.STROKE)
//                    setShadowLayer(
//                        60f,
//                        0f,
//                        0f,
//                        Color.argb(50, 0, 0, 0)
//                    )
//                }
//            )
        }
        // Draw lines
        for (i in min..max) {
            val angleInRad = (i - initial + angle - 90) * ((PI / 180f).toFloat())
//            val lineType = when {
//                i % 10 == 0 -> LineType.TenStep
//                i % 5 == 0 -> LineType.FiveStep
//                else -> LineType.Normal
//            }
//            val lineLength = when(lineType) {
//                LineType.Normal -> style.normalLineLength.toPx()
//                LineType.FiveStep -> style.fiveStepLineLength.toPx()
//                LineType.TenStep -> style.tenStepLineLength.toPx()
//            }
//            val lineColor = when(lineType) {
//                LineType.Normal -> style.normalLineColor
//                LineType.FiveStep -> style.fiveStepLineColor
//                LineType.TenStep -> style.tenStepLineColor
//            }
//            val lineStart = Offset(
//                x = (outerRadius - lineLength) * cos(angleInRad) + circleCenter.x,
//                y = (outerRadius - lineLength) * sin(angleInRad) + circleCenter.y
//            )
//            val lineEnd = Offset(
//                x = outerRadius * cos(angleInRad) + circleCenter.x,
//                y = outerRadius * sin(angleInRad) + circleCenter.y
//            )

            drawContext.canvas.nativeCanvas.apply {
                if (i % style.step == 0) {
//                    Log.i(
//                        "alabama",
//                        "angleInRad: $angleInRad, " +
//                                "angleInDeg: " + radiansToDegrees(angleInRad) +
//                                "round angleInDeg: " + radiansToDegrees(angleInRad).roundToInt() +
//                                "text: ${items[i / style.step].unSelectedText}"
//                    )
                    val x = innerRadius * cos(angleInRad) + circleCenter.x
                    val y = innerRadius * sin(angleInRad) + circleCenter.y

                    drawCircle(
                        x,
                        y,
                        if (radiansToDegrees(radians = angleInRad).roundToInt() == -90 && !isDrag) chosenItemRadius else itemRadius,
                        Paint().apply {
                            color = items[i / style.step].color
                        }
                    )

                    if (radiansToDegrees(radians = angleInRad).roundToInt() == -90 && !isDrag) {
                        drawCircle(
                            x,
                            y,
                            chosenItemRadius * 1.1f,
                            Paint().apply {
                                color = items[i / style.step].color
                                alpha = 80
                            }
                        )
                        drawText(
                            items[i / style.step].selectedTextTop,
                            x,
                            y - unchosenTextSize.toPx() / 2,
                            Paint().apply {
                                color = style.chosenTextColor
                                textSize = chosenTextSize.value
                                textAlign = Paint.Align.CENTER
                            }
                        )
                        drawText(
                            items[i / style.step].selectedTextBottom,
                            x,
                            y + style.textSize.toPx() / 2 + 10.dp.toPx(),
                            Paint().apply {
                                color = style.chosenTextColor
                                textSize = chosenTextSize.value
                                typeface = Typeface.DEFAULT_BOLD
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    } else {
                        getBitmapFromVectorDrawable(context, items[i / style.step].icon)?.let {
                            drawBitmap(
                                it,
                                x - it.width / 2,
                                y - it.height / 2,
                                Paint().apply {
                                })
                        }
                        drawText(
                            items[i / style.step].unSelectedText,
                            x,
                            y + chosenItemRadius,
                            Paint().apply {
                                color = style.textColor
                                textSize = unchosenTextSize.value
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

fun radiansToDegrees(radians: Float): Double {
    return radians * 180.0 / PI
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