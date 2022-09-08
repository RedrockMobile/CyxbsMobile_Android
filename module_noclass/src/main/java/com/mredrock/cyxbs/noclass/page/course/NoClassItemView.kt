package com.mredrock.cyxbs.noclass.page.course

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.mredrock.cyxbs.lib.utils.extensions.dp2pxF

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.page.item
 * @ClassName:      NoClassItemView
 * @Author:         Yan
 * @CreateDate:     2022年09月06日 16:58:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:
 */
abstract class NoClassItemView (context: Context) : CardView(context) {
  
  protected val mTvNames: TextView
  private var mIsShowOverlapTag = false
  
  fun setIsShowOverlapTag(boolean: Boolean) {
    mIsShowOverlapTag = boolean
    invalidate()
  }
  
  fun setOverlapTagColor(color: Int) {
    mPaint.color = color
    invalidate()
  }
  
  fun setText(names: String,height : Int) {
    mTvNames.text = names
    mTvNames.maxLines = if (height == 1) 3 else 6
  }
  
  private val mPaint = Paint().apply {
    style = Paint.Style.FILL
  }
  
  // 课表背景色
  private val mFloorColor = com.mredrock.cyxbs.config.R.color.config_common_background_color.color
  
  override fun draw(canvas: Canvas) {
    canvas.drawColor(mFloorColor) // 需要在底层绘制背景色，不然黑夜模式下 item 颜色会透过去
    super.draw(canvas)
  }
  
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    drawOverlapTag(canvas)
  }
  
  private fun drawOverlapTag(canvas: Canvas) {
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
    val margin1 = 6.dp2px
    val margin2 = 5.dp2px
    mTvNames = TextView(context).apply {
      layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
        topMargin = margin1
        leftMargin = margin2
        rightMargin = margin2
        bottomMargin = margin1
        gravity = Gravity.CENTER
      }
      ellipsize = TextUtils.TruncateAt.END
      gravity = Gravity.CENTER
      setTextSize(TypedValue.COMPLEX_UNIT_DIP, 11F)
    }
    super.addView(mTvNames)
  }
}