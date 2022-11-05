package com.mredrock.cyxbs.discover.grades.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * 用于占位状态栏，
 *
 * 如果在 BottomSheet 内的布局文件中用了 fitsSystemWindows，那么会导致 peekHeight 不好设置，
 * 所以只能用一个 View 去占位，然后在代码中动态设置 peekHeight
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/22 16:23
 */
class StatusBar(context: Context, attrs: AttributeSet) : View(context, attrs) {
  var mStatusBarHeight = 0
  init {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId != 0) {
      mStatusBarHeight = resources.getDimensionPixelSize(resourceId)
    }
  }
  
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    val newHeightMS = MeasureSpec.makeMeasureSpec(mStatusBarHeight, MeasureSpec.EXACTLY)
    super.onMeasure(widthMeasureSpec, newHeightMS)
  }
}