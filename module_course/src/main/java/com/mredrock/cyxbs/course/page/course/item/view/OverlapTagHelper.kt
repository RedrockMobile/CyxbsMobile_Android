package com.mredrock.cyxbs.course.page.course.item.view

import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import com.mredrock.cyxbs.lib.utils.extensions.dp2pxF

/**
 * 跟重叠标记相关的帮助类
 *
 * # 有以下需要你主动调用：
 * ## 1、是否显示重叠标记
 * 请在 View 中实现 IOverlapTag 接口，然后使用该 Helper 来辅助实现
 * ```
 * class MyItemView(context: Context) : ItemView(context), IOverlapTag {
 *
 *   private mHelper = OverlapTagHelper(this)
 *
 *   override fun setIsShowOverlapTag(isShow: Boolean) {
 *     mHelper.setIsShowOverlapTag(isShow)
 *   }
 * }
 * ```
 *
 * ## 2、更改重叠标记的颜色
 * ```
 * mHelper.setOverlapTagColor(color)
 * ```
 *
 * ## 3、绘制重叠标记，请在 onDraw() 中主动使用 !!!
 * ```
 * private mHelper = OverlapTagHelper(this)
 *
 * override fun onDraw(canvas: Canvas) {
 *   super.onDraw()
 *   mHelper.drawOverlapTag(canvas: Canvas)
 * }
 * ```
 *
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 15:34
 */
class OverlapTagHelper(val view: View) : IOverlapTag {
  
  protected var mIsShowOverlapTag = false
    private set
  
  override fun setIsShowOverlapTag(isShow: Boolean) {
    mIsShowOverlapTag = isShow
    view.invalidate()
  }
  
  fun setOverlapTagColor(color: Int) {
    mPaint.color = color
    view.invalidate()
  }
  
  private val mPaint = Paint().apply {
    style = Paint.Style.FILL
  }
  
  fun drawOverlapTag(canvas: Canvas) {
    if (mIsShowOverlapTag) {
      // 绘制右上角的重叠标志
      val l = view.width - 12.dp2pxF
      val r = l + 6.dp2pxF
      val t = 4.dp2pxF
      val b = t + 2.dp2pxF
      canvas.drawCircle(l, (t + b) / 2, 1.dp2pxF, mPaint)
      canvas.drawRect(l, t, r, b, mPaint)
      canvas.drawCircle(r, (t + b) / 2, 1.dp2pxF, mPaint)
    }
  }
}