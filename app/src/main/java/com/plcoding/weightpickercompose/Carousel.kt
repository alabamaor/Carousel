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
const val CIRCLE_ANIMATION_STEP = 0.02f
const val REDUCE_CIRCLE_ANIMATION_STEP = 0.05f

const val MIDDLE_POINT = -90

const val INITIAL_HEIGHT_CENTER = 0.75f
const val INITIAL_INNER_RADIUS = 0.65f

const val REDUCE_INNER_RADIUS = INITIAL_HEIGHT_CENTER * 0.2f
const val REDUCE_HEIGHT_CENTER = INITIAL_INNER_RADIUS * 0.2f


@ExperimentalTime
@ExperimentalComposeUiApi
@Composable
fun Carousel(
    modifier: Modifier = Modifier,
    context: Context,
    canvasWidth: Float,
    canvasHeight: Float,
    style: CarouselStyle = CarouselStyle(),
    items: List<CarouselItem> = listOf(),
    initialChosenItem: Int = (items.size - 1) / 2,
    onItemSelected: (CarouselItem) -> Unit,
    onAngleChangeInside: (Int) -> Unit,
    isDragOutside: Boolean,
    onAngleChangeOutside: Int,
    onItemSelectedPressed: (CarouselItem) -> Unit
) {

//    var itemTextPositionX: Float
    var itemTextPositionY: Float
    var itemRadius = 14f
    var canvasItemRadius = itemRadius
    var canvasMiddleItemPaint: Paint
    var expand = 0f
    var itemText = ""
    var itemTextStyle: Paint


    var carouselViewState: CarouselViewState = CarouselViewState.OtherPoint

    var itemSelected: CarouselItem? = items[(items.size - 1) / 2]
    val handler = Handler(Looper.myLooper()!!)

    var heightCenter by remember { mutableStateOf(canvasWidth * INITIAL_HEIGHT_CENTER) }
    var innerRadius by remember { mutableStateOf(canvasWidth * INITIAL_INNER_RADIUS) }

    var currentItem by remember { mutableStateOf(initialChosenItem) }
    var circleCenter by remember { mutableStateOf(Offset.Zero) }
    var startedAngle by remember { mutableStateOf(0f) }
    var angle by remember { mutableStateOf(0f) }
    var isDrag by remember { mutableStateOf(isDragOutside) }
    var isCancelDragOutsideFlag by remember { mutableStateOf(isDragOutside) }
    var isStartDragOutsideFlag by remember { mutableStateOf(!isDragOutside) }
    var oldAngle by remember { mutableStateOf(angle) }
    var animationTargetState by remember { mutableStateOf(SELECTED_CIRCLE_SIZE) }
//    var selectedPoint by remember { mutableStateOf(MIDDLE_POINT) }

    var isAnimationActive by remember { mutableStateOf(false) }
    var animateMovementSteps = 0f
    var animateCircleSteps = 0
    var movementMaxSteps = 0
    var movementCountSteps = 0f
    var savedOldAngle = oldAngle
    var savedNewAngle = 0f


    val initial = initialChosenItem * STEP
    val maxStep = (items.size - 1) * STEP
    val minStep = 0


    val tapRunnable: Runnable = object : Runnable {
        override fun run() {
            var iterator = movementCountSteps
            if (movementCountSteps <= animateMovementSteps) {
                if (startedAngle > 0) {
                    iterator = movementCountSteps * -1
                }

                angle = (savedOldAngle + iterator).coerceIn(
                    minimumValue = initial - maxStep.toFloat(),
                    maximumValue = initial.toFloat()
                )
                onAngleChangeInside(angle.toInt())
                oldAngle = angle
            }
            if (movementCountSteps == animateMovementSteps - 1f) {
                isDrag = false
                heightCenter = canvasWidth * INITIAL_HEIGHT_CENTER
                innerRadius = canvasWidth * INITIAL_INNER_RADIUS
            }

            if (movementCountSteps > animateMovementSteps - 1) {
                if (animationTargetState < SELECTED_CIRCLE_SIZE) {
                    animationTargetState += CIRCLE_ANIMATION_STEP
                }
            }
            if (movementCountSteps <= movementMaxSteps) {
                movementCountSteps += 1
                isAnimationActive = true
                handler.post(this)
            } else {
                isAnimationActive = false
            }
        }
    }

    val startTapAnimation = {
        animationTargetState = INITIAL_CIRCLE_SIZE
        animateCircleSteps =
            ((SELECTED_CIRCLE_SIZE - animationTargetState) / CIRCLE_ANIMATION_STEP).toInt()

        animateMovementSteps = abs(
            calcClosestAngle(
                angleRoundToInt = startedAngle.roundToInt(),
                step = STEP
            )
        )
        savedOldAngle = oldAngle
        movementMaxSteps = (animateMovementSteps + animateCircleSteps).toInt()
        movementCountSteps = 0f
        isDrag = true
        handler.post(tapRunnable)
    }

    val reduceCircleOnTapRunnable: Runnable = object : Runnable {
        override fun run() {
            if (movementCountSteps <= movementMaxSteps) {
                if (animationTargetState > INITIAL_CIRCLE_SIZE) {
                    animationTargetState -= REDUCE_CIRCLE_ANIMATION_STEP
                }
                movementCountSteps += 1
                isAnimationActive = true
                handler.post(this)
            } else {
                startTapAnimation()
            }
        }
    }

    val reduceCircleOnDragRunnable: Runnable = object : Runnable {
        override fun run() {
            if (movementCountSteps <= movementMaxSteps) {
                if (animationTargetState > INITIAL_CIRCLE_SIZE) {
                    animationTargetState -= REDUCE_CIRCLE_ANIMATION_STEP
                }
                movementCountSteps += 1
                isAnimationActive = true
                handler.post(this)
            } else {
                isAnimationActive = false
            }
        }
    }

    val dragEndRunnable: Runnable = object : Runnable {
        override fun run() {
            var iterator = movementCountSteps
            if (movementCountSteps <= animateMovementSteps) {
                if (savedOldAngle > savedNewAngle) {
                    iterator = movementCountSteps * -1
                }
                angle = (savedOldAngle + iterator).coerceIn(
                    minimumValue = initial - (maxStep).toFloat() - STEP,
                    maximumValue = initial.toFloat() + STEP
                )
                onAngleChangeInside(angle.toInt())
                oldAngle = angle
            }
            if (movementCountSteps.toInt() == animateMovementSteps.roundToInt() - 1) {
                heightCenter = canvasWidth * INITIAL_HEIGHT_CENTER
                innerRadius = canvasWidth * INITIAL_INNER_RADIUS
                angle = (savedNewAngle).coerceIn(
                    minimumValue = initial - (maxStep).toFloat() - STEP,
                    maximumValue = initial.toFloat() + STEP
                )
                onAngleChangeInside(angle.roundToInt())
                oldAngle = angle

                isDrag = false
                currentItem = initialChosenItem - (angle / STEP).toInt()
                onItemSelected(items[currentItem])
            }

            if (movementCountSteps > animateMovementSteps - 1) {
                if (animationTargetState < SELECTED_CIRCLE_SIZE) {
                    animationTargetState += CIRCLE_ANIMATION_STEP
                }
            }
            if (movementCountSteps <= movementMaxSteps) {
                movementCountSteps += 1
                isAnimationActive = true
                handler.post(this)
            } else {
                isAnimationActive = false
            }
        }
    }
    val onDrag = { change: Float ->
        if (abs(startedAngle).roundToInt() != abs(change).roundToInt()) {
            isDrag = true
            angle = (oldAngle + (change - startedAngle))
                .coerceIn(
                    minimumValue = initial - (STEP + (maxStep)).toFloat(),
                    maximumValue = initial - (minStep - STEP).toFloat()
                )
            onAngleChangeInside(angle.toInt())
        }
    }
    val startDragEndAnimation = {
        savedOldAngle = angle
        savedNewAngle = calcClosestAngle(
            angleRoundToInt = angle.roundToInt(),
            step = STEP
        ).coerceIn(
            minimumValue = initial - (maxStep).toFloat(),
            maximumValue = initial.toFloat()
        )
//        selectedPoint += savedNewAngle.toInt()

        animationTargetState = INITIAL_CIRCLE_SIZE
        animateCircleSteps =
            ((SELECTED_CIRCLE_SIZE - animationTargetState) / CIRCLE_ANIMATION_STEP).toInt()

        val range =
            if (savedOldAngle.roundToInt() - savedNewAngle.roundToInt() == 0) 1f else (savedOldAngle - savedNewAngle)
        animateMovementSteps = abs(range)
        movementMaxSteps = (animateMovementSteps + animateCircleSteps).toInt()
        movementCountSteps = 0f
        isDrag = true
        handler.post(dragEndRunnable)
    }

    Canvas(
        modifier = modifier
            .pointerInput(true) {
                detectTapGestures(
                    onPress = {
                        Log.i("alabama", "onPress")
                        if (!isAnimationActive) {
                            val newAngle = -atan2(
                                circleCenter.x - it.x,
                                circleCenter.y - it.y
                            ) * (180f / PI.toFloat())
                            startedAngle = newAngle
                        }
                    },
                    onTap = {
                        Log.i("alabama", "onTap")
                        if (!isAnimationActive) {
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
                                } else {
                                    animateCircleSteps =
                                        ((SELECTED_CIRCLE_SIZE - INITIAL_CIRCLE_SIZE) / REDUCE_CIRCLE_ANIMATION_STEP).toInt()

                                    movementMaxSteps = (animateCircleSteps)
                                    movementCountSteps = 0f
                                    handler.post(reduceCircleOnTapRunnable)
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
                        if (!isAnimationActive) {
                            innerRadius -= canvasHeight * REDUCE_INNER_RADIUS
                            heightCenter -= canvasHeight * REDUCE_HEIGHT_CENTER
                            animationTargetState = SELECTED_CIRCLE_SIZE

                            val newAngle = -atan2(
                                circleCenter.x - it.x,
                                circleCenter.y - it.y
                            ) * (180f / PI.toFloat())
                            startedAngle = newAngle
                        }
                    },
                    onHorizontalDrag = { change, _ ->
                        if (!isAnimationActive) {
                            onDrag(
                                -atan2(
                                    circleCenter.x - change.position.x,
                                    circleCenter.y - change.position.y
                                ) * (180f / PI.toFloat())
                            )
//                            Log.i("alabama", "dragOffset: $change")
                        }
                    },
                    onDragEnd = {
                        Log.i("alabama", "onDragEnd")
                        if (!isAnimationActive) {
                            startDragEndAnimation()
                        }
                    }
                )
            }
    ) {
        if (isDragOutside) {
            if (!isAnimationActive) {
                isCancelDragOutsideFlag = true
                animationTargetState = INITIAL_CIRCLE_SIZE

                isDrag = true
                isStartDragOutsideFlag = false
                angle = (angle + onAngleChangeOutside)
                    .coerceIn(
                        minimumValue = initial - (STEP + (maxStep)).toFloat(),
                        maximumValue = initial - (0 - STEP).toFloat()
                    )
                onAngleChangeInside(angle.toInt())
            }
        } else {
            if (isCancelDragOutsideFlag) {
                startDragEndAnimation()
                isDrag = false
                isStartDragOutsideFlag = true
                isCancelDragOutsideFlag = false
            }
        }
        circleCenter = Offset(
            canvasWidth / 2,
            heightCenter
        )
        itemRadius = (canvasWidth / ITEM_RATIO)

        for (i in 0..(maxStep)) {
            val angleInRad =
                (i - initial + angle + MIDDLE_POINT) * ((PI / 180f).toFloat())
            drawContext.canvas.nativeCanvas.apply {

                if (i % STEP == 0) {
                    var x = calcPointX(
                        radius = innerRadius,
                        angleInRad = angleInRad,
                        circleCenterX = circleCenter.x
                    )
                    val y = calcPointY(
                        radius = innerRadius,
                        angleInRad = angleInRad,
                        circleCenterY = circleCenter.y
                    )

                    carouselViewState =
                        if ((radiansToDegrees(radians = angleInRad).toInt() < MIDDLE_POINT + STEP / 2) &&
                            (radiansToDegrees(radians = angleInRad).toInt() > MIDDLE_POINT - STEP / 2)
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
                            currentItem = (i / STEP)
                            if (isDrag) {
                                expand = if (isAnimationActive) 0f
                                else -(abs(radiansToDegrees(radians = angleInRad).roundToInt() - MIDDLE_POINT) * 1.5f) + itemRadius / 3.5f
                                itemTextPositionY = y + itemRadius * 2f
                                itemTextStyle = Paint().apply {
                                    color = style.textColor
                                    textSize = itemRadius * 0.5f
                                    textAlign = Paint.Align.CENTER
                                }
                            } else {
                                expand = 10f
                                itemTextPositionY = y + style.textSize.toPx() / 2 + 10.dp.toPx()
                                itemTextStyle = Paint().apply {
                                    color = style.chosenTextColor
                                    textSize = itemRadius * animationTargetState * 0.4f
                                    typeface = Typeface.DEFAULT_BOLD
                                    textAlign = Paint.Align.CENTER

                                }
                            }
                            canvasItemRadius = (itemRadius + expand)
                            if (!isDrag) {
                                canvasItemRadius *= animationTargetState * 1.15f
                            }
                        }
                        else -> {
                            if (i / STEP == currentItem + 1 && !isDrag) {
                                x += STEP * 1.2f
                            } else if (i / STEP == currentItem - 1 && !isDrag) {
                                x -= STEP * 1.2f
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

                    /*
                    //ShadowCircle
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

fun degreesToRadians(degrees: Float): Double {
    return degrees * PI / 180.0
}

fun radiansToDegrees(radians: Float): Float {
    return (radians * 180.0 / PI).toFloat()
}

fun getCurrentItemByClick(
    x: Float,
    y: Float,
    items: List<CarouselItem>,
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




