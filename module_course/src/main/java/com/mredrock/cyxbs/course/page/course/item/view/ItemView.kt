package com.mredrock.cyxbs.course.page.course.item.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.mredrock.cyxbs.lib.utils.extensions.dp2pxF

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 19:13
 */
abstract class ItemView(context: Context) : CardView(context) {
  
  protected val mTvTitle: TextView
  protected val mTvContent: TextView
  protected var mIsShowOverlapTag = false
    private set
  
  fun setIsShowOverlapTag(boolean: Boolean) {
    mIsShowOverlapTag = boolean
    invalidate()
  }
  
  fun setOverlapTagColor(color: Int) {
    mPaint.color = color
    invalidate()
  }
  
  fun setText(title: String, content: String) {
    mTvTitle.text = title
    mTvContent.text = content
  }
  
  private val mPaint = Paint().apply {
    style = Paint.Style.FILL
  }
  
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    drawOverlapTag(canvas)
  }
  
  protected fun drawOverlapTag(canvas: Canvas) {
    if (mIsShowOverlapTag) {
      // 绘制右上角的重叠标志
      val l = width - 12.dp2pxF
      val r = l + 6.dp2pxF
      val t = 4.dp2pxF
      val b = t + 2.dp2pxF
      canvas.drawCircle(l, (t + b) / 2, 1.dp2pxF, mPaint)
      canvas.drawRect(l, t, r, b, mPaint)
      canvas.drawCircle(r, (t + b) / 2, 1.dp2pxF, mPaint)
    }
  }
  
  init {
    cardElevation = 0F
    super.setCardBackgroundColor(Color.TRANSPARENT)
    radius = 8.dp2pxF
    
    val margin1 = 7.dp2px
    val margin2 = 10.dp2px
    mTvTitle = TextView(context).apply {
      layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
        topMargin = margin1
        leftMargin = margin2
        rightMargin = margin2
        gravity = Gravity.TOP
      }
      ellipsize = TextUtils.TruncateAt.END
      gravity = Gravity.CENTER
      maxLines = 3
      setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
    }
    mTvContent = TextView(context).apply {
      layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
        leftMargin = margin2
        rightMargin = margin2
        bottomMargin = margin1
        gravity = Gravity.BOTTOM
      }
      ellipsize = TextUtils.TruncateAt.END
      gravity = Gravity.CENTER
      maxLines = 3
      setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
    }
    super.addView(mTvTitle)
    super.addView(mTvContent)
  }
}