package lib.course.item.view

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
import com.ndhzs.netlayout.attrs.NetLayoutParams
import com.ndhzs.netlayout.view.NetLayout
import lib.course.data.LessonData

/**
 * ...
 *
 * @author 985892345 (Guo Xiangrui)
 * @email guo985892345@foxmail.com
 * @date 2022/8/26 19:13
 */
class SelfLessonView(
  context: Context
) : CardView(context) {
  
  private val mTvTitle: TextView
  private val mTvContent: TextView
  
  private val mAmTextColor = 0xFFFF8015.toInt()
  private val mPmTextColor = 0xFFFF6262.toInt()
  private val mNightTextColor = 0xFF4066EA.toInt()
  private val mAmBgColor = 0xFFF9E7D8.toInt()
  private val mPmBgColor = 0xFFF9E3E4.toInt()
  private val mNightBgColor = 0xFFDDE3F8.toInt()
  
  private var mIsShowOverlapTag = false
  
  fun isShowOverlapTag(boolean: Boolean) {
    mIsShowOverlapTag = boolean
    invalidate()
  }
  
  fun setData(data: LessonData) {
    setColor(data.timeType)
    setText(data.course, data.classroom)
  }
  
  private fun setColor(type: LessonData.Type) {
    when (type) {
      LessonData.Type.AM -> {
        mTvTitle.setTextColor(mAmTextColor)
        mTvContent.setTextColor(mAmTextColor)
        setCardBackgroundColor(mAmBgColor)
        mPaint.color = mAmTextColor
      }
      LessonData.Type.PM -> {
        mTvTitle.setTextColor(mPmTextColor)
        mTvContent.setTextColor(mPmTextColor)
        setCardBackgroundColor(mPmBgColor)
        mPaint.color = mPmTextColor
      }
      LessonData.Type.NIGHT -> {
        mTvTitle.setTextColor(mNightTextColor)
        mTvContent.setTextColor(mNightTextColor)
        setCardBackgroundColor(mNightBgColor)
        mPaint.color = mNightTextColor
      }
    }
  }
  
  private fun setText(course: String, classroom: String) {
    mTvTitle.text = course
    mTvContent.text = classroom
  }
  
  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    changeMaxLinesIfNeed()
  }
  
  private val mPaint = Paint().apply {
    style = Paint.Style.FILL
  }
  
  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
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
  
  /**
   * 该方法用于解决标题和内容重叠的问题
   */
  private fun changeMaxLinesIfNeed() {
    post {
      // 因为在 onLayout 中设置 maxLines 会失效，所以需要用 post 延时设置。具体原因可以去看 requestLayout() 源码
      val parent = parent
      if (parent is NetLayout) {
        val lp = layoutParams as NetLayoutParams
        if (lp.endRow == lp.startRow) {
          // 说明只有一行
          if (mTvTitle.bottom > mTvContent.top) {
            // 当标题的底部高于内容的顶部时，说明标题与内容显示区域重叠了
            if (mTvTitle.maxLines != 2 && mTvContent.maxLines != 1) {
              mTvTitle.maxLines = 2
              mTvContent.maxLines = 1
            }
          }
        } else {
          // 还原
          if (mTvTitle.maxLines != 3 && mTvContent.maxLines != 3) {
            mTvTitle.maxLines = 3
            mTvContent.maxLines = 3
          }
        }
      }
    }
  }
  
  
  init {
    cardElevation = 0F
    setCardBackgroundColor(Color.TRANSPARENT)
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
    addView(mTvTitle)
    addView(mTvContent)
  }
}