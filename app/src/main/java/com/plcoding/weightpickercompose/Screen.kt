package com.plcoding.weightpickercompose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Screen(
    cardWidth: Dp,
    cardHeight: Dp,
    screen: CarouselItem,
    isSelected: Boolean = false,
    onSelectedScreenChanged: (CarouselItem) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(
                width = cardWidth,
                height = cardHeight,
            ).noRippleClickable(
                onClick = {},
                enabled = false
            )
                .padding(horizontal = 24.dp)

                .coloredShadow(
                    color = Color(0x371d4773),
                    alpha = 0.25f
                )
                .toggleable(
                    value = isSelected,
                    onValueChange = {
                        onSelectedScreenChanged(screen)
                    }
                ),
            shape = RoundedCornerShape(32.dp)

        ) {
            Text(
                text = screen.unSelectedText,
                style = MaterialTheme.typography.body2,
                color = Color.Blue,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}










