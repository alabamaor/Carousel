package com.plcoding.weightpickercompose

import android.app.Activity
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
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

var selectedScreen: MutableState<CarouselItem?> = mutableStateOf(items[items.size / 2])
var movementInside: MutableState<Int> = mutableStateOf(0)
var movementOutside: MutableState<Int> = mutableStateOf(0)
var isDrag: MutableState<Boolean> = mutableStateOf(false)

class MainActivity : ComponentActivity() {


    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    @ExperimentalTime
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val configuration = LocalConfiguration.current

            var chosenCarouselValue = remember {
                mutableStateOf(items[items.size / 2])
            }

            val dm = resources.displayMetrics

            val screenHeight = configuration.screenHeightDp.dp
            val screenWidth = configuration.screenWidthDp.dp


//            var rnd by remember { mutableStateOf(Random.nextInt(0, items.size))}
//            Handler(Looper.myLooper()!!).postDelayed({ rnd = Random.nextInt(0, items.size) }, 1000L)
// Display 10 items
            var initialChosenItem = remember { mutableStateOf(items.size / 2) }
            var pagerState = rememberPagerState(
                initialPage = initialChosenItem.value,
            )

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
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight(fraction = 0.1f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
//                    HorizontalPager()
                    Button(
                        modifier = Modifier.pointerInteropFilter {
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    isDrag.value = true
                                    movementOutside.value += 1
                                    true
                                }
                                else -> {
                                    isDrag.value = false
                                    movementOutside.value = 0
                                    true
                                }
                            }
                        },
                        onClick = {},
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = Color.Red
                        )
                    ) {
                        Text("MOVE RIGHT")
                    }

                    Button(
                        modifier = Modifier.pointerInteropFilter {
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    isDrag.value = true
                                    movementOutside.value -= 1
                                    true
                                }
                                else -> {
                                    isDrag.value = false
                                    movementOutside.value = 0
                                    true
                                }
                            }
                        },
                        onClick = {},
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = Color.Red
                        )
                    ) {
                        Text("MOVE LEFT")
                    }
                }

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight(fraction = 0.75f)
                        .align(Alignment.Center)
                ) {

                    Pager(
                        items = items,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        itemFraction = .75f,
                        overshootFraction = .75f,
                        initialIndex = initialChosenItem.value,
                        itemSpacing = 16.dp,
                        onChangeInside = {
                            movementInside.value = it
//                        Log.i("alabama", "angle: $it")
                        },
                        onChangeOutside = movementOutside.value,
                        contentFactory = { item ->
                           /* Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = Color(red = item.color.red, green = item.color.green, blue = item.color.blue)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = item.unSelectedText,
                                    modifier = Modifier.padding(all = 16.dp),
                                    style = MaterialTheme.typography.h6,
                                )
                            }*/
                            Card(shape = RoundedCornerShape(30.dp),
                                border = BorderStroke(width = 2.dp, color = Color.Blue),
                                modifier = Modifier
                                    .fillMaxSize(fraction = 0.95f)
//                                    .padding(10.dp)
                                    .coloredShadow(
                                    color = Color(0x371d4773),
                                    alpha = 0.15f
                                ),
                            ) {
                                Text(
                                    text = item.unSelectedText,
                                    color = Color(
                                        red = item.color.red,
                                        green = item.color.green,
                                        blue = item.color.blue
                                    ),
                                    modifier = Modifier.padding(all = 16.dp),
                                    style = MaterialTheme.typography.h6,
                                )
                            }


//                            Card(
//                                modifier = Modifier.fillMaxSize().noRippleClickable(
//                                    onClick = {},
//                                    enabled = false
//                                ).coloredShadow(
//                                    color = Color(0x371d4773),
//                                    alpha = 0.25f
//                                ).toggleable(
//                                    value = true,
//                                    onValueChange = {
//                                    }
//                                )
//                                ,
//                                shape = RoundedCornerShape(32.dp)
//                            ) {
//                                Text(
//                                    text = item.unSelectedText,
//                                    color = Color(
//                                        red = item.color.red,
//                                        green = item.color.green,
//                                        blue = item.color.blue
//                                    ),
//                                    modifier = Modifier.padding(all = 16.dp),
//                                    style = MaterialTheme.typography.h6,
//                                )
//                            }
                        }
                    )


                   /* HorizontalPager(state = pagerState,
                        count = items.count(),
                        modifier = Modifier
                            .pointerInput(true) {
                                detectDragGestures(
                                    onDragStart = {

                                        Log.i("alabama", "onDragStart")
                                    },
                                    onDrag = { change, dragAmount ->
                                        Log.i("alabama", "onDrag")
                                    }
                                )
                            }
                            .pointerInteropFilter(
                                onTouchEvent = { motionEvent ->
                                    when (motionEvent.action) {
                                        MotionEvent.ACTION_UP -> {
                                            Log.i("alabama", "ACTION_UP")
                                        }
                                        MotionEvent.ACTION_DOWN -> {
                                            Log.i("alabama", "ACTION_DOWN")
                                        }
                                        MotionEvent.ACTION_OUTSIDE -> {
                                            Log.i("alabama", "ACTION_UP")
                                        }
                                        MotionEvent.ACTION_MOVE -> {
                                            Log.i("alabama", "ACTION_DOWN")
                                        }
                                        MotionEvent.ACTION_BUTTON_PRESS -> {
                                            Log.i("alabama", "ACTION_BUTTON_PRESS")
                                        }
                                        MotionEvent.ACTION_BUTTON_RELEASE -> {
                                            Log.i("alabama", "ACTION_BUTTON_RELEASE")
                                        }
                                        MotionEvent.ACTION_HOVER_ENTER -> {
                                            Log.i("alabama", "ACTION_HOVER_ENTER")
                                        }
                                        MotionEvent.ACTION_HOVER_MOVE -> {
                                            Log.i("alabama", "ACTION_HOVER_MOVE")
                                        }
                                        MotionEvent.ACTION_POINTER_DOWN -> {
                                            Log.i("alabama", "ACTION_POINTER_DOWN")
                                        }
                                        MotionEvent.ACTION_POINTER_UP -> {
                                            Log.i("alabama", "ACTION_POINTER_UP")
                                        }
                                    }

                                    true
                                }
                            ),
                    contentPadding = PaddingValues(all = 20.dp)) { page ->
                        // Our page content
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Screen(
                                cardHeight = (screenHeight.value * 0.63).dp,
                                cardWidth = (screenWidth.value * 0.85).dp,
                                screen = items[page],
//                            isSelected = selectedScreen == items[page],
                                onSelectedScreenChanged = { screen ->
                                    selectedScreen.value = items[findIndex(items, screen)]
                                },
                            )
                        }
                    }*/
//                    CardsRowLayout(
//                        canvasWidth = resources.displayMetrics.widthPixels.toFloat(),
//                        cardWidth = (screenWidth.value * 0.7).dp,
//                        cardHeight = (screenHeight.value * 0.63).dp,
//                        screens = items,
//                        selectedScreen = chosenCarouselValue.value,
//                        onSelectedScreenChanged = ::onSelectedCategoryChanged,
//                        onDrag = {
//                            isDrag.value = true
//                            movementOutside.value += it
//                        },
//                        onDragEnd = {
//                            isDrag.value = false
//                            movementOutside.value = 0
//                        },
//                    )
                }

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
                        chosenCarouselValue.value = it
                    },
                    onAngleChangeInside = {
                        movementInside.value = it
                        Log.i("alabama", "onAngleChangeInside: $it")
                    },
                    onAngleChangeOutside = movementOutside.value,
                    isDragOutside = isDrag.value,
                )
            }
        }
    }

    fun onItemSelectedPressed(carouselItem: CarouselItem) {
        Toast.makeText(
            applicationContext,
            "click - ${carouselItem.unSelectedText}",
            Toast.LENGTH_SHORT
        )
            .show()
    }

    fun onSelectedCategoryChanged(item: CarouselItem) {

        selectedScreen.value = items[findIndex(items, item)]
    }

    fun findIndex(arr: List<CarouselItem>, item: CarouselItem): Int {
        return arr.indexOf(item)
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