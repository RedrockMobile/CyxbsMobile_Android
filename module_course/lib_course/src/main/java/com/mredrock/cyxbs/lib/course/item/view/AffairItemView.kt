package com.mredrock.cyxbs.lib.course.item.view

import android.content.Context
import android.graphics.*
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * 课表显示事务的 View
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 15:17
 */
open class AffairItemView(context: Context) : ItemView(context) {
  
  protected val mTextColor = com.mredrock.cyxbs.config.R.color.config_level_two_font_color.color
  protected val mStripeColor = R.color.course_affair_stripe.color
  
  init {
    super.setCardBackgroundColor(Color.TRANSPARENT)
    tvTitle.setTextColor(mTextColor)
    tvContent.setTextColor(mTextColor)
  }
  
  // 线条的宽度
  private val mLineWidth = 8.dp2px
  // 线条之间的间隔
  private val mLineSpace = mLineWidth * sqrt(2F)
  
  private val mPaint = Paint().apply {
    color = mStripeColor
    strokeWidth = mLineWidth.toFloat()
    style = Paint.Style.FILL
  }
  
  private val mPath = Path()
  
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    val w = width
    val h = height
    
    val end = max(w, h) + min(w, h) * 1.414213F
    var pos = 0F
    while (pos < end) {
      mPath.reset()
      mPath.moveTo(0F, pos)
      mPath.lineTo(0F, pos + mLineSpace)
      mPath.lineTo(pos + mLineSpace, 0F)
      mPath.lineTo(pos, 0F)
      mPath.close()
      canvas.drawPath(mPath, mPaint)
      pos += mLineSpace * 2
    }
  }
}