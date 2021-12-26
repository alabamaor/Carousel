package com.plcoding.weightpickercompose

import android.app.Activity
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlin.math.abs
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

class MainActivity : ComponentActivity() {


    @ExperimentalPagerApi
    @ExperimentalFoundationApi
    @ExperimentalTime
    @ExperimentalComposeUiApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.iparked_map_bg),
                contentDescription = "",
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()
                ,
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Bottom
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.1f),
                    painter = painterResource(R.drawable.ic_icon_background),
                    contentDescription = "",
                    contentScale = ContentScale.FillBounds
                )
                CarouselView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(fraction = 0.9f),
                    items = items,
                    resources = resources,
                    onItemSelectedPressed = { item: CarouselItem ->
                        Toast.makeText(
                            applicationContext,
                            "click - ${item.unSelectedText}",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onItemSelected = { item ->
                        Toast.makeText(
                            applicationContext,
                            "Selected - ${item.unSelectedText}",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    applicationContext = applicationContext,
                    screenContent = { item: CarouselItem ->
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
                )
            }
        }
    }

    @Composable
    fun draw(item: CarouselItem, modifier: Modifier) {
        Text(
            text = item.unSelectedText,
            color = Color(
                red = item.color.red,
                green = item.color.green,
                blue = item.color.blue
            ),
            modifier = modifier,
        )

    }


    private fun getRangeStep(value: Int, index: Int, step: Int): Int {
        if (value <= 0) {
            return abs(value)
        }
        return abs(index * step) + value
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