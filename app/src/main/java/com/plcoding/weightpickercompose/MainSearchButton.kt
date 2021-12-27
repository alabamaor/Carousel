package com.plcoding.weightpickercompose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainSearchButton(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,
        color = Color.Red,
        fontSize = 16.sp
    ),
    onClick: (Boolean) -> Unit = {},
    hasLiveMap: Boolean = false
) {

    Card(
        modifier = modifier
            .noRippleClickable(onClick = { onClick.invoke(!hasLiveMap) }),
        border = BorderStroke(2.dp, Color.White),
        shape = RoundedCornerShape(32.dp),
        elevation = 6.dp,
        backgroundColor = Color(0xffF5F7F8)
    ) {

        Row(
            modifier = Modifier.padding(all = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                modifier = Modifier.size(36.dp).padding(horizontal = 8.dp),
                painter = painterResource(id = R.drawable.ic_3),
                colorFilter = ColorFilter.tint(color = Color.Blue),
                contentDescription = "Find Nearby"
            )

            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = text,
                style = textStyle
            )

        }
    }

}

// PREVIEW
@Preview
@Composable
fun MockSearchButtonPreview() {
    Surface(Modifier.wrapContentHeight()) {
        MainSearchButton(
            text = "שירותי פנגו סביבך",
            hasLiveMap = true,
            onClick = {}
        )
    }
}