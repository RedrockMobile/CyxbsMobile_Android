package com.cyxbsmobile_single.module_todo.util

import com.aigestudio.wheelpicker.WheelPicker

/**
 * Author: RayleighZ
 * Time: 2021-10-27 10:25
 */

//辅助WheelPicker旋转指定位置的方法
fun WheelPicker.to(offSet: Int){
    //首先获取单条todo的高度(不仅仅是文字部分高度，还有文字之间间隙的高度)
    val singeH = height / visibleItemCount
    scrollTo(0, offSet * singeH)
    invalidate()
}