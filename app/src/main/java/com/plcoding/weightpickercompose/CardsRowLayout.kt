package com.plcoding.weightpickercompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun CardsRowLayout(
    screens: Array<CarouselItem>,
    cardWidth: Dp,
    cardHeight: Dp,
    selectedScreen: CarouselItem?,
    onSelectedScreenChanged: (CarouselItem) -> Unit,
) {
    val scrollState = rememberLazyListState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyRow(
            state = scrollState,
        ) {
            items(screens) { item ->
                Screen(
                    cardHeight = cardHeight,
                    cardWidth = cardWidth,
                    screen = item,
                    isSelected = selectedScreen == item,
                    onSelectedScreenChanged = { screen ->
                        onSelectedScreenChanged(screen)
                    },
                )
            }
        }
    }
}
