package com.plcoding.weightpickercompose

import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp

@Composable
fun ScrollableTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    edgePadding: Dp = TabRowDefaults.ScrollableTabRowPadding,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = @Composable { tabPositions ->
        TabRowDefaults.Indicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
        )
    },
    divider: @Composable () -> Unit = @Composable {
        TabRowDefaults.Divider()
    },
    tabs: @Composable () -> Unit
) {

}

@Preview
@Composable
fun ScrollableTabRowPreview(){
    ScrollableTabRow(selectedTabIndex = 2,
    tabs = {})
}