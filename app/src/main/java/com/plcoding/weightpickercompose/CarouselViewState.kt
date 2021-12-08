package com.plcoding.weightpickercompose

sealed class CarouselViewState {
    //    data class MiddlePoint(val isDrag: Boolean) : CarouselViewState()
    object RangedMiddlePoint : CarouselViewState()
    object OtherPoint : CarouselViewState()
}
