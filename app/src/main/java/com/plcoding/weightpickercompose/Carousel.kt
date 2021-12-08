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


const val INITIAL_CIRCLE_SIZE = 1f
const val SELECTED_CIRCLE_SIZE = 1.8f
const val CHOSEN_ITEM_RATIO = 6f
const val ITEM_RATIO = 15f

const val STEP = 25
const val CIRCLE_ANIMATION_STEP = 0.01f
const val FAST_CIRCLE_ANIMATION_STEP = 0.1f

const val CIRCLE_ANIMATION_TIME = 8L
const val CIRCLE_ANIMATION_REVERSE_TIME = 2L
const val TRANSITION_ANIMATION_TIME = 7L

const val MIDDLE_POINT = -90


@ExperimentalTime
@ExperimentalComposeUiApi
@Composable
fun Carousel(
    modifier: Modifier = Modifier,
    context: Context,
    canvasWidth: Float,
    canvasHeight: Float,
    style: CarouselStyle = CarouselStyle(),
    items: Array<CarouselItem> = arrayOf(),
    initialChosenItem: Int = (items.size - 1) / 2,
    onItemSelected: (CarouselItem) -> Unit,
    onItemSelectedPressed: (CarouselItem) -> Unit
) {

//    var itemTextPositionX: Float
    var itemTextPositionY: Float
    var itemRadius = 14f
    var canvasItemRadius = itemRadius
    var canvasMiddleItemPaint: Paint
    var expandStep = 0
    var expand = 0f
    var itemText = ""
    var itemTextStyle: Paint

    var carouselViewState: CarouselViewState = CarouselViewState.OtherPoint
    var itemSelected: CarouselItem?
    val handler = Handler(Looper.myLooper()!!)

    var heightCenter by remember { mutableStateOf((canvasWidth * 0.5f) * 1.5f) }
    var innerRadius by remember { mutableStateOf((canvasWidth * 0.5f) * 1.3f) }

    var currentItem by remember { mutableStateOf(initialChosenItem) }
    var isAnimationRunning by remember { mutableStateOf(false) }
    var circleCenter by remember { mutableStateOf(Offset.Zero) }
    var angle by remember { mutableStateOf(0f) }
    var startedAngle by remember { mutableStateOf(0f) }
    var oldAngle by remember { mutableStateOf(angle) }
    var isDrag by remember { mutableStateOf(false) }
    var animationTargetState by remember { mutableStateOf(SELECTED_CIRCLE_SIZE) }


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
                        if (!isAnimationRunning) {
                            startedAngle = newAngle
                        }
                    },
                    onTap = {
                        Log.i("alabama", "onTap")
                        if (!isAnimationRunning) {
                            isAnimationRunning = true
                            itemSelected = getCurrentItemByClick(
                                x = it.x,
                                y = it.y,
                                xRadius = canvasWidth / CHOSEN_ITEM_RATIO,
                                yRadius = canvasWidth / CHOSEN_ITEM_RATIO,
                                items = items
                            )
                            itemSelected?.let { item ->
                                onItemSelected(item)
                                if (item == items[currentItem]) {
                                    onItemSelectedPressed.invoke(item)
                                    isAnimationRunning = false
                                } else {
                                    val old = oldAngle
                                    var iterator: Int
                                    val animateSteps = abs(
                                        calcClosestAngle(
                                            angleRoundToInt = startedAngle.roundToInt(),
                                            step = STEP
                                        ).toInt()
                                    )

                                    var innerI1 = 0
                                    var innerI2 = 0

                                    isDrag = false
                                    while (innerI1 <= (SELECTED_CIRCLE_SIZE - INITIAL_CIRCLE_SIZE) / CIRCLE_ANIMATION_STEP) {
                                        handler.postDelayed({
                                            if (animationTargetState > INITIAL_CIRCLE_SIZE) {
                                                animationTargetState -= CIRCLE_ANIMATION_STEP
                                            }
                                            if (animationTargetState <= INITIAL_CIRCLE_SIZE) {
                                                isDrag = true

                                                for (i in 1..animateSteps) {
                                                    handler.postDelayed({
                                                        iterator = i
                                                        if (startedAngle > 0) {
                                                            iterator = i * -1
                                                        }
                                                        angle = (old + iterator).coerceIn(
                                                            minimumValue = (initialChosenItem * STEP) - ((items.size - 1) * STEP).toFloat(),
                                                            maximumValue = (initialChosenItem * STEP).toFloat()
                                                        )
                                                        oldAngle = angle
                                                        if (i == animateSteps - 1) {
                                                            isDrag = false
                                                            while (innerI2 <= (SELECTED_CIRCLE_SIZE - animationTargetState) / CIRCLE_ANIMATION_STEP) {
                                                                handler.postDelayed(
                                                                    {
                                                                        if (animationTargetState < SELECTED_CIRCLE_SIZE) {
                                                                            animationTargetState += CIRCLE_ANIMATION_STEP
                                                                        }
                                                                        if (animationTargetState >= SELECTED_CIRCLE_SIZE) {
                                                                            isAnimationRunning = false
                                                                        }
                                                                    },
                                                                    innerI2 * CIRCLE_ANIMATION_TIME
                                                                )
                                                                innerI2++
                                                            }
                                                        }
                                                    }, i * TRANSITION_ANIMATION_TIME)
                                                }
                                            }
                                        }, (innerI1 * CIRCLE_ANIMATION_REVERSE_TIME ))
                                        innerI1++
                                    }
                                }
                            }
                        }
                    },
                )
            }
            .pointerInput(true) {
                detectHorizontalDragGestures(
                    onDragStart = {
//                        STEP = DRAG_STEP
                        Log.i("alabama", "onDragStart")
                        innerRadius -= canvasHeight * 0.2f
                        heightCenter -= canvasHeight * 0.2f
                        val newAngle = -atan2(
                            circleCenter.x - it.x,
                            circleCenter.y - it.y
                        ) * (180f / PI.toFloat())
                        startedAngle = newAngle
                        animationTargetState = INITIAL_CIRCLE_SIZE
//                        var innerI = 0
//                        while (innerI <= (SELECTED_CIRCLE_SIZE - INITIAL_CIRCLE_SIZE) / CIRCLE_ANIMATION_STEP) {
////                            isAnimationRunning = true
//                            handler.postDelayed({
//                                if (animationTargetState > INITIAL_CIRCLE_SIZE) {
//                                    var animationTargetStatev = animationTargetState
//                                    animationTargetState -= CIRCLE_ANIMATION_STEP
//                                }
//                                if (animationTargetState <= INITIAL_CIRCLE_SIZE) {
////                                    isAnimationRunning = false
//                                }
//                            }, (innerI.toLong() * CIRCLE_ANIMATION_REVERSE_TIME))
//                            innerI++
//                        }
                    },
                    onDragCancel = {
                        Log.i("alabama", "onDragCancel")
                    },
                    onHorizontalDrag = { change, _ ->
                        Log.i("alabama", "onDrag")
                        val touchAngle = -atan2(
                            circleCenter.x - change.position.x,
                            circleCenter.y - change.position.y
                        ) * (180f / PI.toFloat())
                        if (abs(startedAngle).roundToInt() != abs(touchAngle).roundToInt()) {
                            isDrag = true
                            val newAngle = oldAngle + (touchAngle - startedAngle)

                            angle = newAngle
                                .coerceIn(
                                    minimumValue = (initialChosenItem * STEP) - (STEP + ((items.size - 1) * STEP)).toFloat(),
                                    maximumValue = (initialChosenItem * STEP) - (0 - STEP).toFloat()
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
                            minimumValue = (initialChosenItem * STEP) - ((items.size - 1) * STEP).toFloat(),
                            maximumValue = (initialChosenItem * STEP).toFloat()
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
                                    var innerI = 0
                                    while (innerI <= (SELECTED_CIRCLE_SIZE - animationTargetState) / CIRCLE_ANIMATION_STEP) {
                                        handler.postDelayed({
                                            if (animationTargetState < SELECTED_CIRCLE_SIZE) {
                                                animationTargetState += CIRCLE_ANIMATION_STEP
                                            }
                                            else if (animationTargetState >= SELECTED_CIRCLE_SIZE){
                                                onItemSelected(items[currentItem])
                                            }
                                                isDrag = false
                                        }, innerI * CIRCLE_ANIMATION_TIME)
                                        innerI++
                                    }
                                    heightCenter = canvasWidth * 0.75f
                                    innerRadius = canvasWidth * 0.65f
                                }
                            }, i * TRANSITION_ANIMATION_TIME)
                        }
                    }
                )
            }
    ) {
        circleCenter = Offset(
            canvasWidth / 2,
            heightCenter
        )
        itemRadius = (canvasWidth / ITEM_RATIO)

        for (i in 0..((items.size - 1) * STEP)) {
            val angleInRad =
                (i - (initialChosenItem * STEP) + angle + MIDDLE_POINT) * ((PI / 180f).toFloat())
            drawContext.canvas.nativeCanvas.apply {

                if (i % STEP == 0) {
                    Log.i("alabama", "i % STEP: ${i % STEP}")
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

                    carouselViewState =
                        if ((radiansToDegrees(radians = angleInRad).roundToInt() < MIDDLE_POINT + STEP / 2) &&
                            (radiansToDegrees(radians = angleInRad).roundToInt() > MIDDLE_POINT - STEP / 2)
                        ) {
                            CarouselViewState.RangedMiddlePoint
                        } else {
                            CarouselViewState.OtherPoint
                        }
                    itemText = items[i / STEP].selectedTextBottom

                    when (val state = carouselViewState) {
                        is CarouselViewState.RangedMiddlePoint -> {
                            canvasMiddleItemPaint = Paint().apply {
                                color = items[i / STEP].color
                                maskFilter =
                                    BlurMaskFilter(itemRadius * 0.2f, BlurMaskFilter.Blur.SOLID)
                            }
                            if (!isDrag) {
                                expandStep = 10
                                itemTextPositionY = y + style.textSize.toPx() / 2 + 10.dp.toPx()
                                itemTextStyle = Paint().apply {
                                    color = style.chosenTextColor
                                    textSize = itemRadius * animationTargetState * 0.4f
                                    typeface = Typeface.DEFAULT_BOLD
                                    textAlign = Paint.Align.CENTER
                                }
                            } else {
                                expandStep = 0
                                currentItem = (i / STEP)
                                itemTextPositionY = y + itemRadius * 2f
                                itemTextStyle = Paint().apply {
                                    color = style.textColor
                                    textSize = itemRadius * 0.5f
                                    textAlign = Paint.Align.CENTER
                                }
                            }
                            expand =
                                (STEP - expandStep) - abs(radiansToDegrees(radians = angleInRad) - MIDDLE_POINT)
                            y -= expand
                            canvasItemRadius = (itemRadius + expand)
                            if (!isDrag) {
                                canvasItemRadius *= animationTargetState * 1.15f
                            }

                        }
                        else -> {
                            if (radiansToDegrees(radians = angleInRad).roundToInt() == MIDDLE_POINT + STEP && !isDrag) {
                                x += STEP
                            } else if (radiansToDegrees(radians = angleInRad).roundToInt() == MIDDLE_POINT - STEP && !isDrag) {
                                x -= STEP
                            }
                            canvasItemRadius = itemRadius
                            itemTextPositionY = y + itemRadius * 2f
                            canvasMiddleItemPaint = Paint().apply {
                                color = items[i / STEP].color
                                maskFilter =
                                    BlurMaskFilter(itemRadius * 0.2f, BlurMaskFilter.Blur.SOLID)
                            }
                            itemTextStyle = Paint().apply {
                                color = style.textColor
                                textSize = itemRadius * 0.5f
                                textAlign = Paint.Align.CENTER
                            }
                        }
                    }


                    /* //ShadowCircle
                     drawCircle(
                         x + 5.dp.toPx(),
                         y + 5.dp.toPx(),
                         canvasItemRadius* 0.7f,
                         Paint().apply {
                             alpha= 5
                             color = items[i / STEP].color
                             maskFilter = BlurMaskFilter(
                                 canvasItemRadius * animationTargetState,
                                 BlurMaskFilter.Blur.OUTER
                             )
                         }
                     )*/


                    if (carouselViewState == CarouselViewState.RangedMiddlePoint && !isDrag) {

                        //BlurCircle
                        drawCircle(
                            x,
                            y,
                            canvasItemRadius * 1.2f,
                            Paint().apply {
                                color = items[i / STEP].color
                                alpha = if (!isDrag) 35 else 0
                                maskFilter = BlurMaskFilter(
                                    itemRadius * 0.8f * animationTargetState,
                                    BlurMaskFilter.Blur.SOLID
                                )
                            }
                        )
                    }

                    //MainCircle
                    drawCircle(
                        x,
                        y,
                        canvasItemRadius,
                        canvasMiddleItemPaint
                    )

                    //TopText
                    if (carouselViewState == CarouselViewState.RangedMiddlePoint && !isDrag) {
                        drawText(
                            items[i / STEP].selectedTextTop,
                            x,
                            y - itemRadius / 2,
                            Paint().apply {
                                color = style.chosenTextColor
                                textSize = itemRadius * animationTargetState * 0.4f
                                textAlign = Paint.Align.CENTER
                            }
                        )
                    }

                    //BottomText
                    drawText(
                        itemText,
                        x,
                        itemTextPositionY,
                        itemTextStyle
                    )
                    if (carouselViewState == CarouselViewState.OtherPoint || (carouselViewState == CarouselViewState.RangedMiddlePoint && isDrag)) {
                        getBitmapFromVectorDrawable(context, items[i / STEP].icon)?.let {
                            drawBitmap(
                                it,
                                x - it.width / 2,
                                y - it.height / 2,
                                Paint()
                            )
                        }
                    }

                    items[i / STEP].x = x
                    items[i / STEP].y = y
                }
            }
        }
    }
}

fun setAlpha(isVisible: Boolean): Int {
    return if (isVisible) 255 else 0
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




