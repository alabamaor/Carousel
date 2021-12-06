package com.plcoding.weightpickercompose

sealed class CarouselViewState {
    data class MiddlePoint(val isDrag: Boolean) : CarouselViewState()
    data class RangedMiddlePoint(val isDrag: Boolean) : CarouselViewState()
    data class OtherPoint(val isDrag: Boolean) : CarouselViewState()
}