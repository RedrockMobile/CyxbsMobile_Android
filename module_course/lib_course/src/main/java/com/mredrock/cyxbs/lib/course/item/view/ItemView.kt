package com.mredrock.cyxbs.lib.course.item.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.mredrock.cyxbs.lib.utils.extensions.color
import com.mredrock.cyxbs.lib.utils.extensions.dp2px
import com.mredrock.cyxbs.lib.utils.extensions.dp2pxF

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/9/12 15:11
 */
abstract class ItemView(context: Context) : CardView(context) {
  
  val tvTitle: TextView
  val tvContent: TextView
  
  open fun setText(title: String, content: String) {
    tvTitle.text = title
    tvContent.text = content
  }
  
  // 课表背景色
  protected val mFloorColor = com.mredrock.cyxbs.config.R.color.config_common_background_color.color
  
  override fun draw(canvas: Canvas) {
    canvas.drawColor(mFloorColor) // 需要在底层绘制背景色，不然黑夜模式下 item 颜色会透过去
    super.draw(canvas)
  }
  
  final override fun setBackgroundColor(color: Int) {
    setCardBackgroundColor(color)
  }
  
  init {
    cardElevation = 1F
    super.setCardBackgroundColor(Color.TRANSPARENT)
    radius = 8.dp2pxF
    
    val margin1 = 7.dp2px
    val margin2 = 10.dp2px
    tvTitle = TextView(context).apply {
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
    tvContent = TextView(context).apply {
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
    super.addView(tvTitle)
    super.addView(tvContent)
  }
}