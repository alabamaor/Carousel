package com.plcoding.weightpickercompose


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Preview
@Composable
fun SetupAnimationLayout() {

    var state by remember { mutableStateOf(false) }

    var animSpec by remember { mutableStateOf("snap") }

    val startColor = Color.Blue

    val endColor = Color.Green

    val backgroundColor by animateColorAsState(
        if (state) endColor else startColor,
        getAnimationSpec(animSpec)
    )

    Column(
        modifier = Modifier.fillMaxSize().background(backgroundColor),
        verticalArrangement = Arrangement.Center
    ) {
        SetupAnimateButtons(onValueChanged = { currState, animName ->
            animSpec = animName
            state = currState
        }, state = state)
    }
}

@Composable
fun SetupAnimateButtons(onValueChanged: (Boolean, String) -> Unit, state: Boolean) {
    for (anim in animationSpec.values()) {
        Button(
            onClick = { onValueChanged(!state, anim.value) },
            modifier = Modifier.height(50.dp).width(100.dp).padding(top = 10.dp),
            content = {
                Text(text = anim.value, color = Color.White)
            })
    }
}

fun getAnimationSpec(spec: String): AnimationSpec<Color> {

    return when (spec) {
        animationSpec.SPRING.value -> {
            spring(
                dampingRatio = Spring.DampingRatioHighBouncy,
                stiffness = Spring.StiffnessMedium
            )
        }
        animationSpec.TWEEN.value -> {
            tween(
                durationMillis = 2000,
                delayMillis = 500,
                easing = LinearOutSlowInEasing
            )
        }
        animationSpec.REPEATABLE.value -> {
            repeatable(
                iterations = 2,
                animation = tween(durationMillis = 200),
                repeatMode = RepeatMode.Reverse
            )
        }
        animationSpec.INFINITE_REPEATABLE.value -> {
            infiniteRepeatable(
                animation = tween(durationMillis = 200),
                repeatMode = RepeatMode.Reverse
            )
        }

        else -> {
            snap(delayMillis = 50)
        }

    }
}