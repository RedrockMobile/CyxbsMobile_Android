package com.mredrock.cyxbs.lib.course.item.view

import android.content.Context
import android.graphics.*
import com.mredrock.cyxbs.lib.course.R
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import kotlin.math.max
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
  
  private val mPaint = Paint().apply {
    color = mStripeColor
  }
  
  private val mRectF = RectF()
  private val mClipBounds = Rect()
  
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    canvas.getClipBounds(mClipBounds)
    val width = mClipBounds.width()
    val height = mClipBounds.height()
    val drawEdge = max(width, height) * sqrt(2F)
    val space = 8.dp2px
    val num = (drawEdge / (space * 2)).toInt()
    canvas.save()
    canvas.translate(width / 2F, height / 2F)
    canvas.rotate(45F)
    mRectF.set(
      -drawEdge / 2,
      drawEdge / 2,
      -drawEdge / 2 + space,
      -drawEdge / 2
    )
    for (i in 0 until num) {
      canvas.drawRect(mRectF, mPaint)
      mRectF.set(
        mRectF.left + (space * 2),
        mRectF.top,
        mRectF.right + (space * 2),
        mRectF.bottom
      )
    }
    canvas.restore()
  }
}