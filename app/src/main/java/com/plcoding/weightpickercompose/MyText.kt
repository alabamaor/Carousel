package com.plcoding.weightpickercompose

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState


@ExperimentalPagerApi
@Composable
fun MyHorizontalPager(modifier: Modifier) {
  val pagerState = rememberPagerState(initialPage = 2)
  HorizontalPager(
    state = pagerState,
    modifier = Modifier.fillMaxWidth()
      .height(50.dp),
    count = pagerState.pageCount
  ) { page ->
    Image(
      painter = painterResource(
        when (page) {
          0 -> R.drawable.ic_3
          1 -> R.drawable.ic_1
          2 -> R.drawable.ic_4
          else -> throw IllegalStateException("image not provided for page $page")
        }
      ), contentDescription = null
    )
  }
}

@ExperimentalPagerApi
@Preview
@Composable fun MyHorizontalPagerPreview() {
  val pagerState = rememberPagerState(initialPage = 2)
  HorizontalPager(
    state = pagerState,
    modifier = Modifier.fillMaxWidth()
      .height(50.dp),
    count = 3
  ) { page ->
    Image(
      painter = painterResource(
        when (page) {
          0 -> R.drawable.ic_3
          1 -> R.drawable.ic_1
          2 -> R.drawable.ic_4
          else -> throw IllegalStateException("image not provided for page $page")
        }
      ), contentDescription = null
    )
  }
}


/*
@Composable
fun MyText(modifier: Modifier) {
  val LOREM_IPSUM_TEXT = "sbdkj sdiv hsdjvhls vsh vlhv lhv alhvali val vhali vals vla vaiv las"
  Text(
    text = LOREM_IPSUM_TEXT,
    modifier = modifier
      .wrapContentHeight()
      .border(BorderStroke(1.dp, Color.Red))
  )
}

@ExperimentalPagerApi
@Composable
fun MyPager(pagerItem: @Composable () -> Unit = {}) {
  Scaffold {
    Column(
      modifier = Modifier
        .fillMaxSize()
        // In case items in the VP are taller than the screen -> scrollable
        .verticalScroll(rememberScrollState())
    ) {
      HorizontalPager(
        contentPadding = PaddingValues(32.dp),
        itemSpacing = 16.dp,
        count = 3,
      ) {
        pagerItem()ExperimentalPagerApi
      }
    }
  }
}

@ExperimentalPagerApi
@Preview
@Composable
fun MyPager_200dpWidth() {
  MyPager { MyText(modifier = Modifier.widthIn(max = 200.dp)) }
}

@ExperimentalPagerApi
@Preview
@Composable
fun MyPager_500dpWidth() {
  MyPager { MyText(modifier = Modifier.widthIn(max = 500.dp)) }
}

@ExperimentalPagerApi
@Preview
@Composable
fun MyPager_FillMaxWidth() {
  MyPager { MyText(modifier = Modifier.fillMaxWidth()) }
}*/
